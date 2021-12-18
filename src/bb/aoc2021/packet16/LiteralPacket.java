package bb.aoc2021.packet16;

import bb.aoc2021.UsefulBitSet;

public class LiteralPacket extends Packet {
	
	long value;
	
	public LiteralPacket(short v, short t) {
		super(v, t);
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	public void parseBody(UsefulBitSet bits) {
		// Read 5 bits at a time
		int pos = bits.getPos();
		int startPos = pos;
		UsefulBitSet literal = new UsefulBitSet();
		int lPos = 0;
		while (pos < bits.length()) {
			// Read the next 5 bits
			boolean more = bits.get(pos);
			for (int i=pos+1; i<=pos+4; ++i) {
				if (bits.get(i)) {
					literal.set(lPos);
				}
				lPos++;
			}
			pos += 5;
			literal.setRealLength(lPos);
			if (!more) {
				break;
			}
		}
		bits.setPos(pos);
		length = (pos - startPos) + 6;
		value = literal.toLong();
	}
	
	@Override
	public int versionCount() {
		return this.version;
	}
	
}
