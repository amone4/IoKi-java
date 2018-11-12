package Lock;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Lock {
	private static final String HOST = "localhost";
	private static final int PORT = 9191;
	// ID of the lock
	private static final String ID = "1";
	// secret of the lock
	private static final String SECRET = "e90kpl+IVXm6ZB7MVO0tSw==";
	// socket connection
	private static Socket socket;
	// connection output
	private static PrintWriter output;
	// connection input
	private static BufferedReader input;
	// allows commands to be processed
	private static boolean isCommandProcessingAllowed;

	private static String processCommand(int command) throws Exception {
		ProcessBuilder pb = new ProcessBuilder("python", "command.py", ((Integer) command).toString());
		Process p = pb.start();
		BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
		p.waitFor();
		String s = out.readLine();
		System.out.println(s);
		return s;
	}

	private static void processCommands() throws Exception {
		do {
			int command = input.readLine().charAt(0) - '0';

			if (command == 0 || command == 1 || command == 2)
				output.println(processCommand(command));

		} while (isCommandProcessingAllowed);
	}

	// method to generate random string
	private static String generateRandomString() {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(32);
		for (int i = 0; i < 32; i++)
			buffer.append((char) ('a' + (int) (random.nextFloat() * ('a' - 'z' + 1))));
		return buffer.toString();
	}

	private static boolean establishConnection() throws Exception {
		// establish connection with server
		InetAddress serverAddress = InetAddress.getByName(HOST);
		socket = new Socket(serverAddress, PORT);

		// getting input output streams
		output = new PrintWriter(socket.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// sending lock's ID
		output.println(ID);
		// checking if the connection was accepted
		String serverInput = input.readLine();
		if (serverInput.equals("PASS")) {

			// generating a random string
			String randomString = generateRandomString();
			// sending the random string
			output.println(randomString);
			// getting lock's encrypted string and comparing with the original
			if (randomString.equals(AES.decrypt(input.readLine(), SECRET))) {

				// confirming server about correct string
				output.println("PASS");

				// reading the random string
				randomString = input.readLine();
				// encrypting the string
				randomString = AES.encrypt(randomString, SECRET);
				// sending the encrypted string
				output.println(randomString);

				// getting server's response
				if (input.readLine().equals("PASS")) {

					System.out.println("Connection established");
					return true;

				} else System.out.println("Connection rejected");
			} else output.println("FAIL");
		} else System.out.println("Connection rejected");
		return false;
	}

	public static void main(String[] args) {
		(new Thread(() -> {
			while (true) {
				System.out.println("Connecting");
				isCommandProcessingAllowed = true;
				(new Thread(() -> {
					try {
						if (establishConnection()) {
							processCommands();
							socket.close();
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
						isCommandProcessingAllowed = false;
					}
				})).start();
				for (int i = 0; i < 50 && isCommandProcessingAllowed; i++) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
					}
				}
				isCommandProcessingAllowed = false;
				System.out.println("Sleeping");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
				System.out.println("Wakeup");
			}
		})).start();
	}
}