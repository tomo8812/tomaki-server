package reloc.org.sat4j.pb.constraints.pb;

import reloc.org.sat4j.pb.core.PBSolverStats;

public interface IConflictFactory {
   IConflict createConflict(
      PBConstr var1,
      int var2,
      boolean var3,
      SkipStrategy var4,
      IPreProcess var5,
      IPostProcess var6,
      IWeakeningStrategy var7,
      AutoDivisionStrategy var8,
      PBSolverStats var9
   );
}
