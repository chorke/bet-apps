/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bet.apps.classes;

import bet.apps.core.MyDate;
import bet.apps.core.DateMiner;
import bet.apps.core.Match;
import bet.apps.filters.DateFilter;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Chorke
 */
public class DateFilterTest {
    
    public DateFilterTest() {
    }

    /**
     * Test of passFilter method, of class DateFilter.
     */
    @Test
    public void testPassFilter() {
        DateMiner dm = new DateMiner();
        MyDate mdate0 = dm.getDate("01_01_2013"); //ut
        MyDate mdate1 = dm.getDate("02_02_2013"); //so
        MyDate mdate2 = dm.getDate("02_03_2013"); //so
        MyDate mdate3 = dm.getDate("03_02_2013"); //ne
        MyDate mdate4 = dm.getDate("10_02_2013"); //ne
        MyDate mdate5 = dm.getDate("17_02_2013"); //ne
        MyDate mdate6 = dm.getDate("03_03_2013"); //ne
        MyDate mdate7 = dm.getDate("02_07_2014"); //str
        MyDate mdate8 = dm.getDate("04_02_2013"); //po
        MyDate mdate9 = dm.getDate("04_02_2014"); //ut
        MyDate mdate10 = dm.getDate("04_02_2015"); //str
        MyDate mdate11 = dm.getDate("04_02_2017"); //so
        MyDate mdate12 = dm.getDate("05_02_2013"); //ut
        MyDate mdate13 = dm.getDate("06_02_2013"); //str
        MyDate mdate14 = dm.getDate("06_04_2013"); //so
        
        Match date0 = new Match(0, 0, 0, 0, mdate0);
        Match date1 = new Match(0, 0, 0, 0, mdate1);
        Match date2 = new Match(0, 0, 0, 0, mdate2);
        Match date3 = new Match(0, 0, 0, 0, mdate3);
        Match date4 = new Match(0, 0, 0, 0, mdate4);
        Match date5 = new Match(0, 0, 0, 0, mdate5);
        Match date6 = new Match(0, 0, 0, 0, mdate6);
        Match date7 = new Match(0, 0, 0, 0, mdate7);
        Match date8 = new Match(0, 0, 0, 0, mdate8);
        Match date9 = new Match(0, 0, 0, 0, mdate9);
        Match date10 = new Match(0, 0, 0, 0, mdate10);
        Match date11 = new Match(0, 0, 0, 0, mdate11);
        Match date12 = new Match(0, 0, 0, 0, mdate12);
        Match date13 = new Match(0, 0, 0, 0, mdate13);
        Match date14 = new Match(0, 0, 0, 0, mdate14);
        
        
//        System.out.println(date0);
//        System.out.println(date1);
//        System.out.println(date2);
//        System.out.println(date3);
//        System.out.println(date4);
//        System.out.println(date5);
//        System.out.println(date6);
//        System.out.println(date7);
//        System.out.println(date8);
//        System.out.println(date9);
//        System.out.println(date10);
//        System.out.println(date11);
//        System.out.println(date12);
//        System.out.println(date13);
//        System.out.println(date14);
        
        DateFilter dateFT = new DateFilter("Date from to");
        dateFT.setType(DateFilter.FilterType.DATE);
        dateFT.setStart(dm.getDate("03_02_2013")); // date3, date8, date12,
        dateFT.setEnd(dm.getDate("05_02_2013"));   // not date13, date1, 
        dateFT.setRange(DateFilter.FilterRange.FROM_TO);
        DateFilter dateES = new DateFilter("Date every same");
        dateES.setType(DateFilter.FilterType.DATE);
        dateES.setRange(DateFilter.FilterRange.EVERY_SAME);
        dateES.setStart(dm.getDate("03_02_2013"));
        
        DateFilter dayOfWeekFT = new DateFilter("day of week FT");
        dayOfWeekFT.setType(DateFilter.FilterType.DAY_OF_WEEK);
        dayOfWeekFT.setRange(DateFilter.FilterRange.FROM_TO);
        dayOfWeekFT.setStart(dm.getDate("04_02_2013"));
        dayOfWeekFT.setEnd(dm.getDate("06_02_2013"));
        DateFilter dayOfWeekES = new DateFilter("day of month ES");
        dayOfWeekES.setType(DateFilter.FilterType.DAY_OF_WEEK);
        dayOfWeekES.setRange(DateFilter.FilterRange.EVERY_SAME);
        dayOfWeekES.setStart(dm.getDate("03_02_2013"));
        
        DateFilter dayOfMonthES = new DateFilter("day of month es");
        dayOfMonthES.setType(DateFilter.FilterType.DAY_OF_MONTH);
        dayOfMonthES.setRange(DateFilter.FilterRange.EVERY_SAME);
        dayOfMonthES.setStart(dm.getDate("02_02_2013"));
        DateFilter dayOfMonthFT= new DateFilter("day of month ft");
        dayOfMonthFT.setType(DateFilter.FilterType.DAY_OF_MONTH);
        dayOfMonthFT.setRange(DateFilter.FilterRange.FROM_TO);
        dayOfMonthFT.setStart(dm.getDate("02_02_2013"));
        dayOfMonthFT.setEnd(dm.getDate("04_02_2013"));
        
        DateFilter weekES= new DateFilter("week es");
        weekES.setType(DateFilter.FilterType.WEEK);
        weekES.setRange(DateFilter.FilterRange.EVERY_SAME);
        weekES.setStart(dm.getDate("04_02_2013"));
        DateFilter weekFT = new DateFilter("week ft");
        weekFT.setType(DateFilter.FilterType.WEEK);
        weekFT.setRange(DateFilter.FilterRange.FROM_TO);
        weekFT.setStart(dm.getDate("03_02_2013"));
        weekFT.setEnd(dm.getDate("04_02_2013"));
        
        DateFilter monthES = new DateFilter("month es");
        monthES.setType(DateFilter.FilterType.MONTH);
        monthES.setRange(DateFilter.FilterRange.EVERY_SAME);
        monthES.setStart(dm.getDate("12_02_2013"));
        DateFilter monthFT= new DateFilter("month ft");
        monthFT.setType(DateFilter.FilterType.MONTH);
        monthFT.setRange(DateFilter.FilterRange.FROM_TO);
        monthFT.setStart(dm.getDate("03_02_2013"));
        monthFT.setEnd(dm.getDate("04_03_2013"));
        
        DateFilter yearES = new DateFilter("year es");
        yearES.setType(DateFilter.FilterType.YEAR);
        yearES.setRange(DateFilter.FilterRange.EVERY_SAME);
        yearES.setStart(dm.getDate("03_02_2013"));
        DateFilter yearFT = new DateFilter("year ft");
        yearFT.setType(DateFilter.FilterType.YEAR);
        yearFT.setRange(DateFilter.FilterRange.FROM_TO);
        yearFT.setStart(dm.getDate("03_02_2014"));
        yearFT.setEnd(dm.getDate("04_02_2016"));
        
        
        if(dateFT.passFilter(date0) ||
                dateFT.passFilter(date1) ||
                dateFT.passFilter(date2) ||
                !dateFT.passFilter(date3) ||
                dateFT.passFilter(date4) ||
                dateFT.passFilter(date5) ||
                dateFT.passFilter(date6) ||
                dateFT.passFilter(date7) ||
                !dateFT.passFilter(date8) ||
                dateFT.passFilter(date9) ||
                dateFT.passFilter(date10) ||
                dateFT.passFilter(date11) ||
                !dateFT.passFilter(date12) ||
                dateFT.passFilter(date13) ||
                dateFT.passFilter(date14)
                ){
            fail();
        }
        
        if(dateES.passFilter(date0) ||
                dateES.passFilter(date1) ||
                dateES.passFilter(date2) ||
                !dateES.passFilter(date3) ||
                dateES.passFilter(date4) ||
                dateES.passFilter(date5) ||
                dateES.passFilter(date6) ||
                dateES.passFilter(date7) ||
                dateES.passFilter(date8) ||
                dateES.passFilter(date9) ||
                dateES.passFilter(date10) ||
                dateES.passFilter(date11) ||
                dateES.passFilter(date12) ||
                dateES.passFilter(date13) ||
                dateES.passFilter(date14)
                ){
            fail();
        }
        
        if(dayOfWeekES.passFilter(date0) ||
                dayOfWeekES.passFilter(date1) ||
                dayOfWeekES.passFilter(date2) ||
                !dayOfWeekES.passFilter(date3) ||
                !dayOfWeekES.passFilter(date4) ||
                !dayOfWeekES.passFilter(date5) ||
                !dayOfWeekES.passFilter(date6) ||
                dayOfWeekES.passFilter(date7) ||
                dayOfWeekES.passFilter(date8) ||
                dayOfWeekES.passFilter(date9) ||
                dayOfWeekES.passFilter(date10) ||
                dayOfWeekES.passFilter(date11) ||
                dayOfWeekES.passFilter(date12) ||
                dayOfWeekES.passFilter(date13) ||
                dayOfWeekES.passFilter(date14)
                ){
            fail();
        }
        
        if(!dayOfWeekFT.passFilter(date0) ||
                dayOfWeekFT.passFilter(date1) ||
                dayOfWeekFT.passFilter(date2) ||
                dayOfWeekFT.passFilter(date3) ||
                dayOfWeekFT.passFilter(date4) ||
                dayOfWeekFT.passFilter(date5) ||
                dayOfWeekFT.passFilter(date6) ||
                !dayOfWeekFT.passFilter(date7) ||
                !dayOfWeekFT.passFilter(date8) ||
                !dayOfWeekFT.passFilter(date9) ||
                !dayOfWeekFT.passFilter(date10) ||
                dayOfWeekFT.passFilter(date11) ||
                !dayOfWeekFT.passFilter(date12) ||
                !dayOfWeekFT.passFilter(date13) ||
                dayOfWeekFT.passFilter(date14)
                ){
            fail();
        }
        
        if(dayOfMonthES.passFilter(date0) ||
                !dayOfMonthES.passFilter(date1) ||
                !dayOfMonthES.passFilter(date2) ||
                dayOfMonthES.passFilter(date3) ||
                dayOfMonthES.passFilter(date4) ||
                dayOfMonthES.passFilter(date5) ||
                dayOfMonthES.passFilter(date6) ||
                !dayOfMonthES.passFilter(date7) ||
                dayOfMonthES.passFilter(date8) ||
                dayOfMonthES.passFilter(date9) ||
                dayOfMonthES.passFilter(date10) ||
                dayOfMonthES.passFilter(date11) ||
                dayOfMonthES.passFilter(date12) ||
                dayOfMonthES.passFilter(date13) ||
                dayOfMonthES.passFilter(date14)
                ){
            fail();
        }
        
        if(dayOfMonthFT.passFilter(date0) ||
                !dayOfMonthFT.passFilter(date1) ||
                !dayOfMonthFT.passFilter(date2) ||
                !dayOfMonthFT.passFilter(date3) ||
                dayOfMonthFT.passFilter(date4) ||
                dayOfMonthFT.passFilter(date5) ||
                !dayOfMonthFT.passFilter(date6) ||
                !dayOfMonthFT.passFilter(date7) ||
                !dayOfMonthFT.passFilter(date8)|| 
                !dayOfMonthFT.passFilter(date9) ||
                !dayOfMonthFT.passFilter(date10) ||
                !dayOfMonthFT.passFilter(date11) ||
                dayOfMonthFT.passFilter(date12) ||
                dayOfMonthFT.passFilter(date13) ||
                dayOfMonthFT.passFilter(date14)
                ){
            fail();
        }
        
        if(weekES.passFilter(date0) ||
                weekES.passFilter(date1) ||
                weekES.passFilter(date2) ||
                weekES.passFilter(date3) ||
                !weekES.passFilter(date4) ||
                weekES.passFilter(date5) ||
                weekES.passFilter(date6) ||
                weekES.passFilter(date7) ||
                !weekES.passFilter(date8) ||
                weekES.passFilter(date9) ||
                weekES.passFilter(date10) ||
                weekES.passFilter(date11) ||
                !weekES.passFilter(date12) ||
                !weekES.passFilter(date13) ||
                weekES.passFilter(date14)
                ){
            fail();
        }
        
        if(weekFT.passFilter(date0) ||
                !weekFT.passFilter(date1) ||
                weekFT.passFilter(date2) ||
                !weekFT.passFilter(date3) ||
                !weekFT.passFilter(date4) ||
                weekFT.passFilter(date5) ||
                weekFT.passFilter(date6) ||
                weekFT.passFilter(date7) ||
                !weekFT.passFilter(date8) ||
                weekFT.passFilter(date9) ||
                weekFT.passFilter(date10) ||
                weekFT.passFilter(date11) ||
                !weekFT.passFilter(date12) ||
                !weekFT.passFilter(date13) ||
                weekFT.passFilter(date14)
                ){
            fail();
        }
        
        if(monthES.passFilter(date0) ||
                !monthES.passFilter(date1) ||
                monthES.passFilter(date2) ||
                !monthES.passFilter(date3) ||
                !monthES.passFilter(date4) ||
                !monthES.passFilter(date5) ||
                monthES.passFilter(date6) ||
                monthES.passFilter(date7) ||
                !monthES.passFilter(date8) ||
                !monthES.passFilter(date9) ||
                !monthES.passFilter(date10) ||
                !monthES.passFilter(date11) ||
                !monthES.passFilter(date12) ||
                !monthES.passFilter(date13) ||
                monthES.passFilter(date14)
                ){
            fail();
        }
        
        if(monthFT.passFilter(date0) ||
                !monthFT.passFilter(date1) ||
                !monthFT.passFilter(date2) ||
                !monthFT.passFilter(date3) ||
                !monthFT.passFilter(date4) ||
                !monthFT.passFilter(date5) ||
                !monthFT.passFilter(date6) ||
                monthFT.passFilter(date7) ||
                !monthFT.passFilter(date8) ||
                !monthFT.passFilter(date9) ||
                !monthFT.passFilter(date10) ||
                !monthFT.passFilter(date11) ||
                !monthFT.passFilter(date12) ||
                !monthFT.passFilter(date13) ||
                monthFT.passFilter(date14)
                ){
            fail();
        }
        
        if(!yearES.passFilter(date0) ||
                !yearES.passFilter(date1) ||
                !yearES.passFilter(date2) ||
                !yearES.passFilter(date3) ||
                !yearES.passFilter(date4) ||
                !yearES.passFilter(date5) ||
                !yearES.passFilter(date6) ||
                yearES.passFilter(date7) ||
                !yearES.passFilter(date8) ||
                yearES.passFilter(date9) ||
                yearES.passFilter(date10) ||
                yearES.passFilter(date11) ||
                !yearES.passFilter(date12) ||
                !yearES.passFilter(date13) ||
                !yearES.passFilter(date14)
                ){
            fail();
        }
        
        if(yearFT.passFilter(date0) ||
                yearFT.passFilter(date1) ||
                yearFT.passFilter(date2) ||
                yearFT.passFilter(date3) ||
                yearFT.passFilter(date4) ||
                yearFT.passFilter(date5) ||
                yearFT.passFilter(date6) ||
                !yearFT.passFilter(date7) ||
                yearFT.passFilter(date8) ||
                !yearFT.passFilter(date9) ||
                !yearFT.passFilter(date10) ||
                yearFT.passFilter(date11) ||
                yearFT.passFilter(date12) ||
                yearFT.passFilter(date13) ||
                yearFT.passFilter(date14)
                ){
            fail();
        }
        
    }
}