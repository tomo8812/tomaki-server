package dev.diorama.bake;

import dev.diorama.Facing;
import dev.diorama.MapStyle;
import dev.diorama.Projection;
import dev.diorama.data.ChunkSnapshot;
import java.lang.ref.Cleaner;
import java.lang.ref.Cleaner.Cleanable;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public final class GpuMesher {
   public static final int TOP = 0;
   public static final int SOUTH = 1;
   public static final int WEST = 2;
   public static final int FLOOR = 3;
   public static final int UNKNOWN = 1;
   public static final int LAVA = 2;
   public static final int CUT = 4;
   public static final int VOID = 8;
   public static final int OVERLAY = 16;
   public static final int SLIME = 32;
   public static final int SPAWN_AT_NIGHT = 64;
   public static final int SPAWN_ANY_TIME = 128;
   public static final int WATER_FACE = 256;
   private static final byte NO_WATER = 0;
   private static final byte SEEN_WATER = 1;
   private static final byte UNSEEN_WATER = 2;
   public static final int SECTION_UNKNOWN = -2;
   public static final int SECTION_EMPTY = -1;
   public static final int SECTION_PARTIAL = Integer.MIN_VALUE;
   public static final int STRIDE = 14;
   private static final int FLOOR_LO = ShapeFaces.pack(0, 0, 0);
   private static final int FLOOR_HI = ShapeFaces.pack(128, 0, 128);

   private GpuMesher() {
   }

   private static int zLow(double minV, int y, double sin, double cos) {
      return (int)Math.floor((minV + y * cos) / sin) - 1;
   }

   private static int zHigh(double maxV, int y, double sin, double cos) {
      return (int)Math.floor((maxV + (y + 1) * cos) / sin) + 1;
   }

   public static GpuMesher.Mesh build(TileKey key, ChunkSource source, BlockLooks looks, boolean fogOfWar, @Nullable Overlays overlays) {
      return build(key, source, looks, fogOfWar, overlays, null);
   }

   public static GpuMesher.Mesh build(
      TileKey key, ChunkSource source, BlockLooks looks, boolean fogOfWar, @Nullable Overlays overlays, int @Nullable [] savedSpots
   ) {
      long start = System.nanoTime();
      TileBaker helper = TileBaker.forMeshing(key, source, looks, fogOfWar, savedSpots);
      VoxelReader reader = helper.reader;
      MapStyle style = MapStyle.get();
      double sin = Projection.sin();
      double cos = Projection.cos();
      int startY = helper.startY;
      int limitY = helper.limitY;
      boolean lit = helper.lit;
      boolean fog = helper.fogOfWar;
      GpuMesher.Mesh mesh = new GpuMesher.Mesh(key, startY, limitY, sin, cos, lit, helper.cave, startY <= limitY);
      mesh.overlays = overlays;
      boolean lifted = helper.lifted;
      mesh.lifted = lifted;
      mesh.depthLook = helper.depthLook;
      if (helper.caveCut != null) {
         mesh.traceInstead = true;
         return mesh;
      }

      mesh.roof = source.roof();
      mesh.rayTop = lifted ? Math.min(helper.rayTop, startY) : helper.rayTop;
      double minV = key.minV();
      double maxV = minV + 128.0;
      int x0 = mesh.originX;
      int x1 = x0 + 128;
      int zBase = zLow(minV, limitY, sin, cos) - 2;
      int zSpan = zHigh(maxV, startY, sin, cos) - zBase + 3;
      boolean[] aboveOpen = new boolean[zSpan];
      boolean[] hereOpen = new boolean[zSpan];
      boolean[] aboveRock = new boolean[zSpan];
      boolean[] hereRock = new boolean[zSpan];
      double cot = cos / sin;
      double[][] aboveRockU = new double[zSpan][8];
      double[][] hereRockU = new double[zSpan][8];
      int[] aboveRockN = new int[zSpan];
      int[] hereRockN = new int[zSpan];
      double[] rockIn = new double[8];
      int[] pieces = new int[10];
      boolean[] aboveWater = new boolean[zSpan];
      boolean[] hereWater = new boolean[zSpan];
      byte[] aboveFirstWater = new byte[zSpan];
      byte[] hereFirstWater = new byte[zSpan];
      int fx0 = Integer.MAX_VALUE;
      int fx1 = Integer.MIN_VALUE;
      int fy0 = Integer.MAX_VALUE;
      int fy1 = Integer.MIN_VALUE;
      int fz0 = Integer.MAX_VALUE;
      int fz1 = Integer.MIN_VALUE;

      for (int x = x0 - 1; x <= x1 && !mesh.empty; x++) {
         Arrays.fill(aboveOpen, !lit);
         Arrays.fill(aboveRock, lit);
         Arrays.fill(aboveRockN, -1);
         Arrays.fill(aboveWater, false);
         Arrays.fill(aboveFirstWater, (byte)0);

         for (int y = startY - 1; y >= limitY; y--) {
            int lo = zLow(minV, y, sin, cos);
            int hi = zHigh(maxV, y, sin, cos);
            Arrays.fill(hereOpen, false);
            Arrays.fill(hereRock, false);
            Arrays.fill(hereRockN, 0);
            Arrays.fill(hereWater, false);
            Arrays.fill(hereFirstWater, (byte)0);
            boolean any = false;

            for (int z = hi; z >= lo; z--) {
               int i = z - zBase;
               boolean openAbove = aboveOpen[i];
               boolean openSouth = z < hi && hereOpen[i + 1];
               boolean rockAbove = aboveRock[i];
               boolean rockSouth = z < hi && hereRock[i + 1];
               boolean fromAbove = openAbove || rockAbove;
               boolean fromSouth = openSouth || rockSouth;
               if (fromAbove || fromSouth) {
                  int rockN = 0;
                  if (rockAbove || rockSouth) {
                     rockN = RockRays.enter(
                        rockIn, aboveRockU[i], rockAbove ? aboveRockN[i] : 0, rockSouth ? hereRockU[i + 1] : null, rockSouth ? hereRockN[i + 1] : 0, y, z, cot
                     );
                     if (rockN > 4) {
                        mesh.traceInstead = true;
                        rockN = -1;
                     }
                  }

                  int id = reader.state(x, y, z);
                  if (id == -1) {
                     if (!helper.passesMissing(y) && !helper.passedWhenSaved(x, y, z)) {
                        if (fromAbove) {
                           mesh.opaque.add(x, y, z, 0, 0, 0, 0, 1);
                        }

                        if (fromSouth) {
                           mesh.opaque.add(x, y, z, 1, 0, 0, 0, 1);
                        }
                        continue;
                     }

                     id = VoxelReader.AIR_ID;
                  }

                  byte kind = looks.kind[id];
                  int shape = kind != 1 && kind != 4 ? 0 : looks.shape[id];
                  if (lifted && rockN != 0 && kind != 1 && y + 1 < startY && reader.pocket(x, y, z)) {
                     keepRock(hereRock, hereRockU, hereRockN, i, rockIn, rockN);
                     any = true;
                     if (openAbove || openSouth) {
                        if (kind == 3) {
                           if (openAbove) {
                              solidFace(
                                 mesh, helper, source, looks, style, id, x, y, z, true, kind, lit, fog, 0, ShapeFaces.FULL_HI, rockIn, rockN, cot, pieces
                              );
                           }

                           if (openSouth) {
                              solidFace(
                                 mesh, helper, source, looks, style, id, x, y, z, false, kind, lit, fog, 0, ShapeFaces.FULL_HI, rockIn, rockN, cot, pieces
                              );
                           }

                           mesh.lowestY = Math.min(mesh.lowestY, y);
                        } else if (kind == 0 || (kind == 5 || kind == 4) && shape == 0) {
                           hereOpen[i] = true;
                           if (fog) {
                              byte a = openAbove ? aboveFirstWater[i] : 0;
                              byte b = openSouth ? hereFirstWater[i + 1] : 0;
                              hereFirstWater[i] = (byte)(a != 1 && b != 1 ? (a != 2 && b != 2 ? 0 : 2) : 1);
                           }

                           if (kind != 0) {
                              helper.albedo(id, x, z, true);
                              int color = encode(helper.albedoR, helper.albedoG, helper.albedoB, kind == 5 ? 102 : 64);
                              if (openAbove) {
                                 tintedFace(mesh.tinted, x, y, z, true, color, rockIn, rockN, cot, pieces);
                              }

                              if (openSouth) {
                                 tintedFace(mesh.tinted, x, y, z, false, color, rockIn, rockN, cot, pieces);
                              }
                           }
                        } else {
                           mesh.traceInstead = true;
                        }
                     }
                  } else if (shape != 0) {
                     ShapeFaces sh = looks.faces(shape);
                     int wet = looks.wetShape[id];
                     boolean passes = false;
                     boolean traced = false;
                     boolean underside = false;
                     boolean reached;
                     if (kind == 1) {
                        if (rockN != 0) {
                           keepRock(hereRock, hereRockU, hereRockN, i, rockIn, rockN);
                           any = true;
                        }

                        reached = openAbove || openSouth;
                        if (reached && (lifted || helper.depthLook) && reader.ceilingUnderside(x, y, z, helper.lid(x, z), limitY)) {
                           passes = true;
                           underside = true;
                           if (wet >= 0) {
                              mesh.traceInstead = true;
                           }
                        } else if (reached) {
                           mesh.lowestY = Math.min(mesh.lowestY, y);

                           for (int b = 0; b < sh.top.length; b += 2) {
                              if (openAbove || ShapeFaces.inset(sh.top[b + 1], true)) {
                                 solidFace(
                                    mesh, helper, source, looks, style, id, x, y, z, true, kind, lit, fog, sh.top[b], sh.top[b + 1], rockIn, rockN, cot, pieces
                                 );
                              }
                           }

                           for (int b = 0; b < sh.south.length; b += 2) {
                              if (openSouth || ShapeFaces.inset(sh.south[b + 1], false)) {
                                 solidFace(
                                    mesh,
                                    helper,
                                    source,
                                    looks,
                                    style,
                                    id,
                                    x,
                                    y,
                                    z,
                                    false,
                                    kind,
                                    lit,
                                    fog,
                                    sh.south[b],
                                    sh.south[b + 1],
                                    rockIn,
                                    rockN,
                                    cot,
                                    pieces
                                 );
                              }
                           }

                           passes = true;
                           if (wet >= 0 && rockN != 0 && (wet(reader, looks, x, y - 1, z) || wet(reader, looks, x, y, z - 1))) {
                              traced = true;
                              mesh.traceInstead = true;
                           }
                        }
                     } else {
                        reached = fromAbove || fromSouth;
                        if (reached) {
                           helper.albedo(id, x, z, true);
                           int color = encode(helper.albedoR, helper.albedoG, helper.albedoB, 64);
                           int[][] seen = sh.seen(sin, cos);

                           for (int b = 0; b < seen[0].length; b += 2) {
                              if (fromAbove || ShapeFaces.inset(seen[0][b + 1], true)) {
                                 mesh.tinted.add(x, y, z, 0, color, 0, 0, 0, seen[0][b], seen[0][b + 1]);
                              }
                           }

                           for (int b = 0; b < seen[1].length; b += 2) {
                              if (fromSouth || ShapeFaces.inset(seen[1][b + 1], false)) {
                                 mesh.tinted.add(x, y, z, 1, color, 0, 0, 0, seen[1][b], seen[1][b + 1]);
                              }
                           }

                           passes = true;
                        }
                     }

                     if (passes) {
                        hereOpen[i] = true;
                        any = true;
                        if (fog) {
                           byte a = openAbove ? aboveFirstWater[i] : 0;
                           byte c = openSouth ? hereFirstWater[i + 1] : 0;
                           hereFirstWater[i] = (byte)(a != 1 && c != 1 ? (a != 2 && c != 2 ? 0 : 2) : 1);
                        }
                     }

                     if (reached && !traced && !underside && wet >= 0) {
                        int[] wetTop = sh.wetTop;
                        int[] wetSouth = sh.wetSouth;
                        if (kind == 1 && rockN != 0) {
                           wetTop = outsideRock(sh.wetTop, true, rockIn, rockN, y, z, cot, pieces);
                           wetSouth = outsideRock(sh.wetSouth, false, rockIn, rockN, y, z, cot, pieces);
                        }

                        waterCell(
                           mesh,
                           helper,
                           reader,
                           source,
                           wet,
                           x,
                           y,
                           z,
                           i,
                           fromAbove,
                           fromSouth,
                           openAbove,
                           openSouth,
                           aboveWater,
                           hereWater,
                           hereFirstWater,
                           lit,
                           fog,
                           wetTop,
                           wetSouth
                        );
                        if (sh.wetCut) {
                           hereWater[i] = false;
                        }
                     }

                     if (fog && hereRock[i] && hereOpen[i] && hereFirstWater[i] != 0) {
                        mesh.traceInstead = true;
                     }
                  } else if (kind == 1) {
                     if (rockN != 0) {
                        keepRock(hereRock, hereRockU, hereRockN, i, rockIn, rockN);
                        any = true;
                     }

                     if ((openAbove || openSouth) && (lifted || helper.depthLook) && reader.ceilingUnderside(x, y, z, helper.lid(x, z), limitY)) {
                        hereOpen[i] = true;
                        any = true;
                        if (fog) {
                           byte a = openAbove ? aboveFirstWater[i] : 0;
                           byte b = openSouth ? hereFirstWater[i + 1] : 0;
                           hereFirstWater[i] = (byte)(a == 1 || b == 1 ? 1 : (a != 2 && b != 2 ? 0 : 2));
                        }
                     } else {
                        if (openAbove) {
                           solidFace(mesh, helper, source, looks, style, id, x, y, z, true, kind, lit, fog, 0, ShapeFaces.FULL_HI, rockIn, rockN, cot, pieces);
                        }

                        if (openSouth) {
                           solidFace(mesh, helper, source, looks, style, id, x, y, z, false, kind, lit, fog, 0, ShapeFaces.FULL_HI, rockIn, rockN, cot, pieces);
                        }

                        mesh.lowestY = Math.min(mesh.lowestY, y);
                     }
                  } else if (kind == 3) {
                     if (fromAbove) {
                        solidFace(mesh, helper, source, looks, style, id, x, y, z, true, kind, lit, fog);
                     }

                     if (fromSouth) {
                        solidFace(mesh, helper, source, looks, style, id, x, y, z, false, kind, lit, fog);
                     }

                     mesh.lowestY = Math.min(mesh.lowestY, y);
                  } else {
                     hereOpen[i] = true;
                     any = true;
                     if (fog) {
                        byte a = openAbove ? aboveFirstWater[i] : 0;
                        byte b = openSouth ? hereFirstWater[i + 1] : 0;
                        hereFirstWater[i] = (byte)(a != 1 && b != 1 ? (a != 2 && b != 2 ? 0 : 2) : 1);
                     }

                     if (kind == 2) {
                        waterCell(
                           mesh,
                           helper,
                           reader,
                           source,
                           id,
                           x,
                           y,
                           z,
                           i,
                           fromAbove,
                           fromSouth,
                           openAbove,
                           openSouth,
                           aboveWater,
                           hereWater,
                           hereFirstWater,
                           lit,
                           fog
                        );
                     } else if (kind == 5 || kind == 4) {
                        helper.albedo(id, x, z, true);
                        int weight = kind == 5 ? 102 : 64;
                        int color = encode(helper.albedoR, helper.albedoG, helper.albedoB, weight);
                        if (fromAbove) {
                           mesh.tinted.add(x, y, z, 0, color, 0, 0, 0);
                        }

                        if (fromSouth) {
                           mesh.tinted.add(x, y, z, 1, color, 0, 0, 0);
                        }
                     }
                  }

                  if (y < fy0) {
                     fy0 = y;
                  }

                  if (y + 1 > fy1) {
                     fy1 = y + 1;
                  }

                  fx0 = Math.min(fx0, x);
                  fx1 = Math.max(fx1, x);
                  fz0 = Math.min(fz0, z);
                  fz1 = Math.max(fz1, z);
               }
            }

            if (y == helper.rockLimit && y > limitY) {
               for (int z = hi; z >= lo; z--) {
                  int i = z - zBase;
                  if (hereRock[i]) {
                     if (hereOpen[i] && hereRockN[i] == -1) {
                        mesh.traceInstead = true;
                     } else if (hereOpen[i]) {
                        int n = RockRays.pieces(pieces, 0, true, FLOOR_LO, FLOOR_HI, hereRockU[i], hereRockN[i], y, z, cot, true);

                        for (int b = 0; b < n; b += 2) {
                           mesh.opaque.add(x, y, z, 3, 0, 0, 0, fog ? 1 : 4, pieces[b], pieces[b + 1]);
                        }
                     } else {
                        mesh.opaque.add(x, y, z, 3, 0, 0, 0, fog ? 1 : 4);
                     }

                     hereRock[i] = false;
                     hereRockN[i] = 0;
                  }
               }
            }

            if (y == limitY) {
               for (int z = hi; z >= lo; z--) {
                  int i = z - zBase;
                  if (hereRock[i] || hereOpen[i]) {
                     int open = fog && hereFirstWater[i] != 1 ? 1 : 8;
                     if (hereRock[i] && hereOpen[i] && hereRockN[i] != -1) {
                        int n = RockRays.pieces(pieces, 0, true, FLOOR_LO, FLOOR_HI, hereRockU[i], hereRockN[i], y, z, cot, true);

                        for (int b = 0; b < n; b += 2) {
                           mesh.opaque.add(x, y, z, 3, 0, 0, 0, fog ? 1 : 4, pieces[b], pieces[b + 1]);
                        }

                        n = RockRays.pieces(pieces, 0, true, FLOOR_LO, FLOOR_HI, hereRockU[i], hereRockN[i], y, z, cot, false);

                        for (int b = 0; b < n; b += 2) {
                           mesh.opaque.add(x, y, z, 3, 0, 0, 0, open, pieces[b], pieces[b + 1]);
                        }
                     } else {
                        mesh.opaque.add(x, y, z, 3, 0, 0, 0, hereRock[i] ? (fog ? 1 : 4) : open);
                     }
                  }
               }
            }

            boolean[] t = aboveOpen;
            aboveOpen = hereOpen;
            hereOpen = t;
            t = aboveRock;
            aboveRock = hereRock;
            hereRock = t;
            double[][] ru = aboveRockU;
            aboveRockU = hereRockU;
            hereRockU = ru;
            int[] rn = aboveRockN;
            aboveRockN = hereRockN;
            hereRockN = rn;
            t = aboveWater;
            aboveWater = hereWater;
            hereWater = t;
            byte[] w = aboveFirstWater;
            aboveFirstWater = hereFirstWater;
            hereFirstWater = w;
            if (!any) {
               break;
            }
         }
      }

      if (lit && !lifted) {
         mesh.casterX0 = x0 - 1;
         mesh.casterX1 = x1 + 2;
         mesh.casterZ0 = zBase;
         mesh.casterZ1 = zBase + zSpan;
         mesh.casterY0 = limitY;
         mesh.casterY1 = startY;
      } else {
         casters(mesh, helper, reader, looks, style, x0, x1, minV, maxV, source.minY());
         if (fx0 <= fx1) {
            voxels(mesh, helper, reader, fx0, fx1, fy0, fy1, fz0, fz1);
         }
      }

      mesh.originZ = Math.floorDiv(Math.min(zBase, mesh.casterZ0), 16) * 16;
      if (mesh.voxels != null) {
         mesh.voxelOrigin[2] = mesh.voxelOrigin[2] - (mesh.originZ >> 4);
      }

      sortNearToFar(mesh.tinted, sin, cos);
      mesh.buildNanos = System.nanoTime() - start;
      return mesh;
   }

   private static void waterCell(
      GpuMesher.Mesh mesh,
      TileBaker helper,
      VoxelReader reader,
      ChunkSource source,
      int id,
      int x,
      int y,
      int z,
      int i,
      boolean fromAbove,
      boolean fromSouth,
      boolean openAbove,
      boolean openSouth,
      boolean[] aboveWater,
      boolean[] hereWater,
      byte[] hereFirstWater,
      boolean lit,
      boolean fog
   ) {
      waterCell(
         mesh, helper, reader, source, id, x, y, z, i, fromAbove, fromSouth, openAbove, openSouth, aboveWater, hereWater, hereFirstWater, lit, fog, null, null
      );
   }

   private static void waterCell(
      GpuMesher.Mesh mesh,
      TileBaker helper,
      VoxelReader reader,
      ChunkSource source,
      int id,
      int x,
      int y,
      int z,
      int i,
      boolean fromAbove,
      boolean fromSouth,
      boolean openAbove,
      boolean openSouth,
      boolean[] aboveWater,
      boolean[] hereWater,
      byte[] hereFirstWater,
      boolean lit,
      boolean fog,
      int @Nullable [] top,
      int @Nullable [] south
   ) {
      hereWater[i] = true;
      boolean entersTop = fromAbove && (!openAbove || !aboveWater[i]);
      boolean entersSouth = fromSouth && (!openSouth || !hereWater[i + 1]);
      if (fog && hereFirstWater[i] == 0) {
         hereFirstWater[i] = (byte)(source.isSeen(x, y, z) ? 1 : 2);
      }

      if (entersTop || entersSouth) {
         helper.albedo(id, x, z, true);
         int color = encode(helper.albedoR, helper.albedoG, helper.albedoB, 255);
         int light = lit ? light(reader, x, y + 1, z) : 0;
         if (entersTop) {
            waterFaces(mesh.water, x, y, z, 0, color, light, top);
         }

         if (entersSouth) {
            waterFaces(mesh.water, x, y, z, 1, color, light, south);
         }
      }
   }

   private static void waterFaces(GpuMesher.Quads water, int x, int y, int z, int face, int color, int light, int @Nullable [] pieces) {
      if (pieces == null) {
         water.add(x, y, z, face, color, 0, light, 256);
      } else {
         for (int b = 0; b < pieces.length; b += 2) {
            water.add(x, y, z, face, color, 0, light, 256, pieces[b], pieces[b + 1]);
         }
      }
   }

   private static void keepRock(boolean[] rock, double[][] u, int[] n, int i, double[] in, int count) {
      rock[i] = true;
      n[i] = count;
      if (count > 0) {
         System.arraycopy(in, 0, u[i], 0, count * 2);
      }
   }

   private static boolean wet(VoxelReader reader, BlockLooks looks, int x, int y, int z) {
      int id = reader.state(x, y, z);
      return id != -1 && (looks.kind[id] == 2 || looks.wetShape[id] >= 0);
   }

   private static int[] outsideRock(int[] faces, boolean top, double[] rock, int rockN, int y, int z, double cot, int[] pieces) {
      int[] out = new int[faces.length * 5];
      int n = 0;

      for (int b = 0; b < faces.length; b += 2) {
         int end = RockRays.pieces(pieces, 0, top, faces[b], faces[b + 1], rock, rockN, y, z, cot, false);
         System.arraycopy(pieces, 0, out, n, end);
         n += end;
      }

      return Arrays.copyOf(out, n);
   }

   private static void tintedFace(GpuMesher.Quads tinted, int x, int y, int z, boolean top, int color, double[] rock, int rockN, double cot, int[] pieces) {
      int n = RockRays.pieces(pieces, 0, top, 0, ShapeFaces.FULL_HI, rock, rockN, y, z, cot, false);

      for (int b = 0; b < n; b += 2) {
         tinted.add(x, y, z, top ? 0 : 1, color, 0, 0, 0, pieces[b], pieces[b + 1]);
      }
   }

   private static void solidFace(
      GpuMesher.Mesh mesh,
      TileBaker helper,
      ChunkSource source,
      BlockLooks looks,
      MapStyle style,
      int id,
      int x,
      int y,
      int z,
      boolean top,
      byte kind,
      boolean lit,
      boolean fog,
      int lo,
      int hi,
      double[] rock,
      int rockN,
      double cot,
      int[] pieces
   ) {
      if (rockN == 0) {
         solidFace(mesh, helper, source, looks, style, id, x, y, z, top, kind, lit, fog, lo, hi);
      } else {
         int n = RockRays.pieces(pieces, 0, top, lo, hi, rock, rockN, y, z, cot, false);

         for (int b = 0; b < n; b += 2) {
            solidFace(mesh, helper, source, looks, style, id, x, y, z, top, kind, lit, fog, pieces[b], pieces[b + 1]);
         }
      }
   }

   private static void solidFace(
      GpuMesher.Mesh mesh,
      TileBaker helper,
      ChunkSource source,
      BlockLooks looks,
      MapStyle style,
      int id,
      int x,
      int y,
      int z,
      boolean top,
      byte kind,
      boolean lit,
      boolean fog
   ) {
      solidFace(mesh, helper, source, looks, style, id, x, y, z, top, kind, lit, fog, 0, ShapeFaces.FULL_HI);
   }

   private static void solidFace(
      GpuMesher.Mesh mesh,
      TileBaker helper,
      ChunkSource source,
      BlockLooks looks,
      MapStyle style,
      int id,
      int x,
      int y,
      int z,
      boolean top,
      byte kind,
      boolean lit,
      boolean fog,
      int lo,
      int hi
   ) {
      int face = top ? 0 : 1;
      if (!fog || source.isSeen(x, y, z) || (top ? source.isSeen(x, y + 1, z) : source.isSeen(x, y, z + 1))) {
         int color = solidColor(helper, id, x, y, z, top, looks.emission[id], kind, style);
         int ao = top ? aoTop(helper, x, y + 1, z) : aoSouth(helper, x, y, z + 1);
         int light = lit
            ? (top ? light(helper.reader, x, y + 1, z) : light(helper.reader, x, y, z + 1))
            : (top ? helper.reader.blockLight(x, y + 1, z) : helper.reader.blockLight(x, y, z + 1)) << 16;
         if (mesh.depthLook && !top) {
            light |= helper.wallDepth(x, y, z) << 8;
         }

         int flags = kind == 3 ? 2 : 0;
         if (mesh.overlays != null && kind == 1) {
            flags |= overlayFlags(mesh, helper.reader, looks, id, x, y, z, top);
         }

         mesh.opaque.add(x, y, z, face, color, ao, light, flags, lo, hi);
         if (mesh.lifted) {
            double unit = 128.0;
            if (top) {
               double fy = y + (hi >> 8 & 0xFF) / unit;
               mesh.opaque.haze(fogAt(helper, x, fy, z), fogAt(helper, x + 1, fy, z), fogAt(helper, x, fy, z + 1), fogAt(helper, x + 1, fy, z + 1));
            } else {
               double fy = y + 0.5;
               double fz = z + (hi >> 16 & 0xFF) / unit;
               int west = fogAt(helper, x, fy, fz);
               int east = fogAt(helper, x + 1, fy, fz);
               mesh.opaque.haze(west, east, west, east);
            }
         }
      } else {
         mesh.opaque.add(x, y, z, face, 0, 0, 0, 1, lo, hi);
      }
   }

   private static int fogAt(TileBaker helper, double x, double y, double z) {
      helper.fogBlend(x, y, z);
      return srgb(helper.hazeR) | srgb(helper.hazeG) << 8 | srgb(helper.hazeB) << 16;
   }

   private static int overlayFlags(GpuMesher.Mesh mesh, VoxelReader reader, BlockLooks looks, int id, int x, int y, int z, boolean top) {
      int flags = 16;
      Overlays overlays = mesh.overlays;
      Long seed = overlays.slimeSeed();
      if (seed != null) {
         int cx = x >> 4;
         int cz = z >> 4;
         Facing facing = Projection.facing();
         if (mesh.slimeChunks
            .computeIfAbsent((long)cx << 32 | cz & 4294967295L, k -> Overlays.slimeChunk(seed, facing.worldBlockX(cx, cz), facing.worldBlockZ(cx, cz)))) {
            flags |= 32;
         }
      }

      if (overlays.spawnSpots() && top && looks.spawnOn[id]) {
         int feet = reader.state(x, y + 1, z);
         int head = reader.state(x, y + 2, z);
         if (feet != -1 && head != -1 && looks.spawnIn[feet] && looks.spawnIn[head] && reader.blockLight(x, y + 1, z) == 0) {
            flags |= reader.skyLight(x, y + 1, z) <= 7 ? 128 : 64;
         }
      }

      return flags;
   }

   private static int light(VoxelReader reader, int x, int y, int z) {
      return reader.skyLight(x, y, z) << 24 | reader.blockLight(x, y, z) << 16;
   }

   private static void voxels(GpuMesher.Mesh mesh, TileBaker helper, VoxelReader reader, int fx0, int fx1, int fy0, int fy1, int fz0, int fz1) {
      int reach = 13;
      double elevation = Math.toRadians(MapStyle.get().sunElevation());
      double flat = 40.0 * Math.cos(elevation);
      int west = Math.max(reach, (int)Math.ceil(flat * 0.878) + 2);
      int south = Math.max(reach, (int)Math.ceil(flat * 0.479) + 2);
      int up = Math.max(reach, (int)Math.ceil(40.0 * Math.sin(elevation)) + 2);
      int sx0 = fx0 - west >> 4;
      int sx1 = fx1 + reach >> 4;
      int sz0 = fz0 - reach >> 4;
      int sz1 = fz1 + south >> 4;
      int sy0 = fy0 - 2 >> 4;
      int sy1 = Math.min(fy1 + up, mesh.rayTop) + 1 >> 4;
      int nx = sx1 - sx0 + 1;
      int ny = sy1 - sy0 + 1;
      int nz = sz1 - sz0 + 1;
      int tableSize = nx * ny * nz;
      int[] out = new int[tableSize + 8192];
      int next = tableSize;

      for (int sy = sy0; sy <= sy1; sy++) {
         for (int sz = sz0; sz <= sz1; sz++) {
            for (int sx = sx0; sx <= sx1; sx++) {
               int index = ((sy - sy0) * nz + (sz - sz0)) * nx + (sx - sx0);
               long[] mask = reader.solidSection(sx, sy, sz);
               if (mask == null) {
                  out[index] = -2;
               } else {
                  if (mesh.lifted) {
                     mask = withoutLid(mask, helper, sx, sy, sz);
                  }

                  if (VoxelReader.nothingSolid(mask)) {
                     out[index] = -1;
                  } else {
                     long[] partial = mesh.lifted ? null : reader.partialSection(sx, sy, sz);
                     boolean marked = partial != null && !VoxelReader.nothingSolid(partial);
                     if (next + 256 > out.length) {
                        out = Arrays.copyOf(out, out.length * 2);
                     }

                     out[index] = marked ? next | -2147483648 : next;

                     for (int w = 0; w < 64; w++) {
                        out[next++] = (int)mask[w];
                        out[next++] = (int)(mask[w] >>> 32);
                     }

                     if (marked) {
                        for (int w = 0; w < 64; w++) {
                           out[next++] = (int)partial[w];
                           out[next++] = (int)(partial[w] >>> 32);
                        }
                     }
                  }
               }
            }
         }
      }

      mesh.voxels = out;
      mesh.voxelCount = next;
      mesh.voxelOrigin[0] = sx0 - (mesh.originX >> 4);
      mesh.voxelOrigin[1] = sy0;
      mesh.voxelOrigin[2] = sz0;
      mesh.voxelSize[0] = nx;
      mesh.voxelSize[1] = ny;
      mesh.voxelSize[2] = nz;
   }

   private static long[] withoutLid(long[] mask, TileBaker helper, int sx, int sy, int sz) {
      long[] out = null;
      int y0 = sy << 4;

      for (int lz = 0; lz < 16; lz++) {
         for (int lx = 0; lx < 16; lx++) {
            for (int ly = Math.max(0, helper.lid((sx << 4) + lx, (sz << 4) + lz) - y0); ly < 16; ly++) {
               int i = ly << 8 | lz << 4 | lx;
               if ((mask[i >> 6] >>> i & 1L) != 0L) {
                  if (out == null) {
                     out = (long[])mask.clone();
                  }

                  out[i >> 6] = out[i >> 6] & ~(1L << i);
               }
            }
         }
      }

      return out == null ? mask : out;
   }

   private static void casters(
      GpuMesher.Mesh mesh, TileBaker helper, VoxelReader reader, BlockLooks looks, MapStyle style, int x0, int x1, double minV, double maxV, int minY
   ) {
      boolean lifted = mesh.lifted;
      double elevation = Math.toRadians(style.sunElevation());
      double flat = 40.0 * Math.cos(elevation);
      int reachX = (int)Math.ceil(flat * 0.878) + 1;
      int reachZ = (int)Math.ceil(flat * 0.479) + 1;
      int reachY = (int)Math.ceil(40.0 * Math.sin(elevation)) + 1;
      int cx0 = x0 - reachX;
      int cx1 = x1 + 2;
      int bottom = mesh.lowestY == Integer.MAX_VALUE ? Math.max(minY, mesh.topY - 1) : Math.max(minY, mesh.lowestY);
      int cz0 = zLow(minV, bottom, mesh.sin, mesh.cos) - 1;
      int cz1 = zHigh(maxV, mesh.topY, mesh.sin, mesh.cos) + reachZ;
      int highest = Integer.MIN_VALUE;

      for (int x = cx0; x <= cx1 && mesh.lowestY != Integer.MAX_VALUE; x++) {
         for (int z = cz0; z <= cz1; z++) {
            ChunkSnapshot chunk = reader.snapshot(x, z);
            if (chunk != null) {
               int top = Math.min(chunk.surface(x & 15, z & 15) + 1, mesh.topY + reachY);
               if (lifted) {
                  top = Math.min(top, helper.lid(x, z) - 1);
               }

               long[] mayCast = null;
               int castY = Integer.MIN_VALUE;

               for (int y = top; y >= bottom; y--) {
                  if (!lifted) {
                     if (y >> 4 != castY) {
                        castY = y >> 4;
                        mayCast = reader.casterSection(x >> 4, castY, z >> 4);
                     }

                     int i = (y & 15) << 8 | (z & 15) << 4 | x & 15;
                     if (mayCast != null && (mayCast[i >> 6] >>> i & 1L) == 0L) {
                        continue;
                     }
                  }

                  int solid = reader.solid(x, y, z);
                  if (solid == 2) {
                     int id = reader.state(x, y, z);
                     int shape = id < 0 ? 0 : looks.shape[id];
                     if (shape != 0) {
                        int[] boxes = looks.faces(shape).boxes;

                        for (int b = 0; b < boxes.length; b += 2) {
                           mesh.casters.add(x, y, z, 0, 0, 0, 0, 0, boxes[b], boxes[b + 1]);
                           mesh.casters.add(x, y, z, 2, 0, 0, 0, 0, boxes[b], boxes[b + 1]);
                           mesh.casters.add(x, y, z, 1, 0, 0, 0, 0, boxes[b], boxes[b + 1]);
                        }

                        highest = Math.max(highest, y + 1);
                     }
                  } else if (solid == 1) {
                     if (reader.solid(x, y + 1, z) != 1 || lifted && y + 1 >= helper.lid(x, z)) {
                        mesh.casters.add(x, y, z, 0, 0, 0, 0, 0);
                        highest = Math.max(highest, y + 1);
                     }

                     if (reader.solid(x - 1, y, z) != 1 || lifted && y >= helper.lid(x - 1, z)) {
                        mesh.casters.add(x, y, z, 2, 0, 0, 0, 0);
                     }

                     if (reader.solid(x, y, z + 1) != 1 || lifted && y >= helper.lid(x, z + 1)) {
                        mesh.casters.add(x, y, z, 1, 0, 0, 0, 0);
                     }
                  }
               }
            }
         }
      }

      mesh.casterX0 = cx0;
      mesh.casterX1 = cx1 + 1;
      mesh.casterZ0 = cz0;
      mesh.casterZ1 = cz1 + 1;
      mesh.casterY0 = bottom;
      mesh.casterY1 = Math.max(highest, mesh.topY);
   }

   private static void sortNearToFar(GpuMesher.Quads quads, double sin, double cos) {
      int n = quads.count;
      if (n >= 2) {
         long[] order = new long[n];

         for (int q = 0; q < n; q++) {
            int i = q * 14;
            int lo = quads.get(i + 8);
            int hi = quads.get(i + 9);
            double y0 = (lo >> 8 & 0xFF) / 128.0;
            double z0 = (lo >> 16 & 0xFF) / 128.0;
            double y1 = (hi >> 8 & 0xFF) / 128.0;
            double z1 = (hi >> 16 & 0xFF) / 128.0;
            double y = quads.get(i + 1);
            double z = quads.get(i + 2);
            if (quads.get(i + 3) == 0) {
               y += y1;
               z += (z0 + z1) / 2.0;
            } else {
               y += (y0 + y1) / 2.0;
               z += z1;
            }

            double depth = -(sin * y + cos * z);
            long d = (long)(depth * 1024.0);
            order[q] = d << 24 | q;
         }

         Arrays.sort(order);
         int[] faces = new int[n];

         for (int q = 0; q < n; q++) {
            faces[q] = (int)(order[q] & 16777215L);
         }

         quads.reorder(faces);
      }
   }

   private static int solidColor(TileBaker helper, int id, int x, int y, int z, boolean top, int emission, byte kind, MapStyle style) {
      helper.albedo(id, x, z, top);
      float r = helper.albedoR;
      float g = helper.albedoG;
      float b = helper.albedoB;
      float variation = style.blockVariation();
      if (variation > 0.0F) {
         float nudge = 1.0F + variation * TileBaker.blockNoise(x, y, z);
         r *= nudge;
         g *= nudge;
         b *= nudge;
      }

      if (kind == 3) {
         float crust = 1.0F + (helper.lifted ? 0.1F : 0.3F) * TileBaker.blockNoise(x, y, z);
         r *= crust;
         g *= crust * crust;
         b *= crust;
      }

      return encode(r, g, b, emission * 17);
   }

   private static int encode(float r, float g, float b, int alpha) {
      return alpha << 24 | srgb(r * 0.5F) << 16 | srgb(g * 0.5F) << 8 | srgb(b * 0.5F);
   }

   private static int srgb(float linear) {
      double v = Math.max(0.0F, Math.min(1.0F, linear));
      double s = v <= 0.0031308 ? v * 12.92 : 1.055 * Math.pow(v, 0.4166666666666667) - 0.055;
      return (int)Math.round(s * 255.0);
   }

   private static int pack(float a, float b, float c, float d) {
      return byte4(a) << 24 | byte4(b) << 16 | byte4(c) << 8 | byte4(d);
   }

   private static int byte4(float v) {
      return Math.round(Math.max(0.0F, Math.min(1.0F, v)) * 255.0F);
   }

   private static int aoTop(TileBaker h, int x, int y, int z) {
      boolean n = h.occludes(x, y, z - 1);
      boolean s = h.occludes(x, y, z + 1);
      boolean w = h.occludes(x - 1, y, z);
      boolean e = h.occludes(x + 1, y, z);
      return pack(
         TileBaker.corner(w, n, h.occludes(x - 1, y, z - 1)),
         TileBaker.corner(e, n, h.occludes(x + 1, y, z - 1)),
         TileBaker.corner(w, s, h.occludes(x - 1, y, z + 1)),
         TileBaker.corner(e, s, h.occludes(x + 1, y, z + 1))
      );
   }

   private static int aoSouth(TileBaker h, int x, int y, int z) {
      boolean up = h.occludes(x, y + 1, z);
      boolean down = h.occludes(x, y - 1, z);
      boolean w = h.occludes(x - 1, y, z);
      boolean e = h.occludes(x + 1, y, z);
      return pack(
         TileBaker.corner(w, up, h.occludes(x - 1, y + 1, z)),
         TileBaker.corner(e, up, h.occludes(x + 1, y + 1, z)),
         TileBaker.corner(w, down, h.occludes(x - 1, y - 1, z)),
         TileBaker.corner(e, down, h.occludes(x + 1, y - 1, z))
      );
   }

   public static final class Mesh {
      public final TileKey key;
      public final int topY;
      public final int minY;
      public final double sin;
      public final double cos;
      public final boolean lit;
      public boolean lifted;
      public boolean depthLook;
      public int roof;
      public final boolean cave;
      public final boolean empty;
      public boolean traceInstead;
      public final GpuMesher.Quads opaque;
      public final GpuMesher.Quads water;
      public final GpuMesher.Quads tinted;
      public final GpuMesher.Quads casters;
      public int lowestY = Integer.MAX_VALUE;
      public final int originX;
      public int originZ;
      public int casterX0;
      public int casterX1;
      public int casterZ0;
      public int casterZ1;
      public int casterY0;
      public int casterY1;
      public int[] voxels;
      public int voxelCount;
      public final int[] voxelOrigin = new int[3];
      public final int[] voxelSize = new int[3];
      public int rayTop;
      public long buildNanos;
      public @Nullable Overlays overlays;
      private final Map<Long, Boolean> slimeChunks = new HashMap<>();

      Mesh(TileKey key, int topY, int minY, double sin, double cos, boolean lit, boolean cave, boolean empty) {
         this.opaque = new GpuMesher.Quads();
         this.water = new GpuMesher.Quads();
         this.tinted = new GpuMesher.Quads();
         this.casters = new GpuMesher.Quads();
         this.key = key;
         this.topY = topY;
         this.minY = minY;
         this.sin = sin;
         this.cos = cos;
         this.lit = lit;
         this.cave = cave;
         this.empty = empty;
         this.originX = (int)Math.floor(key.minU());
      }

      public void freeFaces() {
         this.opaque.free();
         this.water.free();
         this.tinted.free();
         this.casters.free();
      }
   }

   public static final class Quads {
      private static final int FIRST_INTS = 57344;
      private static final Cleaner CLEANER = Cleaner.create();
      private final GpuMesher.Quads.Memory memory = new GpuMesher.Quads.Memory();
      private final Cleanable cleanable;
      public int count;

      Quads() {
         this.memory.data = MemoryUtil.memAllocInt(57344);
         this.cleanable = CLEANER.register(this, this.memory);
      }

      public int get(int i) {
         return this.memory.data.get(i);
      }

      void free() {
         this.cleanable.clean();
      }

      void reorder(int[] faces) {
         IntBuffer from = this.memory.data;
         IntBuffer to = MemoryUtil.memAllocInt(from.capacity());
         long src = MemoryUtil.memAddress(from);
         long dst = MemoryUtil.memAddress(to);

         for (int q = 0; q < faces.length; q++) {
            MemoryUtil.memCopy(src + faces[q] * 14L * 4L, dst + q * 14L * 4L, 56L);
         }

         this.memory.data = to;
         MemoryUtil.memFree(from);
      }

      void add(int x, int y, int z, int face, int color, int ao, int light, int flags) {
         this.add(x, y, z, face, color, ao, light, flags, 0, ShapeFaces.FULL_HI);
      }

      void add(int x, int y, int z, int face, int color, int ao, int light, int flags, int lo, int hi) {
         int i = this.count * 14;
         IntBuffer data = this.memory.data;
         if (i + 14 > data.capacity()) {
            data = MemoryUtil.memRealloc(data, data.capacity() * 2);
            this.memory.data = data;
         }

         data.put(i, x);
         data.put(i + 1, y);
         data.put(i + 2, z);
         data.put(i + 3, face);
         data.put(i + 4, color);
         data.put(i + 5, ao);
         data.put(i + 6, light);
         data.put(i + 7, flags);
         data.put(i + 8, lo);
         data.put(i + 9, hi);
         data.put(i + 10, 0);
         data.put(i + 11, 0);
         data.put(i + 12, 0);
         data.put(i + 13, 0);
         this.count++;
      }

      void haze(int a, int b, int c, int d) {
         int i = (this.count - 1) * 14;
         IntBuffer data = this.memory.data;
         data.put(i + 10, a);
         data.put(i + 11, b);
         data.put(i + 12, c);
         data.put(i + 13, d);
      }

      private static final class Memory implements Runnable {
         @Nullable IntBuffer data;

         @Override
         public void run() {
            if (this.data != null) {
               MemoryUtil.memFree(this.data);
               this.data = null;
            }
         }
      }
   }
}
