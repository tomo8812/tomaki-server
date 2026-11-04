package org.valkyrienskies.mod.common.config

import java.util.ArrayList
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_304

@SourceDebugExtension(["SMAP\nVSKeyBindings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSKeyBindings.kt\norg/valkyrienskies/mod/common/config/VSKeyBindings\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,46:1\n1#2:47\n1863#3,2:48\n*S KotlinDebug\n*F\n+ 1 VSKeyBindings.kt\norg/valkyrienskies/mod/common/config/VSKeyBindings\n*L\n39#1:48,2\n*E\n"])
public object VSKeyBindings {
   private final val toBeRegistered: MutableList<Consumer<Consumer<class_304>>> = ArrayList() as java.util.List
   public final val shipDown: Supplier<class_304> = INSTANCE.register("key.valkyrienskies.ship_down", 86, "category.valkyrienskies.driving")
   public final val shipCruise: Supplier<class_304> = INSTANCE.register("key.valkyrienskies.ship_cruise", 67, "category.valkyrienskies.driving")
   public final val shipSeat: Supplier<class_304> = INSTANCE.register("key.valkyrienskies.ship_seat", 88, "category.valkyrienskies.driving")

   private fun register(name: String, keyCode: Int, category: String): Supplier<class_304> {
      val var4: <unrepresentable> = object : Supplier<class_304>, Consumer<Consumer<class_304>> {
         public final lateinit var registered: class_304

         public open fun get(): class_304 {
            return this.registered
         }

         public open fun accept(t: Consumer<class_304>) {
            this.registered = class_304(name, keyCode, category)
            t.accept(this.registered)
         }

         // $VF: bridge methodSupplier
         @JvmSynthetic
         override fun get() {
            this.get()
         }

         // $VF: bridge methodSupplier
         @JvmSynthetic
         override fun accept(p0: Any) {
            this.accept(p0 as Consumer<class_304>)
         }
      }
      toBeRegistered.add(var4)
      return var4
   }

   public fun clientSetup(registerar: Consumer<class_304>) {
      for (`element$iv` in toBeRegistered) {
         (`element$iv` as Consumer).accept(registerar)
      }
   }

   public fun isKeyMappingFromVS2(keyMapping: class_304): Boolean {
      val var10000: java.lang.String = keyMapping.method_1431()
      return StringsKt.startsWith$default(var10000, "key.valkyrienskies", false, 2, null)
   }
}
