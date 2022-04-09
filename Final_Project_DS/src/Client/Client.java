package Client;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import Graph.My_Graph;
import javafx.util.Pair;

public class Client extends Thread{

	final static Logger log_file = LogManager.getLogger(Client.class.getName());
	private static int client_number = -1;

	@SuppressWarnings("static-access")
	public Client(int number) {
		this.client_number = number;
	}

	public static void main(String[] args) {

		try {
			if(client_number == -1) {
				client_number = Integer.valueOf(args[0]);
			}
			PropertyConfigurator.configure("H:/Java_projects/Final_Project_DS/src/Client/log4j.properties");
			//H:/Java_projects/Final_Project_DS/src/Client/Logging_Client.log
			FileReader reader = new FileReader("H:/Java_projects/Final_Project_DS/system.properties");

			Properties p = new Properties();
			p.load(reader);
			String host = p.getProperty("GSP.server");
			Integer port = Integer.parseInt(p.getProperty("GSP.rmiregistry.port"));
			// Registry registry = LocateRegistry.getRegistry(host);
			String rmi = String.format("//%s:%s/My_Graph", host, port);
			My_Graph stub = (My_Graph) Naming.lookup(rmi);
			//System.out.println("Connected to server " + rmi + " Successfully.");
			log_file.info("Trying to Connect server " + rmi + ", Client" + client_number+".");
			while(stub.server_ready().equals("N")) {
				int sleep_time = (int) (Math.random() * ((10000 - 0) + 1)) + 0;
				//System.out.println("Server not Ready, Sleeping " + sleep_time + "ms.");
				log_file.info("Server not Ready, Sleeping " + sleep_time + "ms.");
				Thread.sleep(sleep_time);
			}
			log_file.info("Connected to server " + rmi + " Successfully, Client" + client_number + ".");
			stub.set_clientNumber(client_number);
			String cont = "Y";
			File outFile = new File(String.format("H:/Java_projects/Final_Project_DS/Results/log%s.txt", client_number));
			FileWriter fw = new FileWriter(outFile,true);
	    	BufferedWriter bw = new BufferedWriter(fw);
			int count = 0;
			while (cont.equals("Y")) {
				long batch_time = 0;
				@SuppressWarnings("resource")
				Scanner scan_inputs = new Scanner(System.in);
				log_file.info("Sending number of requests = "+10+", Client" + client_number+".");
				ArrayList<Pair<String, Pair<Integer, Integer>>> rg = random_generation(10, stub.get_numerOfNodes());
				long start_time = System.currentTimeMillis();
				log_file.info("Sending batch_" + count + " to server, Client" + client_number+".");
				ArrayList<Integer> result = stub.set_operation(rg);
				long response_time = System.currentTimeMillis() - start_time;
				batch_time += response_time;
				for(int i =0; i<result.size();i++) {
					log_file.info("Received Result from server = " + result.get(i) + ", Client" + client_number + ".");
					bw.write(String.valueOf(result.get(i)));
					bw.write("\n");
				}
				//System.out.println("Time taken: " + response_time+"ms.");
				log_file.info("Time taken: " + response_time +"ms, Client" + client_number + ".");
				int sleep_time = (int) (Math.random() * ((10000 - 0) + 1)) + 0;
				//System.out.println("Sleeping " + sleep_time + "ms.");
				log_file.info("Sleeping " + sleep_time + "ms, Client" + client_number + ".");
				batch_time += sleep_time;
				Thread.sleep(sleep_time);
				log_file.info("Received Response from server at time = " + (response_time + start_time) + "ms, Client" + client_number + ".");
				log_file.info("Time taken for batch_" + count + " + sleeping time = " + batch_time+"ms, Client" + client_number + ".");
				//System.out.println("Time taken for file " + filename +" = " + batch_time+"ms.");
				System.out.print("Another batch? ");
				//cont = scan_inputs.nextLine();
				cont = "N";
				count++;
			}
			//out.close();
			bw.close();
			// System.out.println("Finished Successfully.");
			log_file.info("Finished Successfully, Client" + client_number + ".");
		} catch (Exception e) {
			// System.err.println("Client exception: " + e.toString());
			log_file.error("Client exception: " + e.toString()+ ", Client" + client_number + ".");
			e.printStackTrace();
		}
	}
	
	private static ArrayList<Pair<String, Pair<Integer, Integer>>> random_generation(int number_requests, int max_nodeNumber){
		ArrayList<Pair<String, Pair<Integer, Integer>>> rg = new ArrayList<Pair<String,Pair<Integer,Integer>>>();
		int percentage  = (int) ((0.5) * number_requests);
		double per = Double.valueOf(percentage) / Double.valueOf(number_requests);
		log_file.info("Percentage of Writing = " + (per*100) + "%, Client" + client_number + ".");
		String[] ops = {"Q", "A", "D"};
		int count = 0;
		Integer[] nodes_arr = new Integer[max_nodeNumber];
		for(int j = 0; j<max_nodeNumber; j++) {
			nodes_arr[j] = j+1;
		}
		for(int i = 0; i<number_requests; i++) {
			String selected_op = "";
			if(count == percentage) {
				selected_op = "Q";
			}else {
				Random r = new Random(); 
				int randomNumber = r.nextInt(ops.length);
				selected_op = ops[randomNumber];
			}
			if(selected_op.equals("A") || selected_op.equals("D")) {
				count++;
			}
			int randomNode1 = new Random().nextInt(nodes_arr.length);
			int node1 = nodes_arr[randomNode1];
			int randomNode2 = new Random().nextInt(nodes_arr.length);
			int node2 = nodes_arr[randomNode2];
			log_file.info("Randomly generate operation: " + selected_op + " from node " + node1 + " to node " + node2 + ", Client" + client_number + ".");
			Pair<Integer, Integer> nodes = new Pair<Integer, Integer>(node1, node2);
			Pair<String, Pair<Integer, Integer>> pair = new Pair<String, Pair<Integer,Integer>>(selected_op, nodes);
			rg.add(pair);
		}
		Pair<Integer, Integer> nodes = new Pair<Integer, Integer>(-1, -1);
		Pair<String, Pair<Integer, Integer>> pair = new Pair<String, Pair<Integer,Integer>>("F", nodes);
		rg.add(pair);
		return rg;
	}
}
