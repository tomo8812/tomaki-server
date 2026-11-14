package dev.diorama;

import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.Overlays;
import dev.diorama.compat.SubLevels;
import dev.diorama.craft.Crafts;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.data.Copycats;
import dev.diorama.light.Lights;
import dev.diorama.render.MapLevel;
import dev.diorama.render.SavedChunkImport;
import dev.diorama.share.ShareClient;
import dev.diorama.waypoint.Portals;
import dev.diorama.waypoint.WaypointStore;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

public final class MapSession {
   private static final long LOAD_DELAY_MS = 750L;
   private static final long CHANGE_DELAY_MS = 250L;
   private static final int SNAPSHOTS_PER_TICK = 24;
   private static final int MIN_CAVE_DEPTH = 8;
   private static final int MAX_SKY_DEPTH = 32;
   private static @Nullable MapSession current;
   private static int started;
   private static final boolean DEBUG_CHANGES = Diorama.DEBUG;
   private static final Map<String, Integer> CHANGE_TALLY = new TreeMap<>();
   private static long lastTallyLog;
   private final ClientLevel level;
   private final MapLevel map;
   private final boolean mainMap;
   private @Nullable SavedChunkImport savedChunks;
   private final Crafts crafts;
   private final Queue<Portals.Spot> savedPortals = new ConcurrentLinkedQueue<>();
   private final Lights lights;
   private final Map<Long, MapSession.Pending> pending = new LinkedHashMap<>();
   private boolean underground;
   private int caveLayer = Integer.MIN_VALUE;
   private MapStyle style = MapStyle.get();
   private int ticks;
   private @Nullable Boolean relight;
   private @Nullable BlockPos lastSightOrigin;
   private int sightCooldown;
   private @Nullable CaveSight sight;
   private static final Direction[] DIRECTIONS = Direction.values();

   private MapSession(ClientLevel level, @Nullable String key) {
      this.level = level;
      int roof = level.dimensionType().hasCeiling() ? level.getMinBuildHeight() + level.dimensionType().logicalHeight() : Integer.MIN_VALUE;
      Projection.setFacing(key == null ? Facing.NORTH : Maps.of(key).facing());
      Path folder = mapFolder(level, key);
      this.map = new MapLevel(level.getMinBuildHeight(), level.getMaxBuildHeight() - 1 + 1, roof, folder);
      this.mainMap = key != null && Maps.of(key).active(level.dimension().location().toString()).isEmpty();
      this.crafts = new Crafts(level, this.map, folder, this::lookAgain);
      this.lights = new Lights(level, this.map);
   }

   private void lookAgain(int[] box) {
      int[] change = new int[]{box[0] - 1, box[1] - 1, box[2] - 1, box[3] + 1, box[4] + 1, box[5] + 1, 1};

      for (int cx = change[0] >> 4; cx <= change[3] >> 4; cx++) {
         for (int cz = change[2] >> 4; cz <= change[5] >> 4; cz++) {
            this.schedule(cx, cz, 250L, change);
         }
      }
   }

   private void end() {
      if (this.savedChunks != null) {
         this.savedChunks.stop();
      }

      this.lights.close();
      this.crafts.close(this.ticks);
      this.map.close();
   }

   private static @Nullable Path mapFolder(ClientLevel level, @Nullable String key) {
      if (key == null) {
         return null;
      }

      Maps maps = Maps.of(key);
      String dimension = level.dimension().location().toString();
      return maps.tileFolder(dimension, maps.active(dimension));
   }

   public static void mapChanged(String dimension) {
      if (current != null && current.level.dimension().location().toString().equals(dimension)) {
         current.end();
         current = null;
      }
   }

   public static void facingChanged() {
      if (current != null) {
         current.map.colorsChanged();
      }
   }

   public static @Nullable MapSession get() {
      return current;
   }

   public static int started() {
      return started;
   }

   public @Nullable Long slimeSeed() {
      IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
      return server != null && this.level.dimension() == Level.OVERWORLD ? server.overworld().getSeed() : null;
   }

   public static void close() {
      if (current != null) {
         current.end();
         current = null;
      }
   }

   public static void frame(boolean baked) {
      MapSession session = current;
      if (session != null && !baked && session.level == mc().level) {
         session.map.takeFinished(1);
      }
   }

   public static void tick(Minecraft mc) {
      MapSession session = forLevel(mc.level);
      if (session != null) {
         if (BlockLooks.get() == null) {
            BlockLooks.build();
         } else if (session.style != MapStyle.get()) {
            boolean rebake = !MapStyle.get().looksLike(session.style);
            session.style = MapStyle.get();
            if (rebake) {
               session.map.colorsChanged();
            }
         } else if (BlockLooks.isStale() && mc.getOverlay() == null) {
            BlockLooks.build();
            session.map.colorsChanged();
            session.resnapshotLoaded();
         }

         session.tickSession(mc);
      }
   }

   private static @Nullable MapSession forLevel(@Nullable ClientLevel level) {
      if (current != null && current.level != level) {
         current.end();
         current = null;
      }

      if (level != null && ServerRules.mapOff(level.dimension().location().toString())) {
         if (current != null) {
            current.end();
            current = null;
         }

         return null;
      } else {
         if (level != null && current == null) {
            String key = WorldKey.current(level);
            if (key == null && WorldKey.waiting(level)) {
               return null;
            }

            current = new MapSession(level, key);
            started++;
         }

         return current;
      }
   }

   public static @Nullable Entity viewer() {
      Minecraft mc = mc();
      Entity camera = mc.getCameraEntity();
      return (Entity)(camera != null ? camera : mc.player);
   }

   public static void chunkLoaded(ClientLevel level, LevelChunk chunk) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.level() == level) {
         MapSession session = forLevel(level);
         if (session != null) {
            int cx = chunk.getPos().x;
            int cz = chunk.getPos().z;
            session.schedule(cx, cz, 750L, null);
            int reach = 12;

            for (int dx = -1; dx <= 1; dx++) {
               for (int dz = -1; dz <= 1; dz++) {
                  ChunkSnapshot around = session.map.worldChunk(cx + dx, cz + dz);
                  if ((dx != 0 || dz != 0) && around != null && !around.hasBiomesAround(-dx, -dz)) {
                     int x0 = around.chunkX << 4;
                     int z0 = around.chunkZ << 4;
                     int[] strip = new int[]{
                        dx < 0 ? x0 + 16 - reach : x0,
                        level.getMinBuildHeight(),
                        dz < 0 ? z0 + 16 - reach : z0,
                        dx > 0 ? x0 + reach - 1 : x0 + 15,
                        level.getMaxBuildHeight() - 1,
                        dz > 0 ? z0 + reach - 1 : z0 + 15,
                        1
                     };
                     session.schedule(around.chunkX, around.chunkZ, 250L, strip);
                  }
               }
            }
         }
      }
   }

   private static boolean sameFromEverySide(BlockLooks looks, BlockState oldState, BlockState newState) {
      for (Facing facing : Facing.values()) {
         int oldId = Block.BLOCK_STATE_REGISTRY.getId(facing.turned(oldState));
         int newId = Block.BLOCK_STATE_REGISTRY.getId(facing.turned(newState));
         if (oldId < 0 || newId < 0 || !looks.sameLook(oldId, newId)) {
            return false;
         }
      }

      return true;
   }

   public static void blockChanged(ClientLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
      if (current != null && current.level == level) {
         BlockLooks looks = BlockLooks.get();
         if (looks == null || !sameFromEverySide(looks, oldState, newState)) {
            if (DEBUG_CHANGES) {
               CHANGE_TALLY.merge(oldState.getBlock() + " -> " + newState.getBlock(), 1, Integer::sum);
               long now = System.currentTimeMillis();
               if (now - lastTallyLog > 10000L) {
                  lastTallyLog = now;
                  Diorama.LOGGER.info("Visible block changes so far: {}", CHANGE_TALLY);
               }
            }

            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            if (SubLevels.holds(level, cx, cz)) {
               SubLevels.edited(cx, cz);
            } else {
               int surface = current.visibleFromSky(pos) ? 1 : 0;
               int[] box = new int[]{pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ(), surface};
               current.schedule(cx, cz, 250L, box);
               boolean light = oldState.getLightEmission() != newState.getLightEmission();
               int lx = pos.getX() & 15;
               int lz = pos.getZ() & 15;

               for (int dx = -1; dx <= 1; dx++) {
                  for (int dz = -1; dz <= 1; dz++) {
                     boolean touches = (dx == 0 || dx == -1 && lx == 0 || dx == 1 && lx == 15) && (dz == 0 || dz == -1 && lz == 0 || dz == 1 && lz == 15);
                     if ((dx != 0 || dz != 0) && (light || touches)) {
                        current.schedule(cx + dx, cz + dz, 250L, box);
                     }
                  }
               }
            }
         }
      }
   }

   public static void blockEntityChanged(ClientLevel level, BlockPos pos) {
      if (Copycats.INSTALLED && current != null && current.level == level) {
         if (SubLevels.holds(level, pos.getX() >> 4, pos.getZ() >> 4)) {
            SubLevels.edited(pos.getX() >> 4, pos.getZ() >> 4);
         } else {
            ChunkSnapshot snapshot = current.map.worldChunk(pos.getX() >> 4, pos.getZ() >> 4);
            if (snapshot != null && Copycats.material(level.getBlockEntity(pos)) != snapshot.copycatMaterial(pos.getX(), pos.getY(), pos.getZ())) {
               int surface = current.visibleFromSky(pos) ? 1 : 0;
               current.schedule(
                  pos.getX() >> 4, pos.getZ() >> 4, 250L, new int[]{pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ(), surface}
               );
            }
         }
      }
   }

   public static void lightSectionChanged(ClientChunkCache source, SectionPos section, boolean blockLight) {
      if (current != null && current.level.getChunkSource() == source) {
         int x0 = section.minBlockX();
         int y0 = section.minBlockY();
         int z0 = section.minBlockZ();
         int surface = !current.map.spawnSpots() && !current.map.roofless() ? 0 : 2;
         if (blockLight && current.keepsBlockLight() && current.reachesSurface(section.x(), section.z(), y0 + 15)) {
            surface |= 4;
         }

         current.schedule(section.x(), section.z(), 250L, new int[]{x0, y0, z0, x0 + 15, y0 + 15, z0 + 15, surface});
      }
   }

   public static void lightUpdated(ClientLevel level, int chunkX, int chunkZ, BitSet blockLight) {
      if (current != null && current.level == level) {
         int x0 = chunkX << 4;
         int z0 = chunkZ << 4;
         int surface = !current.map.spawnSpots() && !current.map.roofless() ? 0 : 2;
         int highest = blockLight.length() - 1;
         if (highest >= 0 && current.keepsBlockLight() && current.reachesSurface(chunkX, chunkZ, (level.getMinSection() - 1 + highest << 4) + 15)) {
            surface |= 4;
         }

         current.schedule(chunkX, chunkZ, 250L, new int[]{x0, level.getMinBuildHeight(), z0, x0 + 15, level.getMaxBuildHeight() - 1, z0 + 15, surface});
      }
   }

   private boolean reachesSurface(int chunkX, int chunkZ, int top) {
      ChunkSnapshot chunk = this.map.worldChunk(chunkX, chunkZ);
      return chunk == null || top >= chunk.floorY;
   }

   private boolean keepsBlockLight() {
      return !this.map.roofless() && !this.level.dimensionType().hasFixedTime();
   }

   private boolean visibleFromSky(BlockPos pos) {
      if (!this.level.dimensionType().hasSkyLight()) {
         return true;
      }

      MutableBlockPos p = new MutableBlockPos();
      if (this.level.getBrightness(LightLayer.SKY, pos) > 0) {
         return true;
      }

      for (Direction d : DIRECTIONS) {
         if (this.level.getBrightness(LightLayer.SKY, p.setWithOffset(pos, d)) > 0) {
            return true;
         }
      }

      return false;
   }

   private void schedule(int chunkX, int chunkZ, long delayMs, int @Nullable [] box) {
      if (SubLevels.holds(this.level, chunkX, chunkZ)) {
         if (box == null) {
            SubLevels.edited(chunkX, chunkZ);
         }
      } else {
         long key = ChunkSnapshot.key(chunkX, chunkZ);
         long due = System.currentTimeMillis() + delayMs;
         MapSession.Pending p = this.pending.get(key);
         if (p == null) {
            this.pending.put(key, new MapSession.Pending(due, box == null ? null : (int[])box.clone()));
         } else {
            p.due = Math.min(p.due, due);
            if (p.box != null && box != null) {
               for (int i = 0; i < 3; i++) {
                  p.box[i] = Math.min(p.box[i], box[i]);
                  p.box[i + 3] = Math.max(p.box[i + 3], box[i + 3]);
               }

               p.box[6] = p.box[6] | box[6];
            } else {
               p.box = null;
            }
         }
      }
   }

   private void sweepLoadedChunks(Entity viewer) {
      int radius = (Integer)mc().options.renderDistance().get() + 2;
      int pcx = viewer.blockPosition().getX() >> 4;
      int pcz = viewer.blockPosition().getZ() >> 4;

      for (int cx = pcx - radius; cx <= pcx + radius; cx++) {
         for (int cz = pcz - radius; cz <= pcz + radius; cz++) {
            if (this.map.worldChunk(cx, cz) == null
               && !this.pending.containsKey(ChunkSnapshot.key(cx, cz))
               && this.level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false) != null) {
               this.schedule(cx, cz, 0L, null);
            }
         }
      }
   }

   private void resnapshotLoaded() {
      Entity viewer = viewer();
      if (viewer != null) {
         int radius = (Integer)mc().options.renderDistance().get() + 2;
         int pcx = viewer.blockPosition().getX() >> 4;
         int pcz = viewer.blockPosition().getZ() >> 4;

         for (int cx = pcx - radius; cx <= pcx + radius; cx++) {
            for (int cz = pcz - radius; cz <= pcz + radius; cz++) {
               if (this.level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false) != null) {
                  this.schedule(cx, cz, 0L, null);
               }
            }
         }
      }
   }

   private static Minecraft mc() {
      return Minecraft.getInstance();
   }

   private void updateSavedChunks(Minecraft mc) {
      boolean fill = Settings.get().mapSavedChunks && this.mainMap;
      boolean singleplayer = mc.getSingleplayerServer() != null;
      if (this.relight == null) {
         this.relight = singleplayer && SavedChunkImport.needsRelight(this.map, this.level);
      }

      if (this.relight && !fill && this.map.memory() == null && this.savedChunks != null && this.savedChunks.idle()) {
         this.relight = false;
      }

      boolean on = singleplayer ? fill || this.map.memory() != null || this.relight : fill && ShareClient.sharing();
      if (on && this.savedChunks == null) {
         this.savedChunks = singleplayer
            ? SavedChunkImport.start(this.map, this.level, this.savedPortals::addAll)
            : SavedChunkImport.startShared(this.map, this.level, this.savedPortals::addAll);
         if (this.savedChunks == null) {
            return;
         }
      } else if (!on && this.savedChunks != null) {
         this.savedChunks.stop();
         this.savedChunks = null;
      }

      if (this.savedChunks != null) {
         this.savedChunks.onlyRemembered(!fill);
      }
   }

   public @Nullable SavedChunkImport savedChunks() {
      return this.savedChunks;
   }

   private void tickSession(Minecraft mc) {
      this.updateSavedChunks(mc);
      Entity viewer = viewer();
      if (viewer != null && this.ticks++ % 40 == 0) {
         this.sweepLoadedChunks(viewer);
      }

      if (this.ticks % 600 == 0) {
         this.map.saveSeen();
      }

      int playerChunkX = viewer == null ? 0 : viewer.blockPosition().getX() >> 4;
      int playerChunkZ = viewer == null ? 0 : viewer.blockPosition().getZ() >> 4;
      long now = System.currentTimeMillis();
      boolean fog = ServerRules.fogOfWar(this.level.dimension() == Level.NETHER, this.map.roofless());
      String dimension = this.level.dimension().location().toString();
      WaypointStore pins = fog ? null : WaypointStore.get();
      List<Portals.Spot> saved = new ArrayList<>();

      Portals.Spot spot;
      while ((spot = this.savedPortals.poll()) != null) {
         saved.add(spot);
      }

      if (pins != null) {
         Portals.add(saved, dimension, pins, this.map.roof());
      }

      int budget = 24;
      Iterator<Entry<Long, MapSession.Pending>> it = this.pending.entrySet().iterator();

      while (it.hasNext() && budget > 0) {
         Entry<Long, MapSession.Pending> entry = it.next();
         MapSession.Pending p = entry.getValue();
         if (p.due <= now) {
            it.remove();
            long key = entry.getKey();
            int cx = (int)(key >> 32);
            int cz = (int)key;
            if (this.level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false) instanceof LevelChunk chunk) {
               boolean known = this.map.worldChunk(cx, cz) != null;
               ChunkSnapshot snapshot = ChunkSnapshot.take(chunk, this.level);
               this.map.put(snapshot, known ? p.box : null, playerChunkX, playerChunkZ);
               if (pins != null) {
                  Portals.chunkSeen(snapshot, dimension, pins, this.map.world());
               }

               budget--;
            }
         }
      }

      if (Diorama.DEBUG && fog != this.map.fogOfWar()) {
         Diorama.LOGGER.info("Fog of war {}; underground={}", fog, this.underground);
      }

      this.map.setFogOfWar(fog);
      Settings settings = Settings.get();
      this.map.setDepthCaves(settings.legibleCaves);
      boolean spawnSpots = settings.spawnSpots != Settings.SpawnSpots.OFF;
      boolean overlays = settings.chunkGrid || settings.slimeChunks || spawnSpots;
      this.map.setOverlays(overlays ? new Overlays(this.slimeSeed(), spawnSpots) : null);
      this.map.update();
      this.crafts.tick(viewer, this.ticks);
      if (viewer != null) {
         this.updateUnderground(viewer);
         if (!this.underground) {
            this.caveLayer = Integer.MIN_VALUE;
         } else {
            this.updateCaveLayer(viewer);
            this.updateSight(viewer);
         }
      }
   }

   private void updateSight(Entity viewer) {
      BlockLooks looks = BlockLooks.get();
      BlockPos eye = BlockPos.containing(viewer.getEyePosition());
      if (looks != null && (!eye.equals(this.lastSightOrigin) || this.sightCooldown-- <= 0)) {
         this.lastSightOrigin = eye;
         this.sightCooldown = 40;
         if (this.sight == null) {
            this.sight = new CaveSight(this.level);
         }

         int[] box = this.sight.spread(looks, viewer.getEyePosition(), this.map.seen());
         if (Diorama.DEBUG) {
            Diorama.LOGGER.info("Sight from {}: visited {}, newly seen box {}", new Object[]{eye, this.sight.visited(), Arrays.toString(box)});
         }

         if (box[0] != Integer.MAX_VALUE) {
            this.map.revealed(box[0], box[1], box[2], box[3], box[4], box[5]);
         }
      }
   }

   private void updateUnderground(Entity viewer) {
      if (this.level.dimensionType().hasCeiling()) {
         this.underground = true;
      } else {
         BlockPos eye = BlockPos.containing(viewer.getEyePosition());
         int sky = this.level.getBrightness(LightLayer.SKY, eye);
         boolean covered = !this.level.canSeeSky(eye);
         if (this.underground) {
            this.underground = covered && sky < 10;
         } else {
            this.underground = covered && sky < 5;
         }
      }
   }

   public MapLevel map() {
      return this.map;
   }

   public Crafts crafts() {
      return this.crafts;
   }

   public Lights lights() {
      return this.lights;
   }

   public boolean isUnderground() {
      return this.underground;
   }

   public static int caveLayerFor(double feetY) {
      return Math.floorDiv(Mth.floor(feetY) + 7, 4) * 4;
   }

   public int autoLayer(Entity viewer) {
      if (!this.underground) {
         return Integer.MIN_VALUE;
      } else {
         return Settings.get().preferSurface && this.depthToSky(BlockPos.containing(viewer.getEyePosition())) < 8 ? Integer.MIN_VALUE : this.caveLayer(viewer);
      }
   }

   public int caveLayer(Entity viewer) {
      return this.caveLayer == Integer.MIN_VALUE ? caveLayerFor(viewer.getY()) : this.caveLayer;
   }

   private void updateCaveLayer(Entity viewer) {
      int wanted = caveLayerFor(viewer.getY());
      if (this.caveLayer == Integer.MIN_VALUE || wanted < this.caveLayer || this.caveLayer <= Mth.floor(viewer.getY()) + 2) {
         this.caveLayer = wanted;
      }
   }

   private int depthToSky(BlockPos eye) {
      MutableBlockPos pos = new MutableBlockPos();
      int top = Math.min(this.level.getMaxBuildHeight() - 1, eye.getY() + 32);

      for (int y = eye.getY(); y <= top; y++) {
         if (this.level.getBrightness(LightLayer.SKY, pos.set(eye.getX(), y, eye.getZ())) >= 15) {
            return y - eye.getY();
         }
      }

      return 33;
   }

   private static final class Pending {
      long due;
      int @Nullable [] box;

      Pending(long due, int @Nullable [] box) {
         this.due = due;
         this.box = box;
      }
   }
}
