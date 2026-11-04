package reloc.org.sat4j.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListener;

@Feature("searchlistener")
public class MultiTracing<T extends ISolverService> implements SearchListener<T> {
   private static final long serialVersionUID = 1L;
   private final Collection<SearchListener<T>> listeners = new ArrayList<>();

   public MultiTracing(SearchListener<T>... listeners) {
      this.listeners.addAll(Arrays.asList(listeners));
   }

   public MultiTracing(List<SearchListener<T>> listenersList) {
      this.listeners.addAll(listenersList);
   }

   @Override
   public void assuming(int p) {
      for (SearchListener<T> sl : this.listeners) {
         sl.assuming(p);
      }
   }

   @Override
   public void propagating(int p) {
      for (SearchListener<T> sl : this.listeners) {
         sl.propagating(p);
      }
   }

   @Override
   public void enqueueing(int p, IConstr reason) {
      for (SearchListener<T> sl : this.listeners) {
         sl.enqueueing(p, reason);
      }
   }

   @Override
   public void backtracking(int p) {
      for (SearchListener<T> sl : this.listeners) {
         sl.backtracking(p);
      }
   }

   @Override
   public void adding(int p) {
      for (SearchListener<T> sl : this.listeners) {
         sl.adding(p);
      }
   }

   @Override
   public void learn(IConstr c) {
      for (SearchListener<T> sl : this.listeners) {
         sl.learn(c);
      }
   }

   @Override
   public void learnUnit(int p) {
      for (SearchListener<T> sl : this.listeners) {
         sl.learnUnit(p);
      }
   }

   @Override
   public void delete(IConstr c) {
      for (SearchListener<T> sl : this.listeners) {
         sl.delete(c);
      }
   }

   @Override
   public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
      for (SearchListener<T> sl : this.listeners) {
         sl.conflictFound(confl, dlevel, trailLevel);
      }
   }

   @Override
   public void conflictFound(int p) {
      for (SearchListener<T> sl : this.listeners) {
         sl.conflictFound(p);
      }
   }

   @Override
   public void solutionFound(int[] model, RandomAccessModel lazyModel) {
      for (SearchListener<T> sl : this.listeners) {
         sl.solutionFound(model, lazyModel);
      }
   }

   @Override
   public void beginLoop() {
      for (SearchListener<T> sl : this.listeners) {
         sl.beginLoop();
      }
   }

   @Override
   public void start() {
      for (SearchListener<T> sl : this.listeners) {
         sl.start();
      }
   }

   @Override
   public void end(Lbool result) {
      for (SearchListener<T> sl : this.listeners) {
         sl.end(result);
      }
   }

   @Override
   public void restarting() {
      for (SearchListener<T> sl : this.listeners) {
         sl.restarting();
      }
   }

   @Override
   public void backjump(int backjumpLevel) {
      for (SearchListener<T> sl : this.listeners) {
         sl.backjump(backjumpLevel);
      }
   }

   @Override
   public void init(T solverService) {
      for (SearchListener<T> sl : this.listeners) {
         sl.init(solverService);
      }
   }

   @Override
   public void cleaning() {
      for (SearchListener<T> sl : this.listeners) {
         sl.cleaning();
      }
   }
}
