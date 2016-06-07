package com.quercus.graphagregator;

import java.util.Calendar;

/**
 * Created by Михаил on 05.06.2016.
 * Используется для обработки пограничных состояний при добавление или отнимание дней
 * Вывод форматированного текста из даты
 */
public class CalendarComplement {

    private  static  String[] name_week = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private  static  String[] name_month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static Calendar incrementDay(Calendar date){
        int dateMaxDayInYear = date.getActualMaximum(Calendar.DAY_OF_YEAR);

        if (date.get(Calendar.DAY_OF_YEAR) == dateMaxDayInYear) {
            date.add(Calendar.YEAR, 1);
            date.set(Calendar.DAY_OF_YEAR, 1);
        }
        else {
            date.add(Calendar.DAY_OF_YEAR, 1);
        }

        return date;
    }
    public static  Calendar decrementDay(Calendar date){
        int dateMaxDayInYear = date.getActualMaximum(Calendar.DAY_OF_YEAR);

        if (date.get(Calendar.DAY_OF_YEAR) == 1) {
            date.add(Calendar.YEAR, -1);
            date.set(Calendar.DAY_OF_YEAR, date.getActualMaximum(Calendar.DAY_OF_YEAR));
        }
        else {
            date.add(Calendar.DAY_OF_YEAR, -1);
        }

        return date;
    }

    public static Calendar incrementWeek(Calendar date){
        int dateMaxWeekInYear = date.getActualMaximum(Calendar.WEEK_OF_YEAR);

        if (date.get(Calendar.WEEK_OF_YEAR) == dateMaxWeekInYear) {
            date.add(Calendar.YEAR, 1);
            date.set(Calendar.WEEK_OF_YEAR, 1);
        }
        else {
            date.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return date;
    }
    public static  Calendar decrementWeek(Calendar date){
        int dateMaxWeekInYear = date.getActualMaximum(Calendar.WEEK_OF_YEAR);

        if (date.get(Calendar.WEEK_OF_YEAR) == 1) {
            date.add(Calendar.YEAR, -1);
            date.set(Calendar.WEEK_OF_YEAR, date.getActualMaximum(Calendar.WEEK_OF_YEAR));
        }
        else {
            date.add(Calendar.WEEK_OF_YEAR, -1);
        }

        return date;
    }

    public static Calendar incrementMonth(Calendar date){
        if (date.get(Calendar.MONTH) == Calendar.DECEMBER) {
            date.add(Calendar.YEAR, 1);
            date.set(Calendar.MONTH, Calendar.JANUARY);
        }
        else {
            date.add(Calendar.MONTH, 1);
        }

        return date;
    }
    public static  Calendar decrementMonth(Calendar date){
        if (date.get(Calendar.MONTH) == Calendar.JANUARY) {
            date.add(Calendar.YEAR, -1);
            date.set(Calendar.MONTH, Calendar.DECEMBER);
        }
        else {
            date.add(Calendar.MONTH, -1);
        }

        return date;
    }

    public static Calendar getStartDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();

        Calendar dateStartDay = Calendar.getInstance();
        dateStartDay.setTimeInMillis(dateInMillins);

        dateStartDay.set(Calendar.HOUR_OF_DAY, 0);
        dateStartDay.clear(Calendar.MINUTE);
        dateStartDay.clear(Calendar.SECOND);
        dateStartDay.clear(Calendar.MILLISECOND);

        return dateStartDay;
    }
    public static Calendar getStartWeekDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();

        Calendar dateStartWeek = Calendar.getInstance();
        dateStartWeek.setTimeInMillis(dateInMillins);

        while (dateStartWeek.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            dateStartWeek = decrementDay(dateStartWeek);
        }

        return dateStartWeek;
    }
    public static Calendar getFinishWeekDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();

        Calendar dateFinishWeek = Calendar.getInstance();
        dateFinishWeek.setTimeInMillis(dateInMillins);

        while (dateFinishWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            dateFinishWeek = incrementDay(dateFinishWeek);
        }

        return dateFinishWeek;
    }

    public static String toStringHour(Calendar date){
        String ansver = String.valueOf(date.get(Calendar.HOUR_OF_DAY))
                + ":"
                + "00";
        return ansver;
    }
    public static String toStringDay(Calendar date){
        String answer = name_week[date.get(Calendar.DAY_OF_WEEK)-1]
                + ", "
                + date.get(Calendar.DAY_OF_MONTH) +
                " " + name_month[date.get(Calendar.MONTH)]
                + " "
                + date.get(Calendar.YEAR);
        return answer;
    }
    public static String toStringWeek(Calendar date){
        Calendar dateStartWeek = getStartWeekDay(date);
        Calendar dateEndWeek = getFinishWeekDay(date);

        String answer = dateStartWeek.get(Calendar.DAY_OF_MONTH)
                + " " + name_month[dateStartWeek.get(Calendar.MONTH)]
                + " - "
                + dateEndWeek.get(Calendar.DAY_OF_MONTH)
                + " "
                + name_month[dateEndWeek.get(Calendar.MONTH)]
                + " "
                + dateEndWeek.get(Calendar.YEAR);
        return answer;
    }
    public static String toStringMonth(Calendar date){
        String answer = name_month[date.get(Calendar.MONTH)]
                + " "
                + date.get(Calendar.YEAR);
        return answer;
    }

}
