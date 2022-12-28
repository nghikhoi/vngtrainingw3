package vng.training.w3;

public class Application {

    private final MessageService messageService;

    public Application(MessageService messageService) {
        this.messageService = new MessageServiceProxy(messageService);
    }

    public void sendMessage(String sender, Message message) {
        messageService.sendMessage(sender, message);
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

}
