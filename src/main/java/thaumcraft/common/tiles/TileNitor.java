package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;

public class TileNitor extends TileEntity {
    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}
