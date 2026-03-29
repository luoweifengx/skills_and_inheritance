package luowei.fengxskillsandinter.trajectory;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HomingUtil {
    /**
     * 圆锥 + 距离 加权选目标：
     * - 先用 Box 按 maxDistance 粗筛
     * - 再用圆锥判定（半角 angle）
     * - 最后按 angleWeight/distanceWeight 评分，返回最优目标
     */
    public static LivingEntity getHomingTarget(ProjectileEntity projectile, Vec3d rayOrigin, Vec3d rayDirection,
            double angle, double maxDistance, double distanceWeight, double angleWeight, boolean excludeOwner) {
        if (projectile == null || rayOrigin == null || rayDirection == null) {
            return null;
        }
        if (maxDistance <= 0.0) {
            return null;
        }
        if (rayDirection.lengthSquared() <= 1.0E-8) {
            return null;
        }

        Vec3d rayDirUnit = rayDirection.normalize();
        double cosHalfAngle = Math.cos(angle);
        double maxDistanceSq = maxDistance * maxDistance;

        World world = projectile.getWorld();
        Box searchBox = new Box(rayOrigin, rayOrigin).expand(maxDistance);
        Entity owner = projectile.getOwner();
        Entity projectileEntity = projectile;
        Vec3d projectilePos = projectile.getPos();

        List<LivingEntity> candidates = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e.isAlive()
                        && !e.isRemoved()
                        && e != projectileEntity
                        && (!excludeOwner || owner == null || e != owner));

        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;

        for (LivingEntity e : candidates) {
            Vec3d point = e.getEyePos();
            Vec3d v = point.subtract(rayOrigin);
            double vLenSq = v.lengthSquared();
            if (vLenSq <= 1.0E-8 || vLenSq > maxDistanceSq) {
                continue;
            }

            Vec3d vDir = v.normalize();
            double dot = vDir.dotProduct(rayDirUnit);
            if (dot < cosHalfAngle) {
                continue;
            }

            // 越贴近视线越小，越近越小
            double anglePenalty = 1.0 - dot;
            double distancePenalty = Math.sqrt(e.squaredDistanceTo(projectilePos)) / maxDistance;
            double score = angleWeight * anglePenalty + distanceWeight * distancePenalty;

            if (score < bestScore) {
                bestScore = score;
                best = e;
            }
        }
        if(best == null) {
            return null;
        }
        //FengxSkillsAndInheritance.LOGGER.info("best: " + best.getName().getString() + "Position: " + best.getPos());
        return best;
    }
    public static Vec3d HomingTrace(Vec3d currentVelocity, Vec3d projectilePos, Vec3d targetPos) {
        Vec3d v = currentVelocity.add(targetPos.subtract(projectilePos));
        return v.normalize().multiply(currentVelocity.length());
    }

    /**
     * 根据目标位置进行“平滑追踪”：
     * 每 tick 在当前方向基础上向目标方向偏转一部分，并保持速度大小在[minSpeed, maxSpeed]区间。
     */
    // public static Vec3d steerTowardTargetPos(Vec3d currentVelocity, Vec3d projectilePos, Vec3d targetPos,
    //         double steerStrength, double minSpeed, double maxSpeed) {
    //     if (currentVelocity == null || projectilePos == null || targetPos == null) {
    //         return currentVelocity;
    //     }

    //     Vec3d to = targetPos.subtract(projectilePos);
    //     if (to.lengthSquared() <= 1.0E-8) {
    //         return currentVelocity;
    //     }

    //     Vec3d desiredDir = to.normalize();
    //     double speed = currentVelocity.length();
    //     if (speed < minSpeed) {
    //         speed = minSpeed;
    //     } else if (speed > maxSpeed) {
    //         speed = maxSpeed;
    //     }

    //     Vec3d baseDir = currentVelocity.lengthSquared() > 1.0E-8 ? currentVelocity.normalize() : desiredDir;
    //     Vec3d newDir = baseDir.add(desiredDir.multiply(steerStrength)).normalize();
    //     return newDir.multiply(speed);
    // }
    public static Vec3d steerByGID(
        Vec3d currentVelocity,
        Vec3d projectilePos,
        Vec3d targetPos,
        Vec3d targetVelocity,
        double guidance,      // 导向权重
        double inertia,       // 惯性权重
        double displacement,  // 位移前视（秒或tick系数）
        double minSpeed,
        double maxSpeed
    ) {
        if (currentVelocity == null || projectilePos == null || targetPos == null) {
            return currentVelocity;
        }

        // 1) 速度幅值保持（只改方向）
        double speed = currentVelocity.length();
        if (speed < minSpeed) speed = minSpeed;
        if (speed > maxSpeed) speed = maxSpeed;

        // 2) 位移前视：把目标点往目标速度方向“预判”一段
        // Vec3d leadPos = targetVelocity == null
        //         ? targetPos
        //         : targetPos.add(targetVelocity.multiply(displacement));

        Vec3d toLead = targetPos.subtract(projectilePos);
        if (toLead.lengthSquared() <= 1.0E-8) {
            return currentVelocity;
        }

        Vec3d desiredDir = toLead.normalize();
        Vec3d baseDir = currentVelocity.lengthSquared() > 1.0E-8
                ? currentVelocity.normalize()
                : desiredDir;

        // 3) 导向 + 惯性 混合
        Vec3d mixed = baseDir.multiply(Math.max(0.0, inertia))
                .add(desiredDir.multiply(Math.max(0.0, guidance)));

        if (mixed.lengthSquared() <= 1.0E-8) {
            mixed = desiredDir;
        }

        return mixed.normalize().multiply(speed);
    }
}
