package fuzs.puzzleslib.api.client.input.v1;

import net.minecraft.util.StringUtil;

public record CharacterEvent(int codepoint) {
   public String codepointAsString() {
      return Character.toString(this.codepoint);
   }

   public boolean isAllowedChatCharacter() {
      return StringUtil.isAllowedChatCharacter((char)this.codepoint);
   }
}
