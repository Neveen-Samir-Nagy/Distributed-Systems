package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
//import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import javafx.util.Pair;

public class serverGraph implements Server_Graph{

	private static HashMap<Integer, HashSet<Integer>> my_graph = new HashMap<Integer, HashSet<Integer>>();
	private static Scanner scan;
	private Integer number_ofNodes = 0;
	private String ready = "N";
	private static HashMap<Pair<Integer, Integer>, Integer> distance = new HashMap<Pair<Integer, Integer>, Integer>();
	private static Logger log_file = LogManager.getLogger(serverGraph.class.getName());
	
	public serverGraph() {
		PropertyConfigurator.configure("H:/Java_projects/Final_Project_DS/src/Server/log4j.properties");
	}
	
	@Override
	public ArrayList<Pair<Integer, Integer>> read_file(String filename) {
		// TODO Auto-generated method stub
		ArrayList<Pair<Integer, Integer>> graph = new ArrayList<Pair<Integer, Integer>>();
		try {
			File file = new File(filename);
			scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String[] line = scan.nextLine().split(" ");
				if (line[0].equals("S")) {
					break;
				}
				Pair<Integer, Integer> current_line = new Pair<Integer, Integer>(Integer.parseInt(line[0]),
						Integer.parseInt(line[1]));
				graph.add(current_line);
				if(number_ofNodes < Integer.parseInt(line[0])) {
					number_ofNodes = Integer.parseInt(line[0]);
				}
				if(number_ofNodes < Integer.parseInt(line[1])) {
					number_ofNodes = Integer.parseInt(line[1]);
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return graph;
	}

	@Override
	public void build_initialGraph(ArrayList<Pair<Integer, Integer>> edges) {
		// TODO Auto-generated method stub
		for (int i = 0; i < edges.size(); i++) {
			Pair<Integer, Integer> nodes = edges.get(i);
			//System.out.println("Add Edge " + i + " to initial graph: " + "from node " + nodes.getKey() + " to node " + nodes.getValue());
			log_file.info("Add Edge " + i + " to initial graph: " + "from node " + nodes.getKey() + " to node " + nodes.getValue());
			HashSet<Integer> node_set = my_graph.get(nodes.getKey());
			if (node_set == null) {
	 			node_set = new HashSet<Integer>();
				node_set.add(nodes.getValue());
				my_graph.remove(nodes.getKey());
				my_graph.put(nodes.getKey(), node_set);
				continue;
			}
			node_set.add(nodes.getValue());
			my_graph.remove(nodes.getKey());
			my_graph.put(nodes.getKey(), node_set);
		}
		for (int i = 0; i < number_ofNodes; i++) {
			if (my_graph.get(i + 1) == null) {
				my_graph.put(i + 1, new HashSet<Integer>());
			}
		}
		shortest_path_allNodes();
		log_file.info("Building initial graph Successfully.");
	}

	@Override
	public HashMap<Integer, HashSet<Integer>> get_graph() {
		// TODO Auto-generated method stub
		return my_graph;
	}

	@Override
	public int get_numNodes() {
		// TODO Auto-generated method stub
		return number_ofNodes;
	}

	@Override
	public String isReady() {
		// TODO Auto-generated method stub
		return ready;
	}

	@Override
	public void set_ready(String r) {
		// TODO Auto-generated method stub
		this.ready = r;
	}
	
	@Override
	public void shortest_path_allNodes() {
		for (int i = 0; i < number_ofNodes; i++) {
			for (int j = 0; j < number_ofNodes; j++) {
				int short_distance = shortest_path(i + 1, j + 1);
				Pair<Integer, Integer> pair = new Pair<Integer, Integer>(i + 1, j + 1);
				if (distance.get(pair) != null) {
					distance.remove(pair);
				}
				distance.put(pair, short_distance);
			}
		}
	}

	@Override
	public int shortest_path(Integer node1, Integer node2) {
		// TODO Auto-generated method stub
		int distance = -1;
		if (node1.equals(node2)) {
			return 0;
		}
		if (my_graph.get(node1).size() == 0) {
			return -1;
		}
		PriorityQueue<Pair<Integer, Integer>> PQ = new PriorityQueue<Pair<Integer, Integer>>(
				Comparator.comparing(Pair::getKey));
		for (int i = 0; i < number_ofNodes; i++) {
			if (node1.equals(i + 1)) {
				Pair<Integer, Integer> pair = new Pair<Integer, Integer>(0, node1);
				PQ.add(pair);
			} else {
				Pair<Integer, Integer> pair = new Pair<Integer, Integer>(Integer.MAX_VALUE, i + 1);
				PQ.add(pair);
			}
		}
		// Dijkstra algorithm
		while (!PQ.isEmpty()) {
			Pair<Integer, Integer> min_pair = PQ.poll();
			if (min_pair.getValue().equals(node2) && !min_pair.getKey().equals(Integer.MAX_VALUE)
					&& min_pair.getKey() >= 0) {
				return min_pair.getKey();
			}
			HashSet<Integer> node_set = my_graph.get(min_pair.getValue());
			if (node_set.size() == 0) {
				continue;
			}
			for (Integer node : node_set) {
				Integer dis = min_pair.getKey() + 1;
				Pair<Integer, Integer> neighbour = get_neighbourPair(PQ, node);
				if (neighbour != null && dis < neighbour.getKey()) {
					PQ.remove(neighbour);
					Pair<Integer, Integer> new_pair = new Pair<Integer, Integer>(dis, node);
					PQ.add(new_pair);
				}
			}
		}
		return distance;
	}

	@Override
	public void delete_edge(Integer node1, Integer node2) {
		// TODO Auto-generated method stub
		if (node1 == node2) {
			return;
		}
		if (my_graph.get(node1) != null) {
			HashSet<Integer> node_set = my_graph.get(node1);
			if (node_set.contains(node2)) {
				node_set.remove(node2);
				my_graph.remove(node1);
				my_graph.put(node1, node_set);
				log_file.info("Successfully Delete edge between node " + node1 + " and " + node2 + ".");
				// System.out.println("Successfully Delete edge between node " + node1 + " and "
				// + node2 + ".");
			}
		}
	}

	@Override
	public void insert_edge(Integer node1, Integer node2) {
		// TODO Auto-generated method stub
		if (node1 == node2) {
			return;
		}
		if (my_graph.get(node2) == null) {
			my_graph.put(node2, new HashSet<Integer>());
			number_ofNodes++;
		}
		HashSet<Integer> node_set = my_graph.get(node1);
		node_set.add(node2);
		my_graph.remove(node1);
		my_graph.put(node1, node_set);
		log_file.info("Successfully Add edge between node " + node1 + " and " + node2 + ".");
		// System.out.println("Successfully Add edge between node " + node1 + " and " +
		// node2 + ".");
	}

	private Pair<Integer, Integer> get_neighbourPair(PriorityQueue<Pair<Integer, Integer>> PQ, Integer node) {
		java.util.Iterator<Pair<Integer, Integer>> iterator = PQ.iterator();
		while (iterator.hasNext()) {
			Pair<Integer, Integer> pair = iterator.next();
			if (pair.getValue().equals(node)) {
				return pair;
			}
		}
		return null;
	}

	@Override
	public HashMap<Pair<Integer, Integer>, Integer> get_distance() {
		// TODO Auto-generated method stub
		return distance;
	}
}
