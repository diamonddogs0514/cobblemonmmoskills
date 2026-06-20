package jp.foxhound.cobblemonmmoskills;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import jp.foxhound.cobblemonmmoskills.integration.CobblemonBreedingEvents;
import jp.foxhound.cobblemonmmoskills.command.SkillCommand;
import jp.foxhound.cobblemonmmoskills.integration.CobblemonCaptureEvents;
import jp.foxhound.cobblemonmmoskills.integration.CobblemonTrainingEvents;
import jp.foxhound.cobblemonmmoskills.skill.SkillManager;

public class Cobblemonmmoskills implements ModInitializer {
    public static final String MOD_ID = "cobblemonmmoskills";

    @Override
    public void onInitialize() {
        SkillManager skillManager = SkillManager.getInstance();

        PlayerBlockBreakEvents.AFTER.register(skillManager::onBlockBroken);
        AttackBlockCallback.EVENT.register(skillManager::onAttackBlock);
        AttackEntityCallback.EVENT.register(skillManager::onAttackEntity);
        UseBlockCallback.EVENT.register(skillManager::onUseBlock);
        UseItemCallback.EVENT.register(skillManager::onUseItem);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(skillManager::allowLivingDamage);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(skillManager::onLivingAfterDamage);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(skillManager::onKilledOtherEntity);
        ServerTickEvents.END_SERVER_TICK.register(skillManager::tick);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            SkillCommand.register(dispatcher));
        CobblemonTrainingEvents.register();
        CobblemonCaptureEvents.register();
        CobblemonBreedingEvents.register();
    }
}
