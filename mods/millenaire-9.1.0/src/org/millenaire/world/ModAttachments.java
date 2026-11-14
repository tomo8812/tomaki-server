package org.millenaire.world;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import org.millenaire.world.sitescore.SiteChunkData;

public final class ModAttachments {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Keys.ATTACHMENT_TYPES, "millenaire");
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<SiteChunkData>> SITE_CHUNK = ATTACHMENT_TYPES.register(
      "site_chunk", () -> AttachmentType.builder(() -> SiteChunkData.EMPTY).serialize(SiteChunkData.CODEC, SiteChunkData::worthSaving).build()
   );

   private ModAttachments() {
   }

   public static void register(IEventBus modEventBus) {
      ATTACHMENT_TYPES.register(modEventBus);
   }
}
