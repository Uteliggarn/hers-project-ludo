package globalServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {

	private static final String URL = "jdbc:mysql://mysql.stud.hig.no/s130443";
	private static final String USERNAME = "s130443";
	private static final String PASSWORD = "IR5ouFpy";
	
	private static final String CONNECTION_FAILED = "Failed to connect to database";
	
	public static final String MATCHESWON = "matcheswon";
	public static final String MATCHESPLAYED = "matchesplayed";

	private static Connection connection = null;
	
	public DatabaseHandler() {
	}
	/**
	 * Method for connecting to an external database.
	 */
	private static void connectToDatabase() throws SQLException{
		connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}
	
	private void readAllUsers() {
		String testquery = "SELECT * FROM users";
		Statement statement;
		
		try {
			connectToDatabase();
			statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(testquery);
			ResultSetMetaData metaData = resultSet.getMetaData();
			int noColumns = metaData.getColumnCount();
			
			for (int i = 1; i <= noColumns; i++) {
				System.out.printf("%-8s\t", metaData.getColumnName(i));
			} System.out.println();
			
			while (resultSet.next()) {
				for (int i = 1; i <= noColumns; i++) {
					System.out.printf("%-8s\t", resultSet.getObject(i));
				} System.out.println();
			}
			
			connection.close();
		} catch (SQLException e) {
			System.out.println(CONNECTION_FAILED);
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for retrieving the top ten players, either by matches played, or matches won.
	 * @param matchestype The type of top ten, i.e 'matcheswon' or 'matchesplayed'. 
	 * 	      This can be taken from the finals MATCHESWON and MATCHESPLAYED.
	 * @return Returns the ResultSet containing the top ten.
	 */
	public static ResultSet retrieveTopTen(String matchestype) {
		String query = "SELECT username, " + matchestype + " FROM users ORDER BY " + matchestype + " DESC LIMIT 10";
		Statement stmt;
		
		try {
			connectToDatabase();
			stmt = connection.createStatement();
			
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Method for retrieving the number of played or won matches for the player.
	 * @param userID The ID of the player
	 * @param matchtype The type of match. i.e. 'matcheswon' or 'matchesplated'.
	 * 	      This can be taken from the final MATCHESWON or MATCHESPLAYED
	 * @return Returns the number of matches for the player. If no player was found, returns 0.
	 */
	public static int retrievePlayersMatches(int userID, String matchtype) {
		String query = "SELECT " + matchtype + " FROM users WHERE _ID=\'" + userID + "\'";
		Statement stmt;
		
		try {
			connectToDatabase();
			stmt = connection.createStatement();
			
			ResultSet resultSet = stmt.executeQuery(query);
			
			if (resultSet.next()) {
				return (int) resultSet.getObject(1);
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * Method for updating the number of played matches and won matches for a player.
	 * This will increment the matchesplayed for the player, and if he won, will also increment the matcheswon.
	 * @param userID The ID of the user to update
	 * @param won true if the player won, else false.
	 */
	public static void updatePlayersMatches(int userID, boolean won) {
		String query;
		if (won) query = "UPDATE users SET matchesplayed = matchesplayed + 1, matcheswon = matcheswon + 1 WHERE _ID=\'" + userID + "\'";
		else query = "UPDATE users SET matchesplayed = matchesplayed + 1 WHERE _ID=\'" + userID + "\'";
		
		Statement stmt;
		
		try {
			connectToDatabase();
			stmt = connection.createStatement();
			stmt.execute(query);
			
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for registering a new user with a unique username and a password.
	 * In practice, this is just adding the user to the database.
	 * @param username The username for the user. Must be unique
	 * @param password The password for the user.
	 * @return Returns true if the user was registered, else false if already exists.
	 */
	public static boolean registerNewUser(String username, String password) {
		String query = " insert into users (username, password) values (?, ?)";
		
		try {	
			connectToDatabase();
			PreparedStatement stmt = connection.prepareStatement(query);
			if (!checkIfUserAlreadyExists(username)) {
				stmt.setString(1, username);
				stmt.setString(2, password);
				
				stmt.execute();
				connection.close();
				return true;
			}
			else {
				connection.close();
				return false;
			}
			
		} catch (SQLException e) {
			System.out.println(CONNECTION_FAILED);
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Method for checking if the user already exists based on it's username.
	 * @param username The username to check.
	 * @return Returns true if the user exists, else false.
	 */
	private static boolean checkIfUserAlreadyExists(String username) {
		String query = "SELECT * FROM users WHERE username=\'" + username +"\'";
		Statement stmt;
		
		try {
			stmt = connection.createStatement();
			
			ResultSet resultSet = stmt.executeQuery(query);
			if (resultSet.next()) 		
				return true;
			
		} catch (SQLException e) {
			System.out.println(CONNECTION_FAILED);
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Method for logging in a user with the specified username and password.
	 * This is checked against the database.
	 * @param username The username to login with.
	 * @param password The password to login with.
	 * @return Returns the user ID. If no user was found, returns 0.
	 */
	public static int userLogin(String username, String password) {
		String query = "SELECT _ID FROM users WHERE username=\'" + username + "\' AND password =\'" + password +"\'";
		Statement stmt;
		
		try {
			connectToDatabase();
			stmt = connection.createStatement();
			
			ResultSet resultSet = stmt.executeQuery(query);
			
			if (resultSet.next()) {
				return (int) resultSet.getObject(1);
			} else return 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return 0;
	}
}
