package dev.diorama.render;

import com.google.common.collect.MapMaker;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public final class MobFaces {
   private static final Map<EntityType<?>, MobFaces.Faces> FACES = new HashMap<>();
   private static final Map<Entity, MobFaces.Looked> TEXTURES = new MapMaker().weakKeys().makeMap();
   private static final int REFRESH_TICKS = 40;
   private static final MobFaces.Piece HUMANOID_HAT = new MobFaces.Piece(40, 8, 8, 8, 0, 0);
   private static final MobFaces.Piece BABY_ZOMBIE_HAT = new MobFaces.Piece(41, 9, 6, 6, 0, 0);
   private static final MobFaces.Piece VILLAGER_HAT = new MobFaces.Piece(40, 8, 8, 10, 0, 0);
   private static final MobFaces.Piece VILLAGER_NOSE = new MobFaces.Piece(26, 2, 2, 3, 3, 7);
   private static final MobFaces.Face COW = new MobFaces.Face(64, 32, 6, 6, 8, 8);
   private static final MobFaces.Face COLD_COW = new MobFaces.Face(64, 64, 6, 6, 8, 8, null, new MobFaces.Piece(10, 34, 6, 3, 1, 5));
   public static volatile boolean modelOnly;
   public static volatile boolean fromModels;
   private static final Map<Class<?>, List<Field>> MODEL_FIELDS;

   private MobFaces() {
   }

   public static boolean has(Entity entity) {
      return face(entity) != null;
   }

   public static boolean canDraw(Entity entity) {
      return face(entity) != null && looked(entity).texture() != null;
   }

   public static @Nullable String describe(Entity entity) {
      return describe(face(entity));
   }

   public static @Nullable String describeFromModel(Entity entity) {
      return describe(looked(entity).modelFace());
   }

   private static @Nullable String describe(MobFaces.@Nullable Face face) {
      if (face == null) {
         return null;
      }

      StringBuilder out = new StringBuilder().append(face.texW()).append('x').append(face.texH()).append(' ').append(face.w()).append('x').append(face.h());

      for (MobFaces.Piece p : face.pieces()) {
         out.append(" | ")
            .append(p.u())
            .append(',')
            .append(p.v())
            .append(' ')
            .append(p.w())
            .append('x')
            .append(p.h())
            .append(" at ")
            .append(p.dx())
            .append(',')
            .append(p.dy());
      }

      return out.toString();
   }

   private static MobFaces.@Nullable Face face(Entity entity) {
      MobFaces.Faces faces = FACES.get(entity.getType());
      if (faces != null && !modelOnly) {
         MobFaces.Face face = faces.adult();
         return face == COW && coldCow(entity) ? COLD_COW : face;
      } else {
         return fromModels ? looked(entity).modelFace() : null;
      }
   }

   public static boolean draw(IconBatch icons, Entity entity, float x, float y, float size, int color) {
      MobFaces.Face face = face(entity);
      ResourceLocation texture = face == null ? null : looked(entity).texture();
      if (face != null && texture != null) {
         float scale = size / Math.max(face.w(), face.h());
         float w = face.w() * scale;
         float h = face.h() * scale;
         GuiGraphics graphics = icons.graphics();
         graphics.pose().pushPose();
         graphics.pose().translate(x - w / 2.0F, y - h / 2.0F, 0.0F);
         graphics.pose().scale(scale, scale, 1.0F);
         blitFace(icons, texture, face, color);
         graphics.pose().popPose();
         return true;
      } else {
         return false;
      }
   }

   public static boolean drawPixel(IconBatch icons, Entity entity, float x, float y, float size, int color, int outline, int alpha) {
      MobFaces.Face face = face(entity);
      ResourceLocation texture = face == null ? null : looked(entity).texture();
      if (face != null && texture != null) {
         float texel = Radar.texel(size, Math.max(face.w(), face.h()));
         Radar.pixelFrame(icons, x, y, face.w(), face.h(), texel, outline, alpha);
         blitFace(icons, texture, face, color);
         GuiGraphics graphics = icons.graphics();
         graphics.pose().popPose();
         return true;
      } else {
         return false;
      }
   }

   private static void blitFace(IconBatch icons, ResourceLocation texture, MobFaces.Face face, int color) {
      for (MobFaces.Piece p : face.pieces()) {
         icons.blit(texture, p.dx(), p.dy(), p.u(), p.v(), p.w(), p.h(), face.texW(), face.texH(), color);
      }
   }

   private static boolean coldCow(Entity entity) {
      return false;
   }

   static @Nullable ResourceLocation texture(Entity entity) {
      return looked(entity).texture();
   }

   private static MobFaces.Looked looked(Entity entity) {
      MobFaces.Looked looked = TEXTURES.get(entity);
      int age = entity.tickCount;
      boolean baby = entity instanceof LivingEntity living && living.isBaby();
      if (looked == null || age - looked.age() >= 40 || age < looked.age() || baby != looked.baby()) {
         looked = lookUp(entity, age, baby);
         TEXTURES.put(entity, looked);
      }

      return looked;
   }

   private static MobFaces.@Nullable Face modelFace(LivingEntityRenderer renderer, boolean baby, @Nullable Object state) {
      Model current = renderer.getModel();
      List<Model> models = new ArrayList<>(List.of(current));
      List<Boolean> forBabies = new ArrayList<>(List.of(babyName(current.getClass().getSimpleName())));

      for (Field field : MODEL_FIELDS.computeIfAbsent(renderer.getClass(), MobFaces::modelFields)) {
         Object other;
         try {
            other = field.get(renderer);
         } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
         }

         if (other instanceof Model model) {
            boolean babyField = babyName(field.getName()) || babyName(model.getClass().getSimpleName());
            int at = models.indexOf(model);
            if (at < 0) {
               models.add(model);
               forBabies.add(babyField);
            } else {
               forBabies.set(at, forBabies.get(at) || babyField);
            }
         }
      }

      boolean sorted = forBabies.contains(baby);
      MobFaces.Face face = null;

      for (int i = 0; i < models.size(); i++) {
         if (!sorted || forBabies.get(i) == baby) {
            MobFaces.Face read = ModelFaces.of(models.get(i), state);
            if (read != null) {
               if (face != null && !face.equals(read)) {
                  return null;
               }

               face = read;
            }
         }
      }

      return face;
   }

   private static boolean babyName(String name) {
      String lower = name.toLowerCase(Locale.ROOT);
      return lower.contains("baby") || lower.contains("small") || lower.contains("child");
   }

   private static List<Field> modelFields(Class<?> type) {
      List<Field> fields = new ArrayList<>();

      for (Class<?> c = type; c != null && c != LivingEntityRenderer.class; c = c.getSuperclass()) {
         for (Field field : c.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && Model.class.isAssignableFrom(field.getType())) {
               try {
                  field.setAccessible(true);
                  fields.add(field);
               } catch (RuntimeException var8) {
               }
            }
         }
      }

      return List.copyOf(fields);
   }

   private static MobFaces.Looked lookUp(Entity entity, int age, boolean baby) {
      EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

      try {
         ResourceLocation texture = renderer.getTextureLocation(entity);
         MobFaces.Face face = renderer instanceof LivingEntityRenderer living ? modelFace(living, baby, null) : null;
         return new MobFaces.Looked(texture, face, age, baby);
      } catch (RuntimeException e) {
         return new MobFaces.Looked(null, null, age, baby);
      }
   }

   static {
      MobFaces.Face zombie = new MobFaces.Face(64, 64, 8, 8, 8, 8, HUMANOID_HAT, null);
      MobFaces.Face babyZombie = new MobFaces.Face(64, 64, 9, 9, 6, 6, BABY_ZOMBIE_HAT, null);

      for (EntityType<?> type : new EntityType[]{EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED}) {
         FACES.put(type, new MobFaces.Faces(zombie, babyZombie));
      }

      FACES.put(
         EntityType.ZOMBIE_VILLAGER,
         new MobFaces.Faces(
            new MobFaces.Face(64, 64, 8, 8, 8, 10, VILLAGER_HAT, VILLAGER_NOSE),
            new MobFaces.Face(64, 64, 7, 7, 8, 8, new MobFaces.Piece(7, 38, 8, 8, 0, 0), null)
         )
      );
      MobFaces.Face skeleton = new MobFaces.Face(64, 32, 8, 8, 8, 8, HUMANOID_HAT, null);

      for (EntityType<?> type : new EntityType[]{EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.BOGGED}) {
         FACES.put(type, new MobFaces.Faces(skeleton, null));
      }

      FACES.put(EntityType.CREEPER, new MobFaces.Faces(new MobFaces.Face(64, 32, 8, 8, 8, 8), null));
      MobFaces.Face spider = new MobFaces.Face(64, 32, 40, 12, 8, 8);
      FACES.put(EntityType.SPIDER, new MobFaces.Faces(spider, null));
      FACES.put(EntityType.CAVE_SPIDER, new MobFaces.Faces(spider, null));
      FACES.put(EntityType.ENDERMAN, new MobFaces.Faces(new MobFaces.Face(64, 32, 8, 8, 8, 8), null));
      FACES.put(EntityType.WITCH, new MobFaces.Faces(new MobFaces.Face(64, 128, 8, 8, 8, 10, null, VILLAGER_NOSE), null));
      FACES.put(EntityType.BLAZE, new MobFaces.Faces(new MobFaces.Face(64, 32, 8, 8, 8, 8), null));
      MobFaces.Face piglin = new MobFaces.Face(64, 64, 8, 8, 10, 8, null, new MobFaces.Piece(32, 2, 4, 4, 3, 4));
      MobFaces.Face babyPiglin = new MobFaces.Face(64, 64, 7, 7, 9, 6, null, new MobFaces.Piece(22, 31, 3, 3, 3, 3));
      FACES.put(EntityType.PIGLIN, new MobFaces.Faces(piglin, babyPiglin));
      FACES.put(EntityType.PIGLIN_BRUTE, new MobFaces.Faces(piglin, null));
      FACES.put(EntityType.ZOMBIFIED_PIGLIN, new MobFaces.Faces(piglin, babyPiglin));
      MobFaces.Face illager = new MobFaces.Face(64, 64, 8, 8, 8, 10, null, VILLAGER_NOSE);

      for (EntityType<?> type : new EntityType[]{EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER}) {
         FACES.put(type, new MobFaces.Faces(illager, null));
      }

      FACES.put(EntityType.RAVAGER, new MobFaces.Faces(new MobFaces.Face(128, 128, 16, 16, 16, 20), null));
      FACES.put(
         EntityType.VILLAGER,
         new MobFaces.Faces(
            new MobFaces.Face(64, 64, 8, 8, 8, 10, VILLAGER_HAT, VILLAGER_NOSE),
            new MobFaces.Face(64, 64, 7, 7, 8, 8, new MobFaces.Piece(7, 37, 8, 8, 0, 0), null)
         )
      );
      FACES.put(EntityType.WANDERING_TRADER, new MobFaces.Faces(new MobFaces.Face(64, 64, 8, 8, 8, 10, VILLAGER_HAT, VILLAGER_NOSE), null));
      FACES.put(EntityType.IRON_GOLEM, new MobFaces.Faces(new MobFaces.Face(128, 128, 8, 8, 8, 10, null, VILLAGER_NOSE), null));
      MobFaces.Faces cow = new MobFaces.Faces(COW, new MobFaces.Face(64, 64, 5, 23, 6, 6, null, new MobFaces.Piece(13, 30, 4, 3, 1, 3)));
      FACES.put(EntityType.COW, cow);
      FACES.put(EntityType.MOOSHROOM, cow);
      FACES.put(EntityType.PIG, new MobFaces.Faces(new MobFaces.Face(64, 32, 8, 8, 8, 8, null, new MobFaces.Piece(17, 17, 4, 3, 2, 4)), null));
      FACES.put(EntityType.SHEEP, new MobFaces.Faces(new MobFaces.Face(64, 32, 8, 8, 6, 6), new MobFaces.Face(64, 32, 5, 5, 5, 5)));
      FACES.put(
         EntityType.CHICKEN,
         new MobFaces.Faces(new MobFaces.Face(64, 32, 3, 3, 4, 6, null, new MobFaces.Piece(16, 2, 4, 2, 0, 2)), new MobFaces.Face(16, 16, 4, 4, 4, 4))
      );
      FACES.put(EntityType.WOLF, new MobFaces.Faces(new MobFaces.Face(64, 32, 4, 4, 6, 6), new MobFaces.Face(32, 32, 5, 17, 6, 5)));
      FACES.put(EntityType.CAT, new MobFaces.Faces(new MobFaces.Face(64, 32, 5, 5, 5, 4), new MobFaces.Face(32, 32, 4, 4, 5, 4)));
      FACES.put(
         EntityType.FOX,
         new MobFaces.Faces(new MobFaces.Face(48, 32, 7, 11, 8, 6, null, new MobFaces.Piece(9, 21, 4, 2, 2, 4)), new MobFaces.Face(32, 32, 5, 5, 6, 5))
      );
      FACES.put(EntityType.RABBIT, new MobFaces.Faces(new MobFaces.Face(64, 32, 37, 5, 5, 4), null));
      FACES.put(EntityType.POLAR_BEAR, new MobFaces.Faces(new MobFaces.Face(128, 64, 7, 7, 7, 7), new MobFaces.Face(64, 64, 4, 4, 6, 5)));
      FACES.put(
         EntityType.PANDA,
         new MobFaces.Faces(new MobFaces.Face(64, 64, 9, 15, 13, 10, null, new MobFaces.Piece(47, 18, 7, 5, 3, 5)), new MobFaces.Face(64, 64, 5, 5, 7, 6))
      );
      FACES.put(EntityType.ALLAY, new MobFaces.Faces(new MobFaces.Face(32, 32, 5, 5, 5, 5), null));
      FACES.put(EntityType.BREEZE, new MobFaces.Faces(new MobFaces.Face(32, 32, 8, 8, 8, 8), null));
      FACES.put(EntityType.DOLPHIN, new MobFaces.Faces(new MobFaces.Face(64, 64, 6, 6, 8, 7), new MobFaces.Face(64, 64, 4, 4, 6, 5)));
      FACES.put(EntityType.BEE, new MobFaces.Faces(new MobFaces.Face(64, 64, 10, 10, 7, 7), new MobFaces.Face(32, 32, 5, 5, 4, 4)));
      FACES.put(EntityType.AXOLOTL, new MobFaces.Faces(new MobFaces.Face(64, 64, 5, 6, 8, 5), new MobFaces.Face(32, 32, 4, 12, 6, 3)));
      FACES.put(EntityType.STRIDER, new MobFaces.Faces(new MobFaces.Face(64, 128, 16, 16, 16, 14), new MobFaces.Face(32, 32, 8, 8, 7, 7)));
      fromModels = true;
      MODEL_FIELDS = new ConcurrentHashMap<>();
   }

   record Face(int texW, int texH, int w, int h, List<MobFaces.Piece> pieces) {
      Face(int texW, int texH, int u, int v, int w, int h) {
         this(texW, texH, u, v, w, h, null, null);
      }

      Face(int texW, int texH, int u, int v, int w, int h, MobFaces.@Nullable Piece hat, MobFaces.@Nullable Piece feature) {
         this(texW, texH, w, h, layers(new MobFaces.Piece(u, v, w, h, 0, 0), feature, hat));
      }

      private static List<MobFaces.Piece> layers(MobFaces.Piece face, MobFaces.@Nullable Piece feature, MobFaces.@Nullable Piece hat) {
         List<MobFaces.Piece> pieces = new ArrayList<>(List.of(face));
         if (feature != null) {
            pieces.add(feature);
         }

         if (hat != null) {
            pieces.add(hat);
         }

         return List.copyOf(pieces);
      }
   }

   private record Faces(MobFaces.Face adult, MobFaces.@Nullable Face baby) {
   }

   private record Looked(@Nullable ResourceLocation texture, MobFaces.@Nullable Face modelFace, int age, boolean baby) {
   }

   record Piece(int u, int v, int w, int h, int dx, int dy) {
   }
}
