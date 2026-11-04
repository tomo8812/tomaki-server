package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.corelib.config.ConfigBase;
import de.maxhenkel.gravestone.corelib.tag.Tag;
import de.maxhenkel.gravestone.corelib.tag.TagUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ServerConfig extends ConfigBase {
   public final BooleanValue giveObituaries;
   public final ConfigValue<List<? extends String>> replaceableBlocksSpec;
   public final BooleanValue removeObituary;
   public final BooleanValue onlyOwnersCanBreak;
   public final BooleanValue spawnGhost;
   public final BooleanValue friendlyGhost;
   public final BooleanValue sneakPickup;
   public final BooleanValue breakPickup;
   public final BooleanValue strictPlacement;
   public List<Tag<Block>> replaceableBlocks = new ArrayList<>();

   public ServerConfig(Builder builder) {
      super(builder);
      this.giveObituaries = builder.comment("If this is set to true you get an obituary after you died").define("enable_obituary", true);
      this.replaceableBlocksSpec = builder.comment(new String[]{"The blocks that can be replaced with a grave", "If it starts with '#' it is a tag"})
         .defineList("replaceable_blocks", Collections.singletonList("#gravestone:grave_replaceable"), Objects::nonNull);
      this.removeObituary = builder.comment("If this is set to true the obituary will be taken out of your inventory when you break the grave")
         .define("remove_obituary", false);
      this.onlyOwnersCanBreak = builder.comment("If this is set to true only the player that owns the grave and admins can break the grave")
         .define("only_owners_can_break", false);
      this.spawnGhost = builder.comment("If this is set to true the ghost of the dead player will be spawned when the grave is broken")
         .define("spawn_ghost", false);
      this.friendlyGhost = builder.comment("If this is set to true the ghost player will defend the player").define("friendly_ghost", true);
      this.sneakPickup = builder.comment("If this is set to true you get your items back into your inventory by sneaking on the grave")
         .define("sneak_pickup", false);
      this.breakPickup = builder.comment("If this is set to true you get your items sorted back into your inventory by breaking the grave")
         .define("break_pickup", true);
      this.strictPlacement = builder.comment(
            new String[]{
               "If this is set to true the grave will replace other blocks if there is no free space above",
               "Note that this might cause issues with other mods or multiblock structures - This option is not recommended and subject to change"
            }
         )
         .define("strict_placement", false);
   }

   @Override
   public void onReload(Reloading event) {
      super.onReload(event);
      this.onConfigChange();
   }

   @Override
   public void onLoad(Loading event) {
      super.onLoad(event);
      this.onConfigChange();
   }

   private void onConfigChange() {
      this.replaceableBlocks = ((List)this.replaceableBlocksSpec.get()).stream().map(TagUtils::getBlock).filter(Objects::nonNull).collect(Collectors.toList());
   }
}
