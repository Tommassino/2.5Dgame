package cz.witzany.gamev2.graphics.impl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.gui.EventHandler;
import cz.witzany.gamev2.gui.GUI;
import cz.witzany.gamev2.gui.KeyHandler;

public class TextArea extends Image implements KeyHandler {

	private String value;

	public TextArea(int x, int y, float z, float scale) {
		super(x, y, z, scale, "Data/Textures/Sprites/CharMap");
		value = "";
	}

	public void append(char c) {
		value += c;
	}

	public void delete() {
		if(value.length()>0)
			value = value.substring(0, value.length() - 1);
	}

	public void update() {
		GL11.glPushMatrix();
		GL11.glScaled(1, -1, 1);
		// 16
		float wx = width / 16.0f;
		float hx = height / 16.0f;
		setMeshScale(1 / 16.0f);

		Vector3f position = getPosition();
		float x = position.x;
		int dx = 0;
		for (char c : value.toCharArray()) {
			setPosition(x + dx, position.y, position.z);
			setTexturePosition(wx * (c % 16), hx * (c / 16));
			super.update();
			dx += 16;
		}
		setPosition(x, position.y, position.z);
		GL11.glPopMatrix();
	}

	public void handleKeyEvent(int key) {

	}

	@Override
	public void handleKeyPressed(int key) {
		switch (key) {
		case Keyboard.KEY_BACK:
			delete();
			break;
		case Keyboard.KEY_ESCAPE:
			value = "";
			EventHandler.focus(GUI.getInstance());
			break;
		case Keyboard.KEY_RETURN:
			// #TODO command
			command();
			value = "";
			EventHandler.focus(GUI.getInstance());
			break;
		case Keyboard.KEY_LCONTROL:
		case Keyboard.KEY_RCONTROL:
		case Keyboard.KEY_LSHIFT:
		case Keyboard.KEY_RSHIFT:
		case Keyboard.KEY_LMENU:
		case Keyboard.KEY_RMENU:
			break;
		default:
			char c = Keyboard.getEventCharacter();
			append(c);
		}
		// System.out.println(Keyboard.getKeyName(key));
	}

	public void command() {
		String[] sp = value.split(" ");
		if (sp.length != 2)
			return;
		if (sp[0].equals("night")) {
			float n = Float.parseFloat(sp[1]);
			Game.getInstance().night=n;
		}
	}
}
