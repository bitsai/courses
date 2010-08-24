package gameserver;

import java.io.*;


/**
	A class for storing the list of on-line players.  Also handles simple player-file manipulations for maintaining saved user list.
*/
public class PlayerList
{
	Player[] players;
	
	public PlayerList()
		{
		players = new Player[10];
		
		for ( int n = 0; n < players.length; n++ )
			{
			players[n] = null;
			}
		}

	
	/** Adds a new player to the on-line player list, growing internal array if needed. */
	public void add(String whichPlayer)
		{
		for ( short n = 0; n <= players.length; n++ )
			{
			// If we've checked all player slots and didn't find an empty one, grow the players array.
			if ( n == players.length )
				{
				Player[] tempPlayers = new Player[players.length + 5];
				for ( short k = 0; k < tempPlayers.length; k++ )
					{
					if ( k < players.length )
						tempPlayers[k] = players[k];
					else
						tempPlayers[k] = null;
					}
					
				players = tempPlayers;
				}
			
			// When an empty slot found, add the player.
			if ( players[n] == null )
				{
				players[n] = new Player(whichPlayer);
				return;
				}
			}
		
		}

		
	/** Removes a player from the player list, effectively logging them off. */
	public void remove(Player whichPlayer)
		{
		for ( int n = 0; n < players.length; n++ )
			{
			if ( whichPlayer == players[n] )
				players[n] = null;
			}
		}


	/** Returns an array of players.  This array contains only valid players, ie, it has no null elements. */
	public Player[] getAll()
		{
		int count = 0;
		int cur = 0;
		
		for ( int n = 0; n < players.length; n++ )
			{
			if ( players[n] != null )
				count++;
			}
			
		Player[] newList = new Player[count];
		for ( int n = 0; n < players.length; n++ )
			{
			if ( players[n] != null )
				newList[cur++] = players[n];
			}

		return newList;
		}
		
		
	/** Adds a new entry to the player file. */
	public void addToDatabase(String userName, String password) throws IOException
		{
		// Currently, players are just stored in a simple flat text file.  If we had time for an Iteration 6, this would
		// be replaced to use JDBC instead.
		BufferedWriter bufOutput = new BufferedWriter( new FileWriter("user.txt", true) );
		
		bufOutput.write(userName, 0, userName.length());
		bufOutput.newLine();
		bufOutput.write(password, 0, password.length());
		bufOutput.newLine();
		bufOutput.close();
		}
		
	/** Given a player name, looks through the player list and finds a corresponding Player object, if one exists. */
	public Player lookup(String playerName)
		{
		for ( short n = 0; n < players.length; n++ )
			{
			if ( players[n] != null && players[n].getName().equals(playerName) )
				return players[n];			
			}
		return null;
		}
		
	
	/** Returns the password for a given player, from the player file. Returns null if that player does not currently exist. */
	public String lookupPassword(String name)
		{
		String myPassword = null;
		String curUser;
		String curPW;
		
		try
			{
			BufferedReader bufInput = new BufferedReader( new FileReader("user.txt") );
				
			while ( (curUser = bufInput.readLine()) != null )
				{
				curPW = bufInput.readLine();
				
				if ( name.equalsIgnoreCase(curUser) )
					return curPW;
				}
				
			bufInput.close();
			}
			
		catch ( IOException ioe )
			{
			}
			
		return null;
		}
		
}
		
	