package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.platform.util.Location;
import com.kishku7.chunksmith.util.Translator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class NeoForgeSender implements Sender {
   private final CommandSourceStack source;

   public NeoForgeSender(CommandSourceStack source) {
      this.source = source;
   }

   @Override
   public boolean isPlayer() {
      return this.source.getEntity() instanceof ServerPlayer;
   }

   @Override
   public String getName() {
      return this.source.getTextName();
   }

   @Override
   public World getWorld() {
      return new NeoForgeWorld(this.source.getLevel());
   }

   @Override
   public Location getLocation() {
      Vec3 pos = this.source.getPosition();
      Vec2 rot = this.source.getRotation();
      return new Location(this.getWorld(), pos.x(), pos.y(), pos.z(), rot.x, rot.y);
   }

   @Override
   public boolean hasPermission(String permission) {
      return this.source.hasPermission(2);
   }

   @Override
   public void sendMessage(String key, boolean prefixed, Object... args) {
      this.source.sendSuccess(() -> Component.nullToEmpty(Translator.translateKey(key, prefixed, args).replaceAll("&[0-9a-fk-orA-FK-OR]", "")), false);
   }
}
