package dependenceAnalysis.util.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class CFGMetrics extends CFGExtractor {

	// gets the number of instructions in a method
	public static int getCFGInstructions(String owner, MethodNode mn) throws AnalyzerException {
		final Graph g = buildGraph(owner, mn);
		return g.getNodes().size();
	}

	// gets the number of branches in a method
	public static int getCFGBranches(String owner, MethodNode mn) throws AnalyzerException {
		final Graph g = buildGraph(owner, mn);
		int branches = 0;
		for (Node n : g.getNodes()) {
			if (g.getSuccessors(n).size() > 1) {
				branches++;
			}
		}
		return branches;
	}

	// gets the cyclomatic complexity of a method
	public static int getCFGCyclomatic(String owner, MethodNode mn) throws AnalyzerException {
		return getCFGBranches(owner, mn) + 1;
	}

	// gets the number of attributes in a method
	public int getNumInstructions(ClassNode owner) throws AnalyzerException {
		// return owner.attrs.size();
		return owner.fields.size();
	}

	// gets the number of methods in a class
	public int getNumMethods(ClassNode owner) throws AnalyzerException {
		return owner.methods.size();
	}

	// gets the Halstead Complexity of a methods in a class
	public int getHaslteadComplexity(ClassNode owner) throws AnalyzerException {
		// Give it a TRY
		
		return 0;
	}

	public static void main(String[] args) throws IOException {
		String path = "../jfreechart/target/classes/org/jfree/chart/ChartColor.class";
		File file = new File(path);

		ClassNode cn = new ClassNode(Opcodes.ASM4);
		InputStream in = new FileInputStream(file);
		ClassReader classReader = new ClassReader(in);
		classReader.accept(cn, 0);

		for (MethodNode mn : (List<MethodNode>) cn.methods) {
			try {
				System.out
						.println(mn.name + ", " + getCFGInstructions(cn.name, mn) + ", " + getCFGBranches(cn.name, mn));
			} catch (AnalyzerException e) {
				e.printStackTrace();
			}
		}
	}
}
