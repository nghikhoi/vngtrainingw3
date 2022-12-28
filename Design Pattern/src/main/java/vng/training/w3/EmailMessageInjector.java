package vng.training.w3;

import vng.training.w3.di.ApplicationInjector;
import vng.training.w3.di.EmailMessageService;

public class EmailMessageInjector implements ApplicationInjector {

    @Override
    public Application getApplication() {
        return new Application(new EmailMessageService());
    }

}
