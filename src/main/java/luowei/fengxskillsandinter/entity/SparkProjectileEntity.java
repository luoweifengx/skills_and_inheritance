package luowei.fengxskillsandinter.entity;

import java.util.List;

import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import luowei.fengxskillsandinter.spell.spells.SparkProjectile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class SparkProjectileEntity extends SpellEntity {

    private static final int PARTICLE_COUNT = 2;
    private static final double PARTICLE_DELTA = 0.01;

    public SparkProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.damage = (float) SparkProjectile.DAMAGE;
        this.projectileGravity = SparkProjectile.GRAVITY;
    }

    public SparkProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world,
            List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
        this.damage = (float) SparkProjectile.DAMAGE;
        this.projectileGravity = SparkProjectile.GRAVITY;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder dataTracker) {
        super.initDataTracker(dataTracker);
    }

    @Override
    public void renderParticles() {
        if (this.getWorld().isClient) {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                this.getWorld().addParticleClient(
                        ParticleTypes.WITCH,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        PARTICLE_DELTA,
                        PARTICLE_DELTA,
                        PARTICLE_DELTA);
            }
        }
    }
}
