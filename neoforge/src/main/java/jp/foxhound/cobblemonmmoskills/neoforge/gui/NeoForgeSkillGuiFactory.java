package jp.foxhound.cobblemonmmoskills.neoforge.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jp.foxhound.cobblemonmmoskills.skill.AbilityType;
import jp.foxhound.cobblemonmmoskills.skill.ActiveAbilityTracker;
import jp.foxhound.cobblemonmmoskills.skill.PlayerSkillProfile;
import jp.foxhound.cobblemonmmoskills.skill.SkillManager;
import jp.foxhound.cobblemonmmoskills.skill.SkillProgress;
import jp.foxhound.cobblemonmmoskills.skill.SkillTables;
import jp.foxhound.cobblemonmmoskills.skill.SkillType;
import jp.foxhound.cobblemonmmoskills.state.SkillPersistentState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

public final class NeoForgeSkillGuiFactory {
    private static final int SIZE = 54;
    private static final int[] ROADMAP_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        25, 24, 23, 22, 21, 20, 19,
        28, 29, 30, 31, 32, 33, 34,
        43, 42, 41, 40, 39, 38, 37
    };
    private static final int ROADMAP_STEP = 5;

    private NeoForgeSkillGuiFactory() {
    }

    public static void openMain(ServerPlayer player) {
        GuiView gui = new GuiView(t("gui.title.main"));
        fillBackground(gui);

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 29, 30};
        SkillType[] skills = SkillType.values();
        PlayerSkillProfile profile = SkillManager.getInstance().getProfile(player);

        for (int i = 0; i < skills.length && i < slots.length; i++) {
            SkillType skillType = skills[i];
            SkillProgress progress = profile.getProgress(skillType);
            gui.set(slots[i], item(icon(skillType))
                .name(skillName(skillType).copy().withStyle(ChatFormatting.GOLD))
                .lore(t("gui.common.level", progress.level()).copy().withStyle(ChatFormatting.YELLOW))
                .lore(Component.literal(xpLine(progress)).withStyle(ChatFormatting.GRAY))
                .lore(Component.empty())
                .lore(t("gui.main.click_details").copy().withStyle(ChatFormatting.GREEN))
                .action(() -> openDetail(player, skillType)));
        }

        gui.set(49, item(Items.PAPER)
            .name(t("gui.main.leaderboard").copy().withStyle(ChatFormatting.AQUA))
            .lore(t("gui.main.leaderboard_desc").copy().withStyle(ChatFormatting.GRAY))
            .action(() -> openLeaderboard(player, 0)));

        gui.open(player);
    }

    public static void openDetail(ServerPlayer player, SkillType skillType) {
        GuiView gui = new GuiView(t("gui.title.detail", skillName(skillType)));
        fillBackground(gui);

        PlayerSkillProfile profile = SkillManager.getInstance().getProfile(player);
        SkillProgress progress = profile.getProgress(skillType);
        ActiveAbilityTracker activeAbilities = SkillManager.getInstance().getActiveAbilities();

        gui.set(13, item(icon(skillType))
            .name(skillName(skillType).copy().withStyle(ChatFormatting.GOLD))
            .lore(t("gui.common.level", progress.level()).copy().withStyle(ChatFormatting.YELLOW))
            .lore(Component.literal(xpLine(progress)).withStyle(ChatFormatting.GRAY))
            .lore(Component.empty())
            .lore(t("gui.detail.overview").copy().withStyle(ChatFormatting.AQUA))
            .lore(skillOverview(skillType)));

        fillSection(gui, new int[]{19, 20, 21, 28, 29, 30, 37, 38, 39}, Items.LIME_STAINED_GLASS_PANE, "Passive");
        fillSection(gui, new int[]{23, 24, 25, 32, 33, 34, 41, 42, 43}, Items.ORANGE_STAINED_GLASS_PANE, "Active");

        gui.set(29, buildPassiveItem(skillType, progress.level())
            .lore(Component.empty())
            .lore(t("gui.roadmap.click_passive").copy().withStyle(ChatFormatting.GREEN))
            .action(() -> openPassiveRoadmap(player, skillType, 0)));
        gui.set(33, buildActiveItem(player, skillType, progress.level(), activeAbilities)
            .lore(Component.empty())
            .lore(t("gui.roadmap.click_active").copy().withStyle(ChatFormatting.GREEN))
            .action(() -> openActiveRoadmap(player, skillType, 0)));

        gui.set(45, item(Items.ARROW)
            .name(t("gui.common.back").copy().withStyle(ChatFormatting.YELLOW))
            .action(() -> openMain(player)));
        gui.set(49, item(Items.PAPER)
            .name(t("gui.main.leaderboard").copy().withStyle(ChatFormatting.AQUA))
            .lore(t("gui.detail.leaderboard_desc", skillName(skillType)).copy().withStyle(ChatFormatting.GRAY))
            .action(() -> openLeaderboard(player, tabIndexForSkill(skillType))));

        gui.open(player);
    }

    public static void openPassiveRoadmap(ServerPlayer player, SkillType skillType, int page) {
        GuiView gui = new GuiView(t("gui.title.passive_roadmap", skillName(skillType)));
        fillBackground(gui);

        PlayerSkillProfile profile = SkillManager.getInstance().getProfile(player);
        int level = profile.getProgress(skillType).level();
        int capLevel = passiveCapLevel(skillType);
        int safePage = clampRoadmapPage(page, capLevel);
        fillPassiveRoadmap(gui, skillType, level, capLevel, safePage);

        addRoadmapControls(gui, player, skillType, safePage, capLevel, true);
        gui.set(45, item(Items.ARROW)
            .name(t("gui.common.back").copy().withStyle(ChatFormatting.YELLOW))
            .action(() -> openDetail(player, skillType)));

        gui.open(player);
    }

    public static void openActiveRoadmap(ServerPlayer player, SkillType skillType, int page) {
        GuiView gui = new GuiView(t("gui.title.active_roadmap", skillName(skillType)));
        fillBackground(gui);

        PlayerSkillProfile profile = SkillManager.getInstance().getProfile(player);
        int level = profile.getProgress(skillType).level();
        AbilityType ability = activeAbility(skillType);

        if (ability == null) {
            gui.set(13, item(Items.BARRIER)
                .name(t("gui.active.none").copy().withStyle(ChatFormatting.RED))
                .lore(t("gui.roadmap.no_active").copy().withStyle(ChatFormatting.GRAY)));
        } else {
            int capLevel = 50;
            int safePage = clampRoadmapPage(page, capLevel);
            fillActiveRoadmap(gui, ability, level, capLevel, safePage);
            addRoadmapControls(gui, player, skillType, safePage, capLevel, false);
        }

        gui.set(45, item(Items.ARROW)
            .name(t("gui.common.back").copy().withStyle(ChatFormatting.YELLOW))
            .action(() -> openDetail(player, skillType)));

        gui.open(player);
    }

    public static void openLeaderboard(ServerPlayer player, int tabIndex) {
        GuiView gui = new GuiView(t("gui.title.leaderboard", leaderboardLabel(tabIndex)));
        fillBackground(gui);

        List<Map.Entry<UUID, PlayerSkillProfile>> rankedPlayers = new ArrayList<>(SkillPersistentState.get(player.server).entries());
        rankedPlayers.sort(Comparator
            .comparingInt((Map.Entry<UUID, PlayerSkillProfile> other) -> rankValue(other.getValue(), tabIndex))
            .thenComparingDouble(other -> tieBreakValue(other.getValue(), tabIndex))
            .reversed()
        );

        int slot = 10;
        for (int i = 0; i < rankedPlayers.size() && i < 10; i++) {
            Map.Entry<UUID, PlayerSkillProfile> ranked = rankedPlayers.get(i);
            SkillProgress progress = progressForTab(ranked.getValue(), tabIndex);
            gui.set(slot++, item(Items.PLAYER_HEAD)
                .name(Component.literal((i + 1) + ". " + resolvePlayerName(player, ranked.getKey())).withStyle(ChatFormatting.GOLD))
                .lore(t("gui.common.level", rankValue(ranked.getValue(), tabIndex)).copy().withStyle(ChatFormatting.YELLOW))
                .lore(leaderboardXpLine(ranked.getValue(), progress, tabIndex)));
            if (slot == 17) {
                slot = 19;
            }
        }

        int previous = previousTab(tabIndex);
        int next = nextTab(tabIndex);
        gui.set(45, item(Items.ARROW)
            .name(t("gui.common.back").copy().withStyle(ChatFormatting.YELLOW))
            .action(() -> openMain(player)));
        gui.set(47, item(Items.SPECTRAL_ARROW)
            .name(t("gui.leaderboard.previous", leaderboardLabel(previous)).copy().withStyle(ChatFormatting.GRAY))
            .action(() -> openLeaderboard(player, previous)));
        gui.set(51, item(Items.SPECTRAL_ARROW)
            .name(t("gui.leaderboard.next", leaderboardLabel(next)).copy().withStyle(ChatFormatting.GRAY))
            .action(() -> openLeaderboard(player, next)));
        gui.set(49, item(iconForTab(tabIndex))
            .name(leaderboardLabel(tabIndex).copy().withStyle(ChatFormatting.AQUA))
            .lore(t("gui.leaderboard.saved_players").copy().withStyle(ChatFormatting.GRAY)));

        gui.open(player);
    }

    private static void fillBackground(GuiView gui) {
        for (int i = 0; i < SIZE; i++) {
            gui.set(i, item(Items.GRAY_STAINED_GLASS_PANE)
                .name(Component.literal(" "))
                .hideTooltip());
        }
    }

    private static void fillSection(GuiView gui, int[] slots, Item item, String label) {
        for (int slot : slots) {
            gui.set(slot, item(item)
                .name(Component.literal(label).withStyle(ChatFormatting.DARK_GRAY))
                .hideTooltip());
        }
    }

    private static void fillPassiveRoadmap(GuiView gui, SkillType skillType, int currentLevel, int capLevel, int page) {
        int startLevel = pageStartLevel(page);
        for (int i = 0; i < ROADMAP_SLOTS.length; i++) {
            int milestoneLevel = startLevel + i * ROADMAP_STEP;
            if (milestoneLevel > capLevel) {
                break;
            }

            gui.set(ROADMAP_SLOTS[i], item(roadmapTileItem(currentLevel, milestoneLevel))
                .name(roadmapTileName(currentLevel, milestoneLevel))
                .lore(roadmapStatusLine(currentLevel, milestoneLevel))
                .lore(passiveValue(skillType, milestoneLevel).copy().withStyle(ChatFormatting.GRAY)));
        }
    }

    private static void fillActiveRoadmap(GuiView gui, AbilityType ability, int currentLevel, int capLevel, int page) {
        int startLevel = pageStartLevel(page);
        for (int i = 0; i < ROADMAP_SLOTS.length; i++) {
            int milestoneLevel = startLevel + i * ROADMAP_STEP;
            if (milestoneLevel > capLevel) {
                break;
            }

            gui.set(ROADMAP_SLOTS[i], item(roadmapTileItem(currentLevel, milestoneLevel))
                .name(roadmapTileName(currentLevel, milestoneLevel))
                .lore(roadmapStatusLine(currentLevel, milestoneLevel))
                .lore(activeValue(ability, milestoneLevel).copy().withStyle(ChatFormatting.GRAY)));
        }
    }

    private static void addRoadmapControls(GuiView gui, ServerPlayer player, SkillType skillType, int page, int capLevel, boolean passive) {
        int maxPage = maxRoadmapPage(capLevel);
        int startLevel = pageStartLevel(page);
        int endLevel = Math.min(capLevel, startLevel + (ROADMAP_SLOTS.length - 1) * ROADMAP_STEP);

        gui.set(4, item(icon(skillType))
            .name(skillName(skillType).copy().withStyle(ChatFormatting.GOLD))
            .lore(t("gui.roadmap.page_range", startLevel, endLevel).copy().withStyle(ChatFormatting.YELLOW))
            .lore(t("gui.roadmap.step_desc", ROADMAP_STEP).copy().withStyle(ChatFormatting.GRAY)));

        if (page > 0) {
            gui.set(47, item(Items.SPECTRAL_ARROW)
                .name(t("gui.leaderboard.previous", t("gui.roadmap.page_range", pageStartLevel(page - 1), Math.min(capLevel, pageStartLevel(page - 1) + (ROADMAP_SLOTS.length - 1) * ROADMAP_STEP))).copy().withStyle(ChatFormatting.GRAY))
                .action(() -> {
                    if (passive) {
                        openPassiveRoadmap(player, skillType, page - 1);
                    } else {
                        openActiveRoadmap(player, skillType, page - 1);
                    }
                }));
        }

        if (page < maxPage) {
            gui.set(51, item(Items.SPECTRAL_ARROW)
                .name(t("gui.leaderboard.next", t("gui.roadmap.page_range", pageStartLevel(page + 1), Math.min(capLevel, pageStartLevel(page + 1) + (ROADMAP_SLOTS.length - 1) * ROADMAP_STEP))).copy().withStyle(ChatFormatting.GRAY))
                .action(() -> {
                    if (passive) {
                        openPassiveRoadmap(player, skillType, page + 1);
                    } else {
                        openActiveRoadmap(player, skillType, page + 1);
                    }
                }));
        }
    }

    private static Item roadmapTileItem(int currentLevel, int milestoneLevel) {
        if (currentLevel >= milestoneLevel) {
            return Items.LIME_STAINED_GLASS_PANE;
        }
        int nextMilestone = nextStep(currentLevel, ROADMAP_STEP, SkillProgress.MAX_LEVEL);
        if (milestoneLevel == nextMilestone) {
            return Items.YELLOW_STAINED_GLASS_PANE;
        }
        return Items.WHITE_STAINED_GLASS_PANE;
    }

    private static Component roadmapTileName(int currentLevel, int milestoneLevel) {
        ChatFormatting color = currentLevel >= milestoneLevel ? ChatFormatting.GREEN : ChatFormatting.GRAY;
        if (milestoneLevel == nextStep(currentLevel, ROADMAP_STEP, SkillProgress.MAX_LEVEL) && currentLevel < milestoneLevel) {
            color = ChatFormatting.YELLOW;
        }
        return t("gui.roadmap.level_tile", milestoneLevel).copy().withStyle(color);
    }

    private static Component roadmapStatusLine(int currentLevel, int milestoneLevel) {
        if (currentLevel >= milestoneLevel) {
            return t("gui.roadmap.status.unlocked").copy().withStyle(ChatFormatting.GREEN);
        }
        if (milestoneLevel == nextStep(currentLevel, ROADMAP_STEP, SkillProgress.MAX_LEVEL)) {
            return t("gui.roadmap.status.next").copy().withStyle(ChatFormatting.YELLOW);
        }
        return t("gui.roadmap.status.locked").copy().withStyle(ChatFormatting.DARK_GRAY);
    }

    private static int clampRoadmapPage(int page, int capLevel) {
        return Math.min(Math.max(0, page), maxRoadmapPage(capLevel));
    }

    private static int maxRoadmapPage(int capLevel) {
        int milestoneCount = Math.max(1, capLevel / ROADMAP_STEP);
        return Math.max(0, (milestoneCount - 1) / ROADMAP_SLOTS.length);
    }

    private static int pageStartLevel(int page) {
        return ROADMAP_STEP + page * ROADMAP_SLOTS.length * ROADMAP_STEP;
    }

    private static int previousTab(int current) {
        return current <= 0 ? SkillType.values().length : current - 1;
    }

    private static int nextTab(int current) {
        return current >= SkillType.values().length ? 0 : current + 1;
    }

    private static int tabIndexForSkill(SkillType skillType) {
        return skillType.ordinal() + 1;
    }

    private static Component leaderboardLabel(int tabIndex) {
        return tabIndex == 0 ? t("gui.leaderboard.overall") : skillName(SkillType.values()[tabIndex - 1]);
    }

    private static Item iconForTab(int tabIndex) {
        return tabIndex == 0 ? Items.NETHER_STAR : icon(SkillType.values()[tabIndex - 1]);
    }

    private static int rankValue(PlayerSkillProfile profile, int tabIndex) {
        if (tabIndex == 0) {
            int total = 0;
            for (SkillType type : SkillType.values()) {
                total += profile.getProgress(type).level();
            }
            return total;
        }
        return profile.getProgress(SkillType.values()[tabIndex - 1]).level();
    }

    private static double tieBreakValue(PlayerSkillProfile profile, int tabIndex) {
        if (tabIndex == 0) {
            double total = 0.0;
            for (SkillType type : SkillType.values()) {
                total += profile.getProgress(type).totalXp();
            }
            return total;
        }
        return profile.getProgress(SkillType.values()[tabIndex - 1]).totalXp();
    }

    private static SkillProgress progressForTab(PlayerSkillProfile profile, int tabIndex) {
        return tabIndex == 0 ? null : profile.getProgress(SkillType.values()[tabIndex - 1]);
    }

    private static Component leaderboardXpLine(PlayerSkillProfile profile, SkillProgress progress, int tabIndex) {
        if (tabIndex == 0) {
            return Component.literal(String.format("Total XP %.0f", totalXp(profile))).withStyle(ChatFormatting.GRAY);
        }
        return Component.literal(xpLine(progress)).withStyle(ChatFormatting.GRAY);
    }

    private static double totalXp(PlayerSkillProfile profile) {
        double total = 0.0;
        for (SkillType type : SkillType.values()) {
            total += profile.getProgress(type).totalXp();
        }
        return total;
    }

    private static String xpLine(SkillProgress progress) {
        int nextLevelXp = SkillProgress.xpRequiredForNextLevel(progress.level());
        return nextLevelXp <= 0 ? "XP MAX" : String.format("XP %.0f/%d", progress.xpIntoLevel(), nextLevelXp);
    }

    private static String resolvePlayerName(ServerPlayer viewer, UUID playerId) {
        ServerPlayer online = viewer.server.getPlayerList().getPlayer(playerId);
        if (online != null) {
            return online.getGameProfile().getName();
        }
        var cached = viewer.server.getProfileCache().get(playerId);
        if (cached.isPresent()) {
            return cached.get().getName();
        }
        return playerId.toString().substring(0, 8);
    }

    private static Item icon(SkillType skillType) {
        return switch (skillType) {
            case MINING -> Items.DIAMOND_PICKAXE;
            case WOODCUTTING -> Items.DIAMOND_AXE;
            case EXCAVATION -> Items.DIAMOND_SHOVEL;
            case ACROBATICS -> Items.FEATHER;
            case FISHING -> Items.FISHING_ROD;
            case SWORDS -> Items.DIAMOND_SWORD;
            case AXES -> Items.IRON_AXE;
            case UNARMED -> Items.LEATHER;
            case ARCHERY -> Items.BOW;
            case HERBALISM -> Items.WHEAT;
            case ALCHEMY -> Items.POTION;
            case REPAIR -> Items.ANVIL;
            case SMELTING -> Items.FURNACE;
            case TRAINING -> Items.EXPERIENCE_BOTTLE;
            case CAPTURE -> Items.ENDER_PEARL;
            case BREEDING -> Items.EGG;
        };
    }

    private static Component skillName(SkillType skillType) {
        return Component.translatable(skillType.translationKey());
    }

    private static List<Component> passiveDescription(SkillType skillType, int level) {
        List<Component> lines = new ArrayList<>();
        switch (skillType) {
            case MINING -> {
                lines.add(t("gui.passive.current_chance", formatPercent(mcmmoDoubleDropChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.mining.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.mining.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case WOODCUTTING -> {
                lines.add(t("gui.passive.current_chance", formatPercent(mcmmoDoubleDropChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.woodcutting.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.woodcutting.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case EXCAVATION -> {
                lines.add(t("gui.passive.excavation.value", formatPercent(SkillTables.archaeologyRank(level) * 2.0)).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.excavation.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.excavation.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case ACROBATICS -> {
                lines.add(t("gui.passive.current_chance", formatPercent(SkillTables.acrobaticsRollChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.acrobatics.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.acrobatics.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case FISHING -> {
                lines.add(t("gui.passive.current_chance", formatPercent(SkillTables.fishingTreasureHunterChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.fishing.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.fishing.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case SWORDS -> {
                lines.add(t("gui.passive.swords.value", formatPercent(SkillTables.swordsBleedChance(level)), SkillTables.swordsBleedSeconds(level)).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.swords.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.swords.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case AXES -> {
                lines.add(t("gui.passive.axes.value", formatPercent(SkillTables.axesArmorImpactChance(level)), SkillTables.axesArmorImpactSeconds(level)).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.axes.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.axes.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case UNARMED -> {
                lines.add(t("gui.passive.current_chance", formatPercent(SkillTables.unarmedIronArmChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.unarmed.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.unarmed.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case ARCHERY -> {
                lines.add(t("gui.passive.archery.value", formatPercent(SkillTables.archeryDazeChance(level)), SkillTables.archeryDazeSeconds(level)).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.archery.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.archery.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case HERBALISM -> {
                lines.add(t("gui.passive.current_chance", formatPercent(SkillTables.herbalismGreenThumbChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.herbalism.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.herbalism.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case ALCHEMY -> {
                lines.add(t("gui.passive.current_chance", formatPercent(SkillTables.alchemyPotionBountyChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.alchemy.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.alchemy.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case REPAIR -> {
                lines.add(t("gui.passive.repair.value", formatPercent(SkillTables.repairMasteryChance(level)), SkillTables.repairMasteryRefundLevels(level, 3)).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.repair.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.repair.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case SMELTING -> {
                lines.add(t("gui.passive.current_chance", formatPercent(SkillTables.smeltingSecondSmeltChance(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.smelting.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.smelting.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case TRAINING -> {
                lines.add(t("gui.passive.current_bonus", formatPercent(SkillTables.trainingBattleExperienceBonusPercent(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.training.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.training.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case CAPTURE -> {
                lines.add(t("gui.passive.current_bonus", formatPercent(SkillTables.captureCatchRateBonusPercent(level))).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.capture.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.capture.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            case BREEDING -> {
                lines.add(t("gui.passive.breeding.value", SkillTables.breedingEggFriendshipBonus(level)).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(t("gui.passive.breeding.name").copy().withStyle(ChatFormatting.GRAY));
                lines.add(t("gui.passive.breeding.desc").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        return lines;
    }

    private static List<Component> activeDescription(ServerPlayer player, SkillType skillType, int level, ActiveAbilityTracker tracker) {
        List<Component> lines = new ArrayList<>();
        AbilityType ability = activeAbility(skillType);

        if (ability == null) {
            lines.add(t("gui.active.none").copy().withStyle(ChatFormatting.DARK_GRAY));
            return lines;
        }

        String status;
        long activeSeconds = tracker.remainingActiveSeconds(player, ability);
        long cooldownSeconds = tracker.remainingCooldownSeconds(player, ability);
        if (activeSeconds > 0) {
            status = t("gui.active.status.active", activeSeconds).getString();
        } else if (cooldownSeconds > 0) {
            status = t("gui.active.status.cooldown", cooldownSeconds).getString();
        } else if (level >= ability.unlockLevel()) {
            status = t("gui.active.status.ready").getString();
        } else {
            status = t("gui.active.status.unlock", ability.unlockLevel()).getString();
        }

        lines.add(Component.literal(ability.displayName()).withStyle(ChatFormatting.GRAY));
        lines.add(t("gui.active.duration", ability.durationSeconds(level)).copy().withStyle(ChatFormatting.DARK_GRAY));
        lines.add(t("gui.active.cooldown", ability.cooldownSeconds()).copy().withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.literal(status).withStyle(ChatFormatting.YELLOW));
        lines.add(activeOverview(skillType));
        return lines;
    }

    private static AbilityType activeAbility(SkillType skillType) {
        return switch (skillType) {
            case MINING -> AbilityType.SUPER_BREAKER;
            case WOODCUTTING -> AbilityType.TREE_FELLER;
            case EXCAVATION -> AbilityType.GIGA_DRILL_BREAKER;
            case SWORDS -> AbilityType.SERRATED_STRIKES;
            case AXES -> AbilityType.SKULL_SPLITTER;
            case UNARMED -> AbilityType.BERSERK;
            case HERBALISM -> AbilityType.GREEN_TERRA;
            default -> null;
        };
    }

    private static Component passiveValue(SkillType skillType, int level) {
        return switch (skillType) {
            case MINING -> t("gui.roadmap.value.chance", t("gui.passive.mining.name"), formatPercent(mcmmoDoubleDropChance(level)));
            case WOODCUTTING -> t("gui.roadmap.value.chance", t("gui.passive.woodcutting.name"), formatPercent(mcmmoDoubleDropChance(level)));
            case EXCAVATION -> t("gui.roadmap.value.excavation", t("gui.passive.excavation.name"), formatPercent(SkillTables.archaeologyRank(level) * 2.0), SkillTables.archaeologyRank(level));
            case ACROBATICS -> t("gui.roadmap.value.chance", t("gui.passive.acrobatics.name"), formatPercent(SkillTables.acrobaticsRollChance(level)));
            case FISHING -> t("gui.roadmap.value.chance", t("gui.passive.fishing.name"), formatPercent(SkillTables.fishingTreasureHunterChance(level)));
            case SWORDS -> t("gui.roadmap.value.chance_duration", t("gui.passive.swords.name"), formatPercent(SkillTables.swordsBleedChance(level)), SkillTables.swordsBleedSeconds(level));
            case AXES -> t("gui.roadmap.value.chance_duration", t("gui.passive.axes.name"), formatPercent(SkillTables.axesArmorImpactChance(level)), SkillTables.axesArmorImpactSeconds(level));
            case UNARMED -> t("gui.roadmap.value.chance", t("gui.passive.unarmed.name"), formatPercent(SkillTables.unarmedIronArmChance(level)));
            case ARCHERY -> t("gui.roadmap.value.chance_duration", t("gui.passive.archery.name"), formatPercent(SkillTables.archeryDazeChance(level)), SkillTables.archeryDazeSeconds(level));
            case HERBALISM -> t("gui.roadmap.value.chance", t("gui.passive.herbalism.name"), formatPercent(SkillTables.herbalismGreenThumbChance(level)));
            case ALCHEMY -> t("gui.roadmap.value.chance", t("gui.passive.alchemy.name"), formatPercent(SkillTables.alchemyPotionBountyChance(level)));
            case REPAIR -> t("gui.roadmap.value.repair", t("gui.passive.repair.name"), formatPercent(SkillTables.repairMasteryChance(level)), SkillTables.repairMasteryRefundLevels(level, 3));
            case SMELTING -> t("gui.roadmap.value.chance", t("gui.passive.smelting.name"), formatPercent(SkillTables.smeltingSecondSmeltChance(level)));
            case TRAINING -> t("gui.roadmap.value.bonus", t("gui.passive.training.name"), formatPercent(SkillTables.trainingBattleExperienceBonusPercent(level)));
            case CAPTURE -> t("gui.roadmap.value.bonus", t("gui.passive.capture.name"), formatPercent(SkillTables.captureCatchRateBonusPercent(level)));
            case BREEDING -> t("gui.roadmap.value.friendship", t("gui.passive.breeding.name"), SkillTables.breedingEggFriendshipBonus(level));
        };
    }

    private static Component activeValue(AbilityType ability, int level) {
        if (level < ability.unlockLevel()) {
            return t("gui.roadmap.active.unlocks", ability.displayName());
        }
        return t("gui.roadmap.active.value", ability.displayName(), ability.durationSeconds(level), ability.cooldownSeconds());
    }

    private static int passiveCapLevel(SkillType skillType) {
        return switch (skillType) {
            case MINING, WOODCUTTING -> 1000;
            case EXCAVATION, ACROBATICS, FISHING, SWORDS, AXES, UNARMED, ARCHERY, HERBALISM, ALCHEMY, REPAIR, SMELTING, TRAINING, CAPTURE, BREEDING -> 100;
        };
    }

    private static int nextStep(int level, int step, int cap) {
        if (level >= cap) {
            return level;
        }
        int next = ((level / step) + 1) * step;
        return Math.min(next, cap);
    }

    private static GuiItem buildPassiveItem(SkillType skillType, int level) {
        GuiItem builder = item(passiveIcon(skillType))
            .name(t("gui.section.passive").copy().withStyle(ChatFormatting.GREEN))
            .lore(t("gui.detail.passive_for", skillName(skillType)).copy().withStyle(ChatFormatting.GRAY))
            .lore(Component.empty());
        for (Component line : passiveDescription(skillType, level)) {
            builder.lore(line);
        }
        return builder;
    }

    private static GuiItem buildActiveItem(ServerPlayer player, SkillType skillType, int level, ActiveAbilityTracker tracker) {
        GuiItem builder = item(activeIcon(skillType))
            .name(t("gui.section.active").copy().withStyle(ChatFormatting.RED))
            .lore(t("gui.detail.active_for", skillName(skillType)).copy().withStyle(ChatFormatting.GRAY))
            .lore(Component.empty());
        for (Component line : activeDescription(player, skillType, level, tracker)) {
            builder.lore(line);
        }
        return builder;
    }

    private static Item passiveIcon(SkillType skillType) {
        return switch (skillType) {
            case MINING -> Items.RAW_IRON;
            case WOODCUTTING -> Items.OAK_LOG;
            case EXCAVATION -> Items.BRUSH;
            case ACROBATICS -> Items.RABBIT_FOOT;
            case FISHING -> Items.CHEST;
            case SWORDS -> Items.REDSTONE;
            case AXES -> Items.IRON_INGOT;
            case UNARMED -> Items.SLIME_BALL;
            case ARCHERY -> Items.TIPPED_ARROW;
            case HERBALISM -> Items.WHEAT_SEEDS;
            case ALCHEMY -> Items.GLOWSTONE_DUST;
            case REPAIR -> Items.IRON_INGOT;
            case SMELTING -> Items.BLAST_FURNACE;
            case TRAINING -> Items.EXPERIENCE_BOTTLE;
            case CAPTURE -> Items.ENDER_EYE;
            case BREEDING -> Items.CAKE;
        };
    }

    private static Item activeIcon(SkillType skillType) {
        return switch (skillType) {
            case MINING -> Items.GOLDEN_PICKAXE;
            case WOODCUTTING -> Items.GOLDEN_AXE;
            case EXCAVATION -> Items.GOLDEN_SHOVEL;
            case SWORDS -> Items.GOLDEN_SWORD;
            case AXES -> Items.GOLDEN_AXE;
            case UNARMED -> Items.GOLDEN_CARROT;
            case HERBALISM -> Items.GOLDEN_HOE;
            default -> Items.BARRIER;
        };
    }

    private static Component skillOverview(SkillType skillType) {
        return t("gui.overview." + skillType.name().toLowerCase()).copy().withStyle(ChatFormatting.DARK_GRAY);
    }

    private static Component activeOverview(SkillType skillType) {
        return switch (skillType) {
            case MINING, WOODCUTTING, EXCAVATION, SWORDS, AXES, UNARMED, HERBALISM ->
                t("gui.active.overview." + skillType.name().toLowerCase()).copy().withStyle(ChatFormatting.DARK_GRAY);
            default -> t("gui.active.none").copy().withStyle(ChatFormatting.DARK_GRAY);
        };
    }

    private static Component t(String key, Object... args) {
        String normalized = key.startsWith("gui.") ? key.substring(4) : key;
        return Component.translatable("gui.cobblemonmmoskills." + normalized, args);
    }

    private static String formatPercent(double value) {
        return String.format("%.1f%%", value);
    }

    private static double mcmmoDoubleDropChance(int level) {
        return Math.min(100.0, level * 0.1);
    }

    private static GuiItem item(Item item) {
        return new GuiItem(item);
    }

    private static final class GuiItem {
        private final ItemStack stack;
        private final List<Component> lore = new ArrayList<>();
        private Runnable action;

        private GuiItem(Item item) {
            this.stack = new ItemStack(item);
        }

        private GuiItem name(Component name) {
            this.stack.set(DataComponents.ITEM_NAME, name);
            return this;
        }

        private GuiItem lore(Component line) {
            this.lore.add(line);
            return this;
        }

        private GuiItem hideTooltip() {
            this.stack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
            return this;
        }

        private GuiItem action(Runnable action) {
            this.action = action;
            return this;
        }

        private ItemStack build() {
            if (!this.lore.isEmpty()) {
                this.stack.set(DataComponents.LORE, new ItemLore(this.lore));
            }
            return this.stack;
        }
    }

    private static final class GuiView {
        private final Component title;
        private final ItemStack[] items = new ItemStack[SIZE];
        private final Map<Integer, Runnable> actions = new HashMap<>();

        private GuiView(Component title) {
            this.title = title;
            for (int i = 0; i < SIZE; i++) {
                this.items[i] = ItemStack.EMPTY;
            }
        }

        private void set(int slot, GuiItem item) {
            this.items[slot] = item.build();
            if (item.action != null) {
                this.actions.put(slot, item.action);
            }
        }

        private void open(ServerPlayer player) {
            SimpleContainer container = new SimpleContainer(SIZE);
            for (int i = 0; i < SIZE; i++) {
                container.setItem(i, this.items[i]);
            }
            player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, ignored) -> new GuiMenu(containerId, inventory, container, this.actions),
                this.title
            ));
        }
    }

    private static final class GuiMenu extends ChestMenu {
        private final Map<Integer, Runnable> actions;

        private GuiMenu(int containerId, Inventory inventory, SimpleContainer container, Map<Integer, Runnable> actions) {
            super(MenuType.GENERIC_9x6, containerId, inventory, container, 6);
            this.actions = actions;
        }

        @Override
        public void clicked(int slotId, int button, ClickType clickType, Player player) {
            setCarried(ItemStack.EMPTY);
            if (slotId >= 0 && slotId < SIZE) {
                Runnable action = this.actions.get(slotId);
                if (action != null) {
                    action.run();
                }
            }
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canDragTo(Slot slot) {
            return false;
        }
    }
}
