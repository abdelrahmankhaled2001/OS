import java.util.ArrayList;

public class Interpreter {
	public Interpreter() {

	}

	public void execute(String[] instParts, Process running, Mutex userInput, Mutex userOutput, Mutex file,	SystemCallHandler mySystemCallHandler, ArrayList<Object> x,int lowerBoundry,int upperBoundry,Memory myMemory)
	{
		switch (instParts[0]) {
		case "print":
			print(instParts[1], running, mySystemCallHandler,lowerBoundry,upperBoundry ,myMemory);
			break;
		case "assign":
			assign(instParts[1], instParts[2], instParts, running, mySystemCallHandler,lowerBoundry,upperBoundry ,myMemory);
			break;
		case "writeFile":
			writeFile(instParts[1], instParts[2], running, mySystemCallHandler,lowerBoundry,upperBoundry ,myMemory);
			break;
		case "printFromTo":
			printFromTo(instParts[1], instParts[2], running, mySystemCallHandler,lowerBoundry,upperBoundry ,myMemory);
			break;
		case "semWait":
			semWait(instParts[1], running, userInput, userOutput, file, x,mySystemCallHandler,lowerBoundry,upperBoundry ,myMemory);
			break;
		case "semSignal":
			semSignal(instParts[1], running, userInput, userOutput, file, x,mySystemCallHandler,lowerBoundry,upperBoundry ,myMemory);
			break;
		default: // Question
		}
	}

	private void semSignal(String mutexName, Process running, Mutex userInput, Mutex userOutput, Mutex file,
			ArrayList<Object> x,SystemCallHandler mySystemCallHandler,int lowerBoundry,int upperBoundry,Memory myMemory) {
		int id = -3;
		switch (mutexName) {
		case "userInput":
			id = userInput.semSignal((int)mySystemCallHandler.checkMemory("ProcessID",lowerBoundry,upperBoundry ,myMemory).value);
			break;
		case "userOutput":
			id = userOutput.semSignal((int)mySystemCallHandler.checkMemory( "ProcessID",lowerBoundry,upperBoundry , myMemory).value);
			break;
		case "file":
			id = file.semSignal((int)mySystemCallHandler.checkMemory( "ProcessID", lowerBoundry,upperBoundry ,myMemory).value);
			break;
		default: // question
		}
		if (id == -2) {
			// question
		} else if (id == -3) {
			// question termination ??
		} else {
			if (id != -1) {
				x.add(id);
			}

		}

	}

	private void semWait(String mutexName, Process running, Mutex userInput, Mutex userOutput, Mutex file,
			ArrayList x,SystemCallHandler mySystemCallHandler,int lowerBoundry,int upperBoundry,Memory myMemory) {
		boolean successful = false;
		switch (mutexName) {
		case "userInput":
			successful = userInput.semwait((int)mySystemCallHandler.checkMemory( "ProcessID", lowerBoundry,upperBoundry ,myMemory).value);
			break;
		case "userOutput":
			successful = userOutput.semwait((int)mySystemCallHandler.checkMemory( "ProcessID", lowerBoundry,upperBoundry ,myMemory).value);
			break;
		case "file":
			successful = file.semwait((int)mySystemCallHandler.checkMemory( "ProcessID", lowerBoundry,upperBoundry ,myMemory).value);
			break;
		default: // question
		}
		if (successful == false) {
			x.add(running);

		}

	}

	private void printFromTo(String string1, String string2, Process running, SystemCallHandler mySystemCallHandler,int lowerBoundry,int upperBoundry,Memory myMemory) {
		string1 = (String)mySystemCallHandler.checkMemory( string1,lowerBoundry,upperBoundry ,myMemory).value;
		string2 = (String)mySystemCallHandler.checkMemory(string2,lowerBoundry,upperBoundry ,myMemory).value;

		int first = Integer.parseInt(string1);
		int last = Integer.parseInt(string2);
		int x = (first < last) ? 1 : -1;
		while (first != last) {
			mySystemCallHandler.printOnScreen(first);
			first += x;
		}
		mySystemCallHandler.printOnScreen(first);

	}

	private String readFile(String input, Process running, SystemCallHandler mySystemCallHandler,int lowerBoundry,int upperBoundry,Memory myMemory) {
		String directory = input;
		if (input.equals("input")) {
			mySystemCallHandler.printOnScreen("Please enter the file directory");
			directory = mySystemCallHandler.getInput();
		} else {
			directory =(String) mySystemCallHandler.checkMemory( input,lowerBoundry,upperBoundry ,myMemory).value;
		}
		return mySystemCallHandler.getDataFromFile(directory);

	}

	private void writeFile(String name, String data, Process running, SystemCallHandler mySystemCallHandler,int lowerBoundry,int upperBoundry,Memory myMemory) {
		name =(String) mySystemCallHandler.checkMemory( name,lowerBoundry,upperBoundry ,myMemory).value;
		data = (String)mySystemCallHandler.checkMemory( data,lowerBoundry,upperBoundry ,myMemory).value;
		mySystemCallHandler.createFileOnDisk(name, data);

	}

	private void assign(String name, String value, String[] rest, Process running,	SystemCallHandler mySystemCallHandler,int lowerBoundry,int upperBoundry,Memory myMemory) {
		int j = (value.equals("readFile")) ? 3 : 2;
		String concat = rest[j];
		for (j += 1; j < rest.length; j++) {
			concat += " " + rest[j];
		}

		if (value.equals("input")) {
			int i = getCurrentInstructionIndex(running,lowerBoundry,upperBoundry ,myMemory,mySystemCallHandler);
			Pair p=mySystemCallHandler.checkMemory(i, myMemory);
			
			p.value="assign " + name + " " + mySystemCallHandler.getInput();
			mySystemCallHandler.updateMemory(i, p, myMemory);
			return;

		} else if (value.equals("readFile")) {
			String directory = concat;
			int i = getCurrentInstructionIndex(running,lowerBoundry,upperBoundry ,myMemory,mySystemCallHandler);
Pair p=mySystemCallHandler.checkMemory(i, myMemory);
			
			p.value="assign " + name + " " + readFile(directory, running, mySystemCallHandler,lowerBoundry,upperBoundry ,myMemory);
			mySystemCallHandler.updateMemory(i, p, myMemory);

			
			
			return;

		} else {
			value =(String) mySystemCallHandler.checkMemory( concat,lowerBoundry,upperBoundry ,myMemory).value;

		}

		mySystemCallHandler.updateMemory( new Pair(name, concat),lowerBoundry,upperBoundry ,myMemory);
	}

	private void print(String x, Process running, SystemCallHandler mySystemCallHandler,int lowerBoundry,int upperBoundry,Memory myMemory) {
		mySystemCallHandler.printOnScreen(mySystemCallHandler.checkMemory( x,lowerBoundry,upperBoundry ,myMemory).value);

	}
	public int getCurrentInstructionIndex(Process p,int lowerBoundry,int upperBoundry,Memory myMemory,SystemCallHandler mySystemCallHandler) {
		Pair pcData = mySystemCallHandler.checkMemory( "ProcessPC", lowerBoundry,upperBoundry ,myMemory);
		int next=(int)(pcData.value);
		pcData.value=next-1;
		mySystemCallHandler.updateMemory( pcData,lowerBoundry,upperBoundry , myMemory);
		return next-1;
	}

}
