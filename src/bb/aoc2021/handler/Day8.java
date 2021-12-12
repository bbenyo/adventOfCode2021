package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day8 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day8.class.getName());
	
	protected List<Integer> mapOutput(String wiresOn) {
		int len = wiresOn.length();
		List<Integer> mapping = new ArrayList<Integer>();
		switch (len) {
		case 2 :
			mapping.add(1);
			break;
		case 3 : 
			mapping.add(7);
			break;
		case 4 : 
			mapping.add(4);
			break;
		case 5 :
			mapping.add(2);
			mapping.add(3);
			mapping.add(5);
			break;
		case 6 :
			mapping.add(0);
			mapping.add(6);
			mapping.add(9);
			break;
		case 7 :
			mapping.add(8);
			break;
		default :
			logger.error("Improper length of wires in output: "+wiresOn);
		}
		return mapping;
	}
	
	int knownDigitCount = 0;

	@Override
	public void handleInput(String line) {
		String[] io = line.split("\\|");
		if (io.length != 2) {	
			logger.warn("Unable to parse input: "+line);
			return;
		}
		
		// Not necessary to parse for problem 1, we only handle the output!
		// String inputValues = io[0];
		
		String outputValues = io[1].trim();
		String[] outputDigits = outputValues.split(" ");
		for (String oDigit : outputDigits) {
			List<Integer> pValues = mapOutput(oDigit.trim());
			if (pValues.size() == 1) {
				knownDigitCount++;
			}
		}
	}

	@Override
	public void output() {
		logger.info("Known digit count: "+knownDigitCount);
	}

}
