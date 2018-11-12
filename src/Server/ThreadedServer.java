package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class ThreadedServer extends Thread {
	// two types of servers
	public enum ServerType {
		LOCK_SERVER, USER_SERVER
	}

	// holds the server socket
	private ServerSocket socket;

	// tells whether to stop the server or not
	private boolean stop;

	// holds the current connection type
	private ServerType type;

	ThreadedServer(ServerSocket socket, ServerType type) {
		this.socket = socket;
		this.stop = false;
		this.type = type;
	}

	// method to stop the server
	public void stopServer() {
		this.stop = true;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			if (this.type == ServerType.LOCK_SERVER)
				while (!this.stop)
					(new LockThread(socket.accept())).start();
			else
				while (!this.stop)
					(new UserThread(socket.accept())).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}