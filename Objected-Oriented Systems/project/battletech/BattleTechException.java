package battletech;

import java.io.*;

public class BattleTechException extends Exception implements Serializable
{
	private String cause;

	public BattleTechException(String input)
	{
		super(input); 
		cause = input;
	}

	public String toString()
	{
		String output = cause;
		return(output);
	}
}