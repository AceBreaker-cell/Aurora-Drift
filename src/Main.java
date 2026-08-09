import javax.swing.*;
import java.awt.Dimension;

void main() {
    IO.println("Booting Aurora Drift \u2014 Java " + Runtime.version());

    var canvas = new AuroraCanvas();
    var frame = buildFrame(canvas);

    SwingUtilities.invokeLater(() -> {
        frame.setVisible(true);
        canvas.requestFocusInWindow();
        new Timer(16, e -> canvas.tick()).start();
    });

    startStatsLogger(canvas);
}

JFrame buildFrame(AuroraCanvas canvas) {
    var frame = new JFrame("Aurora Drift");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    canvas.setPreferredSize(new Dimension(960, 640));
    frame.setContentPane(canvas);
    frame.pack();
    frame.setLocationRelativeTo(null);
    return frame;
}

void startStatsLogger(AuroraCanvas canvas) {
    Thread.ofVirtual().start(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(3000);
                IO.println("particles=%d  fps=%d  palette=%s".formatted(
                        canvas.particleCount(), canvas.fps(), canvas.paletteLabel()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    });
}