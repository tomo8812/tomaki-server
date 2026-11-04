package org.embeddedt.modernfix.neoforge.dynresources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class ModelLocationBuilder {
   private final Map<Property<?>, ModelLocationBuilder.PropertyData> propertyToOptionStrings = new Object2ObjectOpenHashMap();
   private final StringBuilder builder = new StringBuilder();

   public void generateForBlock(Set<ModelResourceLocation> destinationSet, Block block, ResourceLocation baseLocation) {
      Set<Property<?>> props = block.defaultBlockState().getValues().keySet();
      List<ImmutableList<String>> optionsList = new ArrayList<>(props.size());
      int requiredBuilderSize = Math.max(0, props.size() - 1);

      for (Property<?> prop : props) {
         ModelLocationBuilder.PropertyData data = this.propertyToOptionStrings.computeIfAbsent(prop, ModelLocationBuilder::computePropertyOptions);
         optionsList.add(data.nameValuePairs);
         requiredBuilderSize += data.maxPairLength;
      }

      List<List<String>> product = Lists.cartesianProduct(optionsList);
      int count = product.size();
      int tupleEntryCount = optionsList.size();
      StringBuilder stringbuilder = this.builder;
      stringbuilder.ensureCapacity(requiredBuilderSize);

      for (int i = 0; i < count; i++) {
         stringbuilder.setLength(0);
         List<String> result = product.get(i);

         for (int j = 0; j < tupleEntryCount; j++) {
            if (j != 0) {
               stringbuilder.append(',');
            }

            stringbuilder.append(result.get(j));
         }

         destinationSet.add(new ModelResourceLocation(baseLocation, stringbuilder.toString()));
      }
   }

   private static ModelLocationBuilder.PropertyData computePropertyOptions(Property<?> prop) {
      Builder<String> valuesList = ImmutableList.builderWithExpectedSize(prop.getPossibleValues().size());
      int maxLength = 0;

      for (Comparable<?> val : prop.getPossibleValues()) {
         String pair = prop.getName() + "=" + getValueName(prop, val);
         valuesList.add(pair.toLowerCase(Locale.ROOT));
         maxLength = Math.max(pair.length(), maxLength);
      }

      return new ModelLocationBuilder.PropertyData(valuesList.build(), maxLength);
   }

   private static <T extends Comparable<T>> String getValueName(Property<T> property, Comparable<?> value) {
      return property.getName(value);
   }

   private record PropertyData(ImmutableList<String> nameValuePairs, int maxPairLength) {
   }
}
