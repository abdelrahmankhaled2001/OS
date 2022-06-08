import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class OS {
	
	//OS components
	Parser myParser = new Parser();
	SystemCallHandler mySystemCallHandler = new SystemCallHandler();
	Interpreter myinterpreter = new Interpreter();
	Scheduler myScheduler;
	//Mutexes
	Mutex userInput = new Mutex();
	Mutex userOutput = new Mutex();
	Mutex file = new Mutex();
	//Programs and their arrival times
	ArrayList<String> programs = new ArrayList<String>();
	ArrayList<Integer> arrivalTimes = new ArrayList<Integer>();
	//Queues and running process
	ArrayList<Process> readyQueue = new ArrayList<Process>();
	ArrayList<Process> blockedQueue = new ArrayList<Process>();
	Process running = null;
	//Clock cycles counter
	int counter = 0;
	//Saved processes' PCBS
	HashMap<Process, ArrayList<Object>> processesPCBs = new HashMap<Process, ArrayList<Object>>();
	//Process currently on mainMemory
	ArrayList<Process> onMemory = new ArrayList<>();
	//Output 
	ArrayList<String>  outPut = new ArrayList<String>();
	//message
	ArrayList<Object> messages = new ArrayList<>();
	//Processes counter
	int processCount = 1;
	//new Process data
	Process p ;
	ArrayList<String> code ;
	ArrayList<Object> PCB ;

	public OS(int timeSlice) {
		myScheduler = new Scheduler(timeSlice);
		
	
	}

	@SuppressWarnings("resource")
	public void takeInput() {
		Scanner scint = new Scanner(System.in);
		Scanner scstr = new Scanner(System.in);
		mySystemCallHandler.printOnScreen("Please enter the number of programs!");
		int n = scint.nextInt();
		for (int i = 0; i < n; i++) {
			mySystemCallHandler.printOnScreen("Please enter the path of the program followed by its arrival time!");
			String path = scstr.nextLine();
			int arrivalTime = scint.nextInt();
			programs.add(path);
			arrivalTimes.add(arrivalTime);
		}
	}

	
	public void execute(Memory myMemory) {
		takeInput();
		while (!((programs.isEmpty()) && (readyQueue.isEmpty()) && (running == null) && (blockedQueue.isEmpty()))) {
			p=null;
			code=null;
			PCB=null;
			mySystemCallHandler.printOnScreen("---------------------------------------------------- \n");
			mySystemCallHandler.printOnScreen("Time cycle: " + counter);
	
			if (!arrivalTimes.isEmpty()) {
				checkArrivingProcess();
			}
			
			decideNextRunningProcess(myMemory);
			updateLastUsed();
			if (p != null) {
				putNewProcessOnMemory(myMemory, code, PCB);
				processesPCBs.put(p, PCB);
				outPut.add(p + " has been added to the main memory for the first time");
			}
			mySystemCallHandler.printOnScreen("Currently running process: " + running);
			if (running != null) {
				if (!onMemory.contains(running)) {
					putOnMemory(running, myMemory, myParser.getProcess(running.toString()));
					onMemory.add(running);
					outPut.add(running + " has been swaped from disk to main memory");
				}
				mySystemCallHandler.updateMemory(new Pair("ProcessState", State.Running), getLowerBound(running),getUpperBound(running), myMemory);
				executeNextInstruction(myMemory);
				updateBlockedQueue(myMemory);
				checkFinishedProcess(myMemory);
			}
			printOutPut();
			printMemory(myMemory);

			counter++;
		}
		mySystemCallHandler.printOnScreen("----------------------------------------------------");
	}

	public void checkArrivingProcess() {
		int nextArrival = arrivalTimes.get(0);
		int index = 0;
		for (int i = 1; i < arrivalTimes.size(); i++) {
			if (nextArrival > arrivalTimes.get(i)) {
				nextArrival = arrivalTimes.get(i);
				index = i;
			}
		}

		if (nextArrival == counter) {
			arrivalTimes.remove(index);
			p = new Process(processCount);
			code = myParser.getProcessBody(programs.remove(index));
			PCB = new ArrayList<Object>();
			PCB.add(processCount++);
			PCB.add(State.Ready);
			PCB.add(5);
			readyQueue.add(p);

			onMemory.add(p);
			
			mySystemCallHandler.printOnScreen( p + " has arrived to the ready queue for the first time");
			printQueues();
			
		}
	}
	

	
	
	
	
	
	public void printQueues() {
		
		mySystemCallHandler.printOnScreen("Ready Queue: " + readyQueue);
		mySystemCallHandler.printOnScreen("Blocked Queue: " + blockedQueue);
		
	}

	
	public void decideNextRunningProcess(Memory myMemory) {
		Process oldRunning = running;
		Process newRunning = myScheduler.getNext(running, readyQueue);

		if (newRunning != null) {
			readyQueue.remove(newRunning);
		}

		if (newRunning != oldRunning) {
			running = newRunning;
			if (oldRunning != null) {
				readyQueue.add(oldRunning);
				mySystemCallHandler.updateMemory(new Pair("ProcessState", State.Ready), getLowerBound(oldRunning),
						getUpperBound(oldRunning), myMemory);
			}
			mySystemCallHandler.printOnScreen(running.toString() + " has been chosen");
			printQueues();
		}
	}	
	public void executeNextInstruction(Memory myMemory) {
		String instruction = getNextInstruction(running, myMemory);
		String[] instParts = instruction.split(" ");
		mySystemCallHandler.printOnScreen("Instruction currently executed: " + instruction);
		int lowerBound = getLowerBound(running);
		int upperBound = getUpperBound(running);
		myinterpreter.execute(instParts, running, userInput, userOutput, file, mySystemCallHandler, messages,
				lowerBound, upperBound, myMemory);
	}

	public void updateBlockedQueue(Memory myMemory) {
		if (!messages.isEmpty()) {
			if (messages.get(0) instanceof Integer) {
				int id = (int) messages.remove(0);
				for (int i = 0; i < blockedQueue.size(); i++) {
					if ((int) processesPCBs.get(blockedQueue.get(i)).get(0) == id) {
						mySystemCallHandler.printOnScreen( blockedQueue.get(i).toString() + " is no longer blocked");
						readyQueue.add(blockedQueue.remove(i));
						printQueues();

						break;
					}
				}
			} else {
				blockedQueue.add(running);
				mySystemCallHandler.updateMemory(new Pair("ProcessState", State.Blocked), getLowerBound(running),
						getUpperBound(running), myMemory);
				running = null;
				mySystemCallHandler.printOnScreen( (Process) messages.remove(0) + " was blocked");
				printQueues();
				
			}
		}
	}

	
	
	
	
	public void checkFinishedProcess(Memory myMemory) {
		if (running != null && running.isFinished()) {
			mySystemCallHandler.printOnScreen(running.toString() + " is finished");
			mySystemCallHandler.updateMemory(new Pair("ProcessState", State.Finished), getLowerBound(running),
					getUpperBound(running), myMemory);
			onMemory.remove(running);
			removeProcessfromMemory(running, myMemory);
			running = null;

		}
	}

	
	
	
	
	
	public Process decideProcessToUnload() {
		int lastUsed = Integer.MAX_VALUE;
		Process toUnload = null;
		for (int i = 0; i < onMemory.size(); i++) {
			if (onMemory.get(i).getLastUsed() < lastUsed && onMemory.get(i).getLastUsed() != 0) {
				lastUsed = onMemory.get(i).getLastUsed();
				toUnload = onMemory.get(i);
			}

		}

		return toUnload;
	}

	
	
	
	
	
	public String getNextInstruction(Process p, Memory myMemory) {
		int lowerBound = getLowerBound(p);
		int upperBound = getUpperBound(p);
		Pair pcData = mySystemCallHandler.checkMemory("ProcessPC", lowerBound, upperBound, myMemory);
		int next = (int) (pcData.value);
		pcData.value = next + 1;
		mySystemCallHandler.updateMemory(pcData, lowerBound, upperBound, myMemory);
		Pair currLine = mySystemCallHandler.checkMemory(next, myMemory);
		if (next == upperBound - 3)
			p.setFinished(true);
		return  (String) currLine.value;
	}

	
	
	
	
	public void putNewProcessOnMemory(Memory myMemory, ArrayList<String> code, ArrayList<Object> PCB) {
		int processSize = code.size() + PCB.size() + 5;
		int lowerBoundry = 0;

		while ((lowerBoundry = myMemory.checkEmptySpace(processSize)) == -1)
			removeProcessfromMemory(decideProcessToUnload(), myMemory);
		ArrayList<Pair> processData = createNewProcessBody(code, PCB, lowerBoundry, processSize);
		myMemory.addProcessToMainMemory(processData, lowerBoundry);

	}

	
	
	
	
	
	public void putOnMemory(Process p, Memory myMemory, ArrayList<Pair> processData) {
		int lowerBoundry = 0;
		while ((lowerBoundry = myMemory.checkEmptySpace(processData.size())) == -1)
			removeProcessfromMemory(decideProcessToUnload(), myMemory);
		modifyProcessData(p, processData, lowerBoundry);
		myMemory.addProcessToMainMemory(processData, lowerBoundry);
	}

	
	
	
	
	public void removeProcessfromMemory(Process toUnload, Memory myMemory) {

		onMemory.remove(toUnload);
		String unloadedProcessBody = mySystemCallHandler.unloadProcessFromMemory(getLowerBound(toUnload),getUpperBound(toUnload), myMemory);
		String id = unloadedProcessBody.split(" ")[1].split("\n")[0];
		mySystemCallHandler.createFileOnDisk("Process " + id, unloadedProcessBody);
		outPut.add((toUnload.isFinished())?toUnload + " has been removed from main memory":toUnload + " has been swapped from main memory to disk");
	}

	
	
	
	public int getLowerBound(Process p) {
		ArrayList<Object> PCB = processesPCBs.get(p);
		return (int) PCB.get(3);
	}

	
	
	
	public int getUpperBound(Process p) {
		ArrayList<Object> PCB = processesPCBs.get(p);
		return (int) PCB.get(4);
	}

	
	
	
	
	public void updateLastUsed() {
		for (int i = 0; i < onMemory.size(); i++) {
			if (onMemory.get(i) != running) {
				onMemory.get(i).setLastUsed(onMemory.get(i).getLastUsed() + 1);
			} else
				onMemory.get(i).setLastUsed(0);
		}
	}

	
	
	
	public void modifyProcessData(Process p, ArrayList<Pair> processData, int lowerBoundry) {
		ArrayList<Object> PCB = processesPCBs.get(p);
		int oldLowerBound = (int) PCB.remove(3);
		processData.remove(3);
		PCB.add(3, lowerBoundry);
		processData.add(3, new Pair("ProcessLowerBoundry", lowerBoundry));
		PCB.remove(4);
		processData.remove(4);
		PCB.add(4, lowerBoundry + processData.size());
		processData.add(4, new Pair("ProcessUpperBoundry", lowerBoundry + processData.size()));
		int oldPC = Integer.parseInt((String) processData.remove(2).value);
		PCB.remove(2);
		PCB.add(2, lowerBoundry + (oldPC - oldLowerBound));
		processData.add(2, new Pair("ProcessPC", lowerBoundry + (oldPC - oldLowerBound)));

	}

	
	
	
	public ArrayList<Pair> createNewProcessBody(ArrayList<String> code, ArrayList<Object> PCB, int lowerBoundry,
			int processSize) {
		ArrayList<Pair> processData = new ArrayList<Pair>();
		processData.add(new Pair("ProcessID", PCB.get(0)));
		processData.add(new Pair("ProcessState", PCB.get(1)));
		processData.add(new Pair("ProcessPC", (int) PCB.get(2) + lowerBoundry));
		processData.add(new Pair("ProcessLowerBoundry", lowerBoundry));
		PCB.add(lowerBoundry);
		processData.add(new Pair("ProcessUpperBoundry", lowerBoundry + processSize - 1));
		PCB.add(lowerBoundry + processSize - 1);
		for (int j = 0; j < code.size(); j++) {
			processData.add(new Pair("Instruction" + j, code.get(j)));
		}
		for (int j = 0; j < 3; j++) {
			processData.add(new Pair("Variable" + j, "Unintialized"));
		}
		return processData;
	}

	
	
	
	
	public void printOutPut() {
		mySystemCallHandler.printOnScreen("             -----");
		mySystemCallHandler.printOnScreen( "Main memory and disk changes: ");
		while(!outPut.isEmpty()) {
			mySystemCallHandler.printOnScreen(outPut.remove(0));
		}
		
	}
	
	
	
	
	
	public void printMemory(Memory myMemory) {
		System.out.println("Main memory Data:  ");
		Pair[] memory = myMemory.getMainMemory();
		for (int i = 0; i < 40; i++) {
			System.out.println("Cell "+ ((i<10)?"0"+i:i)+" : "+((memory[i]==null)?"Empty":memory[i]));
		}
	}

	
	
	
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {

		Scanner scint = new Scanner(System.in);
		System.out.println("Please enter the time slice!");
		int timeSlice = scint.nextInt();
		OS myOS = new OS(timeSlice);
		Memory myMemory = new Memory();
		myOS.execute(myMemory);
	}
}
