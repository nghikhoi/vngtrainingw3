package vng.training.w3;

public class MessageServiceProxy implements MessageService {

    private MessageService messageService;

    public MessageServiceProxy(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void sendMessage(String receiver, Message message) {
        if (message.getMessage().length() > 100) {
            throw new IllegalArgumentException("Message content is too long");
        }
        messageService.sendMessage(receiver, message);
    }

}
