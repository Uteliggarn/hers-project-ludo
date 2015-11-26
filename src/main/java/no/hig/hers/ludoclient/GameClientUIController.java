package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import no.hig.hers.ludoshared.Constants;

/**
 * GameClientUICOntroller creates the LudoBoardFX class which stores all the game logic.
 * It controls all the movement on the board through the LudoBoardFX class and communicate with the gameserver.
 * It also controls all the GUI of the game scene.
 *   
 * @author Hauken
 *
 */
public class GameClientUIController {
	
	LudoBoardFX board;
	
	private int turnOwner;
	private int player;
	private int pawnToMove = 0;
	private int diceRolls = 0;
	private boolean gameOver = false;
	private int gameStatus = 0;
	private String playerName1;
	private String playerName2;
	private String playerName3;
	private String playerName4;
	
	private boolean player1;
	private boolean player2;
	private boolean player3;
	private boolean player4;
	
	private int diceValue;
	
	private Image die1;
    private Image die2;
	private Image die3;
    private Image die4;
	private Image die5;
	private Image die6;
	
	private static BufferedWriter output;
	private static BufferedReader input;
	
	@FXML
	private BorderPane gameClientPane;
	@FXML
	private TextArea chatArea;
	@FXML
	private TextField typeArea;
	@FXML
	private Label redPlayer; 
	@FXML
	private Label bluePlayer;
	@FXML
	private Label yellowPlayer;
	@FXML
	private Label greenPlayer;
	@FXML
	private Button pawn1;
	@FXML
	private Button pawn2;
	@FXML
	private Button pawn3;
	@FXML
	private Button pawn4;
	@FXML
	private Button pass;
	@FXML
	private Button dieRoller;
	@FXML
	private ImageView dieLabel;
	@FXML
    private Label dieTextLabel;
	
	@FXML
	public void initialize() {
		
		try {	
			board = new LudoBoardFX();
			gameClientPane.setCenter(board);
		} catch (Exception e) {
			Main.LOGGER.log(Level.WARNING, "Error while trying to add gameboard", e);
		}
		setUpGUI();
		
		String tmp = (Constants.GAMECHAT + Main.userName);
		Main.cHandler.addNewChat(tmp);
	}
	
	public void setUpGUI() {
		
		turnOwner = 2;	//Red player starts
		pawnToMove = 0;
		diceRolls = 0;
		gameOver = false;
		gameStatus = 0;
		
		die1 = new Image("images/dice1.png");
		die2 = new Image("images/dice2.png");
		die3 = new Image("images/dice3.png");
		die4 = new Image("images/dice4.png");
		die5 = new Image("images/dice5.png");
		die6 = new Image("images/dice6.png");
	
		setTextOnLabels();
		pawn1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 0;
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText(Main.messages.getString("ROLLDICE"));
				setPawnMovesFalse();	
			}
		});

		pawn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 1;
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText(Main.messages.getString("ROLLDICE"));
				setPawnMovesFalse();
			}
		});
		
		pawn3.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 2;
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText(Main.messages.getString("ROLLDICE"));
				setPawnMovesFalse();
			}
		});
		
		pawn4.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 3;
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText(Main.messages.getString("ROLLDICE"));
				setPawnMovesFalse();
			}
		});
		
		pass.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				diceValue = 0;
				diceRolls = 0;
				setPawnMovesFalse();
				setNotValid();
				SendDiceValue(diceValue, turnOwner, pawnToMove);
			}
		});
		
		setNotValidPass();
		setPawnMovesFalse();
		dieRoller.setDisable(true);
	}
	
	
	@FXML
	private void rollDice(ActionEvent even) {
		rollDiceActionListener();
	}
	
	public void setTextOnLabels() {
		pawn1.setText(Main.messages.getString("PAWN1"));
		pawn2.setText(Main.messages.getString("PAWN2"));
		pawn3.setText(Main.messages.getString("PAWN3"));
		pawn4.setText(Main.messages.getString("PAWN4"));
		pass.setText(Main.messages.getString("PASS"));
	}
	
	private void rollDiceActionListener() {
		diceRolls++;
		if (diceRolls < 3) {
			Random rng = new Random();
			diceValue = rng.nextInt(6) + 1;
			switch (turnOwner) {
			case 1:	//Green
				for(int i=0; i < board.greenPawns.size(); i++) {
					if(board.greenPawns.get(i).validMove(diceValue) || diceValue == 6) {
						dieRoller.setDisable(true);
						dieRoller.setText(Main.messages.getString("MOVE"));
						for(int j=0; j < board.greenPawns.size(); j++) {
							if(board.greenPawns.get(j).isValid()) {
								setPawnMovesTrue(j);
							}
						}
						
					}
				}
				break;
			case 2:	//Red
				for(int i=0; i < board.redPawns.size(); i++) {
					if(board.redPawns.get(i).validMove(diceValue) || diceValue == 6) {
						dieRoller.setDisable(true);
						dieRoller.setText(Main.messages.getString("MOVE"));
						for(int j=0; j < board.redPawns.size(); j++) {
							if(board.redPawns.get(j).isValid()) {
								setPawnMovesTrue(j);
							}
						}
					}
				}
				break;
			case 3:	//Yellow
				for(int i=0; i < board.yellowPawns.size(); i++) {
					if(board.yellowPawns.get(i).validMove(diceValue) || diceValue == 6) {
						dieRoller.setDisable(true);
						dieRoller.setText(Main.messages.getString("MOVE"));
						for(int j=0; j < board.yellowPawns.size(); j++) {
							if(board.yellowPawns.get(j).isValid()) {
								setPawnMovesTrue(j);
							}
						}
					}
				}
				break;
			case 4:	//Blue
				for(int i=0; i < board.bluePawns.size(); i++) {
					if(board.bluePawns.get(i).validMove(diceValue) || diceValue == 6) {
						dieRoller.setDisable(true);
						dieRoller.setText(Main.messages.getString("MOVE"));
						for(int j=0; j < board.bluePawns.size(); j++) {
							if(board.bluePawns.get(j).isValid()) {
								setPawnMovesTrue(j);
							}
						}
					}
				}
			}
		setDiceImage(diceValue);
		}
		else {
			dieRoller.setText(Main.messages.getString("PASS"));
			dieRoller.setDisable(true);
		}
	}
	
	private void processRoll() {
		int inGoal;
		if ( turnOwner == 1 && diceValue !=0) {	//Green player
			board.greenPawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
			inGoal = board.greenPawnsInGoal.size();
			if (inGoal == 4 && player == 1) {
				gameStatus = 1;
				gameOver = true;
			}
			if(diceValue !=6) {
				CheckForPlayersGreen();
			}
			setNotValid();
			board.makePawns();
			}
		else if(turnOwner == 2 && diceValue !=0) { //Red player
			try {
				board.redPawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
				inGoal = board.redPawnsInGoal.size();
				if (inGoal == 4 && player == 2) {
					gameStatus = 1;
					gameOver = true;
				}
				pawnToMove = 0;
			} catch (Exception e) {
				Main.LOGGER.log(Level.WARNING, "Goalerror", e);
			}
			if(diceValue !=6) {
				CheckForPlayersRed();
			}
			setNotValid();
			board.makePawns();
		}
		else if (turnOwner == 3 && diceValue !=0) { //Yellow player
			board.yellowPawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
			inGoal = board.yellowPawnsInGoal.size();
			if (inGoal == 4 && player == 3) {
				gameStatus = 1;
				gameOver = true;
			}
			if(diceValue !=6) {
				CheckForPlayersYellow();
			}
			setNotValid();
			board.makePawns();
		}
		else if (turnOwner == 4 && diceValue !=0) { //Blue player
			board.bluePawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
			inGoal = board.bluePawnsInGoal.size();
			if (inGoal == 4 && player == 4) {
				gameStatus = 1;
				gameOver = true;
			}
			if(diceValue !=6) {
				CheckForPlayersBlue();
			}
			setNotValid();
			board.makePawns();
		}
		
		if(diceValue == 0) {
			passChangeTurnOwner();
		};
		
		diceValue = 0;
		diceRolls = 0;
		setPawnMovesFalse();
		
		if(player == turnOwner) {
			setValidPass();
			dieRoller.setDisable(false);
			dieRoller.setText(Main.messages.getString("ROLL"));
			dieTextLabel.setText(Main.messages.getString("YOURTURN"));
		} else {
			dieRoller.setDisable(true);
			dieRoller.setText(Main.messages.getString("WAIT"));
			dieTextLabel.setText(Main.messages.getString("WAITFORYOURTURN"));
			setNotValidPass();
		}
	}
	
	public void setPawnMovesFalse() {
		pawn1.setDisable(true);
		pawn2.setDisable(true);
		pawn3.setDisable(true);
		pawn4.setDisable(true);
	}
	public void setNotValidPass() {
		pass.setDisable(true);
	}
	public void setValidPass() {
		pass.setDisable(false);
	}
	public void setPawnMovesTrue(int val) {
		switch(val) {
		case 0:
			pawn1.setDisable(false);
			break;
		case 1:
			pawn2.setDisable(false);
			break;
		case 2:
			pawn3.setDisable(false);
			break;
		case 3:
			pawn4.setDisable(false);
			break;
		}
	}
	
	public void setNotValid() {
		switch (turnOwner) {
		case 1:	//Green
			for (int i = 0; i < board.greenPawns.size(); i++) {
				board.greenPawns.get(i).setNotValid();
			}
			break;
		case 2: //Red
			for (int i = 0; i < board.redPawns.size(); i++) {
				board.redPawns.get(i).setNotValid();
			}
			break;
		case 3: //Yellow
			for (int i = 0; i < board.yellowPawns.size(); i++) {
				board.yellowPawns.get(i).setNotValid();
			}
			break;
		case 4: //Blue
			for (int i = 0; i < board.bluePawns.size(); i++) {
				board.bluePawns.get(i).setNotValid();
			}
			break;
		}
	}
	
	public void setPlayer(int n) {
		player = n;
		if(player == turnOwner) {
			setValidPass();
			dieRoller.setDisable(false);
			dieRoller.setText(Main.messages.getString("ROLL"));
			dieTextLabel.setText(Main.messages.getString("YOURTURN"));
		} else dieTextLabel.setText(Main.messages.getString("WAITFORYOURTURN"));
		
	}
	public int getPlayer() {
		return player;
	}
	
	public int getTurnOwner() {
		return turnOwner;
	}
	
	public void setPlayerName(int pnr, String name) {
		switch (pnr) {
			case 1:
				player1 = true;
				playerName1 = name;
				greenPlayer.setText(Main.messages.getString("GREEN") + " " + playerName1);
				break;
			case 2:
				player2 = true;
				playerName2 = name;
				redPlayer.setText(Main.messages.getString("RED") + " " + playerName2 + " - " + Main.messages.getString("ROLL"));
				break;
			case 3: 
				player3 = true;
				playerName3 = name;
				yellowPlayer.setText(Main.messages.getString("YELLOW") + " " + playerName3);
				break;
			case 4:
				player4 = true;
				playerName4 = name;
				bluePlayer.setText(Main.messages.getString("BLUE") + " " + playerName4);
				break;
			default: break;
		}
	}
	public void setConnetion(BufferedWriter write, BufferedReader read) {
		output = write;
		input = read;
	}
	
	public void getDiceValue(int diceV, int playernr, int pawn) {
		turnOwner = playernr;
		pawnToMove = pawn;
		diceValue = diceV;
		setDiceImage(diceValue);
		processRoll();
		if(gameOver) {
			setNotValidPass();
			setPawnMovesFalse();
			dieRoller.setDisable(true);
			dieRoller.setText("GG");
			if(gameStatus == 1) {
				dieTextLabel.setText(Main.messages.getString("YOUWON"));
				sendGameStatus();
			}
			else {
				dieTextLabel.setText(Main.messages.getString("BETTERLUCK"));
				String tmp;
				tmp =(Constants.GAMELOST);
				Main.sendText(tmp);
			}			
		}
	}
	
	/**
	 * Send the given message to the client. Ensures that all messages
	 * have a trailing newline and are flushed.
	 * @param text the message to send
	 * @throws IOException if an error occurs when sending the message
	 */
	public void sendText(String text) throws IOException {
		output.write(text);
		output.newLine();
		output.flush();
	}
	
	public void SendDiceValue(int diceVal, int playernr, int pawn) {
		String tmp;
		tmp = (Constants.DICEVALUE + diceVal + playernr + pawn);
		try {
			sendText(tmp);
		} catch (IOException e) {
			Main.LOGGER.log(Level.WARNING, "Error sending message to server", e);
		}
	}
	
	public void sendGameStatus() {
		String tmp;
		tmp = (Constants.GAMEOVER);
		try {
			sendText(tmp);
		} catch (IOException e) {
			Main.LOGGER.log(Level.WARNING, "Error sending message to server", e);
		}
		tmp =(Constants.GAMEWON);
		Main.sendText(tmp);
	}
	public void passChangeTurnOwner() {
		
		switch(turnOwner) {
		case 1:	//Green
			CheckForPlayersGreen();
			break; 
		case 2:	//Red
			CheckForPlayersRed();
			break;
		case 3: //Yellow
			CheckForPlayersYellow();
			break;
		case 4: //Blue
			CheckForPlayersBlue();
			break;
		default: break;
		}
	}
	public void gameover() {
		gameOver = true;
	}
	public void CheckForPlayersGreen() {
		greenPlayer.setText(Main.messages.getString("GREEN") + " " + playerName1);
		if(player2) {
			turnOwner ++;
			redPlayer.setText(Main.messages.getString("RED") 
					+ " " + playerName2 + " - " + Main.messages.getString("ROLL"));
		} else if(player3) {
			turnOwner += 2;
			yellowPlayer.setText(Main.messages.getString("YELLOW")
					+ " " + playerName3 + " - " + Main.messages.getString("ROLL"));
		} else if(player4) {
			turnOwner += 3;
			bluePlayer.setText(Main.messages.getString("BLUE") 
					+ " " + playerName4 + " - " + Main.messages.getString("ROLL"));
		} else {
			greenPlayer.setText(Main.messages.getString("GREEN") + " " + playerName1 + " - " + Main.messages.getString("ROLL"));
			turnOwner = 1;
		}
	}
	public void CheckForPlayersRed() {
		redPlayer.setText(Main.messages.getString("RED") + " " + playerName2);
		if(player3) {
			turnOwner ++;
			yellowPlayer.setText(Main.messages.getString("YELLOW") 
					+ " " + playerName3 + " - " + Main.messages.getString("ROLL"));
		} else if(player4) {
			turnOwner += 2;
			bluePlayer.setText(Main.messages.getString("BLUE") 
					+ " " + playerName4 + " - " + Main.messages.getString("ROLL"));
		} else if(player1) {
			turnOwner = 1;
			greenPlayer.setText(Main.messages.getString("GREEN") 
					+ " " + playerName1 + " - " + Main.messages.getString("ROLL"));
		} else {
			redPlayer.setText(Main.messages.getString("RED") 
					+ " " + playerName2 + " - " + Main.messages.getString("ROLL"));
			turnOwner = 2;
		}
	}
	public void CheckForPlayersYellow() {
		yellowPlayer.setText(Main.messages.getString("YELLOW") + " " + playerName3);
		if(player4) {
			turnOwner ++;
			bluePlayer.setText(Main.messages.getString("BLUE") 
					+ " " + playerName4 + " - " + Main.messages.getString("ROLL"));
		} else if(player1) {
			turnOwner = 1;
			greenPlayer.setText(Main.messages.getString("GREEN") 
					+ " " + playerName1 + " - " + Main.messages.getString("ROLL"));
		} else if(player2) {
			turnOwner = 2;
			redPlayer.setText(Main.messages.getString("RED") 
					+ " " + playerName2 + " - " + Main.messages.getString("ROLL"));
		} else {
			yellowPlayer.setText(Main.messages.getString("YELLOW") 
					+ " " + playerName3 + " - " + Main.messages.getString("ROLL"));
			turnOwner = 3;
		}
	}
	public void CheckForPlayersBlue() {
		bluePlayer.setText(Main.messages.getString("BLUE") + " " + playerName4);
		if(player1) {
			turnOwner = 1;
			greenPlayer.setText(Main.messages.getString("GREEN") 
					+ " " + playerName1 + " - " + Main.messages.getString("ROLL"));
		} else if(player2) {
			turnOwner = 2;
			redPlayer.setText(Main.messages.getString("RED") 
					+ " " + playerName2 + " - " + Main.messages.getString("ROLL"));
		} else if(player3) {
			turnOwner = 3;
			yellowPlayer.setText(Main.messages.getString("YELLOW") 
					+ " " + playerName3 + " - " + Main.messages.getString("ROLL"));
		} else {
			turnOwner = 4;
			bluePlayer.setText(Main.messages.getString("BLUE") 
					+ " " + playerName4 + " - " + Main.messages.getString("ROLL"));
		}
	}
		
	public void setPlayerDisconnect(int dced) {
		switch(dced) {
			case 1: 
				player1 = false;
				for(int i=0; i < board.greenPawns.size(); i++) {
					board.greenPawns.get(i).KnockedOut();
				}
				break;
			case 2:
				player2 = false;
				for(int i=0; i < board.redPawns.size(); i++) {
					board.redPawns.get(i).KnockedOut();
				}
				break;
			case 3:
				for(int i=0; i < board.yellowPawns.size(); i++) {
					board.yellowPawns.get(i).KnockedOut();
				}
				player3 = false;
				break;
			case 4:
				for(int i=0; i < board.bluePawns.size(); i++) {
					board.bluePawns.get(i).KnockedOut();
				}
				player4 = false;
				break;
			default:
				break;
		}
	}
	
	public void setDiceImage(int diceValue) {
		
		dieTextLabel.setText(Main.messages.getString("YOUGOTA"));
		
		switch (diceValue) {
		case 0:
			dieLabel.setImage(null);
			break;
		case 1:
			dieLabel.setImage(die1);
			break;
		case 2:
			dieLabel.setImage(die2);
			break;
		case 3:
			dieLabel.setImage(die3);
			break;
		case 4:
			dieLabel.setImage(die4);
			break;
		case 5:
			dieLabel.setImage(die5);
			break;
		case 6:
			dieLabel.setImage(die6);
			break;
		default:
			break;
		}
	}
}
