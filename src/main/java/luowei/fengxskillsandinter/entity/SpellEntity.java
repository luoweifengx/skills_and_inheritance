package luowei.fengxskillsandinter.entity;

import java.util.List;
import java.util.Optional;

import org.joml.Vector3f;

import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellCaster;
import luowei.fengxskillsandinter.spell.SpellNode;
import luowei.fengxskillsandinter.spell.spells.Homing;
import luowei.fengxskillsandinter.spell.spells.HomingShooter;
import luowei.fengxskillsandinter.spell.spells.StrongHoming;
import luowei.fengxskillsandinter.spell.spells.LarpaDownwards;
import luowei.fengxskillsandinter.spell.spells.GravityAnti;
import luowei.fengxskillsandinter.spell.spells.HeavySpread;
import luowei.fengxskillsandinter.util.HomingUtil;
import luowei.fengxskillsandinter.util.SpellCastUtil;
import luowei.fengxskillsandinter.util.SpellDamageUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class SpellEntity extends ProjectileEntity{

    /** 飞行若干 tick 后尝试执行触发子法术（无碰撞时）。 */
    protected static final int DEFAULT_TRIGGER_SPELL_AGE = 10;
    /** 短寿命静止实体默认存活 tick（子类可覆盖 {@link #getShortLivedStationaryTicks()}）。 */
    protected static final int DEFAULT_SHORT_LIVED_STATIONARY_TICKS = 2;
    protected static final double VELOCITY_EPSILON_SQ = 1.0E-7;
    /** 沿瞄准方向相对 {@link LivingEntity#getEyePos()} 的前移格数（略离开身体再生成）。 */
    public static final double PLAYER_SPAWN_FORWARD_OFFSET = 0.5;

    private static final double HOMING_FOV_DEGREES = 34.0;
    private static final double HOMING_MAX_RANGE = 28.0;
    /** 强力追踪：半锥角更大、索敌更远。 */
    private static final double STRONG_HOMING_FOV_DEGREES = 52.0;
    private static final double STRONG_HOMING_MAX_RANGE = 46.0;
    private static final double HOMING_ARG_A = 1.0;
    private static final double HOMING_ARG_B = 1.0;
    /** {@link HomingUtil#steerByGID}：惯性权重（越小越跟手）。 */
    private static final double HOMING_STEER_INERTIA = 0.10;
    private static final double STRONG_HOMING_STEER_INERTIA = 0.04;
    /** steerByGID 中未参与混合，仅占位与将来预判扩展。 */
    private static final double HOMING_STEER_DISPLACE = 0.85;
    /** steerByGID：导向权重（越大越贴目标方向）。 */
    private static final double HOMING_STEER_GUIDANCE = 0.58;
    private static final double STRONG_HOMING_STEER_GUIDANCE = 0.86;
    /** steerByGID：速度下限 clamp。 */
    private static final double HOMING_STEER_DAMP = 0.08;
    /** steerByGID：速度上限 clamp。 */
    private static final double HOMING_STEER_MAX_STEP = 4.0;
    /** Larpa 分身：竖直向下速度下限，避免 0 速「看不见在动」。 */
    private static final double LARPA_DOWN_MIN_SPEED = 0.35;
    /** Larpa：每隔多少 tick 在母体当前位置生成分身（仅服务端）。 */
    private static final int LARPA_INTERVAL_TICKS = 10;

    private static final double HOMING_TO_OWNER_MIN_DIST_SQ = 1.0;

    private static final TrackedData<Boolean> TRACKED_HOMING = DataTracker.registerData(SpellEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> TRACKED_STRONG_HOMING = DataTracker.registerData(SpellEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> TRACKED_HOMING_TO_OWNER = DataTracker.registerData(SpellEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> TRACKED_HOMING_TARGET = DataTracker.registerData(
            SpellEntity.class,
            TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE);

    /** 命中 {@link LivingEntity} 时的魔法伤害；子类在构造中赋值。 */
    protected float damage;
    /** 每 tick 重力加速度，与 {@link ProjectileEntity#getGravity()} 一致；由 {@link #configureFromSpell} 写入。 */
    protected double projectileGravity = 0.0;

    /** 由法术在生成时写入，便于在法术类集中调数值。 */
    public void setSpellDamage(float amount) {
        this.damage = amount;
    }
    private List<SpellNode> triggerChildren;
    /** 防止 onCollision 被多次调用导致触发法术重复执行 */

    private boolean hasTriggered = false;
    private boolean homing = false;
    private boolean strongHoming = false;
    private boolean homingToOwner = false;
    private boolean heavySpread = false;
    private int gravityAnti = 1;
    private boolean larpaDownwards = false;
    private SpellCastContext context;
    
    public SpellEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        //this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
    }
    public SpellEntity(EntityType<? extends ProjectileEntity> entityType, World world, List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world);
        this.triggerChildren = triggerChildren;
        this.context = context;
        //this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
    }

    /** 不受爆炸伤害与击退，避免核弹等把自己弹道吹飞。 */
    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return true;
    }
    @Override
    protected void initDataTracker(DataTracker.Builder dataTracker) {
        dataTracker.add(TRACKED_HOMING, false);
        dataTracker.add(TRACKED_STRONG_HOMING, false);
        dataTracker.add(TRACKED_HOMING_TO_OWNER, false);
        dataTracker.add(TRACKED_HOMING_TARGET, Optional.empty());
    }
    /**
     * 子类（链锯、光钻等）可重写：在原位短暂存在，不沿 {@link #setVelocity()} 飞行。
     * 每 tick 速度置零，{@link #tickShortLivedStationaryExtra()} 在服务端执行（如拆方块）。
     */
    protected boolean isShortLivedStationary() {
        return false;
    }

    /** 存活的 tick 数（含本 tick 结束后自增的 age）；达到后 {@link #discard()}。 */
    protected int getShortLivedStationaryTicks() {
        return DEFAULT_SHORT_LIVED_STATIONARY_TICKS;
    }

    /** 仅在服务端、短寿命静止模式下每 tick 调用。 */
    protected void tickShortLivedStationaryExtra() {
    }

    @Override
    public void tick() {
        if (isShortLivedStationary()) {
            this.setVelocity(Vec3d.ZERO);
            super.tick();
            if (!this.getWorld().isClient) {
                this.tickShortLivedStationaryExtra();
                if (this.age >= getShortLivedStationaryTicks()) {
                    this.discard();
                    return;
                }
            }
            renderParticles();
            return;
        }

        super.tick();
        if (this.age >= DEFAULT_TRIGGER_SPELL_AGE) {
            triggerSpell();
        }
        //碰撞
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
            return;
        }
        // 移动（与 velocity 一致，直线飞行）
        
        this.applyGravity();

        
        setVelocity();
        //客户端粒子效果

        spawnCopyProjectile();

        renderParticles();

        killEntity();
    }
    public void spawnCopyProjectile() {
        if (!this.larpaDownwards || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        if (this.age % LARPA_INTERVAL_TICKS != 0) {
            return;
        }
        Entity created = this.getType().create(world, SpawnReason.TRIGGERED);
        if (!(created instanceof SpellEntity fresh)) {
            return;
        }
        // 仅「壳」：不参与触发链、不再 Larpa；与母体同伤害/重力/发射者、当前位置、竖直向下飞
        fresh.triggerChildren = null;
        fresh.context = null;
        fresh.hasTriggered = false;
        fresh.larpaDownwards = false;
        fresh.configureFromSpell(this.damage, this.projectileGravity);
        fresh.setOwner(this.getOwner());
        fresh.setPosition(this.getX(), this.getY(), this.getZ());
        double speed = Math.max(this.getVelocity().length(), LARPA_DOWN_MIN_SPEED);
        Vec3d down = new Vec3d(0.0, -speed, 0.0);
        fresh.setVelocity(down);
        SpellCastUtil.applyFacingFromDirection(fresh, down);
        fresh.getAndSolveEffect(null);
        world.spawnEntity(fresh);
    }
    public void killEntity(){
        
    }
    public void renderParticles(){
    }
    public void setVelocity(){
        Vec3d velocity = this.getVelocity();
        if (isHomingEnabled()) {
            velocity = homing(velocity);
        }
        if (isHomingToOwnerEnabled()) {
            velocity = homingToOwner(velocity);
        }
        if (velocity.lengthSquared() > VELOCITY_EPSILON_SQ) {
            SpellCastUtil.applyFacingFromDirection(this, velocity);
        }
        this.setPosition(
            this.getX() + velocity.x,
            this.getY() + velocity.y,
            this.getZ() + velocity.z
        );
        this.setVelocity(velocity);
    }
    public void triggerSpell(){
        if (hasTriggered) return;
        if (triggerChildren != null && !triggerChildren.isEmpty() && getWorld() instanceof ServerWorld) {
            hasTriggered = true;

            SpellCaster.cast(this.context, this.triggerChildren, this, getWorld(),this.getOwner());
            this.discard();
        }
    }
    @Override
    protected void applyGravity() {
        double d = this.getFinalGravity();
        if (d != (double)0.0F) {
           this.setVelocity(this.getVelocity().add((double)0.0F, -d * gravityAnti, (double)0.0F));
        }
  
     }
    //碰撞检测
    @Override
    protected boolean canHit(Entity entity) {
        if(entity == this.getOwner()) {
            return false;
        }
        if(entity == this) {
            return false;
        }
        if (entity instanceof SpellEntity) {
            return false;
        }
        if (entity instanceof ItemEntity) {
            return false;
        }
        return true;
    }
    //碰撞处理
    @Override
    protected void onCollision(HitResult hitResult) {
        switch (hitResult.getType()) {
            case ENTITY:
                this.onEntityHit((EntityHitResult) hitResult);
                break;
            case BLOCK:
                this.onBlockHit((BlockHitResult) hitResult);
                break;
            default:
                break;
        }
    }
    //实体碰撞处理
    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        if(!this.getWorld().isClient) {
            if (hitResult.getEntity() instanceof LivingEntity target) {
                if(this.getWorld() instanceof ServerWorld world) {
                    SpellDamageUtil.applySpellProjectileDamage(world, target, this, damage);
                    if (triggerChildren != null && !triggerChildren.isEmpty() && getWorld() instanceof ServerWorld){
                        Vec3d normal = new Vec3d(0, 1, 0);
                        Vector3f v = this.getVelocity().toVector3f();
                        v.reflect(normal.toVector3f());
                        this.setVelocity(new Vec3d(v));
                    }
                    triggerSpell();
                    this.discard();
                }
            }
        }
    }
    //方块碰撞处理
    @Override
    protected void onBlockHit(BlockHitResult hitResult) {
        if(!this.getWorld().isClient) {
            if (triggerChildren != null && !triggerChildren.isEmpty() && getWorld() instanceof ServerWorld){
                Vec3d normal = Vec3d.of(hitResult.getSide().getVector());
                Vector3f v = this.getVelocity().toVector3f();
                v.reflect(normal.toVector3f());
                this.setVelocity(new Vec3d(v));
            }

            //if(hitResult.getBlock() instanceof Block waterBlock) {
            triggerSpell();
            this.discard();
            
        }
    }
    public void getAndSolveEffect(List<Spell> effectSpellList) {
        // 重置，保证同一实体若被复用/重建时状态不会串
        this.homing = false;
        this.strongHoming = false;
        this.homingToOwner = false;
        this.heavySpread = false;
        this.gravityAnti = 1;
        this.larpaDownwards = false;
        if (effectSpellList == null) {
            this.dataTracker.set(TRACKED_HOMING, false);
            this.dataTracker.set(TRACKED_STRONG_HOMING, false);
            this.dataTracker.set(TRACKED_HOMING_TO_OWNER, false);
            this.dataTracker.set(TRACKED_HOMING_TARGET, Optional.empty());
            return;
        }

        for(Spell spell : effectSpellList) {
            if(spell instanceof Homing) {
                this.homing = true;
            }
            if(spell instanceof StrongHoming) {
                this.homing = true;
                this.strongHoming = true;
            }
            if(spell instanceof HomingShooter) {
                this.homingToOwner = true;
            }
            if(spell instanceof HeavySpread) {
                this.heavySpread = true;
            }
            if(spell instanceof GravityAnti) {
                this.gravityAnti *= -1;
            }
            if(spell instanceof LarpaDownwards) {
                this.larpaDownwards = true;
            }
            applyHeavySpreadToDirection(this.getVelocity());
        }
        this.dataTracker.set(TRACKED_HOMING, this.homing);
        this.dataTracker.set(TRACKED_STRONG_HOMING, this.strongHoming);
        this.dataTracker.set(TRACKED_HOMING_TO_OWNER, this.homingToOwner);
        if (!this.homing) {
            this.dataTracker.set(TRACKED_HOMING_TARGET, Optional.empty());
        }
    }

    protected boolean isHomingEnabled() {
        return this.dataTracker.get(TRACKED_HOMING);
    }

    protected boolean isHomingToOwnerEnabled() {
        return this.dataTracker.get(TRACKED_HOMING_TO_OWNER);
    }
    public Vec3d homing(Vec3d velocity) {
        World world = this.getWorld();
        boolean strong = this.dataTracker.get(TRACKED_STRONG_HOMING);
        double fovRad = Math.toRadians(strong ? STRONG_HOMING_FOV_DEGREES : HOMING_FOV_DEGREES);
        double maxRange = strong ? STRONG_HOMING_MAX_RANGE : HOMING_MAX_RANGE;
        double steerGuidance = strong ? STRONG_HOMING_STEER_GUIDANCE : HOMING_STEER_GUIDANCE;
        double steerInertia = strong ? STRONG_HOMING_STEER_INERTIA : HOMING_STEER_INERTIA;

        if (!world.isClient) {
            Entity owner = this.getOwner();
            if (owner instanceof PlayerEntity player) {
                // 射线起点用弹体（非玩家眼睛）：触发法术在远处生成时，怪物在弹丸附近但可能离玩家较远，
                // 若从玩家眼算距离会筛掉；朝向仍用玩家视角。
                LivingEntity target = HomingUtil.getHomingTarget(
                        this,
                        this.getEyePos(),
                        player.getRotationVector(),
                        fovRad,
                        maxRange,
                        HOMING_ARG_A,
                        HOMING_ARG_B,
                        true);
                if (target != null) {
                    this.dataTracker.set(TRACKED_HOMING_TARGET, Optional.of(new LazyEntityReference<>(target.getUuid())));
                    velocity = HomingUtil.steerByGID(
                            velocity,
                            this.getPos(),
                            target.getEyePos(),
                            target.getVelocity(),
                            steerGuidance,
                            steerInertia,
                            HOMING_STEER_DISPLACE,
                            HOMING_STEER_DAMP,
                            HOMING_STEER_MAX_STEP);
                } else {
                    this.dataTracker.set(TRACKED_HOMING_TARGET, Optional.empty());
                }
            } else {
                this.dataTracker.set(TRACKED_HOMING_TARGET, Optional.empty());
            }
            return velocity;
        }

        Optional<LazyEntityReference<LivingEntity>> tracked = this.dataTracker.get(TRACKED_HOMING_TARGET);
        if (tracked.isEmpty()) {
            return velocity;
        }
        LivingEntity target = LazyEntityReference.resolve(tracked.get(), this.getWorld(), LivingEntity.class);
        if (target != null && target.isAlive() && !target.isRemoved()) {
            velocity = HomingUtil.steerByGID(
                    velocity,
                    this.getPos(),
                    target.getEyePos(),
                    target.getVelocity(),
                    steerGuidance,
                    steerInertia,
                    HOMING_STEER_DISPLACE,
                    HOMING_STEER_DAMP,
                    HOMING_STEER_MAX_STEP);
        }
        return velocity;
    }
    public Vec3d homingToOwner(Vec3d velocity) {
        Entity target = this.getOwner();
        if (target == null) {
            return velocity;
        }
        Vec3d toOwner = target.getEyePos().subtract(this.getPos());
        if (toOwner.lengthSquared() < HOMING_TO_OWNER_MIN_DIST_SQ) {
            return velocity;
        }
        return HomingUtil.steerByGID(
                velocity,
                this.getPos(),
                target.getEyePos(),
                target.getVelocity(),
                HOMING_STEER_INERTIA,
                HOMING_STEER_DISPLACE,
                HOMING_STEER_GUIDANCE,
                HOMING_STEER_DAMP,
                HOMING_STEER_MAX_STEP);
        //this.setVelocity(this.getVelocity().add(this.getOwner().getEyePos().subtract(this.getPos()).normalize().multiply(0.01)));
    }

    /**
     * 沉重散射（spawn-time）：若启用 heavySpread，则直接返回一个随机方向（单位向量），
     * 由外部在 setVelocity(方向 * 初速度大小) 时完成速度初始化。
     */
    public Vec3d applyHeavySpreadToDirection(Vec3d baseDir) {
        if (!this.heavySpread) {
            return baseDir;
        }
        return SpellCastUtil.randomUnitDirection(this.getWorld().getRandom());
    }
    // public SpellEntity setHoming(boolean homing) {
    //     this.homing = homing;
    //     return this;
    // }
    // public SpellEntity setHomingToOwner(boolean homingToOwner) {
    //     this.homingToOwner = homingToOwner;
    //     return this;
    // }
    public static Vec3d computeSpawnDirection(Entity caster, World world, SpellCastContext ctx) {
        Vec3d base;
        if (caster instanceof PlayerEntity) {
            base = caster.getRotationVector();
        } else {
            Vec3d vel = caster.getVelocity();
            if (vel.lengthSquared() > VELOCITY_EPSILON_SQ) {
                base = vel.normalize();
            } else {
                base = caster.getRotationVector();
            }
        }
        return base;
    }

    public static Vec3d computeSpawnPosition(Entity caster, Vec3d aimDir) {
        if (caster instanceof LivingEntity living) {
            return living.getEyePos().add(aimDir.multiply(PLAYER_SPAWN_FORWARD_OFFSET));
        }
        return caster.getPos().add(aimDir.multiply(PLAYER_SPAWN_FORWARD_OFFSET));
    }
    /**
     * 由法术在生成投射物后调用：设置本弹的伤害与重力（非 {@link SparkProjectileEntity} 子类也生效）。
     * @param spellGravity 每 tick 向下加速度，与原版箭矢等一致的量级；0 表示不额外施加重力。
     */
    public void configureFromSpell(float spellDamage, double spellGravity) {
        this.damage = spellDamage;
        this.projectileGravity = spellGravity;
    }

    @Override
    protected double getGravity() {
        return projectileGravity;
    }

}
