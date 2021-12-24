package bb.aoc2021.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.Location3D;
import bb.aoc2021.handler.Day22.Cube;

/** 
 * Well this isn't going to work!  We cant simulate every voxel, there are too many
 *   Instead lets determine the intersection of 2 cubes and create sub-cubes that don't overlap
 *   Then for every sub-cube that doesn't overlap, we can set it on or off
 *   Then we just count the volume of all on cubes
 *   Could generate a lot of subcubes, but not a quintillion
 * 
 * @author bbenyo
 *
 */
public class Day22b extends Day22 {
	static private Logger logger = Logger.getLogger(Day22b.class.getName());
	
	@Override
	protected boolean validVoxel(Location3D voxel) {
		return true;
	}
	
	@Override
	public void output() {
		validArea = null; // Everywhere is valid
		
		List<Cube> finalCubes = new ArrayList<Cube>();
		
		for (Cube c : cubes) {
			logger.info("Handling cube: "+c);
			// Do we intersect with any final on cube?
			List<Cube> newCubes = new ArrayList<>();
			boolean done = false;
			
			while (!done) {
				newCubes.clear();
				Cube remove = null;
				for (Cube fc : finalCubes) {
					if (!c.noOverlap(fc)) {
						// new cube overlaps an existing cube
						// Cut the existing cube so it doesn't overlap
						newCubes = fc.cutOverlapping(c);
						remove = fc;
						logger.info("Added "+newCubes+" new cut cubes for overlap with "+c.name);
						break;
					}
				}
				if (newCubes.isEmpty() && remove == null) {
					done = true;
				} else {
					if (!finalCubes.remove(remove)) {
						logger.error("Unable to remove "+remove);
					}
					finalCubes.addAll(newCubes);
				}
			}
			
			if (c.on) {
				finalCubes.add(c);
			}
		}
		
		long onCubes = 0;
		for (Cube c1 : finalCubes) {
			if (!c1.on) {
				logger.error("Error, off cube in the list");
			}
			onCubes += c1.volume();
			for (Cube c2 : finalCubes) {
				if (c1 == c2) {
					continue;
				}
				if (!c1.noOverlap(c2)) {
					logger.error("Overlapping cubes in the final set: "+c1+" and "+c2);
				}
			}
		}
		
		logger.info("Final on cubes: "+onCubes);
	}
}