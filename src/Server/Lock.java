package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Lock {
	private PrintWriter outputStream;
	private BufferedReader inputStream;

	public Lock(PrintWriter outputStream, BufferedReader inputStream) {
		this.outputStream = outputStream;
		this.inputStream = inputStream;
	}

	public String processCommand(int command) {
		try {
			this.outputStream.println(command);
			return this.inputStream.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "FAIL";
		}
	}
}