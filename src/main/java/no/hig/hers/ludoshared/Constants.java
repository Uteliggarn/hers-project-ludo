package no.hig.hers.ludoshared;
/**
 * Class with all the constants used by communication between clients and server.
 *
 */
public class Constants {
	// Categories for messages
	public static final String GAMEMESSAGE = "GAMEMESSAGE:";	// Every game command starts with this keyword
	public static final String CHATMESSAGE = "CHATMESSAGE:";	// Every chat command starts with this keyword
	public static final String PLAYERMESSAGE = "PLAYERMESSAGE:";	// Every player message starts with this keyword
	
	// General messages:
	public static final String JOIN = "JOIN:";					// Joining game or chat
	public static final String LOGOUT = "LOGOUT:";				// Closing chat rooms
	public static final String CLOGOUT = ">>>LOGOUT<<<";		// Closing the chat windows
	public static final String TOP = "TOP";						// The client asks for top lists
	public static final String PLAYERSCORES = "PLAYERSCORES";	// The client asks for his scores
	
	// Chat-related type messages:
	public static final String NEWCHAT = "NEWGROUPCHAT:";		// Creates a new chat
	public static final String REMOVECHAT = "REMOVECHAT:";		// Removes a chat
	public static final String ERRORCHAT = "ERRORCHAT";			// An error in the chat
	public static final String LEAVECHAT = "LEAVE:";			// Player leaves the chat
	
	// Game-related type messages:
	public static final String CREATEGAME = "CREATEGAME:";		// Creates a new game
	public static final String IDGK = "IDGK";					// Unique name, every host gets the IDGK keyword
	public static final String INVITE = "INVITE:";				// Invites friends to a game
	public static final String HOST = "HOST:";					// The host for the match
	public static final String QUEUE = "QUEUE";					// Joining queue
	public static final String HOTJOIN = "HOTJOIN:";			// The players in queue that does not become host
	public static final String GAMESTART = "GAMESTART:";		// The game starts
	public static final String GAMENAME = "GAMENAME:";			// The name of the game
	public static final String DICEVALUE = "DICEVALUE:";		// Tells which player, pawn, and how far it moved
	public static final String GAMEOVER = "GAMEOVER";			// The game is over
	public static final String GAMEWON = "GAMEWON";				// Only the winner sends winning message
	public static final String GAMELOST = "GAMELOST";			// Everybody that is not the winner send loosing message
	public static final String DISCONNECT = "DISCONNECT:";		// A player leaves the game tab
	public static final String REMOVEHOST = "REMOVEHOST:";		// Removes the host from gamelist
	public static final String QUEOPEN = "QUEOPEN";				// Reopen the que button if noone could host a game
	public static final String GAMECHAT = "Gamechat-";
	
	// Login/Register-related type messages:
	public static final String ACCEPTED = "ACCEPTED";			// Registration accepted
	public static final String DECLINED = "DECLINED";			// Registration declined
	public static final String SENDLOGIN = "SENDLOGIN:";		// Sends the login information
	public static final String SENDREGISTER = "SENDREGISTER:";	// Send registration information

	// Player-related type messages:
	public static final String GETPLAYERLIST = "GETPLAYERLIST";	// Client asks for the list
    public static final String TOPPLAYED = "TOPLISTPLAYED:";	// Returns top played from server
	public static final String TOPWON = "TOPLISTWON:";			// Returns top won from server
	
	private Constants() {}
}
