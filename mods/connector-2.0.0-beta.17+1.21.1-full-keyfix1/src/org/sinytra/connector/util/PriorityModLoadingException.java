package org.sinytra.connector.util;

import java.util.List;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.ModLoadingIssue;

public class PriorityModLoadingException extends ModLoadingException {
   public PriorityModLoadingException(ModLoadingIssue issue) {
      super(issue);
   }

   public PriorityModLoadingException(List<ModLoadingIssue> issues) {
      super(issues);
   }
}
