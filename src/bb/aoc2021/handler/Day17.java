package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Location;
import bb.aoc2021.Utilities;

public class Day17 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day17.class.getName());
	
	// Target Area
	public class Target {
		int minX;
		int minY;
		int maxX;
		int maxY;
		
		public Target(int x1, int x2, int y1, int y2) {
			minX = x1;
			maxX = x2;
			minY = y1;
			maxY = y2;
		}
		
		@Override
		public String toString() {
			return "Target: x = "+minX+".."+maxX+" y = "+minY+".."+maxY;
		}
		
		public boolean isInside(Location loc) {
			int x = loc.getX();
			int y = loc.getY();
			return (x >= minX && x <= maxX && y >= minY && y <= maxY);
		}
		
		public boolean isBelow(Location loc) {
			return loc.getY() < minY;
		}
		
		public boolean isAbove(Location loc) {
			return loc.getY() > maxY;
		}
		
		public boolean isPast(Location loc) {
			return loc.getX() > maxX;
		}
	}
	
	Target target;
	
	@Override
	public void handleInput(String line) {
		try {
			int minX = Integer.parseInt(Utilities.getStringBetween(line, "x=", "..", 0));
			int maxX = Integer.parseInt(Utilities.getStringBetween(line, "..", ",", 0));
			int minY = Integer.parseInt(Utilities.getStringBetween(line, "y=", "..", 0));
			int lPos = line.lastIndexOf("..");
			int maxY = Integer.parseInt(line.substring(lPos+2));
			
			target = new Target(minX, maxX, minY, maxY);
			
		} catch (NumberFormatException ex) {
			logger.error(ex.toString(), ex);
		}
	}
	
	/**
	 * Get the lowest X velocity that can possibly hit the target, we can start searching there
	 * 
	 * @return
	 */
	protected int getMinXVelocity() {
		// Could do smarter math here...
		int testX = 1;
		while (true) {
			int maxX = Utilities.sumIntsBetween(0, testX);
			if (maxX >= target.minX) {
				// This X velocity gets us far enough to hit the target
				return testX;
			}
			testX++;
		}
	}
	
	int highestY = Integer.MIN_VALUE;
	
	public enum SimResult {TARGET, PAST_ABOVE, PAST, BELOW, BEFORE}
	
	// Simulate a shot with velocity x/y
	//   Return what happened (hit the target, went past, below, ended before the target, etc.
	//   These maybe could be used to more smartly bound the brute force search
	//   But in practice that doesn't really work well, since you can have trick shots that just happen to fall inside
	//    with a high vel Y, etc.  So it's safer to just try everything
	//   So the SimResults aren't really used
	protected SimResult simulate(int startX, int startY, int velX, int velY) {
		int step = 0;
		Location cur = new Location(startX, startY);
		logger.info("Probe location: "+cur);
		highestY = startY;
		while (true) {
			if (target.isInside(cur)) {
				logger.info("Probe is inside target, highestY = "+highestY);
				return SimResult.TARGET;
			} else if (target.isPast(cur)) {
				logger.info("Probe is past the target =(");
				if (cur.getY() > target.minY) {
					return SimResult.PAST_ABOVE;
				}
				return SimResult.PAST;
			} else if (target.isBelow(cur)) {
				logger.info("Probe is below the target =(");
				return SimResult.BELOW;
			} else if (cur.getX() < target.minX && velX == 0) {
				logger.info("Probe is before the target =(");
				return SimResult.BEFORE;
			}

		    // The probe's x position increases by its x velocity.
			cur.setX(cur.getX() + velX);
			
		    // The probe's y position increases by its y velocity.
			cur.setY(cur.getY() + velY);
			if (cur.getY() > highestY) {
				highestY = cur.getY();
			}
			
		    // Due to drag, the probe's x velocity changes by 1 toward the value 0; that is, it decreases by 1 if it is greater than 0, increases by 1 if it is less than 0, or does not change if it is already 0.
			if (velX > 0) {
				velX --; 
			} else if (velX < 0) {
				velX ++;
			}
			
		    // Due to gravity, the probe's y velocity decreases by 1.
			velY--;
			step++;

			logger.info("Probe location at "+step+": "+cur);
		}
	}

	List<Location> targetHits = new ArrayList<Location>();
	
	protected int simulateVelX(int velX) {
		int curY = target.minY; // Could do more math here and pick a better lower bound...
		
		int bestY = 0;
		int bestVelocityX = 0;
		int bestVelocityY = 0;
		
		while (curY < 500) {
			logger.info("Trying 0,0 -> "+velX+","+curY);
			SimResult result = simulate(0, 0, velX, curY);
			switch (result) {
			case TARGET : 
				if (this.highestY > bestY) {
					bestY = this.highestY;
					bestVelocityX = velX;
					bestVelocityY = curY;
				}
				Location velLoc = new Location(velX, curY);
				targetHits.add(velLoc);
				break;
			case BEFORE :
				// Dropped to 0 velocity before hitting the target, move on to the next velocity
				curY = 1000;
				break;
			case PAST_ABOVE :
				// Y velocity is too high, we're done here
				curY = 1000;
				break;
			case BELOW :
				// we could have passed the target on this attempt, but may just hit it with a higher velocity...
				//  So we're brute forcing, maybe do something more clever...
				break;
			default:
				// no-op
			}
			curY++;
		}
		
		logger.info("Best Y: "+bestY+" at velocity: "+bestVelocityX+","+bestVelocityY);
		this.highestY = bestY;
		return bestVelocityY;
	}

	@Override
	public void output() {
		int minXVelocity = getMinXVelocity();
		logger.info("Min X Velocity to hit "+target+" is "+minXVelocity);

		// For test input
		/**
		simulate(0, 0, 7, 2); // Should be inside
		simulate(0, 0, 6, 3); // Should be inside
		simulate(0, 0, 9, 0); // Should be inside
		simulate(0, 0, 17, -4); // Should be past
		**/
		
		// Brute force for now, maxX velocity shoots us past in 1 step
		
		int bestVelocityY = simulateVelX(minXVelocity);

		simulate(0, 0, minXVelocity, bestVelocityY);
		logger.info("Highest Y = "+highestY);
	}

}
