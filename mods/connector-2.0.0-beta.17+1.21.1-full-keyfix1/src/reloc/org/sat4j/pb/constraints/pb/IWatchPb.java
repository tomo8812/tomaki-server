package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

public interface IWatchPb extends PBConstr {
   BigInteger slackConstraint(BigInteger[] var1, BigInteger var2);
}
