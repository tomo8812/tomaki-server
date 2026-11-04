package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.util.Formatting;
import com.kishku7.chunksmith.util.Input;
import java.util.List;
import java.util.Optional;

public class CornersCommand implements ChunksmithCommand {
   private final Chunksmith chunky;

   public CornersCommand(Chunksmith chunky) {
      this.chunky = chunky;
   }

   @Override
   public void execute(Sender sender, CommandArguments arguments) {
      Optional<Double> x1 = arguments.next().flatMap(Input::tryDoubleSuffixed);
      Optional<Double> z1 = arguments.next().flatMap(Input::tryDoubleSuffixed);
      Optional<Double> x2 = arguments.next().flatMap(Input::tryDoubleSuffixed);
      Optional<Double> z2 = arguments.next().flatMap(Input::tryDoubleSuffixed);
      if (x1.isEmpty() || z1.isEmpty() || x2.isEmpty() || z2.isEmpty()) {
         sender.sendMessage("help_corners");
      } else if (!Input.isPastWorldLimit(x1.get())
         && !Input.isPastWorldLimit(z1.get())
         && !Input.isPastWorldLimit(x2.get())
         && !Input.isPastWorldLimit(z2.get())) {
         double centerX = (x1.get() + x2.get()) / 2.0;
         double centerZ = (z1.get() + z2.get()) / 2.0;
         double radiusX = Math.abs(x1.get() - x2.get()) / 2.0;
         double radiusZ = Math.abs(z1.get() - z2.get()) / 2.0;
         this.chunky.getSelection().center(centerX, centerZ).radiusX(radiusX).radiusZ(radiusZ);
         sender.sendMessagePrefixed("format_center", Formatting.number(centerX), Formatting.number(centerZ));
         String shape;
         if (radiusX == radiusZ) {
            sender.sendMessagePrefixed("format_radius", Formatting.number(radiusX));
            shape = "square";
         } else {
            sender.sendMessagePrefixed("format_radii", Formatting.number(radiusX), Formatting.number(radiusZ));
            shape = "rectangle";
         }

         this.chunky.getSelection().shape(shape);
         sender.sendMessagePrefixed("format_shape", shape);
      } else {
         sender.sendMessage("help_corners");
      }
   }

   @Override
   public List<String> suggestions(CommandArguments arguments) {
      return List.of();
   }
}
