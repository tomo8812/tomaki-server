package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import kotlin.comparisons.ComparisonsKt;
import org.valkyrienskies.core.internal.joints.VSJointAndId;

public final class Ek$b<T> implements Comparator {
   @Override
   public final int compare(T a, T b) {
      VSJointAndId var3;
      Comparable var10000 = (var3 = (VSJointAndId)a).getJointId();
      a = (VSJointAndId)b;
      Comparable var5 = var10000;
      return ComparisonsKt.compareValues(var5, ((VSJointAndId)a).getJointId());
   }
}
