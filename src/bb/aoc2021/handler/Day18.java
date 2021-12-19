package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.snailfish.SnailfishElement;
import bb.aoc2021.snailfish.SnailfishLiteral;
import bb.aoc2021.snailfish.SnailfishNumber;

public class Day18 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day18.class.getName());
	
	List<SnailfishNumber> snailfish = new ArrayList<SnailfishNumber>();
	
	@Override
	public void handleInput(String line) {
		Stack<SnailfishElement> stack = new Stack<SnailfishElement>();
		String curLiteral = null;
		for (int i=0; i<line.length(); ++i) {
			char c = line.charAt(i);
			if (Character.isDigit(c)) {
				if (curLiteral == null) {
					curLiteral = ""+c;
				} else {
					curLiteral = curLiteral + c;
				}
			} else {
				// If we are making a literal out of digits, we're done since this isn't a digit
				if (curLiteral != null) {
					SnailfishLiteral newLiteral = new SnailfishLiteral(null);
					newLiteral.setValue(Long.parseLong(curLiteral));
					curLiteral = null;
					stack.push(newLiteral);
				}
				
				if (c == ']') {
					// Pop the last 2 elements off the stack and make a pair with them, right, left
					// Ok to throw an exception here, we want to fail right away if parsing is broken
					SnailfishElement right = stack.pop();
					SnailfishElement left = stack.pop();
					SnailfishNumber newNumber = new SnailfishNumber(null);
					newNumber.setLeft(left);
					newNumber.setRight(right);
					stack.push(newNumber);				
				}
			}
		}
		// We should end up with a single pair on the stack
		SnailfishElement sNum = stack.pop();
		if (sNum instanceof SnailfishNumber) {
			this.snailfish.add((SnailfishNumber)sNum);
		} else {
			logger.warn("Unable to parse "+line);
		}

	}

	@Override
	public void output() {
		// Should convert to unit tests =) 
		//  Test data
		/**
		SnailfishNumber lastNum = null;
		for (SnailfishNumber sNum : snailfish) {
			logger.info(sNum+": "+sNum.getMagnitude());
			sNum.reduce();
			logger.info("Reduced: "+sNum);
			lastNum = sNum;
		}
		
		// Add lastNum + 1,1
		SnailfishNumber oneone = new SnailfishNumber(null);
		SnailfishLiteral l1 = new SnailfishLiteral(1);
		oneone.setLeft(l1);
		SnailfishLiteral l2 = new SnailfishLiteral(1);
		oneone.setRight(l2);
		
		logger.info("Adding "+lastNum+" + "+oneone);
		SnailfishNumber a1 = lastNum.add(oneone);
		logger.info(" = "+System.lineSeparator()+a1);
		**/
		
		// Real Day 18
		// Add each number
		SnailfishNumber current = null;
		for (SnailfishNumber n : snailfish) {
			if (current == null) {
				current = n;
			} else {
				current = current.add(n);
			}
		}
		
		logger.info("Final Result: "+current);
		logger.info("Final Magnitude: "+current.getMagnitude());
		
	}

}
