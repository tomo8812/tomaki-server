package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

public interface IRemoveSatisfiedLiterals {
   BigInteger removeSatisfiedLiteralsFromHigherDecisionLevels(IWatchPb var1, BigInteger[] var2, int var3, BigInteger var4);
}
