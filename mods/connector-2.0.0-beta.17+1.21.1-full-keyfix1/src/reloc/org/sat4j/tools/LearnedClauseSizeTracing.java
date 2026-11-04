package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class LearnedClauseSizeTracing extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private final IVisualizationTool visuTool;
   private int counter;

   public LearnedClauseSizeTracing(IVisualizationTool visuTool) {
      this.visuTool = visuTool;
      this.counter = 0;
   }

   @Override
   public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
      this.visuTool.addPoint(this.counter, confl.size());
      this.counter++;
   }

   @Override
   public void end(Lbool result) {
      this.visuTool.end();
   }
}
