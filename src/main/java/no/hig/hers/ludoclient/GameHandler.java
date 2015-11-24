package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public class GameHandler {
	
	private int serverPort;
	
	private Socket connection;
	private BufferedWriter output;
	private BufferedReader input;
	private ExecutorService executorService;
	
	private GameClientUIController gameClientUIController;

	private CreateGameLobbyController createGameLobbyController;
	private HostGameLobbyController hostGameLobbyController;
	private PlayerGameLobbyController playerGameLobbyController;
	
	private String hostName;
	private int caseNr = 0;
	
	private final String JOIN = "JOIN:";
	
	public GameHandler(int serverPort, int caseNr, String hostName) {
		
		this.serverPort = serverPort;
		this.hostName = hostName;
		this.caseNr = caseNr;
		
		connect();
		createNewLobby();
		
		if (caseNr == 1)
			addPlayersToList();
		
	}
	
	
	private void addPlayersToList() {
		Thread t = new Thread(() -> {
			while (true) {
				for (int i=0; i<Main.playerList.size(); i++) {
					if (!Main.playerList.get(i).equals(Main.userName))
						createGameLobbyController.addNewPlayerToList(Main.playerList.get(i));
				}
				//The thread goes to sleep to save the CPU energy
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					// Prints the stackTrace if anything goes wrong.
					e.printStackTrace();
				}
			}
		});
		t.start();
	}	
	
	public void connect() {
		try {			
			connection = new Socket("127.0.0.1", serverPort);
			
			output = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
			
			sendText(Main.userName);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * Method used to send a message to the server. Handled in a separate method
     * to ensure that all messages are ended with a newline character and are
     * flushed (ensure they are sent.)
     * 
     * @param textToSend
     *            the message to send to the server
     */
    public void sendText(String textToSend) {
        try {
            output.write(textToSend);
            output.newLine();
            output.flush();
        } catch (IOException ioe) {
        	Main.showAlert("Error", "Unable to send message to server");
        }
    }
	
    public void startProcessConnection() {
    	executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
    	processConnection(); // Starter en ny evighets tråd som tar seg av meldinger fra server
    	executorService.shutdown();	// Dreper tråden når klassen dør
    }
    
	public void processConnection() {
		executorService.execute(() -> {
			while (true) {
				try {
					
	                String msg = input.readLine();
	                
	                System.out.println("\nHva er msg handler: " + msg);
	                
	                if(msg != null && msg.startsWith("gamestart:")) {
	                	Platform.runLater(new Runnable() {
	                		@Override
            				public void run() {
	                			int n = Integer.parseInt(msg.substring(10, 11));
	                							
	                			Tab tab = Main.gameTabs.getTabs().get(1);
	                			FXMLLoader loader = new FXMLLoader();
	                			try {
	                				System.out.print("Starter spill for " + n);
	                				tab.setContent(loader.load(getClass().getResource("GameClient.fxml").openStream()));
	                				gameClientUIController = loader.getController();
	                				gameClientUIController.setConnetion(output, input);
	                				gameClientUIController.setPlayer(n);
	                				
	                			} catch (IOException e1) {
		            			// TODO Auto-generated catch block
		            			e1.printStackTrace();
	                			}	
	                		}
	                	});
	                }
	                else if(msg != null && msg.startsWith("gamename:")) {
	                	Platform.runLater(new Runnable() {
	                		@Override
            				public void run() {
	                			int n = Integer.parseInt(msg.substring(9, 10));
	                			System.out.println("playernamenr " + n);
	                			String tmpNavn = msg.substring(10, msg.length());
	                			gameClientUIController.setPlayerName(n, tmpNavn);
	                		}
	                	});
	                }
	                else if(msg != null && msg.startsWith("dicevalue:")) {
	                	Platform.runLater(new Runnable() {
	                		@Override
            				public void run() {
	                			int diceVal = Integer.parseInt(msg.substring(10,11));
	                			int player = Integer.parseInt(msg.substring(11,12));
			                	int pawn = Integer.parseInt(msg.substring(12,13));
			                	System.out.println("diceval: " + diceVal + " player " + player + " pawn " + pawn);
			                	gameClientUIController.getDiceValue(diceVal, player, pawn);
	                		}
	                	});
	                }
	                else if(msg != null && msg.startsWith("gameover")) {
	                	Platform.runLater(new Runnable() {
	                		@Override
            				public void run() {
	                		gameClientUIController.gameover();	
	                		}
	                	});	
	                }
	                else if (msg != null && msg.startsWith(JOIN)) {
	                	switch (caseNr) {
	                	case 1: Platform.runLater(new Runnable() {
	                				@Override
	                				public void run() {
	                					if (!msg.substring(5).equals(hostName.substring(4)))
	                						createGameLobbyController.joinedPlayer(msg.substring(5));
	                				}
	                			});
	                		break;
	                	case 2: Platform.runLater(new Runnable() {
            						@Override
            						public void run() {
            							if (!msg.substring(5).equals(hostName.substring(4)))
            								hostGameLobbyController.joinedPlayer(msg.substring(5));
            						}
            					});
	                		break;
	                	case 3: Platform.runLater(new Runnable() {
            						@Override
            						public void run() {
            							if (!msg.substring(5).equals(hostName.substring(4)))
            								playerGameLobbyController.joinedPlayer(msg.substring(5));
            						}
            					});
	                		break;
	                	}
	                }
                    	        	                
	                
	            } catch (IOException ioe) {
	                ioe.printStackTrace();
	            }
				//The thread goes to sleep to save the CPU energy
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					// Prints the stackTrace if anything goes wrong.
					e.printStackTrace();
				}
	            
			}
		});
	}
	

	public void createNewLobby() {
		switch (caseNr) {
		case 1: Platform.runLater(new Runnable() {	//Create
    				@Override
    				public void run() {
						Tab tab = new Tab("Ludo");
						tab.setId(hostName);
					
						FXMLLoader loader = new FXMLLoader();
						try {
							tab.setContent(loader.load(getClass().getResource("CreateGameLobby.fxml").openStream()));
							createGameLobbyController = (CreateGameLobbyController) loader.getController();
							
							createGameLobbyController.setHostPlayer(hostName);
							
							Main.gameTabs.getTabs().add(tab);
							Main.gameTabs.getSelectionModel().select(tab);
							
							startProcessConnection();				
							
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
    				}
				});
			break;
		case 2: Platform.runLater(new Runnable() {	// Host
					@Override
					public void run() {
						Tab tab = new Tab("Ludo");
						tab.setId(hostName);
						
						FXMLLoader loader = new FXMLLoader();
						try {
							tab.setContent(loader.load(getClass().getResource("HostGameLobby.fxml").openStream()));
							hostGameLobbyController = (HostGameLobbyController) loader.getController();
							
							hostGameLobbyController.setHostPlayer(hostName);
							
							hostGameLobbyController.setConnetion(output, input);
							Main.gameTabs.getTabs().add(tab);
							Main.gameTabs.getSelectionModel().select(tab);
							
							startProcessConnection();
							
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
		});
			break;
		case 3: Platform.runLater(new Runnable() {	//Player
					@Override
					public void run() {
						Tab tab = new Tab("Ludo");
						tab.setId(hostName);
						
						FXMLLoader loader = new FXMLLoader();
						try {
							tab.setContent(loader.load(getClass().getResource("PlayerGameLobby.fxml").openStream()));
							playerGameLobbyController = (PlayerGameLobbyController) loader.getController();
							
							playerGameLobbyController.setHostPlayer(hostName);
							
							Main.gameTabs.getTabs().add(tab);
							Main.gameTabs.getSelectionModel().select(tab);
							
							startProcessConnection();
							
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
		});
			break;
		}
	}

	public String read() throws IOException {
		if (input.ready())
			return input.readLine();
		return null;
	}
	
	public void close() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
	}
	
}
