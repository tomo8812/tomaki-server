package org.valkyrienskies.core.impl.game.ships.serialization

public class ChainUpdaterImpl<T>(updateTo: Class<Any>, mappings: Pair<Class<*>, DtoUpdater<*, *>>) : ChainUpdater<T> {
   public final val updateTo: Class<Any>
   private final val mappings: Map<Class<*>, DtoUpdater<*, *>>

   init {
      this.updateTo = updateTo
      this.mappings = MapsKt.toMap(mappings)
   }

   public override fun updateToLatest(value: Any): Any {
      value = value

      while (!this.updateTo.isInstance(value)) {
         val var2: Class = value.getClass()
         val var10000: DtoUpdater = this.mappings.get(var2)
         if (var10000 == null) {
            throw IllegalArgumentException("Could not find updater for type $var2")
         }

         value = var10000.update(value)
      }

      return (T)value
   }
}
