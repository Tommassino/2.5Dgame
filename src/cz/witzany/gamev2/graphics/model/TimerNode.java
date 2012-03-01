package cz.witzany.gamev2.graphics.model;

public abstract class TimerNode extends PosNode {

	private long time;

	public TimerNode() {
		time = System.currentTimeMillis();
	}

	public final void update() {
		long nt = System.currentTimeMillis();
		update(nt - time);
		time = nt;
	}

	public abstract void update(long diff);

}
