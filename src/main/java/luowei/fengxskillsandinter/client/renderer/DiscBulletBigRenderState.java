package luowei.fengxskillsandinter.client.renderer;

import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;

/**
 * 锯片实体渲染状态：额外记录绕飞行轴的自旋角（度）。
 */
public class DiscBulletBigRenderState extends ProjectileEntityRenderState {
    /** 投射物沿飞行方向（局部前向）的旋转，用于圆盘锯齿视觉。 */
    public float rollDegrees;
}
