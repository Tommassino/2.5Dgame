package cz.witzany.gamev2.graphics.shaders;

import java.awt.Rectangle;
import java.io.File;

import cz.witzany.gamev2.graphics.utils.ImageLoader;

public class SimpleImage extends ShaderAttributes{

	private int tex;
	private float tx, ty;
	private float meshScale;
	
	public SimpleImage(String texture) {
		super(ShaderLoader.loadShader("Data/Shaders/Sprite"));
		Rectangle origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, f.getName() + ".png");
		tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
		setDimensions(origSize.width, origSize.height);
		meshScale = 1.0f;
	}

	public void setTextureOffset(float tx, float ty) {
		this.tx = tx;
		this.ty = ty;
	}
	
	@Override
	public void bind() {
		super.bind();
		shader.setTexture("colorMap", 0, tex);
		shader.setUniform("tx", tx / width);
		shader.setUniform("ty", ty / height);
		shader.setUniform("meshScale", meshScale);
	}

	public void setMeshScale(float meshScale) {
		this.meshScale = meshScale;
	}

}
