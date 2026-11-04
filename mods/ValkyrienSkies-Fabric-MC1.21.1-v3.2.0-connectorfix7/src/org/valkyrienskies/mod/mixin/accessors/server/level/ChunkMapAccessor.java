package org.valkyrienskies.mod.mixin.accessors.server.level;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_3193;
import net.minecraft.class_3222;
import net.minecraft.class_3898;
import net.minecraft.class_4153;
import net.minecraft.class_3898.class_3216;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_3898.class)
public interface ChunkMapAccessor {
   @Invoker("markChunkPendingToSend")
   void callMarkChunkPendingToSend(class_3222 var1, class_1923 var2);

   @Invoker("dropChunk")
   void callDropChunk(class_3222 var1, class_1923 var2);

   @Invoker("getChunks")
   Iterable<class_3193> callGetChunks();

   @Invoker("getVisibleChunkIfPresent")
   class_3193 callGetVisibleChunkIfPresent(long var1);

   @Invoker("getUpdatingChunkIfPresent")
   class_3193 callGetUpdatingChunkIfPresent(long var1);

   @Invoker("save")
   boolean callSave(class_2791 var1);

   @Accessor("toDrop")
   LongSet getToDrop();

   @Invoker("processUnloads")
   void callProcessUnloads(BooleanSupplier var1);

   @Accessor("poiManager")
   class_4153 getPoiManager();

   @Accessor("distanceManager")
   class_3216 getDistanceManager();

   @Invoker("getChunkQueueLevel")
   IntSupplier callGetChunkQueueLevel(long var1);
}
