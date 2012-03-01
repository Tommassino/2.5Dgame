package cz.witzany.gamev2.graphics.impl;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import cz.witzany.gamev2.graphics.model.PosNode;
import cz.witzany.gamev2.graphics.model.TimerNode;

public class SimpleAnim extends TimerNode {

	private static Random r = new Random();
	private ArrayList<PosNode> frames;

	public SimpleAnim() {
		frames = new ArrayList<PosNode>();
	}

	public void addFrame(PosNode frame) {
		frames.add(frame);
		frame.bindPosition(this);
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void runFor(int ms) {
		this.runFor = ms;
	}

	private int speed = 200;
	private int timer = r.nextInt() % speed;
	private int curr = 0;
	private int runFor = -1;

	@Override
	public void update(long diff) {
		if (frames.size() == 0)
			return;

		if (runFor >= diff || runFor == -1) {
			if (timer < diff) {
				curr++;
				if (curr >= frames.size())
					curr = 0;
				timer = speed;
			} else
				timer -= diff;
		}
		if (runFor > 0)
			runFor = Math.max(runFor - (int) diff, 0);

		PosNode sprite = frames.get(curr);
		sprite.update();
	}
}
