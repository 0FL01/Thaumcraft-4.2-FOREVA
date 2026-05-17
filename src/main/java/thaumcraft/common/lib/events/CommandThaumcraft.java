package thaumcraft.common.lib.events;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncAspects;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class CommandThaumcraft extends CommandBase {
    private final List<String> aliases = new ArrayList<>();

    public CommandThaumcraft() {
        this.aliases.add("thaumcraft");
        this.aliases.add("thaum");
        this.aliases.add("tc");
    }

    @Override
    public String getName() {
        return "thaumcraft";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/thaumcraft <action> [<player> [<params>]]";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, net.minecraft.util.math.BlockPos targetPos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendError(sender, "Invalid arguments");
            sendError(sender, "Use /thaumcraft help to get help");
            return;
        }

        if ("help".equalsIgnoreCase(args[0])) {
            sendInfo(sender, "You can also use /thaum or /tc instead of /thaumcraft.");
            sendInfo(sender, "Use this to give research to a player.");
            sendInfo(sender, "  /thaumcraft research <list|player> <all|reset|<research>>");
            sendInfo(sender, "Use this to give aspect research points to a player.");
            sendInfo(sender, "  /thaumcraft aspect <player> <aspect|all> <amount>");
            sendInfo(sender, "Use this to give set a players warp level.");
            sendInfo(sender, "  /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>");
            sendInfo(sender, "  not specifying perm or temp will just add normal warp");
            return;
        }

        if (args.length < 2) {
            sendError(sender, "Invalid arguments");
            sendError(sender, "Use /thaumcraft help to get help");
            return;
        }

        if ("research".equalsIgnoreCase(args[0]) && "list".equalsIgnoreCase(args[1])) {
            listResearch(sender);
            return;
        }

        EntityPlayerMP player = getPlayer(server, sender, args[1]);

        if ("research".equalsIgnoreCase(args[0])) {
            if (args.length != 3) {
                sendError(sender, "Invalid arguments");
                sendError(sender, "Use /thaumcraft research <list|player> <all|reset|<research>>");
                return;
            }
            if ("all".equalsIgnoreCase(args[2])) {
                giveAllResearch(sender, player);
            } else if ("reset".equalsIgnoreCase(args[2])) {
                resetResearch(sender, player);
            } else {
                giveResearch(sender, player, args[2]);
            }
            return;
        }

        if ("aspect".equalsIgnoreCase(args[0])) {
            if (args.length != 4) {
                sendError(sender, "Invalid arguments");
                sendError(sender, "Use /thaumcraft aspect <player> <aspect|all> <amount>");
                return;
            }
            int amount = parseInt(args[3], 1);
            giveAspect(sender, player, args[2], amount);
            return;
        }

        if ("warp".equalsIgnoreCase(args[0])) {
            if (args.length < 4) {
                sendError(sender, "Invalid arguments");
                sendError(sender, "Use /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>");
                return;
            }
            String op = args[2];
            String type = args.length == 5 ? args[4] : "";
            if ("set".equalsIgnoreCase(op)) {
                int amount = parseInt(args[3], 0);
                setWarp(sender, player, amount, type);
                return;
            }
            if ("add".equalsIgnoreCase(op)) {
                int amount = parseInt(args[3], -100, 100);
                addWarp(sender, player, amount, type);
                return;
            }
            sendError(sender, "Invalid arguments");
            sendError(sender, "Use /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>");
            return;
        }

        sendError(sender, "Invalid arguments");
        sendError(sender, "Use /thaumcraft help to get help");
    }

    private void giveAspect(ICommandSender sender, EntityPlayerMP player, String aspectKey, int amount) {
        IPlayerKnowledge knowledge = getKnowledge(player);
        if (knowledge == null) {
            sendError(sender, "Player knowledge unavailable.");
            return;
        }

        if ("all".equalsIgnoreCase(aspectKey)) {
            for (Aspect aspect : Aspect.aspects.values()) {
                if (aspect != null) {
                    knowledge.addAspectPool(aspect, amount);
                }
            }
            ResearchManager.updateCache(player.getName(), knowledge);
            PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(knowledge.getAspectsDiscovered()), player);
            sendPlayerInfo(player, sender.getName() + " gave you " + amount + " of all the aspects.");
            sendSuccess(sender);
            return;
        }

        Aspect aspect = Aspect.getAspect(aspectKey);
        if (aspect == null) {
            for (Aspect candidate : Aspect.aspects.values()) {
                if (candidate != null && aspectKey.equalsIgnoreCase(candidate.getName())) {
                    aspect = candidate;
                    break;
                }
            }
        }
        if (aspect == null) {
            sendError(sender, "Aspect does not exist.");
            return;
        }

        knowledge.addAspectPool(aspect, amount);
        ResearchManager.updateCache(player.getName(), knowledge);
        PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(knowledge.getAspectsDiscovered()), player);
        sendPlayerInfo(player, sender.getName() + " gave you " + amount + " " + aspect.getName());
        sendSuccess(sender);
    }

    private void setWarp(ICommandSender sender, EntityPlayerMP player, int amount, String type) {
        IPlayerKnowledge knowledge = getKnowledge(player);
        if (knowledge == null) {
            sendError(sender, "Player knowledge unavailable.");
            return;
        }

        if ("PERM".equalsIgnoreCase(type)) {
            knowledge.setWarpPerm(amount);
        } else if ("TEMP".equalsIgnoreCase(type)) {
            knowledge.setWarpTemp(amount);
        } else {
            knowledge.setWarpSticky(amount);
        }

        ResearchManager.updateCache(player.getName(), knowledge);
        ResearchManager.syncWarp(player);
        sendPlayerInfo(player, sender.getName() + " set your warp to " + amount);
        sendSuccess(sender);
    }

    private void addWarp(ICommandSender sender, EntityPlayerMP player, int amount, String type) {
        IPlayerKnowledge knowledge = getKnowledge(player);
        if (knowledge == null) {
            sendError(sender, "Player knowledge unavailable.");
            return;
        }

        byte messageType;
        if ("PERM".equalsIgnoreCase(type)) {
            knowledge.addWarpPerm(amount);
            messageType = 0;
        } else if ("TEMP".equalsIgnoreCase(type)) {
            knowledge.addWarpTemp(amount);
            messageType = 2;
        } else {
            knowledge.addWarpSticky(amount);
            messageType = 1;
        }

        ResearchManager.updateCache(player.getName(), knowledge);
        ResearchManager.syncWarp(player);
        PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(messageType, amount), player);
        sendPlayerInfo(player, sender.getName() + " added " + amount + " warp to your total.");
        sendSuccess(sender);
    }

    private void listResearch(ICommandSender sender) {
        Collection<ResearchCategoryList> categories = ResearchCategories.researchCategories.values();
        for (ResearchCategoryList category : categories) {
            Collection<ResearchItem> research = category.research.values();
            for (ResearchItem item : research) {
                sender.sendMessage(new TextComponentString("\u00a75" + item.key));
            }
        }
    }

    private void giveResearch(ICommandSender sender, EntityPlayerMP player, String researchKey) {
        if (ResearchCategories.getResearch(researchKey) == null) {
            sendError(sender, "Research does not exist.");
            return;
        }

        giveRecursiveResearch(player, researchKey);
        IPlayerKnowledge knowledge = getKnowledge(player);
        if (knowledge != null) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(new HashSet<>(knowledge.getResearchComplete())), player);
        }
        sendPlayerInfo(player, sender.getName() + " gave you " + researchKey + " research and its requisites.");
        sendSuccess(sender);
    }

    private void giveRecursiveResearch(EntityPlayerMP player, String researchKey) {
        if (ResearchManager.isResearchComplete(player.getName(), researchKey)) {
            return;
        }

        ResearchItem research = ResearchCategories.getResearch(researchKey);
        if (research == null) {
            return;
        }

        ResearchManager.addResearch(player, researchKey);

        if (research.parents != null) {
            for (String parent : research.parents) {
                giveRecursiveResearch(player, parent);
            }
        }
        if (research.parentsHidden != null) {
            for (String parent : research.parentsHidden) {
                giveRecursiveResearch(player, parent);
            }
        }
        if (research.siblings != null) {
            for (String sibling : research.siblings) {
                giveRecursiveResearch(player, sibling);
            }
        }
    }

    private void giveAllResearch(ICommandSender sender, EntityPlayerMP player) {
        Collection<ResearchCategoryList> categories = ResearchCategories.researchCategories.values();
        for (ResearchCategoryList category : categories) {
            for (ResearchItem item : category.research.values()) {
                if (!ResearchManager.isResearchComplete(player.getName(), item.key)) {
                    ResearchManager.addResearch(player, item.key);
                }
            }
        }
        IPlayerKnowledge knowledge = getKnowledge(player);
        if (knowledge != null) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(new HashSet<>(knowledge.getResearchComplete())), player);
        }
        sendPlayerInfo(player, sender.getName() + " has given you all research.");
        sendSuccess(sender);
    }

    private void resetResearch(ICommandSender sender, EntityPlayerMP player) {
        IPlayerKnowledge knowledge = getKnowledge(player);
        if (knowledge == null) {
            sendError(sender, "Player knowledge unavailable.");
            return;
        }

        knowledge.getResearchComplete().clear();
        Collection<ResearchCategoryList> categories = ResearchCategories.researchCategories.values();
        for (ResearchCategoryList category : categories) {
            for (ResearchItem item : category.research.values()) {
                if (item.isAutoUnlock()) {
                    ResearchManager.addResearch(player, item.key);
                }
            }
        }

        ResearchManager.updateCache(player.getName(), knowledge);
        PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(new HashSet<>(knowledge.getResearchComplete())), player);
        sendPlayerInfo(player, sender.getName() + " has reset your research.");
        sendSuccess(sender);
    }

    private static IPlayerKnowledge getKnowledge(EntityPlayerMP player) {
        return player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
    }

    private static void sendError(ICommandSender sender, String text) {
        sender.sendMessage(new TextComponentString("\u00a7c" + text));
    }

    private static void sendInfo(ICommandSender sender, String text) {
        sender.sendMessage(new TextComponentString("\u00a73" + text));
    }

    private static void sendSuccess(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("\u00a75Success!"));
    }

    private static void sendPlayerInfo(EntityPlayerMP player, String text) {
        player.sendMessage(new TextComponentString("\u00a75" + text));
    }
}
