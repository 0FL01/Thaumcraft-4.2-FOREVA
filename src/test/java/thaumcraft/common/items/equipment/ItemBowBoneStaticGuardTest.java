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
        assertTrue("ItemBowBone must keep the TC4 normal-arrow release curve and bonus",
                source.contains("onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft)")
                        && source.contains("ForgeEventFactory.onArrowLoose")
                        && source.contains("(float) charge / 10.0F")
                        && source.contains("velocity * 2.5F")
                         && source.contains("arrow.setDamage(arrow.getDamage() + 0.5D)")
                         && !source.contains("arrow.setIsCritical(true)"));
        assertTrue("ItemBowBone must allow TC4 Infinity nocking without inventory arrows",
                source.contains("onItemRightClick(World world, EntityPlayer player, EnumHand hand)")
                        && source.contains("EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0")
                        && source.contains("player.setActiveHand(hand);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
