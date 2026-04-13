package luowei.fengxskillsandinter.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * 黑洞领域：在落点存在数秒，将范围内生物向中心牵引；客户端刷暗色粒子。
 */
public class BlackHoleEntity extends Entity {

    public static final double RADIUS = 7.5;
    private static final int LIFETIME_TICKS = 10 * 20;
    private static final double PULL_BASE = 0.07;
    private static final double VERTICAL_PULL_SCALE = 0.45;

    public BlackHoleEntity(EntityType<? extends BlackHoleEntity> type, World world) {
        super(type, world);
        this.setInvisible(true);
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age >= LIFETIME_TICKS) {
            this.discard();
            return;
        }
        Vec3d center = this.getPos();
        if (this.getWorld().isClient) {
            if ((this.age & 1) == 0) {
                spawnVortexParticlesClient(center);
            }
            return;
        }
        if (this.getWorld() instanceof ServerWorld sw) {
            applyPullServer(sw, center);
        }
    }

    private void applyPullServer(ServerWorld sw, Vec3d center) {
        double r = RADIUS;
        Box box = new Box(center, center).expand(r);
        List<LivingEntity> targets = sw.getEntitiesByClass(LivingEntity.class, box,
                e -> e.isAlive() && !e.isRemoved() && !e.isSpectator());
        for (LivingEntity target : targets) {
            double distSq = target.squaredDistanceTo(center);
            if (distSq > r * r || distSq < 1.0E-4) {
                continue;
            }
            Vec3d to = center.subtract(target.getPos());
            Vec3d dir = to.normalize();
            double t = 1.0 - Math.sqrt(distSq) / r;
            double strength = PULL_BASE * (0.35 + 0.65 * t * t);
            target.addVelocity(
                    dir.x * strength,
                    dir.y * strength * VERTICAL_PULL_SCALE,
                    dir.z * strength);
        }
    }

    private void spawnVortexParticlesClient(Vec3d center) {
        double r = RADIUS * 0.92;
        var random = this.getWorld().getRandom();
        for (int i = 0; i < 28; i++) {
            double u = random.nextDouble() * Math.PI * 2.0;
            double v = random.nextDouble() * Math.PI;
            double sinV = Math.sin(v);
            double ox = Math.cos(u) * sinV * r * random.nextDouble();
            double oy = Math.cos(v) * r * 0.35 * random.nextDouble();
            double oz = Math.sin(u) * sinV * r * random.nextDouble();
            double px = center.x + ox;
            double py = center.y + oy + 0.5;
            double pz = center.z + oz;
            Vec3d inwardVec = center.subtract(px, py, pz);
            if (inwardVec.lengthSquared() > 1.0E-6) {
                Vec3d inward = inwardVec.normalize();
                this.getWorld().addParticleClient(
                        ParticleTypes.REVERSE_PORTAL,
                        px, py, pz,
                        inward.x * 0.04,
                        inward.y * 0.04,
                        inward.z * 0.04);
            }
        }
        for (int i = 0; i < 10; i++) {
            double t = random.nextDouble() * Math.PI * 2.0;
            double rr = random.nextDouble() * r;
            this.getWorld().addParticleClient(
                    ParticleTypes.SMOKE,
                    center.x + Math.cos(t) * rr,
                    center.y + random.nextDouble() * 1.2,
                    center.z + Math.sin(t) * rr,
                    (random.nextDouble() - 0.5) * 0.02,
                    -0.04 - random.nextDouble() * 0.03,
                    (random.nextDouble() - 0.5) * 0.02);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }
}
