package net.caffeinemc.mods.lithium.common.world.chunk;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.world.level.chunk.MissingPaletteEntryException;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;
import org.jetbrains.annotations.NotNull;

public class LithiumHashPalette<T> implements Palette<T> {
   private static final int ABSENT_VALUE = -1;
   private final IdMap<T> idList;
   private final PaletteResize<T> resizeHandler;
   private final int indexBits;
   private final Reference2IntOpenHashMap<T> table;
   private T[] entries;
   private int size = 0;

   private LithiumHashPalette(IdMap<T> idList, PaletteResize<T> resizeHandler, int indexBits, T[] entries, Reference2IntOpenHashMap<T> table, int size) {
      this.idList = idList;
      this.resizeHandler = resizeHandler;
      this.indexBits = indexBits;
      this.entries = entries;
      this.table = table;
      this.size = size;
   }

   public LithiumHashPalette(IdMap<T> idList, int bits, PaletteResize<T> resizeHandler, List<T> list) {
      this(idList, bits, resizeHandler);

      for (T t : list) {
         this.addEntry(t);
      }
   }

   public LithiumHashPalette(IdMap<T> idList, int bits, PaletteResize<T> resizeHandler) {
      this.idList = idList;
      this.indexBits = bits;
      this.resizeHandler = resizeHandler;
      int capacity = 1 << bits;
      this.entries = (T[])(new Object[capacity]);
      this.table = new Reference2IntOpenHashMap(capacity, 0.5F);
      this.table.defaultReturnValue(-1);
   }

   public int idFor(@NotNull T obj) {
      int id = this.table.getInt(obj);
      if (id == -1) {
         id = this.computeEntry(obj);
      }

      return id;
   }

   public boolean maybeHas(@NotNull Predicate<T> predicate) {
      for (int i = 0; i < this.size; i++) {
         if (predicate.test(this.entries[i])) {
            return true;
         }
      }

      return false;
   }

   private int computeEntry(T obj) {
      int id = this.addEntry(obj);
      if (id >= 1 << this.indexBits) {
         if (this.resizeHandler == null) {
            throw new IllegalStateException("Cannot grow");
         }

         id = this.resizeHandler.onResize(this.indexBits + 1, obj);
      }

      return id;
   }

   private int addEntry(T obj) {
      int nextId = this.size;
      if (nextId >= this.entries.length) {
         this.resize(this.size);
      }

      this.table.put(obj, nextId);
      this.entries[nextId] = obj;
      this.size++;
      return nextId;
   }

   private void resize(int neededCapacity) {
      this.entries = (T[])Arrays.copyOf(this.entries, HashCommon.nextPowerOfTwo(neededCapacity + 1));
   }

   @NotNull
   public T valueFor(int id) {
      T[] entries = this.entries;
      T entry = null;
      if (id >= 0 && id < entries.length) {
         entry = entries[id];
      }

      return entry != null ? entry : this.recoverMissingPaletteEntryOrCrash(id);
   }

   @NotNull
   private T recoverMissingPaletteEntryOrCrash(int id) {
      try {
         Thread.sleep(1L);
      } catch (InterruptedException var4) {
      }

      T[] entries = this.entries;
      T entry = null;
      if (id >= 0 && id < entries.length) {
         entry = entries[id];
      }

      if (entry != null) {
         return entry;
      } else {
         throw this.missingPaletteEntryCrash(id);
      }
   }

   private ReportedException missingPaletteEntryCrash(int id) {
      try {
         throw new MissingPaletteEntryException(id);
      } catch (MissingPaletteEntryException e) {
         CrashReport crashReport = CrashReport.forThrowable(e, "[Lithium] Getting Palette Entry");
         CrashReportCategory crashReportCategory = crashReport.addCategory("Chunk section");
         crashReportCategory.setDetail("IndexBits", this.indexBits);
         crashReportCategory.setDetail("Entries", this.entries.length + " Elements: " + Arrays.toString(this.entries));
         crashReportCategory.setDetail("Table", this.table.size() + " Elements: " + this.table);
         return new ReportedException(crashReport);
      }
   }

   public void read(FriendlyByteBuf buf) {
      this.clear();
      int entryCount = buf.readVarInt();

      for (int i = 0; i < entryCount; i++) {
         this.addEntry((T)this.idList.byIdOrThrow(buf.readVarInt()));
      }
   }

   public void write(FriendlyByteBuf buf) {
      int size = this.size;
      buf.writeVarInt(size);

      for (int i = 0; i < size; i++) {
         buf.writeVarInt(this.idList.getId(this.valueFor(i)));
      }
   }

   public int getSerializedSize() {
      int size = VarInt.getByteSize(this.size);

      for (int i = 0; i < this.size; i++) {
         size += VarInt.getByteSize(this.idList.getId(this.valueFor(i)));
      }

      return size;
   }

   public int getSize() {
      return this.size;
   }

   @NotNull
   public Palette<T> copy() {
      return new LithiumHashPalette<>(this.idList, this.resizeHandler, this.indexBits, (T[])((Object[])this.entries.clone()), this.table.clone(), this.size);
   }

   private void clear() {
      Arrays.fill(this.entries, null);
      this.table.clear();
      this.size = 0;
   }

   public List<T> getElements() {
      T[] copy = (T[])Arrays.copyOf(this.entries, this.size);
      return Arrays.asList(copy);
   }

   public static <A> Palette<A> create(int bits, IdMap<A> idList, PaletteResize<A> listener, List<A> list) {
      return new LithiumHashPalette<>(idList, bits, listener, list);
   }
}
