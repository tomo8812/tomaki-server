package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public interface FactoriesProxy {
   ModConstructorImpl<ModConstructor> getModConstructorImpl();

   ModContext getModContext(String var1);

   RegistryFactory getRegistryFactoryV3();

   fuzs.puzzleslib.api.init.v4.registry.RegistryFactory getRegistryFactoryV4();

   GameRulesFactory getGameRulesFactory();

   ToolTypeHelper getToolTypeHelper();

   CombinedIngredients getCombinedIngredients();

   <T> AbstractTagAppender<T> getTagAppenderV2(TagBuilder var1, @Nullable Function<T, ResourceKey<T>> var2);

   <T> fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> getTagAppenderV3(TagBuilder var1, @Nullable Function<T, ResourceKey<T>> var2);

   DataAttachmentRegistryImpl getDataAttachmentRegistry();

   RecipeOutput getTransformingRecipeOutput(RecipeOutput var1, UnaryOperator<Recipe<?>> var2);

   RecipeOutput getIdentifiableRecipeOutput(AbstractRecipeProvider var1, CachedOutput var2, Provider var3, List<CompletableFuture<?>> var4);
}
