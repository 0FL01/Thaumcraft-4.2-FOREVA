package thaumcraft.client.renderers.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Immutable TC4 wand use pose. Consumers convert it into the corrected model basis. */
@SideOnly(Side.CLIENT)
public final class WandUsePose {

    public final float startup;
    public final float contextRotateX;
    public final float contextRotateZ;
    public final float startupRotateX;
    public final float waveRotateX;
    public final float waveRotateZ;

    WandUsePose(float startup, float contextRotateX, float contextRotateZ,
                float startupRotateX, float waveRotateX, float waveRotateZ) {
        this.startup = startup;
        this.contextRotateX = contextRotateX;
        this.contextRotateZ = contextRotateZ;
        this.startupRotateX = startupRotateX;
        this.waveRotateX = waveRotateX;
        this.waveRotateZ = waveRotateZ;
    }
}
