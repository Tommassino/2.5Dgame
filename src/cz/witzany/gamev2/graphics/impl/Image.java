package cz.witzany.gamev2.graphics.impl;

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.model.PosNode;
import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.graphics.utils.Shader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;

public class Image extends PosNode {

	protected int width, height;
	private Rectangle origSize;
	private float scale;
	protected Shader shader;
	private int tex;
	private float tx, ty, meshScale;

	public Image(int x, int y, float z, float scale, String texture) {
		shader = ShaderLoader.loadShader("Data/Shaders/Sprite");
		origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, f.getName() + ".png");
		tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
		width = (int) (origSize.width * scale);
		height = (int) (origSize.height * scale);
		setPosition(x, y, z);
		this.scale = scale;
		tx = ty = 0;
		meshScale = 1;
	}

	public Image(int x, int y, float z, float scale, String shader,
			int texture, int width, int height) {
		this.shader = ShaderLoader.loadShader(shader);
		this.origSize = new Rectangle(0, 0, width, height);
		this.tex = texture;
		this.width = (int) (origSize.width * scale);
		this.height = (int) (origSize.height * scale);
		setPosition(x, y, z);
		this.scale = scale;
		this.tx = this.ty = 0;
		this.meshScale = 1;
	}

	public void setTexturePosition(float tx, float ty) {
		this.tx = tx;
		this.ty = ty;
	}

	public void setMeshScale(float scale) {
		meshScale = scale;
	}

	public void update() {
		GL11.glPushMatrix();
		Vector3f position = getPosition();
		GL11.glTranslatef(position.x - width / 2, position.y - height / 2,
				position.z);
		GL11.glScaled(width, height, 0);

		shader.apply();
		shader.setTexture("colorMap", 0, tex);
		shader.setFloatUniform("tx", tx / width);
		shader.setFloatUniform("ty", ty / height);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(1, 0, 0, 1);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2d(0, 0);

		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2d(1 * meshScale, 0);

		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2d(1 * meshScale, 1 * meshScale);

		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2d(0, 1 * meshScale);
		GL11.glEnd();

		GL11.glPopMatrix();
	}
}
