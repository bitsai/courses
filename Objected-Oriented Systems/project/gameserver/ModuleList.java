package gameserver;

import java.io.*;


/**
	A class for handling the list of currently-installed game modules.  Can add and remove from the list; can read/write
	the list to disk; etc.
*/
public class ModuleList
{
	private String[] moduleNames;

	public ModuleList()
		{
		moduleNames = new String[10];
		
		for ( int n = 0; n < 10; n++ )
			{
			moduleNames[n] = null;
			}
		
		loadModuleList();
		}
		
	/** Loads the saved list of modules. */
	public void loadModuleList() 
		{
		String curLine;
		
		try
			{
			BufferedReader bufInput = new BufferedReader( new FileReader("gamelist.txt") );
				
			while ( (curLine = bufInput.readLine()) != null )
				{
				try
					{
					addGame(curLine);
					}
				catch ( ClassNotFoundException badClass )
					{
					// Simply do nothing.  The bad class will be removed from the game list upon list save.
					}
				}
				
			bufInput.close();
			}
			
		catch ( IOException ioe )
			{
			// Can safely ignore this, since it means the module file didn't exist yet.
			}
		}
		
	/** Saves the list of loaded modules. */
	public void saveModuleList() 
		{
		try
			{
			BufferedWriter bufOutput = new BufferedWriter( new FileWriter("gamelist.txt", false) );
		
			for ( int n = 0; n < moduleNames.length; n++ )
				{
				if ( moduleNames[n] != null )
					{
					bufOutput.write(moduleNames[n], 0, moduleNames[n].length());
					bufOutput.newLine();
					}
				}
		
			bufOutput.close();
			}
			
		catch ( IOException ioe )
			{
			System.err.println("File problem:  Unable to save module list.");
			}
		}
		
	
	/**	Returns an array of Strings, each containing the name of a loaded game. */
	public String[] getModuleList()
		{
		String[] result;
		int counter = 0, cur = 0;
		
		for ( int n = 0; n < moduleNames.length; n++ )
			{
			if ( moduleNames[n] != null )
				counter++;
			}
			
		result = new String[counter];
		
		for ( int n = 0; n < moduleNames.length; n++ )
			{
			if ( moduleNames[n] != null )
				result[cur++] = new String( moduleNames[n] );
			}
		
		return result;		
		}
	
	/** 
		Adds a game to the server's game list. 
		@param moduleClassName The name of the class file for the game's ServerModule implementation class.
		@throws ClassNotFoundException The specified class cannot be found.
	*/
	public void addGame(String moduleClassName) throws ClassNotFoundException
		{
		try
			{
			Class someClass = Class.forName(moduleClassName);
			}
		
		catch ( ClassNotFoundException excep )
			{
			throw excep;			
			}
		catch ( Exception someOtherExcep )
			{
			System.err.println("Unable to add game: " + moduleClassName + ".\nFull stack trace:");
			someOtherExcep.printStackTrace();
			}
		
		
		for ( int n = 0; n <= moduleNames.length; n++ )
			{
			// If we've checked all game name slots and didn't find an empty one, grow the game name array.
			if ( n == moduleNames.length )
				{
				String[] tempNames = new String[moduleNames.length + 5];
				for ( short k = 0; k < tempNames.length; k++ )
					{
					if ( k < moduleNames.length )
						tempNames[k] = moduleNames[k];
					else
						tempNames[k] = null;
					}
					
				moduleNames = tempNames;
				}
			
			// Found an empty slot, so add the module.
			if ( moduleNames[n] == null )
				{
				moduleNames[n] = moduleClassName;
				return;
				}
			}
		}
		
	
	/** Removes a game from server's game list. */
	public void removeGame(String moduleClassName)
		{
		for ( int n = 0; n < moduleNames.length; n++ )
			{
			if ( moduleNames[n] != null && moduleNames[n].equalsIgnoreCase(moduleClassName) )
				moduleNames[n] = null;
			}
		}

}