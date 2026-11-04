package reloc.org.sat4j.pb.orders;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public interface IBumper {
   void varBumpActivity(ILits var1, BumpStrategy var2, IOrder var3, PBConstr var4, int var5, int var6);

   void postBumpActivity(IOrder var1, PBConstr var2);
}
