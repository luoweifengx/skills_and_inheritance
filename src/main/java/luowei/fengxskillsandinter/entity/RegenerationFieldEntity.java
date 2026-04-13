package luowei.fengxskillsandinter.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;

/**
 * 再生领域：存在 20s，服务端周期性治疗范围内非亡灵；客户端在球面边界刷粒子。
 */
public class RegenerationFieldEntity extends Entity {

    public static final double RADIUS = 6.0;
    private static final int LIFETIME_TICKS = 20 * 20;
    /** 每次刷新施加的再生持续时间：20s */
    private static final int REGEN_DURATION_TICKS = 20 * 20;
    private static final int REGEN_AMPLIFIER = 0;
    private static final float INSTANT_HEAL = 4.0f;
    private static final int HEAL_INTERVAL_TICKS = 20;

    public RegenerationFieldEntity(EntityType<? extends RegenerationFieldEntity> type, World world) {
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
                spawnBoundaryParticlesClient(center);
            }
            return;
        }
        if (this.getWorld() instanceof ServerWorld sw && this.age % HEAL_INTERVAL_TICKS == 0) {
            applyHealingServer(sw, center);
        }
    }

    private void applyHealingServer(ServerWorld sw, Vec3d center) {
        double r = RADIUS;
        Box box = new Box(center, center).expand(r);
        List<LivingEntity> targets = sw.getEntitiesByClass(LivingEntity.class, box,//做范围内的实体检测，而非碰撞检查
                e -> e.isAlive() && !e.isSpectator());
        for (LivingEntity target : targets) {
            if (target.squaredDistanceTo(center) > r * r) {
                continue;
            }
            if (target.getType().isIn(EntityTypeTags.UNDEAD)) {
                continue;
            }
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,
                    REGEN_DURATION_TICKS, REGEN_AMPLIFIER, false, true, true));
            target.heal(INSTANT_HEAL);
        }
    }

    private void spawnBoundaryParticlesClient(Vec3d center) {
        double r = RADIUS;
        int ringPoints = 48;
        double yOff = 0.1;
        for (int ring = 0; ring < 3; ring++) {
            double y = center.y + yOff + ring * 2.5;
            for (int i = 0; i < ringPoints; i++) {
                double t = (Math.PI * 2.0 * i) / ringPoints;
                double x = center.x + Math.cos(t) * r;
                double z = center.z + Math.sin(t) * r;
                this.getWorld().addParticleClient(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0, 0.02, 0.0);
            }
        }
        int verticalRings = 8;
        for (int v = 0; v < verticalRings; v++) {
            double phi = Math.PI * (v + 0.5) / verticalRings;
            double yr = Math.cos(phi) * r;
            double ringR = Math.sin(phi) * r;
            for (int i = 0; i < ringPoints; i += 2) {
                double t = (Math.PI * 2.0 * i) / ringPoints;
                double x = center.x + Math.cos(t) * ringR;
                double z = center.z + Math.sin(t) * ringR;
                double py = center.y + yr + 1.0;
                this.getWorld().addParticleClient(ParticleTypes.HAPPY_VILLAGER, x, py, z, 0.0, 0.0, 0.0);
            }
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
