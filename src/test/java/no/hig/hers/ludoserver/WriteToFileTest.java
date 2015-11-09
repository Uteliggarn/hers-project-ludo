package no.hig.hers.ludoserver;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class WriteToFileTest {

	@Test
	public void test() throws UnknownHostException, IOException {
		Player player = new Player(new Socket("127.0.0.1", 12347));
		final String fileName = "TestFile.txt";
		final String message = "Something";
		
		player.writeToFile(fileName, message);
		
		
	}

}
