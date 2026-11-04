package net.satisfy.candlelight.client.gui;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.candlelight.core.block.entity.TypewriterEntity;
import net.satisfy.candlelight.core.networking.packet.SyncTypewriterDataC2SPacket;
import net.satisfy.candlelight.core.registry.SoundEventRegistry;

@OnlyIn(Dist.CLIENT)
public class TypeWriterGui extends NoteGui {
   private final TypewriterEntity typewriterEntity;
   private float lineProgressLocal = 0.0F;
   private static final int MAX_CHARS_PER_LINE = 19;
   private int charsInLine = 0;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !TypeWriterGui.class.desiredAssertionStatus();

   public TypeWriterGui(Player player, TypewriterEntity typewriterEntity) {
      super(player, typewriterEntity.getPaper());
      this.typewriterEntity = typewriterEntity;
   }

   @Override
   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      assert this.minecraft != null;
      this.minecraft.gameRenderer.processBlurEffect(delta);
      this.minecraft.getMainRenderTarget().bindWrite(false);
      super.render(guiGraphics, mouseX, mouseY, delta);
   }

   @Override
   public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
   }

   public void renderTransparentBackground(GuiGraphics guiGraphics) {
   }

   protected void renderBlurredBackground(float f) {
   }

   public void removed() {
      super.removed();
      this.typewriterEntity.snapRoller();
      this.typewriterEntity.setLineProgress(0.0F);
      this.charsInLine = 0;
      this.lineProgressLocal = 0.0F;
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      boolean result = super.charTyped(chr, modifiers);
      if (!result) {
         return false;
      }

      if (chr != '\n' && chr != '\r') {
         if (chr == ' ') {
            this.typewriterEntity.triggerSpace();
         } else {
            this.typewriterEntity.triggerKeyBounce();
         }

         this.charsInLine++;
         if (this.charsInLine >= 19) {
            this.typewriterEntity.snapRoller();
            this.charsInLine = 0;
            this.lineProgressLocal = 0.0F;
            this.typewriterEntity.setLineProgress(0.0F);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BELL, 1.0F));
            super.charTyped('\n', 0);
         } else {
            this.lineProgressLocal = this.charsInLine / 19.0F;
            this.typewriterEntity.setLineProgress(this.lineProgressLocal);
         }

         this.playTypingSound();
         return true;
      } else {
         return true;
      }
   }

   @Override
   protected void finalizeNote(boolean signNote) {
      if (this.dirty) {
         this.removeEmptyPages();
         this.writeNbtData(signNote);
      }

      CompoundTag nbt = ((CustomData)this.itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      BlockPos pos = this.typewriterEntity.getBlockPos();
      NetworkManager.sendToServer(new SyncTypewriterDataC2SPacket(nbt, pos, signNote));
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      boolean result = super.keyPressed(keyCode, scanCode, modifiers);
      if (!result) {
         return false;
      }

      if (keyCode == 257) {
         this.typewriterEntity.triggerEnter();
         this.typewriterEntity.snapRoller();
         this.charsInLine = 0;
         this.lineProgressLocal = 0.0F;
         this.typewriterEntity.setLineProgress(0.0F);
         this.playEnterSound();
         return true;
      }

      if (keyCode == 259) {
         if (this.charsInLine > 0) {
            this.charsInLine--;
            this.lineProgressLocal = this.charsInLine / 19.0F;
            this.typewriterEntity.setLineProgress(this.lineProgressLocal);
         }

         this.playTypingSound();
         return true;
      } else {
         this.playTypingSound();
         return true;
      }
   }

   private void playEnterSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BELL, 1.0F));
   }

   private void playTypingSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEventRegistry.TYPEWRITER.get(), 2.0F));
   }
}
