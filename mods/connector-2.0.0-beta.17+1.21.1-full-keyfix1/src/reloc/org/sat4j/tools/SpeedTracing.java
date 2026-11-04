package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class SpeedTracing extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private final IVisualizationTool visuTool;
   private final IVisualizationTool cleanVisuTool;
   private final IVisualizationTool restartVisuTool;
   private long begin;
   private long end;
   private int counter;
   private long index;
   private double maxY;

   public SpeedTracing(IVisualizationTool visuTool, IVisualizationTool cleanVisuTool, IVisualizationTool restartVisuTool) {
      this.visuTool = visuTool;
      this.cleanVisuTool = cleanVisuTool;
      this.restartVisuTool = restartVisuTool;
      visuTool.init();
      cleanVisuTool.init();
      restartVisuTool.init();
      this.begin = System.currentTimeMillis();
      this.counter = 0;
      this.index = 0L;
      this.maxY = 0.0;
   }

   @Override
   public void propagating(int p) {
      this.end = System.currentTimeMillis();
      if (this.end - this.begin >= 2000L) {
         long tmp = this.end - this.begin;
         this.index += tmp;
         double y = this.counter * 1000.0 / tmp;
         if (y > this.maxY) {
            this.maxY = y;
         }

         this.visuTool.addPoint(this.index / 1000.0, y);
         this.cleanVisuTool.addPoint(this.index / 1000.0, 0.0);
         this.restartVisuTool.addPoint(this.index / 1000.0, 0.0);
         this.begin = System.currentTimeMillis();
         this.counter = 0;
      }

      this.counter++;
   }

   @Override
   public void end(Lbool result) {
      this.visuTool.end();
      this.cleanVisuTool.end();
      this.restartVisuTool.end();
   }

   @Override
   public void cleaning() {
      this.end = System.currentTimeMillis();
      long indexClean = this.index + this.end - this.begin;
      this.visuTool.addPoint(indexClean / 1000.0, this.counter * 1000.0 / (this.end - this.begin));
      this.cleanVisuTool.addPoint(indexClean / 1000.0, this.maxY);
      this.restartVisuTool.addInvisiblePoint(indexClean, 0.0);
   }

   @Override
   public void restarting() {
      this.end = System.currentTimeMillis();
      long indexRestart = this.index + this.end - this.begin;
      double y = this.counter * 1000.0 / (this.end - this.begin);
      this.visuTool.addPoint(indexRestart / 1000.0, y);
      if (y > this.maxY) {
         this.maxY = y;
      }

      this.restartVisuTool.addPoint(indexRestart / 1000.0, this.maxY);
      this.cleanVisuTool.addInvisiblePoint(indexRestart, 0.0);
   }

   @Override
   public void start() {
      this.visuTool.init();
      this.cleanVisuTool.init();
      this.restartVisuTool.init();
      this.begin = System.currentTimeMillis();
      this.counter = 0;
      this.index = 0L;
   }
}
