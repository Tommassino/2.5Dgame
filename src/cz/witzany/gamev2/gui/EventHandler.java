package cz.witzany.gamev2.gui;

import org.lwjgl.input.Keyboard;

public class EventHandler {

	private boolean[] pressed;
	private long[] time;
	public static final int KEYCODE_COUNT = 256;
	public static final long REFRESH_RATE = 20;
	private static EventHandler instance;
	private KeyHandler focus;

	private EventHandler() {
		pressed = new boolean[KEYCODE_COUNT];
		time = new long[KEYCODE_COUNT];
	}
	
	public void setFocus(KeyHandler focus){
		this.focus = focus;
	}

	private void set(int keyCode, boolean value, long time) {
		if (keyCode < 0 || keyCode >= KEYCODE_COUNT)
			return;
		if (value && !pressed[keyCode]){
			focus.handleKeyPressed(keyCode);
			focus.handleKeyEvent(keyCode);
		}
		pressed[keyCode] = value;
		this.time[keyCode] = time;
	}
	
	private void check(int keyCode,long time){
		if(!pressed[keyCode])
			return;
		long diff = time - this.time[keyCode];
		if(diff >= REFRESH_RATE){
			focus.handleKeyEvent(keyCode);
			this.time[keyCode] = time;
		}
	}

	public void update() {
		long time = System.currentTimeMillis();
		while (Keyboard.next())
			set(Keyboard.getEventKey(), Keyboard.getEventKeyState(), time);
		for(int i = 0; i < KEYCODE_COUNT; i++)
			check(i,time);
	}
	
	public static void focus(KeyHandler focus){
		instance.setFocus(focus);
	}

	public static void poll() {
		if (instance == null)
			initialize();
		instance.update();
	}

	public static void initialize() {
		if (instance == null)
			instance = new EventHandler();
	}
}
