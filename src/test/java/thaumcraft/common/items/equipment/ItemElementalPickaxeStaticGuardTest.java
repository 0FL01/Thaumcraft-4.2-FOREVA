package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemElementalPickaxeStaticGuardTest {

    @Test
    public void elementalPickaxeKeepsFireAndWandfailContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/equipment/ItemElementalPickaxe.java");

        assertTrue("ItemElementalPickaxe must keep tool class and rarity contracts",
                source.contains("ImmutableSet.of(\"pickaxe\")")
                        && source.contains("return EnumRarity.RARE;"));
        assertTrue("ItemElementalPickaxe must keep thaumium-ingot repair contract",
                source.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && source.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemElementalPickaxe must keep fire-on-hit contract",
                source.contains("public boolean onLeftClickEntity(")
                        && source.contains("!player.world.isRemote")
                        && source.contains("!(entity instanceof EntityPlayer)")
                        && source.contains("player.getServer() == null")
                        && source.contains("player.getServer().isPVPEnabled()")
                        && source.contains("entity.setFire(2);"));
        assertTrue("ItemElementalPickaxe must keep use durability and scan/wandfail cue contract",
                source.contains("stack.damageItem(5, player);")
                        && source.contains("world.playSound(null, pos, TCSounds.WANDFAIL")
                        && source.contains("Thaumcraft.proxy.startScan(player, pos, System.currentTimeMillis() + 5000L, 8);")
                        && source.contains("player.swingArm(hand);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
