package thaumcraft.common.lib.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerKnowledgeProvider implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(IPlayerKnowledge.class)
    public static final Capability<IPlayerKnowledge> PLAYER_KNOWLEDGE = null;

    private final IPlayerKnowledge instance;

    public PlayerKnowledgeProvider() {
        this.instance = new PlayerKnowledgeCapability();
    }

    public PlayerKnowledgeProvider(IPlayerKnowledge instance) {
        this.instance = instance;
    }

    public IPlayerKnowledge getInstance() {
        return instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PLAYER_KNOWLEDGE;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == PLAYER_KNOWLEDGE) {
            return PLAYER_KNOWLEDGE.cast(instance);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        instance.deserializeNBT(nbt);
    }

    /**
     * Registers the capability and its default storage. Call once during preInit.
     */
    public static void register() {
        CapabilityManager.INSTANCE.register(
                IPlayerKnowledge.class,
                new Capability.IStorage<IPlayerKnowledge>() {
                    @Override
                    public NBTBase writeNBT(Capability<IPlayerKnowledge> capability, IPlayerKnowledge instance, EnumFacing side) {
                        return instance.serializeNBT();
                    }

                    @Override
                    public void readNBT(Capability<IPlayerKnowledge> capability, IPlayerKnowledge instance, EnumFacing side, NBTBase nbt) {
                        if (nbt instanceof NBTTagCompound) {
                            instance.deserializeNBT((NBTTagCompound) nbt);
                        }
                    }
                },
                PlayerKnowledgeCapability::new
        );
    }
}
