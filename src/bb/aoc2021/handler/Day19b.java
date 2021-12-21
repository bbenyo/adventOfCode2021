package bb.aoc2021.handler;

import org.apache.log4j.Logger;

public class Day19b extends Day19 {
	static private Logger logger = Logger.getLogger(Day19b.class.getName());
	
	@Override
	public void output() {
		super.output();
		
		int maxDist = 0;
		String m1Name = "";
		String m2Name = "";
		for (Scanner s1 : scanners) {
			for (Scanner s2 : scanners) {
				if (s1 == s2) {
					continue;
				}
				int s12Dist = s1.loc.distanceTo(s2.loc);
				if (s12Dist > maxDist) {
					maxDist = s12Dist;
					m1Name = s1.name;
					m2Name = s2.name;
				}
			}
		}
		
		logger.info("Max Distance: "+maxDist);
		logger.info("\t Between "+m1Name+" and "+m2Name);
	}
}
