package com.fasterxml.jackson.databind.util.internal;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class PrivateMaxEntriesMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
   static final int NCPU = Runtime.getRuntime().availableProcessors();
   static final long MAXIMUM_CAPACITY = 9223372034707292160L;
   static final int NUMBER_OF_READ_BUFFERS = ceilingNextPowerOfTwo(NCPU);
   static final int READ_BUFFERS_MASK = NUMBER_OF_READ_BUFFERS - 1;
   static final int READ_BUFFER_THRESHOLD = 32;
   static final int READ_BUFFER_DRAIN_THRESHOLD = 64;
   static final int READ_BUFFER_SIZE = 128;
   static final int READ_BUFFER_INDEX_MASK = 127;
   static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
   static final Queue<?> DISCARDING_QUEUE = new PrivateMaxEntriesMap.DiscardingQueue();
   final ConcurrentMap<K, PrivateMaxEntriesMap.Node<K, V>> data;
   final int concurrencyLevel;
   final long[] readBufferReadCount;
   final LinkedDeque<PrivateMaxEntriesMap.Node<K, V>> evictionDeque;
   final AtomicLong weightedSize;
   final AtomicLong capacity;
   final Lock evictionLock;
   final Queue<Runnable> writeBuffer;
   final AtomicLong[] readBufferWriteCount;
   final AtomicLong[] readBufferDrainAtWriteCount;
   final AtomicReference<PrivateMaxEntriesMap.Node<K, V>>[][] readBuffers;
   final AtomicReference<PrivateMaxEntriesMap.DrainStatus> drainStatus;
   final Queue<PrivateMaxEntriesMap.Node<K, V>> pendingNotifications;
   final EvictionListener<K, V> listener;
   transient Set<K> keySet;
   transient Collection<V> values;
   transient Set<Entry<K, V>> entrySet;
   static final long serialVersionUID = 1L;

   static int ceilingNextPowerOfTwo(int x) {
      return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
   }

   private PrivateMaxEntriesMap(PrivateMaxEntriesMap.Builder<K, V> builder) {
      this.concurrencyLevel = builder.concurrencyLevel;
      this.capacity = new AtomicLong(Math.min(builder.capacity, 9223372034707292160L));
      this.data = new ConcurrentHashMap<>(builder.initialCapacity, 0.75F, this.concurrencyLevel);
      this.evictionLock = new ReentrantLock();
      this.weightedSize = new AtomicLong();
      this.evictionDeque = new LinkedDeque<>();
      this.writeBuffer = new ConcurrentLinkedQueue<>();
      this.drainStatus = new AtomicReference<>(PrivateMaxEntriesMap.DrainStatus.IDLE);
      this.readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
      this.readBufferWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
      this.readBufferDrainAtWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
      this.readBuffers = new AtomicReference[NUMBER_OF_READ_BUFFERS][128];

      for (int i = 0; i < NUMBER_OF_READ_BUFFERS; i++) {
         this.readBufferWriteCount[i] = new AtomicLong();
         this.readBufferDrainAtWriteCount[i] = new AtomicLong();
         this.readBuffers[i] = new AtomicReference[128];

         for (int j = 0; j < 128; j++) {
            this.readBuffers[i][j] = new AtomicReference<>();
         }
      }

      this.listener = builder.listener;
      this.pendingNotifications = this.listener == PrivateMaxEntriesMap.DiscardingListener.INSTANCE ? DISCARDING_QUEUE : new ConcurrentLinkedQueue<>();
   }

   static void checkNotNull(Object o) {
      if (o == null) {
         throw new NullPointerException();
      }
   }

   static void checkArgument(boolean expression) {
      if (!expression) {
         throw new IllegalArgumentException();
      }
   }

   static void checkState(boolean expression) {
      if (!expression) {
         throw new IllegalStateException();
      }
   }

   public long capacity() {
      return this.capacity.get();
   }

   public void setCapacity(long capacity) {
      checkArgument(capacity >= 0L);
      this.evictionLock.lock();

      try {
         this.capacity.lazySet(Math.min(capacity, 9223372034707292160L));
         this.drainBuffers();
         this.evict();
      } finally {
         this.evictionLock.unlock();
      }

      this.notifyListener();
   }

   boolean hasOverflowed() {
      return this.weightedSize.get() > this.capacity.get();
   }

   void evict() {
      while (this.hasOverflowed()) {
         PrivateMaxEntriesMap.Node<K, V> node = this.evictionDeque.poll();
         if (node == null) {
            return;
         }

         if (this.data.remove(node.key, node)) {
            this.pendingNotifications.add(node);
         }

         this.makeDead(node);
      }
   }

   void afterRead(PrivateMaxEntriesMap.Node<K, V> node) {
      int bufferIndex = readBufferIndex();
      long writeCount = this.recordRead(bufferIndex, node);
      this.drainOnReadIfNeeded(bufferIndex, writeCount);
      this.notifyListener();
   }

   static int readBufferIndex() {
      return (int)Thread.currentThread().getId() & READ_BUFFERS_MASK;
   }

   long recordRead(int bufferIndex, PrivateMaxEntriesMap.Node<K, V> node) {
      AtomicLong counter = this.readBufferWriteCount[bufferIndex];
      long writeCount = counter.get();
      counter.lazySet(writeCount + 1L);
      int index = (int)(writeCount & 127L);
      this.readBuffers[bufferIndex][index].lazySet(node);
      return writeCount;
   }

   void drainOnReadIfNeeded(int bufferIndex, long writeCount) {
      long pending = writeCount - this.readBufferDrainAtWriteCount[bufferIndex].get();
      boolean delayable = pending < 32L;
      PrivateMaxEntriesMap.DrainStatus status = this.drainStatus.get();
      if (status.shouldDrainBuffers(delayable)) {
         this.tryToDrainBuffers();
      }
   }

   void afterWrite(Runnable task) {
      this.writeBuffer.add(task);
      this.drainStatus.lazySet(PrivateMaxEntriesMap.DrainStatus.REQUIRED);
      this.tryToDrainBuffers();
      this.notifyListener();
   }

   void tryToDrainBuffers() {
      if (this.evictionLock.tryLock()) {
         try {
            this.drainStatus.lazySet(PrivateMaxEntriesMap.DrainStatus.PROCESSING);
            this.drainBuffers();
         } finally {
            this.drainStatus.compareAndSet(PrivateMaxEntriesMap.DrainStatus.PROCESSING, PrivateMaxEntriesMap.DrainStatus.IDLE);
            this.evictionLock.unlock();
         }
      }
   }

   void drainBuffers() {
      this.drainReadBuffers();
      this.drainWriteBuffer();
   }

   void drainReadBuffers() {
      int start = (int)Thread.currentThread().getId();
      int end = start + NUMBER_OF_READ_BUFFERS;

      for (int i = start; i < end; i++) {
         this.drainReadBuffer(i & READ_BUFFERS_MASK);
      }
   }

   void drainReadBuffer(int bufferIndex) {
      long writeCount = this.readBufferWriteCount[bufferIndex].get();

      for (int i = 0; i < 64; i++) {
         int index = (int)(this.readBufferReadCount[bufferIndex] & 127L);
         AtomicReference<PrivateMaxEntriesMap.Node<K, V>> slot = this.readBuffers[bufferIndex][index];
         PrivateMaxEntriesMap.Node<K, V> node = slot.get();
         if (node == null) {
            break;
         }

         slot.lazySet(null);
         this.applyRead(node);
         this.readBufferReadCount[bufferIndex]++;
      }

      this.readBufferDrainAtWriteCount[bufferIndex].lazySet(writeCount);
   }

   void applyRead(PrivateMaxEntriesMap.Node<K, V> node) {
      if (this.evictionDeque.contains(node)) {
         this.evictionDeque.moveToBack(node);
      }
   }

   void drainWriteBuffer() {
      for (int i = 0; i < 16; i++) {
         Runnable task = this.writeBuffer.poll();
         if (task == null) {
            break;
         }

         task.run();
      }
   }

   boolean tryToRetire(PrivateMaxEntriesMap.Node<K, V> node, PrivateMaxEntriesMap.WeightedValue<V> expect) {
      if (expect.isAlive()) {
         PrivateMaxEntriesMap.WeightedValue<V> retired = new PrivateMaxEntriesMap.WeightedValue<>(expect.value, -expect.weight);
         return node.compareAndSet(expect, retired);
      } else {
         return false;
      }
   }

   void makeRetired(PrivateMaxEntriesMap.Node<K, V> node) {
      PrivateMaxEntriesMap.WeightedValue<V> current;
      PrivateMaxEntriesMap.WeightedValue<V> retired;
      do {
         current = node.get();
         if (!current.isAlive()) {
            return;
         }

         retired = new PrivateMaxEntriesMap.WeightedValue<>(current.value, -current.weight);
      } while (!node.compareAndSet(current, retired));
   }

   void makeDead(PrivateMaxEntriesMap.Node<K, V> node) {
      PrivateMaxEntriesMap.WeightedValue<V> current;
      PrivateMaxEntriesMap.WeightedValue<V> dead;
      do {
         current = node.get();
         dead = new PrivateMaxEntriesMap.WeightedValue<>(current.value, 0);
      } while (!node.compareAndSet(current, dead));

      this.weightedSize.lazySet(this.weightedSize.get() - Math.abs(current.weight));
   }

   void notifyListener() {
      PrivateMaxEntriesMap.Node<K, V> node;
      while ((node = this.pendingNotifications.poll()) != null) {
         this.listener.onEviction(node.key, node.getValue());
      }
   }

   @Override
   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   @Override
   public int size() {
      return this.data.size();
   }

   public long weightedSize() {
      return Math.max(0L, this.weightedSize.get());
   }

   @Override
   public void clear() {
      this.evictionLock.lock();

      try {
         PrivateMaxEntriesMap.Node<K, V> node;
         while ((node = this.evictionDeque.poll()) != null) {
            this.data.remove(node.key, node);
            this.makeDead(node);
         }

         for (AtomicReference<PrivateMaxEntriesMap.Node<K, V>>[] buffer : this.readBuffers) {
            for (AtomicReference<PrivateMaxEntriesMap.Node<K, V>> slot : buffer) {
               slot.lazySet(null);
            }
         }

         Runnable task;
         while ((task = this.writeBuffer.poll()) != null) {
            task.run();
         }
      } finally {
         this.evictionLock.unlock();
      }
   }

   @Override
   public boolean containsKey(Object key) {
      return this.data.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      checkNotNull(value);

      for (PrivateMaxEntriesMap.Node<K, V> node : this.data.values()) {
         if (node.getValue().equals(value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public V get(Object key) {
      PrivateMaxEntriesMap.Node<K, V> node = this.data.get(key);
      if (node == null) {
         return null;
      }

      this.afterRead(node);
      return node.getValue();
   }

   public V getQuietly(Object key) {
      PrivateMaxEntriesMap.Node<K, V> node = this.data.get(key);
      return node == null ? null : node.getValue();
   }

   @Override
   public V put(K key, V value) {
      return this.put(key, value, false);
   }

   @Override
   public V putIfAbsent(K key, V value) {
      return this.put(key, value, true);
   }

   V put(K key, V value, boolean onlyIfAbsent) {
      checkNotNull(key);
      checkNotNull(value);
      int weight = 1;
      PrivateMaxEntriesMap.WeightedValue<V> weightedValue = new PrivateMaxEntriesMap.WeightedValue<>(value, 1);
      PrivateMaxEntriesMap.Node<K, V> node = new PrivateMaxEntriesMap.Node<>(key, weightedValue);

      label27:
      while (true) {
         PrivateMaxEntriesMap.Node<K, V> prior = this.data.putIfAbsent(node.key, node);
         if (prior == null) {
            this.afterWrite(new PrivateMaxEntriesMap.AddTask(node, 1));
            return null;
         }

         if (onlyIfAbsent) {
            this.afterRead(prior);
            return prior.getValue();
         }

         PrivateMaxEntriesMap.WeightedValue<V> oldWeightedValue;
         do {
            oldWeightedValue = prior.get();
            if (!oldWeightedValue.isAlive()) {
               continue label27;
            }
         } while (!prior.compareAndSet(oldWeightedValue, weightedValue));

         int weightedDifference = 1 - oldWeightedValue.weight;
         if (weightedDifference == 0) {
            this.afterRead(prior);
         } else {
            this.afterWrite(new PrivateMaxEntriesMap.UpdateTask(prior, weightedDifference));
         }

         return oldWeightedValue.value;
      }
   }

   @Override
   public V remove(Object key) {
      PrivateMaxEntriesMap.Node<K, V> node = this.data.remove(key);
      if (node == null) {
         return null;
      }

      this.makeRetired(node);
      this.afterWrite(new PrivateMaxEntriesMap.RemovalTask(node));
      return node.getValue();
   }

   @Override
   public boolean remove(Object key, Object value) {
      PrivateMaxEntriesMap.Node<K, V> node = this.data.get(key);
      if (node != null && value != null) {
         PrivateMaxEntriesMap.WeightedValue<V> weightedValue = node.get();

         while (weightedValue.contains(value)) {
            if (this.tryToRetire(node, weightedValue)) {
               if (this.data.remove(key, node)) {
                  this.afterWrite(new PrivateMaxEntriesMap.RemovalTask(node));
                  return true;
               }
               break;
            }

            weightedValue = node.get();
            if (!weightedValue.isAlive()) {
               break;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public V replace(K key, V value) {
      checkNotNull(key);
      checkNotNull(value);
      int weight = 1;
      PrivateMaxEntriesMap.WeightedValue<V> weightedValue = new PrivateMaxEntriesMap.WeightedValue<>(value, 1);
      PrivateMaxEntriesMap.Node<K, V> node = this.data.get(key);
      if (node == null) {
         return null;
      }

      PrivateMaxEntriesMap.WeightedValue<V> oldWeightedValue;
      do {
         oldWeightedValue = node.get();
         if (!oldWeightedValue.isAlive()) {
            return null;
         }
      } while (!node.compareAndSet(oldWeightedValue, weightedValue));

      int weightedDifference = 1 - oldWeightedValue.weight;
      if (weightedDifference == 0) {
         this.afterRead(node);
      } else {
         this.afterWrite(new PrivateMaxEntriesMap.UpdateTask(node, weightedDifference));
      }

      return oldWeightedValue.value;
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) {
      checkNotNull(key);
      checkNotNull(oldValue);
      checkNotNull(newValue);
      int weight = 1;
      PrivateMaxEntriesMap.WeightedValue<V> newWeightedValue = new PrivateMaxEntriesMap.WeightedValue<>(newValue, 1);
      PrivateMaxEntriesMap.Node<K, V> node = this.data.get(key);
      if (node == null) {
         return false;
      }

      PrivateMaxEntriesMap.WeightedValue<V> weightedValue;
      do {
         weightedValue = node.get();
         if (!weightedValue.isAlive() || !weightedValue.contains(oldValue)) {
            return false;
         }
      } while (!node.compareAndSet(weightedValue, newWeightedValue));

      int weightedDifference = 1 - weightedValue.weight;
      if (weightedDifference == 0) {
         this.afterRead(node);
      } else {
         this.afterWrite(new PrivateMaxEntriesMap.UpdateTask(node, weightedDifference));
      }

      return true;
   }

   @Override
   public Set<K> keySet() {
      Set<K> ks = this.keySet;
      return ks == null ? (this.keySet = new PrivateMaxEntriesMap.KeySet()) : ks;
   }

   public Set<K> ascendingKeySet() {
      return this.ascendingKeySetWithLimit(Integer.MAX_VALUE);
   }

   public Set<K> ascendingKeySetWithLimit(int limit) {
      return this.orderedKeySet(true, limit);
   }

   public Set<K> descendingKeySet() {
      return this.descendingKeySetWithLimit(Integer.MAX_VALUE);
   }

   public Set<K> descendingKeySetWithLimit(int limit) {
      return this.orderedKeySet(false, limit);
   }

   Set<K> orderedKeySet(boolean ascending, int limit) {
      checkArgument(limit >= 0);
      this.evictionLock.lock();

      try {
         this.drainBuffers();
         int initialCapacity = Math.min(limit, (int)this.weightedSize());
         Set<K> keys = new LinkedHashSet<>(initialCapacity);
         Iterator<PrivateMaxEntriesMap.Node<K, V>> iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();

         while (iterator.hasNext() && limit > keys.size()) {
            keys.add(iterator.next().key);
         }

         return Collections.unmodifiableSet(keys);
      } finally {
         this.evictionLock.unlock();
      }
   }

   @Override
   public Collection<V> values() {
      Collection<V> vs = this.values;
      return vs == null ? (this.values = new PrivateMaxEntriesMap.Values()) : vs;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      Set<Entry<K, V>> es = this.entrySet;
      return es == null ? (this.entrySet = new PrivateMaxEntriesMap.EntrySet()) : es;
   }

   public Map<K, V> ascendingMap() {
      return this.ascendingMapWithLimit(Integer.MAX_VALUE);
   }

   public Map<K, V> ascendingMapWithLimit(int limit) {
      return this.orderedMap(true, limit);
   }

   public Map<K, V> descendingMap() {
      return this.descendingMapWithLimit(Integer.MAX_VALUE);
   }

   public Map<K, V> descendingMapWithLimit(int limit) {
      return this.orderedMap(false, limit);
   }

   Map<K, V> orderedMap(boolean ascending, int limit) {
      checkArgument(limit >= 0);
      this.evictionLock.lock();

      try {
         this.drainBuffers();
         int initialCapacity = Math.min(limit, (int)this.weightedSize());
         Map<K, V> map = new LinkedHashMap<>(initialCapacity);
         Iterator<PrivateMaxEntriesMap.Node<K, V>> iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();

         while (iterator.hasNext() && limit > map.size()) {
            PrivateMaxEntriesMap.Node<K, V> node = iterator.next();
            map.put(node.key, node.getValue());
         }

         return Collections.unmodifiableMap(map);
      } finally {
         this.evictionLock.unlock();
      }
   }

   Object writeReplace() {
      return new PrivateMaxEntriesMap.SerializationProxy<>(this);
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   final class AddTask implements Runnable {
      final PrivateMaxEntriesMap.Node<K, V> node;
      final int weight;

      AddTask(PrivateMaxEntriesMap.Node<K, V> node, int weight) {
         this.weight = weight;
         this.node = node;
      }

      @Override
      public void run() {
         PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + this.weight);
         if (this.node.get().isAlive()) {
            PrivateMaxEntriesMap.this.evictionDeque.add(this.node);
            PrivateMaxEntriesMap.this.evict();
         }
      }
   }

   public static final class Builder<K, V> {
      static final int DEFAULT_CONCURRENCY_LEVEL = 16;
      static final int DEFAULT_INITIAL_CAPACITY = 16;
      EvictionListener<K, V> listener;
      int concurrencyLevel;
      int initialCapacity;
      long capacity = -1L;

      public Builder() {
         this.initialCapacity = 16;
         this.concurrencyLevel = 16;
         this.listener = PrivateMaxEntriesMap.DiscardingListener.INSTANCE;
      }

      public PrivateMaxEntriesMap.Builder<K, V> initialCapacity(int initialCapacity) {
         PrivateMaxEntriesMap.checkArgument(initialCapacity >= 0);
         this.initialCapacity = initialCapacity;
         return this;
      }

      public PrivateMaxEntriesMap.Builder<K, V> maximumCapacity(long capacity) {
         PrivateMaxEntriesMap.checkArgument(capacity >= 0L);
         this.capacity = capacity;
         return this;
      }

      public PrivateMaxEntriesMap.Builder<K, V> concurrencyLevel(int concurrencyLevel) {
         PrivateMaxEntriesMap.checkArgument(concurrencyLevel > 0);
         this.concurrencyLevel = concurrencyLevel;
         return this;
      }

      public PrivateMaxEntriesMap.Builder<K, V> listener(EvictionListener<K, V> listener) {
         PrivateMaxEntriesMap.checkNotNull(listener);
         this.listener = listener;
         return this;
      }

      public PrivateMaxEntriesMap<K, V> build() {
         PrivateMaxEntriesMap.checkState(this.capacity >= 0L);
         return new PrivateMaxEntriesMap<>(this);
      }
   }

   enum DiscardingListener implements EvictionListener<Object, Object> {
      INSTANCE;

      @Override
      public void onEviction(Object key, Object value) {
      }
   }

   static final class DiscardingQueue extends AbstractQueue<Object> {
      @Override
      public boolean add(Object e) {
         return true;
      }

      @Override
      public boolean offer(Object e) {
         return true;
      }

      @Override
      public Object poll() {
         return null;
      }

      @Override
      public Object peek() {
         return null;
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public Iterator<Object> iterator() {
         return Collections.<Object>emptyList().iterator();
      }
   }

   enum DrainStatus {
      IDLE {
         @Override
         boolean shouldDrainBuffers(boolean delayable) {
            return !delayable;
         }
      },
      REQUIRED {
         @Override
         boolean shouldDrainBuffers(boolean delayable) {
            return true;
         }
      },
      PROCESSING {
         @Override
         boolean shouldDrainBuffers(boolean delayable) {
            return false;
         }
      };

      DrainStatus() {
      }

      abstract boolean shouldDrainBuffers(boolean var1);
   }

   final class EntryIterator implements Iterator<Entry<K, V>> {
      final Iterator<PrivateMaxEntriesMap.Node<K, V>> iterator = PrivateMaxEntriesMap.this.data.values().iterator();
      PrivateMaxEntriesMap.Node<K, V> current;

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Entry<K, V> next() {
         this.current = this.iterator.next();
         return PrivateMaxEntriesMap.this.new WriteThroughEntry(this.current);
      }

      @Override
      public void remove() {
         PrivateMaxEntriesMap.checkState(this.current != null);
         PrivateMaxEntriesMap.this.remove(this.current.key);
         this.current = null;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object next() {
         return this.next();
      }
   }

   final class EntrySet extends AbstractSet<Entry<K, V>> {
      final PrivateMaxEntriesMap<K, V> map = PrivateMaxEntriesMap.this;

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public void clear() {
         this.map.clear();
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return PrivateMaxEntriesMap.this.new EntryIterator();
      }

      @Override
      public boolean contains(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         Entry<?, ?> entry = (Entry<?, ?>)obj;
         PrivateMaxEntriesMap.Node<K, V> node = this.map.data.get(entry.getKey());
         return node != null && node.getValue().equals(entry.getValue());
      }

      public boolean add(Entry<K, V> entry) {
         throw new UnsupportedOperationException("ConcurrentLinkedHashMap does not allow add to be called on entrySet()");
      }

      @Override
      public boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         Entry<?, ?> entry = (Entry<?, ?>)obj;
         return this.map.remove(entry.getKey(), entry.getValue());
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public boolean add(Object var1) {
         return this.add((Entry<K, V>)var1);
      }
   }

   final class KeyIterator implements Iterator<K> {
      final Iterator<K> iterator = PrivateMaxEntriesMap.this.data.keySet().iterator();
      Object current;

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Override
      public K next() {
         this.current = this.iterator.next();
         return (K)this.current;
      }

      @Override
      public void remove() {
         PrivateMaxEntriesMap.checkState(this.current != null);
         PrivateMaxEntriesMap.this.remove(this.current);
         this.current = null;
      }
   }

   final class KeySet extends AbstractSet<K> {
      final PrivateMaxEntriesMap<K, V> map = PrivateMaxEntriesMap.this;

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public void clear() {
         this.map.clear();
      }

      @Override
      public Iterator<K> iterator() {
         return PrivateMaxEntriesMap.this.new KeyIterator();
      }

      @Override
      public boolean contains(Object obj) {
         return PrivateMaxEntriesMap.this.containsKey(obj);
      }

      @Override
      public boolean remove(Object obj) {
         return this.map.remove(obj) != null;
      }

      @Override
      public Object[] toArray() {
         return this.map.data.keySet().toArray();
      }

      @Override
      public <T> T[] toArray(T[] array) {
         return (T[])this.map.data.keySet().toArray(array);
      }
   }

   static final class Node<K, V> extends AtomicReference<PrivateMaxEntriesMap.WeightedValue<V>> implements Linked<PrivateMaxEntriesMap.Node<K, V>> {
      final K key;
      PrivateMaxEntriesMap.Node<K, V> prev;
      PrivateMaxEntriesMap.Node<K, V> next;

      Node(K key, PrivateMaxEntriesMap.WeightedValue<V> weightedValue) {
         super(weightedValue);
         this.key = key;
      }

      public PrivateMaxEntriesMap.Node<K, V> getPrevious() {
         return this.prev;
      }

      public void setPrevious(PrivateMaxEntriesMap.Node<K, V> prev) {
         this.prev = prev;
      }

      public PrivateMaxEntriesMap.Node<K, V> getNext() {
         return this.next;
      }

      public void setNext(PrivateMaxEntriesMap.Node<K, V> next) {
         this.next = next;
      }

      V getValue() {
         return this.get().value;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public void setNext(Linked var1) {
         this.setNext((PrivateMaxEntriesMap.Node<K, V>)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Linked getNext() {
         return this.getNext();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public void setPrevious(Linked var1) {
         this.setPrevious((PrivateMaxEntriesMap.Node<K, V>)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Linked getPrevious() {
         return this.getPrevious();
      }
   }

   final class RemovalTask implements Runnable {
      final PrivateMaxEntriesMap.Node<K, V> node;

      RemovalTask(PrivateMaxEntriesMap.Node<K, V> node) {
         this.node = node;
      }

      @Override
      public void run() {
         PrivateMaxEntriesMap.this.evictionDeque.remove(this.node);
         PrivateMaxEntriesMap.this.makeDead(this.node);
      }
   }

   static final class SerializationProxy<K, V> implements Serializable {
      final EvictionListener<K, V> listener;
      final int concurrencyLevel;
      final Map<K, V> data;
      final long capacity;
      static final long serialVersionUID = 1L;

      SerializationProxy(PrivateMaxEntriesMap<K, V> map) {
         this.concurrencyLevel = map.concurrencyLevel;
         this.data = new HashMap<>(map);
         this.capacity = map.capacity.get();
         this.listener = map.listener;
      }

      Object readResolve() {
         PrivateMaxEntriesMap<K, V> map = new PrivateMaxEntriesMap.Builder<K, V>().maximumCapacity(this.capacity).build();
         map.putAll(this.data);
         return map;
      }
   }

   final class UpdateTask implements Runnable {
      final int weightDifference;
      final PrivateMaxEntriesMap.Node<K, V> node;

      UpdateTask(PrivateMaxEntriesMap.Node<K, V> node, int weightDifference) {
         this.weightDifference = weightDifference;
         this.node = node;
      }

      @Override
      public void run() {
         PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + this.weightDifference);
         PrivateMaxEntriesMap.this.applyRead(this.node);
         PrivateMaxEntriesMap.this.evict();
      }
   }

   final class ValueIterator implements Iterator<V> {
      final Iterator<PrivateMaxEntriesMap.Node<K, V>> iterator = PrivateMaxEntriesMap.this.data.values().iterator();
      PrivateMaxEntriesMap.Node<K, V> current;

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Override
      public V next() {
         this.current = this.iterator.next();
         return this.current.getValue();
      }

      @Override
      public void remove() {
         PrivateMaxEntriesMap.checkState(this.current != null);
         PrivateMaxEntriesMap.this.remove(this.current.key);
         this.current = null;
      }
   }

   final class Values extends AbstractCollection<V> {
      @Override
      public int size() {
         return PrivateMaxEntriesMap.this.size();
      }

      @Override
      public void clear() {
         PrivateMaxEntriesMap.this.clear();
      }

      @Override
      public Iterator<V> iterator() {
         return PrivateMaxEntriesMap.this.new ValueIterator();
      }

      @Override
      public boolean contains(Object o) {
         return PrivateMaxEntriesMap.this.containsValue(o);
      }
   }

   static final class WeightedValue<V> {
      final int weight;
      final V value;

      WeightedValue(V value, int weight) {
         this.weight = weight;
         this.value = value;
      }

      boolean contains(Object o) {
         return o == this.value || this.value.equals(o);
      }

      boolean isAlive() {
         return this.weight > 0;
      }

      boolean isRetired() {
         return this.weight < 0;
      }

      boolean isDead() {
         return this.weight == 0;
      }
   }

   final class WriteThroughEntry extends SimpleEntry<K, V> {
      static final long serialVersionUID = 1L;

      WriteThroughEntry(PrivateMaxEntriesMap.Node<K, V> node) {
         super(node.key, node.getValue());
      }

      @Override
      public V setValue(V value) {
         PrivateMaxEntriesMap.this.put((K)this.getKey(), value);
         return (V)super.setValue(value);
      }

      Object writeReplace() {
         return new SimpleEntry<>(this);
      }
   }
}
