package reloc.org.sat4j.tools.counting;

import java.math.BigInteger;

public interface IModelCounter {
   BigInteger countModels();

   void reset();
}
