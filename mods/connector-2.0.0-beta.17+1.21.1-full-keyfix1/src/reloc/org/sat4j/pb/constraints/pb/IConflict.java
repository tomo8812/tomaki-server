package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.VarActivityListener;

public interface IConflict extends IDataStructurePB {
   BigInteger resolve(PBConstr var1, int var2, VarActivityListener var3);

   BigInteger slackConflict();

   void postProcess(int var1);

   boolean isAssertive(int var1);

   void setDecisionLevel(int var1);

   boolean isUnsat();

   BigInteger reduceInConstraint(IWatchPb var1, BigInteger[] var2, int var3, BigInteger var4, BigInteger var5);

   int getBacktrackLevel(int var1);

   void updateSlack(int var1);

   boolean slackIsCorrect(int var1);

   void undoOne(int var1);
}
