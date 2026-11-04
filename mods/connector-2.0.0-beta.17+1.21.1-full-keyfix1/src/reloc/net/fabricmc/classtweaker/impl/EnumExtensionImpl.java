package reloc.net.fabricmc.classtweaker.impl;

import reloc.net.fabricmc.classtweaker.api.EnumExtension;

public class EnumExtensionImpl implements EnumExtension {
   private final String addedConstant;

   public EnumExtensionImpl(String addedConstant) {
      this.addedConstant = addedConstant;
   }

   @Override
   public String getAddedConstant() {
      return this.addedConstant;
   }
}
