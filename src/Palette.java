import java.awt.Color;

/**
 * Curated color themes. Each particle's color is derived from its
 * remaining energy (0 = about to die, 1 = freshly spawned) by
 * interpolating across three stops: cool -> mid -> hot.
 */
public enum Palette {

    EMBER("Ember", new Color(255, 87, 34), new Color(255, 193, 7), new Color(90, 15, 10)),
    AURORA("Aurora", new Color(0, 229, 255), new Color(156, 39, 176), new Color(8, 8, 36)),
    VOID("Void", new Color(235, 235, 240), new Color(100, 100, 115), new Color(4, 4, 8));

    private final String label;
    private final Color hot;
    private final Color mid;
    private final Color cool;

    Palette(String label, Color hot, Color mid, Color cool) {
        this.label = label;
        this.hot = hot;
        this.mid = mid;
        this.cool = cool;
    }

    public String label() {
        return label;
    }

    public Color colorFor(double energy) {
        double t = Math.clamp(energy, 0.0, 1.0);
        return t > 0.5 ? blend(mid, hot, (t - 0.5) * 2) : blend(cool, mid, t * 2);
    }

    public Palette next() {
        Palette[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    private static Color blend(Color a, Color b, double t) {
        double c = Math.clamp(t, 0.0, 1.0);
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * c);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * c);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * c);
        return new Color(r, g, bl);
    }
}