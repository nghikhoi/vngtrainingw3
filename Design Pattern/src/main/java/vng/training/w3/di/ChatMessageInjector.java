package vng.training.w3.di;

import vng.training.w3.Application;

public class ChatMessageInjector implements ApplicationInjector {

    @Override
    public Application getApplication() {
        return new Application(new EmailMessageService());
    }

}
