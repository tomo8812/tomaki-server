package org.valkyrienskies.core.api.bodies.properties

import kotlin.contracts.InvocationKind
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder

@VsBeta
public inline fun BodyTransform.rebuild(block: (Builder) -> Unit): BodyTransform {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   val var3: BodyTransform.Builder = `$this$rebuild`.toBuilder()
   block(var3)
   return var3.build()
}
