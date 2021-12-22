package bb.aoc2021.handler;

import org.apache.log4j.Logger;

public class Day20b extends Day20 {
	static private Logger logger = Logger.getLogger(Day20b.class.getName());
	
	@Override
	public void output() {
		logger.info("Input image: "+System.lineSeparator()+image);
		logger.info("Pixels On: "+image.getOnPixels());
		
		for (int i=0; i<50; ++i) {
			image = this.enhance();
			logger.info("Input image, enhanced "+(i+1)+" times: "+System.lineSeparator()+image);
			logger.info("Pixels On: "+image.getOnPixels());
		}
	}
}
