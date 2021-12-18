package bb.aoc2021.packet16;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2021.UsefulBitSet;

public class OperatorPacket extends Packet {
	static private Logger logger = Logger.getLogger(OperatorPacket.class.getName());
	boolean lengthTypeId = false;
	List<Packet> subpackets;
	
	public OperatorPacket(short v, short t) {
		super(v, t);
		subpackets = new ArrayList<Packet>();
	}

	public List<Packet> getSubpackets() {
		return subpackets;
	}

	public void setSubpackets(List<Packet> subpackets) {
		this.subpackets = subpackets;
	}
	
	public void parseBody(UsefulBitSet bits) {
		int pos = bits.getPos();
		lengthTypeId = bits.get(pos);
		length = 7;
		if (!lengthTypeId) {
			// Next 15 bits are the subpacket length
			long subPacketLength = bits.toLongRange(pos+1, pos+15);
			logger.info("Parsing Operator packet of type 0, subpacket length: "+subPacketLength);
			long readPacketLen = 0;
			bits.setPos(pos+16);
			while (readPacketLen < subPacketLength) {
				Packet sPacket = Packet.parsePacket(bits);
				subpackets.add(sPacket);
				readPacketLen += sPacket.getLength();
			}
			length += readPacketLen;
		} else {
			// Next 11 bits are the number of subpackets
			long subPacketCount = bits.toLongRange(pos+1, pos+11);
			logger.info("Parsing Operator packet of type 1, subpacket count: "+subPacketCount);
			bits.setPos(pos+12);
			for (int i=0; i<subPacketCount; ++i) {
				Packet sPacket = Packet.parsePacket(bits);
				subpackets.add(sPacket);
				length += sPacket.getLength();
			}
		}
	}
	
	@Override
	public int versionCount() {
		int v = this.version;
		for (Packet sPacket : subpackets) {
			v += sPacket.versionCount();
		}
		return v;
	}
}
