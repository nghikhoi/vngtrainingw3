package vng.training.w3;

public class ChatMessageService implements MessageService {

    @Override
    public void sendMessage(String receiver, Message message) {
        System.out.println("Send chat to " + receiver + " with message: " + message.getMessage() + " and font: " + message.getFont().getFontName());
    }

}
