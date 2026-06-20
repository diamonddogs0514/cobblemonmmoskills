package jp.foxhound.cobblemonmmoskills.integration

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import jp.foxhound.cobblemonmmoskills.skill.SkillManager
import jp.foxhound.cobblemonmmoskills.skill.SkillTables
import jp.foxhound.cobblemonmmoskills.skill.SkillType
import net.minecraft.server.level.ServerPlayer

object CobblemonCaptureEvents {
    private var registered = false

    @JvmStatic
    fun register() {
        if (registered) {
            return
        }
        registered = true

        CobblemonEvents.POKEMON_CATCH_RATE.subscribe(Priority.LOWEST, ::onCatchRate)
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, ::onCaptured)
    }

    private fun onCatchRate(event: PokemonCatchRateEvent) {
        val player = event.thrower as? ServerPlayer ?: return
        val captureLevel = SkillManager.getInstance().getProfile(player).getProgress(SkillType.CAPTURE).level()
        event.catchRate *= SkillTables.captureCatchRateBonusMultiplier(captureLevel)
    }

    private fun onCaptured(event: PokemonCapturedEvent) {
        SkillManager.getInstance().awardSkillXp(
            event.player,
            SkillType.CAPTURE,
            SkillTables.captureSkillXp(event.pokemon.level, false)
        )
    }
}
