package bb.aoc2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import bb.aoc2021.handler.Day1;
import bb.aoc2021.handler.Day1b;

public class MainProgram {

	static private Logger logger = Logger.getLogger(MainProgram.class.getName());

	/**
	 * argument 1: Input file name
	 * @param args
	 */
	static public void main(String[] args) {
		if (args.length != 2) {
			logger.error("Usage: MainProgram [inputFileName] [handler]");
			System.exit(-1);
		}
		
		String input = args[0];
		String handlerName = args[1];
		
		InputHandler handler = createInputHandler(handlerName);
		if (handler != null) {
			try {
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
				handler.handleInput(line);
				line = bread.readLine();
			}
			handler.output();
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		} 
	}
	
	static public InputHandler createInputHandler(String handlerName) {
		switch (handlerName) {
		case "Day1" : 
			return new Day1();
		case "Day1b" : 
			return new Day1b();
		default :
			logger.error("Unknown input handler: "+handlerName);
			return null;
		}
	}
}
