package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GolemClientStateSyncStaticGuardTest {
    @Test
    public void fluidRenderingUsesFullWidthSynchronizedState() throws Exception {
        String entity = read("src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/entity/RenderGolemBase.java");

        assertTrue(entity.contains("DataParameter<String> CARRIED_FLUID")
                && entity.contains("DataParameter<Integer> CARRIED_FLUID_AMOUNT")
                && entity.contains("FluidRegistry.getFluidName(this.fluidCarried)"));
        assertFalse("Fluid amount must not be encoded into an ItemStack metadata field",
                entity.contains("new net.minecraft.item.ItemStack(fluidBlock, 1, this.fluidCarried.amount)"));
        assertTrue(renderer.contains("FluidStack fluidStack = entity.getFluidCarried();"));
        assertFalse(renderer.contains("entity.fluidCarried"));
        assertTrue("Decanting golems must always render the original bucket shell",
                renderer.contains("textures/models/bucket.obj")
                        && renderer.contains("textures/models/bucket.png")
                        && renderer.contains("renderModel(this.renderer.bucketModel);")
                        && renderer.contains("if (fluidStack != null && fluidStack.amount > 0"));
        assertFalse("An empty Decanting golem must not skip its bucket shell",
                renderer.contains("if (fluidStack == null || fluidStack.amount <= 0) return;"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
