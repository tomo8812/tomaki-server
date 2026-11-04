package fuzs.puzzleslib.neoforge.impl.attachment;

import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public record NeoForgeAttachmentTypeAdapter<T extends IAttachmentHolder, A>(DeferredHolder<AttachmentType<?>, AttachmentType<A>> attachmentType)
   implements AttachmentTypeAdapter<T, A> {
   @Override
   public ResourceLocation id() {
      return this.attachmentType.getKey().location();
   }

   public boolean hasData(T holder) {
      Objects.requireNonNull(holder, "holder is null");
      return holder.hasData(this.attachmentType);
   }

   @Nullable
   public A getData(T holder) {
      Objects.requireNonNull(holder, "holder is null");
      return (A)holder.getExistingDataOrNull(this.attachmentType);
   }

   @Nullable
   public A setData(T holder, A value) {
      Objects.requireNonNull(holder, "holder is null");
      Objects.requireNonNull(value, "value is null");
      return (A)holder.setData(this.attachmentType, value);
   }

   @Nullable
   public A removeData(T holder) {
      Objects.requireNonNull(holder, "holder is null");
      return (A)holder.removeData(this.attachmentType);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object removeData(Object param1) {
      return this.removeData((T)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object setData(Object param1, Object nullx) {
      return this.setData((T)var1, (A)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object getData(Object param1) {
      return this.getData((T)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean hasData(Object param1) {
      return this.hasData((T)var1);
   }
}
