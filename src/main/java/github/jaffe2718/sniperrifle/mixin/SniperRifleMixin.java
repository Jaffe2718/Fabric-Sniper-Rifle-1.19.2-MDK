package github.jaffe2718.sniperrifle.mixin;


import github.jaffe2718.sniperrifle.item.SniperRifleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntity.class)
public abstract class SniperRifleMixin {

    @Shadow
    public abstract Iterable<ItemStack> getHandItems();

    @Inject(at = @At("RETURN"), method = "isUsingSpyglass", cancellable = true)
    private void isUsingGlass(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || SniperRifleItem.isWatching(getHandItems().iterator().next()));
    }

}
