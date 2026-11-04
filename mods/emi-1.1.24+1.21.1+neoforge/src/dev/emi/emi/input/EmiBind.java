package dev.emi.emi.input;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.emi.emi.EmiPort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EmiBind {
   public static final EmiBind LEFT_CLICK = new EmiBind("", new EmiBind.ModifiedKey(Type.MOUSE.getOrCreate(0), 0));
   public static final int MAX_BINDS = 4;
   public final String translationKey;
   public final List<EmiBind.ModifiedKey> defaultKeys;
   public List<EmiBind.ModifiedKey> boundKeys;

   public EmiBind(String translationKey, int code) {
      this(translationKey, 0, code);
   }

   public EmiBind(String translationKey, int modifiers, int code) {
      this(translationKey, EmiBind.ModifiedKey.of(code, modifiers));
   }

   public EmiBind(String translationKey, EmiBind.ModifiedKey... defaultKeys) {
      this.translationKey = translationKey;
      this.defaultKeys = Arrays.asList(defaultKeys);
      this.boundKeys = this.defaultKeys.stream().collect(Collectors.toCollection(ArrayList::new));
      this.updateBinds();
   }

   public void updateBinds() {
      if (this.boundKeys.size() == 0) {
         this.boundKeys.add(new EmiBind.ModifiedKey(InputConstants.UNKNOWN, 0));
      }

      for (int i = 0; i < this.boundKeys.size() - 1; i++) {
         if (this.boundKeys.get(i).isUnbound()) {
            this.boundKeys.remove(i);
            i--;
         }
      }

      if (!this.boundKeys.get(this.boundKeys.size() - 1).isUnbound() && this.boundKeys.size() < 4) {
         this.boundKeys.add(new EmiBind.ModifiedKey(InputConstants.UNKNOWN, 0));
      }
   }

   public boolean isBound() {
      return this.boundKeys.size() > 0 && !this.boundKeys.get(0).isUnbound();
   }

   public Component getBindText() {
      if (!this.isBound()) {
         return EmiPort.literal("[]", ChatFormatting.GOLD);
      }

      EmiBind.ModifiedKey bind = this.boundKeys.get(0);

      for (EmiBind.ModifiedKey key : this.boundKeys) {
         if (key.key.getType() == Type.MOUSE) {
            bind = key;
            break;
         }
      }

      return EmiPort.literal("[", ChatFormatting.GOLD).append(bind.getKeyText(ChatFormatting.GOLD)).append(EmiPort.literal("]", ChatFormatting.GOLD));
   }

   public void setToDefault() {
      this.boundKeys = this.defaultKeys.stream().collect(Collectors.toCollection(ArrayList::new));
      this.updateBinds();
   }

   public void setBinds(EmiBind.ModifiedKey... keys) {
      this.boundKeys = Stream.of(keys).collect(Collectors.toCollection(ArrayList::new));
      this.updateBinds();
   }

   public void setBind(int offset, EmiBind.ModifiedKey key) {
      if (offset < this.boundKeys.size() && offset >= 0) {
         this.boundKeys.set(offset, key);
      }

      this.updateBinds();
   }

   public boolean isHeld() {
      for (EmiBind.ModifiedKey boundKey : this.boundKeys) {
         if (EmiInput.getCurrentModifiers() == boundKey.modifiersToMatch()
            && boundKey.key.getType() == Type.KEYSYM
            && boundKey.key.getValue() != -1
            && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), boundKey.key.getValue())) {
            return true;
         }
      }

      return false;
   }

   public boolean matchesKey(int keyCode, int scanCode) {
      for (EmiBind.ModifiedKey boundKey : this.boundKeys) {
         if (EmiInput.getCurrentModifiers() == boundKey.modifiersToMatch()) {
            if (keyCode == InputConstants.UNKNOWN.getValue()) {
               if (boundKey.key.getType() == Type.SCANCODE && boundKey.key.getValue() == scanCode) {
                  return true;
               }
            } else if (boundKey.key.getType() == Type.KEYSYM && boundKey.key.getValue() == keyCode) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean matchesMouse(int code) {
      for (EmiBind.ModifiedKey boundKey : this.boundKeys) {
         if (EmiInput.getCurrentModifiers() == boundKey.modifiersToMatch() && boundKey.key.getType() == Type.MOUSE && boundKey.key.getValue() == code) {
            return true;
         }
      }

      return false;
   }

   public void setKey(List<String> keys) {
      List<EmiBind.ModifiedKey> modifiedKeys = Lists.newArrayList();

      for (String string : keys) {
         String[] parts = string.split(" ");
         Key key = InputConstants.UNKNOWN;
         int modifiers = 0;
         if (parts.length > 0) {
            key = InputConstants.getKey(parts[parts.length - 1]);

            for (int i = 0; i < parts.length - 1; i++) {
               if (parts[i].equals("ctrl") || parts[i].equals("control")) {
                  modifiers |= 1;
               } else if (parts[i].equals("alt")) {
                  modifiers |= 2;
               } else if (parts[i].equals("shift")) {
                  modifiers |= 4;
               }
            }
         }

         modifiedKeys.add(new EmiBind.ModifiedKey(key, modifiers));
      }

      this.boundKeys = modifiedKeys;
      this.updateBinds();
   }

   public record ModifiedKey(Key key, int modifiers) {
      public static EmiBind.ModifiedKey of(int code, int modifiers) {
         return new EmiBind.ModifiedKey(Type.KEYSYM.getOrCreate(code), modifiers);
      }

      public String toName() {
         String name = "";
         if ((this.modifiers & 1) > 0) {
            name = name + "ctrl ";
         }

         if ((this.modifiers & 2) > 0) {
            name = name + "alt ";
         }

         if ((this.modifiers & 4) > 0) {
            name = name + "shift ";
         }

         return name + this.key.getName();
      }

      public int modifiersToMatch() {
         int modifiers = this.modifiers;
         if (this.key.getType() == Type.KEYSYM) {
            modifiers ^= EmiInput.maskFromCode(this.key.getValue());
         }

         return modifiers;
      }

      public boolean isUnbound() {
         return this.key == InputConstants.UNKNOWN;
      }

      public MutableComponent getKeyText(ChatFormatting formatting) {
         MutableComponent text = EmiPort.literal("", formatting);
         this.appendModifiers(text, this.modifiers());
         EmiPort.append(text, this.key().getDisplayName());
         return text;
      }

      private void appendModifiers(MutableComponent text, int modifiers) {
         if ((modifiers & 1) > 0) {
            EmiPort.append(text, EmiPort.translatable("key.keyboard.control"));
            EmiPort.append(text, EmiPort.literal(" + "));
         }

         if ((modifiers & 2) > 0) {
            EmiPort.append(text, EmiPort.translatable("key.keyboard.alt"));
            EmiPort.append(text, EmiPort.literal(" + "));
         }

         if ((modifiers & 4) > 0) {
            EmiPort.append(text, EmiPort.translatable("key.keyboard.shift"));
            EmiPort.append(text, EmiPort.literal(" + "));
         }
      }
   }
}
