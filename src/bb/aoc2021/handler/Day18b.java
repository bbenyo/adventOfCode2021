package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.snailfish.SnailfishNumber;

public class Day18b extends Day18 {
	
	static private Logger logger = Logger.getLogger(Day18b.class.getName());
	

	@Override
	public void output() {
		long maxMag = 0;
		SnailfishNumber f1 = null, f2 = null;
		
		// Add every combo.  Addition is not commutative, so we gotta try them all
		for (SnailfishNumber n : snailfish) {
			for (SnailfishNumber n2 : snailfish) {
				if (n.equals(n2)) {
					continue;
				}
				SnailfishNumber a1 = n.add(n2);
				long mag = a1.getMagnitude();
				if (mag > maxMag) {
					maxMag = mag;
					f1 = n;
					f2 = n2;
				}
			}
		}
		
		logger.info("Final Max Magnitude: "+maxMag);
		logger.info("Add: "+f1+" + "+f2);
		
	}
}
