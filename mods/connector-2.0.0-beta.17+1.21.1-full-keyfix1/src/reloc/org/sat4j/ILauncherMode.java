package reloc.org.sat4j;

import java.io.PrintWriter;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ILogAble;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.tools.SolutionFoundListener;

public interface ILauncherMode extends SolutionFoundListener {
   String SOLUTION_PREFIX = "v ";
   String ANSWER_PREFIX = "s ";
   String CURRENT_OPTIMUM_VALUE_PREFIX = "o ";
   ILauncherMode DECISION = new DecisionMode();
   ILauncherMode OPTIMIZATION = new OptimizationMode();

   void displayResult(ISolver var1, IProblem var2, ILogAble var3, PrintWriter var4, Reader var5, long var6, boolean var8);

   void solve(IProblem var1, Reader var2, ILogAble var3, PrintWriter var4, long var5);

   void setIncomplete(boolean var1);

   ExitCode getCurrentExitCode();

   void setExitCode(ExitCode var1);
}
