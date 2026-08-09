import java.util.concurrent.ThreadLocalRandom;

/**
 * A single point of light drifting through the field. Particles are
 * born with a random outward velocity, gently pulled or pushed by the
 * pointer depending on the current {@link InputMode}, and fade out as
 * their life runs down.
 */
public class Particle {

    private double x, y;
    private double vx, vy;
    private double life;
    private final double maxLife;
    private final double radius;

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;

        var rnd = ThreadLocalRandom.current();
        double angle = rnd.nextDouble(0, Math.PI * 2);
        double speed = rnd.nextDouble(10, 40);
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;

        this.maxLife = rnd.nextDouble(2.5, 5.0);
        this.life = maxLife;
        this.radius = rnd.nextDouble(1.5, 4.0);
    }

    public void update(double dt, InputMode mode, Vector2 pointer, double width, double height) {
        if (mode != InputMode.CALM) {
            // Constant-magnitude pull/push toward the pointer, regardless of
            // distance, so every particle on screen reacts visibly — not just
            // ones that already happen to be close by.
            Vector2 toPointer = pointer.subtract(new Vector2(x, y));
            double strength = mode == InputMode.ATTRACT ? 1000 : -800;
            Vector2 accel = toPointer.normalized().scale(strength);
            vx += accel.x() * dt;
            vy += accel.y() * dt;
        }

        // drag: lets particles overshoot and orbit a little before settling,
        // instead of snapping straight to the pointer
        vx *= 0.98;
        vy *= 0.98;

        x += vx * dt;
        y += vy * dt;
        life -= dt;

        if (x < 0 || x > width) vx *= -1;
        if (y < 0 || y > height) vy *= -1;
        x = Math.clamp(x, 0, width);
        y = Math.clamp(y, 0, height);
    }

    public boolean isAlive() {
        return life > 0;
    }

    /** 0 = about to die, 1 = freshly spawned. */
    public double energy() {
        return Math.max(0, life / maxLife);
    }

    public double x() { return x; }
    public double y() { return y; }
    public double radius() { return radius; }
}