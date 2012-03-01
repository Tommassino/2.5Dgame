package cz.witzany.gamev2.graphics.impl;

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.model.PosNode;
import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.graphics.utils.Mesh;
import cz.witzany.gamev2.graphics.utils.MeshLoader;
import cz.witzany.gamev2.graphics.utils.Shader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;

public class DepthSprite extends PosNode {

	private int width, height;
	private Rectangle origSize;
	private Shader shader;
	private Mesh mesh;
	private int tex; // #TODO , normal;
	private float heightScale;

	public DepthSprite(int guid, int x, int y, double scale, String texture,
			float heightScale) {
		super(guid);
		shader = ShaderLoader.loadShader("Data/Shaders/Depthsprite");
		mesh = MeshLoader.loadMesh("Data/Mesh/Quad", shader);
		origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, "rgbd" + f.getName() + ".png");
		this.tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
		width = (int) (origSize.width * scale);
		height = (int) (origSize.height * scale);
		setPosition(x, y, 0.0f);
		this.heightScale = heightScale;
	}

	public void update() {
		GL11.glLoadIdentity();
		GL11.glPushMatrix();
		Vector3f position = getPosition();
		GL11.glTranslatef(position.x - width / 2, position.y - height / 2,
				position.z);
		GL11.glScaled(width, height, 0);

		shader.apply();
		shader.setTexture("colorMap", 0, tex);
		shader.setFloatUniform("height", heightScale);
		shader.setFloatUniform("night", Game.getInstance().night);

		mesh.bind();
		mesh.draw();

		GL11.glPopMatrix();
	}
}
