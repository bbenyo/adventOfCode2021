package bb.aoc2021.snailfish;

import org.apache.log4j.Logger;

public class SnailfishLiteral extends SnailfishElement {
	static private Logger logger = Logger.getLogger(SnailfishLiteral.class.getName());
	long value;
	
	public SnailfishLiteral(SnailfishNumber parent) {
		super(parent);
	}
	
	public SnailfishLiteral(long val) {
		super(null);
		this.value = val;		
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public long getMagnitude() {
		return value;
	}
	
	@Override
	public boolean tryExplode(int level) {
		return false;
	}
	
	@Override
	public boolean trySplit() {
		if (value >= 10) {
			split();
			return true;
		}
		return false;
	}
	
	/**
	 * To split a regular number, replace it with a pair; the left element of the pair should be the 
	 *   regular number divided by two and rounded down, while the right element of the pair should be 
	 *   the regular number divided by two and rounded up. 
	 *   For example, 10 becomes [5,5], 11 becomes [5,6], 12 becomes [6,6], and so on.
	 * @return
	 */
	public void split() {
		SnailfishNumber newSplit = new SnailfishNumber(parent);
		long leftVal = Math.floorDiv(value, 2);
		SnailfishLiteral newLeft = new SnailfishLiteral(leftVal);
		newSplit.setLeft(newLeft);
		long rightVal = value - leftVal;
		SnailfishLiteral newRight = new SnailfishLiteral(rightVal);
		newSplit.setRight(newRight);
		if (parent == null) {
			logger.warn("null parent for a split?");
		}
		parent.replace(this, newSplit);
		logger.info("Split "+value+" into "+newSplit);
	}

	public SnailfishLiteral getLeftmostLiteral() {
		return this;
	}
	
	public SnailfishLiteral getRightmostLiteral() {
		return this;
	}
	
	protected SnailfishElement clone() {
		SnailfishLiteral l1 = new SnailfishLiteral(null);
		l1.setValue(value);
		return l1;
	}
	
}
