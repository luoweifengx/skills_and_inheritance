package luowei.fengxskillsandinter.spell.spells;

import java.util.List;

import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.entity.SparkProjectileEntity;
import luowei.fengxskillsandinter.entity.SpellEntity;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import luowei.fengxskillsandinter.util.SpellCastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SparkProjectileTrigger extends SparkProjectile {

    @Override
    public boolean isTrigger() {
        return true;
    }

    @Override
    public void cast(Entity caster, World world, List<SpellNode> triggerChildren, SpellCastContext context, List<Spell> effectSpellList) {
        if (!world.isClient && world instanceof ServerWorld) {
            Vec3d lookVec = SpellEntity.computeSpawnDirection(caster, world, context);
            Vec3d spawnPos = SpellEntity.computeSpawnPosition(caster, lookVec);
            SparkProjectileEntity projectile = new SparkProjectileEntity(
                    ModEntities.SPARK_PROJECTILE, world, triggerChildren, context);
            projectile.getAndSolveEffect(effectSpellList);
            projectile.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setVelocity(lookVec.multiply(SparkProjectile.START_SPEED));
            projectile.setOwner(SpellCastUtil.resolveOwnerForProjectile(caster));
            projectile.configureFromSpell((float) SparkProjectile.DAMAGE, SparkProjectile.GRAVITY);
            world.spawnEntity(projectile);
        }
    }
}
