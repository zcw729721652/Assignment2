import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;


public class TraceToCSV {

	public static void main(String[] args) throws IOException {
		HashMap<String,Integer> total_occurrences = new HashMap<>();
		HashSet<String> always_used = new HashSet<>();
		String [] list  = null;
		String path = "E:\\assignment2-zcw729721652\\freecol\\log\\";
		File folder = new File(path);

		//task1.2a
		//Class: read trace.log and count total occurs of Class
		for (File f : folder.listFiles()) {
			FileReader file = new FileReader(f);
			BufferedReader br1 = new BufferedReader(file);
			String line; 
		
			while ((line = br1.readLine()) != null) {
				list = line.split(", ");
				//System.out.println("class: " + list[0] +" method: "+list[1]);
			
				if(total_occurrences.containsKey(list[0])) {
					//int num = total_occurrences.get(list[0]);
					total_occurrences.replace(list[0], total_occurrences.get(list[0]) + 1);
				}
				else {
					total_occurrences.put(list[0], 1);
				}
			}      
		}
		
		/*
		//task1.2c
		//Method: read trace.log and count total occurs of method
		for (File f : folder.listFiles()) {
			FileReader file = new FileReader(f);
			BufferedReader br2 = new BufferedReader(file);
			String line; 
		
			while ((line = br2.readLine()) != null) {
				list = line.split(", ");
				//System.out.println("class: " + list[0] +" method: "+list[1]);
			
				if(total_occurrences.containsKey(list[1])) {
					//int num = total_occurrences.get(list[0]);
					total_occurrences.replace(list[1], total_occurrences.get(list[1]) + 1);
				}
				else {
					total_occurrences.put(list[1], 1);
				}
			}      
		}
		*/
		//System.out.println(total_occurrences.size());
		
		//task 1.2b
		//Class: ALWAYS USED
		//check if the class occurs in all trace.log
		File[] files = folder.listFiles();
		File firstfile = files[0];
		FileReader frfile = new FileReader(firstfile);
		BufferedReader buff1 = new BufferedReader(frfile);
		String line; 
		
		//add all the class in first file to always used
		while ((line = buff1.readLine()) != null) {
		    list = line.split(", ");
		    always_used.add(list[0]);
		} 
		
		//check if the class occurs in the following trace.log
		//remove the one not occurred from always_used
		for (File f : folder.listFiles()) {
			FileReader file = new FileReader(f);
			BufferedReader buff2 = new BufferedReader(file);
			//String line; 
			while ((line = buff2.readLine()) != null) {
				list = line.split(",");
				if(!always_used.contains(list[0])) {
					always_used.remove(list[0]);
				}
			}
		}
		
		
		/*
		//task1.2d
		//Method: ALWAYS USED
		//check if the class occurs in all trace.log
		File[] files = folder.listFiles();
		File firstfile = files[0];
		FileReader frfile = new FileReader(firstfile);
		BufferedReader buff1 = new BufferedReader(frfile);
		String line; 
		
		//add all the class in first file to always used
		while ((line = buff1.readLine()) != null) {
		    list = line.split(", ");
		    always_used.add(list[1]);
		} 
		
		//check if the method occurs in the following trace.log
		//remove the one not occurred from always_used
		for (File f : folder.listFiles()) {
			FileReader file = new FileReader(f);
			BufferedReader buff2 = new BufferedReader(file);
			//String line; 
			while ((line = buff2.readLine()) != null) {
				list = line.split(",");
				if(!always_used.contains(list[1])) {
					always_used.remove(list[1]);
				}
			}
		}
		*/
		
	
	
		//print to CSV
		/*
		PrintWriter writer = new PrintWriter(new File(
				"E:\\assignment2-zcw729721652\\dataFiles\\classes.csv"));
		String header = "ClassName, TotalOccurrences\n";
		*/
		//set the path of csv
		PrintWriter writer = new PrintWriter(new File(
				"E:\\assignment2-zcw729721652\\dataFiles\\classeswithoccur.csv"));
		String header = "ClassName, TotalOccurrences, AlwaysUsed\n";
		
		/*
		PrintWriter writer = new PrintWriter(new File(
				"E:\\assignment2-zcw729721652\\dataFiles\\methods.csv"));
		String header = "MethodName, TotalOccurrences\n";
		
		
		PrintWriter writer = new PrintWriter(new File(
				"E:\\assignment2-zcw729721652\\dataFiles\\methodwithoccur.csv"));
		String header = "MethodName, TotalOccurrences, AlwaysUsed\n";
		*/
		
		writer.write(header);
		
		for (Entry<String, Integer> entry : total_occurrences.entrySet()) {
			System.out.println(entry.getKey() + "," + entry.getValue());
			StringBuilder info = new StringBuilder();
			info.append(entry.getKey());
			info.append(',');
			info.append(entry.getValue());
			info.append(',');
			
			
			//classes or method with occur
			if(always_used.contains(entry.getKey())) {
				info.append("true");
			}
		   else {
			   	info.append("false");
		   }
		   
			
		   info.append('\n');
		   writer.write(info.toString());
		}
		writer.close();

	}

}
