package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.platform.EmiAgnos;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import net.minecraft.network.chat.Component;

public enum FluidUnit implements ConfigEnum {
   LITERS("liters", a -> EmiPort.translatable("emi.fluid.amount.liters", EmiRenderHelper.TEXT_FORMAT.format((int)(a / literDivisor())))),
   MILLIBUCKETS("millibuckets", a -> EmiPort.translatable("emi.fluid.amount.millibuckets", EmiRenderHelper.TEXT_FORMAT.format((int)(a / literDivisor())))),
   DROPLETS("droplets", a -> EmiPort.translatable("emi.fluid.amount.droplets", EmiRenderHelper.TEXT_FORMAT.format((int)a)));

   public static final int BUCKET = EmiAgnos.isForge() ? 1000 : 81000;
   public static final int BOTTLE = EmiAgnos.isForge() ? 250 : 27000;
   private final String name;
   private final Component translation;
   private final Double2ObjectFunction<Component> translator;

   FluidUnit(String name, Double2ObjectFunction<Component> translator) {
      this.name = name;
      this.translation = EmiPort.translatable("emi.unit." + name);
      this.translator = translator;
   }

   public static int literDivisor() {
      return EmiAgnos.isForge() ? 1 : 81;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public Component translate(double amount) {
      return (Component)this.translator.apply(amount);
   }

   @Override
   public Component getText() {
      return this.translation;
   }

   // $VF: synthetic method
   private static FluidUnit[] $values() {
      return new FluidUnit[]{LITERS, MILLIBUCKETS, DROPLETS};
   }
}
