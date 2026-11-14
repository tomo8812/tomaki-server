package org.millenaire.content.validate.semantic;

import java.util.ArrayList;
import java.util.List;
import org.millenaire.content.BuiltInCultures;
import org.millenaire.content.CustomContentIndex;
import org.millenaire.content.validate.ContentIndex;
import org.millenaire.content.validate.ContentValidator;
import org.millenaire.content.validate.Finding;
import org.millenaire.content.validate.ValidationContext;
import org.millenaire.content.validate.ValidationResult;

public final class RuntimeValidation {
   public static final String TIER_SEMANTIC = "M";

   private RuntimeValidation() {
   }

   public static ValidationResult validateLiveOverlay(boolean rebuildIndex) {
      CustomContentIndex snapshot = rebuildIndex ? CustomContentIndex.rebuild(BuiltInCultures.IDS, true) : CustomContentIndex.current();
      return validate(snapshot);
   }

   public static ValidationResult validate(CustomContentIndex snapshot) {
      ContentIndex index = ContentIndex.build(snapshot);
      ValidationContext context = ValidationContext.shipped();
      ValidationResult staticResult = ContentValidator.standard().run(index, context);
      List<Finding> staticFindings = new ArrayList<>(staticResult.findings());
      ContentValidator semantic = ContentValidator.withShippedAllowlist();

      try {
         RegistryProbe.INSTANCE.itemExists("minecraft:stone");
         semantic.withTier("M", SemanticRules.all(index, staticFindings));
      } catch (Throwable t) {
         semantic.withSkippedTier("M", "registries unavailable: " + t.getClass().getSimpleName());
      }

      return ValidationResult.merge(staticResult, semantic.run(index, context));
   }
}
