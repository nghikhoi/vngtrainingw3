package vng.training.w3;

public class FontFactory {

    private static final FontFactory INSTANCE = new FontFactory();

    public static FontFactory getInstance() {
        return INSTANCE;
    }

    private FontFactory() {
    }

    public Font getFont(String fontName) {
        return new Font(fontName);
    }

}
