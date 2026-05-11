package luowei.fengxskillsandinter.screen;

import net.minecraft.client.gui.DrawContext;

/**
 * GUI 底板：渐变、边框、线段、圆环近似描边；玩家背包行间分隔线。
 */
public final class GuiPanelBackdrop {

    private GuiPanelBackdrop() {}

    public static void fillVerticalGradient(DrawContext ctx, int x, int y, int w, int h, int argbTop, int argbBottom) {
        if (w <= 0 || h <= 0) {
            return;
        }
        int steps = Math.min(48, Math.max(8, h / 6));
        for (int i = 0; i < steps; i++) {
            int y0 = y + h * i / steps;
            int y1 = y + h * (i + 1) / steps;
            float t = steps <= 1 ? 0 : (float) i / (float) (steps - 1);
            ctx.fill(x, y0, x + w, y1, lerpArgb(argbTop, argbBottom, t));
        }
    }

    /**
     * 顶部细高光条（现代感）。
     */
    public static void drawTopAccentBar(DrawContext ctx, int screenLeft, int top, int panelWidth, int barHeightPx, int argbAccent) {
        ctx.fill(screenLeft, top, screenLeft + panelWidth, top + barHeightPx, argbAccent);
    }

    public static void drawThinOutline(DrawContext ctx, int x, int y, int w, int h, int argb) {
        ctx.fill(x, y, x + w, y + 1, argb);
        ctx.fill(x, y + h - 1, x + w, y + h, argb);
        ctx.fill(x, y, x + 1, y + h, argb);
        ctx.fill(x + w - 1, y, x + w, y + h, argb);
    }

    /** 双层细框：外亮内暗 */
    public static void drawDoubleOutline(DrawContext ctx, int x, int y, int w, int h, int outerArgb, int innerArgb) {
        drawThinOutline(ctx, x, y, w, h, outerArgb);
        drawThinOutline(ctx, x + 1, y + 1, w - 2, h - 2, innerArgb);
    }

    /** 矩形外框，四边等厚（用于槽位）。 */
    public static void drawRectOutlineThickness(DrawContext ctx, int x, int y, int w, int h, int argb, int thicknessPx) {
        int t = Math.max(1, thicknessPx);
        if (w < t * 2 || h < t * 2) {
            drawThinOutline(ctx, x, y, w, h, argb);
            return;
        }
        ctx.fill(x, y, x + w, y + t, argb);
        ctx.fill(x, y + h - t, x + w, y + h, argb);
        ctx.fill(x, y, x + t, y + h, argb);
        ctx.fill(x + w - t, y, x + w, y + h, argb);
    }

    /** Bresenham 粗线段（GUI，整数坐标）。 */
    public static void drawLineThick(DrawContext ctx, int x0, int y0, int x1, int y1, int argb, int thicknessPx) {
        int t = Math.max(1, thicknessPx);
        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) {
            ctx.fill(x0 - t / 2, y0 - t / 2, x0 + (t + 1) / 2, y0 + (t + 1) / 2, argb);
            return;
        }
        for (int i = 0; i <= steps; i++) {
            int x = x0 + dx * i / steps;
            int y = y0 + dy * i / steps;
            ctx.fill(x - t / 2, y - t / 2, x + (t + 1) / 2, y + (t + 1) / 2, argb);
        }
    }

    /** 折线逼近圆环描边。 */
    public static void drawCircleStroke(DrawContext ctx, int cx, int cy, int radius, int argb, int thicknessPx, int segments) {
        if (radius <= 0 || segments < 8) {
            return;
        }
        int prevX = cx + radius;
        int prevY = cy;
        for (int i = 1; i <= segments; i++) {
            double ang = 2.0 * Math.PI * i / segments;
            int x = cx + (int) Math.round(Math.cos(ang) * radius);
            int y = cy + (int) Math.round(Math.sin(ang) * radius);
            drawLineThick(ctx, prevX, prevY, x, y, argb, thicknessPx);
            prevX = x;
            prevY = y;
        }
    }

    /** 背包三行间细分隔 + 背包与快捷栏之间分隔线（屏幕绝对坐标 + 面板相对槽区原点）。 */
    public static void drawPlayerInventorySectionLines(
        DrawContext ctx,
        int panelScreenLeft,
        int panelScreenTop,
        int panelRelativeOriginX,
        int panelRelativeInvTopY,
        int cols,
        int slotStep,
        int inventoryRows,
        int gapPixelsAboveHotbar,
        int rowDividerArgb,
        int hotbarDividerArgb
    ) {
        int absInvTop = panelScreenTop + panelRelativeInvTopY;
        int x1 = panelScreenLeft + panelRelativeOriginX + 3;
        int x2 = panelScreenLeft + panelRelativeOriginX + cols * slotStep - 3;
        if (x2 <= x1) {
            return;
        }
        for (int row = 1; row < inventoryRows; row++) {
            int y = absInvTop + row * slotStep;
            ctx.fill(x1, y - 1, x2, y, rowDividerArgb);
        }
        int gapMid = absInvTop + inventoryRows * slotStep + Math.max(1, gapPixelsAboveHotbar / 2);
        ctx.fill(x1, gapMid, x2, gapMid + 1, hotbarDividerArgb);
    }

    /** 分区标题下短线。 */
    public static void drawSectionUnderline(DrawContext ctx, int screenX, int screenY, int widthPx, int argb) {
        ctx.fill(screenX, screenY, screenX + widthPx, screenY + 1, argb);
    }

    private static int lerpArgb(int a, int b, float t) {
        float u = Math.clamp(t, 0.0f, 1.0f);
        int aa = (a >>> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int na = Math.round(aa + (ba - aa) * u);
        int nr = Math.round(ar + (br - ar) * u);
        int ng = Math.round(ag + (bg - ag) * u);
        int nb = Math.round(ab + (bb - ab) * u);
        return (na << 24) | (nr << 16) | (ng << 8) | nb;
    }
}
