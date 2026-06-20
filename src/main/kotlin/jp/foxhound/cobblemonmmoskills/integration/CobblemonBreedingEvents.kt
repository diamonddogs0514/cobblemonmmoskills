package jp.foxhound.cobblemonmmoskills.integration

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.CollectEggEvent
import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent
import jp.foxhound.cobblemonmmoskills.skill.SkillManager
import jp.foxhound.cobblemonmmoskills.skill.SkillTables
import jp.foxhound.cobblemonmmoskills.skill.SkillType

object CobblemonBreedingEvents {
    private var registered = false

    @JvmStatic
    fun register() {
        if (registered) {
            return
        }
        registered = true

        CobblemonEvents.COLLECT_EGG.subscribe(Priority.LOWEST, ::onCollectEgg)
        CobblemonEvents.HATCH_EGG_PRE.subscribe(Priority.LOWEST, ::onHatchEggPre)
        CobblemonEvents.HATCH_EGG_POST.subscribe(Priority.LOWEST, ::onHatchEggPost)
    }

    private fun onCollectEgg(event: CollectEggEvent) {
        SkillManager.getInstance().awardSkillXp(
            event.player,
            SkillType.BREEDING,
            SkillTables.breedingSkillXpFromEggCollected()
        )
    }

    private fun onHatchEggPre(event: HatchEggEvent.Pre) {
        val breedingLevel = SkillManager.getInstance().getProfile(event.player).getProgress(SkillType.BREEDING).level()
        val currentFriendship = event.egg.friendship ?: 0
        event.egg.friendship = currentFriendship + SkillTables.breedingEggFriendshipBonus(breedingLevel)
    }

    private fun onHatchEggPost(event: HatchEggEvent.Post) {
        SkillManager.getInstance().awardSkillXp(
            event.player,
            SkillType.BREEDING,
            SkillTables.breedingSkillXpFromEggHatched(event.pokemon.species.eggCycles)
        )
    }
}
