package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.util.Translator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public final class ShapeFactory {
   private static final Map<String, BiFunction<Selection, Boolean, Shape>> custom = new HashMap<>();

   private ShapeFactory() {
   }

   public static Shape getShape(Selection selection) {
      return getShape(selection, true);
   }

   public static Shape getShape(Selection selection, boolean chunkAligned) {
      return switch (selection.shape()) {
         case "circle" -> new Circle(selection, chunkAligned);
         case "diamond" -> new Diamond(selection, chunkAligned);
         case "ellipse", "oval" -> new Ellipse(selection, chunkAligned);
         case "hexagon" -> new Hexagon(selection, chunkAligned);
         case "pentagon" -> new Pentagon(selection, chunkAligned);
         case "rectangle" -> new Rectangle(selection, chunkAligned);
         case "star" -> new Star(selection, chunkAligned);
         case "triangle" -> new Triangle(selection, chunkAligned);
         default -> (Shape)custom.getOrDefault(selection.shape(), Square::new).apply(selection, chunkAligned);
      };
   }

   public static void registerCustom(String name, BiFunction<Selection, Boolean, Shape> shapeFunction) {
      custom.put(name, shapeFunction);
      Translator.addCustomTranslation("shape_%s".formatted(name), name);
   }

   public static Set<String> getCustomTypes() {
      return custom.keySet();
   }
}
