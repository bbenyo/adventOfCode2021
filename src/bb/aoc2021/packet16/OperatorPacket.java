package bb.aoc2021.packet16;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import bb.aoc2021.UsefulBitSet;
import bb.aoc2021.Utilities;

public class OperatorPacket extends Packet {
	static private Logger logger = Logger.getLogger(OperatorPacket.class.getName());
	boolean lengthTypeId = false;
	List<Packet> subpackets;
	boolean valueComputed = false;
	
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
			logger.info("Parsing Operator packet "+this.typeId+", subpacket length: "+subPacketLength);
			long readPacketLen = 0;
			bits.setPos(pos+16);
			length += 15;
			while (readPacketLen < subPacketLength) {
				Packet sPacket = Packet.parsePacket(bits);
				subpackets.add(sPacket);
				readPacketLen += sPacket.getLength();
			}
			length += readPacketLen;
		} else {
			// Next 11 bits are the number of subpackets
			long subPacketCount = bits.toLongRange(pos+1, pos+11);
			logger.info("Parsing Operator packet "+this.typeId+", subpacket count: "+subPacketCount);
			bits.setPos(pos+12);
			length += 11;
			for (int i=0; i<subPacketCount; ++i) {
				Packet sPacket = Packet.parsePacket(bits);
				subpackets.add(sPacket);
				length += sPacket.getLength();
			}
		}
		
		getValue();
		logger.info("Operator packet value: "+value);
	}
	
	/**
	 * Lazy compute, wait until we need to get the value, if it's not set, then compute it
	 **/
	@Override
	public long getValue() {
		if (this.valueComputed) {
			return value;
		}
		
		List<Long> subValues = new ArrayList<Long>();
		for (Packet sub : subpackets) {
			subValues.add(sub.getValue());
		}
		
		value = 0;
		switch(typeId) {
		case 0 : 
			subValues.forEach(v -> value += v); break;
		case 1 :
			value = 1;
			subValues.forEach(v -> value *= v); 
			break;
		case 2 :
			value = subValues.stream().min(Comparator.comparing(Long::valueOf)).get();
			break;
		case 3 : 
			value = subValues.stream().max(Comparator.comparing(Long::valueOf)).get();
			break;
		case 5 : 
			if (subValues.size() != 2) {
				logger.error("GreaterThan packet with "+subValues+" subpackets, invalid!");
				value = 0;
			} else if (subValues.get(0) > subValues.get(1)) {
				value = 1;
			} else {
				value = 0;
			}
			break;
		case 6 : 
			if (subValues.size() != 2) {
				logger.error("GreaterThan packet with "+subValues+" subpackets, invalid!");
				value = 0;
			} else if (subValues.get(0) < subValues.get(1)) {
				value = 1;
			} else {
				value = 0;
			}
			break;
		case 7 : 
			if (subValues.size() != 2) {
				logger.error("GreaterThan packet with "+subValues+" subpackets, invalid!");
				value = 0;
			} else if (subValues.get(0).equals(subValues.get(1))) {
				value = 1;
			} else {
				value = 0;
			}
			break;
		}
		
		valueComputed = true;
		logger.info(this.toString());
		return value;
	}
	
	@Override
	public int versionCount() {
		int v = this.version;
		for (Packet sPacket : subpackets) {
			v += sPacket.versionCount();
		}
		return v;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		switch (typeId) {
		case 0 : sb.append("SUM"); break;
		case 1 : sb.append("PRODUCT"); break;
		case 2 : sb.append("MIN"); break;
		case 3 : sb.append("MAX"); break;
		case 5 : sb.append("GT"); break;
		case 6 : sb.append("LT"); break;
		case 7 : sb.append("EQ"); break;
		}
		sb.append(" ");
		for (Packet sPacket : subpackets) {
			sb.append(sPacket.getValue());
			sb.append(" ");
		}
		sb.append(" = "+value);
		return sb.toString();
	}
}
