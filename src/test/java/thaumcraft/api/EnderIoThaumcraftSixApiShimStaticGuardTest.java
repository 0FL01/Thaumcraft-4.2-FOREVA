package thaumcraft.api;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EnderIoThaumcraftSixApiShimStaticGuardTest {

    @Test
    public void enderIoThaumcraftSixArmorApiShimsMustStayAvailable() throws IOException {
        String goggles = readFile("src/main/java/thaumcraft/api/items/IGoggles.java");
        String revealer = readFile("src/main/java/thaumcraft/api/items/IRevealer.java");
        String discount = readFile("src/main/java/thaumcraft/api/items/IVisDiscountGear.java");

        assertTrue("EnderIO expects thaumcraft.api.items.IGoggles",
                goggles.contains("package thaumcraft.api.items;")
                        && goggles.contains("extends thaumcraft.api.IGoggles"));
        assertTrue("EnderIO expects thaumcraft.api.items.IRevealer",
                revealer.contains("package thaumcraft.api.items;")
                        && revealer.contains("extends thaumcraft.api.nodes.IRevealer"));
        assertTrue("EnderIO expects thaumcraft.api.items.IVisDiscountGear with the TC6 two-argument method",
                discount.contains("package thaumcraft.api.items;")
                        && discount.contains("extends thaumcraft.api.IVisDiscountGear")
                        && discount.contains("int getVisDiscount(ItemStack stack, EntityPlayer player);")
                        && discount.contains("default int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect)"));
    }

    @Test
    public void infusionStabiliserMustExposeThaumcraftSixBlockPosOverload() throws IOException {
        String stabiliser = readFile("src/main/java/thaumcraft/api/crafting/IInfusionStabiliser.java");

        assertTrue("EnderIO references the TC6 BlockPos overload of IInfusionStabiliser",
                stabiliser.contains("import net.minecraft.util.math.BlockPos;")
                        && stabiliser.contains("default boolean canStabaliseInfusion(World world, BlockPos pos)")
                        && stabiliser.contains("return canStabaliseInfusion(world, pos.getX(), pos.getY(), pos.getZ());"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
