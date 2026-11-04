package reloc.org.sat4j.tools.counting;

import reloc.org.sat4j.specs.IteratorInt;

public interface SamplingSet {
   int nVars();

   IteratorInt variables();
}
