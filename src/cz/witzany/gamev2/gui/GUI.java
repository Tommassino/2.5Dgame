package cz.witzany.gamev2.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.Node;
import cz.witzany.gamev2.graphics.impl.Game;
import cz.witzany.gamev2.graphics.impl.SimpleAnim;
import cz.witzany.gamev2.graphics.impl.TextArea;
import cz.witzany.gamev2.graphics.utils.FBO;

public class GUI extends FBO implements KeyHandler {

	private static GUI instance;
	private static Node controlled;
	private TextArea console;

	public GUI() {
		super(Game.getInstance().getWidth(),Game.getInstance().getHeight());
		EventHandler.initialize();
		EventHandler.focus(this);
		console = new TextArea(0, 0, 1f, 1f);
		addChild(console);
	}

	@Override
	public void unbind(){
		//super.unbind();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	@Override
	public void bind() {
		//super.bind();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		int width = Game.getInstance().getWidth();
		int height = Game.getInstance().getHeight();
		GL11.glOrtho(-width / 2, width / 2, height / 2, -height / 2, 10, -10);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glScaled(1, -1, 1);
	}

	public void setControlled(Node c) {
		controlled = c;
	}

	public void handleKeyEvent(int key) {
		Vector3f pos = controlled.getPosition();
		switch (key) {
		case Keyboard.KEY_W:
			controlled.setPosition(pos.x, pos.y - 5, pos.z);
			if (controlled instanceof SimpleAnim)
				((SimpleAnim) controlled).runFor(200);
			break;
		case Keyboard.KEY_A:
			controlled.setPosition(pos.x - 5, pos.y, pos.z);
			if (controlled instanceof SimpleAnim)
				((SimpleAnim) controlled).runFor(200);
			break;
		case Keyboard.KEY_S:
			controlled.setPosition(pos.x, pos.y + 5, pos.z);
			if (controlled instanceof SimpleAnim)
				((SimpleAnim) controlled).runFor(200);
			break;
		case Keyboard.KEY_D:
			controlled.setPosition(pos.x + 5, pos.y, pos.z);
			if (controlled instanceof SimpleAnim)
				((SimpleAnim) controlled).runFor(200);
			break;
		case Keyboard.KEY_ESCAPE:
			System.exit(0);
		}
	}

	public void handleKeyPressed(int key) {
		switch (key) {
		case Keyboard.KEY_GRAVE:
			EventHandler.focus(console);
			break;
		case Keyboard.KEY_SYSRQ:
			Game.getInstance().screenshot();
			break;
		case Keyboard.KEY_SPACE:
			System.out.println(controlled.getPosition());
		}

	}

	public static GUI getInstance() {
		if (instance == null)
			instance = new GUI();
		return instance;
	}
}
