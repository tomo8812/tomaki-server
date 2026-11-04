package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.LodWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.OptionalDouble;

public final class VoxyConfigReader {
   public static final int SECTION_BLOCKS = 512;
   public static final String RENDER_DISTANCE_FIELD = "sectionRenderDistance";
   private static final String CAUSE_FIELD = "voxy-render-distance-field";

   private VoxyConfigReader() {
   }

   public static int radiusBlocks(Object config) {
      if (config == null) {
         return 0;
      }

      if (flag(config, "enabled", true) && flag(config, "enableRendering", true)) {
         OptionalDouble sections = number(config, "sectionRenderDistance");
         if (sections.isEmpty()) {
            LodWarnings.once(
               "voxy-render-distance-field",
               "this voxy ("
                  + config.getClass().getName()
                  + ") has no readable 'sectionRenderDistance' setting; it is either missing or not a number. That is a voxy fork we do not recognise. Falling back to a LOD radius of 256 blocks, which is far less terrain than voxy's own default (8192). Please report this with your voxy version."
            );
            return 0;
         }

         double value = sections.getAsDouble();
         if (value <= 0.0) {
            return 0;
         }

         double blocks = value * 512.0;
         return blocks >= 2.147483647E9 ? Integer.MAX_VALUE : (int)Math.round(blocks);
      } else {
         return 0;
      }
   }

   public static OptionalDouble number(Object instance, String name) {
      Field field = field(instance, name);
      if (field == null) {
         return OptionalDouble.empty();
      }

      try {
         Object value = field.get(instance);
         return value instanceof Number ? OptionalDouble.of(((Number)value).doubleValue()) : OptionalDouble.empty();
      } catch (IllegalAccessException | RuntimeException e) {
         return OptionalDouble.empty();
      }
   }

   public static boolean flag(Object instance, String name, boolean fallback) {
      Field field = field(instance, name);
      if (field == null) {
         return fallback;
      }

      try {
         Object value = field.get(instance);
         return value instanceof Boolean ? (Boolean)value : fallback;
      } catch (IllegalAccessException | RuntimeException e) {
         return fallback;
      }
   }

   public static Object staticField(Class<?> owner, String name) {
      try {
         Field field = owner.getField(name);
         return !Modifier.isStatic(field.getModifiers()) ? null : field.get(null);
      } catch (NoSuchFieldException | IllegalAccessException | RuntimeException e) {
         return null;
      }
   }

   private static Field field(Object instance, String name) {
      if (instance == null) {
         return null;
      }

      try {
         return instance.getClass().getField(name);
      } catch (NoSuchFieldException | RuntimeException var6) {
         Class<?> type = instance.getClass();

         while (type != null) {
            try {
               Field declared = type.getDeclaredField(name);
               declared.setAccessible(true);
               return declared;
            } catch (NoSuchFieldException ignored) {
               type = type.getSuperclass();
            } catch (RuntimeException e) {
               return null;
            }
         }

         return null;
      }
   }
}
