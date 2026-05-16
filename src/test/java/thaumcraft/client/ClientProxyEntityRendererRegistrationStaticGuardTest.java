package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyEntityRendererRegistrationStaticGuardTest {

    @Test
    public void entityRendererBootstrapStaysWired() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String noopRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderNoop.java");

        assertTrue("ClientProxy must keep setupEntityRenderers entry-point",
                source.contains("private void setupEntityRenderers()"));
        assertTrue("ClientProxy must keep non-noop RenderEntityItem registrations for item-like entities",
                source.contains("registerEntityRenderer(EntitySpecialItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityPermanentItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityFollowingItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityItemGrate.class, manager -> new RenderEntityItem(manager, renderItem), registered);"));
        assertTrue("ClientProxy must keep non-noop projectile RenderSnowball baseline registrations",
                source.contains("registerEntityRenderer(EntityDart.class, manager -> new RenderSnowball<>(manager, Items.ARROW, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityPrimalArrow.class,")
                        && source.contains("registerEntityRenderer(EntityBottleTaint.class,")
                        && source.contains("registerEntityRenderer(EntityAlumentum.class, manager -> new RenderSnowball<>(manager, Items.FIRE_CHARGE, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityPrimalOrb.class, manager -> new RenderSnowball<>(manager, Items.ENDER_EYE, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityFrostShard.class, manager -> new RenderSnowball<>(manager, Items.SNOWBALL, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityPechBlast.class, manager -> new RenderSnowball<>(manager, Items.BLAZE_POWDER, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchOrb.class, manager -> new RenderSnowball<>(manager, Items.ENDER_PEARL, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityGolemOrb.class, manager -> new RenderSnowball<>(manager, Items.SLIME_BALL, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityShockOrb.class, manager -> new RenderSnowball<>(manager, Items.GLOWSTONE_DUST, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityExplosiveOrb.class, manager -> new RenderSnowball<>(manager, Items.FIREWORK_CHARGE, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityEmber.class, manager -> new RenderSnowball<>(manager, Items.BLAZE_POWDER, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityGolemBobber.class, manager -> new RenderSnowball<>(manager, Items.FISHING_ROD, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityAspectOrb.class, manager -> new RenderSnowball<>(manager, Items.ENDER_EYE, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityFallingTaint.class, manager -> new RenderSnowball<>(manager, Items.SLIME_BALL, renderItem), registered);"));
        assertTrue("ClientProxy must keep vanilla mob fallback renderer registrations for compatible zombie/spider groups",
                source.contains("registerEntityRenderer(EntityBrainyZombie.class, RenderZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityGiantBrainyZombie.class, RenderZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityInhabitedZombie.class, RenderZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityMindSpider.class, RenderSpider::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSpider.class, RenderSpider::new, registered);"));
        assertTrue("ClientProxy must keep fallback RenderFallbackLiving registrations for taint animal-like entities",
                source.contains("registerEntityRenderer(EntityTaintChicken.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintCow.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintPig.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintSheep.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintVillager.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintCreeper.class, manager -> new RenderFallbackLiving<>("));
        assertTrue("ClientProxy must keep fallback RenderFallbackBiped registrations for cultist entities",
                source.contains("registerEntityRenderer(EntityCultistKnight.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityCultistCleric.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityCultistLeader.class, manager -> new RenderFallbackBiped<>("));
        assertTrue("ClientProxy must keep extended fallback registrations for the remaining Stage 8-d monster baseline",
                source.contains("registerEntityRenderer(EntityFireBat.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityWisp.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityPech.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityEldritchGuardian.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityEldritchWarden.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityEldritchGolem.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityEldritchCrab.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityThaumicSlime.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityTaintSpore.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityTaintSporeSwarmer.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityTaintSwarm.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityTaintacle.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintacleSmall.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintacleGiant.class, manager -> new RenderFallbackLiving<>("));
        assertTrue("ClientProxy must keep fallback registrations for remaining special entities",
                source.contains("registerEntityRenderer(EntityGolemBase.class, manager -> new RenderFallbackBiped<>(")
                        && source.contains("registerEntityRenderer(EntityTravelingTrunk.class, manager -> new RenderFallbackLiving<>(")
                        && source.contains("registerEntityRenderer(EntityCultistPortal.class, manager -> new RenderFallbackBiped<>("));
        assertTrue("RenderFallbackLiving must exist as a non-noop typed texture renderer",
                readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFallbackLiving.java").contains("extends RenderLiving<T>"));
        assertTrue("RenderFallbackBiped must exist as a non-noop typed texture renderer",
                readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFallbackBiped.java").contains("extends RenderBiped<T>"));
        assertTrue("ClientProxy must iterate ConfigEntities.ENTITIES for renderer registration coverage",
                source.contains("for (net.minecraftforge.fml.common.registry.EntityEntry entry : ConfigEntities.ENTITIES)"));
        assertTrue("ClientProxy must keep fallback RenderNoop registrations for remaining entities",
                source.contains("if (registered.contains(entityClass))")
                        && source.contains("RenderingRegistry.registerEntityRenderingHandler(entityClass, RenderNoop::new);"));
        assertTrue("RenderNoop must stay as a side-safe doRender baseline",
                noopRenderer.contains("extends Render<T>")
                        && noopRenderer.contains("public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
