package reloc.org.sat4j.tools;

import java.util.Map;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListener;

@Feature("searchlistener")
public class TextOutputTracing<T> implements SearchListener<ISolverService> {
   private static final long serialVersionUID = 1L;
   private final Map<Integer, T> mapping;

   public TextOutputTracing(Map<Integer, T> mapping) {
      this.mapping = mapping;
   }

   private String node(int dimacs) {
      if (this.mapping != null) {
         int var = Math.abs(dimacs);
         T t = this.mapping.get(var);
         if (t != null) {
            if (dimacs > 0) {
               return t.toString();
            }

            return "-" + t.toString();
         }
      }

      return Integer.toString(dimacs);
   }

   @Override
   public void assuming(int p) {
      System.out.println("assuming " + this.node(p));
   }

   @Override
   public void propagating(int p) {
      System.out.println("propagating " + this.node(p));
   }

   @Override
   public void enqueueing(int p, IConstr reason) {
      System.out.println("enqueueing " + this.node(p));
   }

   @Override
   public void backtracking(int p) {
      System.out.println("backtracking " + this.node(p));
   }

   @Override
   public void adding(int p) {
      System.out.println("adding " + this.node(p));
   }

   @Override
   public void learn(IConstr clause) {
      System.out.println("learning " + clause);
   }

   @Override
   public void learnUnit(int p) {
      System.out.println("learning unit " + p);
   }

   @Override
   public void delete(IConstr c) {
   }

   @Override
   public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
      System.out.println("conflict ");
   }

   @Override
   public void conflictFound(int p) {
      System.out.println("conflict during propagation");
   }

   @Override
   public void solutionFound(int[] model, RandomAccessModel lazyModel) {
      System.out.println("solution found ");
   }

   @Override
   public void beginLoop() {
   }

   @Override
   public void start() {
   }

   @Override
   public void end(Lbool result) {
   }

   @Override
   public void restarting() {
      System.out.println("restarting ");
   }

   @Override
   public void backjump(int backjumpLevel) {
      System.out.println("backjumping to decision level " + backjumpLevel);
   }

   @Override
   public void init(ISolverService solverService) {
   }

   @Override
   public void cleaning() {
      System.out.println("cleaning");
   }
}
