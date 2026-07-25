package thaumcraft.api.capabilities;

import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import thaumcraft.api.research.ResearchCategory;

public interface IPlayerKnowledge extends INBTSerializable<NBTTagCompound> {
    enum EnumKnowledgeType {
        THEORY(32, true, "T"),
        OBSERVATION(16, true, "O");

        private final short progression;
        private final boolean hasFields;
        private final String abbreviation;

        EnumKnowledgeType(int progression, boolean hasFields, String abbreviation) {
            this.progression = (short) progression;
            this.hasFields = hasFields;
            this.abbreviation = abbreviation;
        }

        public int getProgression() {
            return progression;
        }

        public boolean hasFields() {
            return hasFields;
        }

        public String getAbbreviation() {
            return abbreviation;
        }
    }

    enum EnumResearchStatus { UNKNOWN, COMPLETE, IN_PROGRESS }

    enum EnumResearchFlag { PAGE, RESEARCH, POPUP }

    void clear();
    EnumResearchStatus getResearchStatus(String key);
    boolean isResearchComplete(String key);
    boolean isResearchKnown(String key);
    int getResearchStage(String key);
    boolean addResearch(String key);
    boolean setResearchStage(String key, int stage);
    boolean removeResearch(String key);
    Set<String> getResearchList();
    boolean setResearchFlag(String key, EnumResearchFlag flag);
    boolean clearResearchFlag(String key, EnumResearchFlag flag);
    boolean hasResearchFlag(String key, EnumResearchFlag flag);
    boolean addKnowledge(EnumKnowledgeType type, ResearchCategory category, int amount);
    int getKnowledge(EnumKnowledgeType type, ResearchCategory category);
    int getKnowledgeRaw(EnumKnowledgeType type, ResearchCategory category);
    void sync(EntityPlayerMP player);
}
