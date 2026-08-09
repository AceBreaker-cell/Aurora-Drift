/**
 * How particles currently respond to the pointer.
 */
public enum InputMode {
    CALM, ATTRACT, REPEL;

    public InputMode next() {
        InputMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}