package gameserver;

import gameclient.Client;

/** A message containg stylized text to be displayed in a player's text console. */
public class TextMessage implements GameMessage
{
	/** Style name constants. */
	public static final String REGULAR			= "regular";
	public static final String WHITE			= "white"; 
	public static final String YELLOW			= "yellow";
	public static final String RED				= "red";
	public static final String WINNER			= "winner";
	public static final String WINNER_BOLD		= "winner bold";
	public static final String LOG_ON			= "log on";
	public static final String LOG_OFF			= "log off";
	public static final String MESSAGE_SENDER	= "message sender";
	public static final String IM_TEXT			= "IM text";
	
	private String[] text;
	
	/**
		Constructor for creating a TextMessage containing stylized text.
		@param someText Array of strings containing the message's text and styles names. These alternate, ie, the array
						contains text, style name, text, style name, ...
	*/
	public TextMessage(String[] someText)
		{
		text = someText;
		}
		
	
	/** Calls the client's displayMessage() method to print the message on the client's console. */
	public void process(Client myClient)
		{
		myClient.displayMessage(this);
		}


	/** Returns the number of text strings in this message. */
	public int getTextCount()
		{
		return ( text.length / 2 );
		}
		
	/** Returns a specific text string. */
	public String getTextString(int which)
		{
		return ( text[which*2] );
		}

	
	/** Returns a specific text style name. */
	public String getStyleName(int which)
		{
		return ( text[which*2 + 1] );
		}

	
}