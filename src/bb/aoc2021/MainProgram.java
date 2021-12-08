package bb.aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import bb.aoc2021.handler.Day1;
import bb.aoc2021.handler.Day1b;
import bb.aoc2021.handler.Day2;
import bb.aoc2021.handler.Day2b;
import bb.aoc2021.handler.Day3;
import bb.aoc2021.handler.Day3b;
import bb.aoc2021.handler.Day4;
import bb.aoc2021.handler.Day4b;
import bb.aoc2021.handler.Day5;
import bb.aoc2021.handler.Day5b;

public class MainProgram {

	static private Logger logger = Logger.getLogger(MainProgram.class.getName());

	/**
	 * argument 1: test | input to use testX or inputX as the input
	 * argument 2: problem number: 1, 1b, 2, 2b, 3, 3b, etc
	 * @param args
	 */
	static public void main(String[] args) {
		if (args.length != 2) {
			logger.error("Usage: MainProgram [test|input] [Problem number]");
			System.exit(-1);
		}
		
		String testOrInput = args[0];
		String dayNumber = args[1];
		boolean bVersion = false;
		if (dayNumber.endsWith("b")) {
			bVersion = true;
			dayNumber = dayNumber.substring(0, dayNumber.length() - 1);
		}
		
		logger.info("AOC 2021 MainProgram: "+dayNumber+" bVersion: "+bVersion+" Input type: "+testOrInput);
		
		InputHandler handler = createInputHandler(dayNumber, bVersion);
		if (handler != null) {
			try {
				String input = null;
				if (testOrInput.equalsIgnoreCase("test")) {
					input = "data/test/test" + dayNumber + ".txt";
				} else if (testOrInput.equalsIgnoreCase("input")) {
					input = "data/input/input" + dayNumber + ".txt";
				} else {
					logger.error("Unknown input type: "+testOrInput+", expecting test|input");
					System.exit(-1);
				}
				handleInputFile(input, handler);
			} catch (IOException ex) {
				logger.error(ex.toString(), ex);
			}
		}
	}
	
	static public void handleInputFile(String filename, InputHandler handler) throws IOException {
		logger.info("Loading input file at "+filename);
		try (BufferedReader bread = new BufferedReader(new FileReader(filename))) {
			String line = bread.readLine();
			while (line != null) {
				handler.handleInput(line.trim());
				line = bread.readLine();
			}
			handler.output();
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		} 
	}
	
	static public InputHandler createInputHandler(String dayNumber, boolean bVersion) {
		// TODO: Reflection here
		String handlerName = "Day"+dayNumber;
		if (bVersion) {
			handlerName = handlerName + "b";
		}
		switch (handlerName) {
		case "Day1" : 
			return new Day1();
		case "Day1b" : 
			return new Day1b();
		case "Day2" :
			return new Day2();
		case "Day2b" :
			return new Day2b();
		case "Day3" :
			return new Day3();
		case "Day3b" :
			return new Day3b();
		case "Day4" :
			return new Day4();
		case "Day4b" :
			return new Day4b();
		case "Day5" :
			return new Day5();
		case "Day5b" :
			return new Day5b();
		default :
			logger.error("Unknown input handler: "+handlerName);
			return null;
		}
	}
}
