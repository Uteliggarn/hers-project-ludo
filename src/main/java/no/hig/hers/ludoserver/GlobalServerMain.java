package no.hig.hers.ludoserver;

import javax.swing.JFrame;

public class GlobalServerMain {
	static GlobalServer application;
	  public static void main( String[] args )
	    {
	        application = new GlobalServer();
	        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    }
}
