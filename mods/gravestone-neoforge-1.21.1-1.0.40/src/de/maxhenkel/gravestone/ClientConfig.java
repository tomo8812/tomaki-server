package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.corelib.config.ConfigBase;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ClientConfig extends ConfigBase {
   public final BooleanValue renderSkull;
   private final ConfigValue<String> graveTextColorSpec;
   public int graveTextColor = 16777215;

   public ClientConfig(Builder builder) {
      super(builder);
      this.renderSkull = builder.comment("If this is set to true the players head will be rendered on the gravestone when there is a full block under it")
         .translation("render_skull")
         .define("render_skull", true);
      this.graveTextColorSpec = builder.comment("The color of the text at the gravestone (Hex RGB)")
         .translation("grave_text_color")
         .define("grave_text_color", "FFFFFF");
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
      this.graveTextColor = Integer.parseInt((String)this.graveTextColorSpec.get(), 16);
   }
}
