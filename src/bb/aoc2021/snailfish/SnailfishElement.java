package bb.aoc2021.snailfish;

public abstract class SnailfishElement {
	
	SnailfishNumber parent;
	
	public SnailfishElement(SnailfishNumber parent) {
		this.parent = parent;
	}
	
	public SnailfishNumber getParent() {
		return parent;
	}

	public void setParent(SnailfishNumber parent) {
		this.parent = parent;
	}

	abstract public long getMagnitude();
	
	abstract boolean tryExplode(int level);

	abstract boolean trySplit();
	
	abstract SnailfishLiteral getRightmostLiteral();
	
	abstract SnailfishLiteral getLeftmostLiteral();
	
	protected abstract SnailfishElement clone();
	
	
}
