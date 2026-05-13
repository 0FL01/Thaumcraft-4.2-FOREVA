package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigBlocks;

public class PotionThaumarhia extends Potion {

    public static PotionThaumarhia instance;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionThaumarhia(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        setIconIndex(6, 1);
        setPotionName("potion.thaumarhia");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int amplifier) {
        if (target == null || target.world == null || target.world.isRemote || ConfigBlocks.blockFluxGoo == null) return;
        if (target.world.rand.nextInt(15) != 0) return;

        BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
        if (target.world.isAirBlock(pos)) {
            IBlockState state = ConfigBlocks.blockFluxGoo.getDefaultState();
            target.world.setBlockState(pos, state, 3);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
