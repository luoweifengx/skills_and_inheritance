package luowei.fengxskillsandinter.spell.spells;

import luowei.fengxskillsandinter.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HeavySpread implements Spell {
    @Override
    public void cast(Entity caster, World world) {
        
    }
    @Override
    public double getCastingDelay(ItemStack stack) {
        return -0.1;
    }
    @Override
    public double getRechargeDelay(ItemStack stack) {
        return -0.25;
    }
    @Override
    public int getDrawCost(ItemStack stack) {
        return 0;
    }
    @Override
    public double getManaCost(ItemStack stack) {
        return 2;
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
