package luowei.fengxskillsandinter.spell.spells;

import java.util.List;

import luowei.fengxskillsandinter.entity.BubbleShotEntity;
import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.entity.SpellEntity;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.util.SpellCastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** 泡泡弹：使用 {@link BubbleShotEntity} 与专用渲染、贴图。 */
public class BubbleShot implements Spell {

    private static final double CASTING_DELAY = -0.05; 
    private static final double RECHARGE_DELAY = 0; 
    private static final double MANA_COST = 5; 
    private static final int DRAW_COST = 1; 
    private static final double START_SPEED = 0.5; 
    public static final double GRAVITY = 0;
    public static final double DAMAGE = 3.0;

    @Override
    public void cast(Entity caster, World world) {
        cast(caster, world, null, List.of());
    }

    @Override
    public void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        if (!world.isClient && world instanceof ServerWorld) {
            Vec3d lookVec = SpellEntity.computeSpawnDirection(caster, world, context);
            Vec3d spawnPos = SpellEntity.computeSpawnPosition(caster, lookVec);
            BubbleShotEntity projectile = new BubbleShotEntity(ModEntities.BUBBLE_SHOT, world);
            projectile.getAndSolveEffect(effectSpellList);
            lookVec = projectile.applyHeavySpreadToDirection(lookVec);
            projectile.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setVelocity(lookVec.multiply(START_SPEED));
            projectile.setOwner(SpellCastUtil.resolveOwnerForProjectile(caster));
            projectile.configureFromSpell((float) DAMAGE, GRAVITY);
            world.spawnEntity(projectile);
        }
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
        return false;
    }
}
