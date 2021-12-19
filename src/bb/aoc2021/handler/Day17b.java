package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.Location;

public class Day17b extends Day17 {
	static private Logger logger = Logger.getLogger(Day17b.class.getName());
	
	@Override
	public void output() {
		int minXVelocity = getMinXVelocity();
		logger.info("Min X Velocity to hit "+target+" is "+minXVelocity);
		
		// Brute Force-ish
		// if VelocityX > maxX, then we'll overshoot in turn 1, so try until then
		for (int vx = minXVelocity; vx <= target.maxX; ++vx) {
			simulateVelX(vx);
		}
		
		for (Location vLoc : targetHits) {
			logger.info("Target hit with velocity: "+vLoc);
		}
		logger.info("Target hits: "+targetHits.size());
	}
}
