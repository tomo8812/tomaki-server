package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class HeuristicsTracing extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private ISolverService solverService;
   private final IVisualizationTool visuTool;

   public HeuristicsTracing(IVisualizationTool visuTool) {
      this.visuTool = visuTool;
   }

   @Override
   public void solutionFound(int[] model, RandomAccessModel lazyModel) {
      this.trace();
   }

   @Override
   public void restarting() {
      this.trace();
   }

   private void trace() {
      this.visuTool.init();
      int n = this.solverService.nVars();
      double[] heuristics = this.solverService.getVariableHeuristics();

      for (int i = 1; i <= n; i++) {
         this.visuTool.addPoint(heuristics[i], i);
      }

      this.visuTool.end();
   }

   @Override
   public void init(ISolverService solverService) {
      this.solverService = solverService;
   }
}
