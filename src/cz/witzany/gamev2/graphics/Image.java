package cz.witzany.gamev2.graphics;

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.opengl.GL11;

import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.graphics.utils.Shader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;

public class Image extends Node{

	private int x, y, width, height;
	private float z;
	private Rectangle origSize;
	private float scale;
	private Shader shader;
	private int tex;

	public Image(int guid, int x, int y, float z, float scale, String texture) {
		super(guid);
		shader = ShaderLoader.loadShader("Data/Shaders/Sprite");
		origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, f.getName() + ".png");
		tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
		width = (int) (origSize.width*scale);
		height = (int) (origSize.height*scale);
		this.x = x;
		this.y = y;
		this.z = z;
		this.scale = scale;
	}

	public void update() {
		GL11.glLoadIdentity();
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glScaled(width, height, 0);

		shader.apply();
		shader.setTexture("colorMap", 0, tex);
		// shader.setTexture("normalMap", 2, normal);

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

		GL11.glPopMatrix();
	}
}
