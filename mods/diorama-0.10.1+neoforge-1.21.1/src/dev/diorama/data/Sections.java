package dev.diorama.data;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jspecify.annotations.Nullable;

final class Sections {
   private static final @Nullable MethodHandle STORAGE;
   private static final @Nullable MethodHandle PACKED_DATA;
   private static final @Nullable MethodHandle PACKED_STORAGE;
   private static final @Nullable MethodHandle PACKED_PALETTE;

   private Sections() {
   }

   static ChunkSnapshot.@Nullable Packed packed(PalettedContainer<BlockState> states) {
      if (PACKED_DATA == null) {
         return null;
      }

      try {
         Object data = (Object)PACKED_DATA.invokeExact((PalettedContainer)states);
         BitStorage storage = (BitStorage)PACKED_STORAGE.invokeExact((Object)data);
         Palette<BlockState> palette = (Palette)PACKED_PALETTE.invokeExact((Object)data);
         return storage != null && palette != null && storage.getSize() == 4096 ? new ChunkSnapshot.Packed(storage, palette) : null;
      } catch (Throwable e) {
         return null;
      }
   }

   static @Nullable BlockState singleValue(PalettedContainer<BlockState> states) {
      if (STORAGE != null) {
         try {
            return (BitStorage)STORAGE.invokeExact((PalettedContainer)states).getBits() == 0 ? (BlockState)states.get(0, 0, 0) : null;
         } catch (Throwable var2) {
         }
      }

      return states.getSerializedSize() < 512 ? (BlockState)states.get(0, 0, 0) : null;
   }

   static {
      MethodHandle storage = null;

      try {
         for (Field field : PalettedContainer.class.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (type.isRecord() && type.getEnclosingClass() == PalettedContainer.class) {
               for (RecordComponent component : type.getRecordComponents()) {
                  if (component.getType() == BitStorage.class) {
                     field.setAccessible(true);
                     Method accessor = component.getAccessor();
                     accessor.setAccessible(true);
                     Lookup lookup = MethodHandles.lookup();
                     storage = MethodHandles.filterReturnValue(lookup.unreflectGetter(field), lookup.unreflect(accessor))
                        .asType(MethodType.methodType(BitStorage.class, PalettedContainer.class));
                  }
               }
            }
         }
      } catch (ReflectiveOperationException | RuntimeException e) {
         storage = null;
      }

      STORAGE = storage;
      storage = null;
      MethodHandle storagex = null;
      MethodHandle palette = null;

      try {
         Lookup lookup = MethodHandles.lookup();

         for (Field field : PalettedContainer.class.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (type.isRecord() && type.getEnclosingClass() == PalettedContainer.class && !Modifier.isStatic(field.getModifiers())) {
               MethodHandle s = null;
               MethodHandle p = null;

               for (RecordComponent component : type.getRecordComponents()) {
                  Method accessor = component.getAccessor();
                  if (component.getType() == BitStorage.class) {
                     accessor.setAccessible(true);
                     s = lookup.unreflect(accessor).asType(MethodType.methodType(BitStorage.class, Object.class));
                  } else if (component.getType() == Palette.class) {
                     accessor.setAccessible(true);
                     p = lookup.unreflect(accessor).asType(MethodType.methodType(Palette.class, Object.class));
                  }
               }

               if (s != null && p != null) {
                  field.setAccessible(true);
                  storage = lookup.unreflectGetter(field).asType(MethodType.methodType(Object.class, PalettedContainer.class));
                  storagex = s;
                  palette = p;
               }
            }
         }
      } catch (ReflectiveOperationException | RuntimeException e) {
         storage = null;
      }

      PACKED_DATA = storage;
      PACKED_STORAGE = storage == null ? null : storagex;
      PACKED_PALETTE = storage == null ? null : palette;
   }
}
