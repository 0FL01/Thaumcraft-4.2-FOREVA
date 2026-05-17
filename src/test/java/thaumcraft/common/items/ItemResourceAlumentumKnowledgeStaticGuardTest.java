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

        assertTrue(source.contains("if (stack.getItemDamage() == META_ALUMENTUM)"));
        assertTrue(source.contains("new EntityAlumentum(world, player)"));
        assertTrue(source.contains("projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.75F, 1.0F);"));
        assertTrue(source.contains("world.playSound(null, player.posX, player.posY, player.posZ"));
        assertTrue(source.contains("SoundEvents.ENTITY_ARROW_SHOOT"));
        assertTrue(source.contains("else if (stack.getItemDamage() == META_KNOWLEDGE_FRAGMENT)"));
        assertTrue(source.contains("for (Aspect aspect : Aspect.getPrimalAspects())"));
        assertTrue(source.contains("short amount = (short) (world.rand.nextInt(2) + 1);"));
        assertTrue(source.contains("new PacketAspectPool(aspect.getTag(), amount, knowledge.getAspectPoolFor(aspect))"));
        assertTrue(source.contains("ResearchManager.updateCache(player);"));
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
