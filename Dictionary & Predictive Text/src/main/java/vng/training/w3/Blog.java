package vng.training.w3;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Blog {

    @CsvBindByName(column = "id")
    private String id;
    @CsvBindByName(column = "gender")
    private String gender;
    @CsvBindByName(column = "age")
    private String age;
    @CsvBindByName(column = "topic")
    private String topic;
    @CsvBindByName(column = "sign")
    private String sign;
    @CsvBindByName(column = "date")
    private String date;
    @CsvBindByName(column = "text")
    private String text;

}
