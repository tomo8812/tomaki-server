package dev.diorama.data;

import dev.diorama.Diorama;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class Copycats {
   private static final String CREATE = "com.simibubi.create.content.decoration.copycat.CopycatBlockEntity";
   private static final String PLUS = "com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity";
   private static final String PLUS_MULTI = "com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity";
   private static final String PLUS_MULTI_BLOCK = "com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock";
   private static final String EMPTY = "create:copycat_base";
   public static final boolean INSTALLED = installed();
   private static final ClassValue<Copycats.@Nullable Reader> READERS = new ClassValue<Copycats.Reader>() {
      protected Copycats.@Nullable Reader computeValue(Class<?> type) {
         return Copycats.reader(type);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      protected @Nullable Object computeValue(Class param1) {
         return this.computeValue(var1);
      }
   };
   private static final ClassValue<Method @Nullable []> PART_GETTERS = new ClassValue<Method[]>() {
      protected Method @Nullable [] computeValue(Class<?> type) {
         Class<?> block = Copycats.supertype(type, "com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock");

         try {
            return block == null
               ? null
               : new Method[]{
                  block.getMethod("partExists", BlockState.class, String.class), block.getMethod("getVectorFromProperty", BlockState.class, String.class)
               };
         } catch (ReflectiveOperationException e) {
            return null;
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      protected @Nullable Object computeValue(Class param1) {
         return this.computeValue(var1);
      }
   };
   private static volatile boolean failed;

   private Copycats() {
   }

   private static boolean installed() {
      return ModList.get().isLoaded("create");
   }

   public static @Nullable BlockState material(@Nullable BlockEntity entity) {
      if (INSTALLED && !failed && entity != null) {
         Copycats.Reader reader = READERS.get(entity.getClass());
         if (reader == null) {
            return null;
         }

         try {
            BlockState material = reader.material(entity);
            return material != null && !isEmpty(material) ? material : null;
         } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            failed = true;
            Diorama.LOGGER.warn("Couldn't read what a copycat ({}) is filled with; filled copycats draw as they do empty", entity.getClass().getName(), e);
            return null;
         }
      } else {
         return null;
      }
   }

   private static boolean isEmpty(BlockState material) {
      return material.isAir() || BuiltInRegistries.BLOCK.getKey(material.getBlock()).toString().equals("create:copycat_base");
   }

   private static Copycats.@Nullable Reader reader(Class<?> type) {
      try {
         Class<?> multi = supertype(type, "com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity");
         if (multi != null) {
            Method storage = multi.getMethod("getMaterialItemStorage");
            Method parts = storage.getReturnType().getMethod("getMaterialMap");
            return entity -> topPart(entity, (Map<?, ?>)parts.invoke(storage.invoke(entity)));
         }

         Class<?> single = supertype(type, "com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity");
         if (single == null) {
            single = supertype(type, "com.simibubi.create.content.decoration.copycat.CopycatBlockEntity");
         }

         if (single != null) {
            Method getter = single.getMethod("getMaterial");
            return entity -> getter.invoke(entity) instanceof BlockState state ? state : null;
         } else {
            return null;
         }
      } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
         failed = true;
         Diorama.LOGGER.warn("Couldn't find how {} keeps what it's filled with; filled copycats draw as they do empty", type.getName(), e);
         return null;
      }
   }

   private static @Nullable BlockState topPart(BlockEntity entity, Map<?, ?> parts) throws ReflectiveOperationException {
      BlockState state = entity.getBlockState();
      Method[] getters = PART_GETTERS.get(state.getBlock().getClass());
      if (getters == null) {
         return null;
      }

      Method exists = getters[0];
      Method vector = getters[1];
      int topY = Integer.MIN_VALUE;
      Map<BlockState, Integer> atTop = new LinkedHashMap<>();

      for (Entry<?, ?> part : new TreeMap<>(parts).entrySet()) {
         if (part.getKey() instanceof String name
            && part.getValue() instanceof BlockState material
            && !isEmpty(material)
            && Boolean.TRUE.equals(exists.invoke(state.getBlock(), state, name))) {
            int y = vector.invoke(state.getBlock(), state, name) instanceof Vec3i v ? v.getY() : 0;
            if (y > topY) {
               topY = y;
               atTop.clear();
            }

            if (y == topY) {
               atTop.merge(material, 1, Integer::sum);
            }
         }
      }

      BlockState best = null;

      for (Entry<BlockState, Integer> entry : atTop.entrySet()) {
         if (best == null || entry.getValue() > atTop.get(best)) {
            best = entry.getKey();
         }
      }

      return best;
   }

   private static @Nullable Class<?> supertype(@Nullable Class<?> type, String name) {
      if (type == null) {
         return null;
      }

      if (type.getName().equals(name)) {
         return type;
      }

      Class<?> found = supertype(type.getSuperclass(), name);

      for (Class<?> parent : type.getInterfaces()) {
         if (found == null) {
            found = supertype(parent, name);
         }
      }

      return found;
   }

   private interface Reader {
      @Nullable BlockState material(BlockEntity var1) throws ReflectiveOperationException;
   }
}
