package de.maxhenkel.gravestone.corelib.death;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.Event;

public class PlayerDeathEvent extends Event {
   private Death death;
   private ServerPlayer player;
   private DamageSource source;
   private boolean storeDeath;
   private boolean removeDrops;

   public PlayerDeathEvent(Death death, ServerPlayer player, DamageSource source) {
      this.death = death;
      this.player = player;
      this.source = source;
   }

   public void storeDeath() {
      this.storeDeath = true;
   }

   public void removeDrops() {
      this.removeDrops = true;
   }

   public Death getDeath() {
      return this.death;
   }

   public ServerPlayer getPlayer() {
      return this.player;
   }

   public DamageSource getSource() {
      return this.source;
   }

   boolean isStoreDeath() {
      return this.storeDeath;
   }

   boolean isRemoveDrops() {
      return this.removeDrops;
   }
}
