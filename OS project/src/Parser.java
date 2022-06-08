import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
	public Parser() {

	}

	public ArrayList<String> getProcessBody(String Path) {
		ArrayList<String> instructions = new ArrayList<String>();
		try {
			File f = new File(Path);
			Scanner r = new Scanner(f);

			while (r.hasNextLine()) {
				instructions.add(r.nextLine());
			}
			r.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!!");

		}

		return instructions;

	}
	public ArrayList<Pair> getProcess(String Path) {
		ArrayList<Pair> processData = new ArrayList<Pair>();
		try {
			File f = new File(Path+".txt");
			Scanner r = new Scanner(f);

			while (r.hasNextLine()) {
				String line=r.nextLine();
				String name=line.split(" ")[0];
				String value=line.substring(name.length()+1);
				if(name.equals("ProcessID"))
				processData.add(new Pair(name,Integer.parseInt(value)));
				else
					processData.add(new Pair(name,value));
			}
			r.close();
			f.delete();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!!");

		}

		return processData;

	}

}
