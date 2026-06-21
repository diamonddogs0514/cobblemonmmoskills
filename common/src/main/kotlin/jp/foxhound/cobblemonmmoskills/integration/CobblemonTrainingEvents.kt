package jp.foxhound.cobblemonmmoskills.integration

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent
import com.cobblemon.mod.common.api.pokemon.experience.BattleExperienceSource
import com.cobblemon.mod.common.util.getPlayer
import jp.foxhound.cobblemonmmoskills.skill.SkillManager
import jp.foxhound.cobblemonmmoskills.skill.SkillTables
import jp.foxhound.cobblemonmmoskills.skill.SkillType

object CobblemonTrainingEvents {
    private var registered = false

    @JvmStatic
    fun register() {
        if (registered) {
            return
        }
        registered = true

        CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.LOWEST, ::onExperiencePre)
        CobblemonEvents.EXPERIENCE_GAINED_EVENT_POST.subscribe(Priority.LOWEST, ::onExperiencePost)
        CobblemonEvents.LEVEL_UP_EVENT.subscribe(Priority.LOWEST, ::onLevelUp)
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOWEST, ::onBattleVictory)
    }

    private fun onExperiencePre(event: ExperienceGainedEvent.Pre) {
        if (event.source !is BattleExperienceSource) {
            return
        }

        val owner = event.pokemon.getOwnerPlayer() ?: return
        val trainingLevel = SkillManager.getInstance().getProfile(owner).getProgress(SkillType.TRAINING).level()
        val bonus = SkillTables.trainingBattleExperienceBonusAmount(trainingLevel, event.experience)
        if (bonus > 0) {
            event.experience += bonus
        }
    }

    private fun onExperiencePost(event: ExperienceGainedEvent.Post) {
        if (event.source !is BattleExperienceSource) {
            return
        }

        val owner = event.pokemon.getOwnerPlayer() ?: return
        SkillManager.getInstance().awardSkillXp(
            owner,
            SkillType.TRAINING,
            SkillTables.trainingSkillXpFromBattleExperience(event.experience)
        )
    }

    private fun onLevelUp(event: LevelUpEvent) {
        val owner = event.pokemon.getOwnerPlayer() ?: return
        val skillXp = SkillTables.trainingSkillXpFromLevelGain(event.oldLevel, event.newLevel)
        if (skillXp > 0.0) {
            SkillManager.getInstance().awardSkillXp(owner, SkillType.TRAINING, skillXp)
        }
    }

    private fun onBattleVictory(event: BattleVictoryEvent) {
        event.winners
            .flatMap { it.getPlayerUUIDs() }
            .distinct()
            .forEach { playerId ->
                val player = playerId.getPlayer() ?: return@forEach
                SkillManager.getInstance().awardSkillXp(player, SkillType.TRAINING, 300.0)
            }
    }
}
