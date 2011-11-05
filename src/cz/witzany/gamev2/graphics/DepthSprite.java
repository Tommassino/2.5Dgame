package cz.witzany.gamev2.graphics;

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.graphics.utils.Mesh;
import cz.witzany.gamev2.graphics.utils.MeshLoader;
import cz.witzany.gamev2.graphics.utils.Shader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;

public class DepthSprite extends Node {

	private int x, y, width, height;
	private Rectangle origSize;
	private double scale;
	private Shader shader;
	private Mesh mesh;
	private int tex, normal;
	private float heightScale;

	public DepthSprite(int guid, int x, int y, double scale, String texture, float heightScale) {
		super(guid);
		shader = ShaderLoader.loadShader("Data/Shaders/Depthsprite");
		mesh = MeshLoader.loadMesh("Data/Mesh/Quad",shader);
		origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, "rgbd"+f.getName() + ".png");
		this.tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
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
		GL11.glTranslatef(x, y, 1.0f);
		GL11.glScaled(width, height, 0);

		shader.apply();
		shader.setTexture("colorMap", 0, tex);

		int location = ARBShaderObjects.glGetUniformLocationARB(shader.shader, "height");
		if(location == -1)
			throw new RuntimeException("height not available");
		ARBShaderObjects.glUniform1fARB(location, heightScale);
		
		mesh.bind();
		mesh.draw();

		GL11.glPopMatrix();
	}
}
