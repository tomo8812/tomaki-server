package dev.terminalmc.clientsort.client.gui.screen.edit;

import dev.terminalmc.clientsort.client.gui.TriggerButtonManager;
import dev.terminalmc.clientsort.client.gui.widget.TriggerButton;
import java.util.LinkedList;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class PlayerEditorScreen extends EditorScreen {
   public PlayerEditorScreen(AbstractContainerScreen<?> underlay, TriggerButton button) {
      super(underlay, true, button);
   }

   @Override
   protected LinkedList<TriggerButton> getButtons() {
      return TriggerButtonManager.getPlayerButtons();
   }
}
