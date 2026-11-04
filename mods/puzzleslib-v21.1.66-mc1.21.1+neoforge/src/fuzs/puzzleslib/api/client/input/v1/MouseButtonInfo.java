package fuzs.puzzleslib.api.client.input.v1;

public record MouseButtonInfo(int button, int modifiers) implements InputWithModifiers {
   @Override
   public int input() {
      return this.button;
   }
}
