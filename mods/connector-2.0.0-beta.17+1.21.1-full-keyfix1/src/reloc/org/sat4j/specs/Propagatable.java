package reloc.org.sat4j.specs;

public interface Propagatable {
   boolean propagate(UnitPropagationListener var1, int var2);

   boolean propagatePI(MandatoryLiteralListener var1, int var2);

   Constr toConstraint();
}
