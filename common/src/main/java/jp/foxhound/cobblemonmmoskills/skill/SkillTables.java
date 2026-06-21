package jp.foxhound.cobblemonmmoskills.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class SkillTables {
    public static final Map<Block, Integer> MINING_XP = Map.ofEntries(
        Map.entry(Blocks.SCULK, 4),
        Map.entry(Blocks.SCULK_VEIN, 3),
        Map.entry(Blocks.SCULK_SENSOR, 6),
        Map.entry(Blocks.SCULK_CATALYST, 10),
        Map.entry(Blocks.SCULK_SHRIEKER, 12),
        Map.entry(Blocks.REINFORCED_DEEPSLATE, 500),
        Map.entry(Blocks.TUFF, 10),
        Map.entry(Blocks.COPPER_ORE, 1400),
        Map.entry(Blocks.DEEPSLATE_COPPER_ORE, 1900),
        Map.entry(Blocks.DEEPSLATE, 30),
        Map.entry(Blocks.COBBLED_DEEPSLATE, 15),
        Map.entry(Blocks.CALCITE, 400),
        Map.entry(Blocks.SMOOTH_BASALT, 300),
        Map.entry(Blocks.AMETHYST_BLOCK, 500),
        Map.entry(Blocks.BUDDING_AMETHYST, 400),
        Map.entry(Blocks.SMALL_AMETHYST_BUD, 10),
        Map.entry(Blocks.MEDIUM_AMETHYST_BUD, 20),
        Map.entry(Blocks.LARGE_AMETHYST_BUD, 30),
        Map.entry(Blocks.AMETHYST_CLUSTER, 60),
        Map.entry(Blocks.BONE_BLOCK, 500),
        Map.entry(Blocks.CRYING_OBSIDIAN, 3000),
        Map.entry(Blocks.CHAIN, 100),
        Map.entry(Blocks.BLACKSTONE, 55),
        Map.entry(Blocks.WARPED_NYLIUM, 5),
        Map.entry(Blocks.CRIMSON_NYLIUM, 5),
        Map.entry(Blocks.ANCIENT_DEBRIS, 7777),
        Map.entry(Blocks.MAGMA_BLOCK, 30),
        Map.entry(Blocks.BASALT, 40),
        Map.entry(Blocks.TUBE_CORAL_BLOCK, 75),
        Map.entry(Blocks.BRAIN_CORAL_BLOCK, 80),
        Map.entry(Blocks.BUBBLE_CORAL_BLOCK, 70),
        Map.entry(Blocks.FIRE_CORAL_BLOCK, 90),
        Map.entry(Blocks.HORN_CORAL_BLOCK, 125),
        Map.entry(Blocks.COAL_ORE, 400),
        Map.entry(Blocks.DEEPSLATE_COAL_ORE, 700),
        Map.entry(Blocks.DIAMOND_ORE, 2400),
        Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, 3600),
        Map.entry(Blocks.EMERALD_ORE, 1000),
        Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, 1700),
        Map.entry(Blocks.END_STONE_BRICKS, 50),
        Map.entry(Blocks.CHISELED_NETHER_BRICKS, 50),
        Map.entry(Blocks.CRACKED_NETHER_BRICKS, 50),
        Map.entry(Blocks.NETHER_BRICKS, 50),
        Map.entry(Blocks.RED_NETHER_BRICKS, 50),
        Map.entry(Blocks.END_STONE, 15),
        Map.entry(Blocks.GLOWSTONE, 15),
        Map.entry(Blocks.GOLD_ORE, 1300),
        Map.entry(Blocks.DEEPSLATE_GOLD_ORE, 1900),
        Map.entry(Blocks.NETHER_GOLD_ORE, 1300),
        Map.entry(Blocks.GILDED_BLACKSTONE, 200),
        Map.entry(Blocks.TERRACOTTA, 30),
        Map.entry(Blocks.IRON_ORE, 900),
        Map.entry(Blocks.DEEPSLATE_IRON_ORE, 1300),
        Map.entry(Blocks.LAPIS_ORE, 800),
        Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, 1400),
        Map.entry(Blocks.MOSSY_COBBLESTONE, 30),
        Map.entry(Blocks.NETHERRACK, 15),
        Map.entry(Blocks.OBSIDIAN, 150),
        Map.entry(Blocks.PACKED_ICE, 15),
        Map.entry(Blocks.BLUE_ICE, 15),
        Map.entry(Blocks.NETHER_QUARTZ_ORE, 300),
        Map.entry(Blocks.REDSTONE_ORE, 600),
        Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, 900),
        Map.entry(Blocks.SANDSTONE, 30),
        Map.entry(Blocks.BLACK_TERRACOTTA, 50),
        Map.entry(Blocks.BLUE_TERRACOTTA, 50),
        Map.entry(Blocks.BROWN_TERRACOTTA, 50),
        Map.entry(Blocks.CYAN_TERRACOTTA, 50),
        Map.entry(Blocks.GRAY_TERRACOTTA, 50),
        Map.entry(Blocks.GREEN_TERRACOTTA, 50),
        Map.entry(Blocks.LIGHT_BLUE_TERRACOTTA, 50),
        Map.entry(Blocks.LIGHT_GRAY_TERRACOTTA, 50),
        Map.entry(Blocks.LIME_TERRACOTTA, 50),
        Map.entry(Blocks.MAGENTA_TERRACOTTA, 50),
        Map.entry(Blocks.ORANGE_TERRACOTTA, 50),
        Map.entry(Blocks.PINK_TERRACOTTA, 50),
        Map.entry(Blocks.PURPLE_TERRACOTTA, 50),
        Map.entry(Blocks.RED_TERRACOTTA, 50),
        Map.entry(Blocks.WHITE_TERRACOTTA, 50),
        Map.entry(Blocks.YELLOW_TERRACOTTA, 50),
        Map.entry(Blocks.STONE, 15),
        Map.entry(Blocks.GRANITE, 15),
        Map.entry(Blocks.ANDESITE, 15),
        Map.entry(Blocks.DIORITE, 15),
        Map.entry(Blocks.STONE_BRICKS, 50),
        Map.entry(Blocks.CRACKED_STONE_BRICKS, 50),
        Map.entry(Blocks.MOSSY_STONE_BRICKS, 50),
        Map.entry(Blocks.CHISELED_STONE_BRICKS, 50),
        Map.entry(Blocks.RED_SANDSTONE, 100),
        Map.entry(Blocks.PRISMARINE, 70),
        Map.entry(Blocks.PRISMARINE_BRICKS, 70),
        Map.entry(Blocks.DARK_PRISMARINE, 70),
        Map.entry(Blocks.SEA_LANTERN, 70),
        Map.entry(Blocks.PURPUR_BLOCK, 200),
        Map.entry(Blocks.PURPUR_PILLAR, 250),
        Map.entry(Blocks.PURPUR_SLAB, 150),
        Map.entry(Blocks.PURPUR_STAIRS, 250),
        Map.entry(Blocks.PACKED_MUD, 30),
        Map.entry(Blocks.MUD_BRICKS, 40),
        Map.entry(Blocks.DRIPSTONE_BLOCK, 35)
    );

    public static final Map<Block, Integer> WOODCUTTING_XP = Map.ofEntries(
        Map.entry(Blocks.CRIMSON_HYPHAE, 50),
        Map.entry(Blocks.STRIPPED_CRIMSON_HYPHAE, 50),
        Map.entry(Blocks.WARPED_HYPHAE, 50),
        Map.entry(Blocks.STRIPPED_WARPED_HYPHAE, 50),
        Map.entry(Blocks.NETHER_WART_BLOCK, 1),
        Map.entry(Blocks.WARPED_WART_BLOCK, 1),
        Map.entry(Blocks.SHROOMLIGHT, 100),
        Map.entry(Blocks.CRIMSON_STEM, 35),
        Map.entry(Blocks.WARPED_STEM, 35),
        Map.entry(Blocks.OAK_LOG, 70),
        Map.entry(Blocks.CHERRY_LOG, 105),
        Map.entry(Blocks.SPRUCE_LOG, 80),
        Map.entry(Blocks.BIRCH_LOG, 90),
        Map.entry(Blocks.JUNGLE_LOG, 100),
        Map.entry(Blocks.ACACIA_LOG, 90),
        Map.entry(Blocks.DARK_OAK_LOG, 90),
        Map.entry(Blocks.STRIPPED_OAK_LOG, 70),
        Map.entry(Blocks.STRIPPED_CHERRY_LOG, 105),
        Map.entry(Blocks.STRIPPED_SPRUCE_LOG, 80),
        Map.entry(Blocks.STRIPPED_BIRCH_LOG, 90),
        Map.entry(Blocks.STRIPPED_JUNGLE_LOG, 100),
        Map.entry(Blocks.STRIPPED_ACACIA_LOG, 90),
        Map.entry(Blocks.STRIPPED_DARK_OAK_LOG, 90),
        Map.entry(Blocks.STRIPPED_OAK_WOOD, 70),
        Map.entry(Blocks.STRIPPED_CHERRY_WOOD, 70),
        Map.entry(Blocks.STRIPPED_SPRUCE_WOOD, 80),
        Map.entry(Blocks.STRIPPED_BIRCH_WOOD, 90),
        Map.entry(Blocks.STRIPPED_JUNGLE_WOOD, 100),
        Map.entry(Blocks.STRIPPED_ACACIA_WOOD, 90),
        Map.entry(Blocks.STRIPPED_DARK_OAK_WOOD, 90),
        Map.entry(Blocks.STRIPPED_MANGROVE_LOG, 110),
        Map.entry(Blocks.STRIPPED_CRIMSON_STEM, 50),
        Map.entry(Blocks.STRIPPED_WARPED_STEM, 50),
        Map.entry(Blocks.OAK_WOOD, 70),
        Map.entry(Blocks.CHERRY_WOOD, 105),
        Map.entry(Blocks.SPRUCE_WOOD, 70),
        Map.entry(Blocks.BIRCH_WOOD, 70),
        Map.entry(Blocks.JUNGLE_WOOD, 70),
        Map.entry(Blocks.ACACIA_WOOD, 70),
        Map.entry(Blocks.DARK_OAK_WOOD, 70),
        Map.entry(Blocks.MANGROVE_WOOD, 80),
        Map.entry(Blocks.MANGROVE_LOG, 95),
        Map.entry(Blocks.MANGROVE_ROOTS, 10),
        Map.entry(Blocks.RED_MUSHROOM_BLOCK, 70),
        Map.entry(Blocks.BROWN_MUSHROOM_BLOCK, 70),
        Map.entry(Blocks.MUSHROOM_STEM, 80)
    );

    public static final Map<Block, Integer> EXCAVATION_XP = Map.ofEntries(
        Map.entry(Blocks.CLAY, 40),
        Map.entry(Blocks.DIRT, 40),
        Map.entry(Blocks.ROOTED_DIRT, 60),
        Map.entry(Blocks.COARSE_DIRT, 40),
        Map.entry(Blocks.PODZOL, 40),
        Map.entry(Blocks.GRASS_BLOCK, 40),
        Map.entry(Blocks.GRAVEL, 40),
        Map.entry(Blocks.MYCELIUM, 40),
        Map.entry(Blocks.SAND, 40),
        Map.entry(Blocks.RED_SAND, 40),
        Map.entry(Blocks.SNOW, 20),
        Map.entry(Blocks.SNOW_BLOCK, 40),
        Map.entry(Blocks.SOUL_SAND, 40),
        Map.entry(Blocks.SOUL_SOIL, 40),
        Map.entry(Blocks.MUD, 80),
        Map.entry(Blocks.MUDDY_MANGROVE_ROOTS, 90)
    );

    private static final Map<String, Integer> COBBLEMON_MINING_XP = Map.ofEntries(
        Map.entry("cobblemon:dawn_stone_ore", 1800),
        Map.entry("cobblemon:dusk_stone_ore", 1800),
        Map.entry("cobblemon:fire_stone_ore", 1800),
        Map.entry("cobblemon:ice_stone_ore", 1800),
        Map.entry("cobblemon:leaf_stone_ore", 1800),
        Map.entry("cobblemon:moon_stone_ore", 1800),
        Map.entry("cobblemon:shiny_stone_ore", 1800),
        Map.entry("cobblemon:sun_stone_ore", 1800),
        Map.entry("cobblemon:thunder_stone_ore", 1800),
        Map.entry("cobblemon:water_stone_ore", 1800),
        Map.entry("cobblemon:deepslate_dawn_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_dusk_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_fire_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_ice_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_leaf_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_moon_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_shiny_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_sun_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_thunder_stone_ore", 2600),
        Map.entry("cobblemon:deepslate_water_stone_ore", 2600),
        Map.entry("cobblemon:dripstone_moon_stone_ore", 2200),
        Map.entry("cobblemon:nether_fire_stone_ore", 2200),
        Map.entry("cobblemon:terracotta_sun_stone_ore", 2200),
        Map.entry("cobblemon:dawn_stone_block", 1000),
        Map.entry("cobblemon:dusk_stone_block", 1000),
        Map.entry("cobblemon:fire_stone_block", 1000),
        Map.entry("cobblemon:ice_stone_block", 1000),
        Map.entry("cobblemon:leaf_stone_block", 1000),
        Map.entry("cobblemon:moon_stone_block", 1000),
        Map.entry("cobblemon:shiny_stone_block", 1000),
        Map.entry("cobblemon:sun_stone_block", 1000),
        Map.entry("cobblemon:thunder_stone_block", 1000),
        Map.entry("cobblemon:water_stone_block", 1000),
        Map.entry("cobblemon:tumblestone_block", 80),
        Map.entry("cobblemon:black_tumblestone_block", 80),
        Map.entry("cobblemon:sky_tumblestone_block", 80),
        Map.entry("cobblemon:tumblestone_cluster", 150),
        Map.entry("cobblemon:black_tumblestone_cluster", 150),
        Map.entry("cobblemon:sky_tumblestone_cluster", 150),
        Map.entry("cobblemon:small_budding_tumblestone", 90),
        Map.entry("cobblemon:medium_budding_tumblestone", 120),
        Map.entry("cobblemon:large_budding_tumblestone", 150),
        Map.entry("cobblemon:small_budding_black_tumblestone", 90),
        Map.entry("cobblemon:medium_budding_black_tumblestone", 120),
        Map.entry("cobblemon:large_budding_black_tumblestone", 150),
        Map.entry("cobblemon:small_budding_sky_tumblestone", 90),
        Map.entry("cobblemon:medium_budding_sky_tumblestone", 120),
        Map.entry("cobblemon:large_budding_sky_tumblestone", 150)
    );

    private static final Map<String, Integer> COBBLEMON_WOODCUTTING_XP = Map.ofEntries(
        Map.entry("cobblemon:apricorn_log", 80),
        Map.entry("cobblemon:apricorn_wood", 80),
        Map.entry("cobblemon:stripped_apricorn_log", 80),
        Map.entry("cobblemon:stripped_apricorn_wood", 80),
        Map.entry("cobblemon:apricorn_planks", 30),
        Map.entry("cobblemon:apricorn_leaves", 10),
        Map.entry("cobblemon:saccharine_log", 110),
        Map.entry("cobblemon:saccharine_log_slathered", 120),
        Map.entry("cobblemon:saccharine_wood", 110),
        Map.entry("cobblemon:stripped_saccharine_log", 110),
        Map.entry("cobblemon:stripped_saccharine_wood", 110),
        Map.entry("cobblemon:saccharine_planks", 35),
        Map.entry("cobblemon:saccharine_leaves", 12)
    );

    public static final Map<Block, Integer> HERBALISM_XP = Map.ofEntries(
        Map.entry(Blocks.WHEAT, 50),
        Map.entry(Blocks.CARROTS, 50),
        Map.entry(Blocks.POTATOES, 50),
        Map.entry(Blocks.BEETROOTS, 50),
        Map.entry(Blocks.NETHER_WART, 50),
        Map.entry(Blocks.COCOA, 30),
        Map.entry(Blocks.SWEET_BERRY_BUSH, 30),
        Map.entry(Blocks.MELON, 20),
        Map.entry(Blocks.PUMPKIN, 20),
        Map.entry(Blocks.SUGAR_CANE, 20),
        Map.entry(Blocks.CACTUS, 20),
        Map.entry(Blocks.BAMBOO, 15),
        Map.entry(Blocks.KELP, 15)
    );

    public static final List<FishingTreasure> FISHING_TREASURES = List.of(
        fishingTreasure(Items.COD, 1, 60.0),
        fishingTreasure(Items.SALMON, 1, 25.0),
        fishingTreasure(Items.PUFFERFISH, 1, 8.0),
        fishingTreasure(Items.NAME_TAG, 1, 3.0),
        fishingTreasure(Items.ENCHANTED_BOOK, 1, 2.0),
        fishingTreasure(Items.NAUTILUS_SHELL, 1, 2.0)
    );

    public static final List<ExcavationTreasure> EXCAVATION_TREASURES = List.of(
        treasure(Items.HEART_OF_THE_SEA, 1, 9999, 0.01, 90, Blocks.MUD),
        treasure(Items.POTATO, 1, 50, 3.0, 10, Blocks.DIRT, Blocks.MUD),
        treasure(Items.SPYGLASS, 1, 500, 0.1, 7, Blocks.MUD, Blocks.DIRT),
        treasure(Items.STICK, 2, 50, 2.0, 1, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
        treasure(Items.FEATHER, 3, 100, 1.0, 5, Blocks.MUD),
        treasure(Items.TRIDENT, 1, 100, 0.02, 40, Blocks.MUD, Blocks.CLAY, Blocks.MUDDY_MANGROVE_ROOTS),
        treasure(Items.CAKE, 1, 3000, 0.05, 75, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.CLAY, Blocks.MYCELIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL),
        treasure(Items.GUNPOWDER, 1, 30, 10.0, 10, Blocks.GRAVEL),
        treasure(Items.BONE, 1, 30, 10.0, 20, Blocks.GRAVEL, Blocks.MUD),
        treasure(Items.APPLE, 1, 100, 0.1, 25, Blocks.GRASS_BLOCK, Blocks.MYCELIUM),
        treasure(Items.SLIME_BALL, 1, 100, 5.0, 15, Blocks.CLAY),
        treasure(Items.BUCKET, 1, 100, 0.1, 50, Blocks.CLAY),
        treasure(Items.NETHERRACK, 1, 30, 0.5, 85, Blocks.GRAVEL),
        treasure(Items.RED_MUSHROOM, 1, 80, 0.5, 50, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.MUD),
        treasure(Items.BROWN_MUSHROOM, 1, 80, 0.5, 50, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.MUD),
        treasure(Items.EGG, 1, 100, 1.0, 25, Blocks.GRASS_BLOCK),
        treasure(Items.SOUL_SAND, 1, 80, 0.5, 65, Blocks.SAND, Blocks.RED_SAND),
        treasure(Items.CLOCK, 1, 100, 0.1, 50, Blocks.CLAY),
        treasure(Items.COBWEB, 1, 150, 5.0, 75, Blocks.CLAY),
        treasure(Items.STRING, 1, 200, 5.0, 25, Blocks.CLAY),
        treasure(Items.GLOWSTONE_DUST, 1, 80, 5.0, 5, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM),
        treasure(Items.MUSIC_DISC_13, 1, 3000, 0.05, 25, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.CLAY, Blocks.MYCELIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL),
        treasure(Items.MUSIC_DISC_CAT, 1, 3000, 0.05, 25, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.CLAY, Blocks.MYCELIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL),
        treasure(Items.DIAMOND, 1, 1000, 0.13, 35, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.CLAY, Blocks.MYCELIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.MUD),
        treasure(Items.COCOA_BEANS, 1, 100, 1.33, 35, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.MUD),
        treasure(Items.QUARTZ, 1, 100, 0.5, 85, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL),
        treasure(Items.NAME_TAG, 1, 3000, 0.05, 25, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.CLAY, Blocks.MYCELIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL)
    );

    private SkillTables() {
    }

    public static int miningSkillXp(Block block) {
        return MINING_XP.getOrDefault(block, COBBLEMON_MINING_XP.getOrDefault(blockId(block), 0));
    }

    public static int woodcuttingSkillXp(Block block) {
        return WOODCUTTING_XP.getOrDefault(block, COBBLEMON_WOODCUTTING_XP.getOrDefault(blockId(block), 0));
    }

    public static int excavationSkillXp(Block block) {
        return EXCAVATION_XP.getOrDefault(block, 0);
    }

    private static String blockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    private static ExcavationTreasure treasure(Item item, int amount, int skillXp, double chancePercent, int unlockLevel, Block... blocks) {
        List<Block> sources = new ArrayList<>(blocks.length);
        for (Block block : blocks) {
            sources.add(block);
        }
        return new ExcavationTreasure(item, amount, skillXp, chancePercent, unlockLevel, sources);
    }

    private static FishingTreasure fishingTreasure(Item item, int amount, double chanceWeight) {
        return new FishingTreasure(item, amount, chanceWeight);
    }

    public static boolean isLeafBlowerTarget(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.NETHER_WART_BLOCK
            || block == Blocks.WARPED_WART_BLOCK
            || block == Blocks.SHROOMLIGHT
            || block == Blocks.RED_MUSHROOM_BLOCK
            || block == Blocks.BROWN_MUSHROOM_BLOCK
            || state.is(BlockTags.LEAVES);
    }

    public static int archaeologyRank(int level) {
        if (level >= 100) return 8;
        if (level >= 85) return 7;
        if (level >= 75) return 6;
        if (level >= 65) return 5;
        if (level >= 50) return 4;
        if (level >= 35) return 3;
        if (level >= 25) return 2;
        if (level >= 1) return 1;
        return 0;
    }

    public static double acrobaticsRollChance(int level) {
        return Math.min(25.0, level * 0.25);
    }

    public static double acrobaticsSkillXp(float damageTaken) {
        return Math.max(40.0, damageTaken * 120.0);
    }

    public static double fishingTreasureHunterChance(int level) {
        return Math.min(20.0, level * 0.20);
    }

    public static double fishingSkillXp(int fishCaughtDelta) {
        return fishCaughtDelta * 250.0;
    }

    public static double swordsBleedChance(int level) {
        return Math.min(30.0, level * 0.30);
    }

    public static int swordsBleedSeconds(int level) {
        return Math.min(6, 2 + (level / 25));
    }

    public static double swordsSkillXp(float damageDealt, boolean kill) {
        double base = Math.max(20.0, damageDealt * 20.0);
        return kill ? base + 100.0 : base;
    }

    public static double herbalismGreenThumbChance(int level) {
        return Math.min(50.0, level * 0.50);
    }

    public static double axesArmorImpactChance(int level) {
        return Math.min(35.0, level * 0.35);
    }

    public static int axesArmorImpactSeconds(int level) {
        return Math.min(6, 2 + (level / 25));
    }

    public static double axesSkillXp(float damageDealt, boolean kill) {
        double base = Math.max(20.0, damageDealt * 22.0);
        return kill ? base + 120.0 : base;
    }

    public static double unarmedIronArmChance(int level) {
        return Math.min(30.0, level * 0.30);
    }

    public static double unarmedSkillXp(float damageDealt, boolean kill) {
        double base = Math.max(20.0, damageDealt * 20.0);
        return kill ? base + 100.0 : base;
    }

    public static double archeryDazeChance(int level) {
        return Math.min(25.0, level * 0.25);
    }

    public static int archeryDazeSeconds(int level) {
        return Math.min(5, 2 + (level / 30));
    }

    public static double archerySkillXp(float damageDealt, double distance) {
        return Math.max(25.0, damageDealt * 18.0 + Math.min(150.0, distance * 5.0));
    }

    public static double herbalismSkillXp(Block block) {
        return HERBALISM_XP.getOrDefault(block, 0);
    }

    public static double alchemyPotionBountyChance(int level) {
        return Math.min(25.0, level * 0.25);
    }

    public static double alchemySkillXp(ItemStack brewedStack) {
        if (brewedStack.is(Items.LINGERING_POTION)) {
            return 300.0;
        }
        if (brewedStack.is(Items.SPLASH_POTION)) {
            return 250.0;
        }
        return 200.0;
    }

    public static double repairMasteryChance(int level) {
        return Math.min(25.0, level * 0.25);
    }

    public static int repairMasteryRefundLevels(int level, int repairCost) {
        int cap = Math.max(1, 1 + (level / 50));
        return Math.min(repairCost, cap);
    }

    public static double repairSkillXp(ItemStack result, int repairCost) {
        if (!result.isDamageableItem()) {
            return 0.0;
        }
        return Math.max(200.0, repairCost * 125.0);
    }

    public static double smeltingSecondSmeltChance(int level) {
        return Math.min(30.0, level * 0.30);
    }

    public static double smeltingSkillXp(ItemStack outputStack) {
        return outputStack.getCount() * 75.0;
    }

    public static double trainingBattleExperienceBonusPercent(int level) {
        return Math.min(25.0, level * 0.25);
    }

    public static int trainingBattleExperienceBonusAmount(int level, int baseExperience) {
        return (int) Math.floor(baseExperience * (trainingBattleExperienceBonusPercent(level) / 100.0));
    }

    public static double trainingSkillXpFromBattleExperience(int gainedExperience) {
        if (gainedExperience <= 0) {
            return 0.0;
        }
        return Math.max(20.0, gainedExperience * 0.20);
    }

    public static double trainingSkillXpFromLevelGain(int oldLevel, int newLevel) {
        return Math.max(0, newLevel - oldLevel) * 300.0;
    }

    public static double captureCatchRateBonusPercent(int level) {
        return Math.min(10.0, level * 0.10);
    }

    public static float captureCatchRateBonusMultiplier(int level) {
        return (float) (1.0 + (captureCatchRateBonusPercent(level) / 100.0));
    }

    public static double captureSkillXp(int pokemonLevel, boolean criticalCapture) {
        double base = 200.0 + Math.max(1, pokemonLevel) * 8.0;
        return criticalCapture ? base + 150.0 : base;
    }

    public static int breedingEggFriendshipBonus(int level) {
        return Math.min(20, level / 5);
    }

    public static double breedingSkillXpFromEggCollected() {
        return 250.0;
    }

    public static double breedingSkillXpFromEggHatched(int eggCycles) {
        return 200.0 + Math.max(0, eggCycles) * 4.0;
    }

    public static boolean isHerbalismHarvestTarget(BlockState state) {
        Block block = state.getBlock();
        if (!HERBALISM_XP.containsKey(block)) {
            return false;
        }

        if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        }

        if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }

        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= 2;
        }

        if (block instanceof SweetBerryBushBlock) {
            return state.getValue(SweetBerryBushBlock.AGE) >= 3;
        }

        return block == Blocks.MELON
            || block == Blocks.PUMPKIN
            || block == Blocks.SUGAR_CANE
            || block == Blocks.CACTUS
            || block == Blocks.BAMBOO
            || block == Blocks.KELP;
    }

    public static BlockState replantedState(BlockState harvestedState) {
        Block block = harvestedState.getBlock();

        if (block instanceof CropBlock cropBlock) {
            return cropBlock.getStateForAge(0);
        }

        if (block instanceof NetherWartBlock) {
            return block.defaultBlockState().setValue(NetherWartBlock.AGE, 0);
        }

        if (block instanceof CocoaBlock) {
            return block.defaultBlockState()
                .setValue(CocoaBlock.AGE, 0)
                .setValue(CocoaBlock.FACING, harvestedState.getValue(CocoaBlock.FACING));
        }

        if (block instanceof SweetBerryBushBlock) {
            return block.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 1);
        }

        return null;
    }

    public static BlockState greenThumbReplantedState(BlockState harvestedState, int level, RandomSource random) {
        BlockState replanted = replantedState(harvestedState);
        if (replanted == null) {
            return null;
        }

        int bonusAge = greenThumbBonusAge(level, random);
        if (bonusAge <= 0) {
            return replanted;
        }

        Block block = replanted.getBlock();
        if (block instanceof CropBlock cropBlock) {
            return cropBlock.getStateForAge(Math.min(cropBlock.getMaxAge(), bonusAge));
        }

        if (block instanceof NetherWartBlock) {
            return replanted.setValue(NetherWartBlock.AGE, Math.min(3, bonusAge));
        }

        if (block instanceof CocoaBlock) {
            return replanted.setValue(CocoaBlock.AGE, Math.min(2, bonusAge));
        }

        if (block instanceof SweetBerryBushBlock) {
            return replanted.setValue(SweetBerryBushBlock.AGE, Math.max(1, Math.min(3, bonusAge)));
        }

        return replanted;
    }

    private static int greenThumbBonusAge(int level, RandomSource random) {
        int targetAge = Math.min(4, level / 200);
        if (targetAge <= 0) {
            return 0;
        }

        double chance = Math.min(100.0, level * (13.3 / 200.0));
        return random.nextDouble() * 100.0 < chance ? targetAge : 0;
    }

    public record ExcavationTreasure(Item item, int amount, int skillXp, double chancePercent, int unlockLevel, List<Block> sourceBlocks) {
        public boolean canDropFrom(Block block, int level) {
            return level >= unlockLevel && sourceBlocks.contains(block);
        }
    }

    public record FishingTreasure(Item item, int amount, double chanceWeight) {
    }
}
