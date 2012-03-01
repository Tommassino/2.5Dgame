package cz.witzany.gamev2.graphics.impl;

import java.awt.Rectangle;
import java.io.File;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.model.TimerNode;
import cz.witzany.gamev2.graphics.utils.ImageLoader;
import cz.witzany.gamev2.graphics.utils.Mesh;
import cz.witzany.gamev2.graphics.utils.MeshLoader;
import cz.witzany.gamev2.graphics.utils.Shader;
import cz.witzany.gamev2.graphics.utils.ShaderLoader;

public class FunAnim extends TimerNode{

	private int width, height;
	private Rectangle origSize;
	private Shader shader;
	private Mesh mesh;
	private int tex; //#TODO , normal;
	private float heightScale;
	private float baseScale;

	public FunAnim(int x, int y, double scale, String texture, float heightScale) {
		shader = ShaderLoader.loadShader("Data/Shaders/Depthsprite");
		mesh = MeshLoader.loadMesh("Data/Mesh/Quad",shader);
		origSize = new Rectangle();
		File f = new File(texture);
		File color = new File(f, "rgbd"+f.getName() + ".png");
		this.tex = ImageLoader.loadImage(color.getAbsolutePath(), origSize);
		width = (int) (origSize.width*scale);
		height = (int) (origSize.height*scale);
		setPosition(x, y, 0.0f);
		this.heightScale = heightScale;
		this.baseScale = this.scale = (float) scale;
	}

	private final float speed = 0.001f;
	private boolean spin = true;
	private float scale = 0.75f;
	private final float max = 1.25f;
	private final float min = 0.5f;
	public void update(long diff) {
		float add = speed*diff;
		if(!spin)
			add *= -1;
		scale += add;
		if(scale > max){
			scale = max;
			spin = !spin;
		}else if (scale < min){
			scale = min;
			spin = !spin;
		}
		
		GL11.glPushMatrix();
		float w = width*scale;
		float h = height*scale;
		Vector3f position = getPosition();
		GL11.glTranslatef(position.x-w/2, position.y-h/2, position.z);
		GL11.glScaled(w, h, 0);

		shader.apply();
		shader.setTexture("colorMap", 0, tex);

		int location = ARBShaderObjects.glGetUniformLocationARB(shader.shader, "height");
		if(location == -1)
			throw new RuntimeException("height not available");
		ARBShaderObjects.glUniform1fARB(location, heightScale*scale);
		
		mesh.bind();
		mesh.draw();

		GL11.glPopMatrix();
	}
}
