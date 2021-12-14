package bb.aoc2021.handler;

import org.apache.log4j.Logger;

public class Day13b extends Day13 {
	
	static private Logger logger = Logger.getLogger(Day13b.class.getName());
	
	@Override
	public void output() {
		drawGrid();

		for (Fold fold : folds) {
			logger.info("Fold: "+fold);
			this.fold(fold);
			logger.info("Dots visible: "+dots.size());
		}
		
		drawGrid();
		// Should be able to read it!
	}
}
