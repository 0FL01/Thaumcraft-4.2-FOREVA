package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemElementalSwordStaticGuardTest {

    @Test
    public void elementalSwordKeepsLiftAndChainHitContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/equipment/ItemElementalSword.java");

        assertTrue("ItemElementalSword must keep rarity and thaumium repair contracts",
                source.contains("return EnumRarity.RARE;")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && source.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemElementalSword must keep active-use hover contract",
                source.contains("player.setActiveHand(hand)")
                        && source.contains("return 72000;")
                        && source.contains("player.motionY += 0.08D;")
                        && source.contains("Utils.resetFloatCounter((EntityPlayerMP) player);")
                        && source.contains("stack.damageItem(1, player);"));
        assertTrue("ItemElementalSword must keep wind and smoke feedback contracts",
                source.contains("TCSounds.WIND")
                        && source.contains("Thaumcraft.proxy.smokeSpiral(")
                        && source.contains("Thaumcraft.proxy.drawGenericParticles(player.world")
                        && source.contains("false, 0, 8, -1, 8, 0, 0.8F, 1"));
        assertTrue("ItemElementalSword must keep chain-hit sweep contract",
                source.contains("entity.getEntityBoundingBox().grow(1.2D, 1.1D, 1.2D)")
                        && source.contains("player.attackTargetEntityWithCurrentItem(candidate);")
                        && source.contains("TCSounds.SWING"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
