package luowei.fengxskillsandinter.mixin;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import luowei.fengxskillsandinter.block.AnvilForgeHandler;

/**
 * 铁砧方块Mixin
 * 拦截铁砧的右键交互，实现无UI锻造
 */
@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {
    
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onAnvilUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                           BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        // 如果玩家是潜行状态，保留原版的修复UI功能
        if (player.isSneaking()) {
            return; // 允许原版UI
        }

        // 尝试使用自定义的锻造系统
        boolean handled = AnvilForgeHandler.handleAnvilInteraction(world, pos, player, Hand.MAIN_HAND, hit);

        if (handled) {
            // 自定义逻辑成功处理，返回成功
            cir.setReturnValue(ActionResult.SUCCESS);
        } else {
            // 自定义逻辑无法处理，取消原版UI，显示无操作
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}

