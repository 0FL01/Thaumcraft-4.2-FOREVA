package thaumcraft.client.integration.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

@JEIPlugin
public final class ThaumcraftJeiPlugin implements IModPlugin {
    static final String ARCANE_UID = "thaumcraft.arcane";
    static final String CRUCIBLE_UID = "thaumcraft.crucible";
    static final String INFUSION_UID = "thaumcraft.infusion";

    private final List<ResearchVisibility.Entry<ThaumcraftRecipeWrapper>> visibilityEntries =
            new ArrayList<ResearchVisibility.Entry<ThaumcraftRecipeWrapper>>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ThaumcraftRecipeCategory<ThaumcraftRecipeWrapper.Arcane>(
                        ARCANE_UID, "recipe.type.arcane", gui.createBlankDrawable(160, 100)),
                new ThaumcraftRecipeCategory<ThaumcraftRecipeWrapper.Crucible>(
                        CRUCIBLE_UID, "recipe.type.crucible", gui.createBlankDrawable(160, 92)),
                new ThaumcraftRecipeCategory<ThaumcraftRecipeWrapper.Infusion>(
                        INFUSION_UID, "recipe.type.infusion", gui.createBlankDrawable(160, 125)));
    }

    @Override
    public void register(IModRegistry registry) {
        this.visibilityEntries.clear();
        IStackHelper stackHelper = registry.getJeiHelpers().getStackHelper();
        JeiRecipeData.Collection data = JeiRecipeData.collect(
                new ArrayList<Object>(ThaumcraftApi.getCraftingRecipes()), stackHelper::getSubtypes);
        IDrawable slot = registry.getJeiHelpers().getGuiHelper().getSlotDrawable();

        List<ThaumcraftRecipeWrapper.Arcane> arcane = new ArrayList<ThaumcraftRecipeWrapper.Arcane>();
        for (JeiRecipeData.Arcane recipe : data.arcane) {
            ThaumcraftRecipeWrapper.Arcane wrapper = new ThaumcraftRecipeWrapper.Arcane(recipe, slot);
            arcane.add(wrapper);
            this.track(wrapper, ARCANE_UID);
        }
        List<ThaumcraftRecipeWrapper.Crucible> crucible = new ArrayList<ThaumcraftRecipeWrapper.Crucible>();
        for (JeiRecipeData.Crucible recipe : data.crucible) {
            ThaumcraftRecipeWrapper.Crucible wrapper = new ThaumcraftRecipeWrapper.Crucible(recipe, slot);
            crucible.add(wrapper);
            this.track(wrapper, CRUCIBLE_UID);
        }
        List<ThaumcraftRecipeWrapper.Infusion> infusion = new ArrayList<ThaumcraftRecipeWrapper.Infusion>();
        for (JeiRecipeData.Infusion recipe : data.infusion) {
            ThaumcraftRecipeWrapper.Infusion wrapper = new ThaumcraftRecipeWrapper.Infusion(recipe, slot);
            infusion.add(wrapper);
            this.track(wrapper, INFUSION_UID);
        }

        registry.addRecipes(arcane, ARCANE_UID);
        registry.addRecipes(crucible, CRUCIBLE_UID);
        registry.addRecipes(infusion, INFUSION_UID);
        registry.addRecipeCatalyst(new ItemStack(ConfigBlocks.blockTable, 1, 15), ARCANE_UID);
        registry.addRecipeCatalyst(new ItemStack(ConfigBlocks.blockMetalDevice, 1, 0), CRUCIBLE_UID);
        registry.addRecipeCatalyst(new ItemStack(ConfigBlocks.blockMetalDevice, 1, 10), CRUCIBLE_UID);
        registry.addRecipeCatalyst(new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2), INFUSION_UID);
        registry.addRecipeClickArea(GuiArcaneWorkbench.class, 160, 64, 16, 16, ARCANE_UID);

        Thaumcraft.log.info("Thaumcraft JEI integration registered: arcane={}, crucible={}, infusion={}, skipped={}",
                arcane.size(), crucible.size(), infusion.size(), data.getSkippedCount());
        if (!data.skipped.isEmpty()) {
            Thaumcraft.log.debug("Thaumcraft JEI skipped recipe classes: {}", data.skipped);
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        ResearchVisibility<ThaumcraftRecipeWrapper> visibility = new ResearchVisibility<ThaumcraftRecipeWrapper>(
                this.visibilityEntries,
                (recipe, uid, visible) -> {
                    if (visible) {
                        jeiRuntime.getRecipeRegistry().unhideRecipe(recipe, uid);
                    } else {
                        jeiRuntime.getRecipeRegistry().hideRecipe(recipe, uid);
                    }
                });
        visibility.initializeHidden();
        MinecraftForge.EVENT_BUS.register(new ResearchVisibilityTicker(visibility));
    }

    private void track(ThaumcraftRecipeWrapper wrapper, String uid) {
        if (wrapper.research != null && !wrapper.research.isEmpty()) {
            this.visibilityEntries.add(new ResearchVisibility.Entry<ThaumcraftRecipeWrapper>(
                    wrapper, uid, wrapper.research));
        }
    }
}
