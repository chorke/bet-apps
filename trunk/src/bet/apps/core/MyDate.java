package bet.apps.core;

import bet.apps.BetApps;


public class MyDate implements /*Comparator<MyDate>, */Comparable<MyDate> {
    public static enum Days {PO, UT, STR, STV, PI, SO, NE};
    public static final int[] monthCountLeap= {31,29,31,30,31,30,31,31,30,31,30,31};
    public static final int[] monthCountNormal= {31,28,31,30,31,30,31,31,30,31,30,31};
    private final int day;
    private final int week;
    private final int month;
    private final int year;
    private final Days dayName;

    public MyDate(int day, int week, int month, int year, Days dayName) {
        this.day = day;
        this.week = week;
        this.month = month;
        this.year = year;
        this.dayName = dayName;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Days getDayName() {
        return dayName;
    }

    public int getWeek() {
        return week;
    }
    
    @Override
    public final int compareTo(MyDate o){
        if(o == null) {
            throw new NullPointerException(BetApps.MESSAGES.getString("nullDate"));
        } else {
            int oVal = 10000 * o.year + 100 * o.month + o.day;
            int thisVal = 10000 * this.year + 100 * this.month + this.day;
            return thisVal - oVal;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj.getClass() == getClass()){
            MyDate date = (MyDate)obj;
            return ((date.getDay() == day)
                    &&(date.getMonth() == month)
                    &&(date.getYear() == year));
        } else {
            return false; 
        }
    }

    @Override
    public int hashCode() {
        return 10000 * year + 100 * month + day;
    }

    @Override
    public String toString() {
        return "day: " + this.day 
                + "  week: " + this.week 
                + "  month: " + this.month 
                + "  year: " + this.year
                + "  dayName: " + this.dayName;
    }
}
