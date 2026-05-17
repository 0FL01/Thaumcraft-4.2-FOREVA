package thaumcraft.common.items.relics;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemHandMirrorStaticGuardTest {

    @Test
    public void handMirrorKeepsLinkGlintTooltipAndFloorGuiCoords() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/relics/ItemHandMirror.java");

        assertTrue("Hand mirror GUI open must use MathHelper.floor coordinates",
                source.contains("MathHelper.floor(player.posX)")
                        && source.contains("MathHelper.floor(player.posY)")
                        && source.contains("MathHelper.floor(player.posZ)"));
        assertTrue("Hand mirror must keep NBT-linked visual glint contract",
                source.contains("public boolean hasEffect(ItemStack stack)")
                        && source.contains("return stack.hasTagCompound();"));
        assertTrue("Hand mirror must keep linked-destination tooltip contract",
                source.contains("new TextComponentTranslation(\"tc.handmirrorlinkedto\").getFormattedText()")
                        && source.contains("&& stack.getTagCompound().hasKey(\"dimname\")")
                        && source.contains("\" in \" + dimName"));
        assertTrue("Hand mirror transport must keep reference-facing spawn/motion direction contracts",
                source.contains("EnumFacing.byIndex(meta % 6)")
                        && !source.contains("EnumFacing.byIndex(meta % 6).getOpposite()")
                        && source.contains("+ 0.5D - (double) facing.getXOffset() * 0.3D")
                        && source.contains("+ 0.5D - (double) facing.getYOffset() * 0.3D")
                        && source.contains("+ 0.5D - (double) facing.getZOffset() * 0.3D")
                        && source.contains("entityItem.motionX = (double) facing.getXOffset() * 0.15D;")
                        && source.contains("entityItem.motionY = (double) facing.getYOffset() * 0.15D;")
                        && source.contains("entityItem.motionZ = (double) facing.getZOffset() * 0.15D;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
