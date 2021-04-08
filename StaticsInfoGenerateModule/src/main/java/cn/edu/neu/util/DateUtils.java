package cn.edu.neu.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author 32098
 */
public class DateUtils {
    public static String getYearBaseByAge(int age){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -age);
        Date newDate = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        String newDateString = dateFormat.format(newDate);
        int newDateInteger = Integer.parseInt(newDateString);
        String yearbasetype = "未知";
        if(newDateInteger >= 1940 && newDateInteger < 1950){
            yearbasetype = "40后";
        }else if (newDateInteger >= 1950 && newDateInteger < 1960){
            yearbasetype = "50后";
        }else if (newDateInteger >= 1960 && newDateInteger < 1970){
            yearbasetype = "60后";
        }else if (newDateInteger >= 1970 && newDateInteger < 1980){
            yearbasetype = "70后";
        }else if (newDateInteger >= 1980 && newDateInteger < 1990){
            yearbasetype = "80后";
        }else if (newDateInteger >= 1990 && newDateInteger < 2000){
            yearbasetype = "90后";
        }else if (newDateInteger >= 2000 && newDateInteger < 2010){
            yearbasetype = "00后";
        }else if (newDateInteger >= 2010 ){
            yearbasetype = "10后";
        }
        return yearbasetype;
    }
}


