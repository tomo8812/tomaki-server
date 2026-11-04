package org.valkyrienskies.eureka.util

import kotlin.reflect.KProperty
import net.minecraft.class_3913

public interface IKtContainerData : class_3913 {
   public abstract operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ContainerDataDelegate {
   }
}
