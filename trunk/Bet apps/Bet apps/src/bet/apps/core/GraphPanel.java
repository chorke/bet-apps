
package bet.apps.core;

import bet.apps.windows.GraphWin;
import bet.apps.windows.MainWin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Chorke
 */
public class GraphPanel extends JPanel{

    private static final Font FONT = new Font("Lucida Console", Font.PLAIN, 10);
    
    private List<PaintableGraph> graphs;
    private Dimension dimOfGraph;
    private Dimension dimOfLegend;
    private int widthOfLeftMargin = 0;
    private int sizeOfPointOfGraph = PaintableGraph.SIZE_OF_POINT;
    
    public GraphPanel(){
        this.graphs = new ArrayList<>();
        this.dimOfGraph = new Dimension(0,0);
        this.dimOfLegend = new Dimension(0,0);
    }
    
//    public static GraphPanel getNewGraphPanel(){
//        GraphPanel gp = new GraphPanel();
//        
//        return gp;
//    }

    public void setDimOfGraph(Dimension dimOfGraph) {
        this.dimOfGraph = dimOfGraph;
    }

    public void setDimOfLegend(Dimension dimOfLegend) {
        this.dimOfLegend = dimOfLegend;
    }

    public void setWidthOfLeftMargin(int widthOfLeftMargin) {
        this.widthOfLeftMargin = widthOfLeftMargin;
    }

    public void setPaintableGraphs(List<PaintableGraph> graphs) {
        this.graphs = graphs;
    }
    
    public void setGraphs(List<Graph> graphs){
        this.graphs.clear();
        for(Graph g : graphs){
            PaintableGraph pg = new PaintableGraph();
            pg.setGraph(g);
            this.graphs.add(pg);
        }
    }
    
    @Override
    public void paint(Graphics g){
        double maxAbsValue = getMaxAbsValue();
        drawBackground(g, maxAbsValue);
        drawLegend(g);
        if(graphs != null){
            for(int i = 0; i < graphs.size(); i++){
                if(graphs.get(i).isPaintAble()){
                    graphs.get(i).paintGraph(g,
                            dimOfGraph,
                            GraphWin.graphsColors[i],
                            widthOfLeftMargin,
                            maxAbsValue);
                }
            }
        }
    }

    private double getMaxAbsValue(){
        double max = 0.0;
        if(graphs != null){
            for(int i = 0; i < graphs.size(); i++){
                double j = graphs.get(i).getMaxAbsValue();
                if(j > max){
                    max = j;
                }
            }
        }
        return max;
    }

    private void drawBackground(Graphics g, double maxAbsValueOnYAxis){
        int middleLine = dimOfGraph.height/2;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, dimOfGraph.width, dimOfGraph.height);
        int leftSide = widthOfLeftMargin - sizeOfPointOfGraph;
        g.setColor(new Color(0, 255, 0, 40));
        g.fillRect(leftSide, 0, dimOfGraph.width - leftSide, middleLine);
        g.setColor(new Color(255, 0, 0, 30));
        g.fillRect(leftSide, middleLine, dimOfGraph.width - leftSide, dimOfGraph.height - middleLine);
        g.setColor(Color.BLACK);
        g.drawLine(0, middleLine, dimOfGraph.width, middleLine);
        g.drawLine(0, middleLine - 1, dimOfGraph.width, middleLine - 1);

        g.drawLine(leftSide, 0, leftSide, dimOfGraph.height);

        int numOfLedgerLines = (2 * PaintableGraph.getMaximalUsableHeight(dimOfGraph.height)) / 60;
        double oneStep = (maxAbsValueOnYAxis / numOfLedgerLines);
        g.setFont(FONT);
        for(int i = 1; i < (numOfLedgerLines + 2); i++){
            g.drawLine(widthOfLeftMargin - 5, middleLine + 30 * i, dimOfGraph.width, middleLine + 30 * i);
            g.drawLine(widthOfLeftMargin - 5, middleLine - 30 * i, dimOfGraph.width, middleLine - 30 * i);
            double j = Math.round(i * oneStep);
            g.drawString("-" + j, 5, middleLine + 30 * i);
            g.drawString("+" + j, 5, middleLine - 30 * i);
        }
    }

    private void drawLegend(Graphics g){
        if(graphs != null && graphs.size() > 0){
            g.setColor(Color.WHITE);
            g.fillRect(0, dimOfGraph.height, dimOfLegend.width, dimOfLegend.width);
            g.setColor(Color.BLACK);
            g.drawLine(0, dimOfGraph.height, dimOfLegend.width, dimOfGraph.height);
            g.drawLine(0, dimOfGraph.height + 1, dimOfLegend.width, dimOfGraph.height + 1);
            g.drawLine(0, dimOfGraph.height + 2, dimOfLegend.width, dimOfGraph.height + 2);
            int space = dimOfLegend.height / (graphs.size() + 1);
                for(int i = 0; i < graphs.size(); i++){
                    if(graphs.get(i).isPaintAble()){
                    int h = space * (i + 1)+ dimOfGraph.height;
                    int w = widthOfLeftMargin + 40;
                    g.setColor(Color.BLACK);
                    g.drawString(MainWin.textForStat[i], w + 50, h);
                    g.drawLine(w, h, w + 30, h);
                    g.setColor(GraphWin.graphsColors[i]);
                    g.fillRect(w - sizeOfPointOfGraph, h - sizeOfPointOfGraph,
                            sizeOfPointOfGraph * 2, sizeOfPointOfGraph * 2);
                    g.fillRect(w + 30 - sizeOfPointOfGraph, h - sizeOfPointOfGraph,
                            sizeOfPointOfGraph * 2, sizeOfPointOfGraph * 2);
                }
            }
        }
    }
}