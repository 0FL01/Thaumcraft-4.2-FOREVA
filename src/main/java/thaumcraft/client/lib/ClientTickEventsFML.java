package thaumcraft.client.lib;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.tiles.TileInfusionMatrix;

@SideOnly(Side.CLIENT)
public class ClientTickEventsFML {
    public static int warpVignette = 0;
    private static final int SHADER_DESAT = 0;
    private static final int SHADER_BLUR = 1;
    private static final int SHADER_HUNGER = 2;
    private static final int SHADER_SUNSCORNED = 3;
    private final ResourceLocation[] shaderResources = new ResourceLocation[] {
            new ResourceLocation("shaders/post/desaturatetc.json"),
            new ResourceLocation("shaders/post/blurtc.json"),
            new ResourceLocation("shaders/post/hunger.json"),
            new ResourceLocation("shaders/post/sunscorned.json")
    };
    private int tickCount = 0;
    private boolean wandUseReleasePending;

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.CLIENT || event.phase != TickEvent.Phase.START) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || event.player == null || event.player.getEntityId() != player.getEntityId()) {
            return;
        }

        this.checkShaders(player, mc);
        if (warpVignette > 0) {
            warpVignette--;
            RenderEventHandler.targetBrightness = 0.0F;
        } else {
            RenderEventHandler.targetBrightness = 1.0F;
        }

        if (RenderEventHandler.fogFiddled) {
            if (RenderEventHandler.fogDuration < 100) {
                RenderEventHandler.fogTarget = 0.1F * ((float) RenderEventHandler.fogDuration / 100.0F);
            } else if (RenderEventHandler.fogTarget < 0.1F) {
                RenderEventHandler.fogTarget += 0.001F;
            }
            if (--RenderEventHandler.fogDuration < 0) {
                RenderEventHandler.fogDuration = 0;
                RenderEventHandler.fogFiddled = false;
                RenderEventHandler.fogTarget = 0.0F;
            }
        }
    }

    private void checkShaders(EntityPlayer player, Minecraft mc) {
        if (player == null) {
            this.deactivateAllShaders();
            return;
        }
        if (player.isPotionActive(Config.potionDeathGaze)) {
            warpVignette = 10;
        }
        if (!Config.shaders || !OpenGlHelper.shadersSupported) {
            this.deactivateAllShaders();
            return;
        }
        if (player.isPotionActive(Config.potionDeathGaze)) {
            this.ensureShader(mc, SHADER_DESAT, this.shaderResources[SHADER_DESAT]);
        } else {
            this.deactivateShader(SHADER_DESAT);
        }
        if (player.isPotionActive(Config.potionBlurredVision)) {
            this.ensureShader(mc, SHADER_BLUR, this.shaderResources[SHADER_BLUR]);
        } else {
            this.deactivateShader(SHADER_BLUR);
        }
        if (player.isPotionActive(Config.potionUnnaturalHunger)) {
            this.ensureShader(mc, SHADER_HUNGER, this.shaderResources[SHADER_HUNGER]);
        } else {
            this.deactivateShader(SHADER_HUNGER);
        }
        if (player.isPotionActive(Config.potionSunScorned)) {
            this.ensureShader(mc, SHADER_SUNSCORNED, this.shaderResources[SHADER_SUNSCORNED]);
        } else {
            this.deactivateShader(SHADER_SUNSCORNED);
        }
    }

    private void ensureShader(Minecraft mc, int shaderId, ResourceLocation shaderResource) {
        if (RenderEventHandler.shaderGroups.containsKey(shaderId)) {
            return;
        }
        try {
            this.setShader(new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shaderResource), shaderId);
        } catch (JsonException ignored) {
        } catch (Exception ignored) {
        }
    }

    void setShader(ShaderGroup target, int shaderId) {
        if (!OpenGlHelper.shadersSupported) {
            return;
        }
        if (RenderEventHandler.shaderGroups.containsKey(shaderId)) {
            RenderEventHandler.shaderGroups.get(shaderId).deleteShaderGroup();
            RenderEventHandler.shaderGroups.remove(shaderId);
        }
        try {
            if (target == null) {
                this.deactivateShader(shaderId);
            } else {
                RenderEventHandler.resetShaders = true;
                RenderEventHandler.shaderGroups.put(shaderId, target);
            }
        } catch (Exception ignored) {
            RenderEventHandler.shaderGroups.remove(shaderId);
        }
    }

    public void deactivateShader(int shaderId) {
        if (RenderEventHandler.shaderGroups.containsKey(shaderId)) {
            RenderEventHandler.shaderGroups.get(shaderId).deleteShaderGroup();
        }
        RenderEventHandler.shaderGroups.remove(shaderId);
    }

    private void deactivateAllShaders() {
        for (Integer shaderId : new ArrayList<Integer>(RenderEventHandler.shaderGroups.keySet())) {
            this.deactivateShader(shaderId);
        }
    }

    @SubscribeEvent
    public void clientWorldTick(TickEvent.ClientTickEvent event) {
        if (event.side != Side.CLIENT) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END) {
            this.ensureWandUseRelease(mc);
            return;
        }
        if (mc.world == null) {
            return;
        }
        this.tickCount++;

        for (String fxKey : new ArrayList<String>(EssentiaHandler.sourceFX.keySet())) {
            EssentiaHandler.EssentiaSourceFX fx = EssentiaHandler.sourceFX.get(fxKey);
            if (fx == null || fx.ticks <= 0) {
                EssentiaHandler.sourceFX.remove(fxKey);
                continue;
            }

            int sourceY = fx.start.getY();
            TileEntity tile = mc.world.getTileEntity(fx.start);
            if (tile instanceof TileInfusionMatrix) {
                sourceY--;
            }

            if (fx.ticks > 5) {
                Thaumcraft.proxy.essentiaTrailFx(
                        mc.world,
                        fx.end.getX(), fx.end.getY(), fx.end.getZ(),
                        fx.start.getX(), sourceY, fx.start.getZ(),
                        this.tickCount, fx.color, 1.0F);
            } else {
                float scale = (float) (fx.ticks * fx.ticks) / 25.0F;
                Thaumcraft.proxy.essentiaTrailFx(
                        mc.world,
                        fx.end.getX(), fx.end.getY(), fx.end.getZ(),
                        fx.start.getX(), sourceY, fx.start.getZ(),
                        this.tickCount - (5 - fx.ticks), fx.color, scale);
            }

            fx.ticks--;
            EssentiaHandler.sourceFX.put(fxKey, fx);
        }
    }

    /**
     * Vanilla only sends RELEASE_USE_ITEM while the client still considers the hand active.
     * Wand NBT/hand synchronization can clear that flag before the physical key is released,
     * leaving the server's indefinite use action alive. Keep a key-down latch and send the
     * normal controller release once on key-up even if the local active-hand flag was lost.
     */
    private void ensureWandUseRelease(Minecraft mc) {
        if (mc.player == null || mc.world == null || mc.playerController == null) {
            this.wandUseReleasePending = false;
            return;
        }

        boolean useKeyDown = mc.gameSettings.keyBindUseItem.isKeyDown();
        boolean hasWand = isWand(mc.player.getHeldItemMainhand())
                || isWand(mc.player.getHeldItemOffhand())
                || isWand(mc.player.getActiveItemStack());
        if (useKeyDown && hasWand) {
            this.wandUseReleasePending = true;
        }
        if (this.wandUseReleasePending && (!useKeyDown || !hasWand)) {
            mc.playerController.onStoppedUsingItem(mc.player);
            this.wandUseReleasePending = false;
        }
    }

    private static boolean isWand(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemWandCasting;
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
    }
}
