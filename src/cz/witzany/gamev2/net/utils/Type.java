package cz.witzany.gamev2.net.utils;

import java.lang.reflect.InvocationTargetException;

import cz.witzany.gamev2.graphics.impl.Game;

public enum Type {
	DISPLAY(1, Game.class);

	public final int id;
	public final Class c;

	private Type(int id, Class c) {
		this.id = id;
		this.c = c;
	}

	public static Object createInstance(int id, int guid) {
		for (Type t : Type.values()) {
			if (t.id == id)
				try {
					System.out.println("Creating " + t.c.getName()
							+ " with guid " + guid);
					return t.c.getConstructor(Integer.TYPE).newInstance(guid);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		System.out.println("Type " + id + " not found");
		return null;
	}
}
