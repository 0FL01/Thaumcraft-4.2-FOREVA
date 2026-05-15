package thaumcraft.client;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
import thaumcraft.client.gui.GuiSpa;
import thaumcraft.client.gui.GuiThaumatorium;
import thaumcraft.client.gui.GuiTravelingTrunk;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.ClientTickEventsFML;
import thaumcraft.client.lib.KeyHandler;
import thaumcraft.client.lib.RenderEventHandler;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.events.EventHandlerRunic;
import thaumcraft.common.tiles.TileAlchemyFurnace;
import thaumcraft.common.tiles.TileArcaneBore;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileDeconstructionTable;
import thaumcraft.common.tiles.TileFocalManipulator;
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
    }

    private void setupBlockRenderers() {
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
            case GUI_THAUMONOMICON:
                return null;
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
        // Phase 8: particle FX
    }

    @Override
    public void beam(World world, double x, double y, double z, double tx, double ty, double tz, int color, boolean flicker, int ticks) {
        // Phase 8: beam FX
    }

    @Override
    public void bolt(World world, double x, double y, double z, double tx, double ty, double tz, int color, int speed) {
        // Phase 8: lightning bolt FX
    }
}
