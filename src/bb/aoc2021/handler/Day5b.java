package bb.aoc2021.handler;

public class Day5b extends Day5 {
	
	@Override
	protected void addLinesToWorld() throws Exception {
		for (Line l : lines) {
			l.addToWorldHorV(world);
			l.addToWorldDiag45(world);
		}
	}
}
