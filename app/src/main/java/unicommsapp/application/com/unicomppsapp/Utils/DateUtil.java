package unicommsapp.application.com.unicomppsapp.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by fahmed on 28.2.2016.
 */
public class DateUtil {

    public static String changeDateFormat(String dateTime) throws Exception{

        Log.d("dateTime is", dateTime);

        if(dateTime.equals(""))
            return "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(utcZone);
        Date myDate = simpleDateFormat.parse(dateTime);

        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = simpleDateFormat.format(myDate);

        return formattedDate;
    }

}
