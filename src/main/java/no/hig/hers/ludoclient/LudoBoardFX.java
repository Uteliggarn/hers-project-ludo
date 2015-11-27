package no.hig.hers.ludoclient;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

/**
 * Class that keeps track of the Ludogame. 
 * It holds all the game logic and stores information about every pawn. 
 * It also draws the game board and the pawns onto the board. 
 * @author Hauken
 *
 */
public class LudoBoardFX extends Pane {
	
	Canvas gameBoard;
	Canvas gamePieces;
	Image board;
	private int drawn;
	private Color color[] = {Color.BLACK, Color.LAWNGREEN, Color.YELLOW, 
			Color.RED, Color.BLUE, Color.WHITE, Color.ORANGE, Color.GRAY};
	
	//Vectors that keep track of each colors coordinates (valid coordinates for the pawns to move to) 
	private Vector<Point> coordinatesGreen= new Vector<>();
	private Vector<Point> coordinatesRed = new Vector<>();
	private Vector<Point> coordinatesYellow= new Vector<>();
	private Vector<Point> coordinatesBlue = new Vector<>();
	
	//Arraylist that keeps track of where the pawns of each color is on the board
	final ArrayList<Pawned> greenPawns = new ArrayList<Pawned>();
	final ArrayList<Pawned> yellowPawns = new ArrayList<Pawned>();
	final ArrayList<Pawned> redPawns = new ArrayList<Pawned>();
	final ArrayList<Pawned> bluePawns = new ArrayList<Pawned>();
	
	//Arraylist that keeps track of how many pawns of each color is in the goal
	//If all pawns of a color is in their respectively arrayslists, that player wins.
	final ArrayList<Pawned> greenPawnsInGoal = new ArrayList<Pawned>();
	final ArrayList<Pawned> yellowPawnsInGoal = new ArrayList<Pawned>();
	final ArrayList<Pawned> redPawnsInGoal = new ArrayList<Pawned>();
	final ArrayList<Pawned> bluePawnsInGoal = new ArrayList<Pawned>();
	
	/**
	 * Constructor of the ludoBoardFX class.
	 * Makes all the possible coordinates for the pawns, adds pawns to each player and draws them, 
	 * and makes the gameboard.
	 */
	LudoBoardFX() {
		
		try {
			drawn = 0;
			
			//Makes all the valid coordinates the pawns can move on
			makeGreenCoordinates();
			makeRedCoordinates();
			makeYellowCoordinates();
			makeBlueCoordinates();
			
			//Tries to add pawns for each color
			try {
				addPawns(greenPawns, 1);
				addPawns(yellowPawns, 2);
				addPawns(redPawns, 3);
				addPawns(bluePawns, 4);
			} catch (Exception e) {
				Main.LOGGER.log(Level.SEVERE, "Unable to add pawns", e);
			}
			
			//Makes a new canvas and draws the gameboard onto it
			drawGameBoard();
			//Makes a new canvas and draw pawns onto it
			makePawns();
		} catch(Exception e) {
			Main.LOGGER.log(Level.SEVERE, "Error while drawing board", e);
		}
		setMinSize(900, 900);

	}
	/**
	 * Draws the game board on a canvas and adds it to the pane of the class. 
	 */
	private void drawGameBoard() {
		board = new Image("images/ludo_board.png", 800, 800, true, true);
		gameBoard = new Canvas(800, 800);
		GraphicsContext gb = gameBoard.getGraphicsContext2D();
		gb.drawImage(board, 0, 0);
		getChildren().add(gameBoard);
	}
	/**
	 * Makes all the coordinates that the green pawns can move to.
	 * Makes the coordinates as points and add them to a Vector.
	 */
	private void makeGreenCoordinates() {
		
		//Green home:
		coordinatesGreen.add(new Point(160, 110));	//location 0
		coordinatesGreen.add(new Point(110, 160));
		coordinatesGreen.add(new Point(210, 160));
		coordinatesGreen.add(new Point(160, 210));
		
		//Green Track
		coordinatesGreen.add(new Point(85, 335));
		coordinatesGreen.add(new Point(135, 335));
		coordinatesGreen.add(new Point(185, 335));
		coordinatesGreen.add(new Point(235, 335));
		coordinatesGreen.add(new Point(285, 335));
		
		coordinatesGreen.add(new Point(335, 285));
		coordinatesGreen.add(new Point(335, 235));
		coordinatesGreen.add(new Point(335, 185));
		coordinatesGreen.add(new Point(335, 135));
		coordinatesGreen.add(new Point(335, 85));
		
		coordinatesGreen.add(new Point(335, 35));	
		coordinatesGreen.add(new Point(385, 35));
		coordinatesGreen.add(new Point(435, 35));
		
		coordinatesGreen.add(new Point(435, 85));
		coordinatesGreen.add(new Point(435, 135));
		coordinatesGreen.add(new Point(435, 185));
		coordinatesGreen.add(new Point(435, 235));
		coordinatesGreen.add(new Point(435, 285));
		
		coordinatesGreen.add(new Point(485, 335));
		coordinatesGreen.add(new Point(535, 335));
		coordinatesGreen.add(new Point(585, 335));
		coordinatesGreen.add(new Point(635, 335));
		coordinatesGreen.add(new Point(685, 335));
		
		coordinatesGreen.add(new Point(735, 335));
		coordinatesGreen.add(new Point(735, 385));
		coordinatesGreen.add(new Point(735, 435));
		
		coordinatesGreen.add(new Point(685, 435));
		coordinatesGreen.add(new Point(635, 435));
		coordinatesGreen.add(new Point(585, 435));
		coordinatesGreen.add(new Point(535, 435));
		coordinatesGreen.add(new Point(485, 435));
		
		coordinatesGreen.add(new Point(435, 485));
		coordinatesGreen.add(new Point(435, 535));
		coordinatesGreen.add(new Point(435, 585));
		coordinatesGreen.add(new Point(435, 635));
		coordinatesGreen.add(new Point(435, 685));
		
		coordinatesGreen.add(new Point(435, 735));
		coordinatesGreen.add(new Point(385, 735));
		coordinatesGreen.add(new Point(335, 735));
		
		coordinatesGreen.add(new Point(335, 685));
		coordinatesGreen.add(new Point(335, 635));
		coordinatesGreen.add(new Point(335, 585));
		coordinatesGreen.add(new Point(335, 535));
		coordinatesGreen.add(new Point(335, 485));
		
		coordinatesGreen.add(new Point(285, 435));
		coordinatesGreen.add(new Point(235, 435));
		coordinatesGreen.add(new Point(185, 435));
		coordinatesGreen.add(new Point(135, 435));
		coordinatesGreen.add(new Point(85, 435));
		
		coordinatesGreen.add(new Point(35, 435));
		coordinatesGreen.add(new Point(35, 385));
		coordinatesGreen.add(new Point(35, 335));
		
		//Green Goal highway
		coordinatesGreen.add(new Point(85, 335));
		coordinatesGreen.add(new Point(85, 385));
		coordinatesGreen.add(new Point(135,385));
		coordinatesGreen.add(new Point(185,385));
		coordinatesGreen.add(new Point(235,385));
		coordinatesGreen.add(new Point(285,385));
		
		//Green Goal
		coordinatesGreen.add(new Point(335,385));	//Location 62	
	}
	/**
	 * Makes all the coordinates that the red pawns can move to.
	 * Makes the coordinates as points and add them to a Vector.
	 */
	private void makeRedCoordinates() {
		//Red home coordinates
		coordinatesRed.add(new Point(610, 110));
		coordinatesRed.add(new Point(560, 160));
		coordinatesRed.add(new Point(660, 160));
		coordinatesRed.add(new Point(610, 210));
		
		//Red Track
		coordinatesRed.add(new Point(435, 85));
		coordinatesRed.add(new Point(435, 135));
		coordinatesRed.add(new Point(435, 185));
		coordinatesRed.add(new Point(435, 235));
		coordinatesRed.add(new Point(435, 285));
		
		coordinatesRed.add(new Point(485, 335));
		coordinatesRed.add(new Point(535, 335));
		coordinatesRed.add(new Point(585, 335));
		coordinatesRed.add(new Point(635, 335));
		coordinatesRed.add(new Point(685, 335));
		
		coordinatesRed.add(new Point(735, 335));
		coordinatesRed.add(new Point(735, 385));
		coordinatesRed.add(new Point(735, 435));
		
		coordinatesRed.add(new Point(685, 435));
		coordinatesRed.add(new Point(635, 435));
		coordinatesRed.add(new Point(585, 435));
		coordinatesRed.add(new Point(535, 435));
		coordinatesRed.add(new Point(485, 435));
		
		coordinatesRed.add(new Point(435, 485));
		coordinatesRed.add(new Point(435, 535));
		coordinatesRed.add(new Point(435, 585));
		coordinatesRed.add(new Point(435, 635));
		coordinatesRed.add(new Point(435, 685));
		
		coordinatesRed.add(new Point(435, 735));
		coordinatesRed.add(new Point(385, 735));
		coordinatesRed.add(new Point(335, 735));
		
		coordinatesRed.add(new Point(335, 685));
		coordinatesRed.add(new Point(335, 635));
		coordinatesRed.add(new Point(335, 585));
		coordinatesRed.add(new Point(335, 535));
		coordinatesRed.add(new Point(335, 485));
		
		coordinatesRed.add(new Point(285, 435));
		coordinatesRed.add(new Point(235, 435));
		coordinatesRed.add(new Point(185, 435));
		coordinatesRed.add(new Point(135, 435));
		coordinatesRed.add(new Point(85, 435));
		
		coordinatesRed.add(new Point(35, 435));
		coordinatesRed.add(new Point(35, 385));
		coordinatesRed.add(new Point(35, 335));
		
		coordinatesRed.add(new Point(85, 335));
		coordinatesRed.add(new Point(135, 335));
		coordinatesRed.add(new Point(185, 335));
		coordinatesRed.add(new Point(235, 335));
		coordinatesRed.add(new Point(285, 335));
		
		coordinatesRed.add(new Point(335, 285));
		coordinatesRed.add(new Point(335, 235));
		coordinatesRed.add(new Point(335, 185));
		coordinatesRed.add(new Point(335, 135));
		coordinatesRed.add(new Point(335, 85));
		
		coordinatesRed.add(new Point(335, 35));	
		coordinatesRed.add(new Point(385, 35));
		coordinatesRed.add(new Point(435, 35));
		
		//Red Goalhighway coordinates
		coordinatesRed.add(new Point(435, 85));
		coordinatesRed.add(new Point(385, 85));
		coordinatesRed.add(new Point(385, 135));
		coordinatesRed.add(new Point(385, 185));
		coordinatesRed.add(new Point(385, 235));
		coordinatesRed.add(new Point(385, 285));
		
		//Red Goal coordinate
		coordinatesRed.add(new Point(385, 335));
		
	}
	/**
	 * Makes all the coordinates that the yellow pawns can move to. 
	 * Makes the coordinates as points and add them to a Vector.
	 */
	private void makeYellowCoordinates() {
		//Yellow home coordinates
		coordinatesYellow.add(new Point(160, 560));
		coordinatesYellow.add(new Point(110, 610));
		coordinatesYellow.add(new Point(210, 610));
		coordinatesYellow.add(new Point(160, 660));
		
		//Yellow Track
		coordinatesYellow.add(new Point(335, 685));
		coordinatesYellow.add(new Point(335, 635));
		coordinatesYellow.add(new Point(335, 585));
		coordinatesYellow.add(new Point(335, 535));
		coordinatesYellow.add(new Point(335, 485));
		
		coordinatesYellow.add(new Point(285, 435));
		coordinatesYellow.add(new Point(235, 435));
		coordinatesYellow.add(new Point(185, 435));
		coordinatesYellow.add(new Point(135, 435));
		coordinatesYellow.add(new Point(85, 435));
		
		coordinatesYellow.add(new Point(35, 435));
		coordinatesYellow.add(new Point(35, 385));
		coordinatesYellow.add(new Point(35, 335));
		
		coordinatesYellow.add(new Point(85, 335));
		coordinatesYellow.add(new Point(135, 335));
		coordinatesYellow.add(new Point(185, 335));
		coordinatesYellow.add(new Point(235, 335));
		coordinatesYellow.add(new Point(285, 335));
		
		coordinatesYellow.add(new Point(335, 285));
		coordinatesYellow.add(new Point(335, 235));
		coordinatesYellow.add(new Point(335, 185));
		coordinatesYellow.add(new Point(335, 135));
		coordinatesYellow.add(new Point(335, 85));
		
		coordinatesYellow.add(new Point(335, 35));	
		coordinatesYellow.add(new Point(385, 35));
		coordinatesYellow.add(new Point(435, 35));
		
		coordinatesYellow.add(new Point(435, 85));
		coordinatesYellow.add(new Point(435, 135));
		coordinatesYellow.add(new Point(435, 185));
		coordinatesYellow.add(new Point(435, 235));
		coordinatesYellow.add(new Point(435, 285));
		
		coordinatesYellow.add(new Point(485, 335));
		coordinatesYellow.add(new Point(535, 335));
		coordinatesYellow.add(new Point(585, 335));
		coordinatesYellow.add(new Point(635, 335));
		coordinatesYellow.add(new Point(685, 335));
		
		coordinatesYellow.add(new Point(735, 335));
		coordinatesYellow.add(new Point(735, 385));
		coordinatesYellow.add(new Point(735, 435));
		
		coordinatesYellow.add(new Point(685, 435));
		coordinatesYellow.add(new Point(635, 435));
		coordinatesYellow.add(new Point(585, 435));
		coordinatesYellow.add(new Point(535, 435));
		coordinatesYellow.add(new Point(485, 435));
		
		coordinatesYellow.add(new Point(435, 485));
		coordinatesYellow.add(new Point(435, 535));
		coordinatesYellow.add(new Point(435, 585));
		coordinatesYellow.add(new Point(435, 635));
		coordinatesYellow.add(new Point(435, 685));
		
		coordinatesYellow.add(new Point(435, 735));
		coordinatesYellow.add(new Point(385, 735));
		coordinatesYellow.add(new Point(335, 735));
		
		//Yellow GoalHighway
		coordinatesYellow.add(new Point(335, 685));
		coordinatesYellow.add(new Point(385, 685));
		coordinatesYellow.add(new Point(385, 635));
		coordinatesYellow.add(new Point(385, 585));
		coordinatesYellow.add(new Point(385, 535));
		coordinatesYellow.add(new Point(385, 485));
		
		//Yellow Goal
		coordinatesYellow.add(new Point(385, 435));
	
	}
	/**
	 * Makes all the coordinates that the blue pawns can move to. 
	 * Makes the coordinates as points and add them to a Vector.
	 */
	private void makeBlueCoordinates() {
		//Blue Home
		coordinatesBlue.add(new Point(610, 560));
		coordinatesBlue.add(new Point(560, 610));
		coordinatesBlue.add(new Point(660, 610));
		coordinatesBlue.add(new Point(610, 660));
		
		//Blue Track
		coordinatesBlue.add(new Point(685, 435));
		coordinatesBlue.add(new Point(635, 435));
		coordinatesBlue.add(new Point(585, 435));
		coordinatesBlue.add(new Point(535, 435));
		coordinatesBlue.add(new Point(485, 435));
		
		coordinatesBlue.add(new Point(435, 485));
		coordinatesBlue.add(new Point(435, 535));
		coordinatesBlue.add(new Point(435, 585));
		coordinatesBlue.add(new Point(435, 635));
		coordinatesBlue.add(new Point(435, 685));
		
		coordinatesBlue.add(new Point(435, 735));
		coordinatesBlue.add(new Point(385, 735));
		coordinatesBlue.add(new Point(335, 735));
		
		coordinatesBlue.add(new Point(335, 685));
		coordinatesBlue.add(new Point(335, 635));
		coordinatesBlue.add(new Point(335, 585));
		coordinatesBlue.add(new Point(335, 535));
		coordinatesBlue.add(new Point(335, 485));
		
		coordinatesBlue.add(new Point(285, 435));
		coordinatesBlue.add(new Point(235, 435));
		coordinatesBlue.add(new Point(185, 435));
		coordinatesBlue.add(new Point(135, 435));
		coordinatesBlue.add(new Point(85, 435));
		
		coordinatesBlue.add(new Point(35, 435));
		coordinatesBlue.add(new Point(35, 385));
		coordinatesBlue.add(new Point(35, 335));
		
		coordinatesBlue.add(new Point(85, 335));
		coordinatesBlue.add(new Point(135, 335));
		coordinatesBlue.add(new Point(185, 335));
		coordinatesBlue.add(new Point(235, 335));
		coordinatesBlue.add(new Point(285, 335));
		
		coordinatesBlue.add(new Point(335, 285));
		coordinatesBlue.add(new Point(335, 235));
		coordinatesBlue.add(new Point(335, 185));
		coordinatesBlue.add(new Point(335, 135));
		coordinatesBlue.add(new Point(335, 85));
		
		coordinatesBlue.add(new Point(335, 35));	
		coordinatesBlue.add(new Point(385, 35));
		coordinatesBlue.add(new Point(435, 35));
		
		coordinatesBlue.add(new Point(435, 85));
		coordinatesBlue.add(new Point(435, 135));
		coordinatesBlue.add(new Point(435, 185));
		coordinatesBlue.add(new Point(435, 235));
		coordinatesBlue.add(new Point(435, 285));
		
		coordinatesBlue.add(new Point(485, 335));
		coordinatesBlue.add(new Point(535, 335));
		coordinatesBlue.add(new Point(585, 335));
		coordinatesBlue.add(new Point(635, 335));
		coordinatesBlue.add(new Point(685, 335));
		
		coordinatesBlue.add(new Point(735, 335));
		coordinatesBlue.add(new Point(735, 385));
		coordinatesBlue.add(new Point(735, 435));
		
		//Blue GoalHighway
		coordinatesBlue.add(new Point(685, 435));
		coordinatesBlue.add(new Point(685, 385));
		coordinatesBlue.add(new Point(635, 385));
		coordinatesBlue.add(new Point(585, 382));
		coordinatesBlue.add(new Point(535, 385));
		coordinatesBlue.add(new Point(485, 385));
		
		//Blue Goal
		coordinatesBlue.add(new Point(435, 385));	
	}
	/**
	 * Method that goes through the color of the paramter and checks if a pawn is on the 
	 * goal location. If it is, the pawn nr will be returned and added to a goal array, to keep track
	 * of all the pawns in goal.
	 * @param col Holds the color of the pawns that is to be checked.
	 * @return Returns the pawn nr if the methods finds a pawn on the goal location, or returns 5 to deny it.
	 */
	public int getpawnInGoalLocation(int col) {
		switch (col) {
		case 1:
			for (int j = 0; j < greenPawns.size(); j++) {
				int l;
				l = greenPawns.get(j).returnLocation();
				if (l == 62) {
					return j;
				}
			}
		case 2:
			for (int j = 0; j < yellowPawns.size(); j++) {
				int l;
				l = yellowPawns.get(j).returnLocation();
				if (l == 62) {
					return j;
				}
			}
		case 3:
			for (int j = 0; j < redPawns.size(); j++) {
				int l;
				l = redPawns.get(j).returnLocation();
				if (l == 62) {
					return j;
				}
			}
		case 4:
			for (int j = 0; j < bluePawns.size(); j++) {
				int l;
				l = bluePawns.get(j).returnLocation();
				if (l == 62) {
					return j;
				}
			}
		}
		return 5;
	}
	/**
	 * Add 4 pawns for the player with the spesific color and adds them to the arrayList of that color.
	 * @param pawns Holds the spesific arrayList of the spesific player color. The pawns are added to this. 
	 * @param color Holds what color of the pawns that is being made.
	 */
	public void addPawns(final ArrayList<Pawned> pawns, int color) {
		for(int i = 0; i < 4; i++) {
				Pawned newPawn = new Pawned(i, color);
				pawns.add(newPawn);	
		}
	}
	/**
	 * Returns the point at the element @param i in the Vector of the green color.
	 * @param i Holds what point is to be returned
	 * @return Return the point that is requested.
	 */
	public Point getGreenCoordinates(int i) {
		return coordinatesGreen.elementAt(i);
	}
	/**
	 * Returns the point at the element @param i in the Vector of the yellow color.
	 * @param i Holds what point is to be returned.
	 * @return Return the point that is requested.
	 */
	public Point getYellowCoordinates(int i) {
		return coordinatesYellow.elementAt(i);
	}
	/**
	 * Returns the point at the element @param i in the Vector of the red color.
	 * @param i Holds what point is to be returned.
	 * @return Returns the point that is requested.
	 */
	public Point getRedCoordinates(int i) {
		return coordinatesRed.elementAt(i);
	}
	/**
	 * Returns the point at the element @param i in the Vector of the red color.
	 * @param i Holds what point is to be returned
	 * @return Returns the point that is requested
	 */
	public Point getBlueCoordinates(int i) {
		return coordinatesBlue.elementAt(i);
	}
	
	//Is called istead of repaint() from swing project.
	//
	/**
	 * Method that repaints the pawns onto the gameboard.
	 * If a pawn is a tower it will make a tower pawn instead.
	 */
	void makePawns() {
		
		//Used to clear the canvas
		if(drawn > 0) {
		GraphicsContext gp1 = gamePieces.getGraphicsContext2D();

		gp1.clearRect(0, 0, 800, 800);
		}
		else gamePieces = new Canvas(800, 800);

		GraphicsContext gp = gamePieces.getGraphicsContext2D();
		try {	
			for (int j = 0; j < greenPawns.size() ; j++) {
				greenPawns.get(j).setVisible();
				if (greenPawns.get(j).getVisible()) {
					int l = greenPawns.get(j).returnLocation();
					//Draw Pawns
					gp.setStroke(color[0]);
					gp.setFill(color[1]);
					gp.fillOval(coordinatesGreen.elementAt(l).x, coordinatesGreen.elementAt(l).y, 30, 30);
					gp.strokeOval(coordinatesGreen.elementAt(l).x, coordinatesGreen.elementAt(l).y, 30, 30);
					//Draw numbers and mark if tower or not
					gp.setFill(color[0]);
					gp.setFont(new Font("Dialog",20));
					if(greenPawns.get(j).getTower()) {
						gp.fillText("T", coordinatesGreen.elementAt(l).x+2, coordinatesGreen.elementAt(l).y+20);
						gp.fillText("" + (j+1), coordinatesGreen.elementAt(l).x+15, coordinatesGreen.elementAt(l).y+20);
					} else
						gp.fillText("" + (j+1), coordinatesGreen.elementAt(l).x+10, coordinatesGreen.elementAt(l).y+20);
						
				}
			}
			for (int j = 0; j < yellowPawns.size() ; j++) {
				if (yellowPawns.get(j).getVisible()) {
					int l = yellowPawns.get(j).returnLocation();
					//Draw pawns
					gp.setStroke(color[0]);
					gp.setFill(color[2]);
					gp.fillOval(coordinatesYellow.elementAt(l).x, coordinatesYellow.elementAt(l).y, 30, 30);
					gp.strokeOval(coordinatesYellow.elementAt(l).x, coordinatesYellow.elementAt(l).y, 30, 30);
					//Draw numbers and mark if tower or not
					gp.setFill(color[0]);
					gp.setFont(new Font("Dialog",20));
					
					if(yellowPawns.get(j).getTower()) {
						gp.fillText("T", coordinatesYellow.elementAt(l).x+2, coordinatesYellow.elementAt(l).y+20);
						gp.fillText("" + (j+1), coordinatesYellow.elementAt(l).x+15, coordinatesYellow.elementAt(l).y+20);
					} else
						gp.fillText("" + (j+1), coordinatesYellow.elementAt(l).x+10, coordinatesYellow.elementAt(l).y+20);
						
				}	
			}
			for (int j = 0; j < redPawns.size() ; j++) {
				if (redPawns.get(j).getVisible()) {
					int l = redPawns.get(j).returnLocation();
					//Draw pawns
					gp.setStroke(color[0]);
					gp.setFill(color[3]);
					gp.fillOval(coordinatesRed.elementAt(l).x, coordinatesRed.elementAt(l).y, 30, 30);
					gp.strokeOval(coordinatesRed.elementAt(l).x, coordinatesRed.elementAt(l).y, 30, 30);
					//Draw numbers and mark if tower or not
					gp.setFill(color[5]);
					gp.setFont(new Font("Dialog",20));
					
					if(redPawns.get(j).getTower()) {
						gp.fillText("T", coordinatesRed.elementAt(l).x+2, coordinatesRed.elementAt(l).y+20);
						gp.fillText("" + (j+1), coordinatesRed.elementAt(l).x+15, coordinatesRed.elementAt(l).y+20);
					} else
						gp.fillText("" + (j+1), coordinatesRed.elementAt(l).x+10, coordinatesRed.elementAt(l).y+20);	
				}
			}
			for (int j = 0; j < bluePawns.size() ; j++) {	
				if (bluePawns.get(j).getVisible()) {
					int l = bluePawns.get(j).returnLocation();
					//Draw pawns
					gp.setStroke(color[0]);
					gp.setFill(color[4]);
					gp.fillOval(coordinatesBlue.elementAt(l).x, coordinatesBlue.elementAt(l).y, 30, 30);
					gp.strokeOval(coordinatesBlue.elementAt(l).x, coordinatesBlue.elementAt(l).y, 30, 30);
					//Draw numbers and mark if tower or not
					gp.setFill(color[5]);
					gp.setFont(new Font("Dialog",20));
					if(bluePawns.get(j).getTower()) {
						gp.fillText("T", coordinatesBlue.elementAt(l).x+2, coordinatesBlue.elementAt(l).y+20);
						gp.fillText("" + (j+1), coordinatesBlue.elementAt(l).x+15, coordinatesBlue.elementAt(l).y+20);
					} else
						gp.fillText("" + (j+1), coordinatesBlue.elementAt(l).x+10, coordinatesBlue.elementAt(l).y+20);
						
				}
			}
			if(drawn == 0) getChildren().add(gamePieces);
			drawn++;
		} catch (IndexOutOfBoundsException e) {
			Main.LOGGER.log(Level.SEVERE, "Error while trying to paint pawn", e);
		}	
	}
	/**
	 * Class that holds information about every pawn and the gamelogic.
	 * It checks where the pawns homelocation is, where is it located on the board,
	 * what color is it, is it a tower, should it be drawn onto the gameboard and if it can be moved.
	 * Each pawn got methods to create towers, knock out pawns of other colors if it
	 * lands on it, adding themself to goal if it is reached (landed on), and check for towers 
	 * that other colors has created so that that it will bounce away from it if it are trying to pass
	 * them.
	 * Towers should not be able to move, therefore should a pawn that is a tower, make itself not a tower
	 * before it moves.
	 * 
	 * @author Hauken
	 *
	 */
	public class Pawned {
		private int location; //location 0-3 is homelocation.
		private int homelocation;	//Used so that each pawn knows where it should be places if knocked out
		private int color;		
		private boolean inHome;
		private boolean isTower;
		private boolean visible;
		private boolean canBeMoved;
		
		/**
		 * Class constructor that makes every pawn and set their starting location
		 * @param loc holds the start location of the current pawn
		 * @param col holds the same number as location, but this is to verify the color
		 */
		public Pawned(int loc, int col){
			homelocation = loc;
			location = loc; 
			color = col;
			inHome = true;
			isTower = false;
			visible = true;
			canBeMoved = false;
		}
		/**
		 * Method that returns a pawns location
		 * @return returns a pawns current location
		 */
		public int returnLocation() {
			return location;
		}
		/**
		 * Method that returns a pawns homelocation.
		 *  This is used when the pawn is knocked back to the starting location.
		 * @return returns a pawns homelocation
		 */
		public int returnHomeLocation() {
			return homelocation;
		}
		/**
		 * Method that returns the inHome boolean.
		 * It keeps track of whether the pawn is in it's colors homefield or not.
		 * @return
		 */
		public boolean returnInHome() {
			return inHome;
		}
		/**
		 * Method that changes a location of a pawn. This is where most of the gamelogic happens. 
		 * This methods call other methods that create tower, leave towers, knock out other pawns, and
		 * tests for towers of other colors that cannot be passed. It also tries to add the pawn to goal.
		 * @param n The dicevalue that the player has got 
		 * @param pawnToMove what pawn that is being moving
		 */
		public void changeLocation(int n, int pawnToMove) {
			int l;
			int t;
			int loc = location;
			int temp = loc += n;
			
			if (getTower()) {
				moveFromTower(pawnToMove);
			}
			if ( temp > coordinatesGreen.size() - 1) {	//Does not matter which coordinate is used here
				int j;
				j = temp - 62;
				t = testForTowers(pawnToMove, n);
				if(t == 0) { 
					location = 62 - j;
				} else bounceFromTower(t, n);
			} 
			else {
				if(!inHome) {	
					t = testForTowers(pawnToMove, n);
					if(t == 0) {
						makeTowers(temp, pawnToMove);
						knockOutOtherColors(pawnToMove, temp);
						location += n;
						tryAddToGoal();
					} else {
						bounceFromTower(t, n);
						//makeTowers(location, pawnToMove);
						//knockOutOtherColors(pawnToMove, location);
					}
				}
				else {
					if(n == 6) {
						location = 4;
						inHome = false;
					} else System.out.println("Need to get a 6 to move the pawn from the homefield");
				}
			}
		}
		
		/**
		 * Method that make towers of all colors. This method is called if a 
		 * @param temp location of other pawns
		 * @param pawnToMove the pawn currently being moved
		 */
		public void makeTowers(int temp, int pawnToMove) {
			int l;
			switch(color) {
			case 1:	//Green
				for(int i = 0; i < greenPawns.size(); i++) {
					l = greenPawns.get(i).returnLocation();
					if ( temp == l && greenPawns.get(i).getVisible()) {	//Same color on same position
						makeGreenTower(i, pawnToMove);
					}		
				}
				break;
			case 2:	//Yellow
				for(int i = 0; i < yellowPawns.size(); i++) {
					l = yellowPawns.get(i).returnLocation();
					if ( temp == l && yellowPawns.get(i).getVisible()) {	//Same color on same position
						makeYellowTower(i, pawnToMove);
					}
				}
				break;
			case 3:	//Red
				for(int i = 0; i < redPawns.size(); i++) {
					l = redPawns.get(i).returnLocation();
					if ( temp == l && redPawns.get(i).getVisible()) {	//Same color on same position
						makeRedTower(i, pawnToMove);
					}
				}
				break;
			case 4:	//Blue
				for(int i = 0; i < bluePawns.size(); i++) {
					l = bluePawns.get(i).returnLocation();
					if ( temp == l && bluePawns.get(i).getVisible()) {	//Same color on same position
						makeBlueTower(i, pawnToMove);
					}
				}
				break;
			default:
				break;
			}	
		}
		/**
		 * Tries to add a pawn to goal. If the pawn is in the goal location, add it to the goal array and
		 * remove it from the array that is used to draw them on the board and in gamelogic.
		 */
		public void tryAddToGoal() {
			int n;
			switch (color){
			case 1:	//Green pawn
				n = coordinatesGreen.size() - 1  ;
				if (n == location) {
					int j;
					Pawned temp;
					j = getpawnInGoalLocation(color);
					if (j < 5) {
						temp = greenPawns.get(j);
						greenPawnsInGoal.add(temp);	//Add the pawn that finished to the goalArrayList
						greenPawns.remove(j);	//remove pawn from the field
					}
				}
				break;
			case 2:	//Yellow pawn
				n = coordinatesYellow.size() - 1;
				if (n == location) {
					int j;
					Pawned temp;
					j = getpawnInGoalLocation(color);
					if (j < 5) {
						temp = yellowPawns.get(j);
						yellowPawnsInGoal.add(temp);
						yellowPawns.remove(j);
					}
				}
				break;
			case 3: //Red pawn
				n = coordinatesRed.size() - 1;
				if (n == location) {	//Sjekker om den er i m�l
					int j;
					Pawned temp;
					j = getpawnInGoalLocation(color);
					if (j < 5) {
						temp = redPawns.get(j);
							redPawnsInGoal.add(temp);
						redPawns.remove(j);
					}
				}
				break;
			case 4: //Blue pawn
				n = coordinatesBlue.size() - 1;
				if (n == location) {
					int j;
					Pawned temp;
					j = getpawnInGoalLocation(color);
					if (j < 5) {
						temp = bluePawns.get(j);
						bluePawnsInGoal.add(temp);
						bluePawns.remove(j);
					}
				}
				break;
			}
		}
		/**
		 * Make a green tower out of two pawned that is on the same location.
		 * @param i the pawn already on the location
		 * @param pawnToMove the pawn that is being moved
		 */
		public void makeGreenTower(int i, int pawnToMove) {
			greenPawns.get(i).setNotVisible();
			greenPawns.get(i).setTower();
			greenPawns.get(pawnToMove).setTower();	
		}
		/**
		 * Make a yellow tower out of two pawned that is on the same location.
		 * @param i the pawn already on the location
		 * @param pawnToMove the pawn that is being moved
		 */
		public void makeYellowTower(int i, int pawnToMove) {
			yellowPawns.get(i).setNotVisible();
			yellowPawns.get(i).setTower();
			yellowPawns.get(pawnToMove).setTower();	
		}
		/**
		 * Make a red tower out of two pawned that is on the same location.
		 * @param i the pawn already on the location
		 * @param pawnToMove the pawn that is being moved
		 */
		public void makeRedTower(int i, int pawnToMove) {
			redPawns.get(i).setNotVisible();
			redPawns.get(i).setTower();
			redPawns.get(pawnToMove).setTower();	
		}
		/**
		 * Make a blue tower out of two pawned that is on the same location.
		 * @param i the pawn that already on the location
		 * @param pawnToMove the pawn that is being moved
		 */
		public void makeBlueTower(int i, int pawnToMove) {
			bluePawns.get(i).setNotVisible();
			bluePawns.get(i).setTower();
			bluePawns.get(pawnToMove).setTower();	
		}
		/**
		 * Set a pawn to be a tower
		 */
		public void setTower() {
			isTower = true;
		}
		/**
		 * Return if a pawn is a tower or not.
		 * @return return the tower value
		 */
		public boolean getTower() {
			return isTower;
		}
		/**
		 * Set the pawn to no longer be a tower
		 */
		public void setNotTower() {
			isTower = false;
		}
		/**
		 * Sets the pawn to be not visible so that it wont be drawn on the gameboard.
		 * This is used so that towers will only draw one pawn instead of two. 
		 */
		public void setNotVisible() {
			visible = false;
		}
		/**
		 * Sets the pawn to be visible. This is used when a pawn is 
		 * moving from a tower. 
		 */
		public void setVisible() {
			visible = true;
		}
		/**
		 *  Returns if the pawn should be drawn or not.
		 * @return if the pawn is visible or not. 
		 */
		public boolean getVisible() {
			return visible;
		}
		/**
		 * Sets if the pawn can be moved or not. 
		 * @param diceValue The dicevalue rolled by a player
		 * @return
		 */
		public boolean validMove(int diceValue){
			if(inHome && diceValue == 6) {
				canBeMoved = true;
				return true;
			}
			else if(!inHome) {
				canBeMoved = true;
				return true;
			}
			canBeMoved = false; 
			return false;	
		}
		/**
		 * Returns if the pawn can be moved.
		 * Used so a player can only move pawns that is valid. 
		 * @return if the pawn can be moved or not. 
		 */
		public boolean isValid() {
			return canBeMoved;
		}
		/**
		 * Sets the pawn to be not able to move. 
		 */
		public void setNotValid() {
			canBeMoved = false;
		}
		/**
		 * Method that sets the a pawn to be visible again and no longer a tower if
		 * it is not visible or a tower, if it is on the same location as this pawn
		 * @param pawn the pawn that is being moved
		 */
		public void moveFromTower(int pawn) {
			int l;
			setNotTower();
			switch (color) {
			case 1: //Green
				for (int j=0; j < greenPawns.size(); j++ ) {
					if(!greenPawns.get(j).getVisible()) {
						l = greenPawns.get(j).returnLocation();
						if(location == l) {
							greenPawns.get(j).setVisible();
						}
					}
					if(greenPawns.get(j).getTower()) {
						l = greenPawns.get(j).returnLocation();
						if(location == l) {
							greenPawns.get(j).setNotTower();
						}
 					}
				}
				break;
			case 2: //Yellow
				for (int j=0; j < yellowPawns.size(); j++ ) {
					if(!yellowPawns.get(j).getVisible()) {
						l = yellowPawns.get(j).returnLocation();
						if(location == l) {
							yellowPawns.get(j).setVisible();
						}
					}
					if(yellowPawns.get(j).getTower()) {
						l = yellowPawns.get(j).returnLocation();
						if(location == l) {
							yellowPawns.get(j).setNotTower();
						}
 					}
				}
				break;
			case 3: //Red
				for (int j=0; j < redPawns.size(); j++ ) {
					if(!redPawns.get(j).getVisible()) {
						l = redPawns.get(j).returnLocation();
						if(location == l) {
							redPawns.get(j).setVisible();
						}
					}
					if(redPawns.get(j).getTower()) {
						l = redPawns.get(j).returnLocation();
						if(location == l) {
							redPawns.get(j).setNotTower();
						}
 					}
				}
				break;
			case 4: //Blue
				for (int j=0; j < bluePawns.size(); j++ ) {
					if(!bluePawns.get(j).getVisible()) {
						l = bluePawns.get(j).returnLocation();
						if(location == l) {
							bluePawns.get(j).setVisible();
						}
					}	
					if(bluePawns.get(j).getTower()) {
						l = bluePawns.get(j).returnLocation();
						if(location == l) {
							bluePawns.get(j).setNotTower();
						}
 					}
				}
				break;
			}
		}
		/**
		 * Methods that knocks a pawn back to its homelocation.
		 */
		public void KnockedOut() {
			location = homelocation;
			inHome = true;
		}
		/**
		 * Checks every other color than itself if it is on the same location that this pawn is
		 * moving to. If it is, and not a tower, knock it back to homelocation.
		 * @param p pawn that is being moved
		 * @param tmp the new location of the pawn that is being moved
		 */
		public void knockOutOtherColors(int p, int tmp) {
			int l;
			switch(color) {
			case 1: //Green
				for(int i=0; i < yellowPawns.size(); i++) {
					l = yellowPawns.get(i).returnLocation();
					if(l+39 == tmp && !yellowPawns.get(i).getTower()) {
						yellowPawns.get(i).KnockedOut();
					}
				}
				for(int i=0; i < redPawns.size(); i++) {
					l = redPawns.get(i).returnLocation();
					if(l+13 == tmp && !redPawns.get(i).getTower()) {
						redPawns.get(i).KnockedOut();
					}
					
				}
				for(int i=0; i < bluePawns.size(); i++) {
					l = bluePawns.get(i).returnLocation();
					if(l+26 == tmp && !bluePawns.get(i).getTower()) {
						bluePawns.get(i).KnockedOut();
					}
				}
				break;
			case 2: //Yellow
				for(int i=0; i < greenPawns.size(); i++) {
					l = greenPawns.get(i).returnLocation();
					if(l+13 == tmp && !greenPawns.get(i).getTower()) {
						greenPawns.get(i).KnockedOut();
					}
				}
				for(int i=0; i < redPawns.size(); i++) {
					l = redPawns.get(i).returnLocation();
					if(l+26 == tmp && !redPawns.get(i).getTower()) {
						redPawns.get(i).KnockedOut();
					}
					
				}
				for(int i=0; i < bluePawns.size(); i++) {
					l = bluePawns.get(i).returnLocation();
					if(l+39 == tmp && !bluePawns.get(i).getTower()) {
						bluePawns.get(i).KnockedOut();
					} 
				}
				break;
			case 3: //Red
				for(int i=0; i < greenPawns.size(); i++) {
					l = greenPawns.get(i).returnLocation();
					if(l+39 == tmp && !greenPawns.get(i).getTower()) {
						greenPawns.get(i).KnockedOut();
					}
				}
				for(int i=0; i < yellowPawns.size(); i++) {
					l = yellowPawns.get(i).returnLocation();
					if(l+26 == tmp && !yellowPawns.get(i).getTower()) {
						yellowPawns.get(i).KnockedOut();
					}
					
				}
				for(int i=0; i < bluePawns.size(); i++) {
					l = bluePawns.get(i).returnLocation();
					if(l+13 == tmp && !bluePawns.get(i).getTower()) {
						bluePawns.get(i).KnockedOut();
					}
				}
				break;
			case 4: //Blue
				for(int i=0; i < greenPawns.size(); i++) {
					l = greenPawns.get(i).returnLocation();
					if(l+26 == tmp && !greenPawns.get(i).getTower()) {
						greenPawns.get(i).KnockedOut();
					}
				}
				for(int i=0; i < yellowPawns.size(); i++) {
					l = yellowPawns.get(i).returnLocation();
					if(l+13 == tmp && !yellowPawns.get(i).getTower()) {
						yellowPawns.get(i).KnockedOut();
					}
					
				}
				for(int i=0; i < redPawns.size(); i++) {
					l = redPawns.get(i).returnLocation();
					if(l+39 == tmp && !redPawns.get(i).getTower()) {
						redPawns.get(i).KnockedOut();
					}
				}
				break;
			}
		}
		/**
		 * Methods that checks if there is a tower of another color within the range of the dicevalue. 
		 * If it is, the method will return by how far.
		 * @param pawn pawn that is being moved
		 * @param diceval the dicevalue
		 * @return return how far a the pawn is from a tower if it is within the range of the dicevalue.
		 */
		public int testForTowers(int pawn, int diceval) {
			int n = 0;
			int l;
			switch(color) {
			case 1:	//Green
				for (int i=1; i <= diceval; i++ ) {
					for (int j=0; j < yellowPawns.size(); j++ ) {
						if(yellowPawns.get(j).getTower()) {
							l = yellowPawns.get(j).returnLocation();
							if(location + i == l+39) {	 
								return i;
							}
						}	
					}
					for(int j=0; j < redPawns.size();j++) {
						if(redPawns.get(j).getTower()) {
							l = redPawns.get(j).returnLocation();
							if(location + i == l+13) {
								return i;
							}
						}
					}
					for(int j=0; j < bluePawns.size();j++) {
						if(bluePawns.get(j).getTower()) {
							l = bluePawns.get(j).returnLocation();
							if(location + i == l+26) {
								return i;
							}
						}
					}
				}
				break;
			case 2: //Yellow
				for (int i=1; i <= diceval; i++ ) {
					for (int j=0; j < greenPawns.size(); j++ ) {
						if(greenPawns.get(j).getTower()) {
							l = greenPawns.get(j).returnLocation();
							if(location + i == l+13) {
								return i;
							}
						}	
					}
					for(int j=0; j < redPawns.size();j++) {
						if(redPawns.get(j).getTower()) {
							l = redPawns.get(j).returnLocation();
							if(location + i == l+26) {
								return i;
							}
						}
					}
					for(int j=0; j < bluePawns.size();j++) {
						if(bluePawns.get(j).getTower()) {
							l = bluePawns.get(j).returnLocation();
							if(location + i == l+39) {
								return i;
							}
						}
					}
				}
				break;
			case 3: //Red
				for (int i=1; i <= diceval; i++ ) {
					for (int j=0; j < bluePawns.size(); j++ ) {
						if(bluePawns.get(j).getTower()) {
							l = bluePawns.get(j).returnLocation();
							if(location + i == l+13) {
								return i;
							}
						}	
					}
					for(int j=0; j < yellowPawns.size();j++) {
						if(yellowPawns.get(j).getTower()) {
							l = yellowPawns.get(j).returnLocation();
							if(location + i == l+26) {
								return i;
							}
						}
					}
					for(int j=0; j < greenPawns.size();j++) {
						if(greenPawns.get(j).getTower()) {
							l = greenPawns.get(j).returnLocation();
							if(location + i == l+39) {
								return i;
							}
						}
					}
				}
				break;
			case 4: //Blue
				for (int i=1; i <= diceval; i++ ) {
					for (int j=0; j < yellowPawns.size(); j++ ) {
						if(yellowPawns.get(j).getTower()) {
							l = yellowPawns.get(j).returnLocation();
							if(location + i == l+13) {
								return i;
							}
						}	
					}
					for(int j=0; j < greenPawns.size();j++) {
						if(greenPawns.get(j).getTower()) {
							l = greenPawns.get(j).returnLocation();
							if(location + i == l+26) {
								return i;
							}
						}
					}
					for(int j=0; j < redPawns.size();j++) {
						if(redPawns.get(j).getTower()) {
							l = redPawns.get(j).returnLocation();
							if(location + i == l+39) {
								return i;
							}
						}
					}
				}
				break;
			}
			
			return n;
		}
		/**
		 * Method that is called when a pawn is trying to move on or through a tower. 
		 * It will then bounce away from the tower, and the length of the bounce
		 * is based on the dicevalue.
		 * @param t How far the pawn is from a tower of another color.
		 * @param diceVal The dicevalue
		 */
		public void bounceFromTower(int t, int diceVal) {
			int loc = location;
			loc += t; 
			loc -= diceVal - t;
			location = loc - 1;
		}
		
	} //END OF PAWN CLASS
}//END LUDOBOARDFX CLASS
