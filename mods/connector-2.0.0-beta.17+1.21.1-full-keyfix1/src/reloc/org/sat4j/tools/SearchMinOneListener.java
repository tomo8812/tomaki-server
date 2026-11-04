package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.RandomAccessModel;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class SearchMinOneListener extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private ISolverService solverService;
   private final SolutionFoundListener sfl;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !SearchMinOneListener.class.desiredAssertionStatus();

   public SearchMinOneListener(SolutionFoundListener sfl) {
      this.sfl = sfl;
   }

   @Override
   public void init(ISolverService solverService) {
      this.solverService = solverService;
   }

   @Override
   public void solutionFound(int[] model, RandomAccessModel lazyModel) {
      int degree = 0;
      int[] variables = new int[model.length];

      for (int i = 0; i < model.length; i++) {
         if (model[i] > 0) {
            degree++;
            variables[i] = model[i];
         } else {
            variables[i] = -model[i];
         }
      }

      System.out.println(this.solverService.getLogPrefix() + " #one " + degree);
      this.solverService.addAtMostOnTheFly(variables, degree - 1);
      this.sfl.onSolutionFound(model);
   }

   @Override
   public void end(Lbool result) {
      assert result != Lbool.TRUE;
   }
}
