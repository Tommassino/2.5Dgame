package cz.witzany.gamev2.graphics.shaders;

import java.awt.Rectangle;
import java.io.File;

import cz.witzany.gamev2.graphics.utils.ImageLoader;

public class DepthImage extends ShaderAttributes {

	private int tex;
	private float heightScale;
	
	public DepthImage(String texture, float heightScale) {
		super(ShaderLoader.loadShader("Data/Shaders/Depthsprite"));
		Rectangle origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, "rgbd" + f.getName() + ".png");
		this.tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
		setDimensions(origSize.width, origSize.height);
		this.heightScale = heightScale;
	}
	
	public void setHeightScale(float heightScale){
		this.heightScale = heightScale;
	}
	
	@Override
	public void bind() {
		super.bind();
		shader.setTexture("colorMap", 0, tex);
		shader.setUniform("height", heightScale);
	}

}
