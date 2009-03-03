package org.hisp.gtool.charts;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;

public class CombinedCategoryPlotChart1 {

//	 Data Related Bar Chart
	//	Row Keys
	String[] series1;
	// Column Keys
	String[] categories1;
	// data...	
	double[][] data1;
	
	String chartTitle;
	String xAxis_Title;
	String yAxis_Title;
	
	// Data Related Line Chart
	/*
	 String[] series2;
	 // Column Keys
	  String[] categories2;	
	// data...	
	double[][] data2;
	*/
	
	//	constructor
	public CombinedCategoryPlotChart1() { }

	// Function For Creating Dataset for Barchart
	private DefaultCategoryDataset  getDataset() 
	{    
		
		//	create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series1.length;i++)
		{
			for(int j=0;j<categories1.length;j++)
			{
				dataset.addValue(data1[i][j], series1[i], categories1[j]);
			}
		}
		
		return dataset;
	}
	
	/*
	// Function For Creating Dataset for Linechart
	private DefaultCategoryDataset  getDataset() 
	{    		
		//	create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series1.length;i++)
		{
			for(int j=0;j<categories1.length;j++)
			{
				dataset.addValue(data1[i][j], series1[i], categories1[j]);
			}
		}		
		return dataset;
	}
	*/
	
public String getChartViewer(HttpServletRequest request, HttpServletResponse response) {
    
	
	final CategoryDataset dataset1 = getDataset();
    final NumberAxis rangeAxis1 = new NumberAxis(yAxis_Title);
    rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis1.setUpperMargin(0.15);
    rangeAxis1.setLowerMargin(0.15);
    
    final LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
    renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    renderer1.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer1.setItemLabelsVisible(true);
    final CategoryPlot subplot1 = new CategoryPlot(dataset1, null, rangeAxis1, renderer1);
    subplot1.setDomainGridlinesVisible(true);
    
    final CategoryDataset dataset2 = getDataset();
    final NumberAxis rangeAxis2 = new NumberAxis(yAxis_Title);
    rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis2.setUpperMargin(0.30);
    rangeAxis1.setLowerMargin(0.30);
    
    final BarRenderer renderer2 = new BarRenderer();
    renderer2.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    renderer2.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer2.setItemLabelsVisible(true);
    final CategoryPlot subplot2 = new CategoryPlot(dataset2, null, rangeAxis2, renderer2);    
    subplot2.setDomainGridlinesVisible(true);

    final CategoryAxis domainAxis = new CategoryAxis(xAxis_Title);
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    
    final CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainAxis);
    plot.add(subplot1, 2);
    plot.add(subplot2, 1);
   
    
    

    final JFreeChart chart = new JFreeChart(
        "",
        new Font("SansSerif", Font.BOLD, 12),
        plot,
        true
    );
	 
	 /* TITLE */
		final TextTitle mainTitle = new TextTitle("Combined Category Plot Chart");
		mainTitle.setFont(new Font("times", Font.BOLD, 13));
		mainTitle.setPosition(RectangleEdge.TOP);     
		mainTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		chart.addSubtitle(mainTitle);	 
    
    /* SUB TITLE */     
	     final TextTitle subtitle = new TextTitle(chartTitle);
	     subtitle.setFont(new Font("times", Font.BOLD, 13));
	     subtitle.setPosition(RectangleEdge.TOP);     
	     subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
	     chart.addSubtitle(subtitle);    
   
    // OPTIONAL CUSTOMISATION COMPLETED.


  ChartRenderingInfo info = null;
  HttpSession session = request.getSession();
  try {

    //Create RenderingInfo object
    response.setContentType("text/html");
    info = new ChartRenderingInfo(new StandardEntityCollection());
    BufferedImage chartImage = chart.createBufferedImage(800, 500, info);

    // putting chart as BufferedImage in session, 
    // thus making it available for the image reading action Action.
    session.setAttribute("chartImage", chartImage);

    PrintWriter writer = new PrintWriter(response.getWriter());
    
    
    ChartUtilities.writeImageMap(writer,"imageMap",info,true);
    
    
    writer.flush();
  
  }
  catch (Exception e) {
     // handel your exception here
  }
 
  String pathInfo = "http://";
  pathInfo += request.getServerName();
  int port = request.getServerPort();
  pathInfo += ":"+String.valueOf(port);
  pathInfo += request.getContextPath();
  String chartViewer = pathInfo + "/servlet/ChartViewer";
  return chartViewer;
}



public String getChartTitle() {
	return chartTitle;
}

public void setChartTitle(String chartTitle) {
	this.chartTitle = chartTitle;
}

public String[] getCategories1() {
	return categories1;
}

public void setCategories1(String[] categories1) {
	this.categories1 = categories1;
}


public double[][] getData1() {
	return data1;
}

public void setData1(double[][] data1) {
	this.data1 = data1;
}

public String[] getSeries1() {
	return series1;
}

public void setSeries1(String[] series1) {
	this.series1 = series1;
}

public String getXAxis_Title() {
	return xAxis_Title;
}

public void setXAxis_Title(String axis_Title) {
	xAxis_Title = axis_Title;
}

public String getYAxis_Title() {
	return yAxis_Title;
}

public void setYAxis_Title(String axis_Title) {
	yAxis_Title = axis_Title;
}



	
}
