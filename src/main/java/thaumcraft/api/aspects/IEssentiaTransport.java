package thaumcraft.api.aspects;

import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;

public interface IEssentiaTransport {
    public boolean isConnectable(EnumFacing var1);

    public boolean canInputFrom(EnumFacing var1);

    public boolean canOutputTo(EnumFacing var1);

    public void setSuction(Aspect var1, int var2);

    public Aspect getSuctionType(EnumFacing var1);

    public int getSuctionAmount(EnumFacing var1);

    public int takeEssentia(Aspect var1, int var2, EnumFacing var3);

    public int addEssentia(Aspect var1, int var2, EnumFacing var3);

    public Aspect getEssentiaType(EnumFacing var1);

    public int getEssentiaAmount(EnumFacing var1);

    public int getMinimumSuction();

    public boolean renderExtendedTube();
}

