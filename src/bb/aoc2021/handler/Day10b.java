package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.handler.Day10.ParseStatus;

public class Day10b extends Day10 {
	
	static private Logger logger = Logger.getLogger(Day10b.class.getName());
	
	List<Long> completionScores = new ArrayList<>();
	
	protected void completeLine(String line) {
		long score = 0;
		while (!chunks.isEmpty()) {
			String nextChunk = chunks.pop();
			char completeChar = completeChunk(nextChunk);
			score = updateScore(score, completeChar);
		}
		logger.info("Completion score is "+score);
		completionScores.add(score);
	}
	
	protected long updateScore(long score, char cChar) {
		int charScore = 0;
		switch (cChar) {
		case ')' : charScore = 1; break;
		case ']' : charScore = 2; break;
		case '}' : charScore = 3; break;
		case '>' : charScore = 4; break;
		default :
			logger.error("Unexpected char: "+cChar);
		}
		long nScore = (score * 5) + charScore;
		return nScore;
	}
	
	protected char completeChunk(String nextChunk) {
		char cChunk = nextChunk.charAt(0);
		switch (cChunk) {
		case '{' : return '}';
		case '[' : return ']';
		case '<' : return '>';
		case '(' : return ')';
		default :
			logger.error("Unable to complete chunk: "+nextChunk);
			return '\0';
		}
	}
	
	@Override
	public void handleInput(String line) {
		ParseStatus state = parseLine(line);
		switch (state) {
		case CORRUPT :
			logger.info("Discarding corrupt line: "+line);
			break;
		case INCOMPLETE :
			logger.info("Completing incomplete line: "+line);
			completeLine(line);
			break;
		default:
			logger.info("Line: "+line+" parse status: "+state);
		}		
	}
	
	@Override
	public void output() {
		Collections.sort(completionScores);
		int sz = completionScores.size();
		// Instructions say we're guarnateed to be an odd number here
		if ((sz % 2) == 1) {
			int medIndex = (int)Math.floor(sz / 2.0);
			logger.info("Median score: "+completionScores.get(medIndex));
		} else {
			logger.error("Got an even number of incomplete lines!");
		}
	}
	
}
