package com.quercus.graphagregator;

import java.text.SimpleDateFormat;
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
    public static Calendar getFinishDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();

        Calendar dateFinishDay = Calendar.getInstance();
        dateFinishDay.setTimeInMillis(dateInMillins);

        dateFinishDay.set(Calendar.HOUR_OF_DAY, 23);
        dateFinishDay.set(Calendar.MINUTE, 59);
        dateFinishDay.set(Calendar.SECOND, 59);
        dateFinishDay.set(Calendar.MILLISECOND, 99);

        return dateFinishDay;
    }

    public static Calendar getStartWeekDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();

        Calendar dateStartWeek = Calendar.getInstance();
        dateStartWeek.setTimeInMillis(dateInMillins);

        while (dateStartWeek.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            dateStartWeek = decrementDay(dateStartWeek);
        }

        // Установим часы, минуты и т.д. в начало дня
        dateStartWeek = getStartDay(dateStartWeek);

        return dateStartWeek;
    }
    public static Calendar getFinishWeekDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();

        Calendar dateFinishWeek = Calendar.getInstance();
        dateFinishWeek.setTimeInMillis(dateInMillins);

        while (dateFinishWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            dateFinishWeek = incrementDay(dateFinishWeek);
        }

        // Теперь установим часы, минуты и т.д. к окончанию дня
        dateFinishWeek = getFinishDay(dateFinishWeek);

        return dateFinishWeek;
    }


    public static Calendar getStartMonthDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();

        Calendar dateStartMonth = Calendar.getInstance();
        dateStartMonth.setTimeInMillis(dateInMillins);

        dateStartMonth.set(Calendar.DAY_OF_MONTH, 0);
        dateStartMonth = getStartDay(dateStartMonth);

        // Установим часы, минуты и т.д. в начало дня
        dateStartMonth = getStartDay(dateStartMonth);

        return dateStartMonth;
    }
    public static Calendar getFinishMonthDay(Calendar date){

        long dateInMillins  = date.getTimeInMillis();
        int dateMaxDayInThisMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar dateFinishMonth = Calendar.getInstance();
        dateFinishMonth.setTimeInMillis(dateInMillins);

        dateFinishMonth.set(Calendar.DAY_OF_MONTH, dateMaxDayInThisMonth);
        dateFinishMonth = getStartDay(dateFinishMonth);

        // Установим часы, минуты и т.д. в начало дня
        dateFinishMonth = getFinishDay(dateFinishMonth);

        return dateFinishMonth;
    }


    public static String toStringHour(Calendar date){
        String ansver = String.valueOf(date.get(Calendar.HOUR_OF_DAY))
                + ":"
                + "00";
        return ansver;
    }
    public static String toStringDayNameInWeek(Calendar date){
        String answer = name_week[date.get(Calendar.DAY_OF_WEEK)-1];
        return answer;
    }
    public static String toStringNumberDayInMonth(Calendar date){
        String answer = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        return answer;
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

    // TODO: Придумать название получше
    public static String toStringYYYYMMDDHHMM(Calendar date){
        String answer = date.get(Calendar.YEAR) + "." +
                date.get(Calendar.MONTH) + "." +
                date.get(Calendar.DAY_OF_MONTH) + " " +
                date.get(Calendar.HOUR_OF_DAY) + ":";

        int mm = date.get(Calendar.MINUTE);
        if(mm < 10)
            answer += "0" + mm;
        else
            answer += mm;

        return answer;
    }


    // TODO: Не протестировано
    public static Calendar convertStringToCalendar(String YYYY, String MM, String DD, String hh, String mm, String ss){
        Calendar answer = Calendar.getInstance();
        answer.clear();

        // TODO: Исключение обработка
        // TODO: Почему много вопросительных знаков в пустых местах
        answer.set(Calendar.YEAR, Integer.parseInt(YYYY));
        answer.set(Calendar.MONTH, Integer.parseInt(MM));
        answer.set(Calendar.DAY_OF_MONTH, Integer.parseInt(DD));

        answer.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hh));
        answer.set(Calendar.MINUTE, Integer.parseInt(mm));
        answer.set(Calendar.SECOND, Integer.parseInt(ss));

        return answer;
    }

    // TODO: Не протестировано
    public static int convertStringTimeToMinutes(String hh, String mm){
        int answerMinutes = 0;

        try {
            answerMinutes = Integer.parseInt(hh)*60 + Integer.parseInt(mm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return answerMinutes;
    }


    // TODO: Название сменить
    public static Calendar clearTrash(Calendar date){
        date.clear(Calendar.MINUTE);
        date.clear(Calendar.SECOND);
        date.clear(Calendar.MILLISECOND);
        return date;
    }

}
