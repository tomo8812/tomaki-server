package reloc.org.sat4j.pb.core;

import reloc.org.sat4j.minisat.core.ICDCL;
import reloc.org.sat4j.pb.IPBSolver;

public interface IPBCDCLSolver<D extends PBDataStructureFactory> extends IPBSolver, ICDCL<D> {
}
