package gameserver;

/**
	An interface for game messages, which are passed from the server to the client.  Implemented by a specific message
	type to perform a particular task.  For example, TextMessage is a message that stores text to be displayed in the
	client's console window, while ChallengeMessage is a message encapsulating a game challenge from one user to another.
*/
public interface GameMessage extends java.io.Serializable
{
	/** 
		The client calls the process() method upon receiving the GameMessage from the server.  The client passes a reference
		of itself to process() so that the method can call necessary Client methods to perform the effects of the message.
	*/
	public void process(gameclient.Client myClient);
}