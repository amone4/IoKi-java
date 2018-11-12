package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class LockThread extends Thread {
	private Socket socket;

	LockThread(Socket socket) {
		this.socket = socket;
	}

	// method to get secret of a lock with id `id`
	private String getSecret(String id) {
		Database db = new Database();
		if (db.open()) {
			String secret = db.getSecret(id);
			db.close();
			if (secret != null && secret.length() > 0) return secret;
			else return "__FAIL__";
		} else return "__FAIL__";
	}

	// method to generate random string
	private String generateRandomString() {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(32);
		for (int i = 0; i < 32; i++)
			buffer.append((char) ('a' + (int) (random.nextFloat() * ('a' - 'z' + 1))));
		return buffer.toString();
	}

	@Override
	public void run() {
		try {
			// getting input output streams
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("New lock connected from " + socket.getInetAddress() + ":" + socket.getPort());

			// getting lock's ID
			String id = input.readLine();
			// getting lock's secret
			String secret = getSecret(id);
			// if ID was invalid
			if (!secret.equals("__FAIL__")) {

				// confirming lock about correct ID
				output.println("PASS");

				// reading the random string
				String randomString = input.readLine();
				// encrypting the string
				randomString = AES.encrypt(randomString, secret);
				// sending the encrypted string
				output.println(randomString);

				// getting lock's response
				if (input.readLine().equals("PASS")) {

					// generating a random string
					randomString = this.generateRandomString();
					// sending the random string
					output.println(randomString);
					// getting lock's encrypted string and comparing with the original
					if (randomString.equals(AES.decrypt(input.readLine(), secret))) {

						// confirming lock about connection
						output.println("PASS");
						// adding lock to the list
						Server.locks.put(id, new Lock(output, input));
						System.out.println("Connection established");
						return;

					} else output.println("FAIL");
				} else System.out.println("Connection rejected");
			} else output.println("FAIL");
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