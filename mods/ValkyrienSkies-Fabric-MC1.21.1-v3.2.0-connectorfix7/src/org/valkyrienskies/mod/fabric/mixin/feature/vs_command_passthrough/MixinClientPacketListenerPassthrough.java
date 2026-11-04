package org.valkyrienskies.mod.fabric.mixin.feature.vs_command_passthrough;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.class_2172;
import net.minecraft.class_634;
import net.minecraft.class_7472;
import net.minecraft.class_7644;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_634.class, priority = 900)
public abstract class MixinClientPacketListenerPassthrough {
   @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
   private void vs$forwardServerOwnedCommand(String command, CallbackInfo ci) {
      class_634 self = (class_634)this;
      CommandDispatcher<class_2172> serverDispatcher = self.method_2886();
      if (serverDispatcher != null) {
         ParseResults<class_2172> parse = serverDispatcher.parse(command, self.method_2875());
         if (vs$isFullyValid(parse) && !class_7644.method_58774(parse) && !vs$clientOwns(parse)) {
            self.method_52787(new class_7472(command));
            ci.cancel();
         }
      }
   }

   private static boolean vs$isFullyValid(ParseResults<?> parse) {
      return !parse.getReader().canRead() && parse.getExceptions().isEmpty() && parse.getContext().getLastChild().getCommand() != null;
   }

   private static boolean vs$clientOwns(ParseResults<?> parse) {
      CommandDispatcher<FabricClientCommandSource> client = ClientCommandInternals.getActiveDispatcher();
      if (client == null) {
         return false;
      }

      CommandNode<FabricClientCommandSource> node = client.getRoot();
      boolean walkedLiteral = false;

      for (ParsedCommandNode<?> parsed : parse.getContext().getNodes()) {
         if (!(parsed.getNode() instanceof LiteralCommandNode)) {
            break;
         }

         CommandNode<FabricClientCommandSource> child = node.getChild(parsed.getNode().getName());
         if (child == null) {
            return false;
         }

         node = child;
         walkedLiteral = true;
      }

      return walkedLiteral;
   }
}
