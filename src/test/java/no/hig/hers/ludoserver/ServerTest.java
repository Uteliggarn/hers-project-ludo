package no.hig.hers.ludoserver;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.junit.Test;

import no.hig.hers.ludoclient.Main;

public class ServerTest {
	public BufferedWriter output;
	public BufferedReader input;
	
	public void sendLogin(String code, String username, String password) {
		try {
	        output.write(code + username);
	        output.newLine();
	        output.flush();
	        output.write(code + password);
	        output.newLine();
	        output.flush(); 
	    } catch (IOException ioe) {
	    	fail("Couldn't write to output");		
		}
	}

	@Test
	public void test() {
        GlobalServer application = new GlobalServer();

        sendLogin("SENDLOGIN:", "bne", "passord");
	}

}
