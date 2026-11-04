package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public abstract class AbstractLBDComputerStrategy implements ILBDComputerStrategy {
   protected int[] flags = new int[0];
   protected int flag = 0;

   @Override
   public void init(int howmany) {
      if (this.flags.length <= howmany) {
         this.flags = new int[howmany + 1];
      }

      this.flag = 0;
   }

   @Override
   public int computeLBD(ILits voc, PBConstr constr, int propagated) {
      int nblevel = 1;
      this.flag++;
      this.startComputeLBD(constr, propagated);

      for (int i = 0; i < constr.size(); i++) {
         int currentLevel = voc.getLevel(constr.get(i));
         if (currentLevel < 0) {
            nblevel += this.unassignedLiteral(voc, constr, i);
         } else if (this.flags[currentLevel] != this.flag) {
            this.flags[currentLevel] = this.flag;
            nblevel += this.assignedLiteral(voc, constr, i);
         }
      }

      return nblevel;
   }

   protected void startComputeLBD(PBConstr constr, int propagated) {
   }

   protected abstract int unassignedLiteral(ILits var1, PBConstr var2, int var3);

   protected abstract int assignedLiteral(ILits var1, PBConstr var2, int var3);

   protected int fixLbd(PBConstr constr, int lbd) {
      return lbd;
   }
}
