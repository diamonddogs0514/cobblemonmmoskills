package jp.foxhound.cobblemonmmoskills.skill;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import jp.foxhound.cobblemonmmoskills.state.SkillPersistentState;

public final class SkillManager {
    private static final SkillManager INSTANCE = new SkillManager();
    private static final int TREE_FELLER_THRESHOLD = 1000;
    private final ActiveAbilityTracker activeAbilities = new ActiveAbilityTracker();
    private final Set<String> ignoredBreaks = new HashSet<>();
    private final Set<String> herbalismReplants = new HashSet<>();
    private final Map<UUID, Integer> lastFishCaughtStat = new HashMap<>();

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    public void onBlockBroken(Level world, Player playerEntity, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(world instanceof ServerLevel serverWorld) || !(playerEntity instanceof ServerPlayer player)) {
            return;
        }

        if (consumeIgnoredBreak(serverWorld, pos)) {
            return;
        }
        cleanupHerbalismReplant(serverWorld, pos);

        ItemStack tool = player.getMainHandItem();

        if (isPickaxe(tool) && SkillTables.miningSkillXp(state.getBlock()) > 0) {
            handleMiningBreak(serverWorld, player, pos, state, blockEntity, tool);
        }

        if (isAxe(tool) && SkillTables.woodcuttingSkillXp(state.getBlock()) > 0) {
            handleWoodcuttingBreak(serverWorld, player, pos, state, blockEntity, tool);
        }

        if (isShovel(tool) && SkillTables.excavationSkillXp(state.getBlock()) > 0) {
            handleExcavationBreak(serverWorld, player, pos, state, blockEntity, tool);
        }

        if (SkillTables.isHerbalismHarvestTarget(state)) {
            handleHerbalismBreak(serverWorld, player, pos, state, blockEntity, tool);
        }
    }

    public InteractionResult onAttackBlock(Player playerEntity, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
        if (!(world instanceof ServerLevel serverWorld) || !(playerEntity instanceof ServerPlayer player) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    public InteractionResult onAttackEntity(Player playerEntity, Level world, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (!(playerEntity instanceof ServerPlayer player) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    public InteractionResult onUseBlock(Player playerEntity, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (!(playerEntity instanceof ServerPlayer player) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        tryActivateAbility(player);
        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> onUseItem(Player playerEntity, Level world, InteractionHand hand) {
        if (!(playerEntity instanceof ServerPlayer player) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(playerEntity.getItemInHand(hand));
        }

        tryActivateAbility(player);
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    public void tick(MinecraftServer server) {
        activeAbilities.tick(server);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (activeAbilities.isActive(player, AbilityType.SUPER_BREAKER)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 10, 10, true, false, false));
            }
            if (activeAbilities.isActive(player, AbilityType.GIGA_DRILL_BREAKER)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 10, 10, true, false, false));
            }
            if (activeAbilities.isActive(player, AbilityType.SKULL_SPLITTER)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 1, true, false, false));
            }
            if (activeAbilities.isActive(player, AbilityType.BERSERK)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 0, true, false, false));
            }
            syncFishingProgress(player);
        }
    }

    public void onAlchemyPotionTaken(Player playerEntity, ItemStack stack) {
        if (!(playerEntity instanceof ServerPlayer player)) {
            return;
        }

        PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (potionContents.potion().isEmpty()) {
            return;
        }

        int level = awardSkillXp(player, SkillType.ALCHEMY, SkillTables.alchemySkillXp(stack));
        if (chance(level, 100, SkillTables.alchemyPotionBountyChance(level))) {
            ItemStack bonus = stack.copy();
            bonus.setCount(1);
            giveOrDrop(player, bonus);
        }
    }

    public void onRepairResultTaken(Player playerEntity, ItemStack result, ItemStack leftInput, ItemStack rightInput, int repairCost) {
        if (!(playerEntity instanceof ServerPlayer player) || repairCost <= 0) {
            return;
        }

        if (!isRepairAction(result, leftInput, rightInput)) {
            return;
        }

        int level = awardSkillXp(player, SkillType.REPAIR, SkillTables.repairSkillXp(result, repairCost));
        if (chance(level, 100, SkillTables.repairMasteryChance(level))) {
            int refundLevels = SkillTables.repairMasteryRefundLevels(level, repairCost);
            if (refundLevels > 0) {
                player.giveExperienceLevels(refundLevels);
            }
        }
    }

    public void onSmeltingResultTaken(Player playerEntity, ItemStack stack) {
        if (!(playerEntity instanceof ServerPlayer player) || stack.isEmpty()) {
            return;
        }

        int level = awardSkillXp(player, SkillType.SMELTING, SkillTables.smeltingSkillXp(stack));
        int bonusItems = 0;
        for (int i = 0; i < stack.getCount(); i++) {
            if (chance(level, 100, SkillTables.smeltingSecondSmeltChance(level))) {
                bonusItems++;
            }
        }

        if (bonusItems > 0) {
            ItemStack bonus = stack.copy();
            bonus.setCount(bonusItems);
            giveOrDrop(player, bonus);
        }
    }

    private void handleMiningBreak(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        int level = awardSkillXp(player, SkillType.MINING, SkillTables.miningSkillXp(state.getBlock()));
        boolean active = activeAbilities.isActive(player, AbilityType.SUPER_BREAKER);
        double doubleDropChance = mcmmoDoubleDropChance(level);
        if (active) {
            doubleDropChance = Math.min(100.0, doubleDropChance * 3.0);
        }
        boolean doubleProc = level >= 1 && chancePercent(doubleDropChance);

        if (doubleProc) {
            dropBonusCopies(world, player, pos, state, blockEntity, tool, active ? 2 : 1);
        }
    }

    private void handleWoodcuttingBreak(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        int level = awardSkillXp(player, SkillType.WOODCUTTING, SkillTables.woodcuttingSkillXp(state.getBlock()));
        boolean harvestProc = level >= 1 && chancePercent(mcmmoDoubleDropChance(level));

        if (harvestProc) {
            dropBonusCopies(world, player, pos, state, blockEntity, tool, 1);
        }

        if (activeAbilities.isActive(player, AbilityType.TREE_FELLER)) {
            fellConnectedTree(world, player, pos);
        }
    }

    private void handleExcavationBreak(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        int baseXp = SkillTables.excavationSkillXp(state.getBlock());
        int level = awardSkillXp(player, SkillType.EXCAVATION, baseXp);
        int rollCount = activeAbilities.isActive(player, AbilityType.GIGA_DRILL_BREAKER) ? 3 : 1;

        for (int i = 1; i < rollCount; i++) {
            awardSkillXp(player, SkillType.EXCAVATION, baseXp);
        }

        rollExcavationTreasures(world, player, pos, state.getBlock(), level, rollCount);
    }

    private void handleHerbalismBreak(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        int level = awardSkillXp(player, SkillType.HERBALISM, SkillTables.herbalismSkillXp(state.getBlock()));
        boolean autoReplant = canAutoReplant(state) && chance(level, 100, SkillTables.herbalismGreenThumbChance(level));
        boolean greenTerra = activeAbilities.isActive(player, AbilityType.GREEN_TERRA);

        if (greenTerra) {
            dropBonusCopies(world, player, pos, state, blockEntity, tool, 1);
        }

        if (autoReplant && (greenTerra || consumeReplantItem(player, state))) {
            BlockState replantedState = SkillTables.greenThumbReplantedState(state, level, world.random);
            if (replantedState != null) {
                markHerbalismReplant(world, pos);
                world.setBlock(pos, replantedState, 3);
            }
        }
    }

    private void rollExcavationTreasures(ServerLevel world, ServerPlayer player, BlockPos pos, Block block, int level, int rollCount) {
        int archaeologyRank = SkillTables.archaeologyRank(level);

        for (int roll = 0; roll < rollCount; roll++) {
            for (SkillTables.ExcavationTreasure treasure : SkillTables.EXCAVATION_TREASURES) {
                if (!treasure.canDropFrom(block, level)) {
                    continue;
                }

                if (world.random.nextDouble() * 100.0 >= treasure.chancePercent()) {
                    continue;
                }

                Block.popResource(world, pos, new ItemStack(treasure.item(), treasure.amount()));
                awardSkillXp(player, SkillType.EXCAVATION, treasure.skillXp());

                if (archaeologyRank > 0 && world.random.nextDouble() * 100.0 < archaeologyRank * 2.0) {
                    ExperienceOrb.award(world, pos.getCenter(), archaeologyRank);
                }
            }
        }
    }

    private void fellConnectedTree(ServerLevel world, ServerPlayer player, BlockPos origin) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(origin);
        visited.add(origin);
        int processed = 0;

        while (!queue.isEmpty() && processed < TREE_FELLER_THRESHOLD) {
            BlockPos current = queue.removeFirst();

            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (!visited.add(next)) {
                    continue;
                }

                BlockState nextState = world.getBlockState(next);
                if (SkillTables.woodcuttingSkillXp(nextState.getBlock()) <= 0) {
                    continue;
                }

                queue.addLast(next);
            }

            if (current.equals(origin)) {
                continue;
            }

            BlockState targetState = world.getBlockState(current);
            if (SkillTables.woodcuttingSkillXp(targetState.getBlock()) <= 0) {
                continue;
            }

            ignoreNextBreak(world, current);
            world.destroyBlock(current, true, player);
            int baseXp = SkillTables.woodcuttingSkillXp(targetState.getBlock());
            int reducedXp = Math.max(1, baseXp - processed * 5);
            int level = awardSkillXp(player, SkillType.WOODCUTTING, reducedXp);

            if (level >= 1 && chancePercent(mcmmoDoubleDropChance(level))) {
                dropBonusCopies(world, player, current, targetState, world.getBlockEntity(current), player.getMainHandItem(), 1);
            }

            damageTool(player, player.getMainHandItem(), 1);
            processed++;
        }
    }

    public boolean allowLivingDamage(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        if (!source.is(DamageTypes.FALL)) {
            return true;
        }

        int level = profile(player).getProgress(SkillType.ACROBATICS).level();
        if (!chance(level, 100, SkillTables.acrobaticsRollChance(level))) {
            return true;
        }

        awardSkillXp(player, SkillType.ACROBATICS, Math.max(40.0, amount * 100.0));
        player.sendSystemMessage(Component.literal("Acrobatics roll negated the fall."));
        return false;
    }

    public void onLivingAfterDamage(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken, boolean blocked) {
        if (entity instanceof ServerPlayer player && source.is(DamageTypes.FALL) && damageTaken > 0.0F) {
            awardSkillXp(player, SkillType.ACROBATICS, SkillTables.acrobaticsSkillXp(damageTaken));
            return;
        }

        if (!(entity instanceof LivingEntity target)) {
            return;
        }

        Entity directEntity = source.getEntity();
        if (!(directEntity instanceof ServerPlayer player)) {
            if (source.getDirectEntity() instanceof AbstractArrow && source.getEntity() instanceof ServerPlayer shooter) {
                handleArcheryHit(shooter, target, damageTaken);
            }
            return;
        }

        if (blocked || damageTaken <= 0.0F) {
            return;
        }

        ItemStack held = player.getMainHandItem();
        if (isSword(held)) {
            handleSwordHit(player, target, damageTaken, false);
            return;
        }
        if (isAxe(held)) {
            handleAxesHit(player, target, damageTaken, false);
            return;
        }
        if (held.isEmpty()) {
            handleUnarmedHit(player, target, damageTaken, false);
        }
    }

    public void onKilledOtherEntity(ServerLevel world, Entity entity, LivingEntity killedEntity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        ItemStack held = player.getMainHandItem();
        if (isSword(held)) {
            handleSwordHit(player, killedEntity, Math.max(1.0F, killedEntity.getMaxHealth() * 0.25F), true);
            return;
        }
        if (isAxe(held)) {
            handleAxesHit(player, killedEntity, Math.max(1.0F, killedEntity.getMaxHealth() * 0.25F), true);
            return;
        }
        if (held.isEmpty()) {
            handleUnarmedHit(player, killedEntity, Math.max(1.0F, killedEntity.getMaxHealth() * 0.25F), true);
        }
    }

    private void tryActivateAbility(ServerPlayer player) {
        ItemStack tool = player.getMainHandItem();
        PlayerSkillProfile profile = profile(player);

        if (isPickaxe(tool) && profile.getProgress(SkillType.MINING).level() >= AbilityType.SUPER_BREAKER.unlockLevel()) {
            activeAbilities.activate(player, AbilityType.SUPER_BREAKER, profile.getProgress(SkillType.MINING).level());
            return;
        }

        if (isAxe(tool) && profile.getProgress(SkillType.WOODCUTTING).level() >= AbilityType.TREE_FELLER.unlockLevel()) {
            activeAbilities.activate(player, AbilityType.TREE_FELLER, profile.getProgress(SkillType.WOODCUTTING).level());
            return;
        }

        if (isShovel(tool) && profile.getProgress(SkillType.EXCAVATION).level() >= AbilityType.GIGA_DRILL_BREAKER.unlockLevel()) {
            activeAbilities.activate(player, AbilityType.GIGA_DRILL_BREAKER, profile.getProgress(SkillType.EXCAVATION).level());
            return;
        }

        if (isSword(tool) && profile.getProgress(SkillType.SWORDS).level() >= AbilityType.SERRATED_STRIKES.unlockLevel()) {
            activeAbilities.activate(player, AbilityType.SERRATED_STRIKES, profile.getProgress(SkillType.SWORDS).level());
            return;
        }

        if (isAxe(tool) && profile.getProgress(SkillType.AXES).level() >= AbilityType.SKULL_SPLITTER.unlockLevel()) {
            activeAbilities.activate(player, AbilityType.SKULL_SPLITTER, profile.getProgress(SkillType.AXES).level());
            return;
        }

        if (tool.isEmpty() && profile.getProgress(SkillType.UNARMED).level() >= AbilityType.BERSERK.unlockLevel()) {
            activeAbilities.activate(player, AbilityType.BERSERK, profile.getProgress(SkillType.UNARMED).level());
            return;
        }

        if (isHoe(tool) && profile.getProgress(SkillType.HERBALISM).level() >= AbilityType.GREEN_TERRA.unlockLevel()) {
            activeAbilities.activate(player, AbilityType.GREEN_TERRA, profile.getProgress(SkillType.HERBALISM).level());
        }
    }

    public int awardSkillXp(ServerPlayer player, SkillType skillType, double xp) {
        PlayerSkillProfile profile = profile(player);
        SkillProgress progress = profile.getProgress(skillType);
        boolean alreadyMaxLevel = progress.level() >= SkillProgress.MAX_LEVEL;
        int levelsGained = progress.addXp(xp);
        save(player, profile);

        if (alreadyMaxLevel && levelsGained == 0) {
            return progress.level();
        }

        sendXpProgress(player, skillType, xp, progress);

        if (levelsGained > 0) {
            playLevelUpSound(player, levelsGained);
            showLevelUpTitle(player, skillType, progress.level());
            player.sendSystemMessage(Component.literal(
                skillType.displayName() + " reached level " + progress.level()
            ));
        }

        return progress.level();
    }

    public PlayerSkillProfile getProfile(ServerPlayer player) {
        return profile(player);
    }

    public PlayerSkillProfile getProfile(MinecraftServer server, UUID playerId) {
        return SkillPersistentState.get(server).getOrCreate(playerId);
    }

    public ActiveAbilityTracker getActiveAbilities() {
        return activeAbilities;
    }

    private void sendXpProgress(ServerPlayer player, SkillType skillType, double awardedXp, SkillProgress progress) {
        int nextLevelXp = SkillProgress.xpRequiredForNextLevel(progress.level());
        String message = String.format(
            "%s +%.0f XP | %s",
            skillType.displayName(),
            awardedXp,
            nextLevelXp <= 0
                ? "Lv." + progress.level() + " | MAX"
                : String.format("Lv.%d | %.0f/%d", progress.level(), progress.xpIntoLevel(), nextLevelXp)
        );
        player.displayClientMessage(Component.literal(message), true);
    }

    private void playLevelUpSound(ServerPlayer player, int levelsGained) {
        float pitch = Math.min(1.8F, 1.0F + (levelsGained - 1) * 0.08F);
        player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, pitch);
    }

    private void showLevelUpTitle(ServerPlayer player, SkillType skillType, int level) {
        Component title = Component.translatable("message.cobblemonmmoskills.level_up.title")
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        Component subtitle = Component.translatable(
            "message.cobblemonmmoskills.level_up.subtitle",
            Component.translatable(skillType.translationKey()),
            level
        ).withStyle(ChatFormatting.YELLOW);

        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 50, 15));
        player.connection.send(new ClientboundSetTitleTextPacket(title));
        player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
    }

    private void dropBonusCopies(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, int extraCopies) {
        List<ItemStack> drops = Block.getDrops(state, world, pos, blockEntity, player, tool);
        for (int i = 0; i < extraCopies; i++) {
            for (ItemStack drop : drops) {
                Block.popResource(world, pos, drop.copy());
            }
        }
    }

    private void damageTool(ServerPlayer player, ItemStack tool, int amount) {
        if (!tool.isDamageableItem()) {
            return;
        }
        tool.hurtAndBreak(amount, player, EquipmentSlot.MAINHAND);
    }

    private boolean chance(int level, int maxBonusLevel, double chanceMax) {
        double currentChance = Math.min(chanceMax, (level / (double) maxBonusLevel) * chanceMax);
        return chancePercent(currentChance);
    }

    private boolean chancePercent(double percent) {
        return Math.random() * 100.0 < Math.min(100.0, Math.max(0.0, percent));
    }

    private double mcmmoDoubleDropChance(int level) {
        return Math.min(100.0, level * 0.1);
    }

    private boolean isPickaxe(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES);
    }

    private boolean isAxe(ItemStack stack) {
        return stack.is(ItemTags.AXES);
    }

    private boolean isShovel(ItemStack stack) {
        return stack.is(ItemTags.SHOVELS);
    }

    private boolean isSword(ItemStack stack) {
        return stack.is(ItemTags.SWORDS);
    }

    private boolean isHoe(ItemStack stack) {
        return stack.is(ItemTags.HOES);
    }

    private PlayerSkillProfile profile(ServerPlayer player) {
        return SkillPersistentState.get(player.getServer()).getOrCreate(player.getUUID());
    }

    private void save(ServerPlayer player, PlayerSkillProfile profile) {
        SkillPersistentState persistentState = SkillPersistentState.get(player.getServer());
        persistentState.put(player.getUUID(), profile);
    }

    private void ignoreNextBreak(ServerLevel world, BlockPos pos) {
        ignoredBreaks.add(key(world, pos));
    }

    private boolean consumeIgnoredBreak(ServerLevel world, BlockPos pos) {
        return ignoredBreaks.remove(key(world, pos));
    }

    private String key(ServerLevel world, BlockPos pos) {
        return world.dimension().location() + ":" + pos.asLong();
    }

    private void handleSwordHit(ServerPlayer player, LivingEntity target, float damageTaken, boolean kill) {
        int level = awardSkillXp(player, SkillType.SWORDS, SkillTables.swordsSkillXp(damageTaken, kill));
        boolean active = activeAbilities.isActive(player, AbilityType.SERRATED_STRIKES);
        double bleedChance = active ? 100.0 : SkillTables.swordsBleedChance(level);

        if (chance(level, 100, bleedChance)) {
            int duration = SkillTables.swordsBleedSeconds(level) * 20;
            int amplifier = active ? 1 : 0;
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, amplifier, true, true, true));
        }
    }

    private void handleAxesHit(ServerPlayer player, LivingEntity target, float damageTaken, boolean kill) {
        int level = awardSkillXp(player, SkillType.AXES, SkillTables.axesSkillXp(damageTaken, kill));
        boolean active = activeAbilities.isActive(player, AbilityType.SKULL_SPLITTER);
        double impactChance = active ? 100.0 : SkillTables.axesArmorImpactChance(level);

        if (chance(level, 100, impactChance)) {
            target.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS,
                SkillTables.axesArmorImpactSeconds(level) * 20,
                active ? 1 : 0,
                true,
                true,
                true
            ));
        }
    }

    private void handleUnarmedHit(ServerPlayer player, LivingEntity target, float damageTaken, boolean kill) {
        int level = awardSkillXp(player, SkillType.UNARMED, SkillTables.unarmedSkillXp(damageTaken, kill));
        boolean active = activeAbilities.isActive(player, AbilityType.BERSERK);
        double ironArmChance = active ? 100.0 : SkillTables.unarmedIronArmChance(level);

        if (chance(level, 100, ironArmChance)) {
            target.knockback(active ? 1.4 : 0.9, player.getX() - target.getX(), player.getZ() - target.getZ());
        }
    }

    private void handleArcheryHit(ServerPlayer player, LivingEntity target, float damageTaken) {
        double distance = player.distanceTo(target);
        int level = awardSkillXp(player, SkillType.ARCHERY, SkillTables.archerySkillXp(damageTaken, distance));

        if (chance(level, 100, SkillTables.archeryDazeChance(level))) {
            target.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                SkillTables.archeryDazeSeconds(level) * 20,
                0,
                true,
                true,
                true
            ));
        }
    }

    private void syncFishingProgress(ServerPlayer player) {
        int currentFishCaught = player.getStats().getValue(Stats.CUSTOM.get(Stats.FISH_CAUGHT));
        Integer previous = lastFishCaughtStat.put(player.getUUID(), currentFishCaught);
        if (previous == null || currentFishCaught <= previous) {
            return;
        }

        int delta = currentFishCaught - previous;
        int level = awardSkillXp(player, SkillType.FISHING, SkillTables.fishingSkillXp(delta));

        if (chance(level, 100, SkillTables.fishingTreasureHunterChance(level))) {
            SkillTables.FishingTreasure treasure = randomFishingTreasure(player.serverLevel());
            if (treasure != null) {
                Block.popResource(player.serverLevel(), player.blockPosition(), new ItemStack(treasure.item(), treasure.amount()));
                player.sendSystemMessage(Component.literal("Fishing found bonus loot: " + treasure.item().getDescription().getString()));
            }
        }
    }

    private SkillTables.FishingTreasure randomFishingTreasure(ServerLevel world) {
        double totalWeight = 0.0;
        for (SkillTables.FishingTreasure treasure : SkillTables.FISHING_TREASURES) {
            totalWeight += treasure.chanceWeight();
        }

        double roll = world.random.nextDouble() * totalWeight;
        double cursor = 0.0;

        for (SkillTables.FishingTreasure treasure : SkillTables.FISHING_TREASURES) {
            cursor += treasure.chanceWeight();
            if (roll <= cursor) {
                return treasure;
            }
        }

        return SkillTables.FISHING_TREASURES.isEmpty() ? null : SkillTables.FISHING_TREASURES.getFirst();
    }

    private boolean canAutoReplant(BlockState state) {
        return SkillTables.replantedState(state) != null;
    }

    private void markHerbalismReplant(ServerLevel world, BlockPos pos) {
        herbalismReplants.add(key(world, pos));
    }

    private void cleanupHerbalismReplant(ServerLevel world, BlockPos pos) {
        herbalismReplants.remove(key(world, pos));
    }

    private boolean consumeReplantItem(ServerPlayer player, BlockState harvestedState) {
        if (player.getAbilities().instabuild) {
            return true;
        }

        ItemStack required = replantItem(harvestedState);
        if (required.isEmpty()) {
            return false;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!ItemStack.isSameItem(stack, required)) {
                continue;
            }

            stack.shrink(1);
            return true;
        }

        return false;
    }

    private ItemStack replantItem(BlockState harvestedState) {
        Block block = harvestedState.getBlock();
        if (block == Blocks.WHEAT) {
            return new ItemStack(Items.WHEAT_SEEDS);
        }
        if (block == Blocks.CARROTS) {
            return new ItemStack(Items.CARROT);
        }
        if (block == Blocks.POTATOES) {
            return new ItemStack(Items.POTATO);
        }
        if (block == Blocks.BEETROOTS) {
            return new ItemStack(Items.BEETROOT_SEEDS);
        }
        if (block == Blocks.NETHER_WART) {
            return new ItemStack(Items.NETHER_WART);
        }
        if (block == Blocks.COCOA) {
            return new ItemStack(Items.COCOA_BEANS);
        }
        if (block == Blocks.SWEET_BERRY_BUSH) {
            return new ItemStack(Items.SWEET_BERRIES);
        }
        return ItemStack.EMPTY;
    }

    private boolean isRepairAction(ItemStack result, ItemStack leftInput, ItemStack rightInput) {
        if (result.isEmpty() || leftInput.isEmpty() || rightInput.isEmpty() || !result.isDamageableItem()) {
            return false;
        }

        if (leftInput.getDamageValue() > result.getDamageValue()) {
            return true;
        }

        return leftInput.getItem().isValidRepairItem(leftInput, rightInput) || leftInput.is(rightInput.getItem());
    }

    private void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
