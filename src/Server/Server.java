package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Scanner;

public class Server {
	private static final int LOCK_SERVER_PORT = 9191;
	private static final int USER_SERVER_PORT = 9292;

	// HashMap to maintain all live locks
	public static HashMap<String, Lock> locks = new HashMap<>();

	public static void main(String[] args) {
		try {
			// servers to handle lock and user requests
			ThreadedServer lockServer = new ThreadedServer(new ServerSocket(LOCK_SERVER_PORT), ThreadedServer.ServerType.LOCK_SERVER),
							userServer = new ThreadedServer(new ServerSocket(USER_SERVER_PORT), ThreadedServer.ServerType.USER_SERVER);

			// starting the servers
			lockServer.start();
			userServer.start();

			// waiting
			Scanner sc = new Scanner(System.in);
			(new Thread(() -> {
				int stop;
				do {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					stop = sc.nextLine().charAt(0) - '0';
				} while (stop != 0);

				// stopping both servers
				lockServer.stopServer();
				userServer.stopServer();

			})).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}