package org.millenaire.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.millenaire.content.ContentDirectoryManager;
import org.millenaire.content.validate.Finding;
import org.millenaire.content.validate.Severity;
import org.millenaire.content.validate.ValidationResult;
import org.millenaire.content.validate.semantic.RuntimeValidation;
import org.slf4j.Logger;

public final class ValidateContentCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_CHAT_LINES = 40;

   private ValidateContentCommand() {
   }

   public static void registerUnder(LiteralArgumentBuilder<CommandSourceStack> parent) {
      parent.then(Commands.literal("validate-content").executes(ValidateContentCommand::execute));
   }

   private static int execute(CommandContext<CommandSourceStack> ctx) {
      CommandSourceStack source = (CommandSourceStack)ctx.getSource();

      ValidationResult result;
      try {
         result = RuntimeValidation.validateLiveOverlay(true);
      } catch (Exception e) {
         LOGGER.error("[Millenaire] validate-content failed", e);
         source.sendFailure(Component.literal("validate-content: NOT RUN (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")"));
         return 0;
      }

      int errors = 0;
      int warns = 0;

      for (Finding f : result.findings()) {
         if (f.severity() == Severity.ERROR) {
            errors++;
         } else if (f.severity() == Severity.WARN) {
            warns++;
         }
      }

      String summary = "validate-content: tiers "
         + result.describeTiersRun()
         + (result.tiersSkipped().isEmpty() ? "" : " (NOT RUN: " + String.join(", ", result.tiersSkipped()) + ")")
         + " — "
         + result.findings().size()
         + " finding(s): "
         + errors
         + " error(s), "
         + warns
         + " warning(s)";
      source.sendSuccess(() -> Component.literal(summary), false);
      int lines = 0;

      for (Finding f : result.findings()) {
         if (!f.allowlisted() && f.severity() != Severity.INFO) {
            if (lines++ >= 40) {
               source.sendSuccess(() -> Component.literal("… more findings in the report file."), false);
               break;
            }

            String line = f.render();
            source.sendSuccess(() -> Component.literal(line), false);
         }
      }

      if (ContentDirectoryManager.isInitialized()) {
         try {
            Path out = ContentDirectoryManager.getCustomDir().resolve("_content_validation.txt");
            Files.writeString(out, result.render(), StandardCharsets.UTF_8);
            source.sendSuccess(() -> Component.literal("Full report: " + out), false);
         } catch (Exception e) {
            LOGGER.warn("[Millenaire] could not write the content validation report: {}", e.getMessage());
         }
      }

      return errors == 0 ? 1 : 0;
   }
}
