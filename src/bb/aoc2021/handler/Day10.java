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
	char lastIllegalChar;
	
	long syntaxErrorScore = 0;
	
	public enum ParseStatus {VALID, INCOMPLETE, CORRUPT};
	
	protected boolean validChunk(char nextRead, String curChunk) {
		char cChunk = curChunk.charAt(0);
		char expected;
		switch (nextRead) {
		case '}' : expected = '{'; break;
		case ']' : expected = '['; break;
		case '>' : expected = '<'; break;
		case ')' : expected = '('; break;
		default :
			logger.warn("Unexpected token in validChunk: "+nextRead);
			return false;
		}
		if (cChunk == expected) {
			return true;
		} else {
			return false;
		}
	}
	
	protected void process(String chunk) {
		// no-op for part 1
	}
		
	protected ParseStatus parseLine(String line) {
		chunks.clear();
		for (int i=0; i<line.length(); ++i) {
			char nextRead = line.charAt(i);
			switch (nextRead) {
			case '(' :
			case '<' :
			case '{' :
			case '[' :
				chunks.push(String.valueOf(nextRead));
				break;
			case ')' :
			case '>' :
			case '}' :
			case ']' :
				String curChunk = chunks.peek();
				if (validChunk(nextRead, curChunk)) {
					curChunk = chunks.pop();
					process(curChunk);
					break;
				} else {
					logger.info("Invalid chunk: ), top stack: "+curChunk);
					lastIllegalChar = nextRead;
					return ParseStatus.CORRUPT;
				}
			default :
				logger.warn("Unexpected token: '"+nextRead+"' ignoring");
			}			
		}
		if (chunks.isEmpty()) {
			return ParseStatus.VALID;
		} else {
			return ParseStatus.INCOMPLETE;
		}
	}
	
	protected int score(char iChar) {
		switch (iChar) {
		case ')' : return 3;
		case ']' : return 57;
		case '}' : return 1197;
		case '>' : return 25137;
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
			break;
		default:
			logger.info("Line: "+line+" parse status: "+state);
		}		
	}

	@Override
	public void output() {
		logger.info("Syntax Error Score: "+syntaxErrorScore);		
	}

}
