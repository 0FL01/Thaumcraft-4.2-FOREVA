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
        assertTrue("ClientProxy must keep dedicated taint animal-like renderer registrations",
                source.contains("registerEntityRenderer(EntityTaintChicken.class, RenderTaintChicken::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintCow.class, RenderTaintCow::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintPig.class, RenderTaintPig::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSheep.class, RenderTaintSheep::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintVillager.class, RenderTaintVillager::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintCreeper.class, RenderTaintCreeper::new, registered);"));
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
        assertTrue("ClientProxy should not retain obsolete RenderTaintTextureLiving references",
                !source.contains("RenderTaintTextureLiving"));
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
                        && mindSpiderRenderer.contains("entity.spiderScaleAmount()")
                        && mindSpiderRenderer.contains("Math.min(0.1F, entity.ticksExisted / 100.0F)")
                        && mindSpiderRenderer.contains("GlStateManager.alphaFunc(516, 0.003921569F)")
                        && mindSpiderRenderer.contains("GlStateManager.depthMask(false)")
                        && mindSpiderRenderer.contains("int i = 61680;")
                        && mindSpiderRenderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && mindSpiderRenderer.contains("entity.getViewer()")
                        && mindSpiderRenderer.contains("Minecraft.getMinecraft().player.getName()"));
        String taintSpiderRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSpider.java");
        assertTrue("RenderTaintSpider must provide taint spider texture, eyes, and y-scale baseline",
                taintSpiderRenderer.contains("extends RenderLiving<EntityTaintSpider>")
                        && taintSpiderRenderer.contains("textures/models/taint_spider.png")
                        && taintSpiderRenderer.contains("textures/models/taint_spider_eyes.png")
                        && taintSpiderRenderer.contains("this.addLayer(new SpiderEyesLayer())")
                        && taintSpiderRenderer.contains("entity.spiderScaleAmount()")
                        && taintSpiderRenderer.contains("int i = 61680;")
                        && taintSpiderRenderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && taintSpiderRenderer.contains("scale * 1.25F"));
        String taintCreeperRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintCreeper.java");
        assertTrue("RenderTaintCreeper must provide creeper texture plus flash scale/color multiplier baseline",
                taintCreeperRenderer.contains("extends RenderLiving<EntityTaintCreeper>")
                        && taintCreeperRenderer.contains("textures/models/creeper.png")
                        && taintCreeperRenderer.contains("new ResourceLocation(\"thaumcraft\", \"textures/entity/creeper/creeper_armor.png\")")
                        && taintCreeperRenderer.contains("getCreeperFlashIntensity")
                        && taintCreeperRenderer.contains("preRenderCallback")
                        && taintCreeperRenderer.contains("getColorMultiplier")
                        && taintCreeperRenderer.contains("class CreeperArmorLayer")
                        && taintCreeperRenderer.contains("this.addLayer(new CreeperArmorLayer(this))")
                        && taintCreeperRenderer.contains("entity.getPowered()"));
        String taintVillagerRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintVillager.java");
        assertTrue("RenderTaintVillager must provide villager texture and pre-render scale baseline",
                taintVillagerRenderer.contains("extends RenderLiving<EntityTaintVillager>")
                        && taintVillagerRenderer.contains("textures/models/villager.png")
                        && taintVillagerRenderer.contains("GlStateManager.scale(scale, scale, scale)")
                        && taintVillagerRenderer.contains("0.9375F"));
        String taintChickenRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintChicken.java");
        assertTrue("RenderTaintChicken must provide chicken texture and wing rotation baseline",
                taintChickenRenderer.contains("extends RenderLiving<EntityTaintChicken>")
                        && taintChickenRenderer.contains("textures/models/chicken.png")
                        && taintChickenRenderer.contains("handleRotationFloat")
                        && taintChickenRenderer.contains("field_756_e")
                        && taintChickenRenderer.contains("field_752_b")
                        && taintChickenRenderer.contains("destPos")
                        && taintChickenRenderer.contains("MathHelper.sin"));
        String taintCowRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintCow.java");
        assertTrue("RenderTaintCow must provide cow texture baseline",
                taintCowRenderer.contains("extends RenderLiving<EntityTaintCow>")
                        && taintCowRenderer.contains("textures/models/cow.png"));
        String taintPigRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintPig.java");
        assertTrue("RenderTaintPig must provide pig texture baseline",
                taintPigRenderer.contains("extends RenderLiving<EntityTaintPig>")
                        && taintPigRenderer.contains("textures/models/pig.png"));
        String taintSheepRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSheep.java");
        assertTrue("RenderTaintSheep must provide sheep texture and fur-layer baseline",
                taintSheepRenderer.contains("extends RenderLiving<EntityTaintSheep>")
                        && taintSheepRenderer.contains("textures/models/sheep.png")
                        && taintSheepRenderer.contains("textures/models/sheep_fur.png")
                        && taintSheepRenderer.contains("class SheepFurLayer")
                        && taintSheepRenderer.contains("this.addLayer(new SheepFurLayer(this))")
                        && taintSheepRenderer.contains("entity.getSheared()")
                        && taintSheepRenderer.contains("ModelTaintSheep1")
                        && taintSheepRenderer.contains("ModelTaintSheep2"));
        String taintSheepModel1 = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintSheep1.java");
        assertTrue("ModelTaintSheep1 must avoid vanilla EntitySheep cast and use taint sheep head animation hooks",
                taintSheepModel1.contains("extends ModelQuadruped")
                        && taintSheepModel1.contains("instanceof EntityTaintSheep")
                        && taintSheepModel1.contains("getHeadRotationPointY")
                        && taintSheepModel1.contains("getHeadRotationAngleX"));
        String taintSheepModel2 = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintSheep2.java");
        assertTrue("ModelTaintSheep2 must avoid vanilla EntitySheep cast and use taint sheep head animation hooks",
                taintSheepModel2.contains("extends ModelQuadruped")
                        && taintSheepModel2.contains("instanceof EntityTaintSheep")
                        && taintSheepModel2.contains("getHeadRotationPointY")
                        && taintSheepModel2.contains("getHeadRotationAngleX"));
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
        String taintCreeperEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintCreeper.java");
        assertTrue("EntityTaintCreeper must expose flash and powered accessors for renderer timing/layer paths",
                taintCreeperEntity.contains("public float getCreeperFlashIntensity(float partialTicks)"));
        assertTrue("EntityTaintCreeper must expose powered accessor for armor-layer rendering",
                taintCreeperEntity.contains("public boolean getPowered()"));
        assertTrue("EntityTaintCreeper must keep underwater/no-despawn baseline contracts",
                taintCreeperEntity.contains("public boolean canBreatheUnderwater()")
                        && taintCreeperEntity.contains("protected boolean canDespawn()")
                        && taintCreeperEntity.contains("public boolean attackEntityAsMob(net.minecraft.entity.Entity entityIn)")
                        && taintCreeperEntity.contains("public int getMaxFallHeight()")
                        && taintCreeperEntity.contains("return 3 + (int)(this.getHealth() - 1.0F);")
                        && taintCreeperEntity.contains("return true;")
                        && taintCreeperEntity.contains("return false;"));
        assertTrue("EntityTaintCreeper must keep reference-shaped NBT persistence contracts",
                taintCreeperEntity.contains("nbt.setBoolean(\"powered\"")
                        && taintCreeperEntity.contains("nbt.setShort(\"Fuse\"")
                        && taintCreeperEntity.contains("nbt.setByte(\"ExplosionRadius\"")
                        && taintCreeperEntity.contains("nbt.hasKey(\"Fuse\", 99)")
                        && taintCreeperEntity.contains("nbt.hasKey(\"ExplosionRadius\", 99)")
                        && taintCreeperEntity.contains("nbt.hasKey(\"powered\", 1)")
                        && taintCreeperEntity.contains("nbt.getBoolean(\"powered\")"));
        assertTrue("EntityTaintCreeper must keep reference-shaped fuse sound and state-driven ignite progression",
                taintCreeperEntity.contains("SoundEvents.ENTITY_CREEPER_PRIMED")
                        && taintCreeperEntity.contains("this.timeSinceIgnited += state;")
                        && taintCreeperEntity.contains("if (this.timeSinceIgnited < 0)")
                        && taintCreeperEntity.contains("createExplosion(this, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 1.5F, false)"));
        assertTrue("EntityTaintCreeper must keep early client taint FX baseline",
                taintCreeperEntity.contains("public void onLivingUpdate()")
                        && taintCreeperEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintCreeperEntity.contains("Thaumcraft.proxy.particleCount(10)")
                        && taintCreeperEntity.contains("Thaumcraft.proxy.taintLandFX(this)"));
        assertTrue("EntityTaintCreeper must keep fall-accelerated fuse baseline",
                taintCreeperEntity.contains("public void fall(float distance, float damageMultiplier)")
                        && taintCreeperEntity.contains("distance * 1.5F")
                        && taintCreeperEntity.contains("this.timeSinceIgnited > this.fuseTime - 5"));
        assertTrue("EntityTaintCreeper must keep post-explosion taint-poison splash baseline for nearby non-tainted living entities",
                taintCreeperEntity.contains("getEntitiesWithinAABB(EntityLivingBase.class")
                        && taintCreeperEntity.contains("instanceof thaumcraft.api.entities.ITaintedMob")
                        && taintCreeperEntity.contains("Config.potionFluxTaint")
                        && taintCreeperEntity.contains("entity.addPotionEffect(new PotionEffect"));
        assertTrue("EntityTaintCreeper must keep post-explosion taint biome/fibre spread baseline",
                taintCreeperEntity.contains("for (int i = 0; i < 10; i++)")
                        && taintCreeperEntity.contains("Utils.setBiomeAt(this.world, x, z, thaumcraft.common.lib.world.ThaumcraftWorldGenerator.biomeTaint)")
                        && taintCreeperEntity.contains("ConfigBlocks.blockTaintFibres.getDefaultState()")
                        && taintCreeperEntity.contains("this.world.isSideSolid(below, EnumFacing.UP, false)"));
        String taintSheepEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSheep.java");
        assertTrue("EntityTaintSheep must keep sheared-state data/NBT/shearing contracts for fur-layer renderer parity",
                taintSheepEntity.contains("DataParameter<Byte>")
                        && taintSheepEntity.contains("SHEEP_FLAGS")
                        && taintSheepEntity.contains("AIConvertGrass")
                        && taintSheepEntity.contains("convertGrassAI")
                        && taintSheepEntity.contains("tasks.addTask(2, this.convertGrassAI)")
                        && taintSheepEntity.contains("targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true))")
                        && taintSheepEntity.contains("targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false))")
                        && taintSheepEntity.contains("protected void updateAITasks()")
                        && taintSheepEntity.contains("this.sheepTimer = this.convertGrassAI.getConvertTimer();")
                        && taintSheepEntity.contains("sheepTimer")
                        && taintSheepEntity.contains("entityInit()")
                        && taintSheepEntity.contains("handleStatusUpdate(byte id)")
                        && taintSheepEntity.contains("getHeadRotationPointY(float partialTicks)")
                        && taintSheepEntity.contains("getHeadRotationAngleX(float partialTicks)")
                        && taintSheepEntity.contains("public boolean getSheared()")
                        && taintSheepEntity.contains("public void setSheared(boolean sheared)")
                        && taintSheepEntity.contains("setBaseValue(20.0D)")
                        && taintSheepEntity.contains("setBaseValue(3.0)")
                        && taintSheepEntity.contains("setBaseValue(0.25D)")
                        && taintSheepEntity.contains("public boolean canBreatheUnderwater()")
                        && taintSheepEntity.contains("protected boolean canDespawn()")
                        && taintSheepEntity.contains("public int getTotalArmorValue()")
                        && taintSheepEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintSheepEntity.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && taintSheepEntity.contains("SoundEvents.ENTITY_SHEEP_AMBIENT")
                        && taintSheepEntity.contains("this.world.rand.nextInt(3) == 0")
                        && taintSheepEntity.contains("return !this.getSheared();")
                        && taintSheepEntity.contains("this.setSheared(true);")
                        && taintSheepEntity.contains("Blocks.WOOL")
                        && taintSheepEntity.contains("\"Sheared\""));
        String taintVillagerEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintVillager.java");
        assertTrue("EntityTaintVillager must keep reference-shaped AI/village/attribute/fx/drop baseline contracts",
                taintVillagerEntity.contains("this(world, 0);")
                        && taintVillagerEntity.contains("private int randomTickDivider;")
                        && taintVillagerEntity.contains("PathNavigateGround")
                        && taintVillagerEntity.contains("setBreakDoors(true)")
                        && taintVillagerEntity.contains("setCanSwim(true)")
                        && taintVillagerEntity.contains("new EntityAIMoveIndoors(this)")
                        && taintVillagerEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintVillagerEntity.contains("new EntityAIMoveThroughVillage(this, 1.0D, false)")
                        && taintVillagerEntity.contains("new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F)")
                        && taintVillagerEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintVillagerEntity.contains("setBaseValue(30.0D)")
                        && taintVillagerEntity.contains("setBaseValue(4.0D)")
                        && taintVillagerEntity.contains("setBaseValue(0.3D)")
                        && taintVillagerEntity.contains("public boolean canBreatheUnderwater()")
                        && taintVillagerEntity.contains("protected boolean canDespawn()")
                        && taintVillagerEntity.contains("this.world.getVillageCollection().addToVillagerPositionList(pos)")
                        && taintVillagerEntity.contains("this.villageObj = this.world.getVillageCollection().getNearestVillage(pos, 32)")
                        && taintVillagerEntity.contains("this.villageObj.addOrRenewAgressor(livingBase)")
                        && taintVillagerEntity.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && taintVillagerEntity.contains("SoundEvents.ENTITY_VILLAGER_AMBIENT")
                        && taintVillagerEntity.contains("SoundEvents.ENTITY_VILLAGER_HURT")
                        && taintVillagerEntity.contains("SoundEvents.ENTITY_VILLAGER_DEATH")
                        && taintVillagerEntity.contains("this.world.rand.nextInt(2) == 0")
                        && taintVillagerEntity.contains("this.world.rand.nextInt(13) < 1 + looting")
                        && taintVillagerEntity.contains("itemResource, 1, 18"));
        String taintSwarmEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSwarm.java");
        assertTrue("EntityTaintSwarm must keep reference-shaped summoned/flight/attack/NBT/drop baseline contracts",
                taintSwarmEntity.contains("private static final byte FLAG_SUMMONED = 2")
                        && taintSwarmEntity.contains("DataParameter<Byte> FLAGS")
                        && taintSwarmEntity.contains("private BlockPos currentFlightTarget;")
                        && taintSwarmEntity.contains("public int damBonus = 0;")
                        && taintSwarmEntity.contains("this.setSize(2.0F, 2.0F);")
                        && taintSwarmEntity.contains("setBaseValue(30.0D)")
                        && taintSwarmEntity.contains("setBaseValue(2.0D + this.damBonus)")
                        && taintSwarmEntity.contains("public boolean getIsSummoned()")
                        && taintSwarmEntity.contains("public void setIsSummoned(boolean summoned)")
                        && taintSwarmEntity.contains("return 15728880;")
                        && taintSwarmEntity.contains("return 1.0F;")
                        && taintSwarmEntity.contains("this.motionY *= 0.6000000238418579D;")
                        && taintSwarmEntity.contains("Thaumcraft.proxy.particleCount(25)")
                        && taintSwarmEntity.contains("this.attackEntityFrom(DamageSource.STARVE, 5.0F);")
                        && taintSwarmEntity.contains("this.world.getClosestPlayerToEntity(this, 12.0D)")
                        && taintSwarmEntity.contains("this.world.getBiome(pos) == ThaumcraftWorldGenerator.biomeTaint")
                        && taintSwarmEntity.contains("target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 0));")
                        && taintSwarmEntity.contains("EntityUtils.setRecentlyHit(target, 100);")
                        && taintSwarmEntity.contains("nbt.setByte(\"Flags\"")
                        && taintSwarmEntity.contains("nbt.setByte(\"damBonus\"")
                        && taintSwarmEntity.contains("nbt.getByte(\"Flags\")")
                        && taintSwarmEntity.contains("nbt.getByte(\"damBonus\")")
                        && taintSwarmEntity.contains("this.world.rand.nextBoolean()")
                        && taintSwarmEntity.contains("itemResource, 1, 11"));
        String mindSpiderEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityMindSpider.java");
        assertTrue("EntityMindSpider must expose viewer accessor and synced harmless/viewer data contracts",
                mindSpiderEntity.contains("public String getViewer()")
                        && mindSpiderEntity.contains("private static final DataParameter<Byte> HARMLESS")
                        && mindSpiderEntity.contains("private static final DataParameter<String> VIEWER")
                        && mindSpiderEntity.contains("this.dataManager.register(HARMLESS, (byte) 0)")
                        && mindSpiderEntity.contains("this.dataManager.register(VIEWER, \"\")")
                        && mindSpiderEntity.contains("SharedMonsterAttributes.ATTACK_DAMAGE")
                        && !mindSpiderEntity.contains("SharedMonsterAttributes.MOVEMENT_SPEED")
                        && mindSpiderEntity.contains("public boolean isHarmless()")
                        && mindSpiderEntity.contains("public float spiderScaleAmount()")
                        && mindSpiderEntity.contains("public double getYOffset()")
                        && mindSpiderEntity.contains("public boolean canBeCollidedWith()")
                        && mindSpiderEntity.contains("protected boolean canTriggerWalking()")
                        && mindSpiderEntity.contains("public boolean attackEntityAsMob(")
                        && mindSpiderEntity.contains("return super.attackEntityAsMob(entityIn);")
                        && mindSpiderEntity.contains("this.dataManager.set(HARMLESS, harmless ? (byte) 1 : (byte) 0)")
                        && mindSpiderEntity.contains("nbt.setByte(\"harmless\"")
                        && mindSpiderEntity.contains("nbt.setString(\"viewer\""));
        String taintSpiderEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSpider.java");
        assertTrue("EntityTaintSpider must keep reference-shaped scale, size, attributes, and loot-drop contracts",
                taintSpiderEntity.contains("public float spiderScaleAmount()")
                        && taintSpiderEntity.contains("return 0.4F;")
                        && taintSpiderEntity.contains("this.setSize(0.4F, 0.3F);")
                        && taintSpiderEntity.contains("this.experienceValue = 2;")
                        && taintSpiderEntity.contains("setBaseValue(5.0D)")
                        && taintSpiderEntity.contains("setBaseValue(2.0D)")
                        && taintSpiderEntity.contains("public double getYOffset()")
                        && taintSpiderEntity.contains("return 0.1D;")
                        && taintSpiderEntity.contains("this.world.rand.nextInt(6) == 0"));
        String taintChickenEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintChicken.java");
        assertTrue("EntityTaintChicken must keep reference-shaped AI/fall/sound baseline contracts",
                taintChickenEntity.contains("this.tasks.addTask(0, new EntityAISwimming(this));")
                        && taintChickenEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintChickenEntity.contains("new EntityAILeapAtTarget(this, 0.3F)")
                        && taintChickenEntity.contains("new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true)")
                        && taintChickenEntity.contains("new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, true)")
                        && taintChickenEntity.contains("new EntityAIWander(this, 1.0D)")
                        && taintChickenEntity.contains("new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F)")
                        && taintChickenEntity.contains("new EntityAILookIdle(this)")
                        && taintChickenEntity.contains("new EntityAIHurtByTarget(this, false)")
                        && taintChickenEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintChickenEntity.contains("new EntityAINearestAttackableTarget(this, EntityVillager.class, false)")
                        && taintChickenEntity.contains("new EntityAINearestAttackableTarget(this, EntityAnimal.class, false)")
                        && taintChickenEntity.contains("public boolean canBreatheUnderwater()")
                        && taintChickenEntity.contains("protected boolean canDespawn()")
                        && taintChickenEntity.contains("public void fall(float distance, float damageMultiplier)")
                        && taintChickenEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintChickenEntity.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && taintChickenEntity.contains("SoundEvents.ENTITY_CHICKEN_HURT"));
        String taintCowEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintCow.java");
        assertTrue("EntityTaintCow must keep reference-shaped AI/attribute/sound baseline contracts",
                taintCowEntity.contains("this.setSize(0.9F, 1.3F);")
                        && taintCowEntity.contains("PathNavigateGround")
                        && taintCowEntity.contains("setCanSwim(true)")
                        && taintCowEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintCowEntity.contains("new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true)")
                        && taintCowEntity.contains("new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, false)")
                        && taintCowEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintCowEntity.contains("new EntityAINearestAttackableTarget(this, EntityVillager.class, false)")
                        && taintCowEntity.contains("new EntityAINearestAttackableTarget(this, EntityAnimal.class, false)")
                        && taintCowEntity.contains("setBaseValue(40.0D)")
                        && taintCowEntity.contains("setBaseValue(6.0D)")
                        && taintCowEntity.contains("setBaseValue(0.27D)")
                        && taintCowEntity.contains("public boolean canBreatheUnderwater()")
                        && taintCowEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintCowEntity.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && taintCowEntity.contains("SoundEvents.ENTITY_COW_HURT"));
        String taintPigEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintPig.java");
        assertTrue("EntityTaintPig must keep reference-shaped AI/attribute/sound/drop baseline contracts",
                taintPigEntity.contains("this.setSize(0.9F, 0.9F);")
                        && taintPigEntity.contains("PathNavigateGround")
                        && taintPigEntity.contains("setCanSwim(true)")
                        && taintPigEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintPigEntity.contains("new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true)")
                        && taintPigEntity.contains("new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, false)")
                        && taintPigEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintPigEntity.contains("new EntityAINearestAttackableTarget(this, EntityVillager.class, false)")
                        && taintPigEntity.contains("new EntityAINearestAttackableTarget(this, EntityAnimal.class, false)")
                        && taintPigEntity.contains("setBaseValue(20.0D)")
                        && taintPigEntity.contains("setBaseValue(4.0D)")
                        && taintPigEntity.contains("setBaseValue(0.275D)")
                        && taintPigEntity.contains("public boolean canBreatheUnderwater()")
                        && taintPigEntity.contains("protected boolean canDespawn()")
                        && taintPigEntity.contains("public int getMaxSpawnedInChunk()")
                        && taintPigEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintPigEntity.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && taintPigEntity.contains("SoundEvents.ENTITY_PIG_AMBIENT")
                        && taintPigEntity.contains("this.world.rand.nextInt(3) == 0"));
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
