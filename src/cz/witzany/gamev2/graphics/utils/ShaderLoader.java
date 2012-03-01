package cz.witzany.gamev2.graphics.utils;

import java.util.HashMap;

public class ShaderLoader {

	private HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private static ShaderLoader instance = new ShaderLoader();

	public synchronized static Shader loadShader(String shaderPath) {
		if (!instance.shaders.containsKey(shaderPath))
			instance.shaders.put(shaderPath, new Shader(shaderPath));
		return instance.shaders.get(shaderPath);
	}
}
