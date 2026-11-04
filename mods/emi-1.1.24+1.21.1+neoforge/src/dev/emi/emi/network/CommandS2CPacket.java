package dev.emi.emi.network;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.bom.BoM;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class CommandS2CPacket implements EmiPacket {
   private final byte type;
   private final ResourceLocation id;

   public CommandS2CPacket(byte type, ResourceLocation id) {
      this.type = type;
      this.id = id;
   }

   public CommandS2CPacket(FriendlyByteBuf buf) {
      this.type = buf.readByte();
      if (this.type != 1 && this.type != 17 && this.type != 18) {
         this.id = null;
      } else {
         this.id = buf.readResourceLocation();
      }
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeByte(this.type);
      if (this.type == 1 || this.type == 17 || this.type == 18) {
         buf.writeResourceLocation(this.id);
      }
   }

   @Override
   public void apply(Player player) {
      if (this.type == 1) {
         EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(this.id);
         if (recipe != null) {
            EmiApi.displayRecipe(recipe);
         }
      } else if (this.type == 2) {
         EmiApi.viewRecipeTree();
      } else if (this.type == 17) {
         EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(this.id);
         if (recipe != null) {
            BoM.setGoal(recipe);
         }
      } else if (this.type == 18) {
         EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(this.id);
         if (recipe != null && BoM.tree != null) {
            for (EmiStack stack : recipe.getOutputs()) {
               BoM.tree.addResolution(stack, recipe);
            }
         }
      }
   }

   public Type<CommandS2CPacket> type() {
      return EmiNetwork.COMMAND;
   }
}
