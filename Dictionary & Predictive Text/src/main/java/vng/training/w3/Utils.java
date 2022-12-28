package vng.training.w3;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Character> toCharacterList(String s) {
        List<Character> characters = new ArrayList<>();
        for (char c : s.toCharArray()) {
            characters.add(c);
        }
        return characters;
    }

    public static Character[] toCharacterArray(String s) {
        Character[] characters = new Character[s.length()];
        for (int i = 0; i < s.length(); i++) {
            characters[i] = s.charAt(i);
        }
        return characters;
    }

    public static void writeCsv(File cache, List<Blog> blogs) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        try (FileWriter writer = new FileWriter(cache)) {
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
            beanToCsv.write(blogs);
        }
    }

    public static <B> List<B> readCsv(File csv, Class<B> clazz) throws IOException {
        return new CsvToBeanBuilder<B>(new FileReader(csv))
                .withType(clazz).build().parse();
    }

}
