package bb.aoc2021.packet16;

import org.apache.log4j.Logger;

import bb.aoc2021.UsefulBitSet;

public class LiteralPacket extends Packet {
	static private Logger logger = Logger.getLogger(LiteralPacket.class.getName());
	
	public LiteralPacket(short v, short t) {
		super(v, t);
	}

	public void parseBody(UsefulBitSet bits) {
		// Read 5 bits at a time
		int pos = bits.getPos();
		int startPos = pos;
		UsefulBitSet literal = new UsefulBitSet();
		int lPos = 0;
		while (true) {
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
		if (lPos > 63) {
			logger.warn("Too large");
		}
		bits.setPos(pos);
		length = (pos - startPos) + 6;
		value = literal.toLong();
		if (value == 50) {
			logger.info("pt");
		}
	}
	
	@Override
	public int versionCount() {
		return this.version;
	}
	
	public String toString() {
		return "Literal: "+value;
	}
	
}
