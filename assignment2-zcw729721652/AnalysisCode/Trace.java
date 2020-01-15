package aspects;

import java.util.logging.*;

import org.aspectj.lang.*;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.*;

aspect Trace{

        private static Logger logger = Logger.getLogger("Tracing");

        public Trace(){

        try{
                FileHandler handler=new FileHandler("trace.log",false);

                logger.addHandler(handler);

                handler.setFormatter(new Formatter(){

                        public String format(LogRecord record){
                                return record.getMessage()+"\n";
                                }
                                });
                        }

        catch(Exception e){}

        }
        //build the log file and path
        pointcut traceMethods() : (execution(* *(..))&& !cflow(within(Trace)));

        before(): traceMethods(){
                MethodSignature sig = (MethodSignature)thisJoinPointStaticPart.getSignature();
//		String line = "" + thisJoinPointStaticPart.getSourceLocation().getLine();
		String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();
		CodeSignature codeSignature = (CodeSignature) thisJoinPoint.getSignature();

		String[] paramNames = codeSignature.getParameterNames();
		StringBuffer stb = new StringBuffer("[");
		for (int i=0; i<paramNames.length; i++){
			String name = paramNames[i];
			stb.append(name);
		}
		sb.append("]");

		Logger.getLogger("Tracing").log(
			Level.INFO,
			sig.getDeclaringTypeName() + " , " + sig.getName() + " , " + stb);
		}
}
