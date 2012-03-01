package cz.witzany.gamev2.graphics.utils;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.sourceforge.fastpng.PNGDecoder;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ImageLoader {

	private HashMap<String, Integer> images = new HashMap<String, Integer>();
	private HashMap<String, Rectangle> dimensions = new HashMap<String, Rectangle>();
	private static ImageLoader instance = new ImageLoader();

	private ImageLoader() {
	}

	private int load(String path, Rectangle dimension) {
		if (images.containsKey(path)) {
			Rectangle dim = dimensions.get(path);
			dimension.height = dim.height;
			dimension.width = dim.width;
			return images.get(path);
		}
		try {
			InputStream in = new FileInputStream(path);
			PNGDecoder decoder = new PNGDecoder(in);

			dimension.width = decoder.getWidth();
			dimension.height = decoder.getHeight();
			dimensions.put(path, new Rectangle(dimension));

			ByteBuffer data = ByteBuffer.allocateDirect(4 * decoder.getWidth()
					* decoder.getHeight());
			data.order(ByteOrder.nativeOrder());
			decoder.decode(data, decoder.getWidth() * 4,
					PNGDecoder.TextureFormat.RGBA);
			data.rewind();

			IntBuffer tmp = BufferUtils.createIntBuffer(1);
			GL11.glGenTextures(tmp);
			tmp.rewind();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tmp.get(0));
			tmp.rewind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
					decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, data);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
			int p = tmp.get(0);
			images.put(path, p);
			System.out.println("Loaded texture " + path);
			return p;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	public static void gen(File dir){
		if(!dir.isDirectory())
			return;
		File[] subs = dir.listFiles();
		for(File f : subs){
			if(!f.isDirectory()){
				merge(dir.getAbsolutePath());
				return;
			}
			gen(f);
		}
	}

	private static void merge(String path) {
		File dir = new File(path);
		if (!dir.isDirectory() || !dir.exists())
			return;
		File result = new File(dir, "rgbd" + dir.getName() + ".png");
		//if (result.exists())
		//	return;
		File rgba = new File(dir, dir.getName() + ".png");
		File dept = new File(dir, "z-" + dir.getName() + ".png");

		try {
			PNGDecoder rgbad = new PNGDecoder(new FileInputStream(rgba));
			PNGDecoder deptd = new PNGDecoder(new FileInputStream(dept));
			if (rgbad.getWidth() != deptd.getWidth()
					|| rgbad.getHeight() != deptd.getHeight())
				return;

			ByteBuffer rgbaData = ByteBuffer.allocateDirect(4
					* rgbad.getWidth() * rgbad.getHeight());
			rgbaData.order(ByteOrder.nativeOrder());
			ByteBuffer deptData = ByteBuffer.allocateDirect(4
					* deptd.getWidth() * deptd.getHeight());
			deptData.order(ByteOrder.nativeOrder());
			int width = rgbad.getWidth();
			int height = rgbad.getHeight();

			rgbad.decode(rgbaData, width * 4,
					PNGDecoder.TextureFormat.RGBA);
			rgbaData.rewind();
			deptd.decode(deptData, width * 4,
					PNGDecoder.TextureFormat.RGBA);
			deptData.rewind();

			ColorModel cm = ColorModel.getRGBdefault();
			WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
			byte[] data1 = new byte[4];
			byte[] data2 = new byte[4];
			int x = 0;
			int y = 0;
			while (rgbaData.hasRemaining()) {
				rgbaData.get(data1);
				deptData.get(data2);
				int[] col = {data1[0],data1[1],data1[2],0xFF-data2[0]};
				raster.setPixel(x, y, col);
				x++;
				if(x==width){
					y++;x=0;
				}
			}
			BufferedImage img = new BufferedImage(cm, raster, false, null);
		    ImageIO.write(img,"png",result.getAbsoluteFile());
		    System.out.println(path+" converted");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int loadImage(String path, Rectangle dimensions) {
		return instance.load(path, dimensions);
	}
}
