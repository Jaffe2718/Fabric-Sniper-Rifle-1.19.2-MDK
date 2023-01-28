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
public abstract class PlayerEntityMixin {

    /**
     * 访问 PlayerEntity.getHandItems()
     */
    @Shadow
    public abstract Iterable<ItemStack> getHandItems();

    /**
     *  修改 PlayerEntity.isUsingSpyglass()
     *  使游戏判定开镜的玩家正在使用望远镜
     *  因此直接调用望远镜的开镜效果
     */
    @Inject(at = @At("RETURN"), method = "isUsingSpyglass", cancellable = true)
    private void isUsingGlass(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || SniperRifleItem.isWatching(getHandItems().iterator().next()));
    }

}
