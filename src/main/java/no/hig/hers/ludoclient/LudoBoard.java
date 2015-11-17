package no.hig.hers.ludoclient;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * Class that makes the game board
 */
public class LudoBoard extends JPanel {
	private Color color[] = {Color.BLACK, Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.WHITE, Color.ORANGE, Color.GRAY};

	private BufferedImage testDraw;
	private Image testImage;
	private JLayeredPane boardPane;
	private JLabel board;
	private JLabel die;
	
	Dimension boardSize;
	private int currentPlayer = 1;
	private int diceValue;
	
	private final static int ROWS = 15;
	private final static int COLUMNS = 15;
	private final static int PICTUREWIDTH = 41;	// 620/15 pixels per grid
	private final static int PICTUREHEIGHT = 41;	// 625/15 pixels per grid
	private final static int OFFSET = 20;
	private final static Point[][] GRID = new Point[ROWS][COLUMNS];
	
	private Vector<Point> coordinatesGreen= new Vector<>();
	private Vector<Point> coordinatesRed = new Vector<>();
	private Vector<Point> coordinatesYellow= new Vector<>();
	private Vector<Point> coordinatesBlue = new Vector<>();
	
	//Arraylist that keeps track of where the pawns of each color is on the board
	final ArrayList<Pawned> greenPawns = new ArrayList<Pawned>();
	final ArrayList<Pawned> yellowPawns = new ArrayList<Pawned>();
	final ArrayList<Pawned> redPawns = new ArrayList<Pawned>();
	final ArrayList<Pawned> bluePawns = new ArrayList<Pawned>();
	
	//Arralist that keeps track of how many pawns of each color is in the goal
	//If all pawns of a color is in their respectively arrayslists, that player wins.
	final ArrayList<Pawned> greenPawnsInGoal = new ArrayList<Pawned>();
	final ArrayList<Pawned> yellowPawnsInGoal = new ArrayList<Pawned>();
	final ArrayList<Pawned> redPawnsInGoal = new ArrayList<Pawned>();
	final ArrayList<Pawned> bluePawnsInGoal = new ArrayList<Pawned>();
	
	/**
	 * Constructor for the ludo board
	 * Makes all the columns and rows and paints them.
	 */
	public LudoBoard() {
	
		try {
				/*
				 * Kan bruke Icon isteden for 
				 * testDraw = ImageIO.read(new File("bluePawn1.png"));
				 * setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
				 * ImageIcon boardBackground = createImageIcon("ludoBoard.png");
				 * ImageIcon greenPawnIcon = new ImageIcon(getClass().getResource("greenPawn1.png"));
				 * ImageIcon yellowPawnIcon = new ImageIcon(getClass().getResource("yellowPawn1.png"));
				 * ImageIcon redPawnIcon = new ImageIcon(getClass().getResource("redPawn1.png"));
				 * ImageIcon bluePawnIcon = new ImageIcon(getClass().getResource("bluePawn1.png"));
				 * testImage = new ImageIcon(getClass().getResource("greenPawn1.png")).getImage();
				 */	
				setUpGUI();
				 
				makeGreenCoordinates();
				makeRedCoordinates();
				makeYellowCoordinates();
				makeBlueCoordinates();
				
				try {
					addPawns(greenPawns, 1);
					addPawns(yellowPawns, 2);
					addPawns(redPawns, 3);
					addPawns(bluePawns, 4);
				} catch (Exception e) {
					System.out.println("Something went wrong, when making the pawns");
				}
			
			} catch (Exception e) {
				System.out.println("Error");
			}
	}//end LudoBoard constructor
	
	//Coordinates for all the pawns
	
	private void makeGreenCoordinates() {
		
		//Green home:
		coordinatesGreen.add(new Point(197,117));	//location 0
		coordinatesGreen.add(new Point(135, 175));
		coordinatesGreen.add(new Point(260, 175));
		coordinatesGreen.add(new Point(197, 230));
		
		//Green Track
		coordinatesGreen.add(new Point(102, 372));
		coordinatesGreen.add(new Point(167, 372));
		coordinatesGreen.add(new Point(227, 372));
		coordinatesGreen.add(new Point(292, 372));
		coordinatesGreen.add(new Point(355, 372));
		
		coordinatesGreen.add(new Point(415, 317));
		coordinatesGreen.add(new Point(415, 257));
		coordinatesGreen.add(new Point(415, 205));
		coordinatesGreen.add(new Point(415, 147));
		coordinatesGreen.add(new Point(415, 92));
		
		coordinatesGreen.add(new Point(415, 37));	
		coordinatesGreen.add(new Point(482, 37));
		coordinatesGreen.add(new Point(545, 37));
		
		coordinatesGreen.add(new Point(545, 92));
		coordinatesGreen.add(new Point(545, 147));
		coordinatesGreen.add(new Point(545, 205));
		coordinatesGreen.add(new Point(545, 257));
		coordinatesGreen.add(new Point(545, 317));
		
		coordinatesGreen.add(new Point(605, 372));
		coordinatesGreen.add(new Point(665, 372));
		coordinatesGreen.add(new Point(725, 372));
		coordinatesGreen.add(new Point(795, 372));
		coordinatesGreen.add(new Point(855, 372));
		
		coordinatesGreen.add(new Point(915, 372));
		coordinatesGreen.add(new Point(915, 432));
		coordinatesGreen.add(new Point(915, 487));
		
		coordinatesGreen.add(new Point(855, 487));
		coordinatesGreen.add(new Point(795, 487));
		coordinatesGreen.add(new Point(725, 487));
		coordinatesGreen.add(new Point(665, 487));
		coordinatesGreen.add(new Point(605, 487));
		
		coordinatesGreen.add(new Point(545, 542));
		coordinatesGreen.add(new Point(545, 602));
		coordinatesGreen.add(new Point(545, 657));
		coordinatesGreen.add(new Point(545, 712));
		coordinatesGreen.add(new Point(545, 767));
		
		coordinatesGreen.add(new Point(545, 825));
		coordinatesGreen.add(new Point(482, 825));
		coordinatesGreen.add(new Point(415, 825));
		
		coordinatesGreen.add(new Point(415, 767));
		coordinatesGreen.add(new Point(415, 712));
		coordinatesGreen.add(new Point(415, 657));
		coordinatesGreen.add(new Point(415, 602));
		coordinatesGreen.add(new Point(415, 542));
		
		coordinatesGreen.add(new Point(355, 487));
		coordinatesGreen.add(new Point(292, 487));
		coordinatesGreen.add(new Point(227, 487));
		coordinatesGreen.add(new Point(167, 487));
		coordinatesGreen.add(new Point(102, 487));
		
		coordinatesGreen.add(new Point(42, 487));
		coordinatesGreen.add(new Point(42, 432));
		coordinatesGreen.add(new Point(42, 372));
		
		//Green Goal highway
		coordinatesGreen.add(new Point(102, 372));
		coordinatesGreen.add(new Point(102, 432));
		coordinatesGreen.add(new Point(167,432));
		coordinatesGreen.add(new Point(227,432));
		coordinatesGreen.add(new Point(292,432));
		coordinatesGreen.add(new Point(355,432));
		
		//Green Goal
		coordinatesGreen.add(new Point(415,432));	//Location 62	
	}
	private void makeRedCoordinates() {
		//Red home coordinates
		coordinatesRed.add(new Point(762, 117));
		coordinatesRed.add(new Point(702, 175));
		coordinatesRed.add(new Point(827, 175));
		coordinatesRed.add(new Point(762, 230));
		
		//Red Track
		coordinatesRed.add(new Point(545, 92));
		coordinatesRed.add(new Point(545, 147));
		coordinatesRed.add(new Point(545, 205));
		coordinatesRed.add(new Point(545, 257));
		coordinatesRed.add(new Point(545, 317));
		
		coordinatesRed.add(new Point(605, 372));
		coordinatesRed.add(new Point(665, 372));
		coordinatesRed.add(new Point(725, 372));
		coordinatesRed.add(new Point(795, 372));
		coordinatesRed.add(new Point(855, 372));
		
		coordinatesRed.add(new Point(915, 372));
		coordinatesRed.add(new Point(915, 432));
		coordinatesRed.add(new Point(915, 487));
		
		coordinatesRed.add(new Point(855, 487));
		coordinatesRed.add(new Point(795, 487));
		coordinatesRed.add(new Point(725, 487));
		coordinatesRed.add(new Point(665, 487));
		coordinatesRed.add(new Point(605, 487));
		
		coordinatesRed.add(new Point(545, 542));
		coordinatesRed.add(new Point(545, 602));
		coordinatesRed.add(new Point(545, 657));
		coordinatesRed.add(new Point(545, 712));
		coordinatesRed.add(new Point(545, 767));
		
		coordinatesRed.add(new Point(545, 825));
		coordinatesRed.add(new Point(482, 825));
		coordinatesRed.add(new Point(415, 825));
		
		coordinatesRed.add(new Point(415, 767));
		coordinatesRed.add(new Point(415, 712));
		coordinatesRed.add(new Point(415, 657));
		coordinatesRed.add(new Point(415, 602));
		coordinatesRed.add(new Point(415, 542));
		
		coordinatesRed.add(new Point(355, 487));
		coordinatesRed.add(new Point(292, 487));
		coordinatesRed.add(new Point(227, 487));
		coordinatesRed.add(new Point(167, 487));
		coordinatesRed.add(new Point(102, 487));
		
		coordinatesRed.add(new Point(42, 487));
		coordinatesRed.add(new Point(42, 432));
		coordinatesRed.add(new Point(42, 372));
		
		coordinatesRed.add(new Point(102, 372));
		coordinatesRed.add(new Point(167, 372));
		coordinatesRed.add(new Point(227, 372));
		coordinatesRed.add(new Point(292, 372));
		coordinatesRed.add(new Point(355, 372));
		
		coordinatesRed.add(new Point(415, 317));
		coordinatesRed.add(new Point(415, 257));
		coordinatesRed.add(new Point(415, 205));
		coordinatesRed.add(new Point(415, 147));
		coordinatesRed.add(new Point(415, 92));
		
		coordinatesRed.add(new Point(415, 37));	
		coordinatesRed.add(new Point(482, 37));
		coordinatesRed.add(new Point(545, 37));
		
		//Red Goalhighway coordinates
		coordinatesRed.add(new Point(545, 92));
		coordinatesRed.add(new Point(482, 92));
		coordinatesRed.add(new Point(482, 147));
		coordinatesRed.add(new Point(482, 205));
		coordinatesRed.add(new Point(482, 257));
		coordinatesRed.add(new Point(482, 317));
		
		//Red Goal coordinate
		coordinatesRed.add(new Point(482, 372));
		
	}
	private void makeYellowCoordinates() {
		//Yellow home coordinates
		coordinatesYellow.add(new Point(197, 627));
		coordinatesYellow.add(new Point(135, 685));
		coordinatesYellow.add(new Point(260, 685));
		coordinatesYellow.add(new Point(197, 740));
		
		//Yellow Track
		coordinatesYellow.add(new Point(415, 767));
		coordinatesYellow.add(new Point(415, 712));
		coordinatesYellow.add(new Point(415, 657));
		coordinatesYellow.add(new Point(415, 602));
		coordinatesYellow.add(new Point(415, 542));
		
		coordinatesYellow.add(new Point(355, 487));
		coordinatesYellow.add(new Point(292, 487));
		coordinatesYellow.add(new Point(227, 487));
		coordinatesYellow.add(new Point(167, 487));
		coordinatesYellow.add(new Point(102, 487));
		
		coordinatesYellow.add(new Point(42, 487));
		coordinatesYellow.add(new Point(42, 432));
		coordinatesYellow.add(new Point(42, 372));
		
		coordinatesYellow.add(new Point(102, 372));
		coordinatesYellow.add(new Point(167, 372));
		coordinatesYellow.add(new Point(227, 372));
		coordinatesYellow.add(new Point(292, 372));
		coordinatesYellow.add(new Point(355, 372));
		
		coordinatesYellow.add(new Point(415, 317));
		coordinatesYellow.add(new Point(415, 257));
		coordinatesYellow.add(new Point(415, 205));
		coordinatesYellow.add(new Point(415, 147));
		coordinatesYellow.add(new Point(415, 92));
		
		coordinatesYellow.add(new Point(415, 37));	
		coordinatesYellow.add(new Point(482, 37));
		coordinatesYellow.add(new Point(545, 37));
		
		coordinatesYellow.add(new Point(545, 92));
		coordinatesYellow.add(new Point(545, 147));
		coordinatesYellow.add(new Point(545, 205));
		coordinatesYellow.add(new Point(545, 257));
		coordinatesYellow.add(new Point(545, 317));
		
		coordinatesYellow.add(new Point(605, 372));
		coordinatesYellow.add(new Point(665, 372));
		coordinatesYellow.add(new Point(725, 372));
		coordinatesYellow.add(new Point(795, 372));
		coordinatesYellow.add(new Point(855, 372));
		
		coordinatesYellow.add(new Point(915, 372));
		coordinatesYellow.add(new Point(915, 432));
		coordinatesYellow.add(new Point(915, 487));
		
		coordinatesYellow.add(new Point(855, 487));
		coordinatesYellow.add(new Point(795, 487));
		coordinatesYellow.add(new Point(725, 487));
		coordinatesYellow.add(new Point(665, 487));
		coordinatesYellow.add(new Point(605, 487));
		
		coordinatesYellow.add(new Point(545, 542));
		coordinatesYellow.add(new Point(545, 602));
		coordinatesYellow.add(new Point(545, 657));
		coordinatesYellow.add(new Point(545, 712));
		coordinatesYellow.add(new Point(545, 767));
		
		coordinatesYellow.add(new Point(545, 825));
		coordinatesYellow.add(new Point(482, 825));
		coordinatesYellow.add(new Point(415, 825));
		
		//Yellow GoalHighway
		coordinatesYellow.add(new Point(415, 767));
		coordinatesYellow.add(new Point(482, 767));
		coordinatesYellow.add(new Point(482, 712));
		coordinatesYellow.add(new Point(482, 657));
		coordinatesYellow.add(new Point(482, 602));
		coordinatesYellow.add(new Point(482, 542));
		
		//Yellow Goal
		coordinatesYellow.add(new Point(482, 487));
	
	}
	private void makeBlueCoordinates() {
		//Blue Home
		coordinatesBlue.add(new Point(762, 627));
		coordinatesBlue.add(new Point(702, 685));
		coordinatesBlue.add(new Point(827, 685));
		coordinatesBlue.add(new Point(762, 740));
		
		//Blue Track
		coordinatesBlue.add(new Point(855, 487));
		coordinatesBlue.add(new Point(795, 487));
		coordinatesBlue.add(new Point(725, 487));
		coordinatesBlue.add(new Point(665, 487));
		coordinatesBlue.add(new Point(605, 487));
		
		coordinatesBlue.add(new Point(545, 542));
		coordinatesBlue.add(new Point(545, 602));
		coordinatesBlue.add(new Point(545, 657));
		coordinatesBlue.add(new Point(545, 712));
		coordinatesBlue.add(new Point(545, 767));
		
		coordinatesBlue.add(new Point(545, 825));
		coordinatesBlue.add(new Point(482, 825));
		coordinatesBlue.add(new Point(415, 825));
		
		coordinatesBlue.add(new Point(415, 767));
		coordinatesBlue.add(new Point(415, 712));
		coordinatesBlue.add(new Point(415, 657));
		coordinatesBlue.add(new Point(415, 602));
		coordinatesBlue.add(new Point(415, 542));
		
		coordinatesBlue.add(new Point(355, 487));
		coordinatesBlue.add(new Point(292, 487));
		coordinatesBlue.add(new Point(227, 487));
		coordinatesBlue.add(new Point(167, 487));
		coordinatesBlue.add(new Point(102, 487));
		
		coordinatesBlue.add(new Point(42, 487));
		coordinatesBlue.add(new Point(42, 432));
		coordinatesBlue.add(new Point(42, 372));
		
		coordinatesBlue.add(new Point(102, 372));
		coordinatesBlue.add(new Point(167, 372));
		coordinatesBlue.add(new Point(227, 372));
		coordinatesBlue.add(new Point(292, 372));
		coordinatesBlue.add(new Point(355, 372));
		
		coordinatesBlue.add(new Point(415, 317));
		coordinatesBlue.add(new Point(415, 257));
		coordinatesBlue.add(new Point(415, 205));
		coordinatesBlue.add(new Point(415, 147));
		coordinatesBlue.add(new Point(415, 92));
		
		coordinatesBlue.add(new Point(415, 37));	
		coordinatesBlue.add(new Point(482, 37));
		coordinatesBlue.add(new Point(545, 37));
		
		coordinatesBlue.add(new Point(545, 92));
		coordinatesBlue.add(new Point(545, 147));
		coordinatesBlue.add(new Point(545, 205));
		coordinatesBlue.add(new Point(545, 257));
		coordinatesBlue.add(new Point(545, 317));
		
		coordinatesBlue.add(new Point(605, 372));
		coordinatesBlue.add(new Point(665, 372));
		coordinatesBlue.add(new Point(725, 372));
		coordinatesBlue.add(new Point(795, 372));
		coordinatesBlue.add(new Point(855, 372));
		
		coordinatesBlue.add(new Point(915, 372));
		coordinatesBlue.add(new Point(915, 432));
		coordinatesBlue.add(new Point(915, 487));
		
		//Blue GoalHighway
		coordinatesBlue.add(new Point(855, 487));
		coordinatesBlue.add(new Point(855, 432));
		coordinatesBlue.add(new Point(795, 432));
		coordinatesBlue.add(new Point(725, 432));
		coordinatesBlue.add(new Point(665, 432));
		coordinatesBlue.add(new Point(605, 432));
		
		//Blue Goal
		coordinatesBlue.add(new Point(545, 432));
		
	}
	
	public void addPawns(final ArrayList<Pawned> pawns, int color) {
		for(int i = 0; i < 4; i++) {
				Pawned newPawn = new Pawned(i, color);
				pawns.add(newPawn);	
		}
	}
	//public Object getValueAt(int row, int col) {
	public Point getGreenCoordinates(int i) {
		return coordinatesGreen.elementAt(i);
	}
	
	public Point getYellowCoordinates(int i) {
		return coordinatesYellow.elementAt(i);
	}
	
	public Point getRedCoordinates(int i) {
		return coordinatesRed.elementAt(i);
	}
	
	public Point getBlueCoordinates(int i) {
		return coordinatesBlue.elementAt(i);
	}
	
	public void setUpGUI() {
		
		ImageIcon boardBackground = new ImageIcon(getClass().getResource("ludo_Board.png"));
		ImageIcon gameBoard = resizeImageIcon(boardBackground, 1000, 900);
		ImageIcon die = new ImageIcon(getClass().getResource("dice6.png"));
		
		boardPane = new JLayeredPane();
		boardPane.setPreferredSize(new Dimension(1010, 950));
		boardPane.setOpaque(true);
		boardPane.setBackground(color[5]);
		board = new JLabel();	//Board Label
		board.setIcon(gameBoard);	//Sets the boardBackground
		boardPane.add(board, new Integer(0));
		boardSize = board.getPreferredSize();	
		board.setBounds(0, 0, boardSize.width, boardSize.height); //Gives the image some extra space
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(boardPane);
	}
	
	public static ImageIcon resizeImageIcon(ImageIcon icon, Integer width, Integer height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics g2d = bufferedImage.createGraphics();
		g2d.drawImage(icon.getImage(), 0, 0, width, height, null);
		g2d.dispose();
		
		return new ImageIcon(bufferedImage, icon.getDescription());
	}
	
	public void paint(Graphics g){
		
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		paintPawns(g2d);
		
		/*
		 * TEST
		 * DRAWS A PAWN ON EVERY POSSIBLE LOCATION
		 * TODO REMOVE THIS
		Graphics2D g2 = (Graphics2D) g;
		
		for ( int i = 0; i < coordinatesBlue.size(); i++) {
		
		float thickness = 4;
		g2.setStroke(new BasicStroke(thickness));
		
		g2.setColor(color[5]);
		
		g2.drawOval(coordinatesGreen.elementAt(i).x, coordinatesGreen.elementAt(i).y, 40, 40);
		g2.setColor(color[2]);
		g2.fillOval(coordinatesGreen.elementAt(i).x, coordinatesGreen.elementAt(i).y, 40, 40);
		
		
		g2.setColor(color[5]);
		g2.drawOval(coordinatesRed.elementAt(i).x, coordinatesRed.elementAt(i).y, 40, 40);
		g2.setColor(color[1]);
		g2.fillOval(coordinatesRed.elementAt(i).x, coordinatesRed.elementAt(i).y, 40, 40);
		
		
		g2.setColor(color[5]);
		g2.drawOval(coordinatesYellow.elementAt(i).x, coordinatesYellow.elementAt(i).y, 40, 40);
		g2.setColor(color[4]);
		g2.fillOval(coordinatesYellow.elementAt(i).x, coordinatesYellow.elementAt(i).y, 40, 40);
		
		
		g2.setColor(color[5]);
		g2.drawOval(coordinatesBlue.elementAt(i).x, coordinatesBlue.elementAt(i).y, 40, 40);
		g2.setColor(color[3]);
		g2.fillOval(coordinatesBlue.elementAt(i).x, coordinatesBlue.elementAt(i).y, 40, 40);
		
		}
		*/
	}
	
	void paintPawns(Graphics2D g2d) {
			try {
				float thickness = 4;
				g2d.setStroke(new BasicStroke(thickness));
				
				for (int j = 0; j < greenPawns.size() ; j++) {
					if (greenPawns.get(j).getVisible()) {
						int l = greenPawns.get(j).returnLocation();
						g2d.setColor(color[0]);
						g2d.drawOval(coordinatesGreen.elementAt(l).x, coordinatesGreen.elementAt(l).y, 40, 40);
						g2d.setColor(color[1]);
						g2d.fillOval(coordinatesGreen.elementAt(l).x, coordinatesGreen.elementAt(l).y, 40, 40);
						Font f = new Font("Dialog", Font.PLAIN, 20);
						g2d.setFont(f);
						g2d.setColor(color[0]);
						if (greenPawns.get(j).getTower()) {
							g2d.drawString("T", coordinatesGreen.elementAt(l).x+8, coordinatesGreen.elementAt(l).y+25);
							g2d.drawString("" + (j+1), coordinatesGreen.elementAt(l).x+20, coordinatesGreen.elementAt(l).y+25);
						} else
						g2d.drawString("" + (j+1), coordinatesGreen.elementAt(l).x+15, coordinatesGreen.elementAt(l).y+25);
					}
				}
				for (int j = 0; j < yellowPawns.size() ; j++) {	
					if (yellowPawns.get(j).getVisible()) {
						int l = yellowPawns.get(j).returnLocation();
						g2d.setColor(color[0]);
						g2d.drawOval(coordinatesYellow.elementAt(l).x, coordinatesYellow.elementAt(l).y, 40, 40);
						g2d.setColor(color[2]);
						g2d.fillOval(coordinatesYellow.elementAt(l).x, coordinatesYellow.elementAt(l).y, 40, 40);
						Font f = new Font("Dialog", Font.PLAIN, 20);
						g2d.setFont(f);
						g2d.setColor(color[0]);
						if (yellowPawns.get(j).getTower()) {
							g2d.drawString("T", coordinatesYellow.elementAt(l).x+8, coordinatesYellow.elementAt(l).y+25);
							g2d.drawString("" + (j+1), coordinatesYellow.elementAt(l).x+20, coordinatesYellow.elementAt(l).y+25);
						} else
						g2d.drawString("" + (j+1), coordinatesYellow.elementAt(l).x+15, coordinatesYellow.elementAt(l).y+25);
					}	
				}
				for (int j = 0; j < redPawns.size() ; j++) {
					if (redPawns.get(j).getVisible()) {
						int l = redPawns.get(j).returnLocation();
						g2d.setColor(color[0]);
						g2d.drawOval(coordinatesRed.elementAt(l).x, coordinatesRed.elementAt(l).y, 40, 40);
						g2d.setColor(color[3]);
						g2d.fillOval(coordinatesRed.elementAt(l).x, coordinatesRed.elementAt(l).y, 40, 40);
						Font f = new Font("Dialog", Font.PLAIN, 20);
						g2d.setFont(f);
						g2d.setColor(color[0]);
						if (redPawns.get(j).getTower()) {
							g2d.drawString("T", coordinatesRed.elementAt(l).x+8, coordinatesRed.elementAt(l).y+25);
							g2d.drawString("" + (j+1), coordinatesRed.elementAt(l).x+20, coordinatesRed.elementAt(l).y+25);
						} else
						g2d.drawString("" + (j+1), coordinatesRed.elementAt(l).x+15, coordinatesRed.elementAt(l).y+25);
					}
				}
				for (int j = 0; j < bluePawns.size() ; j++) {	
					if (bluePawns.get(j).getVisible()) {
						int l = bluePawns.get(j).returnLocation();
						g2d.setColor(color[0]);
						g2d.drawOval(coordinatesBlue.elementAt(l).x, coordinatesBlue.elementAt(l).y, 40, 40);
						g2d.setColor(color[4]);
						g2d.fillOval(coordinatesBlue.elementAt(l).x, coordinatesBlue.elementAt(l).y, 40, 40);
						Font f = new Font("Dialog", Font.PLAIN, 20);
						g2d.setFont(f);
						g2d.setColor(color[0]);
						if (bluePawns.get(j).getTower()) {
							g2d.drawString("T", coordinatesBlue.elementAt(l).x+8, coordinatesBlue.elementAt(l).y+25);
							g2d.drawString("" + (j+1), coordinatesBlue.elementAt(l).x+20, coordinatesBlue.elementAt(l).y+25);
						} else
						g2d.drawString("" + (j+1), coordinatesBlue.elementAt(l).x+15, coordinatesBlue.elementAt(l).y+25);
					}
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Error try to paint pawn");
			}	
	}
	
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
	 * 
	 * @author Hauken
	 *Class that holds the information about every pawn
	 *
	 */
	public class Pawned {
		private int location; //location 0-3 is homelocation.
		private int homelocation;	//Used so that each pawn knows where it should be places if knocked out
		private int color;		//Not really needed(?)
		//private Vector<Point> coordinates= new Vector<>();
		private Color paintColor[] = {Color.BLACK, Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE};
		private boolean inHome;
		private boolean isTower;
		private int pointWorth;
		private boolean visible;
		private boolean passable;
		
		/**
		 * Class constructor that makes every pawn and set their starting location
		 * @param loc holds the start location of the current pawn
		 * @param col holds the same nuber as location, but this is to verify the color
		 */
		public Pawned(int loc, int col){
			homelocation = loc;
			location = loc; 
			color = col;
			inHome = true;
			isTower = false;
			visible = true;
			pointWorth = 1;
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
		 * Method that changes a pawns location
		 * @param n holds the location that the pawns should change with. The diceValue.
		 */
		public void changeLocation(int n, int turnOwner, int pawnToMove) {
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
						switch(color) {
						case 1:	//Green
							for(int i = 0; i < greenPawns.size(); i++) {
								l = greenPawns.get(i).returnLocation();
								if ( temp == l && greenPawns.get(i).getVisible()) {	//Lik farge står på samme felt
									makeGreenTower(i, pawnToMove);
								}		
							}
							break;
						case 2:	//Yellow
							for(int i = 0; i < yellowPawns.size(); i++) {
								l = yellowPawns.get(i).returnLocation();
								if ( temp == l && yellowPawns.get(i).getVisible()) {	//Lik farge står på samme felt
									makeYellowTower(i, pawnToMove);
								}
							}
							break;
						case 3:	//Red
							for(int i = 0; i < redPawns.size(); i++) {
								l = redPawns.get(i).returnLocation();
								if ( temp == l && redPawns.get(i).getVisible()) {	//Lik farge står på samme felt
									makeRedTower(i, pawnToMove);
								}
							}
							break;
						case 4:	//Blue
							for(int i = 0; i < bluePawns.size(); i++) {
								l = bluePawns.get(i).returnLocation();
								if ( temp == l && bluePawns.get(i).getVisible()) {	//Lik farge står på samme felt
									makeBlueTower(i, pawnToMove);
								}
							}
							break;
						}
						knockOutOtherColors(pawnToMove, temp);
						location += n;;
						tryAddToGoal();
					} else bounceFromTower(t, n);
				}
				else {
					if(n == 6) {
						location = 4;
						inHome = false;
					} else System.out.println("Need to get a 6 to move the pawn from the homefield");
				}
			}
		}
		
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
				if (n == location) {	//Sjekker om den er i mål
					int j;
					Pawned temp;
					j = getpawnInGoalLocation(color);
					if (j < 5) {
						temp = redPawns.get(j);
						for(int i = 1; i <= redPawns.get(j).returnPointWorth(); i++ ) {
							redPawnsInGoal.add(new Pawned(0, 0));
						}
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
		
		public void makeGreenTower(int i, int pawnToMove) {
			greenPawns.get(i).setNotVisible();
			greenPawns.get(i).setTower();
			greenPawns.get(pawnToMove).setTower();	
		}
		
		public void makeYellowTower(int i, int pawnToMove) {
			yellowPawns.get(i).setNotVisible();
			yellowPawns.get(i).setTower();
			yellowPawns.get(pawnToMove).setTower();	
		}
		
		public void makeRedTower(int i, int pawnToMove) {
			redPawns.get(i).setNotVisible();
			redPawns.get(i).setTower();
			redPawns.get(pawnToMove).setTower();	
		}
		
		public void makeBlueTower(int i, int pawnToMove) {
			bluePawns.get(i).setNotVisible();
			bluePawns.get(i).setTower();
			bluePawns.get(pawnToMove).setTower();	
		}
		
		public void setTower() {
			isTower = true;
			//pointWorth++; Not used anymore. Cant move towers
		}
		
		public boolean getTower() {
			return isTower;
		}
		public void setNotTower() {
			isTower = false;
		}
		
		public int returnPointWorth() {
			return pointWorth;
		}
		
		public void setNotVisible() {
			visible = false;
		}
		public void setVisible() {
			visible = true;
		}
		
		public boolean getVisible() {
			return visible;
		}
		public boolean validMove(int diceValue){
			if(inHome && diceValue == 6) {
				return true;
			}
			else if(!inHome) {
				return true;
			}
			 
			return false;	
		}
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
		public void KnockedOut() {
			location = homelocation;
			inHome = true;
		}
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
					if(l+13 == tmp &&!redPawns.get(i).getTower()) {
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
		
		public void bounceFromTower(int t, int diceVal) {
			int loc = location;
			loc += t; 
			loc -= diceVal - t;
			location = loc - 1;
		}
		
	} //END OF PAWN CLASS
}//END OF LUDOBOARD CLASS
	