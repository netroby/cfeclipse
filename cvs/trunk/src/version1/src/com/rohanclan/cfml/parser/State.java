package com.rohanclan.cfml.parser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Oliver Tupman
 *
 * Represents the current state of the parser.
 */
public class State 
{
	protected ArrayList messages = new ArrayList();
	protected String filename;
	protected int errCount = 0;
	protected boolean hadFatal = false;
	protected ArrayList matches = new ArrayList();
	
	//
	// The following is to keep track of function & variable names
	// TODO: I think the following should be a map so we can store the doc items against name for type recognition, etc.
	protected HashMap functionNames = new HashMap();
	protected HashMap variableNames = new HashMap();
	
	static public final int ADD_BEFORE = 0x01;
	static public final int ADD_AFTER =  0x02;
	
	public void addFunction(TagItem newFunction)
	{
		String funcName = newFunction.getAttribute("name");
		if(functionNames.containsKey(funcName))
		{
			addMessage(new ParseError(newFunction.lineNumber, newFunction.startPosition,
											newFunction.endPosition, newFunction.getItemData(), 
											"Duplicate function \'" + funcName + "\' found."));
		}
		else
			functionNames.put(funcName, funcName);
	}
	
	public ArrayList getMatches()
	{
		return matches;
	}
	
	public State(String docFilename)
	{
		filename = docFilename;
	}
	
	public ArrayList getMessages()
	{
		return messages;
	}
	
	public void addMatch(TagMatch newMatch, int position, int numIndicies)
	{
		switch(position)
		{
			case ADD_BEFORE:
				matches.add(matches.size() - numIndicies, newMatch);
				break;
			case ADD_AFTER:
				addMatch(newMatch);
				break;
			default:
				// Should this raise an exception?
				break;
		}
	}
	
	public void addMatch(TagMatch newMatch)
	{
		matches.add(newMatch);
	}
	
	public boolean hadFatal() { return hadFatal; }
	
	/**
	 * Adds a message to the parser state.
	 * @param newMsg
	 */
	public void addMessage(ParseMessage newMsg)
	{
		if(newMsg instanceof ParseError)
		{
			if(((ParseError)newMsg).isFatal())
				hadFatal = true;
			
			errCount++;				
		}
		
		messages.add(newMsg);
	}
}