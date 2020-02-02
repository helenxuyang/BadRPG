public class DialogueBlock {

	private String[] lines;
	private int index = 0;
	private String currentLine;

	public DialogueBlock(String[] lines) {
		this.lines = lines;
		currentLine = lines[0];
	}

	public int getIndex() {
		return index;
	}

	public boolean onLastLine() {
		return (index == lines.length - 1);
	}

	public void nextLine() {
		if (index + 1 < lines.length) {
			index++;
			currentLine = lines[index];
		}	
	}
	
	public void reset() {
		index = 0;
		currentLine = lines[index];
	}
	
	public String getCurrentLine() {
		return currentLine;
	}
}