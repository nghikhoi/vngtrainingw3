package vng.training.w3.predictive;

import java.util.List;

public interface IPredictiveText {

    List<String> predict(String source, int limit);

}
