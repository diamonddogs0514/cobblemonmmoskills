package jp.foxhound.cobblemonmmoskills.mixin;

import jp.foxhound.cobblemonmmoskills.skill.SkillManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @Shadow
    private DataSlot cost;

    @Unique
    private ItemStack cobblemonmmoskills$leftInput = ItemStack.EMPTY;

    @Unique
    private ItemStack cobblemonmmoskills$rightInput = ItemStack.EMPTY;

    @Unique
    private int cobblemonmmoskills$repairCost;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void cobblemonmmoskills$captureRepairContext(Player player, ItemStack stack, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        this.cobblemonmmoskills$leftInput = menu.getSlot(0).getItem().copy();
        this.cobblemonmmoskills$rightInput = menu.getSlot(1).getItem().copy();
        this.cobblemonmmoskills$repairCost = this.cost.get();
    }

    @Inject(method = "onTake", at = @At("TAIL"))
    private void cobblemonmmoskills$awardRepairSkill(Player player, ItemStack stack, CallbackInfo ci) {
        SkillManager.getInstance().onRepairResultTaken(
            player,
            stack,
            this.cobblemonmmoskills$leftInput,
            this.cobblemonmmoskills$rightInput,
            this.cobblemonmmoskills$repairCost
        );
        this.cobblemonmmoskills$leftInput = ItemStack.EMPTY;
        this.cobblemonmmoskills$rightInput = ItemStack.EMPTY;
        this.cobblemonmmoskills$repairCost = 0;
    }
}
