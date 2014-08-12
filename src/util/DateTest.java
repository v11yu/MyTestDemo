package util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public class DateTest {
	public static void main(String[] args) throws ParseException {
		Date d = new Date();
		System.out.println(d);
		SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		d = df.parse("2014-03-01 00-00-00");
		System.out.println(d);
	}
}
