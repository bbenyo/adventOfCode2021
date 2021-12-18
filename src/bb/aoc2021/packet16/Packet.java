package bb.aoc2021.packet16;

import org.apache.log4j.Logger;

import bb.aoc2021.UsefulBitSet;

public abstract class Packet {
	static private Logger logger = Logger.getLogger(Packet.class.getName());
	protected short version;
	protected short typeId;
	
	protected int length = 0;
	
	public Packet(short v, short t) {
		this.version = v;
		this.typeId = t;
		length = 6;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	/*
	 * Starting at position in bitset, parse the next packet
	 */
	static public Packet parsePacket(UsefulBitSet bits) {
		// Read the header
		int pos = bits.getPos();
		short v1 = (short)bits.toIntRange(pos+0, pos+2);
		short t1 = (short)bits.toIntRange(pos+3, pos+5);
		bits.setPos(pos+6);
		logger.info("Parsed packet version "+v1+" type: "+t1);
				
		if (t1 == 4) {
			LiteralPacket lPacket = new LiteralPacket(v1, t1);
			lPacket.parseBody(bits);
			logger.info("  Parsed LiteralPacket: "+lPacket.value+" size: "+lPacket.length);
			return lPacket;
		} else {
			OperatorPacket oPacket = new OperatorPacket(v1, t1);
			oPacket.parseBody(bits);
			logger.info("  Parsed OperatorPacket subpackets: "+oPacket.subpackets.size()+" size: "+oPacket.length);
			return oPacket;
		}
	}
	
	abstract public int versionCount();	
}
