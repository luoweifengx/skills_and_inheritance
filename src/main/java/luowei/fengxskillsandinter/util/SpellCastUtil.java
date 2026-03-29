package luowei.fengxskillsandinter.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

/**
 * 法术施放相关工具。投射物作为 caster 时，velocity 可能已被碰撞逻辑清零，
 * 使用位置差 (pos - prevPos) 获取本 tick 的飞行方向更可靠。
 */
public final class SpellCastUtil {

    private SpellCastUtil() {}

    /**
     * 获取 caster 的施法方向。当 caster 为投射物时，优先用位置差计算方向，
     * 避免 velocity 被碰撞逻辑清零导致方向错误。
     */
    /**
     * 触发法术时 {@code caster} 常为父投射物；若直接 {@code setOwner(caster)}，子弹的 owner 不是玩家，
     * {@link luowei.fengxskillsandinter.entity.SpellEntity#homing} 中依赖 {@code PlayerEntity} owner 的逻辑会失效。
     * 沿投射物 {@link ProjectileEntity#getOwner()} 链向上解析，直到非投射物或 owner 为空。
     */
    public static Entity resolveOwnerForProjectile(Entity caster) {
        Entity e = caster;
        while (e instanceof ProjectileEntity pe) {
            Entity next = pe.getOwner();
            if (next == null) {
                return e;
            }
            e = next;
        }
        return e;
    }

    public static Vec3d getCastDirection(Entity caster) {
        if (caster instanceof ProjectileEntity) {
            Vec3d prevPos = caster.getLerpedPos(0);
            Vec3d currPos = caster.getPos();
            Vec3d delta = currPos.subtract(prevPos);
            if (delta.lengthSquared() > 1.0E-6) {
                return delta.normalize();
            }
            Vec3d v = caster.getVelocity();
            if (v.lengthSquared() > 1.0E-6) {
                return v.normalize();
            }
        }
        return caster.getRotationVector();
    }

    /**
     * 按方向向量设置实体 yaw/pitch，与 {@link luowei.fengxskillsandinter.entity.SpellEntity} 中由速度推导朝向的公式一致，
     * 也与 {@link luowei.fengxskillsandinter.client.renderer.BaseProjectileEntityRenderer} 用速度算航向时一致。
     */
    public static void applyFacingFromDirection(Entity entity, Vec3d direction) {
        if (direction.lengthSquared() < 1.0E-8) {
            return;
        }
        Vec3d d = direction.normalize();
        double h = Math.sqrt(d.x * d.x + d.z * d.z);
        entity.setYaw((float) (Math.atan2(d.x, d.z) * (180.0 / Math.PI)));
        entity.setPitch((float) (Math.atan2(-d.y, h) * (180.0 / Math.PI)));
    }

    /** 单位球面上近似均匀随机方向（用于沉重散射等）。 */
    public static Vec3d randomUnitDirection(Random random) {
        double x = random.nextGaussian();
        double y = random.nextGaussian();
        double z = random.nextGaussian();
        Vec3d v = new Vec3d(x, y, z);
        if (v.lengthSquared() < 1.0E-8) {
            return new Vec3d(0.0, 1.0, 0.0);
        }
        return v.normalize();
    }
}
