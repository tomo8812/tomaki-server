package reloc.org.sat4j.minisat.orders;

public interface VariableComparator {
   boolean preferredTo(int var1, int var2);
}
