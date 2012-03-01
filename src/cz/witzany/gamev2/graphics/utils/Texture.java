package cz.witzany.gamev2.graphics.utils;

import java.nio.ByteBuffer;

public class Texture {
	public ByteBuffer imageData; // Image Data (Up To 32 Bits)
	public int bpp; // Image Color Depth In Bits Per Pixel
	public int width; // Image width
	public int height; // Image height
	public int[] texID = new int[1]; // Texture ID Used To Select A Texture
	public int type; // Image Type (GL_RGB, GL_RGBA)
}
