package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class LearnedClausesSizeTracing extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private final IVisualizationTool visuTool;
   private final IVisualizationTool restartTool;
   private final IVisualizationTool cleanTool;
   private int counter;
   private int maxSize;

   public LearnedClausesSizeTracing(IVisualizationTool visuTool, IVisualizationTool restartTool, IVisualizationTool cleanTool) {
      this.visuTool = visuTool;
      this.restartTool = restartTool;
      this.cleanTool = cleanTool;
      this.counter = 0;
      this.maxSize = 0;
   }

   @Override
   public void end(Lbool result) {
      this.visuTool.end();
      this.restartTool.end();
      this.cleanTool.end();
   }

   @Override
   public void learn(IConstr c) {
      int s = c.size();
      if (s > this.maxSize) {
         this.maxSize = s;
      }

      this.visuTool.addPoint(this.counter, s);
      this.restartTool.addInvisiblePoint(this.counter, 0.0);
      this.cleanTool.addInvisiblePoint(this.counter, 0.0);
      this.counter++;
   }

   @Override
   public void start() {
      this.visuTool.init();
      this.restartTool.init();
      this.cleanTool.init();
      this.counter = 0;
      this.maxSize = 0;
   }

   @Override
   public void restarting() {
      this.visuTool.addInvisiblePoint(this.counter, 0.0);
      this.restartTool.addPoint(this.counter, this.maxSize);
      this.cleanTool.addPoint(this.counter, 0.0);
   }

   @Override
   public void cleaning() {
      this.visuTool.addInvisiblePoint(this.counter, 0.0);
      this.restartTool.addPoint(this.counter, 0.0);
      this.cleanTool.addPoint(this.counter, this.maxSize);
   }
}
