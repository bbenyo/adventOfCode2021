package bb.aoc2021.handler;

import java.util.Stack;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day10 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day10.class.getName());
	
	// Current token we're parsing, ignored for part 1
	String token;
	
	// Stack of the current chunk delimiters
	Stack<String> chunks = new Stack<String>();
	
	// Last illegal character encountered
	String lastIllegalChar = null;
	
	long syntaxErrorScore = 0;
	
	public enum ParseStatus {VALID, INCOMPLETE, CORRUPT};
	
	
	protected ParseStatus parseLine(String line) {
		return ParseStatus.VALID;
	}
	
	protected int score(String iChar) {
		switch (iChar) {
		case ")" : return 3;
		case "]" : return 57;
		case "}" : return 1197;
		case ">" : return 25137;
		default:
			logger.error("Unknown illegal char: "+iChar);
		}
		return 0;
	}
	
	@Override
	public void handleInput(String line) {
		ParseStatus state = parseLine(line);
		switch (state) {
		case CORRUPT :
			syntaxErrorScore += score(lastIllegalChar);
		default:
			logger.info("Line: "+line+" parse status: "+state);
		}		
	}

	@Override
	public void output() {
		// TODO Auto-generated method stub
		
	}

}
