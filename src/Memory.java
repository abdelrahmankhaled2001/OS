import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Memory {
private final int mainMemoryMaxSize=40;
private  Pair [] mainMemory=new Pair[mainMemoryMaxSize];
public boolean addNewProcessToMainMemory(ArrayList<String>code,ArrayList<Object>PCB) {
	
		
	
	return false;
}
public void addProcessToMainMemory(ArrayList<Pair>processData,int lowerBoundry) {
	
	if(lowerBoundry!=-1) {
		
		while(!processData.isEmpty()) {
			mainMemory[lowerBoundry++]=processData.remove(0);
		
		}
	}
	
}
public int checkEmptySpace(int processSize) {
	int availableSpace=0;
	for(int i=0;i<mainMemory.length;i++) {
		int startIndex=i;
			while(i<mainMemory.length&&mainMemory[i]==null) {
				availableSpace++;
				i++;
			}
			
			if(availableSpace>=processSize)
				return startIndex;
		    availableSpace=0;
		
	}
	return -1;
}
public String unload(int lowerBoundry,int upperBoundry) {
	String  processData="";
	for(int i=lowerBoundry;i<=upperBoundry;i++) {
		processData+=mainMemory[i].toString()+"\n";
		mainMemory[i]=null;
	}
	return processData;
}
public void update(Pair pair,int lowerBound,int upperBound) {
	
	
	for(int i=lowerBound;i<=upperBound;i++) {
		if(mainMemory[i].name.equals(pair.name)||mainMemory[i].value.equals("Unintialized")) {
			mainMemory[i]=pair;
			return ;
		}
	}
	
	
}
public Pair get(String name,int lowerBound,int upperBound) {
	

	
	for(int i=lowerBound;i<=upperBound;i++) {
		if(mainMemory[i].name.equals(name)) {
			return mainMemory[i];
		}
	}
	return new Pair("notFound",name);
}
public Pair get(int index) {
	return mainMemory[index];
}
public Pair[] getMainMemory() {
	return mainMemory;
}
public void setMainMemory(Pair[] mainMemory) {
	this.mainMemory = mainMemory;
}
public void update(int index,Pair p) {
	mainMemory[index]=p;
}

}
