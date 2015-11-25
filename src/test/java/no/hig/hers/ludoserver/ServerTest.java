package no.hig.hers.ludoserver;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import no.hig.hers.ludoclient.Main;

public class ServerTest {
	private Socket connection;
		
	private ExecutorService executorService;
	private BufferedReader input;
	private BufferedWriter output;
	
	private final String text = "This is a test";
	
	private void sendText(String text) throws IOException {
		output.write(text);
		output.newLine();
		output.flush();
	}
	
	private String read() throws IOException {
		if (input.ready())
			return input.readLine();
		return null;
	}

	@Test
	public void test() throws IOException {
		//connection = new Socket("127.0.0.1", 12345);
		/*input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));*/
		
		/*executorService = Executors.newCachedThreadPool();
		sendText(text);
		read();
		executorService.shutdown();*/
	}
}
