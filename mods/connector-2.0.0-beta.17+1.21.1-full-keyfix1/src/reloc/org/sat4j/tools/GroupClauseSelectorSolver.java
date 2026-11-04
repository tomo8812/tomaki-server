package reloc.org.sat4j.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.IGroupSolver;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

@Feature("solver")
public class GroupClauseSelectorSolver<T extends ISolver> extends AbstractClauseSelectorSolver<T> implements IGroupSolver {
   private static final long serialVersionUID = 1L;
   private final Map<Integer, Integer> varToHighLevel = new HashMap<>();
   private final Map<Integer, Integer> highLevelToVar = new HashMap<>();

   public GroupClauseSelectorSolver(T solver) {
      super(solver);
   }

   public IConstr addControlableClause(IVecInt literals, int desc) throws ContradictionException {
      if (desc == 0) {
         return super.addClause(literals);
      }

      Integer hlvar = this.getGroupVar(literals, desc);
      literals.push(hlvar);
      return super.addClause(literals);
   }

   protected Integer getGroupVar(IVecInt literals, int groupid) {
      Integer hlvar = this.highLevelToVar.get(groupid);
      if (hlvar == null) {
         hlvar = this.createNewVar(literals);
         this.highLevelToVar.put(groupid, hlvar);
         this.varToHighLevel.put(hlvar, groupid);
      }

      return hlvar;
   }

   public IConstr addNonControlableClause(IVecInt literals) throws ContradictionException {
      return super.addClause(literals);
   }

   @Override
   public IConstr addClause(IVecInt literals, int desc) throws ContradictionException {
      return this.addControlableClause(literals, desc);
   }

   @Override
   public Collection<Integer> getAddedVars() {
      return this.varToHighLevel.keySet();
   }

   @Override
   public int[] model() {
      int[] fullmodel = super.modelWithInternalVariables();
      if (fullmodel == null) {
         return null;
      }

      int[] model = new int[fullmodel.length - this.varToHighLevel.size()];
      int j = 0;

      for (int element : fullmodel) {
         if (this.varToHighLevel.get(Math.abs(element)) == null) {
            model[j++] = element;
         }
      }

      return model;
   }

   public Map<Integer, Integer> getVarToHighLevel() {
      return this.varToHighLevel;
   }

   @Override
   public IVecInt unsatExplanation() {
      IVecInt internal = super.unsatExplanation();
      IVecInt external = new VecInt(internal.size());
      IteratorInt it = internal.iterator();

      while (it.hasNext()) {
         int p = it.next();
         Integer group;
         if (p > 0) {
            group = this.varToHighLevel.get(p);
         } else {
            Integer negGroup = this.varToHighLevel.get(-p);
            group = negGroup == null ? null : -negGroup;
         }

         if (group != null) {
            external.push(group);
         }
      }

      return external;
   }
}
