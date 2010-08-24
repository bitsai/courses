package cs321.superscrabble;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/** This class oversees execution of SuperScrabble. */
public class SuperScrabble
{
	static Scrabble aScrabble = new Scrabble();

/** Main method, reads in dictionary file, initializes Scrabble object, and initializes ScrabbleFrame object. */	
	public static void main (String[] args)
	{
		try
		{
			String filename = "ospd.txt";

			BufferedReader filein = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = filein.readLine()) != null)
			{
				aScrabble.addWord(line);
			}
			filein.close();
		}
		catch(IOException e)
		{
			System.out.println("Error reading dictionary!");
			System.exit(0);
		}

		try
		{
			aScrabble.initialize();
		}
		catch (IllegalMove i)
		{
			System.out.println("Error initializing board!");
			System.exit(0);
		}

		ScrabbleFrame frame = new ScrabbleFrame(aScrabble);
		frame.show();
	}
}