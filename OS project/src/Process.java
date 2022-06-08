import java.util.ArrayList;

public class Process {
	private boolean isFinished = false;
	private int lastUsed=-1;
	private int id;
	
	public Process(int id) {
		this.id=id;
	}
	public int getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(int lastUsed) {
		this.lastUsed = lastUsed;
	}

	public String toString() {
		return "Process "+id;
	}
	
		
	

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	

}

class Pair {
	Object name;
	Object value;

	public Pair(Object name, Object value) {
		this.name = name;
		this.value = value;
	}
	public String toString() {
		return name+" "+value;
	}
	
}
enum State{
	Ready,Running,Blocked,Finished
}