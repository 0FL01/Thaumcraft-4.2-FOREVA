package thaumcraft.client.lib;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.tiles.TileInfusionMatrix;

@SideOnly(Side.CLIENT)
public class ClientTickEventsFML {
    public static int warpVignette = 0;
    private int tickCount = 0;

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

    @SubscribeEvent
    public void clientWorldTick(TickEvent.ClientTickEvent event) {
        if (event.side != Side.CLIENT || event.phase != TickEvent.Phase.START) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
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

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
    }
}
