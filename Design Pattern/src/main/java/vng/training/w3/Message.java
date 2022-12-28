package vng.training.w3;

public class Message {

    private final String message;
    private final Font font;

    public Message(String message, Font font) {
        this.message = message;
        this.font = font;
    }

    public String getMessage() {
        return message;
    }

    public Font getFont() {
        return font;
    }

    public static class Builder {
        private String message;
        private Font font;

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setFont(Font font) {
            this.font = font;
            return this;
        }

        public Message build() {
            return new Message(message, font);
        }
    }

}
