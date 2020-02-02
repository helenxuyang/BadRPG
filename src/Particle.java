public class Particle {
	private int x;
	private int y;
	private int type;
	private double yVel;
	private double xVel;
	public static final int SNOW = 0;
	public static final int SAND = 1;
	public static final int RAIN = 2;
	Particle(int inputType, int inputStartX, int inputXVel, int inputYVel){
		type=inputType;
		x = inputStartX;
		y=0;
		xVel=inputXVel;
		yVel=inputYVel;
	}
	public void update() {
		x+=xVel;
		y+=yVel;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getType() {
		return type;
	}

}