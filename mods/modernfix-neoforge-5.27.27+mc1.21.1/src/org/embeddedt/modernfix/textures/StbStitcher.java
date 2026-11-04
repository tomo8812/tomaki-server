package org.embeddedt.modernfix.textures;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.Stitcher.Entry;
import net.minecraft.client.renderer.texture.Stitcher.Holder;
import net.minecraft.util.Mth;
import org.embeddedt.modernfix.ModernFix;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;
import org.lwjgl.stb.STBRPRect.Buffer;

public class StbStitcher {
   private static final MethodHandle MH_rect_shortSet;
   private static final MethodHandle MH_rect_intSet;
   private static final MethodHandle MH_rect_intX;
   private static final MethodHandle MH_rect_intY;
   private static final MethodHandle MH_rect_shortX;
   private static final MethodHandle MH_rect_shortY;

   public static STBRPRect setWrapper(STBRPRect rect, int id, int width, int height, int x, int y, boolean was_packed) {
      try {
         return MH_rect_shortSet != null
            ? (STBRPRect)MH_rect_shortSet.invokeExact(
               (STBRPRect)rect, (int)id, (short)((short)width), (short)((short)height), (short)((short)x), (short)((short)y), (boolean)was_packed
            )
            : (STBRPRect)MH_rect_intSet.invokeExact((STBRPRect)rect, (int)id, (int)width, (int)height, (int)x, (int)y, (boolean)was_packed);
      } catch (Throwable e) {
         throw new AssertionError(e);
      }
   }

   public static int getX(STBRPRect rect) {
      try {
         return MH_rect_shortX != null ? (short)MH_rect_shortX.invokeExact((STBRPRect)rect) : (int)MH_rect_intX.invokeExact((STBRPRect)rect);
      } catch (Throwable e) {
         throw new AssertionError(e);
      }
   }

   public static int getY(STBRPRect rect) {
      try {
         return MH_rect_shortX != null ? (short)MH_rect_shortY.invokeExact((STBRPRect)rect) : (int)MH_rect_intY.invokeExact((STBRPRect)rect);
      } catch (Throwable e) {
         throw new AssertionError(e);
      }
   }

   public static <T extends Entry> Pair<Pair<Integer, Integer>, List<StbStitcher.LoadableSpriteInfo<T>>> packRects(Holder<T>[] holders) {
      int holderSize = holders.length;
      List<StbStitcher.LoadableSpriteInfo<T>> infoList = new ArrayList<>();
      Buffer rectBuf = STBRPRect.malloc(holderSize);

      Pair var28;
      try {
         STBRPContext ctx = STBRPContext.malloc();

         try {
            int totalArea = 0;
            int longestWidth = 0;
            int longestHeight = 0;

            for (int j = 0; j < holderSize; j++) {
               Holder<T> holder = holders[j];
               int width = holder.width();
               int height = holder.height();
               STBRPRect rect = (STBRPRect)rectBuf.get(j);
               setWrapper(rect, j, width, height, 0, 0, false);
               totalArea += width * height;
               longestWidth = Math.max(longestWidth, width);
               longestHeight = Math.max(longestHeight, height);
            }

            longestWidth = Mth.smallestEncompassingPowerOfTwo(longestWidth);
            longestHeight = Mth.smallestEncompassingPowerOfTwo(longestHeight);

            while (longestWidth * longestHeight < totalArea) {
               if (longestWidth <= longestHeight) {
                  longestWidth *= 2;
               } else {
                  longestHeight *= 2;
               }
            }

            int numTries = 0;

            while (true) {
               numTries++;

               try {
                  org.lwjgl.stb.STBRPNode.Buffer nodes = STBRPNode.malloc(longestWidth + 10);

                  try {
                     STBRectPack.stbrp_init_target(ctx, longestWidth, longestHeight, nodes);
                     STBRectPack.stbrp_pack_rects(ctx, rectBuf);

                     for (STBRPRect rect : rectBuf) {
                        Holder<T> holder = holders[rect.id()];
                        if (!rect.was_packed()) {
                           throw new StitcherException(holder.entry(), Stream.of(holders).map(Holder::entry).collect(ImmutableList.toImmutableList()));
                        }
                     }

                     for (STBRPRect rect : rectBuf) {
                        Holder<T> holder = holders[rect.id()];
                        infoList.add(new StbStitcher.LoadableSpriteInfo<>((T)holder.entry(), longestWidth, longestHeight, getX(rect), getY(rect)));
                     }

                     var28 = Pair.of(Pair.of(longestWidth, longestHeight), infoList);
                  } catch (Throwable var17) {
                     if (nodes != null) {
                        try {
                           nodes.close();
                        } catch (Throwable var16) {
                           var17.addSuppressed(var16);
                        }
                     }

                     throw var17;
                  }

                  if (nodes != null) {
                     nodes.close();
                  }
                  break;
               } catch (StitcherException e) {
                  if (numTries >= 4) {
                     ModernFix.LOGGER.error("Stitcher ran out of space with target atlas size " + longestWidth + "x" + longestHeight + ":");

                     for (Holder<T> h : holders) {
                        ModernFix.LOGGER.error(" - " + h.entry().name() + ", " + h.width() + "x" + h.height());
                     }

                     throw e;
                  }

                  if (longestWidth <= longestHeight) {
                     longestWidth *= 2;
                  } else {
                     longestHeight *= 2;
                  }
               }
            }
         } catch (Throwable var19) {
            if (ctx != null) {
               try {
                  ctx.close();
               } catch (Throwable var15) {
                  var19.addSuppressed(var15);
               }
            }

            throw var19;
         }

         if (ctx != null) {
            ctx.close();
         }
      } catch (Throwable var20) {
         if (rectBuf != null) {
            try {
               rectBuf.close();
            } catch (Throwable var14) {
               var20.addSuppressed(var14);
            }
         }

         throw var20;
      }

      if (rectBuf != null) {
         rectBuf.close();
      }

      return var28;
   }

   static {
      MethodHandle shortM = null;
      MethodHandle intM = null;
      List<ReflectiveOperationException> exceptions = new ArrayList<>();

      try {
         intM = MethodHandles.publicLookup()
            .findVirtual(STBRPRect.class, "set", MethodType.methodType(STBRPRect.class, int.class, int.class, int.class, int.class, int.class, boolean.class));
      } catch (ReflectiveOperationException e) {
         exceptions.add(e);
      }

      try {
         shortM = MethodHandles.publicLookup()
            .findVirtual(
               STBRPRect.class, "set", MethodType.methodType(STBRPRect.class, int.class, short.class, short.class, short.class, short.class, boolean.class)
            );
      } catch (ReflectiveOperationException e) {
         exceptions.add(e);
      }

      if (shortM == null && intM == null) {
         IllegalStateException e = new IllegalStateException("An STBRPRect set method could not be located");
         exceptions.forEach(e::addSuppressed);
         throw e;
      }

      MH_rect_shortSet = shortM;
      MH_rect_intSet = intM;
      exceptions.clear();

      try {
         intM = MethodHandles.publicLookup().findVirtual(STBRPRect.class, "x", MethodType.methodType(int.class));
      } catch (ReflectiveOperationException e) {
         exceptions.add(e);
      }

      try {
         shortM = MethodHandles.publicLookup().findVirtual(STBRPRect.class, "x", MethodType.methodType(short.class));
      } catch (ReflectiveOperationException e) {
         exceptions.add(e);
      }

      if (shortM == null && intM == null) {
         IllegalStateException e = new IllegalStateException("An STBRPRect x() method could not be located");
         exceptions.forEach(e::addSuppressed);
         throw e;
      }

      MH_rect_shortX = shortM;
      MH_rect_intX = intM;

      try {
         if (MH_rect_shortX != null) {
            MH_rect_shortY = MethodHandles.publicLookup().findVirtual(STBRPRect.class, "y", MethodType.methodType(short.class));
            MH_rect_intY = null;
         } else {
            MH_rect_intY = MethodHandles.publicLookup().findVirtual(STBRPRect.class, "y", MethodType.methodType(int.class));
            MH_rect_shortY = null;
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("An STBRPRect y() method could not be located", e);
      }
   }

   public static class LoadableSpriteInfo<T extends Entry> {
      public final T info;
      public final int width;
      public final int height;
      public final int x;
      public final int y;

      LoadableSpriteInfo(T info, int width, int height, int x, int y) {
         this.info = info;
         this.width = width;
         this.height = height;
         this.x = x;
         this.y = y;
      }
   }
}
