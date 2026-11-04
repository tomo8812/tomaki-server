package dev.terminalmc.clientsort.client.gui.screen.edit;

import dev.terminalmc.clientsort.client.gui.TriggerButtonManager;
import dev.terminalmc.clientsort.client.gui.widget.TriggerButton;
import java.util.LinkedList;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class ContainerEditorScreen extends EditorScreen {
   public ContainerEditorScreen(AbstractContainerScreen<?> underlay, TriggerButton button) {
      super(underlay, false, button);
   }

   @Override
   protected LinkedList<TriggerButton> getButtons() {
      return TriggerButtonManager.getContainerButtons();
   }
}
