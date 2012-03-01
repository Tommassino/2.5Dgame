package cz.witzany.gamev2.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Message {

	static final char[] HEX_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private int[] address;
	private byte[] data;

	public int getAddressLenght() {
		return address.length;
	}

	public int getAddress(int level) {
		return address[level];
	}

	public byte[] getData() {
		return data;
	}

	public static Message readMessage(DataInputStream in) throws IOException {
		int addressLength = in.readInt();
		Message out = new Message();
		out.address = new int[addressLength];
		for (int i = 0; i < addressLength; i++)
			out.address[i] = in.readInt();
		int dataLength = in.readInt();
		out.data = new byte[dataLength];
		for (int i = 0; i < dataLength; i++)
			out.data[i] = in.readByte();
		return out;
	}

	public void writeMessage(DataOutputStream out) throws IOException {
		out.writeInt(address.length);
		for (int a : address)
			out.writeInt(a);
		out.writeInt(data.length);
		out.write(data);
	}

	public static Message buildMessage(String address, byte[] data) {
		ArrayList<Integer> adrs = new ArrayList<Integer>();
		StringTokenizer tokenizer = new StringTokenizer(address, ".");
		while (tokenizer.hasMoreTokens()) {
			String num = tokenizer.nextToken();
			adrs.add(Integer.parseInt(num));
		}
		Message msg = new Message();
		msg.address = new int[adrs.size()];
		for (int i = 0; i < msg.address.length; i++)
			msg.address[i] = adrs.get(i);
		msg.data = data;
		return msg;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Message ");
		for (int a : address)
			sb.append(a).append(':');
		sb.append("->");
		for (byte b : data)
			sb.append(byteString(b));
		return sb.toString();
	}

	public static String byteString(byte b) {
		int v = b & 0xFF;
		return HEX_TABLE[v >> 4] + "" + HEX_TABLE[v & 0x0F];
	}
}
