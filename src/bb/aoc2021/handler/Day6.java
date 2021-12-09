package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day6 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day6.class.getName());
	
	// Stores the number of lantern fish with internal timer of the index
	// e.g. lfc[2] stores the number of lantern fish with internal timer = 2
	long[] lanternFishCounts;

	@Override
	public void handleInput(String line) {
		String[] lFish = line.split(",");
		lanternFishCounts = new long[9];
		for (int i=0; i<9; ++i) {
			lanternFishCounts[i] = 0;
		}
		for (String lf : lFish) {
			try {
				int iTimer = Integer.parseInt(lf);
				if (iTimer < 0 || iTimer > 8) {
					logger.error("Lantern Fish with invalid timer: "+iTimer);					
				} else {
					lanternFishCounts[iTimer] += 1;
				}
			} catch (NumberFormatException ex) {
				logger.error(ex.toString(), ex);
			}
		}		
	}
	
	protected long countFish() {
		long count = 0;
		for (int i=0; i<9; ++i) {
			count += lanternFishCounts[i];
		}
		return count;
	}
	
	protected void simulate(int days) {
		for (int i=0; i<days; ++i) {
			// all Fish with count 3 become fish with count 2, etc
			long [] newDay = new long[9];
			for (int j=0; j<8; ++j) {
				newDay[j] = lanternFishCounts[j+1];
			}
			// 0's become 6's 
			newDay[6] += lanternFishCounts[0];
			// And 0's spawn 8's
			newDay[8] = lanternFishCounts[0];
			
			for (int j=0; j<9; ++j) {
				lanternFishCounts[j] = newDay[j];
			}
			logger.info("Day "+i+": fish count: "+countFish());
		}
	}

	@Override
	public void output() {
		simulate(80);
		logger.info("Final Lantern fish count after 80 days: "+countFish());
		
	}	

}
