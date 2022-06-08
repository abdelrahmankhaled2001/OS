import java.util.ArrayList;

public class Mutex {
	boolean available = true;
	int currentProcess;
	ArrayList<Integer> blockedProcesses = new ArrayList<Integer>();

	public Mutex() {

	}

	public boolean semwait(int processID) {
		if (available == false) {
			blockedProcesses.add(processID);
			return false;

		} else {
			available = false;
			currentProcess = processID;
			return true;
		}
	}

	public int semSignal(int processID) {
		if (processID == currentProcess) {
			if (!blockedProcesses.isEmpty()) {
				currentProcess = blockedProcesses.remove(0);

				return currentProcess;
			}
			available = true;
			return -1;
		}
		return -2;
	}
}
