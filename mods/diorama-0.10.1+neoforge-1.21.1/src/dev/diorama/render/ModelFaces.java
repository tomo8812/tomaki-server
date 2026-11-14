package dev.diorama.render;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import dev.diorama.Diorama;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart.Cube;
import org.jspecify.annotations.Nullable;

public final class ModelFaces {
   private static final int MAX_SIZE = 32;
   private static final float FACING = 0.999F;
   private static final Map<Object, Optional<ModelPart>> HEADS = new MapMaker().weakKeys().makeMap();
   private static final Map<Object, Map<BitSet, Optional<MobFaces.Face>>> FACES = new MapMaker().weakKeys().makeMap();
   private static @Nullable Set<String> headShapes;

   private ModelFaces() {
   }

   public static MobFaces.@Nullable Face of(Model model, @Nullable Object state) {
      try {
         return read(model, state);
      } catch (RuntimeException e) {
         Diorama.LOGGER.debug("Couldn't read a face off {}", model.getClass().getName(), e);
         return null;
      }
   }

   private static MobFaces.@Nullable Face read(Model model, @Nullable Object state) {
      ModelPart head = HEADS.computeIfAbsent(model, m -> Optional.ofNullable(head((Model)m))).orElse(null);
      if (head == null) {
         return null;
      }

      List<ModelPart> touched = parts(head);
      List<ModelFaces.Held> held = new ArrayList<>(touched.size());

      for (ModelPart part : touched) {
         held.add(ModelFaces.Held.of(part));
      }

      try {
         List<ModelPart> headParts = parts(head);
         BitSet shown = new BitSet(headParts.size() * 2);

         for (int i = 0; i < headParts.size(); i++) {
            shown.set(i * 2, headParts.get(i).visible);
            shown.set(i * 2 + 1, headParts.get(i).skipDraw);
         }

         Map<BitSet, Optional<MobFaces.Face>> faces = FACES.computeIfAbsent(model, m -> new HashMap<>());
         Optional<MobFaces.Face> face = faces.get(shown);
         if (face == null) {
            face = Optional.ofNullable(readHead(head, headParts));
            faces.put(shown, face);
         }

         return face.orElse(null);
      } finally {
         for (ModelFaces.Held h : held) {
            h.restore();
         }
      }
   }

   private static MobFaces.@Nullable Face readHead(ModelPart head, List<ModelPart> headParts) {
      for (ModelPart part : headParts) {
         part.resetPose();
      }

      head.setRotation(0.0F, 0.0F, 0.0F);
      List<ModelFaces.Front> fronts = new ArrayList<>();
      head.visit(new PoseStack(), (pose, path, index, cube) -> {
         if (shown(head, path)) {
            ModelFaces.Front front = front(pose, cube);
            if (front != null) {
               fronts.add(front);
            }
         }
      });
      return layOut(fronts);
   }

   private static @Nullable ModelPart head(Model model) {
      if (model instanceof HeadedModel headed) {
         return headed.getHead();
      } else {
         ModelPart root = model instanceof HierarchicalModel<?> hierarchical ? hierarchical.root() : null;
         ModelPart shaped = null;
         if (root == null) {
            for (Class<?> c = model.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
               for (Field field : c.getDeclaredFields()) {
                  if (field.getType() == ModelPart.class && !Modifier.isStatic(field.getModifiers())) {
                     try {
                        field.setAccessible(true);
                        ModelPart part = (ModelPart)field.get(model);
                        if (part != null && field.getName().equalsIgnoreCase("head")) {
                           return part;
                        }

                        if (part != null && root == null && part.hasChild("head")) {
                           root = part;
                        }

                        if (part != null && shaped == null && headShapes().contains(shape(part))) {
                           shaped = part;
                        }
                     } catch (ReflectiveOperationException | RuntimeException var9) {
                     }
                  }
               }
            }

            if (root == null) {
               return shaped;
            }
         }

         return headBelow(root);
      }
   }

   private static Set<String> headShapes() {
      Set<String> shapes = headShapes;
      if (shapes == null) {
         shapes = new HashSet<>();
         EntityModelSet models = Minecraft.getInstance().getEntityModels();
         Iterator<ModelLayerLocation> it = ModelLayers.getKnownLocations().iterator();

         while (it.hasNext()) {
            try {
               ModelPart layer = models.bakeLayer(it.next());
               String head = layer.hasChild("head") ? shape(layer.getChild("head")) : "";
               if (!head.isEmpty()) {
                  shapes.add(head);
               }
            } catch (RuntimeException var5) {
            }
         }

         headShapes = shapes;
      }

      return shapes;
   }

   private static String shape(ModelPart part) {
      StringBuilder shape = new StringBuilder();
      part.visit(
         new PoseStack(),
         (pose, path, index, cube) -> shape.append(path)
            .append('#')
            .append(index)
            .append(':')
            .append(cube.minX)
            .append(',')
            .append(cube.minY)
            .append(',')
            .append(cube.minZ)
            .append(',')
            .append(cube.maxX)
            .append(',')
            .append(cube.maxY)
            .append(',')
            .append(cube.maxZ)
            .append(';')
      );
      return shape.toString();
   }

   private static @Nullable ModelPart headBelow(ModelPart root) {
      int[] rank = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
      ModelPart[] found = new ModelPart[1];
      root.visit(new PoseStack(), (pose, path, index, cube) -> {
         String[] names = path.split("/");

         for (int i = 1; i < names.length; i++) {
            int kind = headName(names[i]);
            if (kind != Integer.MAX_VALUE && (kind < rank[0] || kind == rank[0] && i < rank[1])) {
               ModelPart part = resolve(root, names, i);
               if (part != null) {
                  found[0] = part;
                  rank[0] = kind;
                  rank[1] = i;
               }
            }
         }
      });
      return found[0];
   }

   private static int headName(String name) {
      String lower = name.toLowerCase(Locale.ROOT);
      if (lower.equals("head")) {
         return 0;
      } else if (lower.equals("skull")) {
         return 1;
      } else if (!lower.contains("head") && !lower.contains("skull")) {
         return lower.equals("neck") ? 3 : Integer.MAX_VALUE;
      } else {
         return 2;
      }
   }

   private static @Nullable ModelPart resolve(ModelPart root, String[] names, int last) {
      ModelPart part = root;

      for (int i = 1; i <= last; i++) {
         if (!part.hasChild(names[i])) {
            return null;
         }

         part = part.getChild(names[i]);
      }

      return part;
   }

   private static boolean shown(ModelPart head, String path) {
      ModelPart part = head;
      if (!part.visible) {
         return false;
      }

      for (String name : path.split("/")) {
         if (!name.isEmpty()) {
            if (!part.hasChild(name)) {
               return false;
            }

            part = part.getChild(name);
            if (!part.visible) {
               return false;
            }
         }
      }

      return !part.skipDraw;
   }

   private static List<ModelPart> parts(ModelPart top) {
      return top.getAllParts().toList();
   }

   private static ModelFaces.@Nullable Front front(Pose pose, Cube cube) {
      int w = Math.round(cube.maxX - cube.minX);
      int h = Math.round(cube.maxY - cube.minY);
      if (w > 0 && h > 0) {
         ModelFaces.Corners corners = new ModelFaces.Corners();
         cube.compile(pose, corners, 0, 0, -1);

         for (int q = 0; q + 4 <= corners.count; q += 4) {
            if (!(corners.at[q * 8 + 7] > -0.999F)) {
               float minX = Float.MAX_VALUE;
               float maxX = -Float.MAX_VALUE;
               float minY = Float.MAX_VALUE;
               float maxY = -Float.MAX_VALUE;
               float z = 0.0F;
               float uLeft = 0.0F;
               float uRight = 0.0F;
               float vTop = 0.0F;
               float vBottom = 0.0F;

               for (int i = q; i < q + 4; i++) {
                  int o = i * 8;
                  float x = corners.at[o] * 16.0F;
                  float y = corners.at[o + 1] * 16.0F;
                  z += corners.at[o + 2] * 16.0F / 4.0F;
                  if (x < minX) {
                     minX = x;
                     uLeft = corners.at[o + 3];
                  }

                  if (x > maxX) {
                     maxX = x;
                     uRight = corners.at[o + 3];
                  }

                  if (y < minY) {
                     minY = y;
                     vTop = corners.at[o + 4];
                  }

                  if (y > maxY) {
                     maxY = y;
                     vBottom = corners.at[o + 4];
                  }
               }

               float grownX = maxX - minX - w;
               float grownY = maxY - minY - h;
               boolean straight = Math.abs(grownX - grownY) < 0.01F && grownX > -0.01F && grownX < 2.0F;
               return new ModelFaces.Front((minX + maxX) / 2.0F, (minY + maxY) / 2.0F, z, w, h, straight, uLeft, vTop, uRight, vBottom);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static MobFaces.@Nullable Face layOut(List<ModelFaces.Front> fronts) {
      if (fronts.isEmpty()) {
         return null;
      }

      ModelFaces.Front main = fronts.stream().filter(ModelFaces.Front::straight).max(Comparator.comparingInt(f -> f.w() * f.h())).orElse(null);
      if (main == null) {
         return null;
      }

      float du = main.uRight() - main.uLeft();
      float dv = main.vBottom() - main.vTop();
      if (du != 0.0F && dv != 0.0F) {
         boolean flip = du < 0.0F;
         int texW = Math.round(main.w() / Math.abs(du));
         int texH = Math.round(main.h() / Math.abs(dv));
         List<ModelFaces.Front> sorted = new ArrayList<>(fronts);
         sorted.sort(Comparator.comparingDouble(f -> -f.z()));
         int minX = Integer.MAX_VALUE;
         int minY = Integer.MAX_VALUE;
         int maxX = Integer.MIN_VALUE;
         int maxY = Integer.MIN_VALUE;
         List<MobFaces.Piece> pieces = new ArrayList<>();

         for (ModelFaces.Front f : sorted) {
            float cx = flip ? -f.cx() : f.cx();
            float uLeft = flip ? f.uRight() : f.uLeft();
            float uRight = flip ? f.uLeft() : f.uRight();
            if (!(uRight < uLeft) && !(f.vBottom() < f.vTop())) {
               int w = Math.round((uRight - uLeft) * texW);
               int h = Math.round((f.vBottom() - f.vTop()) * texH);
               if (w > 0 && h > 0) {
                  int x = Math.round(cx - w / 2.0F);
                  int y = Math.round(f.cy() - h / 2.0F);
                  pieces.add(new MobFaces.Piece(Math.round(uLeft * texW), Math.round(f.vTop() * texH), w, h, x, y));
                  minX = Math.min(minX, x);
                  minY = Math.min(minY, y);
                  maxX = Math.max(maxX, x + w);
                  maxY = Math.max(maxY, y + h);
               }
            }
         }

         int w = maxX - minX;
         int h = maxY - minY;
         if (!pieces.isEmpty() && w <= 32 && h <= 32 && w <= 3 * h && h <= 3 * w && w >= 3 && h >= 3) {
            List<MobFaces.Piece> placed = new ArrayList<>(pieces.size());

            for (MobFaces.Piece p : pieces) {
               placed.add(new MobFaces.Piece(p.u(), p.v(), p.w(), p.h(), p.dx() - minX, p.dy() - minY));
            }

            return new MobFaces.Face(texW, texH, w, h, List.copyOf(placed));
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static final class Corners implements VertexConsumer {
      static final int SIZE = 8;
      float[] at = new float[192];
      int count;

      public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
         if ((this.count + 1) * 8 > this.at.length) {
            this.at = Arrays.copyOf(this.at, this.at.length * 2);
         }

         int o = this.count++ * 8;
         this.at[o] = x;
         this.at[o + 1] = y;
         this.at[o + 2] = z;
         this.at[o + 3] = u;
         this.at[o + 4] = v;
         this.at[o + 5] = nx;
         this.at[o + 6] = ny;
         this.at[o + 7] = nz;
      }

      public VertexConsumer addVertex(float x, float y, float z) {
         return this;
      }

      public VertexConsumer setColor(int r, int g, int b, int a) {
         return this;
      }

      public VertexConsumer setColor(int color) {
         return this;
      }

      public VertexConsumer setUv(float u, float v) {
         return this;
      }

      public VertexConsumer setUv1(int u, int v) {
         return this;
      }

      public VertexConsumer setUv2(int u, int v) {
         return this;
      }

      public VertexConsumer setUv3(float u, float v) {
         return this;
      }

      public VertexConsumer setNormal(float x, float y, float z) {
         return this;
      }

      public VertexConsumer setLineWidth(float width) {
         return this;
      }
   }

   private record Front(float cx, float cy, float z, int w, int h, boolean straight, float uLeft, float vTop, float uRight, float vBottom) {
   }

   private record Held(ModelPart part, PartPose pose, float xScale, float yScale, float zScale, boolean visible, boolean skipDraw) {
      static ModelFaces.Held of(ModelPart part) {
         return new ModelFaces.Held(part, part.storePose(), part.xScale, part.yScale, part.zScale, part.visible, part.skipDraw);
      }

      void restore() {
         this.part.loadPose(this.pose);
         this.part.xScale = this.xScale;
         this.part.yScale = this.yScale;
         this.part.zScale = this.zScale;
         this.part.visible = this.visible;
         this.part.skipDraw = this.skipDraw;
      }
   }
}
