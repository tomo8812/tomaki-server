package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class SearchEnumeratorListener extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private ISolverService solverService;
   private int nbsolutions = 0;
   private final SolutionFoundListener sfl;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !SearchEnumeratorListener.class.desiredAssertionStatus();

   public SearchEnumeratorListener(SolutionFoundListener sfl) {
      this.sfl = sfl;
   }

   @Override
   public void init(ISolverService solverService) {
      this.solverService = solverService;
   }

   @Override
   public void solutionFound(int[] model, RandomAccessModel lazyModel) {
      IVecInt clauseToAdd = this.solverService.createBlockingClauseForCurrentModel();
      int[] vecint = new int[clauseToAdd.size()];
      clauseToAdd.copyTo(vecint);
      this.solverService.addClauseOnTheFly(vecint);
      this.nbsolutions++;
      this.sfl.onSolutionFound(model);
   }

   @Override
   public void end(Lbool result) {
      assert result != Lbool.TRUE;
      if (result == Lbool.FALSE) {
         this.sfl.onUnsatTermination();
      }
   }

   public int getNumberOfSolutionFound() {
      return this.nbsolutions;
   }
}
