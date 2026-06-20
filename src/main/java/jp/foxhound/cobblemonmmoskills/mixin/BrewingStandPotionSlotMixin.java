package jp.foxhound.cobblemonmmoskills.mixin;

import jp.foxhound.cobblemonmmoskills.skill.SkillManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandMenu.PotionSlot.class)
public class BrewingStandPotionSlotMixin {
    @Inject(method = "onTake", at = @At("TAIL"))
    private void cobblemonmmoskills$onTakePotion(Player player, ItemStack stack, CallbackInfo ci) {
        SkillManager.getInstance().onAlchemyPotionTaken(player, stack);
    }
}
