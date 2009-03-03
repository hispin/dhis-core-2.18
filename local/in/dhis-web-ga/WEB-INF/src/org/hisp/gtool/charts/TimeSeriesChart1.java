package org.hisp.gtool.charts;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;

public class TimeSeriesChart1 {

//	Row Keys
	String[] series;
	// Column Keys
	String[] categories;
	// data...	
	double[][] data;
	
	int[] monthValues;
	int[] yearValues;
	
	String chartTitle;
	
	//	constructor
	public TimeSeriesChart1() { }

	
	private TimeSeriesCollection  getDataset() 
	{    
		
		//	create the dataset...
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		
		//DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series.length;i++)
		{
			final TimeSeries seriesTemp = new TimeSeries(series[i], Month.class);

			for(int j=0;j<categories.length;j++)
			{
				seriesTemp.add(new Month(monthValues[j], yearValues[j]), data[i][j]);
				//dataset.addValue(data[i][j], series[i], categories[j]);
			}
			dataset.addSeries(seriesTemp);
		}
		
		
        
        

		return dataset;
	}

 public String getChartViewer(HttpServletRequest request, HttpServletResponse response) {
	 TimeSeriesCollection dataset = getDataset();
	 // create the chart...
	 
	
	 final JFreeChart chart = ChartFactory.createTimeSeriesChart(
	            "",
	            "Time",
	            "Value",
	            dataset,
	            true,
	            true,
	            false
	        );
	 
     /* TITLE */
     final TextTitle mainTitle = new TextTitle("Time Series");
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
     
	        final XYPlot plot = chart.getXYPlot();
	        final DateAxis axis = (DateAxis) plot.getDomainAxis();
	        axis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 1,
	                                          new SimpleDateFormat("MMM-yyyy")));
	        axis.setVerticalTickLabels(true);
	        
	        final StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
	        renderer.setPlotLines(true);
	        renderer.setSeriesShapesFilled(0, Boolean.TRUE);
	        renderer.setSeriesShapesFilled(1, Boolean.FALSE);




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


public int[] getMonthValues() {
	return monthValues;
}


public void setMonthValues(int[] monthValues) {
	this.monthValues = monthValues;
}


public int[] getYearValues() {
	return yearValues;
}


public void setYearValues(int[] yearValues) {
	this.yearValues = yearValues;
}
	

}
