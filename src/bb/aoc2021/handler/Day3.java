package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day3 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day3.class.getName());
	
	int count = 0;
	int[] onesCount = null;
	
	@Override
	public void handleInput(String line) {
		count++;
		
		if (onesCount == null) {
			// Assuming lines are all the same size
			onesCount = new int[line.length()];
			logger.info("Initialize onesCounter for "+onesCount.length+" places");
			for (int i=0; i<onesCount.length; ++i) {
				onesCount[i] = 0;
			}
		}
		
		for (int i=0; i<line.length(); ++i) {
			char c = line.charAt(i);
			if (c == '1') {
				onesCount[i]++;
			} else if (c != '0') {
				logger.error("Unrecognized 'digit' in "+line+": "+c);
			}
		}
	}
	
	public long computeBinaryValue(boolean mostCommon) {
		int halfCount = count / 2;
		long val = 0;
		logger.info("Count is: "+count+" half is: "+halfCount);
		
		// If OnesCount[i] > count/2, then 1s are the most common, more than half are ones
		for (int i=0; i < onesCount.length; ++i) {
			int valPos = onesCount.length - i - 1;
			if (onesCount[i] > halfCount) {
				if (mostCommon) {
					val += Math.pow(2, valPos);
				}
			} else if (onesCount[i] == halfCount) {
				logger.warn("Equal number of 0's and 1's, undefined situation, we'll go with 1");
				if (mostCommon) {
					logger.info("Setting bit "+valPos);
					val += Math.pow(2, valPos);
				}
			} else if (!mostCommon) {
				logger.info("Setting bit "+valPos);
				val += Math.pow(2, valPos);
			}
		}
		return val;
	}
	
	@Override
	public void output() {
		long gamma = computeBinaryValue(true); 
		logger.info("Gamma: "+gamma);
		long epsilon = computeBinaryValue(false);
		logger.info("Epsilon: "+epsilon);
		logger.info("Product: "+(gamma * epsilon));
	}

}
