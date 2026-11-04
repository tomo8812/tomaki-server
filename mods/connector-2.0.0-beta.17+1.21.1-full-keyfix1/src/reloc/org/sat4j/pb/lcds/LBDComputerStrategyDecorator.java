package reloc.org.sat4j.pb.lcds;

import reloc.org.sat4j.minisat.core.ILits;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public class LBDComputerStrategyDecorator implements ILBDComputerStrategy {
   private final ILBDComputerStrategy decorated;

   public LBDComputerStrategyDecorator(ILBDComputerStrategy decorated) {
      this.decorated = decorated;
   }

   @Override
   public void init(int howmany) {
      this.decorated.init(howmany);
   }

   @Override
   public int computeLBD(ILits voc, PBConstr constr, int propagated) {
      return this.decorated.computeLBD(voc, constr, propagated);
   }
}
