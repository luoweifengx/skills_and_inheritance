package luowei.fengxskillsandinter.spell;

import java.util.List;

import luowei.fengxskillsandinter.item.WandItem;
import luowei.fengxskillsandinter.spell.modifier.ProjectileTrajectoryModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * 单次施法的上下文，封装所有可变状态，避免静态共享。
 */
public class SpellCastContext {

    public int spellCachePointer;
    public int drawCount;
    public double castingDelay;
    public double currentRechargeDelay;
    public double currentMana;
    public List<String> spells;
    /** 最后消费的 spellList 下标，用于 spellListIndices */
    public int lastConsumedSpellListIndex;

    public ProjectileTrajectoryModifiers projectileTrajectory = new ProjectileTrajectoryModifiers();

    /**
     * 本次施法树认定的“施法者/追踪参照”（通常为玩家），与 {@code caster} 可能不同（例如 {@code caster} 为投射物）。
     */
    private Entity spellOwner;

    public SpellCastContext(ItemStack wandStack) {
        this.spellCachePointer = WandItem.getSpellCachePointer(wandStack);
        this.drawCount = WandItem.getDrawCount(wandStack);
        this.castingDelay = WandItem.getCastingDelay(wandStack);
        this.currentRechargeDelay = WandItem.getCurrentRechargeDelay(wandStack);
        this.currentMana = WandItem.getCurrentMana(wandStack);
        this.spells = WandItem.getSpells(wandStack);
        if (spellCachePointer == 0) {
            this.currentRechargeDelay = WandItem.getRechargeDelay(wandStack);
        } else {
            this.currentRechargeDelay = WandItem.getCurrentRechargeDelay(wandStack);
        }
    }
    public void resetModifiers() {
        this.projectileTrajectory.resetProjectileTrajectoryModifiers();
    }

    public Entity getSpellOwner() {
        return spellOwner;
    }

    /** 仅在非空时写入，避免用 null 覆盖已记录的施法者。 */
    public void setSpellOwner(Entity entity) {
        if (entity != null) {
            this.spellOwner = entity;
        }
    }
}
