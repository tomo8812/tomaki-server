package dev.diorama.bake;

import dev.diorama.data.Copycats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class TableLooks extends Looks {
   private final VanillaTable table;
   private final int[] lookOf;
   private final @Nullable JarModels jars;

   public TableLooks(VanillaTable table) {
      this(table, null);
   }

   public TableLooks(VanillaTable table, @Nullable JarModels jars) {
      super(Block.BLOCK_STATE_REGISTRY.size(), Copycats.INSTALLED ? Block.BLOCK_STATE_REGISTRY.size() + 16384 : Block.BLOCK_STATE_REGISTRY.size());
      this.table = table;
      this.jars = jars;
      this.lookOf = table.lookByStateId();
      this.ensure(VoxelReader.AIR_ID);
   }

   @Override
   protected void make(int id) {
      synchronized (this) {
         if (this.kind[id] == -1) {
            BlockState state = (BlockState)Block.BLOCK_STATE_REGISTRY.byId(id);
            byte kind = state == null ? 0 : this.work(id, state);
            KINDS.setRelease((byte[])this.kind, (int)id, (byte)kind);
         }
      }
   }

   @Override
   protected byte colours(int id, BlockState state, byte kind) {
      int index = id < this.lookOf.length ? this.lookOf[id] : -1;
      if (index < 0) {
         FaceColor[] faces = this.jars == null ? null : this.jars.faces(state);
         if (faces == null) {
            return this.flatColours(id, state, kind);
         }

         this.setColours(id, faces[0], faces[1]);
         FaceColor tinted = faces[0].tintIndex() >= 0 ? faces[0] : faces[1];
         if (tinted.tintIndex() >= 0 && greyish(tinted.tinted())) {
            this.tint[id] = JarModels.tintOf(state);
         }

         return kind == 1 && !state.is(BlockTags.LEAVES) && faces[0].coverage() < 0.55F ? 4 : kind;
      } else {
         VanillaTable.Look look = this.table.looks.get(index);
         this.top[id] = look.top();
         this.topTinted[id] = look.topTinted();
         this.topTintWeight[id] = look.topTintWeight();
         this.side[id] = look.side();
         this.sideTinted[id] = look.sideTinted();
         this.sideTintWeight[id] = look.sideTintWeight();
         this.tint[id] = look.tint();
         this.constantTint[id] = look.constantTint();
         return look.kind() != 0 && (kind != 1 || look.kind() != 4) ? kind : look.kind();
      }
   }

   private static boolean greyish(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      int max = Math.max(r, Math.max(g, b));
      int min = Math.min(r, Math.min(g, b));
      return max == 0 || max - min <= max / 5;
   }
}
