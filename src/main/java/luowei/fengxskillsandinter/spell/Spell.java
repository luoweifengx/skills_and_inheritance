package luowei.fengxskillsandinter.spell;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
//创造法术要去法术注册处，物品注册处进行注册，并且注册时需要绑定物品
public interface Spell {
    /** 
     * @param caster 施法者
     * @param world 世界
     * @param wandStack 法杖
     * @param context 上下文
     * @return 是否消耗遍历点数
     */
    //static final double TICK = 0.05;
    double getCastingDelay(ItemStack wandStack);
    double getRechargeDelay(ItemStack wandStack);
    double getManaCost(ItemStack wandStack);
    int getDrawCost(ItemStack wandStack);
    void cast(Entity caster, World world);

    /**
     * 带施法上下文的施放（效果类法术可在此修改 {@link SpellCastContext}，影响后续同次施法中的投射物等）。
     * 默认委托给 {@link #cast(Entity, World)}。
     */
    default void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        cast(caster, world);
    }

    /**
     * @param triggerChildren 触发法术的子树（由 {@link SpellCaster} 在排序阶段构建）
     */
    default void cast(Entity caster, World world, List<SpellNode> triggerChildren, SpellCastContext context, List<Spell> effectSpellList) {
        cast(caster, world, context, effectSpellList);
    }
    boolean isTrigger();//触发类
    boolean isEffect();//效果类
    //, SpellCastContext context
}
