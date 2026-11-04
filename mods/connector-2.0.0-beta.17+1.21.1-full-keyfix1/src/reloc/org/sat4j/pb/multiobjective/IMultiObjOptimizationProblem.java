package reloc.org.sat4j.pb.multiobjective;

import java.math.BigInteger;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.specs.IOptimizationProblem;

public interface IMultiObjOptimizationProblem extends IOptimizationProblem {
   void addObjectiveFunction(ObjectiveFunction var1);

   BigInteger[] getObjectiveValues();
}
