package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

public final class aZ {
   public static final Comparator<aB<?, ?>> a = (var0, var1) -> Boolean.compare(var0.s().endsWith(")"), var1.s().endsWith(")"));
   public static final Comparator<aB<?, ?>> b = (var0, var1) -> var0.s().compareTo(var1.s());
   public static final Comparator<aB<?, ?>> c = a.thenComparing(b);
}
