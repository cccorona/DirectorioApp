package mx.com.cesarcorona.directorio.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ccabrera on 10/08/17.
 */

public class DateUtils {

    // format 24hre ex. 12:12 , 17:15
    private static String  HOUR_FORMAT = "HH:mm:ss";

    private DateUtils() {    }

    public static String getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfHour = new SimpleDateFormat(HOUR_FORMAT);
        String hour = sdfHour.format(cal.getTime());
        return hour;
    }

    /**
     * @param  target  hour to check
     * @param  start   interval start
     * @param  end     interval end
     * @return true    true if the given hour is between
     */
    public static boolean isHourInInterval(String target, String start, String end) {
        return ((target.compareTo(start) >= 0)
                && (target.compareTo(end) <= 0));
    }

    /**
     * @param  start   interval start
     * @param  end     interval end
     * @return true    true if the current hour is between
     */
    public static boolean isNowInInterval(String start, String end) {

        if(start == null || end == null){
            return false;
        }

        String formatedStart[] = start.split(":");
        if(formatedStart != null && formatedStart.length >3){
            start = formatedStart[1] +":" + formatedStart[2].substring(0,2);
        }
        String formatedEnd[] = end.split(":");
        if(formatedStart != null && formatedStart.length >3){
            end = formatedEnd[1] +":" + formatedEnd[2].substring(0,2);
        }
        return DateUtils.isHourInInterval
                (DateUtils.getCurrentHour(), start, end);
    }


    public static String formatDate(String start, String end) {
        if(start == null || end == null){
            return new Date().toString();
        }
        String formatedStart[] = start.split(":");
        if(formatedStart != null && formatedStart.length >3){
            start = formatedStart[1] +":" + formatedStart[2].substring(0,2);
        }
        String formatedEnd[] = end.split(":");
        if(formatedStart != null && formatedStart.length >3){
            end = formatedEnd[1] +":" + formatedEnd[2].substring(0,2);
        }
        return start+"-"+end;
    }


    public static String[] getCurrentDatKeys(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String currentDat ="";
        String curretnKEys[] = new String[2];
        boolean abreHoy = false;
        String negocioAbre ="";
        switch (day) {
            case Calendar.SUNDAY:
                // Current day is Sunday
                curretnKEys[0] ="da";
                curretnKEys[1] ="dc";
                break;

            case Calendar.MONDAY:
                // Current day is Monday
                curretnKEys[0] ="la";
                curretnKEys[1] ="lc";

                break;

            case Calendar.TUESDAY:
                curretnKEys[0] ="ma";
                curretnKEys[1] ="mc";

                break;
            case Calendar.THURSDAY:
                curretnKEys[0] ="ja";
                curretnKEys[1] ="jc";

                break;
            case Calendar.WEDNESDAY:
                curretnKEys[0] ="mia";
                curretnKEys[1] ="mic";

                break;
            case Calendar.FRIDAY:
                curretnKEys[0] ="va";
                curretnKEys[1] ="vc";

                break;
            case Calendar.SATURDAY:
                curretnKEys[0] ="sa";
                curretnKEys[1] ="sc";

                break;
            // etc.
        }
        return curretnKEys;
    }


    //    TEST
    public static void main (String[] args) {
        String now = DateUtils.getCurrentHour();
        String start = "14:00";
        String end   = "14:26";
        System. out.println(now + " between " + start + "-" + end + "?");
        System. out.println(DateUtils.isHourInInterval(now,start,end));
      /*
       * output example :
       *   21:01 between 14:00-14:26?
       *   false
       *
       */
    }
}