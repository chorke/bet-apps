package bet.apps.core;

import bet.apps.core.MyDate.Days;
import bet.apps.windows.MainWin;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class DateMiner {
    
    private static final int DAY = 1;
    private static final int WEEK = 1;
    private static final int MONTH = 1;
    private static final int YEAR = 2013;
    public static final MyDate INITIAL_DATE = new MyDate(DAY, WEEK, MONTH, YEAR, Days.UT);
    
    private final List<MyDate> allDates;
    
    public DateMiner(){
        allDates = new ArrayList<>();
        allDates.add(INITIAL_DATE);
    }
    
    /**
     * For given input <tt>str</tt> returns date represeted by MyDate. Input must has
     * at the first place date in format DD_MM_YYYY and splited from rest by space.
     *
     * @param str   string containing date of match at first place formatted DD_MM_YYYY
     *              separated by space from rest
     * @return      {@code MyDate} represented by <tt>str</tt>
     */
    public MyDate getDate(String str){
        if(str != null){
            str = removeExtension(str);
            String onlyDate = str.split("[ ]+")[0];
            String[] parse = onlyDate.split("[_]+");
            if(parse.length == 3){
                try{
                    int day = Integer.parseInt(parse[0]);
                    int month = Integer.parseInt(parse[1]);
                    int year = Integer.parseInt(parse[2]);
                    return getWeek(new MyDate(day, 1, month, year, Days.PO));
                } catch (NumberFormatException e){
                    return INITIAL_DATE;
                }
            } else {
                return INITIAL_DATE;
            }
        } else {
            return INITIAL_DATE;
        }
    }
    
    private MyDate getWeek(MyDate date){// throws IllegalArgumentException{
        if(date.compareTo(INITIAL_DATE) <= 0){
            return INITIAL_DATE;
        } else if(allDates.contains(date)){
            return find(date);
        } else {
            computeMissingDates(date);
            return allDates.get(allDates.size() - 1);
        }
    }
    
    private void computeMissingDates(MyDate date){
        MyDate last = allDates.get(allDates.size() - 1);
        while(!last.equals(date)){
            last = getNextDate(last);
            allDates.add(last);
        }
    }
    
    private MyDate getNextDate(MyDate date){
        Days newDayName = getNextDay(date.getDayName());
        int week = date.getWeek();
        if(newDayName == Days.PO){
            week++;
        }
        int day = date.getDay();
        int month = date.getMonth();
        int year = date.getYear();
        day++;
        MyDate md = new MyDate(day, week, month, year, newDayName);
        return correctDate(md);
    }
    
    private MyDate correctDate(MyDate date){
        if(!isCorrectDate(date)){
            int newDay = 1;
            int newMonth = date.getMonth() + 1;
            int newYear = date.getYear();
            if(newMonth > 12){
                newMonth = 1;
                newYear++;
            }
            return new MyDate(newDay, date.getWeek(), newMonth, newYear, date.getDayName());
        } else {
            return date;
        }
    }
    
    /**
     * Checks only whether day of week is greater then maximum days count for month.
     * 
     * @param date  date to check
     * @return      true if date is correct, false otherwise
     */
    private boolean isCorrectDate(MyDate date){
        if(isLeap(date.getYear())){
            return date.getDay() <= MyDate.monthCountLeap[date.getMonth()-1];
        } else {
            return date.getDay() <= MyDate.monthCountNormal[date.getMonth()-1];
        }
    }
    
    private boolean isLeap(int year){
        if((year % 4) != 0){
            return false;
        }
        /*(year % 4) == 0 */
        if((year % 400) == 0){
            return true;
        }
        /*(year % 400) != 0 */
        if((year % 100) == 0){
            return false;
        }
        return true;
    }
    
    private Days getNextDay(Days day){
        switch (day){
            case PO:
                return Days.UT;
            case UT:
                return Days.STR;
            case STR:
                return Days.STV;
            case STV:
                return Days.PI;
            case PI:
                return Days.SO;
            case SO:
                return Days.NE;
            default:
                return Days.PO;
        }
    }
    
    private MyDate find(MyDate date){
        for(MyDate md : allDates ){
            if(date.equals(md)){
                return md;
            }
        }
        return INITIAL_DATE;
    }
    
    private static String removeExtension(String str){
        int lastDot = str.lastIndexOf('.');
        if(lastDot == -1){
            return str;
        }
        return str.substring(0, lastDot);
    }
    
    /**
     * Returns actual date represented as {@code String} in format 
     * DD{@code separator}MM{@code separator}YYYY.
     * For example if separator is '_' and actual date is 12 march 2013, then 
     * returned value is 12_03_2013.
     * 
     * @param separator separator for isolate date fields
     * @return  actual date as String
     */
    public static String getTodayAsString(String separator){
        Calendar cal = new GregorianCalendar();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return day + separator + month + separator + year;
    }
    
    /**
     * Returns actual date represented as {@code MyDate}.
     * 
     * @return  actual date as {@code MyDate}
     */
    public static MyDate getTodayAsMyDate(){
        return MainWin.DATE_MINER.getDate(getTodayAsString("_"));
    }
}