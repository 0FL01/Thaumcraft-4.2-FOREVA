package thaumcraft.client.fx;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ParticleEngine {

    public static final ParticleEngine instance = new ParticleEngine();
    public static final ParticleEngine INSTANCE = instance;

    private ParticleEngine() {
    }

    @SubscribeEvent
    public void updateParticles(TickEvent.ClientTickEvent event) {
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
    }
}
