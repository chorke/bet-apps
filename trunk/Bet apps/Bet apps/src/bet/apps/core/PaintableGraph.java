
package bet.apps.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chorke
 */
public class PaintableGraph {
    private Graph graph;
    
    private List<Point> points;
    private Dimension dimension;
    private double maxValue;
    private boolean paintable = true;
    private boolean needResize = true;
    
    //private int widthOfLeftMargin = 0;
    public static final int SIZE_OF_POINT = 3;


    public PaintableGraph() {
        graph = new Graph();
        points = new ArrayList<>();
        dimension = new Dimension(0,0);
        maxValue = 0.0;
    }

    private void resizeGraph(int widthOfLeftMargin){
        if(needResize){
            double xScale = ((dimension.width - widthOfLeftMargin) - (1 / (points.size() + 1))) 
                    / (points.size() + 1);
            double yScale =  getMaximalUsableHeight(dimension.height) / maxValue;
            for(int i = 0; i < points.size(); i++){
                Point p = points.get(i);
                p.x = widthOfLeftMargin + (int)Math.round(i * xScale);
                double dist = graph.getValues().get(i) * yScale;
                p.y = dimension.height/2 - (int)Math.round(dist);
            }
        }
    }
    
    public static int getMaximalUsableHeight(int height){
        return ((3 * height)/8);
    }
    
    public void paintGraph(Graphics g, Dimension dim, Color color, int widthOfLeftMargin, double maxValue){
//        List<Point> points = graph.getPoints();
        setDimension(dim);
        setMaxValue(maxValue);
        resizeGraph(widthOfLeftMargin);
        int numOfPoints = points.size();
        int[] xPoints = new int[numOfPoints];
        int[] yPoints = new int[numOfPoints];
        for(int i = 0; i < numOfPoints; i++){
            xPoints[i] = points.get(i).x;
            yPoints[i] = points.get(i).y;
        }
        paintLinesOfGraph(g, xPoints, yPoints);
        paintPointsOfGraph(g, xPoints, yPoints, color);
    }

    private void paintPointsOfGraph(Graphics g, int[] xPoints, int[] yPoints, Color color){
        g.setColor(color);
        for(int i = 0; i < xPoints.length; i++){
            g.fillRect(xPoints[i] - SIZE_OF_POINT, yPoints[i] - SIZE_OF_POINT,
                    SIZE_OF_POINT * 2, SIZE_OF_POINT * 2);
        }
    }

    private void paintLinesOfGraph(Graphics g, int[] xPoints, int[] yPoints){
        g.setColor(Color.BLACK);
        g.drawPolyline(xPoints, yPoints, xPoints.length);
    }
    
    public void addPoint(double value){
        addPointAt(value, -1);
    }
    
    public void addPointAt(double value, int position){
        graph.addValueAt(value, position);
        int x = 0;
        if(points.size() > 0){
            if(position < 0){
                x = points.get(points.size() - 1).x + 10;
            } else {
                movePoints(position);
                x = position * 10;
            }
        }
        int y = (int)Math.round(value);
        points.add(new Point(x, y));
        
    }
    
    private void movePoints(int startIndex){
        for(int i = startIndex; i < points.size(); i++){
            points.get(i).x += 10;
        }
    }
    
    public void setGraph(Graph g){
        points.clear();
        for(double d : g.getValues()){
            addPoint(d);
        }
        this.graph = g;
        needResize = true;
    }
    
    public double getMaxAbsValue(){
        return graph.getMaxAbsValue();
    }
    
    public List<Point> getPoints() {
        return points;
    }

    public Graph getGraph() {
        return graph;
    }

    public double getActualMaxValue() {
        return maxValue;
    }

    public Dimension getActualDimension() {
        return dimension;
    }

    public boolean isPaintAble() {
        return paintable;
    }

    public void setDimension(Dimension actualDimension) {
        if(!this.dimension.equals(actualDimension)){
            this.dimension = actualDimension;
            needResize = true;
        }
    }

    public void setMaxValue(double actualMaxValue) {
        if(this.maxValue != actualMaxValue){
            this.maxValue = actualMaxValue;
            needResize = true;
        }
    }

    public void setPaintAble(boolean isPaintAble) {
        this.paintable = isPaintAble;
    }
}
