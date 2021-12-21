package bb.aoc2021;

public class Location3D extends Location {
	
	int z;
	
	Location3D truePos;
	
	public Location3D(int x, int y, int z) {
		super(x, y);
		this.z = z;
	}
	
	public Location3D(String line) throws IllegalArgumentException {
		super(0,0);
		String[] coords = line.split(",");
		if (coords.length != 3) {
			throw new IllegalArgumentException("Line doesn't have 3 coordinates: "+coords.length);
		}
		try {
			x = Integer.parseInt(coords[0]);
			y = Integer.parseInt(coords[1]);
			z = Integer.parseInt(coords[2]);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(ex.toString());
		}
	}
	
	@Override
	public String toString() {
		return x+","+y+","+z;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	/**
	 * Assume this location and rLoc are the same point
	 *   Return the location of the sensor that detected rLoc that would make these two locations the same
	 * Ignores orientation, assumes same orientation as the sensor that detected this
	 * @param rLoc
	 * @return
	 */
	public Location3D relativeTo(Location3D rLoc) {
		return new Location3D(x - rLoc.x, y - rLoc.y, z - rLoc.z);		
	}
	
	/**
	 * Given this location that represents a sensor detection, and the passed in location of the sensor that detected it
	 *   What is our true location (relative to 0,0,0)?
	 * @param sensorLoc
	 * @return
	 */
	public Location3D trueLoc(Location3D sensorLoc) {
		return new Location3D(x + sensorLoc.x, y + sensorLoc.y, z + sensorLoc.z);		
	}
	
	/**
	 * Manhattan distance
	 * @param otherLoc
	 * @return
	 */
	public int distanceTo(Location3D otherLoc) {
		return Math.abs(x - otherLoc.x) + Math.abs(y - otherLoc.y) + Math.abs(z - otherLoc.z); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location3D other = (Location3D) obj;
		if (z != other.z)
			return false;
		return true;
	}
	
	

}
