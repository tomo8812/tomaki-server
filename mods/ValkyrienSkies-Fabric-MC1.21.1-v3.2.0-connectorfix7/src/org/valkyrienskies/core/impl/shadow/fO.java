package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import java.util.SortedMap;

public interface fO<K, V> extends SortedMap<K, V>, fB<K, V> {
   fO<V, K> f();

   Comparator<? super V> g();
}
