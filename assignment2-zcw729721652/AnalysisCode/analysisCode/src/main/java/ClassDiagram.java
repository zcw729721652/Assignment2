import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class ClassDiagram {
	
	Map<String, String> superClasses = new HashMap<String, String>();
	Map<String, ArrayList<String>> relationships = new HashMap<String, ArrayList<String>>();
	List<String> always_used = new ArrayList<String>();
	HashMap<String, Integer> total_occurrences = new HashMap<String, Integer>();//HashMap to store class and the number of occurrences


	//read all the classes from class.csv
	public static List<String> processClassNames() throws Exception {
		List<String> classes = new ArrayList();
		try {
			//set the path of csv
			BufferedReader br = new BufferedReader(new FileReader("E:\\assignment2-zcw729721652\\dataFiles\\classes.csv"));
			String readLine = br.readLine();
			String line;
			//if file is not null, then read the file
			while((line = br.readLine()) != null) {
				String[] row = line.split(",");
				String classname = row[0];
				//String[] cn = line.split(".");
				String name = classname.replace(".","/");
				//System.out.println(name);
				classes.add(name);
			}    
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classes;
	}
	
	
	public List<ClassNode> processCSVFile(String file) throws Exception {
	
		ArrayList<ClassNode> classes = new ArrayList<ClassNode>();
		List<String> classesfromcsv = processClassNames();
		//set the pat of csv
		String path = "E:\\assignment2-zcw729721652\\freecol\\build\\";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readLine = br.readLine();
			String line;
			while((line = br.readLine()) != null) {
				String[] row = line.split(",");
				String classname = row[0].replace("\"","");
				String isalways_used = row[2];
				int occurence = Integer.parseInt(row[1]);
			
			
				if(classname.contains("junit") || classname.contains("sun.proxy")) {
					continue;
				}
				//String[] cn = line.split(".");
				String cname = classname.replaceAll("\\.","/");
				String cname2 = cname;
				cname= path+cname.trim()+".class";
				
				if(isalways_used.equalsIgnoreCase("true")) {
				//System.out.println(cname2);
					always_used.add(cname2.trim());
				}
				total_occurrences.put(cname2.trim(), occurence);
				
				//System.out.println(name);
				classes.add(loadClass(cname));
			}    
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return classes;
	}
	
	//loading classes
	private static ClassNode loadClass(String className) {
		try {
			ClassNode cn = new ClassNode(Opcodes.ASM4);
			FileInputStream in = new FileInputStream(className);
			ClassReader classReader = new ClassReader(in);
			classReader.accept(cn, 0);
			return cn;
		} 
		catch (Exception e) {
			throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
		}
	}
	
	//task2.1
	public ClassDiagram(String root) throws Exception {
		Set<String> primitives = new HashSet<String>(Arrays.asList( new String[] {"Integer", "Double", "String", "Float", "boolean", "Object", "List"} ));
		File dir = new File(root);
		List<ClassNode> classes = processCSVFile(root);
		// Insert your lab code here.
		String cName, superCName;
		List<FieldNode> cFields;
		ArrayList<String> primitiveFields;
		
		for (ClassNode cl : classes) {
			cName = cl.name;
			superCName = cl.superName;
			cFields = cl.fields;
			primitiveFields = new ArrayList<String>();
			for(FieldNode fl : cFields) {
				String field = fl.desc;
				if(!primitives.contains(field.substring(field.lastIndexOf("/")+1, field.length()-1)) && field.length() > 2) {
					field = field.substring(1);
					field = field.substring(0, field.length()-1);
					primitiveFields.add(field);
				}
			}
			superClasses.put(cName, superCName);
			relationships.put(cName, primitiveFields);
		}
	}
	//task2.2
	//create graph with inheritance and association in
	public void createGraph() throws FileNotFoundException {
		
		Collection<String> classes = new HashSet<String>();//collection to store all classes in the graph
		String graph = "digraph ClassDiagram{ \n" + "graph [splines=ortho, width =0.1, height=0.1]\n\n ";
		
		//inheritance
		for(Entry<String, String> x : getSuperClasses().entrySet()) {
			classes.add(x.getKey());//Add all classes in the Class diagram
			if(x.getKey().equals(x.getValue()))
			continue;
			graph += "\""+x.getKey()+"\" -> \""+x.getValue()+"\"[arrowhead = onormal];\n";
		}
		
		//find and set relationships
		ArrayList<String> allrelationships = new ArrayList();
		for (Entry<String, ArrayList<String>> x : getRelationships().entrySet()) {
			for(String field:getFields(x.getKey())) {
				String classname = x.getKey();
				String relationship = "\""+x.getKey()+"\" -> \""+field+"\"[arrowhead = diamond];\n";
				if(x.getKey().equals(x.getValue()))
					continue;
				if(allrelationships.contains(relationship))
					continue;
				allrelationships.add(relationship);
				graph += "\""+x.getKey()+"\" -> \""+field+"\"[arrowhead = diamond];\n";
			}
		
		}
		//task2.3
		//always used and total_occurrence
		List<Integer> TO = new ArrayList<Integer>();
		for (String x : total_occurrences.keySet()) {
			TO.add(total_occurrences.get(x));
		}
		Collections.sort(TO);
		double Y = TO.size();
		double x = 5/Y;
		
		for(String freecolclass: classes) {
			int Index = TO.indexOf(total_occurrences.get(freecolclass));
		    Index = Index+1;
		    double wh = x * Index;
		    wh=wh+5;
	
			if(always_used.contains(freecolclass)) {
				graph+=  "\""+freecolclass ;
			    graph+="\"[shape = box, style=filled,color=green, width="+wh+",height="+wh+"];\n";
			}
			else {
				graph+="\""+freecolclass+  "\"[shape = box, style=filled, width="+wh+",height="+wh+"];\n";
			}
		}
	
		graph += "}";
		System.out.println(graph);
		PrintWriter writer = new PrintWriter("E:\\assignment2-zcw729721652\\freecol\\classDiagram.dot");
		writer.print(graph);
		writer.close();
	}
	
	
	//get superclass for inheritance
	public Map<String, String> getSuperClasses() {
		return superClasses;
	}
	
	//get relationship for association
	public Map<String,  ArrayList<String>> getRelationships() {
		return relationships;
	}
	
	
	public ArrayList<String> getFields(String classname) {
		return relationships.get(classname);
	}
	
	//task2.4
	public static void main(String[] args) throws Exception {
		String path = "E:\\assignment2-zcw729721652\\dataFiles\\classes.csv";
		ClassDiagram cd = new ClassDiagram(path);
		cd.createGraph();
	}

}