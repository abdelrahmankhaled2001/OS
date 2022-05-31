import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SystemCallHandler {

	public SystemCallHandler() {

	}

	public String getDataFromFile(String directory) {
		String file = "";
		try {
			File f = new File(directory + ".txt");
			Scanner r = new Scanner(f);

			while (r.hasNextLine()) {
				file += r.nextLine() + "\n";
			}
			r.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!!");

		}

		return file;
	}

	public void createFileOnDisk(String name, String data) {
		try {

			File file = new File(name + ".txt");
			if (file.createNewFile()) {
				try {
					FileWriter writer = new FileWriter(name + ".txt");
					writer.write(data);
					writer.close();
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void printOnScreen(Object value) {
		System.out.println(value);
	}

	public String getInput() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter a value");
		return sc.nextLine();
	}

	public Pair checkMemory( String name,int lowerBoundry,int upperBoundry,Memory myMemory) {
		
		
		return myMemory.get(name,lowerBoundry,upperBoundry);
	}
public Pair checkMemory(int index,Memory myMemory) {
		
		
		return myMemory.get(index);
	}

	public void updateMemory(Pair pa,int lowerBoundry,int upperBoundry,Memory myMemory ) {
		
			myMemory.update( pa,lowerBoundry,upperBoundry);
		
	}
	public void updateMemory(int index,Pair p,Memory myMemory ) {
		
		myMemory.update(index, p);
	
}
	public String unloadProcessFromMemory( int lowerBound, int upperBound, Memory myMemory) {
		return myMemory.unload(lowerBound, upperBound);
	}
}
