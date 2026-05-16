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
                source.contains("registerEntityRenderer(EntityBrainyZombie.class, RenderBrainyZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityGiantBrainyZombie.class, RenderBrainyZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityInhabitedZombie.class, RenderInhabitedZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityMindSpider.class, RenderMindSpider::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSpider.class, RenderTaintSpider::new, registered);"));
        assertTrue("ClientProxy must keep dedicated RenderTaintTextureLiving registrations for taint animal-like entities",
                source.contains("registerEntityRenderer(EntityTaintChicken.class, manager -> new RenderTaintTextureLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintCow.class, manager -> new RenderTaintTextureLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintPig.class, manager -> new RenderTaintTextureLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintSheep.class, manager -> new RenderTaintTextureLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintVillager.class, manager -> new RenderTaintTextureLiving<>(")
                        && source.contains("registerEntityRenderer(EntityTaintCreeper.class, manager -> new RenderTaintTextureLiving<>("));
        assertTrue("ClientProxy must keep fallback RenderFallbackBiped registrations for cultist entities",
                source.contains("registerEntityRenderer(EntityCultistKnight.class, manager -> new RenderCultist<>(manager, 0.5F), registered);")
                        && source.contains("registerEntityRenderer(EntityCultistCleric.class, manager -> new RenderCultist<>(manager, 0.5F), registered);")
                        && source.contains("registerEntityRenderer(EntityCultistLeader.class, manager -> new RenderCultist<>(manager, 0.6F), registered);"));
        assertTrue("ClientProxy must keep extended fallback registrations for the remaining Stage 8-d monster baseline",
                source.contains("registerEntityRenderer(EntityFireBat.class, RenderFireBat::new, registered);")
                        && source.contains("registerEntityRenderer(EntityWisp.class, RenderWisp::new, registered);")
                        && source.contains("registerEntityRenderer(EntityPech.class, RenderPech::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchGuardian.class, RenderEldritchGuardian::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchWarden.class, RenderEldritchWarden::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchGolem.class, RenderEldritchGolem::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchCrab.class, RenderEldritchCrab::new, registered);")
                        && source.contains("registerEntityRenderer(EntityThaumicSlime.class, RenderThaumicSlime::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSpore.class, RenderTaintSpore::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSporeSwarmer.class, RenderTaintSporeSwarmer::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSwarm.class, RenderTaintSwarm::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintacle.class, manager -> new RenderTaintacle<>(manager, 0.6F, 1.0F), registered);")
                        && source.contains("registerEntityRenderer(EntityTaintacleSmall.class, manager -> new RenderTaintacle<>(manager, 0.45F, 0.85F), registered);")
                        && source.contains("registerEntityRenderer(EntityTaintacleGiant.class, manager -> new RenderTaintacle<>(manager, 0.8F, 1.33F), registered);"));
        assertTrue("ClientProxy must keep fallback registrations for remaining special entities",
                source.contains("registerEntityRenderer(EntityGolemBase.class, RenderGolemBase::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTravelingTrunk.class, RenderTravelingTrunk::new, registered);")
                        && source.contains("registerEntityRenderer(EntityCultistPortal.class, RenderCultistPortal::new, registered);"));
        assertTrue("RenderFallbackLiving must exist as a non-noop typed texture renderer",
                readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFallbackLiving.java").contains("extends RenderLiving<T>"));
        assertTrue("RenderTaintTextureLiving must exist as a dedicated taint texture renderer baseline",
                readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintTextureLiving.java").contains("extends RenderLiving<T>"));
        assertTrue("RenderFallbackBiped must exist as a non-noop typed texture renderer",
                readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFallbackBiped.java").contains("extends RenderBiped<T>"));
        String pechRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderPech.java");
        assertTrue("RenderPech must provide pech-type texture routing baseline",
                pechRenderer.contains("extends RenderBiped<EntityPech>")
                        && pechRenderer.contains("textures/models/pech_forage.png")
                        && pechRenderer.contains("textures/models/pech_thaum.png")
                        && pechRenderer.contains("textures/models/pech_stalker.png")
                        && pechRenderer.contains("entity.getPechType()"));
        String fireBatRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFireBat.java");
        assertTrue("RenderFireBat must provide vampire texture, scaling, and hanging transform baselines",
                fireBatRenderer.contains("extends RenderLiving<EntityFireBat>")
                        && fireBatRenderer.contains("textures/models/firebat.png")
                        && fireBatRenderer.contains("textures/models/vampirebat.png")
                        && fireBatRenderer.contains("entity.getIsVampire()")
                        && fireBatRenderer.contains("entity.getIsDevil()")
                        && fireBatRenderer.contains("entity.getIsBatHanging()"));
        String wispRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderWisp.java");
        assertTrue("RenderWisp must provide aspect-tinted texture baseline",
                wispRenderer.contains("extends RenderLiving<EntityWisp>")
                        && wispRenderer.contains("textures/misc/wispy.png")
                        && wispRenderer.contains("Aspect.getAspect(entity.getWispType())")
                        && wispRenderer.contains("GlStateManager.color(red, green, blue, 1.0F)")
                        && wispRenderer.contains("GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)"));
        String eldritchGuardianRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchGuardian.java");
        assertTrue("RenderEldritchGuardian must provide dedicated guardian texture baseline",
                eldritchGuardianRenderer.contains("extends RenderBiped<EntityEldritchGuardian>")
                        && eldritchGuardianRenderer.contains("textures/models/eldritch_guardian.png"));
        String eldritchWardenRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchWarden.java");
        assertTrue("RenderEldritchWarden must provide dedicated warden texture and scale baseline",
                eldritchWardenRenderer.contains("extends RenderBiped<EntityEldritchWarden>")
                        && eldritchWardenRenderer.contains("textures/models/eldritch_warden.png")
                        && eldritchWardenRenderer.contains("GlStateManager.scale(1.5F, 1.5F, 1.5F)"));
        String eldritchGolemRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchGolem.java");
        assertTrue("RenderEldritchGolem must provide dedicated golem texture and scale baseline",
                eldritchGolemRenderer.contains("extends RenderBiped<EntityEldritchGolem>")
                        && eldritchGolemRenderer.contains("textures/models/eldritch_golem.png")
                        && eldritchGolemRenderer.contains("GlStateManager.scale(2.15F, 2.15F, 2.15F)"));
        String eldritchCrabRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchCrab.java");
        assertTrue("RenderEldritchCrab must provide crab texture and overlay layer baseline",
                eldritchCrabRenderer.contains("extends RenderLiving<EntityEldritchCrab>")
                        && eldritchCrabRenderer.contains("textures/models/crab.png")
                        && eldritchCrabRenderer.contains("textures/models/craboverlay.png")
                        && eldritchCrabRenderer.contains("class CrabOverlayLayer")
                        && eldritchCrabRenderer.contains("this.addLayer(new CrabOverlayLayer())"));
        String cultistPortalRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderCultistPortal.java");
        assertTrue("RenderCultistPortal must provide dedicated portal texture baseline",
                cultistPortalRenderer.contains("extends RenderBiped<EntityCultistPortal>")
                        && cultistPortalRenderer.contains("textures/misc/cultist_portal.png"));
        String thaumicSlimeRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderThaumicSlime.java");
        assertTrue("RenderThaumicSlime must provide texture, scale, and gel layer baselines",
                thaumicSlimeRenderer.contains("extends RenderLiving<EntityThaumicSlime>")
                        && thaumicSlimeRenderer.contains("textures/models/tslime.png")
                        && thaumicSlimeRenderer.contains("entity.getSlimeSize()")
                        && thaumicSlimeRenderer.contains("entity.field_70812_c")
                        && thaumicSlimeRenderer.contains("entity.field_70811_b")
                        && thaumicSlimeRenderer.contains("class SlimeGelLayer")
                        && thaumicSlimeRenderer.contains("this.addLayer(new SlimeGelLayer())"));
        String taintSporeRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSpore.java");
        assertTrue("RenderTaintSpore must provide taint spore texture and display-size scale baseline",
                taintSporeRenderer.contains("extends RenderLiving<EntityTaintSpore>")
                        && taintSporeRenderer.contains("textures/models/taint_spore.png")
                        && taintSporeRenderer.contains("entity.displaySize")
                        && taintSporeRenderer.contains("entity.getSporeSize()"));
        String taintSporeSwarmerRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSporeSwarmer.java");
        assertTrue("RenderTaintSporeSwarmer must provide dedicated taint spore texture baseline",
                taintSporeSwarmerRenderer.contains("extends RenderLiving<EntityTaintSporeSwarmer>")
                        && taintSporeSwarmerRenderer.contains("textures/models/taint_spore.png"));
        String taintSwarmRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSwarm.java");
        assertTrue("RenderTaintSwarm must stay as dedicated noop baseline renderer",
                taintSwarmRenderer.contains("extends RenderNoop<EntityTaintSwarm>"));
        String taintacleRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintacle.java");
        assertTrue("RenderTaintacle must provide shared taintacle texture and scale baseline",
                taintacleRenderer.contains("extends RenderLiving<T>")
                        && taintacleRenderer.contains("textures/models/taintacle.png")
                        && taintacleRenderer.contains("scaleMultiplier")
                        && taintacleRenderer.contains("GlStateManager.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier)"));
        String brainyZombieRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderBrainyZombie.java");
        assertTrue("RenderBrainyZombie must provide dedicated brainy texture baseline",
                brainyZombieRenderer.contains("extends RenderZombie")
                        && brainyZombieRenderer.contains("textures/models/bzombie.png"));
        String inhabitedZombieRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderInhabitedZombie.java");
        assertTrue("RenderInhabitedZombie must provide dedicated czombie texture baseline",
                inhabitedZombieRenderer.contains("extends RenderZombie")
                        && inhabitedZombieRenderer.contains("textures/models/czombie.png"));
        String mindSpiderRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderMindSpider.java");
        assertTrue("RenderMindSpider must provide taint spider texture and eyes layer baseline",
                mindSpiderRenderer.contains("extends RenderLiving<EntityMindSpider>")
                        && mindSpiderRenderer.contains("textures/models/taint_spider.png")
                        && mindSpiderRenderer.contains("textures/models/taint_spider_eyes.png")
                        && mindSpiderRenderer.contains("this.addLayer(new SpiderEyesLayer())")
                        && mindSpiderRenderer.contains("entity.width / 1.4F"));
        String taintSpiderRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSpider.java");
        assertTrue("RenderTaintSpider must provide taint spider texture, eyes, and y-scale baseline",
                taintSpiderRenderer.contains("extends RenderLiving<EntityTaintSpider>")
                        && taintSpiderRenderer.contains("textures/models/taint_spider.png")
                        && taintSpiderRenderer.contains("textures/models/taint_spider_eyes.png")
                        && taintSpiderRenderer.contains("this.addLayer(new SpiderEyesLayer())")
                        && taintSpiderRenderer.contains("scale * 1.25F"));
        String cultistRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderCultist.java");
        assertTrue("RenderCultist must provide shared cultist texture baseline",
                cultistRenderer.contains("extends RenderBiped<T>")
                        && cultistRenderer.contains("textures/models/cultist.png"));
        String trunkRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTravelingTrunk.java");
        assertTrue("RenderTravelingTrunk must provide anger-based texture routing baseline",
                trunkRenderer.contains("extends RenderLiving<EntityTravelingTrunk>")
                        && trunkRenderer.contains("textures/models/trunk.png")
                        && trunkRenderer.contains("textures/models/trunkangry.png")
                        && trunkRenderer.contains("entity.getAnger() > 0"));
        String golemRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderGolemBase.java");
        assertTrue("RenderGolemBase must provide golem-type texture routing baseline",
                golemRenderer.contains("extends RenderBiped<EntityGolemBase>")
                        && golemRenderer.contains("entity.getGolemType()")
                        && golemRenderer.contains("golem_straw.png")
                        && golemRenderer.contains("golem_wood.png")
                        && golemRenderer.contains("golem_tallow.png")
                        && golemRenderer.contains("golem_clay.png")
                        && golemRenderer.contains("golem_flesh.png")
                        && golemRenderer.contains("golem_stone.png")
                        && golemRenderer.contains("golem_iron.png")
                        && golemRenderer.contains("golem_thaumium.png"));
        String travelingTrunkEntity = readFile("src/main/java/thaumcraft/common/entities/golems/EntityTravelingTrunk.java");
        assertTrue("EntityTravelingTrunk must expose anger accessor for renderer texture routing",
                travelingTrunkEntity.contains("public int getAnger()"));
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
