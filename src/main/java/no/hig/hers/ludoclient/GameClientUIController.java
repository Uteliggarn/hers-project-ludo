package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.SwingUtilities;

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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GameClientUIController {
	
	LudoBoardFX board;
	
	
	private int turnOwner = 2;
	private int player;
	private int pawnToMove = 0;
	private int diceRolls = 0;
	private boolean gameOver = false;
	private String playerName;
	
	private int lastDiceValue;
	private int diceValue;
	
	private Image die1;
    private Image die2;
	private Image die3;
    private Image die4;
	private Image die5;
	private Image die6;
	
	public static BufferedWriter output;
	public static BufferedReader input;
	
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
			System.out.println("Error while trying to add gameboard");
		}
		setUpGUI();
	}
	
	public void setUpGUI() {
		
		die1 = new Image("dice1.png");
		die2 = new Image("dice2.png");
		die3 = new Image("dice3.png");
		die4 = new Image("dice4.png");
		die5 = new Image("dice5.png");
		die6 = new Image("dice6.png");

		/*
		dieRoller.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				rollDiceActionListener();
			}
		
		});
		*/
		
		pawn1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 0;
				processRoll(diceValue);
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText("Roll dice");
				dieLabel.setImage(null);
				setPawnMovesFalse();
				
			}
		});
		
		pawn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 1;
				processRoll(diceValue);
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText("Roll dice");
				dieLabel.setImage(null);
				setPawnMovesFalse();
			}
		});
		
		pawn3.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 2;
				processRoll(diceValue);
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText("Roll dice");
				dieLabel.setImage(null);
				setPawnMovesFalse();
			}
		});
		
		pawn4.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				pawnToMove = 3;
				try {
				processRoll(diceValue);
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				} catch (Exception e) {
					System.out.println("test");
				}
				diceValue = 0;
				diceRolls = 0;
				dieTextLabel.setText("Roll dice");
				dieLabel.setImage(null);
				setPawnMovesFalse();
			}
		});
		
		pass.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				diceValue = 0;
				diceRolls = 0;
				
				dieTextLabel.setText("Roll dice");
				dieLabel.setImage(null);
				
				switch(turnOwner) {
				case 1:	//Green
					turnOwner ++;
					greenPlayer.setText("Green player:");
					redPlayer.setText("Red player: Your turn!");
					break;
				case 2:	//Red
					turnOwner ++;
					redPlayer.setText("Red player:");
					yellowPlayer.setText("Yellow player: Your Turn!");
					break;
				case 3: //Yellow
					turnOwner ++;
					yellowPlayer.setText("Yellow player:");
					bluePlayer.setText("Blue player: Your turn!");
					break;
				case 4: //Blue
					turnOwner = 1;
					bluePlayer.setText("Blue player:");
					greenPlayer.setText("Green player: Your Turn!");
					break;
					
				}
				setPawnMovesFalse();
				setNotValid();
				SendDiceValue(diceValue, turnOwner, pawnToMove);
				dieRoller.setDisable(false);
				dieRoller.setText("Roll dice");
		/*
		pass.setStyle("-fx-background-color: red");
		pass.getStylesheets().add("ludoBoard.css");
		*/
				
			}
		});
		setNotValidPass();
		setPawnMovesFalse();
		dieRoller.setDisable(true);
	}
	
	
	@FXML
	private void rollDice(ActionEvent even) {
//		if (yourTurn) {
		//dieRoller.setDisable(false);
		rollDiceActionListener();
	//	dieRoller.setEnabled(false);
	//	yourTurn = false;
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
						dieRoller.setText("Move");
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
						dieRoller.setText("Move");
						for(int j=0; j < board.redPawns.size(); j++) {
							if(board.redPawns.get(j).isValid()) {
								setPawnMovesTrue(j);
							}
						}
						//setPawnMovesTrue();
					}
				}
				break;
			case 3:	//Yellow
				for(int i=0; i < board.yellowPawns.size(); i++) {
					if(board.yellowPawns.get(i).validMove(diceValue) || diceValue == 6) {
						dieRoller.setDisable(true);
						dieRoller.setText("Move");
						for(int j=0; j < board.yellowPawns.size(); j++) {
							if(board.yellowPawns.get(j).isValid()) {
								setPawnMovesTrue(j);
							}
						}
						//setPawnMovesTrue();
					}
				}
				break;
			case 4:	//Blue
				for(int i=0; i < board.bluePawns.size(); i++) {
					if(board.bluePawns.get(i).validMove(diceValue) || diceValue == 6) {
						dieRoller.setDisable(true);
						dieRoller.setText("Move");
						for(int j=0; j < board.bluePawns.size(); j++) {
							if(board.bluePawns.get(j).isValid()) {
								setPawnMovesTrue(j);
							}
						}
					//	setPawnMovesTrue();
					}
				}
			}
				
			//if (turnOwner == player) {
				//sendText(throwDiceText + player);
			//} else {
			//	displayMessage("It's not your turn!\n");
			//}
			
			switch (diceValue) {
			case 1:
				dieTextLabel.setText("You got a: ");
				dieLabel.setImage(die1);
				break;
			case 2:
				dieTextLabel.setText("You got a: ");
				dieLabel.setImage(die2);
				break;
			case 3:
				dieTextLabel.setText("You got a: ");
				dieLabel.setImage(die3);
				break;
			case 4:
				dieTextLabel.setText("You got a: ");
				dieLabel.setImage(die4);
				break;
			case 5:
				dieTextLabel.setText("You got a: ");
				dieLabel.setImage(die5);
				break;
			case 6:
				dieTextLabel.setText("You got a: ");
				dieLabel.setImage(die6);
				break;
			}	
			
			if(gameOver) {
				dieRoller.setDisable(true);
				dieRoller.setText("GG");
			}
		}
		else {
			dieRoller.setText("Pass");
			dieRoller.setDisable(true);
		}
	}
	
	private void processRoll(int diceVal) {
		int inGoal;
		if ( turnOwner == 1 && diceValue !=0) {	//Green player
			board.greenPawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
			inGoal = board.greenPawnsInGoal.size();
			if (inGoal == 4) {
				//displayMessage("Green Player won");
				System.out.println(("You won"));
				gameOver = true;
			}
			if(diceVal !=6) {
			turnOwner ++;
			greenPlayer.setText("Green player:");
			redPlayer.setText("Red player: Your turn!");
			}
			setNotValid();
			board.makePawns();
			}
		else if(turnOwner == 2 && diceValue !=0) { //Red player
			try {
				board.redPawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
				inGoal = board.redPawnsInGoal.size();
				if (inGoal == 4) {
						System.out.println(("You won"));
						gameOver = true;
						//displayMessage("Red Player won");
				}
				pawnToMove = 0;
			} catch (Exception e ) {
				System.out.println("Goalerror");
			}
			if(diceVal !=6) {
			turnOwner ++;
			redPlayer.setText("Red player:");
			yellowPlayer.setText("Yellow player: Your Turn!");
			}
			setNotValid();
			board.makePawns();
		}
		else if (turnOwner == 3 && diceValue !=0) { //Yellow player
			board.yellowPawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
			inGoal = board.yellowPawnsInGoal.size();
			if (inGoal == 4) {
				//displayMessage("Yellow Player won");
				System.out.println(("You won"));
				gameOver = true;
			}
			if(diceVal !=6) {
			turnOwner ++;
			yellowPlayer.setText("Yellow player:");
			bluePlayer.setText("Blue player: Your turn!");
			}
			setNotValid();
			board.makePawns();
		}
		else if (turnOwner == 4 && diceValue !=0) { //Blue player
			board.bluePawns.get(pawnToMove).changeLocation(diceValue, turnOwner, pawnToMove);
			inGoal = board.bluePawnsInGoal.size();
			if (inGoal == 4) {
				//displayMessage("Blue Player won");
				System.out.println(("You won"));
				gameOver = true;
			}
			if(diceVal !=6) {
			bluePlayer.setText("Blue player:");
			greenPlayer.setText("Green player: Your Turn!");
			turnOwner = 1;
			}
			setNotValid();
			board.makePawns();
		}
		setPawnMovesFalse();
		dieRoller.setDisable(false);
		dieRoller.setText("Roll dice");
		
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
			dieRoller.setText("Roll");
		}
		
	}
	
	public void setPlayerName(String name) {
		playerName = name;
		switch (player) {
			case 1:
				greenPlayer.setText("Green: " + playerName);
				break;
			case 2:
				redPlayer.setText("Red: " + playerName);
				break;
			case 3: 
				yellowPlayer.setText("Yellow: " + playerName);
				break;
			case 4:
				bluePlayer.setText("Blue: " + playerName);
				break;
		
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
		if(diceValue == 0) {
			passChangeTurnOwner();
		} else processRoll(diceV);
		diceValue = 0;
		diceRolls = 0;
		setPawnMovesFalse();
		if(turnOwner == player) {
			dieRoller.setDisable(false);
			setValidPass();
		}
	}
	
	/**
     * Used to add messages to the message area in a thread safe manner
     * 
     * @param text
     *            the text to be added
     */
    private void displayMessage(String text) {
        SwingUtilities.invokeLater(() -> chatArea.appendText(text));
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
		tmp = ("dicevalue:" + diceVal + playernr + pawn);
		try {
			sendText(tmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void passChangeTurnOwner() {
		
		switch(turnOwner) {
		case 1:	//Green
			turnOwner ++;
			greenPlayer.setText("Green:");
			redPlayer.setText("Red: Your turn!");
			break;
		case 2:	//Red
			turnOwner ++;
			redPlayer.setText("Red:");
			yellowPlayer.setText("Yellow: Your Turn!");
			break;
		case 3: //Yellow
			turnOwner ++;
			yellowPlayer.setText("Yellow:");
			bluePlayer.setText("Blue: Your turn!");
			break;
		case 4: //Blue
			turnOwner = 1;
			bluePlayer.setText("Blue:");
			greenPlayer.setText("Green: Your Turn!");
			break;
			
		}
	}
}
