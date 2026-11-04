package reloc.org.sat4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;
import reloc.org.sat4j.core.ASolverFactory;
import reloc.org.sat4j.minisat.core.ICDCL;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.minisat.orders.OrientedOrder;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.reader.Reader;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ILogAble;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.SearchListener;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.DotSearchTracing;
import reloc.org.sat4j.tools.ModelIteratorToSATAdapter;
import reloc.org.sat4j.tools.RupSearchListener;
import reloc.org.sat4j.tools.SearchEnumeratorListener;
import reloc.org.sat4j.tools.SearchMinOneListener;
import reloc.org.sat4j.tools.SolverDecorator;

public abstract class AbstractLauncher implements Serializable, ILogAble {
   private static final long serialVersionUID = 1L;
   public static final String COMMENT_PREFIX = "c ";
   protected long beginTime;
   protected transient Reader reader;
   protected boolean feedWithDecorated = false;
   protected transient PrintWriter out = new PrintWriter(System.out, true);
   private StringBuilder logBuffer;
   private boolean displaySolutionLine = true;
   protected transient Thread shutdownHook = new Thread() {
      @Override
      public void run() {
         if (AbstractLauncher.this.solver != null) {
            AbstractLauncher.this.solver.expireTimeout();
         }

         AbstractLauncher.this.displayResult();
      }
   };
   protected ISolver solver;
   protected IProblem problem;
   private boolean silent = false;
   protected boolean prime = System.getProperty("prime") != null;
   private ILauncherMode launcherMode = ILauncherMode.DECISION;

   protected void setLauncherMode(ILauncherMode launcherMode) {
      this.launcherMode = launcherMode;
   }

   protected ILauncherMode getLauncherMode() {
      return this.launcherMode;
   }

   protected void setIncomplete(boolean isIncomplete) {
      this.launcherMode.setIncomplete(isIncomplete);
   }

   public final void addHook() {
      Runtime.getRuntime().addShutdownHook(this.shutdownHook);
   }

   protected void displayResult() {
      this.launcherMode.displayResult(this.solver, this.problem, this, this.out, this.reader, this.beginTime, this.displaySolutionLine);
   }

   public abstract void usage();

   protected final void displayHeader() {
      this.displayLicense();
      URL url = AbstractLauncher.class.getResource("/sat4j.version");
      if (url == null) {
         this.log("no version file found!!!");
      } else {
         try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            this.log("version " + in.readLine());
         } catch (IOException e) {
            this.log("c ERROR: " + e.getMessage());
         }
      }

      Properties prop = System.getProperties();
      String[] infoskeys = new String[]{
         "java.runtime.name", "java.vm.name", "java.vm.version", "java.vm.vendor", "sun.arch.data.model", "java.version", "os.name", "os.version", "os.arch"
      };

      for (String key : infoskeys) {
         this.log(key + (key.length() < 14 ? "\t\t" : "\t") + prop.getProperty(key));
      }

      Runtime runtime = Runtime.getRuntime();
      this.log("Free memory \t\t" + runtime.freeMemory());
      this.log("Max memory \t\t" + runtime.maxMemory());
      this.log("Total memory \t\t" + runtime.totalMemory());
      this.log("Number of processors \t" + runtime.availableProcessors());
   }

   public void displayLicense() {
      this.log("SAT4J: a SATisfiability library for Java (c) 2004-2013 Artois University and CNRS");
      this.log("This is free software under the dual EPL/GNU LGPL licenses.");
      this.log("See www.sat4j.org for details.");
   }

   protected IProblem readProblem(String problemname) throws ParseFormatException, IOException, ContradictionException {
      this.log("solving " + problemname);
      this.log("reading problem ... ");
      SolverDecorator<ISolver> decorator = null;
      ISolver originalProblem;
      if (this.feedWithDecorated) {
         decorator = (SolverDecorator<ISolver>)this.solver;
         originalProblem = decorator.decorated();
      } else {
         originalProblem = this.solver;
      }

      this.reader = this.createReader(originalProblem, problemname);
      IProblem aProblem = this.reader.parseInstance(problemname);
      if (this.reader.hasAMapping()) {
         SearchListener<?> listener = this.solver.getSearchListener();
         if (listener instanceof DotSearchTracing) {
            ((DotSearchTracing)listener).setMapping(this.reader.getMapping());
         }
      }

      this.log("... done. Wall clock time " + (System.currentTimeMillis() - this.beginTime) / 1000.0 + "s.");
      this.log("declared #vars     " + aProblem.nVars());
      if (this.solver.nVars() < this.solver.realNumberOfVariables()) {
         this.log("internal #vars     " + this.solver.realNumberOfVariables());
      }

      this.log("#constraints  " + aProblem.nConstraints());
      aProblem.printInfos(this.out);
      if (System.getProperty("UNSATPROOF") != null) {
         String proofFile = problemname + ".rupproof";
         this.solver.setSearchListener(new RupSearchListener(proofFile));
         if (!this.isSilent()) {
            System.out.println(this.solver.getLogPrefix() + "Generating unsat proof in file " + proofFile);
         }
      }

      if (System.getProperty("forceorder") != null) {
         String orderFile = problemname + ".order";
         ICDCL solverService = (ICDCL)this.solver;
         IOrder order = new OrientedOrder(orderFile, solverService.getOrder());
         solverService.setOrder(order);
         if (!this.isSilent()) {
            System.out.println(this.solver.getLogPrefix() + "Forcing heuristics using file " + orderFile);
         }
      }

      return this.feedWithDecorated ? decorator : aProblem;
   }

   protected abstract Reader createReader(ISolver var1, String var2);

   public void run(String[] args) {
      try {
         this.displayHeader();
         this.solver = this.configureSolver(args);
         if (this.solver == null) {
            this.usage();
            return;
         }

         if (!this.isSilent()) {
            this.solver.setVerbose(true);
         }

         this.configureLauncher();
         String instanceName = this.getInstanceName(args);
         if (instanceName == null) {
            this.usage();
            return;
         }

         this.beginTime = System.currentTimeMillis();
         this.problem = this.readProblem(instanceName);
         this.solve(this.problem);
      } catch (TimeoutException e) {
         this.log("timeout");
      } catch (ContradictionException e) {
         this.launcherMode.setExitCode(ExitCode.UNSATISFIABLE);
         this.log("(trivial inconsistency)");
      } catch (IOException | ParseFormatException e) {
         System.err.println("FATAL " + e.getLocalizedMessage());
      }
   }

   protected void configureLauncher() {
      String all = System.getProperty("all");
      if (all != null) {
         if ("external".equals(all)) {
            this.feedWithDecorated = true;
            this.solver = new ModelIteratorToSATAdapter(this.solver, this.launcherMode);
            System.out.println(this.solver.getLogPrefix() + "model enumeration using the external way");
         } else {
            SearchEnumeratorListener enumerator = new SearchEnumeratorListener(this.launcherMode);
            this.solver.setSearchListener(enumerator);
            System.out.println(this.solver.getLogPrefix() + "model enumeration using the internal way");
         }
      }

      if (System.getProperty("minone") != null) {
         SearchMinOneListener minone = new SearchMinOneListener(this.launcherMode);
         this.solver.setSearchListener(minone);
      }
   }

   protected abstract String getInstanceName(String[] var1);

   protected abstract ISolver configureSolver(String[] var1);

   @Override
   public void log(String message) {
      if (!this.isSilent()) {
         if (this.logBuffer != null) {
            this.logBuffer.append("c ").append(message).append('\n');
         } else {
            this.out.println("c " + message);
         }
      }
   }

   protected void solve(IProblem problem) throws TimeoutException {
      this.launcherMode.solve(problem, this.reader, this, this.out, this.beginTime);
   }

   protected void setDisplaySolutionLine(boolean value) {
      this.displaySolutionLine = value;
   }

   public final void setExitCode(ExitCode exitCode) {
      this.launcherMode.setExitCode(exitCode);
   }

   public final ExitCode getExitCode() {
      return this.launcherMode.getCurrentExitCode();
   }

   public final long getBeginTime() {
      return this.beginTime;
   }

   public final Reader getReader() {
      return this.reader;
   }

   public void setLogWriter(PrintWriter out) {
      this.out = out;
   }

   public PrintWriter getLogWriter() {
      return this.out;
   }

   protected void setSilent(boolean b) {
      this.silent = b;
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.out = new PrintWriter(System.out, true);
      this.shutdownHook = new Thread() {
         @Override
         public void run() {
            AbstractLauncher.this.displayResult();
         }
      };
   }

   protected <T extends ISolver> void showAvailableSolvers(ASolverFactory<T> afactory) {
      this.showAvailableSolvers(afactory, "");
   }

   protected <T extends ISolver> void showAvailableSolvers(ASolverFactory<T> afactory, String framework) {
      if (afactory != null) {
         if (framework.length() > 0) {
            this.log("Available solvers for " + framework + ": ");
         } else {
            this.log("Available solvers: ");
         }

         String[] names = afactory.solverNames();

         for (String name : names) {
            this.log(name);
         }
      }
   }

   protected void bufferizeLog() {
      this.logBuffer = new StringBuilder();
   }

   protected void flushLog() {
      if (this.logBuffer != null) {
         this.out.print(this.logBuffer.toString());
      }

      this.logBuffer = null;
   }

   public boolean isSilent() {
      return this.silent;
   }
}
