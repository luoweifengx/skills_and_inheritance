package luowei.fengxskillsandinter.spell.spells;

import java.util.List;

import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.entity.RegenerationFieldEntity;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 再生领域：在施法者位置生成持续 20s 的领域实体（边界粒子 + 周期性治疗）。
 */
public class RegenerationField implements Spell {

    private static final double CASTING_DELAY = 0.25;
    private static final double RECHARGE_DELAY = 0;
    private static final double MANA_COST = 80.0;
    private static final int DRAW_COST = 1;

    @Override
    public void cast(Entity caster, World world) {
        cast(caster, world, null, List.of());
    }

    @Override
    public void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        if (world.isClient || caster == null) {
            return;
        }
        RegenerationFieldEntity field = new RegenerationFieldEntity(ModEntities.REGENERATION_FIELD_ENTITY, world);
        field.setPosition(caster.getX(), caster.getY(), caster.getZ());
        world.spawnEntity(field);
    }

    @Override
    public double getCastingDelay(ItemStack stack) {
        return CASTING_DELAY;
    }

    @Override
    public double getRechargeDelay(ItemStack stack) {
        return RECHARGE_DELAY;
    }

    @Override
    public int getDrawCost(ItemStack stack) {
        return DRAW_COST;
    }

    @Override
    public double getManaCost(ItemStack stack) {
        return MANA_COST;
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
