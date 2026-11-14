package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.integration.BorderIntegration;
import com.kishku7.chunksmith.integration.Integration;
import com.kishku7.chunksmith.platform.Border;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.platform.util.Vector2;
import com.kishku7.chunksmith.util.Formatting;
import com.kishku7.chunksmith.util.Input;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WorldBorderCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public WorldBorderCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      if (arguments.size() > 0) {
         Optional<World> world = arguments.next().flatMap(arg -> Input.tryWorld(this.chunky, arg));
         if (!world.isPresent()) {
            sender.sendMessage("help_worldborder");
            return;
         }

         this.chunky.getSelection().world(world.get());
      }

      Selection previous = this.chunky.getSelection().build();
      if (!this.setBorderViaIntegration(previous.world())) {
         this.chunky.getSelection().worldborder();
      }

      Selection current = this.chunky.getSelection().build();
      sender.sendMessagePrefixed("format_center", Formatting.number(current.centerX()), Formatting.number(current.centerZ()));
      if (current.radiusX() == current.radiusZ()) {
         sender.sendMessagePrefixed("format_radius", Formatting.number(current.radiusX()));
      } else {
         sender.sendMessagePrefixed("format_radii", Formatting.number(current.radiusX()), Formatting.number(current.radiusZ()));
      }

      if (!previous.shape().equals(current.shape())) {
         sender.sendMessagePrefixed("format_shape", current.shape());
      }

      if (current.radiusX() > this.chunky.getServer().getMaxWorldSize()) {
         sender.sendMessagePrefixed("format_worldborder_too_large", this.chunky.getServer().getMaxWorldSize());
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      if (arguments.size() == 1) {
         List<String> suggestions = new ArrayList<>();
         this.chunky.getServer().getWorlds().forEach(world -> suggestions.add(world.getName()));
         return suggestions;
      } else {
         return List.of();
      }
   }

   boolean setBorderViaIntegration(World world) {
      Map<String, Integration> integrations = this.chunky.getServer().getIntegrations();
      if (integrations.containsKey("border")) {
         BorderIntegration worldborder = (BorderIntegration)integrations.get("border");
         String worldName = world.getName();
         if (worldborder.hasBorder(worldName)) {
            Border border = worldborder.getBorder(worldName);
            Vector2 center = border.getCenter();
            this.chunky.getSelection().center(center.getX(), center.getZ()).radiusX(border.getRadiusX()).radiusZ(border.getRadiusZ()).shape(border.getShape());
            return true;
         }
      }

      return false;
   }
}
