package bb.aoc2021;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

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
					input = "data/test/test" + dayNumber;
				} else if (testOrInput.equalsIgnoreCase("input")) {
					input = "data/input/input" + dayNumber;
				} else {
					logger.error("Unknown input type: "+testOrInput+", expecting test|input");
					System.exit(-1);
				}
				
				if (bVersion) {
					// do we have a special B version of the input?
					String i2 = input + "b.txt";
					File f1 = new File(i2);
					if (!f1.exists()) {
						input = input + ".txt";
					} else {
						input = i2;
					}
				} else {
					input = input + ".txt";
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
		String pkg = "bb.aoc2021.handler";
		try {
			String className = pkg + "." + handlerName;
			Class<?> hClass = Class.forName(className);
			InputHandler handler = (InputHandler)hClass.getConstructor().newInstance();
			return handler;
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		return null;
	}
}
