package reloc.org.sat4j.specs;

import reloc.org.sat4j.annotations.Feature;

@Feature("searchlistener")
public abstract class SearchListenerAdapter<S extends ISolverService> implements SearchListener<S> {
   private static final long serialVersionUID = 1L;

   @Override
   public void init(S solverService) {
   }

   @Override
   public void assuming(int p) {
   }

   @Override
   public void propagating(int p) {
   }

   @Override
   public void enqueueing(int p, IConstr reason) {
   }

   @Override
   public void backtracking(int p) {
   }

   @Override
   public void adding(int p) {
   }

   @Override
   public void learn(IConstr c) {
   }

   @Override
   public void learnUnit(int p) {
   }

   @Override
   public void delete(IConstr c) {
   }

   @Override
   public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
   }

   @Override
   public void conflictFound(int p) {
   }

   @Override
   public void solutionFound(int[] model, RandomAccessModel lazyModel) {
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
   }

   @Override
   public void backjump(int backjumpLevel) {
   }

   @Override
   public void cleaning() {
   }
}
