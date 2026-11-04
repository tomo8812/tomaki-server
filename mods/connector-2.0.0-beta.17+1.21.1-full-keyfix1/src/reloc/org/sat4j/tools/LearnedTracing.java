package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVec;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class LearnedTracing extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private ISolverService solverService;
   private final IVisualizationTool visuTool;

   public LearnedTracing(IVisualizationTool visuTool) {
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
      IVec<? extends IConstr> constrs = this.solverService.getLearnedConstraints();
      int n = constrs.size();

      for (int i = 0; i < n; i++) {
         this.visuTool.addPoint(i, constrs.get(i).getActivity());
      }

      this.visuTool.end();
   }

   @Override
   public void init(ISolverService solverService) {
      this.solverService = solverService;
   }

   @Override
   public void cleaning() {
      this.trace();
   }
}
