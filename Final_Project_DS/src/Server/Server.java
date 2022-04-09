package Server;


import java.rmi.registry.Registry;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
//import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import Graph.Graph_Implementation;
import Graph.My_Graph;

import java.rmi.registry.LocateRegistry;
import java.io.FileReader;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;
//import java.util.Scanner;
import javafx.util.Pair;

public class Server extends Thread{

	final static Logger log_file = LogManager.getLogger(Server.class.getName());
	
	public Server() {
	}

	public static void main(String args[]) {

		try {
			PropertyConfigurator.configure("H:/Java_projects/Final_Project_DS/src/Server/log4j.properties");
			//BasicConfigurator.configure();
			FileReader reader = new FileReader("H:/Java_projects/Final_Project_DS/system.properties");

			Properties p = new Properties();
			p.load(reader);
			Integer rmi_port = Integer.parseInt(p.getProperty("GSP.rmiregistry.port"));
			int port = Integer.parseInt(p.getProperty("GSP.server.port"));
			Server_Graph sg = new serverGraph();
			Graph_Implementation obj = new Graph_Implementation(sg);
			log_file.info("Starting Server.");
			//System.out.println("Starting Server.");
			My_Graph stub = (My_Graph) UnicastRemoteObject.exportObject(obj, port);
			@SuppressWarnings("resource")
			//Scanner scan = new Scanner(System.in);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.createRegistry(rmi_port);
			registry.rebind("My_Graph", stub);
			log_file.info("Starting service on port " + port + " with name: My_Graph.");
			//System.out.println("Starting service on port " + port + " with name: My_Graph.");
			//System.out.print("Plaese enter number of nodes: ");
			//String input = scan.nextLine();
			//sg.set_numberOfNodes(Integer.parseInt(input));
			sg.set_ready("N");
			//System.out.println("Number of Nodes = "+ input+".");
			//System.out.print("Plaese enter initial graph filename: ");
			//String filename = scan.nextLine();
			String filename = "initial_graph";
			log_file.info("Read initial graph from file " + filename+".");
			//System.out.println("Read initial graph from file " + filename+".");
			ArrayList<Pair<Integer, Integer>> edges = sg.read_file(filename + ".txt");
			sg.build_initialGraph(edges);
			log_file.info("Number of Nodes in graph = "+ sg.get_numNodes()+".");
			log_file.info("R");
			//System.out.println("R");
			sg.set_ready("R");
		} catch (Exception e) {
			System.out.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
