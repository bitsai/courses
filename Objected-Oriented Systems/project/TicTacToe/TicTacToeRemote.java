package tictactoe;

import java.rmi.*;

/** Remote interface for a TicTacToe ServerModule. */
public interface TicTacToeRemote extends Remote, gameserver.ServerModule
{
	public String makeMove(String playerName, int row, int col) throws RemoteException;
	
	public String[][] getBoard() throws RemoteException;
	
	public String getPlayerStatus(String whichPlayer) throws RemoteException;
	
	public String getWinner() throws RemoteException;
}