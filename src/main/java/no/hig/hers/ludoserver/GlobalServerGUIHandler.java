package no.hig.hers.ludoserver;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Class for handling the GUI for the GlobalServer.
 * 
 * @author daniel on 26.11.2015
 */
public class GlobalServerGUIHandler extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea outputArea;
	
	/**
	 * Constructor, sets the Title and EXIT_ON_CLOSE
	 */
	public GlobalServerGUIHandler() {
		super("Global Server");
		
		setUpGUI();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Method for setting up the server GUI.
	 */
	private void setUpGUI() {
		outputArea = new JTextArea();
		outputArea.setFont(new Font("Ariel", Font.PLAIN, 14));
		outputArea.setEditable(false);
		add(new JScrollPane(outputArea), BorderLayout.CENTER);
		outputArea.setText("Server awaiting connections\n");
		
		setSize(600, 400);
		setVisible(true);
		
	}
	
	/**
	 * Method for displaying messages in the
	 * servers textArea.
	 * @param text The text to display
	 */
	void displayMessage(String text) {
		SwingUtilities.invokeLater(() -> outputArea.append(text));
	}
}
