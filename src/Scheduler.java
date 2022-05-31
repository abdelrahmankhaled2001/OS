import java.util.ArrayList;

public class Scheduler {
	private int timeSlice;
	private int timeSpent;

	public Scheduler(int timeSlice) {
		this.timeSlice = timeSlice;
	}

	public int getTimeSlice() {
		return timeSlice;
	}

	public void setTimeSlice(int timeSlice) {
		this.timeSlice = timeSlice;
	}

	public int getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(int timeSpent) {
		this.timeSpent = timeSpent;
	}

	public Process getNext(Process running, ArrayList<Process> readyQueue) {
		if (running == null) {
			if (!readyQueue.isEmpty()) {
				timeSpent = 0;
				return readyQueue.get(0);

			}
			return null;
		} else {
			this.timeSpent += 1;
			if (timeSpent == timeSlice) {
				if (!readyQueue.isEmpty()) {
					timeSpent = 0;
					return readyQueue.get(0);

				} else {
					timeSpent = 0;
					return running;
				}

			}
			return running;

		}
	}

}
