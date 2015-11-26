package no.hig.hers.ludoshared;

public class Constants {
	public final static String NEWCHAT = "NEWGROUPCHAT:"; //Creates a new chat
	public final static String JOINCHAT = "JOIN:"; //Player joins the chat
	public final static String ERRORCHAT = "ERRORCHAT"; //An error in the chat
	public final static String LEAVECHAT = "LEAVE:"; //Player leaves the chat
	public final static String CREATEGAME = "CREATEGAME:"; //Creates a new game
	public final static String IDGK = "IDGK";// Unique name, every host gets the IDGK codeword
	public final static String INVITE = "INVITE:";	//Invites friends to a game
	public final static String HOST = "HOST:";	//The host for the match
	public final static String QUEUE = "QUEUE";  //Joining queue
	public final static String JOIN = "JOIN:";	//Joining game
	//public final static String QUITGAME = "LOGOUT:";
	public final static String HOTJOIN = "HOTJOIN:"; //The players in queue that does not become host
	public final static String TOPPLAYED = "TOPLISTPLAYED:"; //Returns top played from server
	public final static String TOPWON = "TOPLISTWON:"; //Returns top won from server
	public final static String GAMESTART = "GAMESTART:"; //The game starts
	public final static String GAMENAME = "GAMENAME:"; //The name of the game
	public final static String DICEVALUE = "DICEVALUE:"; //Tells which player, pawn, and how far it moved
	public final static String GAMEOVER = "GAMEOVER"; //The game is over
	public final static String GLOBALCHAT = "GlobalJOIN:";	// Global chat name
	public final static String QUEOPEN = "QUEOPEN";	
	public final static String GAMEWON = "GAMEWON";	//Only the winner sends winning message
	public final static String GAMELOST = "GAMELOST"; //Everybody that is not the winner send loosing message
	public final static String ACCEPTED = "ACCEPTED"; //Accepted the invite
	public final static String DECLINED = "DECLINED"; //Declined the invite
	public final static String SENDLOGIN = "SENDLOGIN:"; //Sends the login information
	public final static String SENDREGISTER = "SENDREGISTER:"; //Send registration information
	public final static String LOGOUT = "LOGOUT:";	//Closing chat rooms
	public final static String CLOGOUT = ">>>LOGOUT<<<"; //Closing the chat windows
	public final static String ERROR = "ERROR"; // Something went wrong
	public final static String TOP = "TOP"; //The client asks for both top played and top won
	public final static String GETPLAYERLIST = "GETPLAYERLIST"; //Client asks for the list
	public final static String DISCONNECT = "DISCONNECT:"; //A player leaves the game tab
	
	private Constants() {}
}
