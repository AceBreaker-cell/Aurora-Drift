# Aurora Drift

A small interactive particle simulation for the desktop, written in pure Java (Swing, no external dependencies). Glowing particles drift across the window, leaving soft motion trails, and react to your mouse. Pull them in, push them away, and cycle between three color palettes.

<img width="962" height="672" alt="Screenshot 2026-08-09 073844" src="https://github.com/user-attachments/assets/6935daa1-7728-4211-93bc-12d7b886d42a" />

## Features

- **Living particle field** — particles spawn continuously, drift, fade, and respawn
- **Mouse interaction** — left-click to attract particles toward the cursor, right-click to repel them
- **Three color palettes** — Ember (warm amber), Aurora (cyan/violet), Void (monochrome) — cycle with `SPACE`
- **Motion trails** — a translucent overlay each frame instead of a hard clear, giving particles a comet-like glow
- **Live stats** — particle count and FPS logged to the console every few seconds from a background virtual thread
- Written with modern Java: records, enums with behavior, a compact-source-file entry point (`void main()`, no boilerplate class), text blocks, and virtual threads

## Requirements

- JDK 26 (or any JDK ≥ 25 the compact source file / instance `main` feature was finalized in JDK 25)
- No build tool, no external libraries, just the JDK

## Running it

**From IntelliJ IDEA**
Open the project, then click the ▶ run icon next to `void main()` in `Main.java` (or press `Shift+F10`).

**From the command line**
```bash
javac -d out src/*.java
java -cp out Main
```

## Controls

| Input | Effect |
|---|---|
| Left-click (hold) | Attract particles toward the cursor |
| Right-click (hold) | Repel particles away from the cursor |
| `SPACE` | Switch color palette |

## Project structure

```
src/
├── Main.java          # Entry point — builds the window and starts the render loop
├── AuroraCanvas.java   # Simulation + rendering (Swing JPanel) and input handling
├── Particle.java       # Per-particle physics (position, velocity, life)
├── Palette.java        # Color themes with energy-based interpolation
├── Vector2.java         # Immutable 2D vector math (record)
└── InputMode.java      # Attract / repel / calm state
```
