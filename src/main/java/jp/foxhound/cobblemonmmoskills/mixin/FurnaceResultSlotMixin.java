package jp.foxhound.cobblemonmmoskills.mixin;

import jp.foxhound.cobblemonmmoskills.skill.SkillManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceResultSlot.class)
public class FurnaceResultSlotMixin {
    @Inject(method = "onTake", at = @At("TAIL"))
    private void cobblemonmmoskills$onTakeSmelted(Player player, ItemStack stack, CallbackInfo ci) {
        SkillManager.getInstance().onSmeltingResultTaken(player, stack);
    }
}
