package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ActivityLCDS;
import reloc.org.sat4j.minisat.core.ConflictTimer;
import reloc.org.sat4j.minisat.core.DataStructureFactory;
import reloc.org.sat4j.minisat.core.Solver;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.pb.core.PBSolverStats;
import reloc.org.sat4j.specs.Constr;

public class PBActivityLCDS extends ActivityLCDS {
   private static final long serialVersionUID = 1L;

   public PBActivityLCDS(Solver<? extends DataStructureFactory> solver, ConflictTimer timer) {
      super(solver, timer);
   }

   @Override
   protected void onRemove(Constr c) {
      PBConstr constr = (PBConstr)c;
      PBSolverStats stats = (PBSolverStats)this.solver.getStats();
      stats.incNbRemoved();
      stats.setMinRemoved(constr.getDegree());
      stats.setMaxRemoved(constr.getDegree());
   }
}
