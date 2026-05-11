package luowei.fengxskillsandinter.spell.spells;

import luowei.fengxskillsandinter.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 强力追踪：与普通 {@link Homing} 同类效果，但搜索锥更宽、距离更远、转向惯性更小（见 {@link luowei.fengxskillsandinter.entity.SpellEntity} 常量）。
 */
public class StrongHoming implements Spell {
    @Override
    public void cast(Entity caster, World world) {
    }

    @Override
    public double getCastingDelay(ItemStack stack) {
        return 0;
    }

    @Override
    public double getRechargeDelay(ItemStack stack) {
        return 0;
    }

    @Override
    public int getDrawCost(ItemStack stack) {
        return 0;
    }

    @Override
    public double getManaCost(ItemStack stack) {
        return 18;
    }

    @Override
    public boolean isTrigger() {
        return false;
    }

    @Override
    public boolean isEffect() {
        return true;
    }
}
