package tictactoe;

import gameserver.*;
import java.rmi.*;
import java.rmi.server.*;

/** A server-side module for a TicTacToe game. */
public class TicTacToe extends UnicastRemoteObject implements TicTacToeRemote
{
	private String firstPlayer;
	private String secondPlayer;
	private String[][] board;
	private String winner;
	private String whosTurn;
	private int filledSpaces;
	private Server exitHandler;
	
	public TicTacToe() throws RemoteException
		{
		board = new String[3][3];
		winner = new String("");
		filledSpaces = 0;
		
		for ( int n = 0; n < 3; n++ )
			{
			for ( int z = 0; z < 3; z++ )
				{
				board[n][z] = new String("");
				}
			}
		}
	
	/** 
		The Server calls this method to tell the ServerModule to begin gameplay. 
		@param aServer A reference to the Server object; needed so the ServerModule knows what to call gameOver() on.
		@param player1 The name of the first player.
		@param player2 The name of the second player.
		@return A string indicating the name of the player who won the game.
	*/
	public void playGame(Server aServer, String player1, String player2) throws RemoteException
		{
		firstPlayer = player1;
		secondPlayer = player2;
		whosTurn = player1;
		exitHandler = aServer;		
		}
	
	
	/** The server calls this method if a player disconnects in mid-game (meaning one player logged off mid-game, or had their connection lost, or hit their "abort game" button).  Sets winner as the other player. */
	public synchronized void playerDisconnection(String playerName) throws RemoteException
		{
		String winner;
		
		if ( playerName.equalsIgnoreCase(firstPlayer) )
			winner = secondPlayer;
		else
			winner = firstPlayer;
			
		exitHandler.gameOver(winner, firstPlayer, secondPlayer);
		}

	
	/**
		Returns the module's name.
	*/
	public String getName()
		{
		return new String("TicTacToe");
		}
		
		
	/**
		RMI-enabled function for making a move on the TicTacToe board.
		@return An error message if there is a problem with the move, or null otherwise.
	*/
	public synchronized String makeMove(String playerName, int row, int col) throws RemoteException
		{
		if ( !whosTurn.equals(playerName) )
			return new String("It is not your turn!  It is " + whosTurn + "'s turn!");
		
		if ( !board[row][col].equals("") )
			return new String("That square has already been used!");
		
		filledSpaces++;
		
		if ( playerName.equals(firstPlayer) )
			{
			board[row][col] = firstPlayer;
			whosTurn = secondPlayer;
			}
		
		if ( playerName.equals(secondPlayer) )
			{
			board[row][col] = secondPlayer;
			whosTurn = firstPlayer;
			}
			
		checkWinner();
		return null;
		}
	
	/** RMI-enabled method for getting the game board.  The TicTacToeGUI calls this method regularly for a game that
		updates in real-time. */
	public synchronized String[][] getBoard() throws RemoteException
		{
		String[][] guiBoard = new String[3][3];
		
		
		for ( int n = 0; n < 3; n++ )
			{
			for ( int z = 0; z < 3; z++ )
				{
				if ( board[n][z].equals("") )
					guiBoard[n][z] = board[n][z];
				if ( board[n][z].equals(firstPlayer) )
					guiBoard[n][z] = new String("X");
				if ( board[n][z].equals(secondPlayer) )
					guiBoard[n][z] = new String("O");
				}
			}
			
		return guiBoard;
		}
	
	/** Called by the TicTacToeGUI regularly for game status bar. */
	public synchronized String getPlayerStatus(String whichPlayer) throws RemoteException
		{
		String status = new String ("Your symbol is: ");
		
		if ( whichPlayer.equalsIgnoreCase(firstPlayer) )
			status += "X.   ";  
		else
			status += "O.   ";

		if ( whosTurn.equalsIgnoreCase(whichPlayer) )
			status += "It is your turn.";
		else
			status += "It is your opponent's turn.";

		return status;
		}
	
	
	public synchronized String getWinner() throws RemoteException
		{
		return winner;
		}
		
	
	public void checkWinner()
		{
		for ( int n = 0; n < 3; n++ )
			{
			if ( areEqual(board[n][0], board[n][1], board[n][2]) )
				winner = board[n][0];
			if ( areEqual(board[0][n], board[1][n], board[2][n]) )
				winner = board[0][n];
			}
			
		if ( areEqual(board[0][0], board[1][1], board[2][2]) )
			winner = board[0][0];
			
		if ( areEqual(board[0][2], board[1][1], board[2][0]) )
			winner = board[0][2];
			
		if ( winner.length() > 0 )
			exitHandler.gameOver(winner, firstPlayer, secondPlayer);
		else
			{
			// Check for stalemate
			if ( filledSpaces == 9 )
				exitHandler.gameOver("Nobody", firstPlayer, secondPlayer);
			}
		}
		
		
	public boolean areEqual(String s1, String s2, String s3)
		{
		if ( s1.length() == 0 || !s1.equals(s2) )
			return false;
		if ( !s1.equals(s3) )
			return false;
		if ( !s2.equals(s3) )
			return false;
		return true;
		}
	
}