package luowei.fengxskillsandinter.block;

import luowei.fengxskillsandinter.screen.RunicTableScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RunicTable extends Block {

    public RunicTable(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return openTable(world, pos, player);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return openTable(world, pos, player);
    }

    private static ActionResult openTable(World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
            (syncId, inventory, p) -> new RunicTableScreenHandler(syncId, inventory),
            Text.literal("符文台")
        ));
        return ActionResult.CONSUME;
    }
}
