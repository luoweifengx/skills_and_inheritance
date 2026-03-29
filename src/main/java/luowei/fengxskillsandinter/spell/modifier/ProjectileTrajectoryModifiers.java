package luowei.fengxskillsandinter.spell.modifier;

public class ProjectileTrajectoryModifiers {
    private boolean homingToOwner;
    /** 每 tick 偏向搜索范围内最近的 {@link net.minecraft.entity.LivingEntity}（不含自身）。 */
    private boolean homing;
    private boolean heavySpread;

    public ProjectileTrajectoryModifiers() {
        this.homingToOwner = false;
        this.homing = false;
        this.heavySpread = false;
    }
    public boolean isHomingToOwner() {
        return this.homingToOwner;
    }
    public boolean isHeavySpread() {
        return this.heavySpread;
    }
    public boolean isHoming() {
        return this.homing;
    }
    public void resetProjectileTrajectoryModifiers() {
        this.homingToOwner = false;
        this.homing = false;
        this.heavySpread = false;
    }
    public void setHomingToOwner(boolean homingToOwner) {
        this.homingToOwner = homingToOwner;
    }
    public void setHeavySpread(boolean heavySpread) {
        this.heavySpread = heavySpread;
    }
    public void setHoming(boolean homing) {
        this.homing = homing;
    }
}
