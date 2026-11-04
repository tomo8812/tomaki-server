package de.maxhenkel.gravestone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class DeathInfo {
   public static final Codec<DeathInfo> CODEC = RecordCodecBuilder.create(
      i -> i.group(UUIDUtil.CODEC.fieldOf("PlayerUUID").forGetter(DeathInfo::getPlayerId), UUIDUtil.CODEC.fieldOf("DeathID").forGetter(DeathInfo::getDeathId))
         .apply(i, DeathInfo::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, DeathInfo> STREAM_CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC, DeathInfo::getPlayerId, UUIDUtil.STREAM_CODEC, DeathInfo::getDeathId, DeathInfo::new
   );
   private final UUID playerId;
   private final UUID deathId;

   public DeathInfo(UUID playerId, UUID deathId) {
      this.playerId = playerId;
      this.deathId = deathId;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public UUID getDeathId() {
      return this.deathId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DeathInfo deathInfo = (DeathInfo)o;
         return Objects.equals(this.playerId, deathInfo.playerId) && Objects.equals(this.deathId, deathInfo.deathId);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = Objects.hashCode(this.playerId);
      return 31 * result + Objects.hashCode(this.deathId);
   }
}
