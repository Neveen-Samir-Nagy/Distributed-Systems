package Graph;


import java.rmi.Remote;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javafx.util.Pair;

public interface My_Graph extends Remote {

	String server_ready() throws RemoteException;
	
	ArrayList<Integer> set_operation(ArrayList<Pair<String, Pair<Integer, Integer>>> ops) throws RemoteException;
			
	int get_numerOfNodes() throws RemoteException;
	
	void set_clientNumber(int cn) throws RemoteException;
	
}
