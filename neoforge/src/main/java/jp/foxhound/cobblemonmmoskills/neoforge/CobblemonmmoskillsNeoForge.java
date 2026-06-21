package jp.foxhound.cobblemonmmoskills.neoforge;

import jp.foxhound.cobblemonmmoskills.Cobblemonmmoskills;
import jp.foxhound.cobblemonmmoskills.command.SkillCommand;
import jp.foxhound.cobblemonmmoskills.gui.SkillGuiBridge;
import jp.foxhound.cobblemonmmoskills.neoforge.gui.NeoForgeSkillGuiFactory;
import jp.foxhound.cobblemonmmoskills.skill.SkillManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.fml.common.Mod;

@Mod(Cobblemonmmoskills.MOD_ID)
public final class CobblemonmmoskillsNeoForge {
    public CobblemonmmoskillsNeoForge(IEventBus modBus) {
        SkillGuiBridge.register(NeoForgeSkillGuiFactory::openMain, NeoForgeSkillGuiFactory::openLeaderboard);

        modBus.addListener(this::onCommonSetup);

        NeoForge.EVENT_BUS.addListener(this::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(this::onAttackEntity);
        NeoForge.EVENT_BUS.addListener(this::onLeftClickBlock);
        NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(this::onRightClickItem);
        NeoForge.EVENT_BUS.addListener(this::onLivingIncomingDamage);
        NeoForge.EVENT_BUS.addListener(this::onLivingDamagePost);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(this::onServerTickPost);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        Cobblemonmmoskills.initializeCommon();
    }

    private void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof Level level)) {
            return;
        }

        SkillManager.getInstance().onBlockBroken(
            level,
            event.getPlayer(),
            event.getPos(),
            event.getState(),
            event.getLevel().getBlockEntity(event.getPos())
        );
    }

    private void onAttackEntity(AttackEntityEvent event) {
        SkillManager.getInstance().onAttackEntity(
            event.getEntity(),
            event.getEntity().level(),
            InteractionHand.MAIN_HAND,
            event.getTarget(),
            null
        );
    }

    private void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        SkillManager.getInstance().onAttackBlock(
            event.getEntity(),
            event.getLevel(),
            event.getHand(),
            event.getPos(),
            event.getFace()
        );
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        SkillManager.getInstance().onUseBlock(
            event.getEntity(),
            event.getLevel(),
            event.getHand(),
            event.getHitVec()
        );
    }

    private void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        SkillManager.getInstance().onUseItem(
            event.getEntity(),
            event.getLevel(),
            event.getHand()
        );
    }

    private void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!SkillManager.getInstance().allowLivingDamage(
            event.getEntity(),
            event.getSource(),
            event.getAmount()
        )) {
            event.setCanceled(true);
        }
    }

    private void onLivingDamagePost(LivingDamageEvent.Post event) {
        SkillManager.getInstance().onLivingAfterDamage(
            event.getEntity(),
            event.getSource(),
            event.getOriginalDamage(),
            event.getNewDamage(),
            event.getBlockedDamage() > 0.0F
        );
    }

    private void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        LivingEntity killed = event.getEntity();
        if (killer == null || !(killed.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        SkillManager.getInstance().onKilledOtherEntity(serverLevel, killer, killed);
    }

    private void onServerTickPost(ServerTickEvent.Post event) {
        SkillManager.getInstance().tick(event.getServer());
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        SkillCommand.register(event.getDispatcher());
    }
}
