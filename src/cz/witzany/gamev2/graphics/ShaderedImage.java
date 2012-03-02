package cz.witzany.gamev2.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.shaders.ShaderAttributes;
import cz.witzany.gamev2.graphics.utils.Mesh;

public class ShaderedImage<T extends ShaderAttributes> extends Node {

	protected int width, height;
	private T shaderAttrib;
	private Mesh mesh;

	public ShaderedImage(float x, float y, float z, float scale, T shaderAttrib) {
		this.shaderAttrib = shaderAttrib;
		width = (int) (shaderAttrib.getWidth() * scale);
		height = (int) (shaderAttrib.getHeight() * scale);
		mesh = new Mesh("", shaderAttrib.getShader());
		setPosition(x, y, z);
	}

	public void setScale(float scale) {
		width = (int) (shaderAttrib.getWidth() * scale);
		height = (int) (shaderAttrib.getHeight() * scale);
	}

	@Override
	public void update(int diff) {
		GL11.glPushMatrix();
		Vector3f position = getPosition();
		GL11.glTranslatef(position.x - width / 2, position.y - height / 2,
				position.z);
		GL11.glScaled(width, height, 0);

		shaderAttrib.bind();

		mesh.bind();
		mesh.draw();

		GL11.glPopMatrix();
	}
	
	public T getAttributes(){
		return shaderAttrib;
	}
}
