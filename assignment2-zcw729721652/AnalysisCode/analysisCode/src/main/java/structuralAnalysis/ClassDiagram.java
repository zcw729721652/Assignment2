package structuralAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static List<ClassNode> processDirectory(File directory, String pkgname) {

		ArrayList<ClassNode> classes = new ArrayList<ClassNode>();

		String prefix = pkgname + "/";
		if (pkgname.equals(""))
			prefix = "";

		// Get the list of the files contained in the package
		String[] files = directory.list();

		for (int i = 0; i < files.length; i++) {
			String fileName = files[i];
			String className = null;

			// we are only interested in .class files
			if (fileName.endsWith(".class")) {
				className = prefix + fileName;
			}

			if (className != null) {
				classes.add(loadClass(className));
			}

			// If the file is a directory recursively class this method.
			File subdir = new File(directory, fileName);
			if (subdir.isDirectory()) {

				classes.addAll(processDirectory(subdir, prefix + fileName));
			}
		}
		return classes;
	}

	private static ClassNode loadClass(String className) {
		try {
			ClassNode cn = new ClassNode(Opcodes.ASM4);
			FileInputStream in = new FileInputStream(className);
			ClassReader classReader = new ClassReader(in);
			classReader.accept(cn, 0);
			return cn;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
		}
	}

	public ClassDiagram(String root) {
		Set<String> primitives = new HashSet<String>(Arrays.asList( new String[] {"Integer", "Double", "String", "Float", "boolean", "Object", "List"} ));
		File dir = new File(root);
		List<ClassNode> classes = processDirectory(dir, root);
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
	
	public void createGraph() {
		String graph = "digraph ClassDiagram{ \n";
		for (Entry<String, String> x : getSuperClasses().entrySet()) {
			graph += "\""+x.getKey()+"\" -> \""+x.getValue()+"\"\n";
		}
		
		//write codes to add the field's relationships to the graph
		for (Entry<String, ArrayList<String>> y : relationships.entrySet()) {
			graph += "\""+y.getKey()+"\" -<> \""+y.getValue()+"\"\n";
		}
		
		graph += "}";
		System.out.println(graph);
		
		
	}

	public Map<String, String> getSuperClasses() {
		return superClasses;
	}

	public ArrayList<String> getFields(String classname) {
		return relationships.get(classname);
	}

	public static void main(String[] args) throws IOException {
		String path = "../../freecol/build/net/sf/freecol/common/model";
		ClassDiagram cd = new ClassDiagram(path);
		cd.createGraph();
		
	}

}