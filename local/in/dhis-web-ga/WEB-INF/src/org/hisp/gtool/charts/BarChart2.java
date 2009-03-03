package org.hisp.gtool.charts;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;

public class BarChart2 {

//	Row Keys
	String[] series;
	// Column Keys
	String[] categories;
	// data...	
	double[][] data;
	
	String chartTitle;
	String xAxis_Title;
	String yAxis_Title;
	
	
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


	public String getChartTitle() {
		return chartTitle;
	}


	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}


	//	constructor
	public BarChart2() { }

	
	private DefaultCategoryDataset  getDataset() 
	{    
		
		//	create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series.length;i++)
		{
			for(int j=0;j<categories.length;j++)
			{
				dataset.addValue(data[i][j], series[i], categories[j]);
			}					
		}				
		
		return dataset;
	}

 public String getChartViewer(HttpServletRequest request, HttpServletResponse response) {
	 DefaultCategoryDataset dataset = getDataset();
     // create the chart...
     final JFreeChart chart = ChartFactory.createBarChart(
         "",       // chart title
         "Category",               // domain axis label
         "Value",                  // range axis label
         dataset,                  // data
         PlotOrientation.HORIZONTAL, // orientation
         true,                    // include legend
         true,                     // tooltips?
         false                     // URLs?
     );

     // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

     // set the background color for the chart...
     //chart.setBackgroundPaint(Color.lightGray);
     
     /* TITLE */
     final TextTitle mainTitle = new TextTitle("BarChart");
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
     

     // get a reference to the plot for further customisation...
     final CategoryPlot plot = chart.getCategoryPlot();
    // plot.setBackgroundPaint(Color.white);
    // plot.setDomainGridlinePaint(Color.lightGray);
    // plot.setRangeGridlinePaint(Color.lightGray);
     
     // set the range axis to display integers only...
     final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
     rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
     rangeAxis.setUpperMargin(0.15);
     rangeAxis.setLowerMargin(0.15);
     rangeAxis.setLabel(yAxis_Title);
     
     // disable bar outlines...
     final CategoryItemRenderer renderer = plot.getRenderer();
     renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
     renderer.setItemLabelsVisible(true); 
     
     final CategoryAxis domainAxis = plot.getDomainAxis();
     domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
     domainAxis.setLabel(xAxis_Title);
     
     
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


public String[] getCategories() {
	return categories;
}


public void setCategories(String[] categories) {
	this.categories = categories;
}


public double[][] getData() {
	return data;
}


public void setData(double[][] data) {
	this.data = data;
}


public String[] getSeries() {
	return series;
}


public void setSeries(String[] series) {
	this.series = series;
}
	

}
