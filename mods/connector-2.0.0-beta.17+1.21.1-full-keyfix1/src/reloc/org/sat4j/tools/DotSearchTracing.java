package reloc.org.sat4j.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListenerAdapter;
import reloc.org.sat4j.specs.VarMapper;

@Feature("searchlistener")
public class DotSearchTracing<T> extends SearchListenerAdapter<ISolverService> implements VarMapper {
   private static final long serialVersionUID = 1L;
   private final Vec<String> stack;
   private String currentNodeName = null;
   private transient Writer out;
   private boolean assertive = false;
   private Map<Integer, T> mapping;

   public DotSearchTracing(String fileNameToSave) {
      this.stack = new Vec<>();

      try {
         this.out = new FileWriter(fileNameToSave);
      } catch (IOException e) {
         System.err.println("Problem when created file.");
      }
   }

   public void setMapping(Map<Integer, T> mapping) {
      this.mapping = mapping;
   }

   @Override
   public String map(int dimacs) {
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
   public final void assuming(int p) {
      int absP = Math.abs(p);
      String newName;
      if (this.currentNodeName == null) {
         newName = Integer.toString(absP);
         this.stack.push(newName);
         this.saveLine(this.lineTab("\"" + newName + "\"[label=\"" + this.map(p) + "\", shape=circle, color=blue, fontcolor=white, style=filled]"));
      } else {
         newName = this.currentNodeName;
         this.stack.push(newName);
         this.saveLine(this.lineTab("\"" + newName + "\"[label=\"" + this.map(p) + "\", shape=circle, color=blue, fontcolor=white, style=filled]"));
      }

      this.currentNodeName = newName;
   }

   @Override
   public final void propagating(int p) {
      String newName = this.currentNodeName + "." + p + "." + this.assertive;
      if (this.currentNodeName == null) {
         this.saveLine(this.lineTab("\"null\" [label=\"\", shape=point]"));
      }

      String couleur = this.assertive ? "orange" : "green";
      this.saveLine(this.lineTab("\"" + newName + "\"[label=\"" + this.map(p) + "\",shape=point, color=black]"));
      this.saveLine(
         this.lineTab(
            "\""
               + this.currentNodeName
               + "\" -- \""
               + newName
               + "\"[label=\" "
               + this.map(p)
               + "\", fontcolor ="
               + couleur
               + ", color = "
               + couleur
               + ", style = bold]"
         )
      );
      this.currentNodeName = newName;
      this.assertive = false;
   }

   @Override
   public final void enqueueing(int p, IConstr reason) {
      if (reason != null) {
         String newName = this.currentNodeName + "." + p + ".propagated." + this.assertive;
         this.saveLine(this.lineTab("\"" + newName + "\"[label=\"" + this.map(p) + "\",shape=point, color=black]"));
         this.saveLine(
            this.lineTab(
               "\"" + this.currentNodeName + "\" -- \"" + newName + "\"[label=\" " + this.map(p) + "\", fontcolor = gray, color = gray, style = bold]"
            )
         );
         String reasonName = newName + ".reason";
         this.saveLine(this.lineTab("\"" + reasonName + "\" [label=\"" + reason.toString(this) + "\", shape=box, color=\"gray\", style=dotted]"));
         this.saveLine("\"" + reasonName + "\"--\"" + this.currentNodeName + "\"[label=\"\", color=gray, style=dotted]");
         this.currentNodeName = newName;
      }
   }

   @Override
   public final void backtracking(int p) {
      String temp = this.stack.last();
      this.stack.pop();
      this.saveLine("\"" + temp + "\"--\"" + this.currentNodeName + "\"[label=\"\", color=red, style=dotted]");
      this.currentNodeName = temp;
   }

   @Override
   public final void adding(int p) {
      this.assertive = true;
   }

   @Override
   public final void learn(IConstr constr) {
      String learned = this.currentNodeName + "_learned";
      this.saveLine(this.lineTab("\"" + learned + "\" [label=\"" + constr.toString(this) + "\", shape=box, color=\"orange\", style=dotted]"));
      this.saveLine("\"" + learned + "\"--\"" + this.currentNodeName + "\"[label=\"\", color=orange, style=dotted]");
   }

   @Override
   public final void delete(IConstr c) {
   }

   @Override
   public final void conflictFound(IConstr confl, int dlevel, int trailLevel) {
      this.saveLine(
         this.lineTab("\"" + this.currentNodeName + "\" [label=\"" + confl.toString(this) + "\", shape=box, color=\"red\", fontcolor=white, style=filled]")
      );
   }

   @Override
   public final void conflictFound(int p) {
      this.saveLine(this.lineTab("\"" + this.currentNodeName + "\" [label=\"\", shape=box, color=\"red\", style=filled]"));
   }

   @Override
   public final void solutionFound(int[] model, RandomAccessModel lazyModel) {
      this.saveLine(this.lineTab("\"" + this.currentNodeName + "\" [label=\"\", shape=box, color=\"green\", style=filled]"));
   }

   @Override
   public final void beginLoop() {
   }

   @Override
   public final void start() {
      this.saveLine("graph G {");
   }

   @Override
   public final void end(Lbool result) {
      this.saveLine("}");
   }

   private String lineTab(String line) {
      return "\t" + line;
   }

   private void saveLine(String line) {
      try {
         this.out.write(line + '\n');
         if ("}".equals(line)) {
            this.out.close();
         }
      } catch (IOException e) {
         Logger.getLogger("reloc.org.sat4j.core").log(Level.INFO, "Something went wrong when saving dot file", e);
      }
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.out = new PrintWriter(System.out);
   }
}
