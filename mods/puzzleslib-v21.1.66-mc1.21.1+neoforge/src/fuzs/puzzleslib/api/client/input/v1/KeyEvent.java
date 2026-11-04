package fuzs.puzzleslib.api.client.input.v1;

public record KeyEvent(int key, int scancode, int modifiers) implements InputWithModifiers {
   @Override
   public int input() {
      return this.key;
   }
}
