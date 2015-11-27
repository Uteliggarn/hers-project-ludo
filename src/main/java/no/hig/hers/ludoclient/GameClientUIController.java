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
 * @author Hauken on 17.11.2015
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
	
	/**
	 * Method that is called when the game starts.
	 * Adds the gameboard, make the gamelogic and setup the GUI
 	 */
	@FXML
	public void initialize() {
		
		try {	
			board = new LudoBoardFX();
			gameClientPane.setCenter(board);
		} catch (Exception e) {
			Main.LOGGER.log(Level.WARNING, "Error while trying to add gameboard", e);
		}
		setUpGUI();
	}
	/**
	 * Sets up most of the GAMEGUI
	 * Sets buttons to be disabled until it is your turn.
	 * Also set up the on action for the game buttons except the dice roller. 
	 * All the on acions will send the values to the game server
	 * and then to all the players when pressed. 
	 * The values will not be process before it is received. 
	 *  
	 */
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
				sendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText(Main.messages.getString("ROLLDICE"));
				setPawnMovesFalse();	
			}
		});

		pawn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 1;
				sendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText(Main.messages.getString("ROLLDICE"));
				setPawnMovesFalse();
			}
		});
		
		pawn3.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 2;
				sendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText(Main.messages.getString("ROLLDICE"));
				setPawnMovesFalse();
			}
		});
		
		pawn4.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 3;
				sendDiceValue(diceValue, turnOwner, pawnToMove);
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
				sendDiceValue(diceValue, turnOwner, pawnToMove);
			}
		});
		
		setNotValidPass();
		setPawnMovesFalse();
		dieRoller.setDisable(true);
	}
	
	/**
	 * Starts the function that rolls the dice and sets for 
	 * @param even Button pressed
	 */
	@FXML
	private void rollDice() {
		rollDiceActionListener();
	}
	/**
	 * Sets the correct text for the pawn buttons
	 */
	public void setTextOnLabels() {
		pawn1.setText(Main.messages.getString("PAWN1"));
		pawn2.setText(Main.messages.getString("PAWN2"));
		pawn3.setText(Main.messages.getString("PAWN3"));
		pawn4.setText(Main.messages.getString("PAWN4"));
		pass.setText(Main.messages.getString("PASS"));
	}
	/**
	 * Methods that started when the roll dice button is pressed.
	 * You will get three rolls as long as you dont got a valid move. 
	 * You get a extra roll if you get a six. 
	 * You need to pass if you dont got a valid move after three rolls.
	 * Make the valid buttons available when you got a valid move.
	 * Also show what value you get with a image.
	 */
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
				break;
			default : 
				break;
			}
		setDiceImage(diceValue);
		}
		else {
			dieRoller.setText(Main.messages.getString("PASS"));
			dieRoller.setDisable(true);
		}
	}
	/**
	 * Method that process the roll of the player that last moved a pawn or passed. 
	 * Changes the location of the chosen pawn by the value of the dicevalue. 
	 * The next method called, will test for towers, knock out other colors or try to make a
	 * tower.
	 * Also changes the GUI elements to be correct and set to valid as long as it 
	 * is your turn. 
	 */
	private void processRoll() {
		int inGoal;
		if ( turnOwner == 1 && diceValue !=0) {	//Green player
			board.greenPawns.get(pawnToMove).changeLocation(diceValue, pawnToMove);
			inGoal = board.greenPawnsInGoal.size();
			if (inGoal == 4 && player == 1) {
				gameStatus = 1;
				gameOver = true;
			}
			if(diceValue !=6) {
				checkforPlayersGreen();
			}
			setNotValid();
			board.makePawns();
			}
		else if(turnOwner == 2 && diceValue !=0) { //Red player
			try {
				board.redPawns.get(pawnToMove).changeLocation(diceValue, pawnToMove);
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
				checkforPlayersRed();
			}
			setNotValid();
			board.makePawns();
		}
		else if (turnOwner == 3 && diceValue !=0) { //Yellow player
			board.yellowPawns.get(pawnToMove).changeLocation(diceValue, pawnToMove);
			inGoal = board.yellowPawnsInGoal.size();
			if (inGoal == 4 && player == 3) {
				gameStatus = 1;
				gameOver = true;
			}
			if(diceValue !=6) {
				checkforPlayersYellow();
			}
			setNotValid();
			board.makePawns();
		}
		else if (turnOwner == 4 && diceValue !=0) { //Blue player
			board.bluePawns.get(pawnToMove).changeLocation(diceValue, pawnToMove);
			inGoal = board.bluePawnsInGoal.size();
			if (inGoal == 4 && player == 4) {
				gameStatus = 1;
				gameOver = true;
			}
			if(diceValue !=6) {
				checkforPlayersBlue();
			}
			setNotValid();
			board.makePawns();
		}
		
		if(diceValue == 0)
			passChangeTurnOwner();
		
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
	/**
	 * Sets the pawn buttons to disabled so the player cant use them
	 * as long as you dont got a valid move.
	 */
	public void setPawnMovesFalse() {
		pawn1.setDisable(true);
		pawn2.setDisable(true);
		pawn3.setDisable(true);
		pawn4.setDisable(true);
	}
	/**
	 * Sets the pass button to not usable.
	 */
	public void setNotValidPass() {
		pass.setDisable(true);
	}
	/**
	 * Sets the pass button to usable.
	 */
	public void setValidPass() {
		pass.setDisable(false);
	}
	/**
	 * If there is a valid move on a pawn, the pawn button is set to 
	 * usable.
	 * @param val the value of the valid pawn.
	 */
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
		default :
			break;
		}
	}
	/**
	 * Sets all the pawns of the current player to not valid.
	 */
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
		default:
			break;
		}
	}
	/**
	 * Sets a player to a color. If the player is player two, he/she starts to roll the dice.
	 * @param n the player number
	 */
	public void setPlayer(int n) {
		player = n;
		if(player == turnOwner) {
			setValidPass();
			dieRoller.setDisable(false);
			dieRoller.setText(Main.messages.getString("ROLL"));
			dieTextLabel.setText(Main.messages.getString("YOURTURN"));
		} else dieTextLabel.setText(Main.messages.getString("WAITFORYOURTURN"));	
	}
	/**
	 * Returns the chosen player. Used to handle disconnects.
	 * @return returns the player, or the color in this case. 
	 */
	public int getPlayer() {
		return player;
	}
	/**
	 * Returns the current turn owner. This is used to check if the 
	 * player disconnected is the player that is currently rolling.
	 * @return returns the turn owner. 
	 */
	public int getTurnOwner() {
		return turnOwner;
	}
	/**
	 * Sets the correct player to the correct color/player label.
	 * @param pnr player number/color of the player
	 * @param name the name to be set to the label of that color
	 */
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
	/**
	 * Sets the connection so the controller can communicate with the game server.
	 * @param write output
	 * @param read input Not used. Is handled by the Gamehandler
	 */
	public void setConnetion(BufferedWriter write, BufferedReader read) {
		output = write;
		input = read;
	}
	/**
	 * Gets the dicevalue, the player that rolled/passed and what pawn that player moved. 
	 * This is called by the gamehandler and the values is received from the game server.
	 * The last player that played sent these values to the game server and this method 
	 * process them when they return to the players.
	 * @param diceV the dice value
	 * @param playernr the player that moved/passed
	 * @param pawn the pawn that that the player moved. Is 0 if passed.
	 */
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
	/**
	 * Sends the dicevalue, the player number of the current player and 
	 * what pawn that is moved to the game server.
	 * @param diceVal the dice value
	 * @param playernr turn owner
	 * @param pawn Pawned that is moved. 0 if passed
	 */
	public void sendDiceValue(int diceVal, int playernr, int pawn) {
		String tmp;
		tmp = (Constants.DICEVALUE + diceVal + playernr + pawn);
		try {
			sendText(tmp);
		} catch (IOException e) {
			Main.LOGGER.log(Level.WARNING, "Error sending message to server", e);
		}
	}
	/**
	 * Sends that the game is over to the other players
	 * and that this player won to the database.
	 */
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
	/**
	 * Changes the labels when the turn is passed.
	 */
	public void passChangeTurnOwner() {
		
		switch(turnOwner) {
		case 1:	//Green
			checkforPlayersGreen();
			break; 
		case 2:	//Red
			checkforPlayersRed();
			break;
		case 3: //Yellow
			checkforPlayersYellow();
			break;
		case 4: //Blue
			checkforPlayersBlue();
			break;
		default: break;
		}
	}
	/**
	 * Sets the game to be over
	 */
	public void gameover() {
		gameOver = true;
	}
	/**
	 * Sets the correct labels for the green player.
	 * If this player is the last man standing, the game is over and that player won.
	 */
	public void checkforPlayersGreen() {
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
			gameStatus = 1;
			gameOver = true;
		}
	}
	/**
	 * Sets the correct labels for the red player and changes turn owner.
	 * If this player is the last man standing, the game is over and that player won. 
	 */
	public void checkforPlayersRed() {
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
			gameStatus = 1;
			gameOver = true;
		}
	}
	/**
	 * Sets the correct labels for the yellow player.
	 * If this player is the last man standing, the game is over and that player won.
	 */
	public void checkforPlayersYellow() {
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
			gameStatus = 1;
			gameOver = true;
		}
	}
	/**
	 * Sets the correct labels for the blue player. 
	 * If this player is the last man standing, the game is over and that player won.
	 */
	public void checkforPlayersBlue() {
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
			gameStatus = 1;
			gameOver = true;
		}
	}
	/**
	 * Sets the player that is disconnect to not playing
	 * It also sets all the pawns of that color back to home.
	 * @param dced the disconnected player
	 */
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
	/**
	 * Sets a image showing the dice value that is received. 
	 * @param diceValue the dice value
	 */
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
