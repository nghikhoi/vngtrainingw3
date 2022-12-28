package vng.training.w3.di;

import vng.training.w3.Message;
import vng.training.w3.MessageService;

public class EmailMessageService implements MessageService {

    @Override
    public void sendMessage(String receiver, Message message) {
        System.out.println("Send email to " + receiver + " with message: " + message.getMessage() + " and font: " + message.getFont().getFontName());
    }

}
