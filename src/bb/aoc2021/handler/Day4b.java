package bb.aoc2021.handler;

import org.apache.log4j.Logger;

public class Day4b extends Day4 {
	
	static private Logger logger = Logger.getLogger(Day4b.class.getName());
	
	@Override
	public BingoGrid playGame() {
		for (int i=0; i<bingoNumbers.length; ++i) {
			int call = bingoNumbers[i];
			for (BingoGrid grid : grids) {
				if (grid.won) {
					continue;
				}
				grid.playNumber(call);
				if (grid.bingo()) {
					boolean last = isLastOne(grid);
					if (last) {
						winningNumber = call;
						return grid;
					}
				}
			}
		}
		logger.error("Played all numbers with no winner!");
		return null;
	}
	
	public boolean isLastOne(BingoGrid g1) {
		for (BingoGrid grid : grids) {
			if (g1 == grid) {
				continue;
			}
			if (!grid.won) {
				logger.info("More than 1 grid remaining");
				return false;
			}
		}
		return true;
	}
	
}
