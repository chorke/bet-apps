package bet.apps.core;

public class Match {
    
    private double bet1, bet2, bet0;
    private int winner;
    private MyDate date;

    public Match(double bet0, double bet1, double bet2, int winner, MyDate date) {
        this.bet0 = bet0;
        this.bet1 = bet1;
        this.bet2 = bet2;
        this.winner = winner;
        this.date = date;
    }
    
    public double getBet0() {
        return bet0;
    }
    
    public double getBet1() {
        return bet1;
    }

    public double getBet2() {
        return bet2;
    }

    public int getWinner() {
        return winner;
    }
    
    public int getHashCodeByDay(){
        return 10000 * date.getYear() + 100 * date.getMonth() + date.getDay();
    }
    
    public int getHashCodeByWeek(){
        return 10 * date.getYear() + date.getWeek();
    }
    
    public int getHashCodeByMonth(){
        return 10 * date.getYear() + date.getMonth();
    }
    
    public int getHashCodeByYear(){
        return date.getYear();
    }

    public MyDate getDate() {
        return date;
    }
    
    @Override
    public String toString(){
        return bet1 + "\t" + bet0 + "\t" + bet2 + "\t" + winner + "\t" + date;
    }
}