package org.millenaire.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.millenaire.config.MillenaireServerConfig;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.millenaire.village.Village;
import org.millenaire.village.VillageManager;
import org.millenaire.village.VillageSavedData;
import org.millenaire.world.sitescore.ArtificialScan;
import org.millenaire.world.sitescore.ChunkTerrainSummary;
import org.millenaire.world.sitescore.CoreSpread;
import org.millenaire.world.sitescore.EdgeWater;
import org.millenaire.world.sitescore.SevereRelief;
import org.millenaire.world.sitescore.SiteChunkData;
import org.millenaire.world.sitescore.SiteMeasures;
import org.millenaire.world.sitescore.SiteScoreLog;
import org.millenaire.world.sitescore.SiteSurvey;
import org.slf4j.Logger;

public final class SiteHunter {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final boolean ENABLED = !"false".equalsIgnoreCase(System.getProperty("millenaire.siteHunter", "true")) && !SiteSurvey.ENABLED;
   static final int FULL_RADIUS = 80;
   static final int SMALL_RADIUS = 70;
   static final int SMALL_MAX_TYPE_RADIUS = 70;
   static final int LOOK = 128;
   static final int FULL_TYPE_RADIUS = 90;
   static final int MAX_TRIALS = 3;
   static final int LOOKS_PER_TICK = 64;
   static final float MIN_BAND = floatProp("millenaire.siteBand", 0.7F);
   static final int MAX_CORE_SPREAD = Integer.getInteger("millenaire.siteCoreSpread", 10);
   static final boolean CHAT = Boolean.getBoolean("millenaire.siteChat");
   static final boolean LOG = Boolean.getBoolean("millenaire.siteHunterLog");
   static final float GOOD_ENOUGH = floatProp("millenaire.siteGoodEnough", 1.25F);
   static final int STATUS_INTERVAL = 1200;
   static final int EVICT_DISTANCE = 512;
   static final int SAMPLE_KEEP_DISTANCE = 733;
   static final int EVICT_INTERVAL = 1200;
   static final String LOG_FILE = "hunter.csv";
   static final String LOG_HEADER = "tick,event,size,chunk_x,chunk_z,x,z,type,full_score,small_score,trial,player_distance,player_behind,detail";
   static final String ARTIFICIAL_FILE = "hunter_artificial_v3.csv";
   static final String ARTIFICIAL_HEADER = "tick,chunk_x,chunk_z,x,z,sample_hits,block_entities,structures,avoided,village,blocks,block_entity_types";
   static final String STATUS_FILE = "hunter_status.csv";
   static final String STATUS_HEADER = "tick,open,not_scored,waiting_unexplored,waiting_better,waiting_full_first,ready_waiting_for_load,resolved,spawned";
   private final Long2ObjectOpenHashMap<ChunkTerrainSummary> samples = new Long2ObjectOpenHashMap();
   private final Long2ObjectOpenHashMap<List<BoundingBox>> pieces = new Long2ObjectOpenHashMap();
   private final Long2ObjectOpenHashMap<SiteHunter.Candidate> candidates = new Long2ObjectOpenHashMap();
   private final LongArrayFIFOQueue work = new LongArrayFIFOQueue();
   private int minVillage = 300;
   private long seed;
   private int spawnedThisSession;
   static final int[] SPOT_OFFSETS = new int[]{-8, -4, 0, 4, 8};
   static final int HINT_VARIANTS = 4;

   static int requiredDistance(int radiusA, int radiusB, int minVillageDistance) {
      return radiusA + radiusB + Math.max(0, minVillageDistance - 180);
   }

   static int sizeRadius(SiteHunter.Size size) {
      return size == SiteHunter.Size.FULL ? 90 : 70;
   }

   private static int minVillageDistance() {
      return SiteScoreLog.minVillageDistance(MillenaireServerConfig.SERVER.minVillageDistance.getAsInt());
   }

   private static float floatProp(String name, float fallback) {
      try {
         return Float.parseFloat(System.getProperty(name, String.valueOf(fallback)));
      } catch (NumberFormatException e) {
         return fallback;
      }
   }

   void setSeed(long seed) {
      this.seed = seed;
   }

   public void onChunkLoad(ServerLevel level, ChunkPos pos) {
      LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
      if (chunk != null) {
         long key = pos.toLong();
         SiteChunkData d = (SiteChunkData)chunk.getExistingDataOrNull((AttachmentType)ModAttachments.SITE_CHUNK.get());
         if (d == null || !d.hasSamples()) {
            ChunkTerrainSummary s = ChunkTerrainSummary.compute(level, pos.x, pos.z);
            List<BoundingBox> p = StructureAvoidance.avoidedPieces(level, pos);
            ArtificialScan scan = ArtificialScan.compute(level, chunk, s.heights());
            d = SiteChunkData.of(s, p, d == null ? 0 : d.state(), scan);
            chunk.setData((AttachmentType)ModAttachments.SITE_CHUNK.get(), d);
            chunk.setUnsaved(true);
            logArtificial(level, chunk, s.heights(), scan, !p.isEmpty());
         } else if (!d.hasArtificialScan()) {
            ArtificialScan scan = ArtificialScan.compute(level, chunk, d.heights());
            d = d.withArtificial(scan);
            chunk.setData((AttachmentType)ModAttachments.SITE_CHUNK.get(), d);
            chunk.setUnsaved(true);
            logArtificial(level, chunk, d.heights(), scan, !d.pieces().isEmpty());
         }

         this.putSamples(key, d.summary(pos.x, pos.z), d.pieces());
         if (d.state() == 1 && !this.candidates.containsKey(key)) {
            this.track(key);
         }
      }
   }

   public String sitesStatus() {
      int open = 0;
      int notScored = 0;
      int passing = 0;
      int load = 0;
      int spawned = this.spawnedThisSession;
      ObjectIterator var6 = this.candidates.values().iterator();

      while (var6.hasNext()) {
         SiteHunter.Candidate c = (SiteHunter.Candidate)var6.next();
         if (!c.resolved) {
            open++;
            if (!c.scored) {
               notScored++;
            } else if (c.openFull() || c.openSmall()) {
               passing++;
            }

            if (c.waitingForLoad) {
               load++;
            }
         }
      }

      return "candidates open "
         + open
         + " ("
         + notScored
         + " not judged yet, "
         + passing
         + " passing, "
         + load
         + " decided and waiting for their area to load); villages spawned this session "
         + spawned;
   }

   public List<String> sitesNear(BlockPos from, int max) {
      List<SiteHunter.Candidate> open = new ArrayList<>();
      ObjectIterator out = this.candidates.values().iterator();

      while (out.hasNext()) {
         SiteHunter.Candidate c = (SiteHunter.Candidate)out.next();
         if (!c.resolved && c.scored && (c.openFull() || c.openSmall()) && c.lastStep != SiteHunter.Step.WAIT_BETTER) {
            open.add(c);
         }
      }

      open.sort((a, b) -> Double.compare(distSq(a, from), distSq(b, from)));
      List<String> outx = new ArrayList<>();

      for (SiteHunter.Candidate c : open.subList(0, Math.min(max, open.size()))) {
         boolean full = c.openFull();
         outx.add(
            siteX(c)
               + ","
               + siteZ(c)
               + " "
               + (full ? "full" : "small")
               + " "
               + Math.round(Math.sqrt(distSq(c, from)))
               + " blocks, score "
               + SiteScoreLog.f(full ? c.fullScore : c.smallScore)
               + ", "
               + state(c)
         );
      }

      return outx;
   }

   private static String state(SiteHunter.Candidate c) {
      if (c.waitingForLoad) {
         return "decided, waiting for its area to load";
      }

      if (c.lastStep == null) {
         return "judged";
      }

      return switch (c.lastStep) {
         case WAIT_UNEXPLORED -> "waiting for unexplored terrain nearby";
         case WAIT_BETTER -> "a better site nearby goes first";
         case WAIT_FULL_FIRST -> "a full village nearby goes first";
         case TRY_FULL, TRY_SMALL -> "ready";
      };
   }

   private static int siteX(SiteHunter.Candidate c) {
      return c.spotSize != null ? c.spotX : c.x();
   }

   private static int siteZ(SiteHunter.Candidate c) {
      return c.spotSize != null ? c.spotZ : c.z();
   }

   private static double distSq(SiteHunter.Candidate c, BlockPos p) {
      double dx = siteX(c) - p.getX();
      double dz = siteZ(c) - p.getZ();
      return dx * dx + dz * dz;
   }

   public void addCandidate(ServerLevel level, BlockPos chunkCentre) {
      long key = ChunkPos.asLong(chunkCentre.getX() >> 4, chunkCentre.getZ() >> 4);
      if (!this.candidates.containsKey(key)) {
         long prot = MillenaireServerConfig.SERVER.spawnProtectionRadius.getAsInt();
         if (!(new BlockPos(chunkCentre.getX(), 0, chunkCentre.getZ()).distSqr(level.getSharedSpawnPos()) < prot * prot)) {
            this.track(key);
            setState(level, key, (byte)1);
         }
      }
   }

   void putSamples(long key, ChunkTerrainSummary s, List<BoundingBox> p) {
      this.samples.put(key, s);
      this.pieces.put(key, p.isEmpty() ? List.of() : p);
   }

   SiteHunter.Candidate track(long key) {
      SiteHunter.Candidate c = new SiteHunter.Candidate(key);
      this.candidates.put(key, c);
      this.work.enqueue(key);
      return c;
   }

   public void tick(ServerLevel level, VillageSpawnQueue queue, VillageManager manager, VillageSavedData saved, boolean genLone) {
      this.minVillage = minVillageDistance();
      this.seed = level.getSeed();
      int limit = Math.min(this.work.size(), 64);
      boolean tried = false;

      for (int i = 0; i < limit; i++) {
         long key = this.work.dequeueLong();
         SiteHunter.Candidate c = (SiteHunter.Candidate)this.candidates.get(key);
         if (c != null && !c.resolved) {
            if (!c.scored && !this.score(c)) {
               this.work.enqueue(key);
            } else {
               String reason = !c.fullPass && !c.smallPass
                  ? (c.reliefRejected ? "no_pass_relief" : (c.spreadRejected ? "no_pass_spread" : "no_pass"))
                  : "blocked";
               if (c.openFull()) {
                  String no = sizeUnavailable(level, c, SiteHunter.Size.FULL);
                  if (no != null) {
                     c.fullClosed = true;
                     reason = no;
                  }
               }

               if (c.openSmall() && !c.openFull()) {
                  String no = sizeUnavailable(level, c, SiteHunter.Size.SMALL);
                  if (no != null) {
                     c.smallPass = false;
                     reason = no;
                  }
               }

               if (c.openFull() && this.blocked(c, SiteHunter.Size.FULL, manager, saved)) {
                  c.fullClosed = true;
               }

               if (c.openSmall() && !c.openFull() && this.blocked(c, SiteHunter.Size.SMALL, manager, saved)) {
                  c.smallPass = false;
               }

               if (!c.openFull() && !c.openSmall()) {
                  this.resolve(level, queue, manager, saved, c, genLone, reason, null);
               } else {
                  SiteHunter.Step step = this.rank(c);
                  c.lastStep = step;
                  if (!step.waiting() && !tried) {
                     tried = true;
                     if (!this.attempt(
                        level, queue, manager, saved, c, step == SiteHunter.Step.TRY_FULL ? SiteHunter.Size.FULL : SiteHunter.Size.SMALL, genLone
                     )) {
                        this.work.enqueue(key);
                     }
                  } else {
                     this.work.enqueue(key);
                  }
               }
            }
         }
      }

      if (LOG && level.getGameTime() % 1200L == 0L) {
         this.writeStatus(level);
      }

      if (level.getGameTime() % 1200L == 0L) {
         this.evict(keyx -> level.hasChunk(ChunkPos.getX(keyx), ChunkPos.getZ(keyx)), keyx -> nearestPlayerDistSq(level, keyx));
      }
   }

   private static double nearestPlayerDistSq(ServerLevel level, long key) {
      double cx = (ChunkPos.getX(key) << 4) + 8;
      double cz = (ChunkPos.getZ(key) << 4) + 8;
      double best = Double.POSITIVE_INFINITY;

      for (ServerPlayer p : level.players()) {
         double dx = p.getX() - cx;
         double dz = p.getZ() - cz;
         best = Math.min(best, dx * dx + dz * dz);
      }

      return best;
   }

   int evict(LongPredicate loaded, LongToDoubleFunction playerDistSq) {
      double candidateFar = 262144.0;
      double sampleFar = 537289.0;
      int dropped = 0;
      ObjectIterator<Entry<SiteHunter.Candidate>> it = this.candidates.long2ObjectEntrySet().fastIterator();

      while (it.hasNext()) {
         long key = ((Entry)it.next()).getLongKey();
         if (!loaded.test(key) && playerDistSq.applyAsDouble(key) > candidateFar) {
            it.remove();
            dropped++;
         }
      }

      it = this.samples.long2ObjectEntrySet().fastIterator();

      while (it.hasNext()) {
         long key = ((Entry)it.next()).getLongKey();
         if (!loaded.test(key) && playerDistSq.applyAsDouble(key) > sampleFar) {
            it.remove();
            this.pieces.remove(key);
            dropped++;
         }
      }

      return dropped;
   }

   private void writeStatus(ServerLevel level) {
      int open = 0;
      int notScored = 0;
      int unexplored = 0;
      int better = 0;
      int fullFirst = 0;
      int load = 0;
      int resolved = 0;
      int spawned = 0;
      ObjectIterator var10 = this.candidates.values().iterator();

      while (var10.hasNext()) {
         SiteHunter.Candidate c = (SiteHunter.Candidate)var10.next();
         if (c.resolved) {
            resolved++;
            if (c.spawned) {
               spawned++;
            }
         } else {
            open++;
            if (!c.scored) {
               notScored++;
            } else if (c.waitingForLoad) {
               load++;
            } else if (c.lastStep == SiteHunter.Step.WAIT_UNEXPLORED) {
               unexplored++;
            } else if (c.lastStep == SiteHunter.Step.WAIT_BETTER) {
               better++;
            } else if (c.lastStep == SiteHunter.Step.WAIT_FULL_FIRST) {
               fullFirst++;
            }
         }
      }

      SiteScoreLog.write(
         level,
         "hunter_status.csv",
         "tick,open,not_scored,waiting_unexplored,waiting_better,waiting_full_first,ready_waiting_for_load,resolved,spawned",
         level.getGameTime() + "," + open + "," + notScored + "," + unexplored + "," + better + "," + fullFirst + "," + load + "," + resolved + "," + spawned
      );
   }

   boolean score(SiteHunter.Candidate c) {
      int reach = Math.max(83, 90);
      if (!this.sampledAround(c.x(), c.z(), reach)) {
         return false;
      } else {
         SiteMeasures full = this.measure(c, 80);
         SiteMeasures small = this.measure(c, 70);
         if (full != null && small != null) {
            BiFunction<Integer, Integer, ChunkTerrainSummary> lookup = (cx, cz) -> (ChunkTerrainSummary)this.samples.get(ChunkPos.asLong(cx, cz));
            c.fullEdgeWater = EdgeWater.share(lookup, c.x(), c.z(), 90);
            c.smallEdgeWater = EdgeWater.share(lookup, c.x(), c.z(), 70);
            c.relief = SevereRelief.count(lookup, c.x(), c.z());
            boolean reliefOk = SevereRelief.ok(c.relief);
            c.reliefRejected = !reliefOk && (passes(full) || passes(small));
            c.coreSpread = CoreSpread.of(lookup, c.x(), c.z());
            boolean spreadOk = c.coreSpread <= MAX_CORE_SPREAD;
            c.spreadRejected = reliefOk && !spreadOk && (passes(full) || passes(small));
            c.fullPass = passes(full) && reliefOk && spreadOk && !this.structureInTheWay(c, 80);
            c.smallPass = passes(small) && reliefOk && spreadOk && !this.structureInTheWay(c, 70);
            c.fullScore = score(full) * EdgeWater.factor(c.fullEdgeWater);
            c.smallScore = score(small) * EdgeWater.factor(c.smallEdgeWater);
            c.centreY = full.centreY();
            c.scored = true;
            return true;
         } else {
            return false;
         }
      }
   }

   static boolean passes(SiteMeasures m) {
      return m.centreUsable() && m.waterOk() && m.lavaOk() && m.centreWeightedOk() && m.connectedOk() && m.bandFraction() >= MIN_BAND;
   }

   static float score(SiteMeasures m) {
      return m.centreUsable() && m.waterOk() && m.lavaOk()
         ? Math.min(
            m.bandFraction() / MIN_BAND,
            Math.min(m.centreWeightedGentle2() / SiteMeasures.MIN_CENTRE_WEIGHTED, m.connectedGentle2() / SiteMeasures.MIN_CONNECTED)
         )
         : 0.0F;
   }

   @Nullable
   private SiteMeasures measure(SiteHunter.Candidate c, int r) {
      return this.measureAt(c.x(), c.z(), r);
   }

   @Nullable
   private SiteMeasures measureAt(int x, int z, int r) {
      BlockPos centre = new BlockPos(x, 0, z);
      BiFunction<Integer, Integer, ChunkTerrainSummary> lookup = (cx, cz) -> {
         ChunkTerrainSummary s = (ChunkTerrainSummary)this.samples.get(ChunkPos.asLong(cx, cz));
         return s == null && SiteScoreLog.outsideSquare(cx, cz, centre, r) ? ChunkTerrainSummary.filler(cx, cz) : s;
      };
      return SiteMeasures.fromSummariesCroppedFast(lookup, x, z, SiteScoreLog.windowChunks(r), r);
   }

   @Nullable
   int[] bestSpot(SiteHunter.Candidate c, SiteHunter.Size size) {
      int r = size == SiteHunter.Size.FULL ? 80 : 70;
      BiFunction<Integer, Integer, ChunkTerrainSummary> lookup = (cx, cz) -> (ChunkTerrainSummary)this.samples.get(ChunkPos.asLong(cx, cz));
      int[] best = null;
      float bestScore = -1.0F;
      int bestDist = Integer.MAX_VALUE;

      for (int dx : SPOT_OFFSETS) {
         for (int dz : SPOT_OFFSETS) {
            int x = c.x() + dx;
            int z = c.z() + dz;
            SiteMeasures m = this.measureAt(x, z, r);
            if (m != null
               && passes(m)
               && !this.structureInTheWay(x, z, r)
               && SevereRelief.ok(SevereRelief.count(lookup, x, z))
               && CoreSpread.of(lookup, x, z) <= MAX_CORE_SPREAD) {
               float s = score(m) * EdgeWater.factor(EdgeWater.share(lookup, x, z, sizeRadius(size)));
               int d = dx * dx + dz * dz;
               if (s > bestScore + 1.0E-6F || Math.abs(s - bestScore) <= 1.0E-6F && d < bestDist) {
                  best = new int[]{x, z};
                  bestScore = s;
                  bestDist = d;
               }
            }
         }
      }

      return best;
   }

   private boolean sampledAround(int x, int z, int half) {
      for (int cx = x - half >> 4; cx <= x + half >> 4; cx++) {
         for (int cz = z - half >> 4; cz <= z + half >> 4; cz++) {
            if (!this.samples.containsKey(ChunkPos.asLong(cx, cz))) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean structureInTheWay(SiteHunter.Candidate c, int r) {
      return this.structureInTheWay(c.x(), c.z(), r);
   }

   private boolean structureInTheWay(int x, int z, int r) {
      int half = r + 3;
      int minX = x - half;
      int maxX = x + half;
      int minZ = z - half;
      int maxZ = z + half;

      for (int cx = minX >> 4; cx <= maxX >> 4; cx++) {
         for (int cz = minZ >> 4; cz <= maxZ >> 4; cz++) {
            List<BoundingBox> boxes = (List<BoundingBox>)this.pieces.get(ChunkPos.asLong(cx, cz));
            if (boxes != null) {
               for (BoundingBox b : boxes) {
                  if (StructureAvoidance.overlapsXZ(b, minX, minZ, maxX, maxZ)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   SiteHunter.Step rank(SiteHunter.Candidate c) {
      boolean full = c.openFull();
      float own = full ? c.fullScore : c.smallScore;
      if (own < GOOD_ENOUGH && !c.promoted && !this.settled(c)) {
         return SiteHunter.Step.WAIT_UNEXPLORED;
      }

      int reach = 8;
      SiteHunter.Candidate best = null;
      float bestScore = own;

      for (int cx = c.cx - reach; cx <= c.cx + reach; cx++) {
         for (int cz = c.cz - reach; cz <= c.cz + reach; cz++) {
            SiteHunter.Candidate o = (SiteHunter.Candidate)this.candidates.get(ChunkPos.asLong(cx, cz));
            if (o != null && o != c && !(dist(o, c) > 128.0)) {
               boolean rival = full ? o.openFull() : o.openSmall() && !o.openFull();
               if (rival) {
                  float s = full ? o.fullScore : o.smallScore;
                  if (this.better(s, o, bestScore, best == null ? c : best)) {
                     best = o;
                     bestScore = s;
                  }
               }
            }
         }
      }

      if (best != null) {
         if (!this.decidable(best)) {
            best.promoted = true;
         }

         return SiteHunter.Step.WAIT_BETTER;
      } else if (!full && this.fullFirstHolder(c) != null) {
         return SiteHunter.Step.WAIT_FULL_FIRST;
      } else {
         return full ? SiteHunter.Step.TRY_FULL : SiteHunter.Step.TRY_SMALL;
      }
   }

   @Nullable
   private SiteHunter.Candidate fullFirstHolder(SiteHunter.Candidate c) {
      int need = requiredDistance(sizeRadius(SiteHunter.Size.SMALL), sizeRadius(SiteHunter.Size.FULL), this.minVillage);
      int far = need + 15 >> 4;

      for (int cx = c.cx - far; cx <= c.cx + far; cx++) {
         for (int cz = c.cz - far; cz <= c.cz + far; cz++) {
            SiteHunter.Candidate o = (SiteHunter.Candidate)this.candidates.get(ChunkPos.asLong(cx, cz));
            if (o != null && o != c && o.openFull() && dist(o, c) < need && this.decidable(o)) {
               return o;
            }
         }
      }

      return null;
   }

   private boolean decidable(SiteHunter.Candidate o) {
      float s = o.openFull() ? o.fullScore : o.smallScore;
      return s >= GOOD_ENOUGH || o.promoted || this.settled(o);
   }

   boolean settled(SiteHunter.Candidate c) {
      int reach = 8;

      for (int cx = c.cx - reach; cx <= c.cx + reach; cx++) {
         for (int cz = c.cz - reach; cz <= c.cz + reach; cz++) {
            long k = ChunkPos.asLong(cx, cz);
            if (!this.samples.containsKey(k)) {
               return false;
            }

            SiteHunter.Candidate o = (SiteHunter.Candidate)this.candidates.get(k);
            if (o != null && o != c && !o.resolved && !o.scored) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean better(float sa, SiteHunter.Candidate a, float sb, SiteHunter.Candidate b) {
      if (sa != sb) {
         return sa > sb;
      }

      long ha = mix(this.seed ^ a.key);
      long hb = mix(this.seed ^ b.key);
      return ha != hb ? ha > hb : a.key < b.key;
   }

   private static long mix(long z) {
      z = (z ^ z >>> 30) * -4658895280553007687L;
      z = (z ^ z >>> 27) * -7723592293110705685L;
      return z ^ z >>> 31;
   }

   static double dist(SiteHunter.Candidate a, SiteHunter.Candidate b) {
      double dx = a.x() - b.x();
      double dz = a.z() - b.z();
      return Math.sqrt(dx * dx + dz * dz);
   }

   private boolean blocked(SiteHunter.Candidate c, SiteHunter.Size size, VillageManager manager, VillageSavedData saved) {
      return this.blockedAt(c.x(), c.z(), size, manager, saved);
   }

   private boolean blockedAt(int x, int z, SiteHunter.Size size, VillageManager manager, VillageSavedData saved) {
      for (Village v : manager.getAllVillages()) {
         VillageType vt = ModCultures.getVillageType(v.getVillageTypeId());
         if (vt == null || !vt.loneBuilding()) {
            int otherRadius = vt != null ? vt.radius() : 90;
            int need = requiredDistance(sizeRadius(size), otherRadius, this.minVillage);
            double dx = v.getCenter().getX() - x;
            double dz = v.getCenter().getZ() - z;
            if (dx * dx + dz * dz < (double)need * need) {
               return true;
            }
         }
      }

      return manager.isWithinMinDistanceOfLoneBuildings(
         new BlockPos(x, 0, z), MillenaireServerConfig.SERVER.minVillageLoneBuildingDistance.getAsInt(), saved.getLoneBuildingPositions()
      );
   }

   @Nullable
   private static String sizeUnavailable(ServerLevel level, SiteHunter.Candidate c, SiteHunter.Size size) {
      int smallest = Integer.MAX_VALUE;

      for (VillageType vt : ModCultures.getAllVillageTypes().values()) {
         if (vt.weight() > 0 && !vt.loneBuilding() && vt.radius() <= 70 == (size == SiteHunter.Size.SMALL)) {
            smallest = Math.min(smallest, vt.radius());
         }
      }

      if (smallest == Integer.MAX_VALUE) {
         return "no_type_of_size";
      }

      long need = (long)MillenaireServerConfig.SERVER.spawnProtectionRadius.getAsInt() + smallest;
      return new BlockPos(c.x(), 0, c.z()).distSqr(level.getSharedSpawnPos()) < (double)need * need ? "spawn_protection" : null;
   }

   private boolean attempt(
      ServerLevel level, VillageSpawnQueue queue, VillageManager manager, VillageSavedData saved, SiteHunter.Candidate c, SiteHunter.Size size, boolean genLone
   ) {
      BlockPos probe = new BlockPos(c.x(), level.getMaxBuildHeight() - 1, c.z());
      double distToSpawnSq = new BlockPos(c.x(), 0, c.z()).distSqr(level.getSharedSpawnPos());
      int[] excluded = new int[2];
      List<VillageType> fitting = this.fittingTypes(level, c, size, probe, distToSpawnSq, excluded);
      if (fitting.isEmpty()) {
         log(
            level,
            "no_type",
            size,
            c,
            null,
            "no " + size.name().toLowerCase() + " type fits: " + excluded[0] + " ruled out by biome, " + excluded[1] + " by spawn protection"
         );
         return this.close(level, queue, manager, saved, c, size, genLone);
      }

      VillageType vt = c.chosenSize == size && c.chosenType != null && fitting.contains(c.chosenType) ? c.chosenType : pickByWeight(fitting);
      c.chosenType = vt;
      c.chosenSize = size;
      if (c.spotSize != size) {
         int[] spot = this.bestSpot(c, size);
         boolean usable = spot != null && !this.blockedAt(spot[0], spot[1], size, manager, saved);
         c.spotX = usable ? spot[0] : c.x();
         c.spotZ = usable ? spot[1] : c.z();
         c.spotSize = size;
      }

      int sx = c.spotX;
      int sz = c.spotZ;
      int r = vt.radius();
      if (!VillageTerrainMap.placementAreaLoaded(level, sx, sz, r)) {
         c.waitingForLoad = true;
         if (!c.readyLogged) {
            c.readyLogged = true;
            log(level, "ready", size, c, vt, "waiting for the area to load");
            hint(
               level,
               sx,
               sz,
               size.name().toLowerCase()
                  + " village site chosen ("
                  + shortId(vt)
                  + ", score "
                  + SiteScoreLog.f(size == SiteHunter.Size.FULL ? c.fullScore : c.smallScore)
                  + "): it spawns once its whole area is loaded"
            );
         }

         return false;
      } else {
         c.waitingForLoad = false;
         int y = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, sx, sz);
         BlockPos surface = new BlockPos(sx, y, sz);
         if (manager.overlapsExistingVillage(surface, r)) {
            log(level, "blocked", size, c, vt, "footprint overlaps a village");
            return this.close(level, queue, manager, saved, c, size, genLone);
         } else {
            c.trials++;
            c.chosenType = null;
            c.tried.add(vt.id());
            int completion = VillageSpawnQueue.computeCompletionByDistance(Math.sqrt(distToSpawnSq));
            Component failure = VillageSpawner.spawnVillage(level, townHallOrigin(level, sx, sz, vt), vt, completion);
            if (failure == null) {
               LOGGER.info(
                  "[Millenaire site hunter] {} village {} spawned at {} (score {}, trial {})",
                  new Object[]{
                     size.name().toLowerCase(), vt.id(), surface.toShortString(), size == SiteHunter.Size.FULL ? c.fullScore : c.smallScore, c.trials
                  }
               );
               float edge = size == SiteHunter.Size.FULL ? c.fullEdgeWater : c.smallEdgeWater;
               log(
                  level,
                  "spawned",
                  size,
                  c,
                  vt,
                  "completion "
                     + completion
                     + ", edge water "
                     + (Float.isNaN(edge) ? "?" : Math.round(edge * 100.0F) + "%")
                     + ", relief "
                     + c.relief
                     + ", core spread "
                     + c.coreSpread
                     + ", spot "
                     + (sx - c.x())
                     + ","
                     + (sz - c.z())
                     + " from the chunk centre"
               );
               tell(
                  level,
                  sx,
                  sz,
                  ChatFormatting.GREEN,
                  shortId(vt)
                     + " spawned (score "
                     + SiteScoreLog.f(size == SiteHunter.Size.FULL ? c.fullScore : c.smallScore)
                     + ", core spread "
                     + c.coreSpread
                     + ", edge water "
                     + (Float.isNaN(edge) ? "?" : Math.round(edge * 100.0F) + "%")
                     + ")"
               );
               c.spawned = true;
               this.spawnedThisSession++;
               c.resolved = true;
               setState(level, c.key, (byte)2);
               return true;
            } else {
               log(level, "trial_failed", size, c, vt, failure.getString());
               tell(
                  level,
                  sx,
                  sz,
                  ChatFormatting.GRAY,
                  shortId(vt) + " could not be built here (" + failure.getString() + ")" + (c.trials >= 3 ? "; site given up" : "; another type may be tried")
               );
               if (c.trials >= 3) {
                  this.resolve(level, queue, manager, saved, c, genLone, "trials_used", vt);
                  return true;
               } else {
                  return this.fittingTypes(level, c, size, probe, distToSpawnSq, new int[2]).isEmpty()
                     ? this.close(level, queue, manager, saved, c, size, genLone)
                     : false;
               }
            }
         }
      }
   }

   static BlockPos townHallOrigin(ServerLevel level, int siteX, int siteZ, VillageType vt) {
      int[] size = SiteScoreLog.centreBuildingSize(vt);
      int x = siteX - (size == null ? 0 : size[0] / 2);
      int z = siteZ - (size == null ? 0 : size[1] / 2);
      return new BlockPos(x, level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
   }

   private boolean close(
      ServerLevel level, VillageSpawnQueue queue, VillageManager manager, VillageSavedData saved, SiteHunter.Candidate c, SiteHunter.Size size, boolean genLone
   ) {
      if (size == SiteHunter.Size.FULL) {
         c.fullClosed = true;
      } else {
         c.smallPass = false;
      }

      if (!c.openFull() && !c.openSmall()) {
         this.resolve(level, queue, manager, saved, c, genLone, "closed", null);
         return true;
      } else {
         return false;
      }
   }

   private List<VillageType> fittingTypes(ServerLevel level, SiteHunter.Candidate c, SiteHunter.Size size, BlockPos probe, double distToSpawnSq, int[] excluded) {
      boolean hamlets = (Boolean)MillenaireServerConfig.SERVER.generateHamlets.get();
      long prot = MillenaireServerConfig.SERVER.spawnProtectionRadius.getAsInt();
      Holder<Biome> centreBiome = level.getBiome(probe);
      Map<Integer, List<Holder<Biome>>> biomeSamples = new HashMap<>();
      List<VillageType> out = new ArrayList<>();

      for (VillageType vt : ModCultures.getAllVillageTypes().values()) {
         if (vt.weight() > 0
            && !vt.loneBuilding()
            && vt.radius() <= 70 == (size == SiteHunter.Size.SMALL)
            && (hamlets || vt.hamlets().isEmpty())
            && !c.tried.contains(vt.id())) {
            long combined = prot + vt.radius();
            if (distToSpawnSq < (double)combined * combined) {
               excluded[1]++;
            } else {
               if (!vt.biomeTags().isEmpty()) {
                  if (!VillageSpawnQueue.matchesAnyTag(centreBiome, vt.biomeTags())) {
                     excluded[0]++;
                     continue;
                  }

                  List<Holder<Biome>> s = biomeSamples.computeIfAbsent(
                     VillageSpawnQueue.biomeSampleRadius(vt), rr -> VillageSpawnQueue.sampleBiomes(level, probe, rr)
                  );
                  float validity = (float)VillageSpawnQueue.countMatching(s, vt) / s.size();
                  if (validity < VillageSpawnQueue.requiredBiomeValidity(vt)) {
                     excluded[0]++;
                     continue;
                  }
               }

               out.add(vt);
            }
         }
      }

      return out;
   }

   private static VillageType pickByWeight(List<VillageType> types) {
      int total = 0;

      for (VillageType vt : types) {
         total += vt.weight();
      }

      int roll = ThreadLocalRandom.current().nextInt(Math.max(1, total));

      for (VillageType vt : types) {
         roll -= vt.weight();
         if (roll < 0) {
            return vt;
         }
      }

      return types.getLast();
   }

   private void resolve(
      ServerLevel level,
      VillageSpawnQueue queue,
      VillageManager manager,
      VillageSavedData saved,
      SiteHunter.Candidate c,
      boolean genLone,
      String reason,
      @Nullable VillageType vt
   ) {
      c.resolved = true;
      setState(level, c.key, (byte)2);
      log(level, "resolved", null, c, vt, reason);
      if (genLone) {
         BlockPos pos = new BlockPos(c.x(), c.scored ? c.centreY : level.getSeaLevel(), c.z());
         queue.deferToLoneBuildings(level, pos, new BlockPos(c.x(), 0, c.z()).distSqr(level.getSharedSpawnPos()), manager, saved);
      }
   }

   private static void setState(ServerLevel level, long key, byte state) {
      LevelChunk chunk = level.getChunkSource().getChunkNow(ChunkPos.getX(key), ChunkPos.getZ(key));
      if (chunk != null) {
         SiteChunkData d = (SiteChunkData)chunk.getExistingDataOrNull((AttachmentType)ModAttachments.SITE_CHUNK.get());
         chunk.setData((AttachmentType)ModAttachments.SITE_CHUNK.get(), (d == null ? SiteChunkData.EMPTY : d).withState(state));
         chunk.setUnsaved(true);
      }
   }

   private static void logArtificial(ServerLevel level, LevelChunk chunk, int[] heights, ArtificialScan scan, boolean avoided) {
      if (LOG && scan.any()) {
         ChunkPos pos = chunk.getPos();
         String row = level.getGameTime()
            + ","
            + pos.x
            + ","
            + pos.z
            + ","
            + ((pos.x << 4) + 8)
            + ","
            + ((pos.z << 4) + 8)
            + ","
            + Integer.bitCount(scan.mask())
            + ","
            + scan.blockEntities()
            + ","
            + SiteScoreLog.csv(String.join(" ", surfaceStructures(level, pos, heights)))
            + ","
            + avoided
            + ","
            + SiteScoreLog.csv(villageAt(level, pos))
            + ","
            + SiteScoreLog.csv(String.join(" ", scan.blocks()))
            + ","
            + SiteScoreLog.csv(String.join(" ", scan.entityTypes()));
         SiteScoreLog.write(
            level, "hunter_artificial_v3.csv", "tick,chunk_x,chunk_z,x,z,sample_hits,block_entities,structures,avoided,village,blocks,block_entity_types", row
         );
      }
   }

   private static Set<String> surfaceStructures(ServerLevel level, ChunkPos pos, int[] heights) {
      int ground = heights.length > 0 ? Arrays.stream(heights).min().getAsInt() : level.getMinBuildHeight();
      Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
      Set<String> names = new TreeSet<>();

      for (StructureStart start : level.structureManager().startsForStructure(pos, s -> true)) {
         if (start.isValid()) {
            for (StructurePiece piece : start.getPieces()) {
               BoundingBox b = piece.getBoundingBox();
               if (b.maxY() >= ground - 1
                  && b.minX() <= pos.getMaxBlockX()
                  && b.maxX() >= pos.getMinBlockX()
                  && b.minZ() <= pos.getMaxBlockZ()
                  && b.maxZ() >= pos.getMinBlockZ()) {
                  ResourceLocation id = registry.getKey(start.getStructure());
                  names.add(id == null ? "?" : id.toString());
                  break;
               }
            }
         }
      }

      return names;
   }

   private static String villageAt(ServerLevel level, ChunkPos pos) {
      int x = (pos.x << 4) + 8;
      int z = (pos.z << 4) + 8;

      for (Village v : VillageSavedData.get(level).getVillageManager().getAllVillages()) {
         VillageType vt = ModCultures.getVillageType(v.getVillageTypeId());
         int reach = (vt != null ? vt.radius() : 90) + 16;
         if (Math.abs(v.getCenter().getX() - x) <= reach && Math.abs(v.getCenter().getZ() - z) <= reach) {
            return String.valueOf(v.getVillageTypeId());
         }
      }

      return "";
   }

   private static void log(
      ServerLevel level, String event, @Nullable SiteHunter.Size size, SiteHunter.Candidate c, @Nullable VillageType vt, @Nullable String detail
   ) {
      if (LOG) {
         ServerPlayer nearest = null;
         double best = Double.MAX_VALUE;

         for (ServerPlayer p : level.players()) {
            double dx = p.getX() - c.x();
            double dz = p.getZ() - c.z();
            double d = dx * dx + dz * dz;
            if (d < best) {
               best = d;
               nearest = p;
            }
         }

         String distance = "";
         String behind = "";
         if (nearest != null) {
            distance = String.valueOf(Math.round(Math.sqrt(best)));
            Vec3 look = nearest.getLookAngle();
            behind = String.valueOf(look.x * (c.x() - nearest.getX()) + look.z * (c.z() - nearest.getZ()) < 0.0);
         }

         String row = level.getGameTime()
            + ","
            + event
            + ","
            + (size == null ? "" : size.name().toLowerCase())
            + ","
            + c.cx
            + ","
            + c.cz
            + ","
            + c.x()
            + ","
            + c.z()
            + ","
            + (vt == null ? "" : vt.id())
            + ","
            + (c.scored ? SiteScoreLog.f(c.fullScore) : "")
            + ","
            + (c.scored ? SiteScoreLog.f(c.smallScore) : "")
            + ","
            + c.trials
            + ","
            + distance
            + ","
            + behind
            + ","
            + SiteScoreLog.csv(detail == null ? "" : detail);
         SiteScoreLog.write(
            level, "hunter.csv", "tick,event,size,chunk_x,chunk_z,x,z,type,full_score,small_score,trial,player_distance,player_behind,detail", row
         );
      }
   }

   private static void tell(ServerLevel level, int x, int z, ChatFormatting colour, String text) {
      if (CHAT) {
         String tp = "/tp @s " + x + " ~ " + z;

         for (ServerPlayer p : level.players()) {
            long d = Math.round(Math.hypot(p.getX() - x, p.getZ() - z));
            Component button = Component.literal(" [tp]")
               .withStyle(
                  s -> s.withColor(ChatFormatting.AQUA)
                     .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, tp))
                     .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(tp)))
               );
            p.sendSystemMessage(Component.literal("[Sites] " + text + " at " + x + "," + z + ", " + d + " blocks away").withStyle(colour).append(button));
         }
      }
   }

   private static void hint(ServerLevel level, int x, int z, String details) {
      BlockPos site = new BlockPos(x, 0, z);
      double radiusSq = (double)VillageNotifier.getNotificationRadius() * VillageNotifier.getNotificationRadius();

      for (ServerPlayer p : level.players()) {
         BlockPos at = new BlockPos(p.getBlockX(), 0, p.getBlockZ());
         if (!(at.distSqr(site) >= radiusSq)) {
            MutableComponent line = Component.translatable(
                  "hint.millenaire.village_nearby." + level.random.nextInt(4), new Object[]{VillageNotifier.cardinalPlaceComponent(at, site)}
               )
               .withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
            if (CHAT) {
               String tp = "/tp @s " + x + " ~ " + z;
               String hover = "[Sites] " + details + " at " + x + "," + z + ", " + Math.round(Math.sqrt(at.distSqr(site))) + " blocks away\n" + tp;
               line.append(
                  Component.literal(" [tp]")
                     .withStyle(
                        s -> s.withColor(ChatFormatting.AQUA)
                           .withItalic(false)
                           .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, tp))
                           .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(hover)))
                     )
               );
            }

            p.sendSystemMessage(line);
         }
      }
   }

   private static String shortId(VillageType vt) {
      return vt.id().getPath();
   }

   public void reset() {
      this.samples.clear();
      this.pieces.clear();
      this.candidates.clear();
      this.work.clear();
      this.spawnedThisSession = 0;
   }

   boolean hasSamples(long key) {
      return this.samples.containsKey(key);
   }

   @Nullable
   SiteHunter.Candidate candidate(long key) {
      return (SiteHunter.Candidate)this.candidates.get(key);
   }

   static final class Candidate {
      final long key;
      final int cx;
      final int cz;
      boolean scored;
      boolean resolved;
      boolean fullPass;
      boolean smallPass;
      boolean fullClosed;
      float fullScore;
      float smallScore;
      int centreY;
      int trials;
      final Set<ResourceLocation> tried = new HashSet<>();
      @Nullable
      SiteHunter.Step lastStep;
      boolean waitingForLoad;
      boolean readyLogged;
      boolean spawned;
      @Nullable
      VillageType chosenType;
      @Nullable
      SiteHunter.Size chosenSize;
      float fullEdgeWater = Float.NaN;
      float smallEdgeWater = Float.NaN;
      int relief;
      boolean reliefRejected;
      int coreSpread;
      boolean spreadRejected;
      boolean promoted;
      int spotX;
      int spotZ;
      @Nullable
      SiteHunter.Size spotSize;

      Candidate(long key) {
         this.key = key;
         this.cx = ChunkPos.getX(key);
         this.cz = ChunkPos.getZ(key);
      }

      int x() {
         return (this.cx << 4) + 8;
      }

      int z() {
         return (this.cz << 4) + 8;
      }

      boolean openFull() {
         return this.scored && !this.resolved && this.fullPass && !this.fullClosed;
      }

      boolean openSmall() {
         return this.scored && !this.resolved && this.smallPass;
      }
   }

   enum Size {
      FULL,
      SMALL;

      // $VF: synthetic method
      private static SiteHunter.Size[] $values() {
         return new SiteHunter.Size[]{FULL, SMALL};
      }
   }

   enum Step {
      WAIT_UNEXPLORED,
      WAIT_BETTER,
      WAIT_FULL_FIRST,
      TRY_FULL,
      TRY_SMALL;

      boolean waiting() {
         return this != TRY_FULL && this != TRY_SMALL;
      }

      // $VF: synthetic method
      private static SiteHunter.Step[] $values() {
         return new SiteHunter.Step[]{WAIT_UNEXPLORED, WAIT_BETTER, WAIT_FULL_FIRST, TRY_FULL, TRY_SMALL};
      }
   }
}
