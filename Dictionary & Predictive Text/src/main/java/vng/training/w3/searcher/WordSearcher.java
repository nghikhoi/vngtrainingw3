package vng.training.w3.searcher;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public abstract class WordSearcher {

    @Getter
    private final List<String> words;

    public abstract boolean contains(String word);

}
