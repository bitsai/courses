package gameserver;

import gameclient.Client;

/** A game message containing an updated player list for the server.  Sent by the server whenever a player logs on or off,
	or whenever players leave or enter a game. */
public class UpdateUserListMessage implements GameMessage
{
	private String[] userList;
	
	/**
		@param aUserList A user list, in the format provided by the server's getPlayerNames() method.
	*/
	public UpdateUserListMessage(String[] aUserList)
		{
		userList = aUserList;
		}
		
	/**	Calls the client's updateUserList() method. */
	public void process(Client myClient)
		{
		myClient.updateUserList(userList);
		}
}