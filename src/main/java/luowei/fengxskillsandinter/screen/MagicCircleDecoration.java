package luowei.fengxskillsandinter.screen;

import net.minecraft.client.gui.DrawContext;

/**
 * 符文台八槽环形区域的魔法阵装饰（矢量近似绘制，与 {@link RunicTableScreenHandler#INPUT_RING_RADIUS} 对齐）。
 */
public final class MagicCircleDecoration {

    private MagicCircleDecoration() {
    }

    /**
     * @param cx 屏幕绝对中心 X（面板 left + {@link RunicTableScreenHandler#RING_CENTER_X}）
     * @param cy 屏幕绝对中心 Y（面板 top + {@link RunicTableScreenHandler#RING_CENTER_Y}）
     * @param ringRadius 与槽位环半径一致（{@link RunicTableScreenHandler#INPUT_RING_RADIUS}）
     */
    public static void drawEightSlotMagicCircle(DrawContext ctx, int cx, int cy, int ringRadius) {
        int outer = ringRadius + 14;
        int midRing = ringRadius + 4;
        int innerRing = Math.max(8, ringRadius - 8);
        int hub = 7;

        int cOuter = 0xCCbfb0e8;
        int cMid = 0xAA9578d8;
        int cInner = 0x884a3868;
        int cSpoke = 0x99605090;

        GuiPanelBackdrop.drawCircleStroke(ctx, cx, cy, outer, cOuter, 2, 72);
        GuiPanelBackdrop.drawCircleStroke(ctx, cx, cy, midRing, cMid, 1, 56);
        GuiPanelBackdrop.drawCircleStroke(ctx, cx, cy, innerRing, cInner, 1, 40);
        GuiPanelBackdrop.drawCircleStroke(ctx, cx, cy, hub, cMid, 1, 24);

        for (int i = 0; i < 8; i++) {
            double deg = -90.0 + i * 45.0;
            double rad = Math.toRadians(deg);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            int x1 = cx + (int) Math.round(cos * innerRing);
            int y1 = cy + (int) Math.round(sin * innerRing);
            int x2 = cx + (int) Math.round(cos * (outer - 2));
            int y2 = cy + (int) Math.round(sin * (outer - 2));
            GuiPanelBackdrop.drawLineThick(ctx, x1, y1, x2, y2, cSpoke, 1);
        }

        int diamondR = ringRadius - 2;
        for (int i = 0; i < 4; i++) {
            double a0 = Math.toRadians(-45.0 + i * 90.0);
            double a1 = Math.toRadians(-45.0 + (i + 1) * 90.0);
            int ax = cx + (int) Math.round(Math.cos(a0) * diamondR);
            int ay = cy + (int) Math.round(Math.sin(a0) * diamondR);
            int bx = cx + (int) Math.round(Math.cos(a1) * diamondR);
            int by = cy + (int) Math.round(Math.sin(a1) * diamondR);
            GuiPanelBackdrop.drawLineThick(ctx, ax, ay, bx, by, cInner, 1);
        }
    }
}
