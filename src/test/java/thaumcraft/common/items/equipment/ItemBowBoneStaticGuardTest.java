package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemBowBoneStaticGuardTest {

    @Test
    public void boneBowKeepsReferenceDamageEnchantAndEarlyReleaseContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/equipment/ItemBowBone.java");

        assertTrue("ItemBowBone must keep max damage and enchantability baseline contracts",
                source.contains("this.setMaxDamage(512);")
                        && source.contains("return 3;"));
        assertTrue("ItemBowBone must keep bone repair contract",
                source.contains("!repair.isEmpty() && repair.getItem() == Items.BONE"));
        assertTrue("ItemBowBone must keep early-release draw hook contract",
                source.contains("onUsingTick(ItemStack stack, EntityLivingBase entity, int count)")
                        && source.contains("int ticks = this.getMaxItemUseDuration(stack) - count;")
                        && source.contains("ticks > 18")
                        && source.contains("((EntityPlayer) entity).stopActiveHand();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
