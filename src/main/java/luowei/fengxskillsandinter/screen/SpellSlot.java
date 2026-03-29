package luowei.fengxskillsandinter.screen;

import luowei.fengxskillsandinter.item.SpellItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SpellSlot extends Slot{
    public SpellSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof SpellItem;
    }
}
