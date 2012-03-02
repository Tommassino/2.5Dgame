package cz.witzany.gamev2.graphics.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cz.witzany.gamev2.graphics.impl.ShaderedImage;
import cz.witzany.gamev2.graphics.impl.SimpleAnim;
import cz.witzany.gamev2.graphics.shaders.DepthImage;

public class AnimTemplate {

	private ArrayList<FrameInfo> frameList;

	public AnimTemplate(String file) throws IOException {
		frameList = new ArrayList<AnimTemplate.FrameInfo>();
		load(file);
	}

	private void load(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			FrameInfo frameInfo = new FrameInfo();
			String[] split = line.split(" ");
			frameInfo.texture = split[0];
			float t = Float.parseFloat(split[1]);
			frameInfo.scale = t;
			t = Float.parseFloat(split[2]);
			frameInfo.heightScale = t;
			frameList.add(frameInfo);
		}
		br.close();
	}

	public SimpleAnim construct() {
		SimpleAnim anim = new SimpleAnim();
		int i = 1;
		for (FrameInfo info : frameList) {
			ShaderedImage<DepthImage> ds = new ShaderedImage<DepthImage>(0, 0, 0, info.scale, new DepthImage(info.texture,
					info.heightScale));
			anim.addFrame(ds);
			i++;
		}
		return anim;
	}

	private class FrameInfo {
		String texture;
		float scale;
		float heightScale;
	}
}
