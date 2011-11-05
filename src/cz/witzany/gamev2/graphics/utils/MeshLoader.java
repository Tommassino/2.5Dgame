package cz.witzany.gamev2.graphics.utils;

import java.util.HashMap;

public class MeshLoader {
	private HashMap<String, Mesh> meshmap = new HashMap<String, Mesh>();
	private static MeshLoader instance = new MeshLoader();
	
	public synchronized Mesh load(String name, Shader shader){
		if(meshmap.containsKey(name))
			return meshmap.get(name);
		Mesh mesh = new Mesh(name,shader);
		meshmap.put(name, mesh);
		System.out.println("Loaded mesh "+name);
		return mesh;
	}
	
	public static Mesh loadMesh(String name, Shader shader){
		return instance.load(name,shader);
	}
}
