package org.valkyrienskies.mod.common

public object VS2ChunkAllocator {
   public fun isChunkInShipyardCompanion(chunkX: Int, chunkZ: Int): Boolean {
      return vsCore.isChunkInShipyard(chunkX, chunkZ)
   }
}
