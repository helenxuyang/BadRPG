public enum Quest {

	RAINFORESTKEY("Rainforest Key", "the snakes"),
	GRASSKEY("Grassland Key", "Batthew"),
	OCEANKEY("Ocean Key", "Clownfish"),
	SNOWKEY("Snow Key", "Snowman"),
	DESERTKEY("Desert Key", "Bush"),
	RAINFORESTITEM("Rainforest Item", "Mouse", "bugspray", "rainforest"),
	GRASSITEM("Grassland Item", "Squirrel", "flashlight", "grassland"),
	OCEANITEM("Ocean Item", "Bird", "goggles", "ocean"),
	SNOWITEM("Snow Item", "Cat", "coat", "snow"),
	DESERTITEM("Desert Item", "Dog", "boots", "desert");
	
	private String name;
	private boolean started = false;
	private boolean completed = false;
	private String message = "";
  
	public static Quest[] getAllQuests() {
		return new Quest[] {RAINFORESTITEM, RAINFORESTKEY, GRASSITEM, GRASSKEY, OCEANITEM, OCEANKEY, SNOWITEM, SNOWKEY, DESERTITEM, DESERTKEY};
	}
	
	private Quest(String questName, String startNPC, String item, String biome) {
		name = questName + " Quest";
		message = "Obtain the " + item;
	}
	private Quest(String questName, String startNPC) {
		name = questName + " Quest";
		message = "Talk to " + startNPC;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean started() {
		return started;
	}
	
	public boolean completed() {
		return completed;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void start() {
		started = true;
	}
	
	public void complete() {
		completed = true;
		message = "COMPLETE";
	}
	
	public void setMessage(String newMessage) {
		message = newMessage;
	}
}