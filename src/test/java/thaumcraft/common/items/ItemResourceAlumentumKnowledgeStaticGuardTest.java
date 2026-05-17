package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemResourceAlumentumKnowledgeStaticGuardTest {

    @Test
    public void itemResourceShouldKeepAlumentumThrowAndKnowledgeFragmentUseContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemResource.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(source.contains("if (stack.getItemDamage() == META_ALUMENTUM)"));
        assertTrue(source.contains("new EntityAlumentum(world, player)"));
        assertTrue(source.contains("projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.75F, 1.0F);"));
        assertTrue(source.contains("world.playSound(null, player.posX, player.posY, player.posZ"));
        assertTrue(source.contains("SoundEvents.ENTITY_ARROW_SHOOT"));
        assertTrue(source.contains("if (i == META_BRAIN)"));
        assertTrue(source.contains("else if (stack.getItemDamage() == META_KNOWLEDGE_FRAGMENT)"));
        assertTrue(source.contains("for (Aspect aspect : Aspect.getPrimalAspects())"));
        assertTrue(source.contains("short amount = (short) (world.rand.nextInt(2) + 1);"));
        assertTrue(source.contains("new PacketAspectPool(aspect.getTag(), amount, knowledge.getAspectPoolFor(aspect))"));
        assertTrue(source.contains("ResearchManager.updateCache(player);"));
        assertTrue(source.contains("if (stack.getItemDamage() != META_NITOR)"));
        assertTrue(source.contains("!block.isReplaceable(world, pos)"));
        assertTrue(source.contains("return EnumActionResult.PASS;"));
        assertTrue(source.contains("world.mayPlace(ConfigBlocks.blockAiry, pos, false, facing, player)"));
        assertTrue(source.contains("placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, ConfigBlocks.blockAiry, META_NITOR)"));
        assertTrue(source.contains("return stack.getItemDamage() == META_CHARM ? 1 : super.getItemStackLimit(stack);"));
        assertTrue(source.contains("stack.getItemDamage() == META_TAINT_SLIME || stack.getItemDamage() == META_TAINT_TENDRIL"));
        assertTrue(source.contains("new PotionEffect(Config.potionFluxTaint, 120, 0, false, true)"));
        assertTrue(source.contains("InventoryUtils.consumeInventoryItem(player, stack.getItem(), stack.getItemDamage())"));
        assertTrue(source.contains("stack.getTagCompound().removeTag(\"blurb\")"));
        assertTrue(source.contains("new EntityAspectOrb(world, entity.posX, entity.posY, entity.posZ, aspect, 1)"));
        assertTrue(source.contains("ResearchManager.addResearch(player, \"@FOCUSPRIMAL\")"));
        assertTrue(source.contains("I18n.translateToLocal(\"tc.primalcharm.trigger\")"));
        assertTrue(source.contains("I18n.translateToLocal(\"tc.primalcharm.\" + rand.nextInt(5))"));
        assertTrue(source.contains("return aspects.size() > 0 ? aspects : null;"));
        assertTrue(lang.contains("tc.taint_item_poison=A dissolving %s has infected you with Taint"));
        assertTrue(lang.contains("tc.primalcharm.0=It seems to be leaking"));
        assertTrue(lang.contains("tc.primalcharm.1=You think you hear whispering"));
        assertTrue(lang.contains("tc.primalcharm.2=It is vibrating violently"));
        assertTrue(lang.contains("tc.primalcharm.3=It's humming is quite soothing"));
        assertTrue(lang.contains("tc.primalcharm.4=Wait, did it just flash a seventh color?"));
        assertTrue(lang.contains("tc.primalcharm.trigger=For a moment strange energies surround the primal charm. They dissipate quickly, but you are left strangely inspired..."));
    }

    @Test
    public void dispenserAndProjectileContractsShouldMatchReferenceShape() throws IOException {
        String behavior = readFile("src/main/java/thaumcraft/common/items/BehaviorDispenseAlumetum.java");
        String entity = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityAlumentum.java");
        String thaumcraft = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");

        assertTrue(behavior.contains("class BehaviorDispenseAlumetum extends BehaviorProjectileDispense"));
        assertTrue(behavior.contains("if (stack.getItemDamage() != ItemResource.META_ALUMENTUM)"));
        assertTrue(behavior.contains("return FALLBACK.dispense(source, stack);"));
        assertTrue(behavior.contains("new EntityAlumentum(worldIn, position.getX(), position.getY(), position.getZ())"));
        assertTrue(behavior.contains("source.getWorld().playEvent(1009, source.getBlockPos(), 0);"));
        assertTrue(entity.contains("protected float getGravityVelocity() { return 0.03f; }"));
        assertTrue(thaumcraft.contains("BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ConfigItems.itemResource, new BehaviorDispenseAlumetum());"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
