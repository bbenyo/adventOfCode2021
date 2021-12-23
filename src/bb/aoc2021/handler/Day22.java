package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Location3D;
import bb.aoc2021.Utilities;

/**
 * Idea is to check each voxel (x,y,z) in each ON cube
 *    Look backwards for previous steps, if this voxel was inside a previous ON cube, we already considered it and can ignore
 *    If not, look forwards, if it is inside any subsequent OFF cube, then turn it off and
 *       Look for any subsequent ON cube it's inside of that turns it back on, etc.
 *       
 * Count the voxels that end up on
 * 
 * @author bbeny
 *
 */
public class Day22 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day22.class.getName());
	
	public class Cube {
		// MinX,MaxX are inclusive, so x=1..3 means x=1 and x=3 are part of the cube
		int minX, minY, minZ;
		int maxX, maxY, maxZ;
		boolean on;
		String name;
		
		public Cube(int x1, int x2, int y1, int y2, int z1, int z2, boolean onOff, String name) {
			this.minX = x1;
			this.minY = y1;
			this.minZ = z1;
			this.maxX = x2;
			this.maxY = y2;
			this.maxZ = z2;
			this.on = onOff;
			this.name = name;
		}
		
		public boolean inside(Location3D loc) {
			if (loc.getX() >= minX && loc.getX() <= maxX &&
				loc.getY() >= minY && loc.getY() <= maxY &&
				loc.getZ() >= minZ && loc.getZ() <= maxZ) {
				return true;
			}
			return false;
		}
		
		public String toString() {
			return name+" x="+minX+".."+maxX+" y="+minY+".."+maxY+" z="+minZ+".."+maxZ;
		}
		
		public boolean noOverlap(Cube c2) {
			if (maxX < c2.minX || minX > c2.maxX) {
				return true;
			}
			if (maxY < c2.minY || minY > c2.maxY) {
				return true;
			}
			if (maxZ < c2.minZ || minZ > c2.maxZ) {
				return true;
			}
			return false;
		}
	}
	
	public List<Cube> cubes = new ArrayList<>();

	protected Cube validArea = new Cube(-50, 50, -50, 50, -50, 50, true, "Valid");
		
	@Override
	public void handleInput(String line) {
		boolean on = line.startsWith("on");
		
		Integer minX = Utilities.parseIntOrNull(Utilities.getStringBetween(line, "x=", "..", 0));
		Integer maxX = Utilities.parseIntOrNull(Utilities.getStringBetween(line, "..", ",", 0));
		Integer minY = Utilities.parseIntOrNull(Utilities.getStringBetween(line, "y=", "..", 0));
		Integer maxY = Utilities.parseIntOrNull(Utilities.getStringBetween(line, "..", ",", line.indexOf("y=")));
		Integer minZ = Utilities.parseIntOrNull(Utilities.getStringBetween(line, "z=", "..", 0));
		Integer maxZ = Utilities.parseIntOrNull(line.substring(line.lastIndexOf("..") + 2));
		
		if (minX == null || maxX == null || minY == null || maxY == null || minZ == null || maxZ == null) {
			logger.error("Unable to parse "+line);
			return;
		}
		
		Cube c = new Cube(minX.intValue(), maxX.intValue(), minY.intValue(), maxY.intValue(), minZ.intValue(), maxZ.intValue(), on,
				"Cube"+(cubes.size()+1));
		cubes.add(c);
	}

	/***
	 *  For the given Cube, count the number of voxels that will still be ON in the end
	 *    For an ON cube, for each voxel, check each following OFF cube to see if the voxel is inside, if so, it turns off
	 *      And we then look for a following ON cube that turns it back on.
	 *    Also for an ON cube, we look backwards for previous ON cubes.  For each voxel, if it was inside a previous ON
	 *      cube, then we already considered it and can skip it to avoid double counting
	 */
	public long countOnCubes(Cube c, int index) {
		if (validArea != null && c.noOverlap(validArea)) {
			return 0;
		}
		long onCubes = 0;
		
		List<Cube> relevantBeforeCubes = new ArrayList<Cube>();
		for (int l = 0; l<index; ++l) {
			Cube pCube = cubes.get(l);
			if (pCube.on && !pCube.noOverlap(c)) {
				relevantBeforeCubes.add(pCube);
			}
		}
		
		List<Cube> relevantAfterCubes = new ArrayList<Cube>();
		for (int l = index+1; l<cubes.size(); ++l) {
			Cube pCube = cubes.get(l);
			if (!pCube.noOverlap(c)) {
				relevantAfterCubes.add(pCube);
			}
		}

		if (relevantBeforeCubes.isEmpty() && relevantAfterCubes.isEmpty()) {
			onCubes += (c.maxX - c.minX) * (c.maxY - c.minY) * (c.maxZ - c.minZ);
			return onCubes;
		}
				
		for (int i=c.minX; i<=c.maxX; ++i) {
			for (int j=c.minY; j<=c.maxY; ++j) {
				for (int k=c.minZ; k<=c.maxZ; ++k) {
					Location3D voxel = new Location3D(i,j,k);
					if (!validVoxel(voxel)) {
						continue;
					}
					// Look backwards for each v, did we already figure out whether it's on or off?
					// If it was inside any previous On cube then yes
					boolean skip = false;
					for (Cube pCube : relevantBeforeCubes) {
						if (pCube.on && pCube.inside(voxel)) {
							skip = true;
							break;
						}
					}
					if (skip) {
						continue;
					}
					
					// If not, simulate this voxel going forward, do we turn it off (then back on, then off, etc?)
					boolean voxelOn = true;
					for (Cube nCube : relevantAfterCubes) {
						if (voxelOn != nCube.on && nCube.inside(voxel)) {
							voxelOn = !voxelOn;							
						}
					}
					
					if (voxelOn) {
						// logger.info("On cube: "+voxel);
						onCubes++;
					}
				}
			}						
		}
		return onCubes;		
	}
		
	protected boolean validVoxel(Location3D voxel) {
		return validArea.inside(voxel);
	}
	
	@Override
	public void output() {
		long onCubes = 0;
		for (int i=0; i<cubes.size(); ++i) {
			Cube c = cubes.get(i);
			logger.info(c);
			if (c.on) {
				onCubes += countOnCubes(c, i);
			}			
		}
		
		logger.info("Total ON cubes: "+onCubes);
	}

}
