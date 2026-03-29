package luowei.fengxskillsandinter.entity;

import java.util.List;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import luowei.fengxskillsandinter.spell.spells.Nuke;

public class NukeEntity extends SpellEntity {

    private static final int PARTICLE_LOOP = 2;
    private static final double PARTICLE_DELTA = 0.01;

    public NukeEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public NukeEntity(EntityType<? extends ProjectileEntity> entityType, World world, List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
    }

    private void explode(ServerWorld world) {
        Vec3d p = this.getPos();
        world.createExplosion(
                this,
                null,
                null,
                p.x,
                p.y,
                p.z,
                Nuke.EXPLOSION_POWER,
                false,
                World.ExplosionSourceType.TNT,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.ENTITY_GENERIC_EXPLODE);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder dataTracker) {
        super.initDataTracker(dataTracker);
    }

    @Override
    protected void onBlockHit(BlockHitResult hitResult) {
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld sw) {
            this.explode(sw);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        if (hitResult.getEntity() == this.getOwner()) {
            return;
        }
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld sw) {
            this.explode(sw);
            this.discard();
        }
    }

    @Override
    protected double getGravity() {
        return Nuke.PROJECTILE_GRAVITY;
    }

    @Override
    public void renderParticles() {
        if (this.getWorld().isClient) {
            for (int i = 0; i < PARTICLE_LOOP; i++) {
                this.getWorld().addParticleClient(
                        ParticleTypes.SMOKE,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        PARTICLE_DELTA,
                        PARTICLE_DELTA,
                        PARTICLE_DELTA);
                this.getWorld().addParticleClient(
                        ParticleTypes.WHITE_SMOKE,
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
