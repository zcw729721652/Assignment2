import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will write a single CSV file of metrics for a class.
 *
 *
 * Created by neilwalkinshaw on 24/10/2017.
 */
public class ClassMetrics {


    /**
     * First argument is the class name, e.g. /java/awt/geom/Area.class"
     * These seond argument is the name of the target csv file, e.v. "classMetrics.csv"
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //Read in the class given in the argument to a ClassNode.
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        InputStream in=CFGExtractor.class.getResourceAsStream("/java/awt/geom/Area.class");
        ClassReader classReader=new ClassReader(in);
        classReader.accept(cn, 0);

        //Set up the CSV Printer
        FileWriter fw = new FileWriter("outputs/classMetrics2.csv");
        CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.EXCEL);
        String record;
        record = "Method, Nodes, Cyclomatic Complexity\n";
        csvPrinter.printRecord(record);

        for(MethodNode mn : (List<MethodNode>)cn.methods){
            int numNodes = -1;
            int cyclomaticComplexity = -1; // both values default to -1 if they cannot be computed.
            try {
                Graph cfg = CFGExtractor.getCFG(cn.name, mn);
                numNodes = getNodeCount(cfg);
                cyclomaticComplexity = getCyclomaticComplexity(cfg);

            } catch (AnalyzerException e) {
                e.printStackTrace();
            }

            //Write the method details and metrics to the CSV record.
            record = cn.name+"."+mn.name+", "; //Add method signature in first column.
            record += Integer.toString(numNodes)+", ";
            record += Integer.toString(cyclomaticComplexity)+"\n";
            csvPrinter.printRecord(record);
            System.out.println(record.toString());
        }
        csvPrinter.close();
    }

    /**
     * Returns the number of nodes in the CFG.
     * @param cfg
     * @return
     */
    private static int getNodeCount(Graph cfg){
        return cfg.getNodes().size();
    }

    /**
     * Returns the Cyclomatic Complexity by counting the number of branches and adding 1.
     * @param cfg
     * @return
     */
    private static int getCyclomaticComplexity(Graph cfg){
        int branchCount = 0;
        for(Node n : cfg.getNodes()){
            if(cfg.getSuccessors(n).size()>1){
                branchCount ++;
            }
        }
        return branchCount + 1;
    }


}
