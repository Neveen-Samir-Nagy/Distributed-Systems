package Server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javafx.util.Pair;

public interface Server_Graph {

	//void set_numberOfNodes(Integer number) throws RemoteException;

	ArrayList<Pair<Integer, Integer>> read_file(String filename) throws RemoteException;

	void build_initialGraph(ArrayList<Pair<Integer, Integer>> edges) throws RemoteException;
	
	HashMap<Integer, HashSet<Integer>> get_graph();
	
	int get_numNodes();
	
	String isReady();
	
	void set_ready(String r);
		
	void shortest_path_allNodes();
	
	int shortest_path(Integer node1, Integer node2); 
	
	public void delete_edge(Integer node1, Integer node2);
	
	void insert_edge(Integer node1, Integer node2);
	
	HashMap<Pair<Integer,Integer>, Integer> get_distance();
	
}
