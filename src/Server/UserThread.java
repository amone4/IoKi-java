package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserThread extends Thread {
	private Socket socket;

	UserThread(Socket socket) {
		this.socket = socket;
	}

	private static String decryptBlowfish(String input) {
		ProcessBuilder pb = new ProcessBuilder("php", "decrypt.php", input);
		Process p;
		try {
			p = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		try {
			return out.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// method to check if user can access lock
	private boolean canAccess(String user, String lock) {
		Database db = new Database();
		if (db.open()) {
			boolean canAccess = db.canAccess(decryptBlowfish(user), lock);
			db.close();
			return canAccess;
		} else return false;
	}

	@Override
	public void run() {
		try {
			System.out.println("New user connected from " + socket.getInetAddress() + ":" + socket.getPort());

			// getting user's ID, lock's ID and command
			String string = (new BufferedReader(new InputStreamReader(socket.getInputStream()))).readLine();
			int index1 = string.indexOf(":");
			String userId = string.substring(0, index1);
			int index2 = string.indexOf(":", index1+1);
			String lockId = string.substring(index1+1, index2);
			int command = string.charAt(index2+1) - '0';

			// validating command
			if (command == 0 || command == 1 || command == 2) {

				// checking if user can access lock
				if (canAccess(userId, lockId)) {

					// checking if the lock is live
					if (Server.locks.containsKey(lockId)) {

						// processing command
						string = Server.locks.get(lockId).processCommand(command);

					} else string = "FAIL";
				} else string = "FAIL";
			} else string = "FAIL";

			// sending back the response
			(new PrintWriter(socket.getOutputStream(), true)).println(string);

		} catch (IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		// closing socket connection
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}