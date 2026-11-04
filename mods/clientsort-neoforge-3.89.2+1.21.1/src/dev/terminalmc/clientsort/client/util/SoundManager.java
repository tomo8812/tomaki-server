package dev.terminalmc.clientsort.client.util;

import dev.terminalmc.clientsort.client.config.Config;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SoundManager {
   private static long nextSoundTime = Long.MIN_VALUE;
   private static float pitch = 1.0F;
   private static float increment = 0.01F;
   @Nullable
   private static SoundInstance sound;

   public static boolean shouldPlaySortingSounds() {
      return Config.options().playSoundSort && Config.options().soundVolume > 0.0F;
   }

   public static boolean shouldPlayOtherSounds() {
      return Config.options().playSoundOther && Config.options().soundVolume > 0.0F;
   }

   public static void resetForCount(int size) {
      increment = (Config.options().soundPitchMax - Config.options().soundPitchMin) / size;
      pitch = Config.options().soundPitchMin;
   }

   public static void play() {
      long now = Util.getMillis();
      float soundPitch = getPitch();
      if (now >= nextSoundTime) {
         nextSoundTime = now + Config.options().soundInterval;
         ResourceLocation location = Config.options().sortSoundLoc;
         if (location != null) {
            if (sound != null && !Config.options().allowSoundOverlap) {
               Minecraft.getInstance().getSoundManager().stop(sound);
            }

            sound = new SimpleSoundInstance(
               location,
               SoundSource.MASTER,
               Config.options().soundVolume,
               soundPitch,
               SoundInstance.createUnseededRandom(),
               false,
               0,
               Attenuation.NONE,
               0.0,
               0.0,
               0.0,
               true
            );
            Minecraft.getInstance().getSoundManager().play(sound);
         }
      }
   }

   private static float getPitch() {
      float val = pitch;
      pitch = pitch + increment;
      if (pitch > Config.options().soundPitchMax) {
         pitch = Config.options().soundPitchMax;
      }

      return val;
   }

   public static int estimateSortSounds(ItemStack[] stacks) {
      int stackCount = 0;

      for (ItemStack stack : stacks) {
         if (stack != ItemStack.EMPTY) {
            stackCount++;
         }
      }

      int compaction = 0;

      for (int i = 0; i < stackCount; i++) {
         if (stacks[i] == ItemStack.EMPTY) {
            compaction++;
         }
      }

      int size = stackCount + compaction;
      return size + size / 15;
   }

   public static int estimateStackFillSounds(ItemStack[] srcStacks, ItemStack[] dstStacks) {
      int dstPartialCount = 0;

      for (ItemStack stack : dstStacks) {
         if (stack != ItemStack.EMPTY && stack.getCount() < stack.getMaxStackSize()) {
            dstPartialCount++;
         }
      }

      int srcStackCount = 0;

      for (ItemStack stack : srcStacks) {
         if (stack != ItemStack.EMPTY) {
            srcStackCount++;
         }
      }

      return (dstPartialCount / 2 + srcStackCount / 2) / 2;
   }

   public static int estimateTransferSounds(ItemStack[] srcStacks, ItemStack[] dstStacks) {
      int srcStackCount = 0;

      for (ItemStack stack : srcStacks) {
         if (stack != ItemStack.EMPTY) {
            srcStackCount++;
         }
      }

      int dstHoleCount = 0;

      for (ItemStack stack : dstStacks) {
         if (stack == ItemStack.EMPTY) {
            dstHoleCount++;
         }
      }

      dstHoleCount += dstHoleCount / 8;
      return Math.min(srcStackCount, dstHoleCount);
   }
}
