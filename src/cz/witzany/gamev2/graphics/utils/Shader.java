package cz.witzany.gamev2.graphics.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class Shader {
	private static int activeShader = -1;
	public int shader;
	public int vertShader;
	public int fragShader;
	private boolean useShader;

	public Shader(String shaderDir) {
		File sDir = new File(shaderDir);
		useShader = true;
		if (!sDir.exists() || !sDir.isDirectory()) {
			useShader = false;
			return;
		}
		File vShader = new File(sDir, sDir.getName() + ".vert");
		File fShader = new File(sDir, sDir.getName() + ".frag");
		if (!vShader.exists() || !fShader.exists()) {
			useShader = false;
			return;
		}
		shader = ARBShaderObjects.glCreateProgramObjectARB();
		if (shader == 0) {
			useShader = false;
			return;
		}
		vertShader = createShader(vShader.getAbsolutePath(),
				ARBVertexShader.GL_VERTEX_SHADER_ARB);
		fragShader = createShader(fShader.getAbsolutePath(),
				ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		if (vertShader == 0 || fragShader == 0) {
			useShader = false;
			return;
		}
		ARBShaderObjects.glAttachObjectARB(shader, vertShader);
		ARBShaderObjects.glAttachObjectARB(shader, fragShader);
		ARBShaderObjects.glLinkProgramARB(shader);

		if(GL20.glGetShader(shader, GL20.GL_LINK_STATUS) == GL11.GL_FALSE){
			printLogInfo(shaderDir,shader);
			useShader = false;
			return;
		}
		
		ARBShaderObjects.glValidateProgramARB(shader);

		if(GL20.glGetShader(shader, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE){
			printLogInfo(shaderDir,shader);
			useShader = false;
			return;
		}
		useShader = true;
		System.out.println("Loaded shader " + shaderDir);
	}

	private int createShader(String filename, int type) {
		int nshader = ARBShaderObjects.glCreateShaderObjectARB(type);
		if (nshader == 0) {
			return 0;
		}
		String code = "";
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				code += line + "\n";
			}
		} catch (Exception e) {
			System.out.println("Fail reading vertex shading code");
			return 0;
		}
		ARBShaderObjects.glShaderSourceARB(nshader, code);
		ARBShaderObjects.glCompileShaderARB(nshader);

		if(GL20.glGetShader(nshader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
			printLogInfo(filename,nshader);
			return 0;
		}
		return nshader;
	}

	private boolean printLogInfo(String name, int obj) {
		IntBuffer iVal = BufferUtils.createIntBuffer(1);
		ARBShaderObjects.glGetObjectParameterARB(obj,
				ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);

		int length = iVal.get();
		if (length > 1) {
			// We have some info we need to output.
			ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
			iVal.flip();
			ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
			byte[] infoBytes = new byte[length];
			infoLog.get(infoBytes);
			String out = new String(infoBytes);
			System.out.println(name+" log:\n" + out);
		}
		return true;
	}

	public void setTexture(String name, int index, int pointer) {
		int location = ARBShaderObjects.glGetUniformLocationARB(shader, name);
		if (location == -1)
			throw new RuntimeException(name + " not available");
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pointer);
		ARBShaderObjects.glUniform1iARB(location, index);
	}

	public synchronized void apply() {
		if(activeShader == shader)
			return;
		if (useShader){
			release();
			ARBShaderObjects.glUseProgramObjectARB(shader);
			activeShader = shader;
		}else
			System.out.println("warning, shader not used");
	}

	private void release() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
}