package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public interface ILBDComputerStrategy {
   void init(int var1);

   int computeLBD(ILits var1, PBConstr var2, int var3);
}
