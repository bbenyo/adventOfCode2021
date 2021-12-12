package bb.aoc2021.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import bb.aoc2021.Utilities;

public class Day8b extends Day8 {
	static private Logger logger = Logger.getLogger(Day8b.class.getName());
	Map<Integer, Set<Character>> wires;
	
	long sum = 0;

	protected void mapInput(String[] inputs) {
		wires = new HashMap<Integer, Set<Character>>();
		// First pass, map the unique 4
		mapUnique(inputs);
		
		// Next pass, get the 6 wire ones, 0, 6, 9
		// We can deduce these from based on the unique 4
		// 6 is the only one that doesn't have all wires from 1 or 7
		// 0 doesn't have all wires from the 4 (missing the middle)
		// 9 is the 6 wire number that isn't 6 or 0
		mapSixWire(inputs);
		
		// Finally, get the 5 wires, 2, 3, 5
		// 3 is the only one that has all wires from 1 or 7
		// 5 has all wires from the 6
		// 2 is the one that isn't 3 or 5
		mapFiveWire(inputs);
	}
	
	
	
	protected void mapUnique(String[] inputs) {
		for (String i : inputs) {
			switch(i.length()) {
			case 2 :
				wires.put(1, Utilities.stringToCharList(i));
				break;
			case 3 :
				wires.put(7, Utilities.stringToCharList(i));
				break;
			case 4 :
				wires.put(4, Utilities.stringToCharList(i));
				break;
			case 7 :
				wires.put(8, Utilities.stringToCharList(i));
				break;
			default:
				break;
			}
		}
	}
	
	protected void mapSixWire(String[] inputs) {
		for (String i : inputs) {
			if (i.length() == 6) {
				// 6 is the only one that doesn't have all wires from 1 or 7
				if (!Utilities.matchesRight(i, wires.get(1))) {
					wires.put(6, Utilities.stringToCharList(i));
				} else if (!Utilities.matchesRight(i, wires.get(4))) {
					// 0 doesn't have all wires from the 4 (missing the middle)
					wires.put(0, Utilities.stringToCharList(i));
				} else {
					// Process of elimination
					wires.put(9, Utilities.stringToCharList(i));
				}
			}
		}
	}
	
	protected void mapFiveWire(String[] inputs) {
		for (String i : inputs) {
			if (i.length() == 5) {
				// 3 is the only one that has all wires from 1 or 7
				if (Utilities.matchesRight(i, wires.get(1))) {
					wires.put(3, Utilities.stringToCharList(i));
				} else if (Utilities.matchesLeft(i, wires.get(6))) {
					// 5 has all wires from the 6
					wires.put(5, Utilities.stringToCharList(i));
				} else {
					// Process of elimination
					wires.put(2, Utilities.stringToCharList(i));
				}
			}
		}
	}
	
	protected int decodeOutput(String val) {
		switch (val.length()) {
		case 2 :
			return 1;
		case 3 :
			return 7;
		case 4 : 
			return 4;
		case 5 :
			if (Utilities.matchesRight(val, wires.get(2))) {
				return 2;
			} else if (Utilities.matchesRight(val, wires.get(3))) {
				return 3;
			} else {
				return 5;
			}
		case 6 :
			if (Utilities.matchesRight(val, wires.get(6))) {
				return 6;
			} else if (Utilities.matchesRight(val, wires.get(9))) {
				return 9;
			} else {
				return 0;
			}
		case 7 :
			return 8;
		default :
			logger.error("Invalid output length: "+val);
		}
		return -1;
	}
	
	@Override
	public void handleInput(String line) {
		String[] io = line.split("\\|");
		if (io.length != 2) {	
			logger.warn("Unable to parse input: "+line);
			return;
		}
		
		// Not necessary to parse for problem 1, we only handle the output!
		// String inputValues = io[0];
		
		String[] inputDigits = io[0].trim().split(" ");
		String outputValues = io[1].trim();
		String[] outputDigits = outputValues.split(" ");
		
		mapInput(inputDigits);
		
		StringBuffer oValStr = new StringBuffer();
		for (String oDigit : outputDigits) {
			int o = decodeOutput(oDigit);
			logger.info("Decode "+oDigit+" = "+o);
			oValStr.append(o);
		}
		
		try { 
			int oVal = Integer.parseInt(oValStr.toString());
			sum += oVal;
		} catch (NumberFormatException ex) {
			logger.error(ex.toString(), ex);
		}
	}

	@Override
	public void output() {
		logger.info("Sum of all outputs: "+sum);
	}

}
