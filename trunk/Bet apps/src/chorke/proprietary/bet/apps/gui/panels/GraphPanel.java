
package chorke.proprietary.bet.apps.gui.panels;

import chorke.proprietary.bet.apps.core.graphs.Graph;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import javax.swing.JPanel;

/**
 * Vykresľuje jeden graf na JPanel.
 * 
 * @author Chorke
 */
public class GraphPanel extends JPanel{
    
    private static final int X_STEP = 25;
    
    private Graph graph;
    private Color dotsColor = Color.BLACK;
    private int graphWidth = 0;
    private Insets borders = new Insets(30, 50, 30, 35);
    
    private int zeroLine;
    private BigDecimal yStep;
    
    public GraphPanel(Graph graph) {
        setGraph(graph);
    }

    public final void setGraph(Graph graph){
        if(graph == null){
            this.graph = new Graph();
        } else {
            this.graph = graph.getDeepClone();
        }
        graphWidth = Math.max(getPreferredSize().width, X_STEP * this.graph.valuesCount() 
                + borders.left + borders.right);
        setMinimumSize(getMinimumSize());
        setMaximumSize(getMaximumSize());
        setPreferredSize(getPreferredSize());
        setYStepAndZeroLine();
    }

    @Override
    public void setMaximumSize(Dimension maximumSize) {
        super.setMaximumSize(new Dimension(Math.max(maximumSize.width, graphWidth), maximumSize.height));
        setYStepAndZeroLine();
    }

    @Override
    public void setMinimumSize(Dimension minimumSize) {
        super.setMinimumSize(new Dimension(Math.max(minimumSize.width, graphWidth), minimumSize.height));
        setYStepAndZeroLine();
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(new Dimension(Math.max(d.width, graphWidth), d.height));
        setYStepAndZeroLine();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(Math.max(width, graphWidth), height);
        setYStepAndZeroLine();
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(new Dimension(Math.max(preferredSize.width, graphWidth), preferredSize.height));
        setYStepAndZeroLine();
    }
    
    public void setDotsColor(Color dotsColor) {
        this.dotsColor = dotsColor;
    }
    
    @Override
    public void paint(Graphics g) {
        paintBackground(g);
        paintGraph(g, graph);
    }
    
    private void paintGraph(Graphics g, Graph gr){
        MathContext zeroContext = new MathContext(0);
        List<BigDecimal> values = gr.getValues();
        int[] xPoints = new int[values.size()];
        int[] yPoints = new int[values.size()];
        int x = borders.left;
        int i = 0;
        for(BigDecimal val : values){
            xPoints[i] = x;
            yPoints[i] = zeroLine 
                    - val.multiply(yStep).round(zeroContext).intValue();
            x += X_STEP;
            i++;
        }
        g.drawPolyline(xPoints, yPoints, i);
        g.setColor(dotsColor);
        for(int j = 0; j < i; j++){
            g.fillOval(xPoints[j] - 4, yPoints[j] - 4, 8, 8);
        }
    }
    
    private void paintBackground(Graphics g){
        super.paint(g);
        int height = getPreferredSize().height;
        
        g.setColor(new Color(0, 255, 0, 50));
        g.fillRect(0, 0, graphWidth, zeroLine);
        g.setColor(new Color(255, 0, 0, 50));
        g.fillRect(0, zeroLine, graphWidth, height - zeroLine);
        
        g.setColor(Color.BLACK);
        g.fillRect(0, zeroLine, graphWidth, 2);
        g.fillRect(borders.left, 0, 2, height);
        
        g.setColor(new Color(120, 120, 120));
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        boolean nextStep = true;
        int linesCount = 1;
        while(nextStep){
            int lineY = Math.round(height * 0.1f * linesCount);
            BigDecimal label = new BigDecimal(lineY).divide(yStep, new MathContext(2));
            
            nextStep = false;
            int yPos = zeroLine - lineY;
            if(yPos >= 10){
                g.drawLine(borders.left - 5, yPos, graphWidth, yPos);
                g.drawString(label.toPlainString()+ " %", 5, yPos + 5);
                nextStep = true;
            }
            yPos = zeroLine + lineY;
            if(yPos <= height - 10){
                g.drawLine(borders.left - 5, yPos, graphWidth, yPos);
                g.drawString(label.negate().toPlainString()+ " %", 5, yPos + 5);
                nextStep = true;
            }
            linesCount++;
        }
    }
    
    /**
     * Nastaví krok pre os Y a pozíciu nulovej čiary.
     */
    private void setYStepAndZeroLine(){
        float topSpace = 0.1f;
        float bottomSpace = 0.1f;
        int height = getPreferredSize().height;
        
        if(graph.getMax() != null){
            yStep = graph.getMax().abs().add(graph.getMin().abs());
        } else {
            yStep = new BigDecimal("200");
        }
        yStep = new BigDecimal(Math.round(height * (1 - topSpace - bottomSpace)))
                .divide(yStep, 50, BigDecimal.ROUND_CEILING);
        if(graph.getMin() == null){
            zeroLine = height / 2;
        }else if(graph.getMin().compareTo(BigDecimal.ZERO) >= 0){
            zeroLine = height;
        } else if(graph.getMax().compareTo(BigDecimal.ZERO) > 0){
            BigDecimal bd = yStep.multiply(graph.getMin().abs());
            zeroLine = height - bd.round(new MathContext(0)).intValue() - Math.round(height * topSpace);
        }
    }
}
