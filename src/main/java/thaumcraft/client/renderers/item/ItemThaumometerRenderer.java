package thaumcraft.client.renderers.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ScanResult;
import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.Map;

public class ItemThaumometerRenderer extends TileEntityItemStackRenderer {

    private static final ResourceLocation SCANNER_OBJ =
            new ResourceLocation("thaumcraft", "textures/models/scanner.obj");
    private static final ResourceLocation SCANNER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/scanner.png");
    private static final ResourceLocation SCANSCREEN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/scanscreen.png");

    private final CCModel scannerModel = loadScannerModel();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty() || scannerModel == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        renderScannerModel(mc);
        renderScannerScreen(mc, player);
        if (player != null && player.isHandActive() && ItemStack.areItemStacksEqual(player.getActiveItemStack(), stack)
                && mc.gameSettings.thirdPersonView == 0) {
            renderScanReadout(mc, stack, player);
        }
        GlStateManager.popMatrix();
    }

    private void renderScannerModel(Minecraft mc) {
        mc.getTextureManager().bindTexture(SCANNER_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableCull();
        CCRenderState.reset();
        CCRenderState.startDrawing();
        scannerModel.render();
        CCRenderState.draw();
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderScannerScreen(Minecraft mc, EntityPlayer player) {
        mc.getTextureManager().bindTexture(SCANSCREEN_TEXTURE);
        int packed = player != null ? player.getBrightnessForRender() : 200;
        int sky = packed % 65536;
        int block = packed / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sky, block);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.11F, 0.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        float half = 1.25F;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-half, half, 0.0D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        buffer.pos(half, half, 0.0D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        buffer.pos(half, -half, 0.0D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        buffer.pos(-half, -half, 0.0D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderScanReadout(Minecraft mc, ItemStack stack, EntityPlayer player) {
        ScanResult scan = doScan(stack, player);
        if (scan == null) {
            return;
        }

        String title = getScanTitle(scan, player.world, player);
        String detail = "";
        if (ScanManager.hasBeenScanned(player, scan)) {
            try {
                int vis = ScanManager.getScanAspects(scan, player.world).visSize();
                detail = "vis " + vis;
            } catch (Exception ignored) {
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.12F, -0.01F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.01F, -0.01F, 0.01F);
        GlStateManager.disableLighting();
        FontRenderer font = mc.fontRenderer;
        int titleWidth = font.getStringWidth(title);
        font.drawString(title, -titleWidth / 2.0F, -8.0F, 0xFFFFFF, false);
        if (!detail.isEmpty()) {
            int detailWidth = font.getStringWidth(detail);
            font.drawString(detail, -detailWidth / 2.0F, 4.0F, 0xA0E0FF, false);
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static String getScanTitle(ScanResult scan, World world, EntityPlayer player) {
        if (scan == null) {
            return "?";
        }
        try {
            if (scan.type == 1 && scan.id > 0) {
                Item item = Item.getItemById(scan.id);
                if (item != null) {
                    return new ItemStack(item, 1, scan.meta).getDisplayName();
                }
            }
            if (scan.type == 2 && scan.entity != null) {
                return scan.entity.getName();
            }
            if (scan.type == 3) {
                if (scan.phenomena != null && scan.phenomena.startsWith("NODE")) {
                    RayTraceResult hit = player.rayTrace(10.0D, 1.0F);
                    if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                        TileEntity tile = world.getTileEntity(hit.getBlockPos());
                        if (tile instanceof INode) {
                            String out = ((INode) tile).getNodeType().name().toLowerCase();
                            if (((INode) tile).getNodeModifier() != null) {
                                out = out + ", " + ((INode) tile).getNodeModifier().name().toLowerCase();
                            }
                            return out;
                        }
                    }
                    return "node";
                }
                if (scan.phenomena != null && !scan.phenomena.isEmpty()) {
                    return scan.phenomena.toLowerCase();
                }
            }
        } catch (Exception ignored) {
        }
        return "?";
    }

    private static ScanResult doScan(ItemStack stack, EntityPlayer player) {
        if (stack == null || stack.isEmpty() || player == null || player.world == null) {
            return null;
        }
        World world = player.world;

        Entity pointed = EntityUtils.getPointedEntity(world, player, 10.0D, null);
        if (pointed != null) {
            ScanResult result = new ScanResult((byte) 2, 0, 0, pointed, "");
            return ScanManager.isValidScanTarget(player, result, "@") ? result : null;
        }

        RayTraceResult hit = player.rayTrace(10.0D, 1.0F);
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof INode) {
                INode node = (INode) tile;
                String id = node.getId();
                if (id == null || id.isEmpty()) {
                    id = world.provider.getDimension() + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
                }
                ScanResult result = new ScanResult((byte) 3, 0, 0, null, "NODE" + id);
                return ScanManager.isValidScanTarget(player, result, "@") ? result : null;
            }

            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            int meta = block.getMetaFromState(state);
            ItemStack target = ItemStack.EMPTY;
            try {
                target = block.getPickBlock(state, hit, world, pos, player);
            } catch (Exception ignored) {
            }
            if (target == null || target.isEmpty()) {
                target = BlockUtils.createStackedBlock(block, meta);
            }
            if (!target.isEmpty()) {
                ScanResult result = new ScanResult((byte) 1, Item.getIdFromItem(target.getItem()), target.getMetadata(), null, "");
                return ScanManager.isValidScanTarget(player, result, "@") ? result : null;
            }
        }

        for (IScanEventHandler handler : ThaumcraftApi.scanEventhandlers) {
            ScanResult result = handler.scanPhenomena(stack, world, player);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static CCModel loadScannerModel() {
        Map<String, CCModel> models = CCModel.parseObjModels(SCANNER_OBJ);
        if (models == null || models.isEmpty()) {
            return null;
        }
        CCModel model = models.get("scanner");
        if (model != null) {
            return model;
        }
        return models.values().iterator().next();
    }
}
