package cz.witzany.gamev2.graphics.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Mesh {
	private int VBOBuffer;
	private int stride;
	private Shader shader;
	private int position, texture;

	public Mesh(String file, Shader shader) {
		// #TODO load from a file
		// x y z u v
		float[] vertices = { 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f,
				0f, 1f, 1f, 0f, 1f, 0f, 0f, 1f };
		this.stride = 5 * 4;
		this.shader = shader;

		// register vertex buffer
		VBOBuffer = genBuffer();

		// bind
		ARBVertexBufferObject.glBindBufferARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, VBOBuffer);

		// push data to the buffer
		ByteBuffer fbuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		fbuffer.order(ByteOrder.LITTLE_ENDIAN);
		for (float f : vertices)
			fbuffer.putFloat(f);
		fbuffer.rewind();
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, fbuffer,
				ARBVertexBufferObject.GL_STATIC_DRAW_ARB);

		// get the texture and position location pointers
		shader.apply();
		position = GL20.glGetAttribLocation(shader.shader, "position");
		texture = GL20.glGetAttribLocation(shader.shader, "texture");
		shader.release();
	}

	private int genBuffer() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		ARBVertexBufferObject.glGenBuffersARB(buffer);
		return buffer.get(0);
	}

	public void draw() {
		// bind the buffer
		ARBVertexBufferObject.glBindBufferARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, VBOBuffer);

		// set the position and texture to point on the buffer
		GL20.glVertexAttribPointer(position, 3, GL11.GL_FLOAT, false, stride, 0);
		GL20.glVertexAttribPointer(texture, 2, GL11.GL_FLOAT, false, stride, 12);

		GL20.glEnableVertexAttribArray(position);
		GL20.glEnableVertexAttribArray(texture);

		// debug draw, draw anything
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 5); // #TODO indices
	}

	public void dispose() {
		ARBVertexBufferObject.glDeleteBuffersARB(VBOBuffer);
	}
}
