package bb.aoc2021.handler;

import bb.aoc2021.Location3D;

public class Day22b extends Day22 {
	
	@Override
	protected boolean validVoxel(Location3D voxel) {
		return true;
	}
	
	@Override
	public void output() {
		validArea = null; // Everywhere is valid
		super.output();
	}
}
