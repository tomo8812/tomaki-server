package net.satisfy.candlelight.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.platform.PlatformHelper;

public final class DinnerMenu {
   public static final TagKey<Item> STARTERS = TagKey.create(Registries.ITEM, Candlelight.identifier("starters"));
   public static final TagKey<Item> MAIN_COURSES = TagKey.create(Registries.ITEM, Candlelight.identifier("main_courses"));
   public static final TagKey<Item> DESSERTS = TagKey.create(Registries.ITEM, Candlelight.identifier("desserts"));
   public static final int COURSES = 3;
   private static final Map<UUID, DinnerMenu.Progress> PROGRESS = new HashMap<>();

   private DinnerMenu() {
   }

   public static DinnerMenu.Course courseOf(ItemStack stack) {
      if (stack.is(STARTERS)) {
         return DinnerMenu.Course.STARTER;
      } else if (stack.is(DESSERTS)) {
         return DinnerMenu.Course.DESSERT;
      } else {
         return stack.is(MAIN_COURSES) ? DinnerMenu.Course.MAIN : null;
      }
   }

   public static DinnerMenu.Result eat(Player player, ItemStack stack) {
      DinnerMenu.Course course = courseOf(stack);
      if (course == null) {
         return null;
      }

      long now = player.level().getGameTime();
      DinnerMenu.Progress progress = PROGRESS.get(player.getUUID());
      int previous = progress != null && now - progress.gameTime() <= PlatformHelper.menuMaxTicksBetweenCourses() ? progress.step() : 0;
      int step = course.ordinal() == previous ? previous + 1 : (course == DinnerMenu.Course.STARTER ? 1 : 0);
      if (step >= 3) {
         PROGRESS.remove(player.getUUID());
         return new DinnerMenu.Result(course, 3, true);
      }

      if (step == 0) {
         PROGRESS.remove(player.getUUID());
      } else {
         PROGRESS.put(player.getUUID(), new DinnerMenu.Progress(step, now));
      }

      return new DinnerMenu.Result(course, step, false);
   }

   public enum Course {
      STARTER,
      MAIN,
      DESSERT;

      // $VF: synthetic method
      private static DinnerMenu.Course[] $values() {
         return new DinnerMenu.Course[]{STARTER, MAIN, DESSERT};
      }
   }

   private record Progress(int step, long gameTime) {
   }

   public record Result(DinnerMenu.Course course, int step, boolean complete) {
   }
}
