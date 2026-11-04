package reloc.org.sat4j.core;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import reloc.org.sat4j.specs.ISolver;

public abstract class ASolverFactory<T extends ISolver> implements Serializable {
   private static final long serialVersionUID = 1L;

   public String[] solverNames() {
      List<String> l = new ArrayList<>();
      Method[] solvers = this.getClass().getDeclaredMethods();

      for (Method solver : solvers) {
         if (solver.getParameterTypes().length == 0 && solver.getName().startsWith("new")) {
            l.add(solver.getName().substring(3));
         }
      }

      Collections.sort(l);
      String[] names = new String[l.size()];
      l.toArray(names);
      return names;
   }

   public T createSolverByName(String solvername) {
      try {
         Class<?>[] paramtypes = new Class[0];
         Method m = this.getClass().getMethod("new" + solvername, paramtypes);
         return (T)m.invoke(null, (Object[])null);
      } catch (SecurityException e) {
         System.err.println(e.getLocalizedMessage());
      } catch (IllegalArgumentException e) {
         System.err.println(e.getLocalizedMessage());
      } catch (NoSuchMethodException e) {
         System.err.println(e.getLocalizedMessage());
      } catch (IllegalAccessException e) {
         System.err.println(e.getLocalizedMessage());
      } catch (InvocationTargetException e) {
         System.err.println(e.getLocalizedMessage());
      }

      return null;
   }

   public abstract T defaultSolver();

   public abstract T lightSolver();
}
