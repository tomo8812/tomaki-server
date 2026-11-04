package net.satisfy.bakery.core.recipe;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum JamConsistency implements StringRepresentable {
   RUNNY("runny", ChatFormatting.AQUA),
   PERFECT("perfect", ChatFormatting.GOLD),
   CARAMELIZED("caramelized", ChatFormatting.RED);

   public static final Codec<JamConsistency> CODEC = StringRepresentable.fromEnum(JamConsistency::values);
   public static final StreamCodec<ByteBuf, JamConsistency> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(index -> values()[index], Enum::ordinal);
   private final String name;
   private final ChatFormatting color;

   JamConsistency(String name, ChatFormatting color) {
      this.name = name;
      this.color = color;
   }

   public ChatFormatting color() {
      return this.color;
   }

   public String translationKey() {
      return "tooltip.bakery.jam_consistency." + this.name;
   }

   @NotNull
   public String getSerializedName() {
      return this.name;
   }

   // $VF: synthetic method
   private static JamConsistency[] $values() {
      return new JamConsistency[]{RUNNY, PERFECT, CARAMELIZED};
   }
}
