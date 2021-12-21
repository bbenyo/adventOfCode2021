package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Location3D;
import bb.aoc2021.Utilities;

public class Day19 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day19.class.getName());
	
	int neededToAlign = 12;
	
	/**
	 * Rotations
	 * 
	 * x, y, z
	 * x, y, -z
	 * x, -y, z
	 * x, -y, -z
	 * -x, y, z
	 * -x, y, -z
	 * -x, -y, z
	 * -x, -y, -z
	 * 
	 * Swap x,y
	 * y, x, z
	 * y, x, -z
	 * y, -x, z
	 * y, -x, -x
	 * -y, x, z
	 * -y, x, -z
	 * -y, -x, z
	 * -y, -x, -z
	 * 
	 * Swap x,z
	 * z, y, x
	 * 
	 * Swap y,z
	 * x, z, y
	 * 
	 * Swap x,y, swap y,z
	 * y, z, x
	 * 
	 * z, x, y
	 * Some of these may end up being the same, but we'll just power through =)
	 */

	public class Scanner {
		Location3D loc;
		String name;

		List<Location3D> beacons;
		List<Location3D> beaconsTrue;
		
		// Temporary storage for rotated locations
		List<Location3D> rotated;
		
		public Scanner(String n) {
			this.name = n;
			loc = null;
			beacons = new ArrayList<Location3D>();
			beaconsTrue = new ArrayList<Location3D>();
			rotated = new ArrayList<Location3D>();
		}
		
		public void addBeacon(Location3D b) {
			beacons.add(b);
		}
		
		public void align() {
			beaconsTrue.clear();
			for (Location3D b : rotated) {
				beaconsTrue.add(b.trueLoc(loc));
			}
			
			beacons.clear();
			beacons.addAll(rotated);
		}
		
		// Could be a lot more cleaner here, but c'est la vie
		public void rotate(boolean xPos, boolean yPos, boolean zPos, int swapIdx) {
			rotated.clear();
			for (Location3D b: beacons) {
				int bx = b.getX();
				if (!xPos) {
					bx = -bx;
				}
				
				int by = b.getY();
				if (!yPos) {
					by = -by;
				}
				
				int bz = b.getZ();
				if (!zPos) {
					bz = -bz;
				}
				
				// X,Z,Y swap
				int tmpx, tmpy;
				switch (swapIdx) {
				case 0 :
					break;
				case 1: // xzy
					tmpy = by;
					by = bz;
					bz = tmpy;
					break;
				case 2 : // yxz Swap
					tmpx = bx;
					bx = by;
					by = tmpx;
					break;
				case 3 : // yzxSwap
					tmpx = bx;
					bx = by;
					by = bz;
					bz = tmpx;
					break;
				case 4 : // zxySwap
				    tmpx = bx;
					bx = bz;
					bz = by;
					by = tmpx;
					break;
				case 5 : // zyxSwap
					tmpx = bx;
					bx = bz;
					bz = tmpx;
					break;
				default :
					break;
				}
				
				Location3D br = new Location3D(bx, by, bz);
				rotated.add(br);
			}
			
		}
		
		public String toString() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Scanner other = (Scanner) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private Day19 getEnclosingInstance() {
			return Day19.this;
		}
	
	}
	
	
	List<Scanner> scanners = new ArrayList<Scanner>();
	Scanner curScanner = null;

	@Override
	public void handleInput(String line) {
		if (line.trim().length() == 0) {
			return;
		}
		if (line.startsWith("---")) {
			String scannerName = Utilities.getStringBetween(line, "scanner ", " -", 0);
			Scanner s1 = new Scanner(scannerName);
			if (scannerName.equals("0")) {
				// We know where scanner 0 is
				s1.loc = new Location3D(0,0,0);				
			}
			curScanner = s1;
			scanners.add(s1);			
		} else if (curScanner != null) {
			Location3D beacon = new Location3D(line);
			curScanner.addBeacon(beacon);
			if (curScanner.name.equals("0")) {
				curScanner.beaconsTrue.add(beacon);
			}
		} else {
			logger.error("Logic error in parsing input: "+line);
		}
	}
	
	/**
	 * Try to align 2 scanners
	 *    We need to know where s1 is, and we shouldn't know where s2 is
	 *    If we can align them, then we figure out where s2 is, and return true
	 * @param s1
	 * @param s2
	 * @return
	 */
	public boolean align(Scanner s1, Scanner s2) {
		if (s1.loc == null) {
			logger.error("Unable to align "+s1.name+" and "+s2.name+", s1 location is unknown");
			return false;
		}
		if (s2.loc != null) {
			logger.error("No need to align "+s1.name+" and "+s2.name+", s2 location is known!");
			return false;
		}
		
		// Try to align s2 with s1.
		// For every beacon in s1, try to align s2 to that beacon
		//   See if there are at least 11 other beacons that line up with the proposed loc for s2
	
		// Try all 48 rotations, could be smart and skip half of them, but that's only a factor of 2 reduction in time	
		for (int swaps = 0; swaps < 6; ++swaps) {
			for (int rots = 0; rots < 8; ++rots) {
				
				// Rotate S2
				rotation(s2, rots, swaps);
				
				// Let's try b2 = b1
				//  If this is right, then we can get the location of s2, and check to see if there are 11 other aligned beacons
				
				for (Location3D b1 : s1.beacons) {
					for (Location3D b2 : s2.rotated) {
						Location3D s2True = b1.relativeTo(b2);
						if (align(s1, s2, s2True)) {
							Location3D s2Loc = s2True.trueLoc(s1.loc);
							s2.loc = s2Loc;
							logger.info("Setting scanner "+s2.name+" location to "+s2.loc);
							s2.align();
							return true;
						}
					}
				}
			}
		}
		logger.info(s1.name+" and "+s2.name+" don't align");
		return false;
	}
	
	protected void rotation(Scanner s, int signs, int swaps) {
		switch (signs) {
		case 0 : s.rotate(true, true, true, swaps); break;
		case 1 : s.rotate(true, true, false, swaps); break;
		case 2 : s.rotate(true, false, true, swaps); break;
		case 3 : s.rotate(true, false, false, swaps); break;
		case 4 : s.rotate(false, true, true, swaps); break;
		case 5 : s.rotate(false, true, false, swaps); break;
		case 6 : s.rotate(false, false, true, swaps); break;
		case 7 : s.rotate(false, false, false, swaps); break;
		default: logger.error("Unknown rotation: "+signs+" swap: "+swaps);
		}
	}
	
	/**
	 * Try to align scanner s1 with a known true position, with s2, assuming s2 is at s2True
	 * @param s1
	 * @param s2
	 * @param b2True
	 * @return
	 */
	protected boolean align(Scanner s1, Scanner s2, Location3D s2True) {
		Set<Location3D> b2TrueLocs = new HashSet<Location3D>();
		for (Location3D b2 : s2.rotated) {
			Location3D b2True = b2.trueLoc(s2True);
			b2TrueLocs.add(b2True);
		}
			
		// If these align, then at least 12 (neededToAlign) beacons will match
		int aligned = 0;
		List<Location3D> alignedBeacons = new ArrayList<Location3D>();
		for (Location3D b1b : s1.beacons) {
			if (b2TrueLocs.contains(b1b)) {
				aligned++;
				alignedBeacons.add(b1b);
			}
		}
		
		if (aligned >= neededToAlign) {
			logger.info("Aligned "+s1.name+" and "+s2.name);
			for (Location3D ab : alignedBeacons) {
				logger.info("\tAligned beacon: "+ab);
			}
			return true;
		}
		return false;
	}
	
	public boolean alignAll() {
		List<Scanner> unaligned = new ArrayList<Scanner>();
		
		// Worklist is the set of sensors we know the location of and need to align other scanners to
		List<Scanner> workList = new ArrayList<Scanner>();
		
		for (Scanner s1 : scanners) {
			if (s1.loc != null) {
				// Should be just scanner 0, we know it's location as 0,0,0
				workList.add(s1);
			} else {
				unaligned.add(s1);
			}
		}
		
		while (!unaligned.isEmpty() && !workList.isEmpty()) {
			Scanner s1 = workList.remove(0);
			
			List<Scanner> newAligned = new ArrayList<Scanner>();
			for (Scanner s2 : unaligned) {
				if (align(s1, s2)) {
					newAligned.add(s2);
				}
			}
			
			unaligned.removeAll(newAligned);
			workList.addAll(newAligned);
		}
		
		if (unaligned.isEmpty()) {
			logger.info("Aligned everything!");
			return true;
		} else {
			logger.error("Still "+unaligned.size()+" scanners unaligned!");
			for (Scanner u : unaligned) {
				logger.info("Unaligned: "+u.name);
			}
			return false;
		}				
	}

	@Override
	public void output() {
		logger.info("Scanner count: "+scanners.size());
		for (Scanner s : scanners) {
			logger.info("Scanner: "+s.name);
			for (Location3D b : s.beacons) {
				logger.info("\t"+b);
			}
		}
		
		boolean alignAll = alignAll();
		Set<Location3D> uBeacons = new HashSet<Location3D>();
		if (alignAll) {
			logger.info("Aligned all sensors, counting unique beacons");
			for (Scanner s : scanners) {
				for (Location3D tb : s.beaconsTrue) {
					uBeacons.add(tb);
				}
			}
		}
		
		for (Location3D tb : uBeacons) {
			logger.info("True Loc beacon: "+tb);
		}
		logger.info("True location beacon count: "+uBeacons.size());
		
	}

}
