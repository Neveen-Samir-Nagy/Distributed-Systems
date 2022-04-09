import Client.Client;
import Server.Server;

public class Start extends Thread {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Runnable r1 = new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Server s = new Server();
				s.main(args);
			}
		};

		Runnable r2 = new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				Client c1 = new Client(2);
				c1.main(args);
			}
		};

		Runnable r3 = new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				Client c2 = new Client(3);
				c2.main(args);
			}
		};

		Thread t1 = new Thread(r1);
		t1.start();

		Thread t2 = new Thread(r2);
		t2.start();

		//Thread t3 = new Thread(r3);
		//t3.start();
	}

}
