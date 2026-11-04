package reloc.org.sat4j.pb;

import java.io.IOException;
import reloc.org.sat4j.AbstractLauncher;
import reloc.org.sat4j.ILauncherMode;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.pb.core.ObjectiveReducerPBSolverDecorator;
import reloc.org.sat4j.pb.reader.OPBReader2006;
import reloc.org.sat4j.pb.tools.SearchOptimizerListener;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ILogAble;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.tools.SolverDecorator;

public class LanceurPseudo2005 extends AbstractLauncher implements ILogAble {
   ASolverFactory<IPBSolver> factory;
   private static final long serialVersionUID = 1L;
   protected ObjectiveFunction obfct;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !LanceurPseudo2005.class.desiredAssertionStatus();

   public LanceurPseudo2005() {
      this(SolverFactory.instance());
   }

   LanceurPseudo2005(ASolverFactory<IPBSolver> factory) {
      this.factory = factory;
      this.setLauncherMode(ILauncherMode.OPTIMIZATION);
   }

   public static void main(String[] args) {
      AbstractLauncher lanceur = new LanceurPseudo2005();
      lanceur.addHook();
      lanceur.run(args);
      System.exit(lanceur.getExitCode().value());
   }

   @Override
   protected Reader createReader(ISolver theSolver, String problemname) {
      return new OPBReader2006((IPBSolver)theSolver);
   }

   @Override
   protected ISolver configureSolver(String[] args) {
      String solverName = args[0];
      boolean lower = false;
      if (solverName.startsWith("Lower")) {
         lower = true;
         solverName = solverName.substring("Lower".length());
      }

      IPBSolver theSolver;
      if (args.length > 1) {
         theSolver = this.factory.createSolverByName(solverName);
      } else {
         theSolver = this.factory.defaultSolver();
      }

      if (System.getProperty("OBJREDUCER") != null) {
         if (lower) {
            theSolver = new ConstraintRelaxingPseudoOptDecorator(new ObjectiveReducerPBSolverDecorator(theSolver));
         } else {
            theSolver = new PseudoOptDecorator(new ObjectiveReducerPBSolverDecorator(theSolver));
         }
      } else if (System.getProperty("INTERNAL") != null) {
         theSolver.setSearchListener(new SearchOptimizerListener(ILauncherMode.DECISION));
         this.setLauncherMode(ILauncherMode.DECISION);
      } else if (lower) {
         theSolver = new ConstraintRelaxingPseudoOptDecorator(theSolver);
      } else {
         theSolver = new PseudoOptDecorator(theSolver);
      }

      if (args.length == 3) {
         theSolver.setTimeout(Integer.valueOf(args[1]));
      }

      this.out.println(theSolver.toString("c "));
      return theSolver;
   }

   @Override
   public void usage() {
      this.out.println("java -jar sat4j-pb.jar [solvername [timeout]] instancename.opb");
      this.showAvailableSolvers(SolverFactory.instance());
   }

   @Override
   protected String getInstanceName(String[] args) {
      assert args.length == 1 || args.length == 2 || args.length == 3;
      return args.length == 0 ? null : args[args.length - 1];
   }

   @Override
   protected IProblem readProblem(String problemname) throws ParseFormatException, IOException, ContradictionException {
      IProblem problem = super.readProblem(problemname);
      ObjectiveFunction obj = null;
      if (super.feedWithDecorated) {
         SolverDecorator<IPBSolver> decorator = (SolverDecorator<IPBSolver>)problem;
         obj = decorator.decorated().getObjectiveFunction();
      } else {
         obj = ((IPBSolver)problem).getObjectiveFunction();
      }

      if (obj != null) {
         this.out.println("c objective function length is " + obj.getVars().size() + " literals");
      }

      return problem;
   }
}
