package org.embeddedt.modernfix.util.blockpos;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;

public class SectionBlockPosIterator implements Iterator<BlockPos> {
   private final MutableBlockPos pos = new MutableBlockPos();
   private int index = 0;
   private final int baseX;
   private final int baseY;
   private final int baseZ;

   public SectionBlockPosIterator(int baseX, int baseY, int baseZ) {
      this.baseX = baseX;
      this.baseY = baseY;
      this.baseZ = baseZ;
   }

   public SectionBlockPosIterator(BlockPos pos) {
      this(pos.getX(), pos.getY(), pos.getZ());
   }

   @Override
   public boolean hasNext() {
      return this.index < 4096;
   }

   public BlockPos next() {
      int i = this.index;
      if (i >= 4096) {
         throw new NoSuchElementException();
      }

      this.index = i + 1;
      MutableBlockPos pos = this.pos;
      pos.set(this.baseX + (i & 15), this.baseY + (i >> 8 & 15), this.baseZ + (i >> 4 & 15));
      return pos;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }
}
