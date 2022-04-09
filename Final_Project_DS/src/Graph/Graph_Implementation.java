package Graph;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Server.Server_Graph;
import Server.serverGraph;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javafx.util.Pair;

public class Graph_Implementation implements My_Graph {

	private static HashMap<Integer, HashSet<Integer>> my_graph = new HashMap<Integer, HashSet<Integer>>();
	private int number_ofNodes = 0;
	private static String ready = "N";
	private static HashMap<Pair<Integer, Integer>, Integer> distance = new HashMap<Pair<Integer, Integer>, Integer>();
	private int client_number = -1;
	@SuppressWarnings("unused")
	private Server_Graph sg = new serverGraph();
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock writeLock = readWriteLock.writeLock();
	private final Lock readLock = readWriteLock.readLock();
	private static boolean modified = false;
	private Logger log_file = LogManager.getLogger(Graph_Implementation.class.getName());;

	@SuppressWarnings("static-access")
	public Graph_Implementation(Server_Graph sg) throws RemoteException {
		this.sg = sg;
		PropertyConfigurator.configure("H:/Java_projects/Final_Project_DS/src/Server/log4j.properties");
		ready = sg.isReady();
	}

	@Override
	public ArrayList<Integer> set_operation(ArrayList<Pair<String, Pair<Integer, Integer>>> ops)
			throws RemoteException {
		// TODO Auto-generated method stub
		if(my_graph.size() == 0) {
			my_graph = sg.get_graph();
			number_ofNodes = sg.get_numNodes();
			distance = sg.get_distance();
			ready = sg.isReady();
		}
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < ops.size(); i++) {
			Pair<String, Pair<Integer, Integer>> op = ops.get(i);
			// System.out.println(
			// "Received Operation: " + op.getKey() + " : " + op.getValue().getKey() + " " +
			// op.getValue().getValue());
			log_file.info("Received Operation from client" + client_number + ": " + op.getKey() + " : "
					+ op.getValue().getKey() + " " + op.getValue().getValue());
			String received_opration = op.getKey();
			int d = -1;
			if(received_opration.equals("F")) {
				break;
			}else if (received_opration.equals("Q")) {
				if (modified) {
					writeLock.lock();
					if (modified) {
						sg.shortest_path_allNodes();
						distance = new HashMap<Pair<Integer,Integer>, Integer>();
						distance = sg.get_distance();
						my_graph = new HashMap<Integer, HashSet<Integer>>();
						my_graph = sg.get_graph();
						number_ofNodes = sg.get_numNodes();
						modified = false;
					}
					writeLock.unlock();
				}
				readLock.lock();
				Pair<Integer, Integer> p = new Pair<Integer, Integer>(op.getValue().getKey(), op.getValue().getValue());
				if (distance.get(p) == null) {
					d = -1;
				} else {
					d = distance.get(p);
				}
				log_file.info("Result of computing shortest path between node " + op.getValue().getKey() + " and node "
						+ op.getValue().getValue() + " = " + d + ".");
				// System.out.println("Result of computing shortest path between node " +
				// op.getValue().getKey() + " and node " + op.getValue().getValue() + " = " + d
				// + ".");
				result.add(d);
				readLock.unlock();
			} else if (received_opration.equals("A")) {
				writeLock.lock();
				sg.insert_edge(op.getValue().getKey(), op.getValue().getValue());
				modified = true;
				writeLock.unlock();
			} else if (received_opration.equals("D")) {
				writeLock.lock();
				sg.delete_edge(op.getValue().getKey(), op.getValue().getValue());
				modified = true;
				writeLock.unlock();
			} else {
				log_file.error("Received invalid operation.");
				// System.err.println("Received invalid operation.");
			}
		}

		return result;
	}

	

	@Override
	public String server_ready() throws RemoteException {
		// TODO Auto-generated method stub
		ready = sg.isReady();
		return ready;
	}

	@Override
	public int get_numerOfNodes() throws RemoteException {
		// TODO Auto-generated method stub
		number_ofNodes = sg.get_numNodes();
		return number_ofNodes;
	}

	@Override
	public void set_clientNumber(int cn) throws RemoteException {
		// TODO Auto-generated method stub
		this.client_number = cn;
	}


}
