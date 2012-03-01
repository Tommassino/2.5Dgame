package cz.witzany.gamev2.net.utils;

import cz.witzany.gamev2.net.Message;

public class ByteUtils {

	private static final int INT_MASK_0 = 0x000000FF;
	private static final int INT_MASK_8 = 0x0000FF00;
	private static final int INT_MASK_16 = 0x00FF0000;
	private static final int INT_MASK_24 = 0xFF000000;

	private ByteUtils() {
	}

	public static int readInt(byte[] data, int offset) {
		int result = 0;
		int o = 0;
		int shift = 24;
		int v;
		StringBuilder debug = new StringBuilder();
		while (o <= 3) {
			debug.append(Message.byteString(data[offset + o]));
			v = ((int) data[offset + o]) << shift;
			if (v < 0)
				v += 256;
			result += v;
			o++;
			shift -= 8;
		}
		debug.append('=').append(result);
		System.out.println(debug.toString());
		return result;
	}

	public static void writeInt(int write, byte[] data, int offset) {
		data[offset] = (byte) ((write & INT_MASK_24) >> 24);
		data[offset + 1] = (byte) ((write & INT_MASK_16) >> 16);
		data[offset + 2] = (byte) ((write & INT_MASK_8) >> 8);
		data[offset + 3] = (byte) ((write & INT_MASK_0));
	}
}
