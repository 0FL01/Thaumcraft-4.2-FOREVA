package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import thaumcraft.common.Thaumcraft;

public class TileNitor extends TileEntity implements ITickable {
    @Override
    public void update() {
        if (this.world == null || !this.world.isRemote) {
            return;
        }
        if (this.world.rand.nextInt(9 - Thaumcraft.proxy.particleCount(2)) == 0) {
            Thaumcraft.proxy.wispFX3(
                    this.world,
                    this.pos.getX() + 0.5f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.5f,
                    this.pos.getX() + 0.3f + this.world.rand.nextFloat() * 0.4f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.3f + this.world.rand.nextFloat() * 0.4f,
                    0.5f,
                    4,
                    true,
                    -0.025f);
        }
        if (this.world.rand.nextInt(15 - Thaumcraft.proxy.particleCount(4)) == 0) {
            Thaumcraft.proxy.wispFX3(
                    this.world,
                    this.pos.getX() + 0.5f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.5f,
                    this.pos.getX() + 0.4f + this.world.rand.nextFloat() * 0.2f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.4f + this.world.rand.nextFloat() * 0.2f,
                    0.25f,
                    1,
                    true,
                    -0.02f);
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}
