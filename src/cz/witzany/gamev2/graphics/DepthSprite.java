package cz.witzany.gamev2.graphics;

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader.Shader;

public class DepthSprite extends Node {

	private int x, y, width, height;
	private Rectangle origSize;
	private double scale;
	private Shader shader;
	private int tex, normal;
	private float heightScale;

	public DepthSprite(int guid, int x, int y, double scale, String texture, float heightScale) {
		super(guid);
		shader = ShaderLoader.loadShader("Data/Shaders/Depthsprite");
		origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, "rgbd"+f.getName() + ".png");
		File norm = new File(f, "n-" + f.getName() + ".png");
		this.tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
		this.normal = ImageLoader.loadImage(norm.getAbsolutePath(), origSize);
		width = (int) (origSize.width*scale);
		height = (int) (origSize.height*scale);
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.heightScale = heightScale;
	}

	public void update() {
		GL11.glLoadIdentity();
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 1);
		GL11.glScaled(width, height, 0);

		shader.apply();
		shader.setTexture("colorMap", 0, tex);
		// shader.setTexture("normalMap", 2, normal);

		int location = ARBShaderObjects.glGetUniformLocationARB(shader.shader, "height");
		if(location == -1)
			throw new RuntimeException("height not available");
		ARBShaderObjects.glUniform1fARB(location, heightScale);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(1, 0, 0, 1);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2d(0, 0);

		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2d(1, 0);

		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2d(1, 1);

		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2d(0, 1);
		GL11.glEnd();

		shader.release();

		GL11.glPopMatrix();
	}
}
