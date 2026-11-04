package org.valkyrienskies.core.impl.shadow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.valkyrienskies.core.util.datastructures.IBlockPosSet;

public final class Dw implements IBlockPosSet {
   private final Set<Vector3ic> a = new HashSet<>();

   @Override
   public final boolean add(int x, int y, int z) {
      return this.a.add(new Vector3i(x, y, z));
   }

   @Override
   public final boolean remove(int x, int y, int z) {
      return this.a.remove(new Vector3i(x, y, z));
   }

   @Override
   public final boolean contains(int x, int y, int z) {
      return this.a.contains(new Vector3i(x, y, z));
   }

   @Override
   public final boolean canStore(int x, int y, int z) {
      return true;
   }

   @Override
   public final int size() {
      return this.a.size();
   }

   @Override
   public final Iterator<Vector3ic> iterator() {
      return this.a.iterator();
   }

   @Override
   public final void clear() {
      this.a.clear();
   }
}
