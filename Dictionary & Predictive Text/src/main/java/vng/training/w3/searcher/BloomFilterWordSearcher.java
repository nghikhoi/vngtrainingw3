package vng.training.w3.searcher;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.List;

public class BloomFilterWordSearcher extends WordSearcher {

    private final BloomFilter<String> bloomFilter;

    public BloomFilterWordSearcher(List<String> words) {
        super(words);
        bloomFilter = initialize(words);
    }

    private BloomFilter<String> initialize(List<String> words) {
        final BloomFilter<String> bloomFilter;
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(
                        Charset.forName("UTF-8")),
                words.size());

        words.forEach(bloomFilter::put);

        return bloomFilter;
    }

    @Override
    public boolean contains(String word) {
        return bloomFilter.mightContain(word) && getWords().contains(word);
    }

}
