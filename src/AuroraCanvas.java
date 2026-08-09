import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The living canvas: owns the particle field, reacts to mouse and
 * keyboard input, and paints everything with a soft motion-trail glow.
 */
public class AuroraCanvas extends JPanel {

    private static final int SPAWN_PER_TICK = 3;
    private static final int MAX_PARTICLES = 500;

    private final List<Particle> particles = new ArrayList<>();

    private Vector2 pointer = new Vector2(300, 300);
    private InputMode mode = InputMode.CALM;

    private volatile Palette palette = Palette.AURORA;
    private volatile int fps;
    private volatile int liveParticleCount;

    private long lastFrameNanos = System.nanoTime();
    private int frameAccumulator;
    private long fpsWindowStart = System.nanoTime();

    public AuroraCanvas() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        var mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                pointer = new Vector2(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                pointer = new Vector2(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mode = SwingUtilities.isLeftMouseButton(e) ? InputMode.ATTRACT : InputMode.REPEL;
                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mode = InputMode.CALM;
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    palette = palette.next();
                }
            }
        });
    }

    /** Advances the simulation by one frame. Call from a Swing Timer on the EDT. */
    public void tick() {
        long now = System.nanoTime();
        double dt = Math.min((now - lastFrameNanos) / 1_000_000_000.0, 0.05);
        lastFrameNanos = now;

        int width = Math.max(getWidth(), 1);
        int height = Math.max(getHeight(), 1);

        var rnd = ThreadLocalRandom.current();
        for (int i = 0; i < SPAWN_PER_TICK && particles.size() < MAX_PARTICLES; i++) {
            double sx = rnd.nextDouble(width * 0.3, width * 0.7);
            double sy = rnd.nextDouble(height * 0.3, height * 0.7);
            particles.add(new Particle(sx, sy));
        }

        for (Particle p : particles) {
            p.update(dt, mode, pointer, width, height);
        }
        particles.removeIf(p -> !p.isAlive());
        liveParticleCount = particles.size();

        frameAccumulator++;
        if (now - fpsWindowStart >= 1_000_000_000L) {
            fps = frameAccumulator;
            frameAccumulator = 0;
            fpsWindowStart = now;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Painting a translucent black rectangle every frame (instead of a hard
        // clear) leaves faint trails behind each particle for a comet-like drift.
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        for (Particle p : particles) {
            double t = p.energy();
            Color base = palette.colorFor(t);
            float alpha = (float) Math.max(0.15, t);
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (alpha * 255)));
            double r = p.radius() * (0.6 + t);
            g2.fill(new Ellipse2D.Double(p.x() - r, p.y() - r, r * 2, r * 2));
        }

        drawHud(g2);
    }

    private void drawHud(Graphics2D g2) {
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        g2.setColor(new Color(255, 255, 255, 190));
        String hud = """
                AURORA DRIFT \u2014 %s
                particles %d \u00b7 %d fps
                LMB attract \u00b7 RMB repel \u00b7 SPACE palette
                """.formatted(palette.label(), particles.size(), fps);
        int y = 18;
        for (String line : hud.split("\n")) {
            g2.drawString(line, 12, y);
            y += 16;
        }
    }

    public int particleCount() { return liveParticleCount; }
    public int fps() { return fps; }
    public String paletteLabel() { return palette.label(); }
}