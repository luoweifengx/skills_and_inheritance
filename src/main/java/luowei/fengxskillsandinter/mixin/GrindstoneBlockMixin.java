package luowei.fengxskillsandinter.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.GrindstoneBlock;
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
import luowei.fengxskillsandinter.block.GrindstonePolishHandler;

/**
 * 砂轮方块Mixin
 * 拦截砂轮的右键交互，实现无UI打磨
 */
@Mixin(GrindstoneBlock.class)
public class GrindstoneBlockMixin {
    
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onGrindstoneUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        // 如果玩家是潜行状态，保留原版的去附魔UI功能
        if (player.isSneaking()) {
            return; // 允许原版UI
        }

        // 尝试使用自定义的打磨系统
        boolean handled = GrindstonePolishHandler.handleGrindstoneInteraction(world, pos, player, Hand.MAIN_HAND);

        if (handled) {
            // 自定义逻辑成功处理，返回成功
            cir.setReturnValue(ActionResult.SUCCESS);
        } else {
            // 自定义逻辑无法处理，取消原版UI，显示无操作
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}

