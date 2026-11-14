package org.millenaire.content.validate.rules;

import com.google.gson.JsonObject;
import java.util.List;
import org.millenaire.content.validate.ContentIndex;
import org.millenaire.content.validate.Finding;
import org.millenaire.content.validate.Json;
import org.millenaire.content.validate.Severity;
import org.millenaire.content.validate.ValidationContext;
import org.millenaire.content.validate.ValidationRule;

public final class StaticSemanticRules {
   private StaticSemanticRules() {
   }

   public static List<ValidationRule> all() {
      return List.of(new StaticSemanticRules.M05Static());
   }

   static final class M05Static implements ValidationRule {
      @Override
      public String id() {
         return "M-05";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.Entry vt : index.entries(ContentIndex.Family.VILLAGE_TYPE)) {
            if (vt.json() != null && Json.integer(vt.json(), "weight", 10) > 0 && !Json.bool(vt.json(), "lone_building", false)) {
               boolean centre = false;
               int starts = 0;
               int startNonSub = 0;

               for (JsonObject slot : Json.objects(vt.json(), "layout")) {
                  String role = Json.string(slot, "role");
                  String ref = CrossReferenceRules.path(Json.string(slot, "plan"));
                  if (role != null && ref != null) {
                     int slash = ref.indexOf(47);
                     ContentIndex.Entry plan = slash > 0 ? index.planSet(ref.substring(0, slash), ref.substring(slash + 1)) : null;
                     if ("centre".equals(role) && plan != null) {
                        centre = true;
                     }

                     if ("start".equals(role)) {
                        starts++;
                        if (plan != null && plan.json() != null && !Json.bool(plan.json(), "is_sub_building", false)) {
                           startNonSub++;
                        }
                     }
                  }
               }

               if (!centre) {
                  out.add(
                     Finding.of(
                        "M-05",
                        Severity.ERROR,
                        "village_type",
                        vt.identity(),
                        vt.origin().label(),
                        "natural-spawn village type has no 'centre' layout slot with an existing plan set.",
                        "add a centre slot pointing at the town hall plan set."
                     )
                  );
               }

               if (starts > 0 && startNonSub == 0) {
                  out.add(
                     Finding.of(
                        "M-05",
                        Severity.ERROR,
                        "village_type",
                        vt.identity(),
                        vt.origin().label(),
                        "every 'start' layout slot is missing or a sub-building — the village never raises a first building.",
                        "point at least one start slot at a regular plan set."
                     )
                  );
               }
            }
         }
      }
   }
}
