package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Utilities;

public class Day3b extends Day3 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day3b.class.getName());
		
	List<String> values = new ArrayList<String>();
	int digits = -1;
	
	@Override
	public void handleInput(String line) {
		values.add(line);
		if (digits == -1) {
			digits = line.length();
		} else if (digits != line.length()) {
			logger.warn("Different size strings: "+line+" expected: "+digits);
		}
	}

	protected long computeOxygenGeneratorRating() {
		return getCommonBits(true);
	}
	
	protected long computeCO2ScrubberRating() {
		return getCommonBits(false);
	}
	
	/**
	 * Get the most common value at index in the list of values
	 *   This version returns a boolean, true for 1 and false for 0
	 *   
	 * @param index
	 * @param rValues
	 * @return true if 1 is the most common value at index
	 */
	protected boolean getMostCommon(int index, List<String> rValues) {
		int onesCount = 0;
		int half =(int)Math.round(rValues.size() / 2.0);
		for (String v : rValues) {
			if (index < v.length() && v.charAt(index) == '1') {
				onesCount++;
			}
		}
		if (onesCount >= half) {
			return true;
		}
		return false;
	}
	
	/**
	 * Remove all entries in rValues where the digit at index is not equal to targetValue
	 *   Again targetValue is a boolean, so true = '1' and false = '0'
	 * @param index
	 * @param targetValue
	 * @param rValues
	 */
	protected void removeAllWith(int index, boolean targetValue, List<String> rValues) {
		List<String> remove = new ArrayList<String>();
		for (String v : rValues) {
			if (index >= v.length()) {
				logger.warn("String too long: "+v+" for index "+index);
			} else if 
				// Which ones to remove? targetValue is true means we want to keep strings with a 1 in index
				//   So remove if they have a 0 instead, and vice versa
			    ((targetValue && v.charAt(index) == '0') ||
			     (!targetValue && v.charAt(index) == '1')) {
				remove.add(v);
			}
		}
		logger.debug("Removing "+remove.size()+" strings due to index "+index);
		rValues.removeAll(remove);
	}
	
	/**
	 * Iterate through the strings index by index, removing all entries with the most (or least) common value
	 * @param wantMostCommon
	 * @return
	 */
	protected long getCommonBits(boolean wantMostCommon) {
		List<String> rValues = new ArrayList<String>(values);
		for (int i=0; i<digits; ++i) {
			boolean targetValue = getMostCommon(i, rValues);
			if (!wantMostCommon) {
				targetValue = !targetValue;
			}
			logger.info("Most common "+i+" was "+targetValue);
			removeAllWith(i, targetValue, rValues);
			if (rValues.size() == 1) {
				return Utilities.bitStringToLong(rValues.get(0));
			}
		}
		if (rValues.size() == 1) {
			return Utilities.bitStringToLong(rValues.get(0));
		} else {
			logger.error("More than 1 option left: "+rValues.size());
			return -1;
		}
	}
	
	
	
	@Override
	public void output() {
		long o2 = computeOxygenGeneratorRating();
		logger.info("O2 Generator Rating: "+o2);
		long co2 = computeCO2ScrubberRating();
		logger.info("CO2 Scrubber Rating: "+co2);
		logger.info("Product: "+(o2 * co2));
	}

}
