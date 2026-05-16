package thaumcraft.client;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.client.gui.GuiArcaneBore;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.client.gui.GuiAlchemyFurnace;
import thaumcraft.client.gui.GuiDeconstructionTable;
import thaumcraft.client.gui.GuiFocalManipulator;
import thaumcraft.client.gui.GuiFocusPouch;
import thaumcraft.client.gui.GuiGolem;
import thaumcraft.client.gui.GuiHandMirror;
import thaumcraft.client.gui.GuiHoverHarness;
import thaumcraft.client.gui.GuiMagicBox;
import thaumcraft.client.gui.GuiPech;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.client.gui.GuiSpa;
import thaumcraft.client.gui.GuiThaumatorium;
import thaumcraft.client.gui.GuiTravelingTrunk;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.renderers.entity.RenderFallbackBiped;
import thaumcraft.client.renderers.entity.RenderFallbackLiving;
import thaumcraft.client.renderers.entity.RenderFireBat;
import thaumcraft.client.renderers.entity.RenderGolemBase;
import thaumcraft.client.renderers.entity.RenderEldritchGuardian;
import thaumcraft.client.renderers.entity.RenderEldritchGolem;
import thaumcraft.client.renderers.entity.RenderEldritchWarden;
import thaumcraft.client.renderers.entity.RenderEldritchCrab;
import thaumcraft.client.renderers.entity.RenderNoop;
import thaumcraft.client.renderers.entity.RenderPech;
import thaumcraft.client.renderers.entity.RenderTravelingTrunk;
import thaumcraft.client.renderers.entity.RenderWisp;
import thaumcraft.client.lib.ClientTickEventsFML;
import thaumcraft.client.lib.KeyHandler;
import thaumcraft.client.lib.RenderEventHandler;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.EntityAspectOrb;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.EntityItemGrate;
import thaumcraft.common.entities.EntityPermanentItem;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.golems.EntityGolemBobber;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.events.EventHandlerRunic;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityMindSpider;
import thaumcraft.common.entities.monster.EntityCultistCleric;
import thaumcraft.common.entities.monster.EntityCultistKnight;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityTaintChicken;
import thaumcraft.common.entities.monster.EntityTaintCow;
import thaumcraft.common.entities.monster.EntityTaintCreeper;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.entities.monster.EntityTaintSheep;
import thaumcraft.common.entities.monster.EntityTaintSpore;
import thaumcraft.common.entities.monster.EntityTaintSporeSwarmer;
import thaumcraft.common.entities.monster.EntityTaintSpider;
import thaumcraft.common.entities.monster.EntityTaintSwarm;
import thaumcraft.common.entities.monster.EntityTaintacle;
import thaumcraft.common.entities.monster.EntityTaintacleSmall;
import thaumcraft.common.entities.monster.EntityTaintVillager;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import thaumcraft.common.entities.projectile.EntityDart;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.entities.projectile.EntityEmber;
import thaumcraft.common.entities.projectile.EntityExplosiveOrb;
import thaumcraft.common.entities.projectile.EntityFrostShard;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.entities.projectile.EntityPechBlast;
import thaumcraft.common.entities.projectile.EntityPrimalArrow;
import thaumcraft.common.entities.projectile.EntityPrimalOrb;
import thaumcraft.common.entities.projectile.EntityShockOrb;
import thaumcraft.common.tiles.TileAlchemyFurnace;
import thaumcraft.common.tiles.TileArcaneBore;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileCrucible;
import thaumcraft.common.tiles.TileDeconstructionTable;
import thaumcraft.common.tiles.TileFocalManipulator;
import thaumcraft.common.tiles.TileResearchTable;
import thaumcraft.common.tiles.TileSpa;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.monster.EntityPech;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerDisplayInformation() {
        setupItemRenderers();
        setupEntityRenderers();
        setupBlockRenderers();
        setupTileRenderers();
    }

    private void setupItemRenderers() {
        for (Item item : ConfigItems.getAllItems()) {
            ResourceLocation registryName = item.getRegistryName();
            if (registryName == null) continue;
            ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
            for (int meta = 0; meta < 64; meta++) {
                ModelLoader.setCustomModelResourceLocation(item, meta, model);
            }
        }
        if (ConfigItems.itemManaBean != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemManaBean.getColorFromItemStack(stack),
                    ConfigItems.itemManaBean
            );
        }
        if (ConfigItems.itemEssence != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemEssence.getColorFromItemStack(stack, tintIndex),
                    ConfigItems.itemEssence
            );
        }
        if (ConfigItems.itemCrystalEssence != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemCrystalEssence.getColorFromItemStack(stack),
                    ConfigItems.itemCrystalEssence
            );
        }
        if (ConfigItems.itemWispEssence != null) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                    (stack, tintIndex) -> ConfigItems.itemWispEssence.getColorFromItemStack(stack),
                    ConfigItems.itemWispEssence
            );
        }
    }

    private void setupEntityRenderers() {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        Set<Class<? extends Entity>> registered = new HashSet<>();

        registerEntityRenderer(EntitySpecialItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);
        registerEntityRenderer(EntityPermanentItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);
        registerEntityRenderer(EntityFollowingItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);
        registerEntityRenderer(EntityItemGrate.class, manager -> new RenderEntityItem(manager, renderItem), registered);

        registerEntityRenderer(EntityDart.class, manager -> new RenderSnowball<>(manager, Items.ARROW, renderItem), registered);
        registerEntityRenderer(EntityPrimalArrow.class,
                manager -> new RenderSnowball<>(manager, itemOrFallback(ConfigItems.itemPrimalArrow, Items.ARROW), renderItem), registered);
        registerEntityRenderer(EntityBottleTaint.class,
                manager -> new RenderSnowball<>(manager, itemOrFallback(ConfigItems.itemBottleTaint, Items.SPLASH_POTION), renderItem), registered);
        registerEntityRenderer(EntityAlumentum.class, manager -> new RenderSnowball<>(manager, Items.FIRE_CHARGE, renderItem), registered);
        registerEntityRenderer(EntityPrimalOrb.class, manager -> new RenderSnowball<>(manager, Items.ENDER_EYE, renderItem), registered);
        registerEntityRenderer(EntityFrostShard.class, manager -> new RenderSnowball<>(manager, Items.SNOWBALL, renderItem), registered);
        registerEntityRenderer(EntityPechBlast.class, manager -> new RenderSnowball<>(manager, Items.BLAZE_POWDER, renderItem), registered);
        registerEntityRenderer(EntityEldritchOrb.class, manager -> new RenderSnowball<>(manager, Items.ENDER_PEARL, renderItem), registered);
        registerEntityRenderer(EntityGolemOrb.class, manager -> new RenderSnowball<>(manager, Items.SLIME_BALL, renderItem), registered);
        registerEntityRenderer(EntityShockOrb.class, manager -> new RenderSnowball<>(manager, Items.GLOWSTONE_DUST, renderItem), registered);
        registerEntityRenderer(EntityExplosiveOrb.class, manager -> new RenderSnowball<>(manager, Items.FIREWORK_CHARGE, renderItem), registered);
        registerEntityRenderer(EntityEmber.class, manager -> new RenderSnowball<>(manager, Items.BLAZE_POWDER, renderItem), registered);
        registerEntityRenderer(EntityGolemBobber.class, manager -> new RenderSnowball<>(manager, Items.FISHING_ROD, renderItem), registered);
        registerEntityRenderer(EntityAspectOrb.class, manager -> new RenderSnowball<>(manager, Items.ENDER_EYE, renderItem), registered);
        registerEntityRenderer(EntityFallingTaint.class, manager -> new RenderSnowball<>(manager, Items.SLIME_BALL, renderItem), registered);

        registerEntityRenderer(EntityBrainyZombie.class, RenderZombie::new, registered);
        registerEntityRenderer(EntityGiantBrainyZombie.class, RenderZombie::new, registered);
        registerEntityRenderer(EntityInhabitedZombie.class, RenderZombie::new, registered);
        registerEntityRenderer(EntityMindSpider.class, RenderSpider::new, registered);
        registerEntityRenderer(EntityTaintSpider.class, RenderSpider::new, registered);
        registerEntityRenderer(EntityTaintChicken.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelChicken(), 0.3F, new ResourceLocation("thaumcraft", "textures/models/chicken.png")), registered);
        registerEntityRenderer(EntityTaintCow.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelCow(), 0.7F, new ResourceLocation("thaumcraft", "textures/models/cow.png")), registered);
        registerEntityRenderer(EntityTaintPig.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelPig(), 0.5F, new ResourceLocation("thaumcraft", "textures/models/pig.png")), registered);
        registerEntityRenderer(EntityTaintSheep.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelSheep2(), 0.7F, new ResourceLocation("thaumcraft", "textures/models/sheep.png")), registered);
        registerEntityRenderer(EntityTaintVillager.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelVillager(0.0F), 0.5F, new ResourceLocation("thaumcraft", "textures/models/villager.png")), registered);
        registerEntityRenderer(EntityTaintCreeper.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelCreeper(), 0.5F, new ResourceLocation("thaumcraft", "textures/models/creeper.png")), registered);
        registerEntityRenderer(EntityCultistKnight.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.5F, new ResourceLocation("thaumcraft", "textures/models/cultist.png")), registered);
        registerEntityRenderer(EntityCultistCleric.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.5F, new ResourceLocation("thaumcraft", "textures/models/cultist.png")), registered);
        registerEntityRenderer(EntityCultistLeader.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.6F, new ResourceLocation("thaumcraft", "textures/models/cultist.png")), registered);
        registerEntityRenderer(EntityFireBat.class, RenderFireBat::new, registered);
        registerEntityRenderer(EntityWisp.class, RenderWisp::new, registered);
        registerEntityRenderer(EntityPech.class, RenderPech::new, registered);
        registerEntityRenderer(EntityEldritchGuardian.class, RenderEldritchGuardian::new, registered);
        registerEntityRenderer(EntityEldritchWarden.class, RenderEldritchWarden::new, registered);
        registerEntityRenderer(EntityEldritchGolem.class, RenderEldritchGolem::new, registered);
        registerEntityRenderer(EntityEldritchCrab.class, RenderEldritchCrab::new, registered);
        registerEntityRenderer(EntityThaumicSlime.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.5F, new ResourceLocation("thaumcraft", "textures/models/tslime.png")), registered);
        registerEntityRenderer(EntityTaintSpore.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.4F, new ResourceLocation("thaumcraft", "textures/models/taint_spore.png")), registered);
        registerEntityRenderer(EntityTaintSporeSwarmer.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.3F, new ResourceLocation("thaumcraft", "textures/models/taint_spore.png")), registered);
        registerEntityRenderer(EntityTaintSwarm.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.3F, new ResourceLocation("thaumcraft", "textures/models/taint_spore.png")), registered);
        registerEntityRenderer(EntityTaintacle.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelSpider(), 0.6F, new ResourceLocation("thaumcraft", "textures/models/taintacle.png")), registered);
        registerEntityRenderer(EntityTaintacleSmall.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelSpider(), 0.45F, new ResourceLocation("thaumcraft", "textures/models/taintacle.png")), registered);
        registerEntityRenderer(EntityTaintacleGiant.class, manager -> new RenderFallbackLiving<>(
                manager, new ModelSpider(), 0.8F, new ResourceLocation("thaumcraft", "textures/models/taintacle.png")), registered);
        registerEntityRenderer(EntityGolemBase.class, RenderGolemBase::new, registered);
        registerEntityRenderer(EntityTravelingTrunk.class, RenderTravelingTrunk::new, registered);
        registerEntityRenderer(EntityCultistPortal.class, manager -> new RenderFallbackBiped<>(
                manager, new ModelBiped(), 0.8F, new ResourceLocation("thaumcraft", "textures/misc/cultist_portal.png")), registered);

        for (net.minecraftforge.fml.common.registry.EntityEntry entry : ConfigEntities.ENTITIES) {
            @SuppressWarnings("unchecked")
            Class<? extends Entity> entityClass = (Class<? extends Entity>) entry.getEntityClass();
            if (registered.contains(entityClass)) {
                continue;
            }
            RenderingRegistry.registerEntityRenderingHandler(entityClass, RenderNoop::new);
        }
    }

    private static <T extends Entity> void registerEntityRenderer(
            Class<T> entityClass,
            net.minecraftforge.fml.client.registry.IRenderFactory<? super T> factory,
            Set<Class<? extends Entity>> registered) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, factory);
        registered.add(entityClass);
    }

    private static Item itemOrFallback(@Nullable Item preferred, Item fallback) {
        return preferred != null ? preferred : fallback;
    }

    private void setupBlockRenderers() {
        registerBlockItemModel(ConfigBlocks.blockMagicalLeavesItem, 0, "type=0");
        registerBlockItemModel(ConfigBlocks.blockMagicalLeavesItem, 1, "type=1");
    }

    private static void registerBlockItemModel(Item item, int meta, String variant) {
        if (item == null || item.getRegistryName() == null) {
            return;
        }
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(item.getRegistryName(), variant));
    }

    private void setupTileRenderers() {
    }

    @Override
    public void registerKeyBindings() {
        MinecraftForge.EVENT_BUS.register(new KeyHandler());
    }

    @Override
    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(new ClientTickEventsFML());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(ParticleEngine.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    private static final class ClientEventHandler {
        @SubscribeEvent
        public void onItemTooltip(ItemTooltipEvent event) {
            int charge = EventHandlerRunic.getFinalCharge(event.getItemStack());
            int warp = EventHandlerRunic.getFinalWarp(event.getItemStack(), event.getEntityPlayer());

            if (charge > 0) {
                event.getToolTip().add(TextFormatting.GOLD + I18n.translateToLocal("item.runic.charge") + " +" + charge);
            }
            if (warp > 0 && event.getEntityPlayer() != null) {
                event.getToolTip().add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.warping") + " " + warp);
            }
        }
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_FOCUS_POUCH:
                return new GuiFocusPouch(player.inventory, world, x, y, z);
            case GUI_HAND_MIRROR:
                return new GuiHandMirror(player.inventory, world, x, y, z);
            case GUI_HOVER_HARNESS:
                return new GuiHoverHarness(player.inventory, world, x, y, z);
            case GUI_GOLEM:
            {
                net.minecraft.entity.Entity entity = world.getEntityByID(x);
                return entity instanceof EntityGolemBase
                        ? new GuiGolem(player, (EntityGolemBase) entity)
                        : null;
            }
            case GUI_PECH:
            {
                net.minecraft.entity.Entity entity = world.getEntityByID(x);
                return entity instanceof EntityPech
                        ? new GuiPech(player, world, (EntityPech) entity)
                        : null;
            }
            case GUI_TRAVELING_TRUNK:
            {
                net.minecraft.entity.Entity entity = world.getEntityByID(x);
                return entity instanceof EntityTravelingTrunk
                        ? new GuiTravelingTrunk(player, (EntityTravelingTrunk) entity)
                        : null;
            }
            case GUI_THAUMATORIUM:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileThaumatorium
                        ? new GuiThaumatorium(player.inventory, (TileThaumatorium) tile)
                        : null;
            }
            case GUI_DECONSTRUCTION_TABLE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileDeconstructionTable
                        ? new GuiDeconstructionTable(player.inventory, (TileDeconstructionTable) tile)
                        : null;
            }
            case GUI_ALCHEMY_FURNACE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileAlchemyFurnace
                        ? new GuiAlchemyFurnace(player.inventory, (TileAlchemyFurnace) tile)
                        : null;
            }
            case GUI_RESEARCH_TABLE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileResearchTable
                        ? new GuiResearchTable(player, (TileResearchTable) tile)
                        : null;
            }
            case GUI_THAUMONOMICON:
                return new GuiResearchBrowser();
            case GUI_ARCANE_WORKBENCH:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileArcaneWorkbench
                        ? new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile)
                        : null;
            }
            case GUI_ARCANE_BORE:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileArcaneBore
                        ? new GuiArcaneBore(player.inventory, (TileArcaneBore) tile)
                        : null;
            }
            case GUI_MAGIC_BOX:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof IInventory
                        ? new GuiMagicBox(player.inventory, tile)
                        : null;
            }
            case GUI_SPA:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileSpa
                        ? new GuiSpa(player.inventory, (TileSpa) tile)
                        : null;
            }
            case GUI_FOCAL_MANIPULATOR:
            {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                return tile instanceof TileFocalManipulator
                        ? new GuiFocalManipulator(player.inventory, (TileFocalManipulator) tile)
                        : null;
            }
            default:
                return null;
        }
    }

    // ---- FX overrides ----

    @Override
    public void blockSparkle(World world, int x, int y, int z, int color, int count) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(1, count));
        if (amount <= 0) return;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());
        for (int i = 0; i < amount; i++) {
            double px = x + world.rand.nextFloat();
            double py = y + world.rand.nextFloat();
            double pz = z + world.rand.nextFloat();
            world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, red, green, blue);
        }
    }

    @Override
    public void beam(World world, double x, double y, double z, double tx, double ty, double tz, int color, boolean flicker, int ticks) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(4, ticks / 2));
        if (amount <= 0) return;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());

        double dx = tx - x;
        double dy = ty - y;
        double dz = tz - z;
        for (int i = 0; i <= amount; i++) {
            double t = amount == 0 ? 0.0 : (double) i / (double) amount;
            double px = x + dx * t + (world.rand.nextFloat() - 0.5f) * 0.08f;
            double py = y + dy * t + (world.rand.nextFloat() - 0.5f) * 0.08f;
            double pz = z + dz * t + (world.rand.nextFloat() - 0.5f) * 0.08f;
            if (flicker) {
                world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 0.0, 0.0, 0.0);
            } else {
                world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, red, green, blue);
            }
        }
    }

    @Override
    public void bolt(World world, double x, double y, double z, double tx, double ty, double tz, int color, int speed) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(6, speed * 2));
        if (amount <= 0) return;

        Color tint = decodeColor(color);
        float red = normalizeColor(tint.getRed());
        float green = normalizeColor(tint.getGreen());
        float blue = normalizeColor(tint.getBlue());

        double dx = tx - x;
        double dy = ty - y;
        double dz = tz - z;
        for (int i = 0; i <= amount; i++) {
            double t = amount == 0 ? 0.0 : (double) i / (double) amount;
            double noise = 0.18f;
            double px = x + dx * t + (world.rand.nextFloat() - 0.5f) * noise;
            double py = y + dy * t + (world.rand.nextFloat() - 0.5f) * noise;
            double pz = z + dz * t + (world.rand.nextFloat() - 0.5f) * noise;
            world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, red, green, blue);
            if (world.rand.nextBoolean()) {
                world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void burst(World world, double x, double y, double z, float scale) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(6, (int) (scale * 12.0f)));
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            double mx = (world.rand.nextFloat() - 0.5f) * 0.2f;
            double my = (world.rand.nextFloat() - 0.5f) * 0.2f;
            double mz = (world.rand.nextFloat() - 0.5f) * 0.2f;
            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, y, z, mx, my, mz);
        }
    }

    @Override
    public void wispFX3(World world, double x, double y, double z, double tx, double ty, double tz, float size, int count, boolean flag, float speed) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(2, (int) (size * 18.0f)));
        if (amount <= 0) return;

        float red = speed;
        if (red < 0.0f) red = 0.0f;
        if (red > 1.0f) red = 1.0f;
        float green = flag ? 0.8f : 0.4f;
        float blue = 1.0f - red * 0.5f;

        boolean hasTarget = tx != 0.0 || ty != 0.0 || tz != 0.0;
        for (int i = 0; i < amount; i++) {
            double px;
            double py;
            double pz;
            if (hasTarget) {
                double t = (double) i / (double) amount;
                px = x + (tx - x) * t + (world.rand.nextFloat() - 0.5f) * 0.12f;
                py = y + (ty - y) * t + (world.rand.nextFloat() - 0.5f) * 0.12f;
                pz = z + (tz - z) * t + (world.rand.nextFloat() - 0.5f) * 0.12f;
            } else {
                px = x + (world.rand.nextFloat() - 0.5f) * 0.55f;
                py = y + (world.rand.nextFloat() - 0.5f) * 0.55f;
                pz = z + (world.rand.nextFloat() - 0.5f) * 0.55f;
            }
            world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, red, green, blue);
        }
    }

    @Override
    public void wispFXEG(World world, double x, double y, double z, Entity target) {
        if (world == null || !world.isRemote || target == null) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            double tx = target.posX + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f;
            double ty = target.posY + target.height * 0.22f + (world.rand.nextFloat() - 0.5f) * 0.1f;
            double tz = target.posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f;
            wispFX3(world, x, y, z, tx, ty, tz, 0.45f, 0x66AAFF, true, 0.02f);
        }
    }

    @Override
    public void taintLandFX(Entity entity) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            float x = (float) (entity.posX + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.5f);
            float y = (float) ((entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) * 0.5);
            float z = (float) (entity.posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.5f);
            sparkle(x, y, z, 0.8f, 0x661166, -0.02f);
            if (world.rand.nextBoolean()) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.01, 0.0);
            }
        }
    }

    @Override
    public void slimeJumpFX(Entity entity, int size) {
        if (entity == null || entity.world == null || !entity.world.isRemote) return;
        World world = entity.world;
        int amount = particleCount(Math.max(1, size + 1));
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            float x = (float) (entity.posX + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f);
            float y = (float) ((entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) * 0.5);
            float z = (float) (entity.posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4f);
            sparkle(x, y, z, 0.7f, 0xAA22FF, 0.03f);
        }
    }

    @Override
    public void drawGenericParticles(World world, double x, double y, double z,
                                     double mx, double my, double mz,
                                     float red, float green, float blue, float alpha,
                                     boolean loop, int start, int num, int inc, int age, int delay, float scale) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(1, num / 2 + 1));
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            double px = x + (world.rand.nextFloat() - 0.5f) * 0.15f;
            double py = y + (world.rand.nextFloat() - 0.5f) * 0.15f;
            double pz = z + (world.rand.nextFloat() - 0.5f) * 0.15f;
            world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, red, green, blue);
            if (alpha >= 0.6f || loop || world.rand.nextBoolean()) {
                world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                        px, py, pz,
                        mx + (world.rand.nextFloat() - 0.5f) * 0.01f,
                        my + (world.rand.nextFloat() - 0.5f) * 0.01f,
                        mz + (world.rand.nextFloat() - 0.5f) * 0.01f);
            }
        }
    }

    @Override
    public void sparkle(float x, float y, float z, float scale, int type, float speed) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc == null ? null : mc.world;
        if (world == null || !world.isRemote) return;
        int amount = particleCount(Math.max(1, (int) (scale * 4.0f)));
        if (amount <= 0) return;

        float red = ((type & 0xFF) / 255.0f);
        float green = ((type >> 8) & 0xFF) / 255.0f;
        float blue = ((type >> 16) & 0xFF) / 255.0f;
        if (red == 0.0f && green == 0.0f && blue == 0.0f) {
            red = 0.8f;
            green = 0.8f;
            blue = 1.0f;
        }
        for (int i = 0; i < amount; i++) {
            double mx = (world.rand.nextFloat() - 0.5f) * 0.02f;
            double my = Math.max(-0.2f, Math.min(0.2f, speed * 0.05f));
            double mz = (world.rand.nextFloat() - 0.5f) * 0.02f;
            world.spawnParticle(EnumParticleTypes.REDSTONE,
                    x + (world.rand.nextFloat() - 0.5f) * 0.2f,
                    y + (world.rand.nextFloat() - 0.5f) * 0.2f,
                    z + (world.rand.nextFloat() - 0.5f) * 0.2f,
                    red, green, blue);
            world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, x, y, z, mx, my, mz);
        }
    }

    @Override
    public int particleCount(int base) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) {
            return Math.max(1, base);
        }
        int setting = mc.gameSettings.particleSetting;
        if (setting >= 2) {
            return 0;
        }
        if (setting == 1) {
            return Math.max(1, base);
        }
        return Math.max(1, base * 2);
    }

    @Override
    public void crucibleFroth(World world, float x, float y, float z) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    0.02f + world.rand.nextFloat() * 0.01f,
                    (world.rand.nextFloat() - 0.5f) * 0.01f);
            if (world.rand.nextInt(4) == 0) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + 0.03f, z, 0.0, 0.01, 0.0);
            }
        }
    }

    @Override
    public void crucibleFrothDown(World world, float x, float y, float z) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            world.spawnParticle(EnumParticleTypes.WATER_SPLASH, x, y, z,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    -0.02f - world.rand.nextFloat() * 0.02f,
                    (world.rand.nextFloat() - 0.5f) * 0.01f);
        }
    }

    @Override
    public void crucibleBubble(World world, float x, float y, float z, float red, float green, float blue) {
        if (world == null || !world.isRemote) return;
        int amount = particleCount(1);
        if (amount <= 0) return;

        for (int i = 0; i < amount; i++) {
            world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, red, green, blue);
            world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    0.015f + world.rand.nextFloat() * 0.01f,
                    (world.rand.nextFloat() - 0.5f) * 0.01f);
        }
    }

    @Override
    public void crucibleBoilSound(World world, int x, int y, int z) {
        if (world == null) return;
        world.playSound(
                x + 0.5, y + 0.5, z + 0.5,
                TCSounds.BUBBLE,
                SoundCategory.BLOCKS,
                0.2f,
                1.0f + world.rand.nextFloat() * 0.4f,
                false
        );
    }

    @Override
    public void crucibleBoil(World world, int x, int y, int z, TileCrucible crucible, int type) {
        if (world == null || !world.isRemote || crucible == null) return;
        int amount = particleCount(type >= 5 ? 3 : 2);
        if (amount <= 0) return;

        float surface = y + 0.05f + crucible.getFluidHeight();
        for (int i = 0; i < amount; i++) {
            float px = x + 0.2f + world.rand.nextFloat() * 0.6f;
            float pz = z + 0.2f + world.rand.nextFloat() * 0.6f;
            world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, px, surface, pz,
                    (world.rand.nextFloat() - 0.5f) * 0.01f,
                    0.02f + world.rand.nextFloat() * 0.02f,
                    (world.rand.nextFloat() - 0.5f) * 0.01f);
            if (type >= 5 || world.rand.nextBoolean()) {
                world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, surface + 0.05f, pz, 0.0, 0.02, 0.0);
            }
        }
    }

    private static Color decodeColor(int color) {
        if (color < 0 || color > 0xFFFFFF) {
            return new Color(0xCCCCFF);
        }
        return new Color(color);
    }

    private static float normalizeColor(int channel) {
        float c = channel / 255.0f;
        return c <= 0.01f ? 0.02f : c;
    }
}
