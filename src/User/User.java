 package User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class User {
	private static final String HOST = "localhost";
	private static final int PORT = 9292;
	private static final String userId = "p3R5gPSUFUI=";
	private static final String lockId = "1";

	private static String processCommand(int command) {
		String response = "FAIL";
		Socket socket = null;

		try {
			// establishing connection
			InetAddress serverAddress = java.net.InetAddress.getByName(HOST);
			socket = new Socket(serverAddress, PORT);

			// sending command
			(new PrintWriter(socket.getOutputStream(), true)).println(userId + ":" + lockId + ":" + command);

			// checking response
			response = (new BufferedReader(new InputStreamReader(socket.getInputStream()))).readLine();
			if (command == 2)
				if (response.equals("L"))
					response = "Locked";
				else if (response.equals("U"))
					response = "Unlocked";
				else response = "FAIL";

		} catch (IOException e) {
			e.printStackTrace();
		}

		// closing socket connection
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// returning result
		return response;
	}

	private static void printInstructions() {
		System.out.println("0. Unlock");
		System.out.println("1. Lock");
		System.out.println("2. Status");
		System.out.println("3. Help");
		System.out.println("4. Exit");
	}

	public static void main(String[] args) {
		printInstructions();
		Scanner sc = new Scanner(System.in);
		int command;
		do {
			System.out.print("Enter choice: ");
			command = sc.nextInt();
			switch (command) {
				case 0:
				case 1:
				case 2:
					System.out.println(processCommand(command));
					break;
				case 3:
					printInstructions();
					break;
				case 4:
					break;
				default:
					System.out.println("Invalid choice");
			}
		} while (command != 4);
	}
}