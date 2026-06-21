package jp.foxhound.cobblemonmmoskills.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import jp.foxhound.cobblemonmmoskills.gui.SkillGuiBridge;
import jp.foxhound.cobblemonmmoskills.state.SkillPersistentState;
import jp.foxhound.cobblemonmmoskills.skill.AbilityType;
import jp.foxhound.cobblemonmmoskills.skill.ActiveAbilityTracker;
import jp.foxhound.cobblemonmmoskills.skill.PlayerSkillProfile;
import jp.foxhound.cobblemonmmoskills.skill.SkillManager;
import jp.foxhound.cobblemonmmoskills.skill.SkillProgress;
import jp.foxhound.cobblemonmmoskills.skill.SkillTables;
import jp.foxhound.cobblemonmmoskills.skill.SkillType;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class SkillCommand {
    private SkillCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mmoskills")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> openGui(context.getSource().getPlayerOrException()))
            .then(Commands.literal("reset")
                .then(Commands.literal("self")
                    .executes(context -> resetSelf(context.getSource().getPlayerOrException())))
                .then(Commands.literal("all")
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> resetAll(context.getSource()))))
            .then(Commands.literal("admin")
                .requires(source -> source.getPlayer() != null && source.hasPermission(2))
                .then(Commands.literal("addxp")
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("skill", StringArgumentType.word())
                            .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0))
                                .executes(context -> adminAddXp(
                                    context.getSource(),
                                    EntityArgument.getPlayer(context, "target"),
                                    StringArgumentType.getString(context, "skill"),
                                    DoubleArgumentType.getDouble(context, "amount")
                                ))))))
                .then(Commands.literal("setlevel")
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("skill", StringArgumentType.word())
                            .then(Commands.argument("level", IntegerArgumentType.integer(0, SkillProgress.MAX_LEVEL))
                                .executes(context -> adminSetLevel(
                                    context.getSource(),
                                    EntityArgument.getPlayer(context, "target"),
                                    StringArgumentType.getString(context, "skill"),
                                    IntegerArgumentType.getInteger(context, "level")
                                ))))))
                .then(Commands.literal("reset")
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> adminResetPlayer(
                            context.getSource(),
                            EntityArgument.getPlayer(context, "target")
                        )))))
            .then(Commands.literal("stats")
                .executes(context -> showAll(context.getSource().getPlayerOrException()))
                .then(Commands.literal("gui")
                    .executes(context -> openGui(context.getSource().getPlayerOrException())))
                .then(Commands.literal("leaderboard")
                    .executes(context -> openLeaderboard(context.getSource().getPlayerOrException())))
                .then(Commands.literal("mining")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.MINING)))
                .then(Commands.literal("woodcutting")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.WOODCUTTING)))
                .then(Commands.literal("excavation")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.EXCAVATION)))
                .then(Commands.literal("acrobatics")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.ACROBATICS)))
                .then(Commands.literal("fishing")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.FISHING)))
                .then(Commands.literal("swords")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.SWORDS)))
                .then(Commands.literal("axes")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.AXES)))
                .then(Commands.literal("unarmed")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.UNARMED)))
                .then(Commands.literal("archery")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.ARCHERY)))
                .then(Commands.literal("herbalism")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.HERBALISM)))
                .then(Commands.literal("alchemy")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.ALCHEMY)))
                .then(Commands.literal("repair")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.REPAIR)))
                .then(Commands.literal("smelting")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.SMELTING)))
                .then(Commands.literal("training")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.TRAINING)))
                .then(Commands.literal("capture")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.CAPTURE)))
                .then(Commands.literal("breeding")
                    .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.BREEDING)))));
        dispatcher.register(Commands.literal("mining")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.MINING)));
        dispatcher.register(Commands.literal("woodcutting")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.WOODCUTTING)));
        dispatcher.register(Commands.literal("excavation")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.EXCAVATION)));
        dispatcher.register(Commands.literal("acrobatics")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.ACROBATICS)));
        dispatcher.register(Commands.literal("fishing")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.FISHING)));
        dispatcher.register(Commands.literal("swords")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.SWORDS)));
        dispatcher.register(Commands.literal("axes")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.AXES)));
        dispatcher.register(Commands.literal("unarmed")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.UNARMED)));
        dispatcher.register(Commands.literal("archery")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.ARCHERY)));
        dispatcher.register(Commands.literal("herbalism")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.HERBALISM)));
        dispatcher.register(Commands.literal("alchemy")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.ALCHEMY)));
        dispatcher.register(Commands.literal("repair")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.REPAIR)));
        dispatcher.register(Commands.literal("smelting")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.SMELTING)));
        dispatcher.register(Commands.literal("training")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.TRAINING)));
        dispatcher.register(Commands.literal("capture")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.CAPTURE)));
        dispatcher.register(Commands.literal("breeding")
            .requires(source -> source.getPlayer() != null)
            .executes(context -> showOne(context.getSource().getPlayerOrException(), SkillType.BREEDING)));
    }

    private static int showAll(ServerPlayer player) {
        PlayerSkillProfile profile = SkillManager.getInstance().getProfile(player);
        player.sendSystemMessage(Component.literal("== MMO Skills =="));
        for (SkillType skillType : SkillType.values()) {
            player.sendSystemMessage(formatOverviewLine(profile, skillType));
        }
        return 1;
    }

    private static int openGui(ServerPlayer player) {
        if (SkillGuiBridge.openMain(player)) {
            return 1;
        }

        player.sendSystemMessage(Component.literal("GUI is not available in this environment. Showing text stats instead."));
        return showAll(player);
    }

    private static int openLeaderboard(ServerPlayer player) {
        if (!SkillGuiBridge.openLeaderboard(player, 0)) {
            player.sendSystemMessage(Component.literal("GUI leaderboard is not available in this environment."));
        }
        return 1;
    }

    private static int resetSelf(ServerPlayer player) {
        boolean removed = SkillPersistentState.get(player.getServer()).reset(player.getUUID());
        player.sendSystemMessage(Component.literal(
            removed ? "Your skill data was reset." : "No skill data was found for you."
        ));
        return 1;
    }

    private static int resetAll(CommandSourceStack source) {
        int count = SkillPersistentState.get(source.getServer()).resetAll();
        source.sendSuccess(() -> Component.literal("Reset skill data for " + count + " player(s)."), true);
        return 1;
    }

    private static int adminAddXp(CommandSourceStack source, ServerPlayer target, String skillName, double amount) {
        SkillType skillType = parseSkill(source, skillName);
        if (skillType == null) {
            return 0;
        }

        int level = SkillManager.getInstance().awardSkillXp(target, skillType, amount);
        source.sendSuccess(() -> Component.literal(
            "Added " + String.format("%.0f", amount) + " XP to " + target.getGameProfile().getName()
                + "'s " + skillType.displayName() + ". Current level: " + level + "."
        ), true);
        return 1;
    }

    private static int adminSetLevel(CommandSourceStack source, ServerPlayer target, String skillName, int level) {
        SkillType skillType = parseSkill(source, skillName);
        if (skillType == null) {
            return 0;
        }

        PlayerSkillProfile profile = SkillManager.getInstance().getProfile(target);
        profile.getProgress(skillType).setLevel(level);
        SkillPersistentState.get(source.getServer()).put(target.getUUID(), profile);
        source.sendSuccess(() -> Component.literal(
            "Set " + target.getGameProfile().getName() + "'s " + skillType.displayName() + " to level " + level + "."
        ), true);
        target.sendSystemMessage(Component.literal(skillType.displayName() + " was set to level " + level + " by an operator."));
        return 1;
    }

    private static int adminResetPlayer(CommandSourceStack source, ServerPlayer target) {
        boolean removed = SkillPersistentState.get(source.getServer()).reset(target.getUUID());
        source.sendSuccess(() -> Component.literal(
            removed
                ? "Reset skill data for " + target.getGameProfile().getName() + "."
                : "No skill data was found for " + target.getGameProfile().getName() + "."
        ), true);
        target.sendSystemMessage(Component.literal("Your skill data was reset by an operator."));
        return 1;
    }

    private static SkillType parseSkill(CommandSourceStack source, String skillName) {
        try {
            return SkillType.valueOf(skillName.toUpperCase());
        } catch (IllegalArgumentException exception) {
            source.sendFailure(Component.literal("Unknown skill: " + skillName + ". Use names like mining, training, capture, or breeding."));
            return null;
        }
    }

    private static int showOne(ServerPlayer player, SkillType skillType) {
        SkillManager manager = SkillManager.getInstance();
        PlayerSkillProfile profile = manager.getProfile(player);
        SkillProgress progress = profile.getProgress(skillType);
        int nextLevelXp = SkillProgress.xpRequiredForNextLevel(progress.level());
        double percent = nextLevelXp <= 0 ? 100.0 : (progress.xpIntoLevel() / nextLevelXp) * 100.0;

        player.sendSystemMessage(Component.literal("== " + skillType.displayName() + " =="));
        player.sendSystemMessage(Component.literal(xpStatusLine(progress, percent)));
        player.sendSystemMessage(Component.literal("XP Source: " + xpSource(skillType)));
        String activeLine = activeAbilityLine(player, skillType, progress.level(), manager.getActiveAbilities());
        if (activeLine != null) {
            player.sendSystemMessage(Component.literal(activeLine));
        }
        player.sendSystemMessage(Component.literal(passiveAbilityLine(skillType, progress.level())));
        return 1;
    }

    private static Component formatOverviewLine(PlayerSkillProfile profile, SkillType skillType) {
        SkillProgress progress = profile.getProgress(skillType);
        int nextLevelXp = SkillProgress.xpRequiredForNextLevel(progress.level());
        double percent = nextLevelXp <= 0 ? 100.0 : (progress.xpIntoLevel() / nextLevelXp) * 100.0;
        String line = skillType.displayName() + ": " + xpStatusLine(progress, percent);
        return Component.literal(line);
    }

    private static String xpStatusLine(SkillProgress progress, double percent) {
        int nextLevelXp = SkillProgress.xpRequiredForNextLevel(progress.level());
        if (nextLevelXp <= 0) {
            return String.format("Level %d | MAX", progress.level());
        }
        return String.format("Level %d | %.0f/%d XP | %.1f%%", progress.level(), progress.xpIntoLevel(), nextLevelXp, percent);
    }

    private static String activeAbilityLine(ServerPlayer player, SkillType skillType, int skillLevel, ActiveAbilityTracker tracker) {
        AbilityType ability = activeAbility(skillType);
        if (ability == null) {
            return null;
        }
        long remainingActive = tracker.remainingActiveSeconds(player, ability);
        long remainingCooldown = tracker.remainingCooldownSeconds(player, ability);
        String status;

        if (remainingActive > 0) {
            status = "active (" + remainingActive + "s left)";
        } else if (remainingCooldown > 0) {
            status = "cooldown (" + remainingCooldown + "s left)";
        } else if (skillLevel >= ability.unlockLevel()) {
            status = "ready";
        } else {
            status = "locked until Lv." + ability.unlockLevel();
        }

        return String.format(
            "Active: %s | duration %ds | cooldown %ds | %s",
            ability.displayName(),
            ability.durationSeconds(skillLevel),
            ability.cooldownSeconds(),
            status
        );
    }

    private static String passiveAbilityLine(SkillType skillType, int skillLevel) {
        return switch (skillType) {
            case MINING -> String.format(
                "Passive: Double Drops | chance %.1f%% | Super Breaker active = triple drops",
                scaledChance(skillLevel, 1000, 100.0)
            );
            case WOODCUTTING -> String.format(
                "Passive: Harvest Lumber | chance %.1f%%",
                scaledChance(skillLevel, 1000, 100.0)
            );
            case EXCAVATION -> String.format(
                "Passive: Archaeology | orb chance %.1f%% | orb reward %d",
                SkillTables.archaeologyRank(skillLevel) * 2.0,
                SkillTables.archaeologyRank(skillLevel)
            );
            case ACROBATICS -> String.format(
                "Passive: Roll | negate fall chance %.1f%%",
                SkillTables.acrobaticsRollChance(skillLevel)
            );
            case FISHING -> String.format(
                "Passive: Treasure Hunter | bonus catch chance %.1f%%",
                SkillTables.fishingTreasureHunterChance(skillLevel)
            );
            case SWORDS -> String.format(
                "Passive: Bleed | chance %.1f%% | duration %ds",
                SkillTables.swordsBleedChance(skillLevel),
                SkillTables.swordsBleedSeconds(skillLevel)
            );
            case AXES -> String.format(
                "Passive: Armor Impact | chance %.1f%% | duration %ds",
                SkillTables.axesArmorImpactChance(skillLevel),
                SkillTables.axesArmorImpactSeconds(skillLevel)
            );
            case UNARMED -> String.format(
                "Passive: Iron Arm Style | chance %.1f%%",
                SkillTables.unarmedIronArmChance(skillLevel)
            );
            case ARCHERY -> String.format(
                "Passive: Daze | chance %.1f%% | duration %ds",
                SkillTables.archeryDazeChance(skillLevel),
                SkillTables.archeryDazeSeconds(skillLevel)
            );
            case HERBALISM -> String.format(
                "Passive: Green Thumb | auto-replant chance %.1f%%",
                SkillTables.herbalismGreenThumbChance(skillLevel)
            );
            case ALCHEMY -> String.format(
                "Passive: Alchemist's Bounty | bonus potion chance %.1f%%",
                SkillTables.alchemyPotionBountyChance(skillLevel)
            );
            case REPAIR -> String.format(
                "Passive: Repair Mastery | refund chance %.1f%% | refund up to %d level(s)",
                SkillTables.repairMasteryChance(skillLevel),
                SkillTables.repairMasteryRefundLevels(skillLevel, 3)
            );
            case SMELTING -> String.format(
                "Passive: Second Smelt | bonus output chance %.1f%%",
                SkillTables.smeltingSecondSmeltChance(skillLevel)
            );
            case TRAINING -> String.format(
                "Passive: Battle Experience Boost | +%.1f%% battle exp",
                SkillTables.trainingBattleExperienceBonusPercent(skillLevel)
            );
            case CAPTURE -> String.format(
                "Passive: Catch Rate Boost | +%.1f%% catch rate",
                SkillTables.captureCatchRateBonusPercent(skillLevel)
            );
            case BREEDING -> String.format(
                "Passive: Hatch Friendship Boost | +%d friendship on hatch",
                SkillTables.breedingEggFriendshipBonus(skillLevel)
            );
        };
    }

    private static String xpSource(SkillType skillType) {
        return switch (skillType) {
            case MINING -> "pickaxe mining";
            case WOODCUTTING -> "axe wood blocks";
            case EXCAVATION -> "shovel digging blocks";
            case ACROBATICS -> "surviving falls and rolling";
            case FISHING -> "catching fish";
            case SWORDS -> "damaging enemies with swords";
            case AXES -> "damaging enemies with axes";
            case UNARMED -> "damaging enemies bare-handed";
            case ARCHERY -> "hitting enemies with arrows";
            case HERBALISM -> "harvesting crops and plants";
            case ALCHEMY -> "brewing and collecting finished potions";
            case REPAIR -> "repairing damaged tools and gear at an anvil";
            case SMELTING -> "taking finished smelted output from furnaces";
            case TRAINING -> "pokemon battle exp and level ups";
            case CAPTURE -> "successful pokemon captures";
            case BREEDING -> "egg collection and egg hatching";
        };
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
            case ACROBATICS -> null;
            case FISHING -> null;
            case ARCHERY -> null;
            case ALCHEMY -> null;
            case REPAIR -> null;
            case SMELTING -> null;
            case TRAINING -> null;
            case CAPTURE -> null;
            case BREEDING -> null;
        };
    }

    private static double scaledChance(int level, int maxBonusLevel, double chanceMax) {
        return Math.min(chanceMax, (level / (double) maxBonusLevel) * chanceMax);
    }
}
