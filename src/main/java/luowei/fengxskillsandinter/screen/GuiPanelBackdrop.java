package luowei.fengxskillsandinter.screen;

import net.minecraft.client.gui.DrawContext;

/**
 * 极简面板底色：纵向渐变 + 顶栏强调线 + 细边框。
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
