package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.pb.IPBSolverService;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListenerAdapter;
import reloc.org.sat4j.tools.SolutionFoundListener;

@Feature("searchlistener")
public final class SearchOptimizerListener extends SearchListenerAdapter<IPBSolverService> {
   private static final long serialVersionUID = 1L;
   private IPBSolverService solverService;
   private ObjectiveFunction obj;
   private final SolutionFoundListener sfl;
   private BigInteger currentValue;
   private IConstr prevConstr = null;

   public SearchOptimizerListener(SolutionFoundListener sfl) {
      this.sfl = sfl;
   }

   public void init(IPBSolverService solverService) {
      this.obj = solverService.getObjectiveFunction();
      this.solverService = solverService;
      this.currentValue = null;
      this.prevConstr = null;
   }

   @Override
   public void solutionFound(int[] model, RandomAccessModel lazyModel) {
      if (this.obj != null) {
         this.currentValue = this.obj.calculateDegree(lazyModel);
         BigInteger offset = this.obj.getCorrectionOffset();
         BigInteger factor = this.obj.getCorrectionFactor();
         System.out.println("o " + this.currentValue.multiply(factor).add(offset));
         if (this.prevConstr != null) {
            this.solverService.removeSubsumedConstr(this.prevConstr);
         }

         this.prevConstr = this.solverService.addAtMostOnTheFly(this.obj.getVars(), this.obj.getCoeffs(), this.currentValue.subtract(BigInteger.ONE));
      }

      this.sfl.onSolutionFound(model);
   }

   @Override
   public void end(Lbool result) {
      if (result == Lbool.FALSE) {
         this.sfl.onUnsatTermination();
         System.out.println(this.solverService.getLogPrefix() + "objective function=" + this.currentValue);
      }
   }

   @Override
   public String toString() {
      return "Internal optimizer search listener";
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void init(ISolverService var1) {
      this.init((IPBSolverService)var1);
   }
}
