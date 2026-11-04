package reloc.org.sat4j.tools;

import java.io.Serializable;

public interface IVisualizationTool extends Serializable {
   Integer NOTGOOD = Integer.MIN_VALUE;

   void addPoint(double var1, double var3);

   void addInvisiblePoint(double var1, double var3);

   void init();

   void end();
}
