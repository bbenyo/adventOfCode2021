package bb.aoc2021.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Utilities;

public class Day14 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day14.class.getName());
	
	protected String polymer = null;
	
	// pair -> element, e.g. HN -> B means HN gets replaced by HBN
	protected Map<String, String> rules = new HashMap<>();

	@Override
	public void handleInput(String line) {
		if (polymer == null) {
			polymer = line;
			logger.info("Initial Polymer: "+polymer);
			return;
		}
		
		if (line.trim().length() == 0) {
			return;
		}
		
		int aPos = line.indexOf("->");
		if (aPos == -1) {
			logger.error("Unable to parse: "+line);
			return;
		}
		
		String pair = line.substring(0, aPos).trim();
		String element = line.substring(aPos + 2).trim();
		
		if (pair.length() != 2) {
			logger.error("Unable to parse pair "+pair+" from: "+line);
			return;
		}
		
		if (element.length() != 1) {
			logger.error("Unable to parse element "+element+" from: "+line);
			return;
		}
		
		if (rules.containsKey(pair)) {
			logger.error("Already have a rule for "+pair+": "+rules.get(pair));
			logger.error("Ignoring this new rule, since it will never fire: "+line);
			return;
		}
		
		rules.put(pair, element);
	}
	
	protected String growPolymer(String polymer) {
		StringBuffer newPolymer = new StringBuffer();
		for (int i=0; i<polymer.length() - 1; ++i) {
			String currentPair = polymer.substring(i, i+2);
			String cFirst = currentPair.substring(0,1);
			String replaceElement = rules.get(currentPair);
			if (replaceElement == null) {
				newPolymer.append(cFirst); // Just add the first letter of the pair
				// the next step will get the next letter, etc.
			} else {
				newPolymer.append(cFirst).append(replaceElement);				
			}
		}
		newPolymer.append(polymer.substring(polymer.length() - 1));
		return newPolymer.toString();
	}

	protected int steps = 10;
	protected boolean verbose = true;
	
	@Override
	public void output() {
		String curPolymer = polymer;
		for (int i=1; i<=steps; ++i) {
			curPolymer = growPolymer(curPolymer);
			if (verbose) logger.info("Polymer after "+i+": "+curPolymer);
		}
		
		Map<Character, Long> counts = Utilities.characterCounts(curPolymer);
		countLargestSmallest(counts);
	}
	
	protected void countLargestSmallest(Map<Character, Long> counts) {
		long smallest = -1;
		long largest = 0;
		String sElement = null;
		String lElement = null;
		
		for (Entry<Character,Long> entry : counts.entrySet()) {
			Character c1 = entry.getKey();
			Long i1 = entry.getValue();
			if (smallest == -1 || i1 < smallest) {
				smallest = i1;
				sElement = String.valueOf(c1);
			}
			if (i1 > largest) {
				largest = i1;
				lElement = String.valueOf(c1);
			}
		}

		logger.info("Most Common element: "+lElement+" count: "+largest);
		logger.info("Least Common element: "+sElement+" count: "+smallest);

		logger.info("Diff: "+(largest - smallest));
	}

}
