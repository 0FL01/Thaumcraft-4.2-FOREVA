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
        assertTrue("Hand mirror right-click no-link and invalid-link branches must keep PASS semantics",
                source.contains("return new ActionResult<>(EnumActionResult.PASS, stack);"));
        assertTrue("Hand mirror mirror-link client use-first branch must keep swing+PASS semantics",
                source.contains("player.swingArm(hand);")
                        && source.contains("if (world.isRemote) {")
                        && source.contains("return EnumActionResult.PASS;"));
        assertTrue("Hand mirror link/error feedback must keep reference-shaped sound and chat contracts",
                source.contains("world.playSound(null, pos, TCSounds.JAR, SoundCategory.BLOCKS, 1.0F, 2.0F);")
                        && source.contains("world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.ZAP, SoundCategory.PLAYERS, 1.0F, 0.8F);")
                        && source.contains("player.sendStatusMessage(new TextComponentTranslation(\"tc.handmirrorlinked\"), false);")
                        && source.contains("player.sendStatusMessage(new TextComponentTranslation(\"tc.handmirrorerror\"), false);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
