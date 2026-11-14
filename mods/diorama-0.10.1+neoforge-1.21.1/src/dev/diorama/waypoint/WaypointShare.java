package dev.diorama.waypoint;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.ClickEvent.Action;
import org.jspecify.annotations.Nullable;

public final class WaypointShare {
   private static final Pattern SHARED = Pattern.compile(
      "xaero-waypoint:([^:]{1,160}):([^:]{0,20}):(-?\\d{1,9}):(~|-?\\d{1,5}):(-?\\d{1,9}):(\\d{1,2}):(?:true|false):-?\\d{1,5}:(Internal-[^\\s]{1,200}-waypoints)"
   );
   private static final int MAX_HORIZONTAL = 30000000;
   private static final int MAX_VERTICAL = 4096;
   private static final int MAX_CHAT = 256;
   private static final int MAX_NAME = 32;
   private static final Pattern DIMENSION_ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+");

   private WaypointShare() {
   }

   public static Optional<String> format(Waypoint waypoint) {
      String initials = encode(chatSafe(waypoint.label()));
      String tail = ":"
         + initials
         + ":"
         + waypoint.x
         + ":"
         + waypoint.y
         + ":"
         + waypoint.z
         + ":"
         + colorIndex(waypoint.color)
         + ":false:0:Internal-"
         + xaeroDimension(waypoint.dimension)
         + "-waypoints";
      String name = chatSafe(waypoint.shownName()).trim();
      if (name.isEmpty()) {
         name = "Waypoint";
      }

      String encoded;
      for (encoded = encode(name); "xaero-waypoint:".length() + encoded.length() + tail.length() > 256 && name.length() > 1; encoded = encode(name)) {
         name = cut(name, name.length() - 1);
      }

      String line = "xaero-waypoint:" + encoded + tail;
      return line.length() > 256 ? Optional.empty() : Optional.of(line);
   }

   public static boolean send(Waypoint waypoint) {
      Minecraft mc = Minecraft.getInstance();
      Optional<String> text = format(waypoint);
      if (text.isEmpty()) {
         mc.gui.getChat().addMessage(Component.translatable("diorama.share.too_long"));
         return false;
      }

      if (mc.player != null) {
         mc.player.connection.sendChat(text.get());
      }

      return true;
   }

   static String encode(String text) {
      return text.replace("-", "^min^").replace("_", "-").replace(":", "^col^").replace("*", "^ast^");
   }

   static String decode(String text) {
      return text.replace("^ast^", "*").replace("^col^", ":").replace("-", "_").replace("^min^", "-");
   }

   static String chatSafe(String text) {
      StringBuilder out = new StringBuilder();
      text.codePoints().filter(c -> c >= 32 && c != 167 && c != 127).forEach(out::appendCodePoint);
      return out.toString();
   }

   public static Optional<WaypointShare.Shared> parse(String text) {
      Matcher m = SHARED.matcher(text);
      if (!m.find()) {
         return Optional.empty();
      }

      String name = cut(chatSafe(decode(m.group(1))).trim(), 32).trim();
      StringBuilder initials = new StringBuilder();
      decode(m.group(2)).codePoints().filter(Character::isLetterOrDigit).limit(4L).forEach(initials::appendCodePoint);
      int x = Integer.parseInt(m.group(3));
      int z = Integer.parseInt(m.group(5));
      Integer y = "~".equals(m.group(4)) ? null : Integer.parseInt(m.group(4));
      int color = Integer.parseInt(m.group(6));
      String xaero = m.group(7).substring("Internal-".length(), m.group(7).length() - "-waypoints".length());
      String dimension = ourDimension(xaero);
      return !name.isEmpty() && Math.abs(x) <= 30000000 && Math.abs(z) <= 30000000 && (y == null || Math.abs(y) <= 4096) && color <= 15 && dimension != null
         ? Optional.of(new WaypointShare.Shared(name, initials.toString(), x, y, z, color, dimension))
         : Optional.empty();
   }

   static String cut(String text, int max) {
      if (text.length() <= max) {
         return text;
      }

      int end = Character.isHighSurrogate(text.charAt(max - 1)) ? max - 1 : max;
      return text.substring(0, end);
   }

   public static Component withAddLink(Component message) {
      Optional<WaypointShare.Shared> shared = parse(message.getString());
      if (shared.isEmpty()) {
         return message;
      }

      Minecraft mc = Minecraft.getInstance();
      int y = shared.get().y() != null ? shared.get().y() : (mc.player != null ? mc.player.getBlockY() : 64);
      return message.copy().append(" ").append(addLink(shared.get(), y));
   }

   public static String addCommand(WaypointShare.Shared shared, int y) {
      return "/diorama waypoint add "
         + shared.x()
         + " "
         + y
         + " "
         + shared.z()
         + " "
         + shared.colorIndex()
         + " \""
         + shared.dimension()
         + "\" \""
         + shared.initials()
         + "\" "
         + shared.name();
   }

   private static Component addLink(WaypointShare.Shared shared, int y) {
      String command = addCommand(shared, y);
      return Component.translatable("diorama.share.add")
         .withStyle(
            style -> style.withColor(ChatFormatting.GREEN)
               .withClickEvent(new ClickEvent(Action.RUN_COMMAND, command))
               .withHoverEvent(
                  new HoverEvent(
                     net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                     Component.translatable("diorama.share.add_hover", new Object[]{shared.name(), shared.x(), y, shared.z()})
                  )
               )
         );
   }

   public static <S> void register(CommandDispatcher<S> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)LiteralArgumentBuilder.literal("diorama")
            .then(
               ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal("waypoint").then(LiteralArgumentBuilder.literal("list").executes(WaypointShare::list)))
                  .then(
                     LiteralArgumentBuilder.literal("add")
                        .then(
                           RequiredArgumentBuilder.argument("x", IntegerArgumentType.integer())
                              .then(
                                 RequiredArgumentBuilder.argument("y", IntegerArgumentType.integer())
                                    .then(
                                       RequiredArgumentBuilder.argument("z", IntegerArgumentType.integer())
                                          .then(
                                             RequiredArgumentBuilder.argument("colour", IntegerArgumentType.integer(0, 15))
                                                .then(
                                                   RequiredArgumentBuilder.argument("dimension", StringArgumentType.string())
                                                      .then(
                                                         RequiredArgumentBuilder.argument("initials", StringArgumentType.string())
                                                            .then(
                                                               RequiredArgumentBuilder.argument("name", StringArgumentType.greedyString())
                                                                  .executes(WaypointShare::add)
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static <S> int list(CommandContext<S> context) {
      Minecraft mc = Minecraft.getInstance();
      mc.tell(() -> mc.setScreen(new WaypointListScreen(null)));
      return 1;
   }

   private static <S> int add(CommandContext<S> context) {
      String name = StringArgumentType.getString(context, "name");
      Waypoint waypoint = new Waypoint(
         cut(name, 32),
         IntegerArgumentType.getInteger(context, "x"),
         IntegerArgumentType.getInteger(context, "y"),
         IntegerArgumentType.getInteger(context, "z"),
         StringArgumentType.getString(context, "dimension"),
         paletteColor(IntegerArgumentType.getInteger(context, "colour"))
      );
      String initials = StringArgumentType.getString(context, "initials");
      if (!initials.isEmpty()) {
         waypoint.initials = cut(initials, 4);
      }

      Minecraft mc = Minecraft.getInstance();
      mc.tell(() -> mc.setScreen(new WaypointEditScreen(null, waypoint, true)));
      return 1;
   }

   static int colorIndex(int argb) {
      int best = 15;
      long bestDistance = Long.MAX_VALUE;

      for (int i = 0; i < 16; i++) {
         int chat = TextColor.fromLegacyFormat(ChatFormatting.values()[i]).getValue();
         long dr = (argb >> 16 & 0xFF) - (chat >> 16 & 0xFF);
         long dg = (argb >> 8 & 0xFF) - (chat >> 8 & 0xFF);
         long db = (argb & 0xFF) - (chat & 0xFF);
         long distance = dr * dr + dg * dg + db * db;
         if (distance < bestDistance) {
            bestDistance = distance;
            best = i;
         }
      }

      return best;
   }

   static int paletteColor(int index) {
      return nearestPaletteColor(chatColor(index));
   }

   static int chatColor(int index) {
      return TextColor.fromLegacyFormat(ChatFormatting.values()[Math.max(0, Math.min(15, index))]).getValue();
   }

   static int nearestPaletteColor(int rgb) {
      int best = Waypoint.PALETTE[0];
      long bestDistance = Long.MAX_VALUE;

      for (int color : Waypoint.PALETTE) {
         long dr = (color >> 16 & 0xFF) - (rgb >> 16 & 0xFF);
         long dg = (color >> 8 & 0xFF) - (rgb >> 8 & 0xFF);
         long db = (color & 0xFF) - (rgb & 0xFF);
         long distance = dr * dr + dg * dg + db * db;
         if (distance < bestDistance) {
            bestDistance = distance;
            best = color;
         }
      }

      return best;
   }

   static String xaeroDimension(String id) {
      return switch (id) {
         case "minecraft:overworld" -> "overworld";
         case "minecraft:the_nether" -> "the-nether";
         case "minecraft:the_end" -> "the-end";
         default -> "dim%" + id.replace(':', '$').replace('/', '%');
      };
   }

   static @Nullable String ourDimension(String xaero) {
      String id = switch (xaero.toLowerCase(Locale.ROOT)) {
         case "overworld" -> "minecraft:overworld";
         case "the-nether" -> "minecraft:the_nether";
         case "the-end" -> "minecraft:the_end";
         default -> xaero.startsWith("dim%") ? xaero.substring(4).replace('$', ':').replace('%', '/') : null;
      };
      return id != null && DIMENSION_ID.matcher(id).matches() ? id : null;
   }

   public record Shared(String name, String initials, int x, Integer y, int z, int colorIndex, String dimension) {
   }
}
