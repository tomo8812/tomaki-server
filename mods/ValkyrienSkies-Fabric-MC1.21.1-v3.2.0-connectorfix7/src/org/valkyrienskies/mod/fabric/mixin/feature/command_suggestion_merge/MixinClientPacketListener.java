package org.valkyrienskies.mod.fabric.mixin.feature.command_suggestion_merge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.class_2172;
import net.minecraft.class_2641;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_634.class)
public abstract class MixinClientPacketListener {
   @Shadow
   private CommandDispatcher<class_2172> field_3696;

   @Inject(method = "handleCommands", at = @At("RETURN"), require = 1)
   private void vs$mergeCollidingClientSubcommands(class_2641 packet, CallbackInfo ci) {
      CommandDispatcher<FabricClientCommandSource> clientDispatcher = ClientCommandInternals.getActiveDispatcher();
      if (clientDispatcher != null) {
         CommandNode<class_2172> serverRoot = this.field_3696.getRoot();

         for (CommandNode<FabricClientCommandSource> clientRoot : clientDispatcher.getRoot().getChildren()) {
            CommandNode<class_2172> serverNode = serverRoot.getChild(clientRoot.getName());
            if (serverNode != null) {
               for (CommandNode<FabricClientCommandSource> sub : clientRoot.getChildren()) {
                  if (serverNode.getChild(sub.getName()) == null) {
                     serverNode.addChild(vs$completionCopy(sub));
                  }
               }
            }
         }
      }
   }

   private static CommandNode<class_2172> vs$completionCopy(CommandNode<FabricClientCommandSource> node) {
      ArgumentBuilder builder = node.createBuilder();
      builder.requires(s -> true);
      if (builder.getCommand() != null) {
         builder.executes(ctx -> 0);
      }

      CommandNode result = builder.build();

      for (CommandNode child : node.getChildren()) {
         result.addChild(vs$completionCopy(child));
      }

      return result;
   }
}
