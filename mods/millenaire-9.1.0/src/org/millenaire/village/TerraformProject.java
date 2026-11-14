package org.millenaire.village;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.world.PlacedLocation;
import org.millenaire.world.TerraformJobCompiler;

public final class TerraformProject {
   private final ResourceLocation planSetId;
   private final String variant;
   private final int level;
   private final PlacedLocation location;
   private final int padY;
   private final int cutVolume;
   private final int fillVolume;
   private final int gate;
   private final long createdTick;
   private final List<TerraformJobCompiler.Op> ops;
   private final BitSet done;
   private TerraformProject.Status status;
   private long lastProgressTick;
   private final Map<UUID, List<Integer>> claims = new HashMap<>();
   private final Map<UUID, Long> claimTicks = new HashMap<>();
   private int scanFrom;
   private boolean verified;
   private long[] columns;

   public TerraformProject(
      ResourceLocation planSetId,
      String variant,
      int level,
      PlacedLocation location,
      int padY,
      int cutVolume,
      int fillVolume,
      long createdTick,
      List<TerraformJobCompiler.Op> ops
   ) {
      this(planSetId, variant, level, location, padY, cutVolume, fillVolume, createdTick, ops, 9);
   }

   public TerraformProject(
      ResourceLocation planSetId,
      String variant,
      int level,
      PlacedLocation location,
      int padY,
      int cutVolume,
      int fillVolume,
      long createdTick,
      List<TerraformJobCompiler.Op> ops,
      int gate
   ) {
      this(
         planSetId,
         variant,
         level,
         location,
         padY,
         cutVolume,
         fillVolume,
         gate,
         createdTick,
         ops,
         new BitSet(ops.size()),
         TerraformProject.Status.ACTIVE,
         createdTick
      );
   }

   private TerraformProject(
      ResourceLocation planSetId,
      String variant,
      int level,
      PlacedLocation location,
      int padY,
      int cutVolume,
      int fillVolume,
      int gate,
      long createdTick,
      List<TerraformJobCompiler.Op> ops,
      BitSet done,
      TerraformProject.Status status,
      long lastProgressTick
   ) {
      this.planSetId = planSetId;
      this.variant = variant;
      this.level = level;
      this.location = location;
      this.padY = padY;
      this.cutVolume = cutVolume;
      this.fillVolume = fillVolume;
      this.gate = gate;
      this.createdTick = createdTick;
      this.ops = List.copyOf(ops);
      this.done = done;
      this.status = done.cardinality() >= ops.size() ? TerraformProject.Status.READY : status;
      this.lastProgressTick = lastProgressTick;
   }

   public ResourceLocation planSetId() {
      return this.planSetId;
   }

   public String variant() {
      return this.variant;
   }

   public int level() {
      return this.level;
   }

   public PlacedLocation location() {
      return this.location;
   }

   public int padY() {
      return this.padY;
   }

   public int cutVolume() {
      return this.cutVolume;
   }

   public int fillVolume() {
      return this.fillVolume;
   }

   public int gate() {
      return this.gate;
   }

   public long createdTick() {
      return this.createdTick;
   }

   public long lastProgressTick() {
      return this.lastProgressTick;
   }

   public TerraformProject.Status status() {
      return this.status;
   }

   public int totalOps() {
      return this.ops.size();
   }

   public int doneOps() {
      return this.done.cardinality();
   }

   public int activeWorkers() {
      return this.claims.size();
   }

   public Map<UUID, List<Integer>> claimsSnapshot() {
      Map<UUID, List<Integer>> copy = new HashMap<>();
      this.claims.forEach((worker, held) -> copy.put(worker, List.copyOf((Collection<? extends Integer>)held)));
      return copy;
   }

   public TerraformJobCompiler.Op op(int index) {
      return this.ops.get(index);
   }

   public boolean hasUnclaimedWork() {
      return this.status != TerraformProject.Status.ACTIVE ? false : this.nextFree(this.scanFrom) >= 0;
   }

   public List<Integer> claim(UUID worker, int max) {
      return this.claim(worker, max, this.lastProgressTick);
   }

   public List<Integer> claim(UUID worker, int max, long gameTick) {
      this.release(worker);
      List<Integer> got = new ArrayList<>(max);
      int i = this.nextFree(this.scanFrom);
      if (i >= 0) {
         this.scanFrom = i;
      }

      while (i >= 0 && got.size() < max) {
         got.add(i);
         i = this.nextFree(i + 1);
      }

      if (!got.isEmpty()) {
         this.claims.put(worker, new ArrayList<>(got));
         this.claimTicks.put(worker, gameTick);
      }

      return got;
   }

   public void complete(UUID worker, int index, long gameTick) {
      this.done.set(index);
      this.lastProgressTick = gameTick;
      if (this.claims.containsKey(worker)) {
         this.claimTicks.put(worker, gameTick);
      }

      List<Integer> held = this.claims.get(worker);
      if (held != null) {
         held.remove(Integer.valueOf(index));
         if (held.isEmpty()) {
            this.claims.remove(worker);
         }
      }

      if (this.done.cardinality() >= this.ops.size()) {
         this.status = TerraformProject.Status.READY;
         this.claims.clear();
         this.claimTicks.clear();
      }
   }

   public void releaseStale(long gameTick, long maxIdleTicks) {
      for (UUID worker : List.copyOf(this.claims.keySet())) {
         Long since = this.claimTicks.get(worker);
         if (since == null || gameTick - since > maxIdleTicks) {
            this.release(worker);
         }
      }
   }

   public void release(UUID worker) {
      this.claimTicks.remove(worker);
      List<Integer> held = this.claims.remove(worker);
      if (held != null && !held.isEmpty()) {
         this.scanFrom = Math.min(this.scanFrom, held.get(0));
      }
   }

   private int nextFree(int from) {
      for (int i = this.done.nextClearBit(from); i < this.ops.size(); i = this.done.nextClearBit(i + 1)) {
         if (!this.isClaimed(i)) {
            return i;
         }
      }

      return -1;
   }

   private boolean isClaimed(int index) {
      for (List<Integer> held : this.claims.values()) {
         if (held.contains(index)) {
            return true;
         }
      }

      return false;
   }

   public long[] columns() {
      if (this.columns == null) {
         Set<Long> set = new HashSet<>();

         for (TerraformJobCompiler.Op op : this.ops) {
            set.add((long)op.pos().getX() << 32 | op.pos().getZ() & 4294967295L);
         }

         this.columns = set.stream().mapToLong(Long::longValue).toArray();
      }

      return this.columns;
   }

   public boolean verified() {
      return this.verified;
   }

   public void markVerified() {
      this.verified = true;
   }

   public List<TerraformJobCompiler.Op> ops() {
      return this.ops;
   }

   public void completeAll(long gameTick) {
      this.done.set(0, this.ops.size());
      this.claims.clear();
      this.claimTicks.clear();
      this.status = TerraformProject.Status.READY;
      this.lastProgressTick = gameTick;
   }

   public CompoundTag save() {
      CompoundTag tag = new CompoundTag();
      tag.putString("plan_set", this.planSetId.toString());
      tag.putString("variant", this.variant);
      tag.putInt("level", this.level);
      tag.putInt("x", this.location.position().getX());
      tag.putInt("y", this.location.position().getY());
      tag.putInt("z", this.location.position().getZ());
      tag.putInt("rotation", this.location.rotation().ordinal());
      tag.putInt("pad_y", this.padY);
      tag.putInt("cut", this.cutVolume);
      tag.putInt("fill", this.fillVolume);
      tag.putInt("gate", this.gate);
      tag.putLong("created", this.createdTick);
      tag.putLong("last_progress", this.lastProgressTick);
      tag.putString("status", this.status.name());
      List<BlockState> palette = new ArrayList<>();
      Map<BlockState, Integer> paletteIndex = new HashMap<>();
      int[] packed = new int[this.ops.size() * 4];

      for (int i = 0; i < this.ops.size(); i++) {
         TerraformJobCompiler.Op op = this.ops.get(i);
         int p = 0;
         if (op.state() != null) {
            Integer known = paletteIndex.get(op.state());
            if (known == null) {
               known = palette.size();
               palette.add(op.state());
               paletteIndex.put(op.state(), known);
            }

            p = known + 1;
         }

         packed[i * 4] = op.pos().getX();
         packed[i * 4 + 1] = op.pos().getY();
         packed[i * 4 + 2] = op.pos().getZ();
         packed[i * 4 + 3] = p << 3 | (op.cut() ? 4 : 0) | op.kind().ordinal();
      }

      ListTag paletteTag = new ListTag();

      for (BlockState state : palette) {
         paletteTag.add(NbtUtils.writeBlockState(state));
      }

      tag.put("palette", paletteTag);
      tag.putIntArray("ops", packed);
      tag.putLongArray("done", this.done.toLongArray());
      return tag;
   }

   @Nullable
   public static TerraformProject load(CompoundTag tag) {
      ListTag paletteTag = tag.getList("palette", 10);
      List<BlockState> palette = new ArrayList<>(paletteTag.size());

      for (int i = 0; i < paletteTag.size(); i++) {
         palette.add(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), paletteTag.getCompound(i)));
      }

      int[] packed = tag.getIntArray("ops");
      List<TerraformJobCompiler.Op> ops = new ArrayList<>(packed.length / 4);
      TerraformJobCompiler.Kind[] kinds = TerraformJobCompiler.Kind.values();

      for (int i = 0; i + 3 < packed.length; i += 4) {
         int meta = packed[i + 3];
         int p = meta >>> 3;
         BlockState state = p > 0 && p - 1 < palette.size() ? palette.get(p - 1) : null;
         ops.add(
            new TerraformJobCompiler.Op(
               new BlockPos(packed[i], packed[i + 1], packed[i + 2]), kinds[Math.min(meta & 3, kinds.length - 1)], state, (meta & 4) != 0
            )
         );
      }

      int rot = Math.floorMod(tag.getInt("rotation"), Rotation.values().length);
      PlacedLocation location = new PlacedLocation(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), Rotation.values()[rot]);

      TerraformProject.Status status;
      try {
         status = TerraformProject.Status.valueOf(tag.getString("status"));
      } catch (IllegalArgumentException e) {
         status = TerraformProject.Status.ACTIVE;
      }

      return new TerraformProject(
         ResourceLocation.parse(tag.getString("plan_set")),
         tag.getString("variant"),
         tag.getInt("level"),
         location,
         tag.getInt("pad_y"),
         tag.getInt("cut"),
         tag.getInt("fill"),
         tag.contains("gate") ? tag.getInt("gate") : 9,
         tag.getLong("created"),
         ops,
         BitSet.valueOf(tag.getLongArray("done")),
         status,
         tag.getLong("last_progress")
      );
   }

   public enum Status {
      ACTIVE,
      READY;

      // $VF: synthetic method
      private static TerraformProject.Status[] $values() {
         return new TerraformProject.Status[]{ACTIVE, READY};
      }
   }
}
