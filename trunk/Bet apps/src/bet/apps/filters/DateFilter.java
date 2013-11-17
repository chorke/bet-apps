
package bet.apps.filters;

import bet.apps.BetApps;
import bet.apps.core.DateMiner;
import bet.apps.core.Match;
import bet.apps.core.MyDate;

public class DateFilter implements Filter {

    public static enum FilterType {DATE, DAY_OF_WEEK, DAY_OF_MONTH, WEEK, MONTH, YEAR};

    public static enum FilterRange {EVERY_SAME, FROM_TO};
    
    
    private final String name;
    private FilterRange range = FilterRange.EVERY_SAME;
    private FilterType type = FilterType.DAY_OF_WEEK;
    private MyDate start = DateMiner.INITIAL_DATE;
    private MyDate end = DateMiner.INITIAL_DATE;
    private boolean isNotFilter = false;

    public DateFilter(String name) {
        if(name != null){
            this.name = name;
        } else {
            this.name = "";
        }
    }
    
    @Override
    public boolean passFilter(Match match){
        boolean result = checkPassFilter(match.getDate());
        if(isNotFilter){
            return !result;
        } else {
            return result;
        }
    }

    private boolean checkPassFilter(MyDate date) {
        switch (type) {
            case DATE:
                switch(range){
                    case EVERY_SAME:
                        return date.equals(start);
                    case FROM_TO:
                        return (date.compareTo(start) >= 0
                                && date.compareTo(end) <= 0);
                }
                break;
            case DAY_OF_WEEK:
                switch(range){
                    case EVERY_SAME:
                        return date.getDayName() == start.getDayName();
                    case FROM_TO:
                        return (date.getDayName().compareTo(start.getDayName()) >= 0
                                && date.getDayName().compareTo(end.getDayName()) <= 0);
                }
                break;
            case DAY_OF_MONTH:
                switch(range){
                    case EVERY_SAME:
                        return date.getDay() == start.getDay();
                    case FROM_TO:
                        return (date.getDay() >= start.getDay()
                                && date.getDay() <= end.getDay());
                }
                break;
            case WEEK:
                switch(range){
                    case EVERY_SAME:
                        return date.getWeek() == start.getWeek();
                    case FROM_TO:
                        return (date.getWeek() >= start.getWeek()
                                && date.getWeek() <= end.getWeek());
                }
                break;
            case MONTH:
                switch(range){
                    case EVERY_SAME:
                        return date.getMonth() == start.getMonth();
                    case FROM_TO:
                        return (date.getMonth() >= start.getMonth()
                                && date.getMonth() <= end.getMonth());
                }
                break;
            case YEAR:
                switch(range){
                    case EVERY_SAME:
                        return date.getYear() == start.getYear();
                    case FROM_TO:
                        return (date.getYear() >= start.getYear()
                                && date.getYear() <= end.getYear());
                }
                break;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public FilterRange getRange() {
        return range;
    }

    public FilterType getType() {
        return type;
    }

    public void setRange(FilterRange range) {
        this.range = range;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public void setEnd(MyDate end) {
        this.end = end;
    }

    public MyDate getStart() {
        return start;
    }

    public MyDate getEnd() {
        return end;
    }

    public void setStart(MyDate start) {
        this.start = start;
    }

    public boolean isIsNotFilter() {
        return isNotFilter;
    }

    public void setIsNotFilter(boolean isNotFilter) {
        this.isNotFilter = isNotFilter;
    }
    
    @Override
    public String toString(){
        return "name: " + name + "type: " + type + "range: " + range;
    }
    
    
    public static String[] localeFilterRange(){
        return new String[] {BetApps.LABLES.getString("everySame"), BetApps.LABLES.getString("fromTo")};
    }
    
    public static String[] localeFilterType(){
        return new String[] {BetApps.LABLES.getString("date"),
            BetApps.LABLES.getString("dayOfWeek"),
            BetApps.LABLES.getString("dayOfMonth"),
            BetApps.LABLES.getString("week"),
            BetApps.LABLES.getString("month"),
            BetApps.LABLES.getString("year")};
    }
    
    public static FilterRange localeValueOfRange(String s){
        if(s.equals(BetApps.LABLES.getString("everySame"))){
            return FilterRange.EVERY_SAME;
        } else {
            return FilterRange.FROM_TO;
        }
    }
    
    public static FilterType localeValueOfType(String s){
        if(s.equals(BetApps.LABLES.getString("date"))){
            return FilterType.DATE;
        } else if (s.equals(BetApps.LABLES.getString("dayOfWeek"))) {
            return FilterType.DAY_OF_WEEK;
        } else if (s.equals(BetApps.LABLES.getString("dayOfMonth"))) {
            return FilterType.DAY_OF_MONTH;
        } else if (s.equals(BetApps.LABLES.getString("week"))) {
            return FilterType.WEEK;
        } else if (s.equals(BetApps.LABLES.getString("month"))) {
            return FilterType.MONTH;
        } else {
            return FilterType.YEAR;
        }
    }
    
    
}
