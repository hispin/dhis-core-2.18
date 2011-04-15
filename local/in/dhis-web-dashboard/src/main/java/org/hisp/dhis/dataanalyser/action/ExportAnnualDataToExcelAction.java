/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.dataanalyser.action;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;

import com.keypoint.PngEncoder;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version ExportAnnualDataToExcelAction.java Dec 24, 2010 5:10:42 PM
 */
public class ExportAnnualDataToExcelAction
    implements Action
{

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    double[][] data1;

    double[][] numDataArray;

    double[][] denumDataArray;

    String[] series1;

    // String[] denumDataArray;

    String[] categories1;

    // String[] categories2;

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    /*
     * private String contentType;
     * 
     * public String getContentType() { return contentType; }
     */

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
     * private int bufferSize;
     * 
     * public int getBufferSize() { return bufferSize; }
     */

    private String viewSummary;

    public void setViewSummary( String viewSummary )
    {
        this.viewSummary = viewSummary;
    }

    private String chartDisplayOption;

    public void setChartDisplayOption( String chartDisplayOption )
    {
        this.chartDisplayOption = chartDisplayOption;
    }

    private String radioButtonValue;

    public void setRadioButtonValue( String radioButtonValue )
    {
        this.radioButtonValue = radioButtonValue;
    }

    private Integer selctedIndicatorId;

    public void setSelctedIndicatorId( Integer selctedIndicatorId )
    {
        this.selctedIndicatorId = selctedIndicatorId;
    }

    // private Indicator selectedIndicator;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        int tempCol1 = 0;
        int tempRow1 = 1;
        // int tempCol2 = 0;

        if ( radioButtonValue.equals( "indicator" ) )
        {
            System.out.println( "indicator" );
        }
        else
        {
            System.out.println( "dataelement" );
        }
        // System.out.println("Complete path is :" + radioButtonValue.equals(
        // "indicator" ) );

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );
        HttpSession session = req.getSession();
        BufferedImage chartImage = (BufferedImage) session.getAttribute( "chartImage" );
        PngEncoder encoder = new PngEncoder( chartImage, false, 0, 9 );

        byte[] encoderBytes = encoder.pngEncode();
        Double[][] objData1 = (Double[][]) session.getAttribute( "data1" );

        Double[][] objnumData1 = (Double[][]) session.getAttribute( "numServiceValues" );
        Double[][] objdenumData1 = (Double[][]) session.getAttribute( "denumServiceValues" );
        // Double[][] objData2 = (Double[][]) session.getAttribute( "data2" );

        String[] series1S = (String[]) session.getAttribute( "series1" );
        // String[] series2S = (String[]) session.getAttribute( "series2" );
        String[] categories1S = (String[]) session.getAttribute( "categories1" );
        // String[] categories2S = (String[]) session.getAttribute(
        // "categories2" );

        // initialzeAllLists(series1S, series2S, categories1S, categories2S);
        initialzeAllLists( series1S, categories1S );

        // if(objData1 == null || objData2 == null || series1 == null || series2
        // == null || categories1 == null || categories2 == null )
        if ( objData1 == null || series1 == null || categories1 == null || objnumData1 == null || objdenumData1 == null )
            System.out.println( "Session Objects are null" );
        else
            System.out.println( "Session Objects are not null" );

        data1 = convertDoubleTodouble( objData1 );

        numDataArray = convertDoubleTodouble( objnumData1 );
        denumDataArray = convertDoubleTodouble( objdenumData1 );

        // System.out.println(" Selected Indicator Id is : " +
        // selctedIndicatorId + " ,selected Indicator :" + selectedIndicator );

        // selectedIndicator = new Indicator();
        // selectedIndicator = indicatorService.getIndicator( selctedIndicatorId
        // );
        // String IndicatorName = selectedIndicator.getName();

        // System.out.println(" Selected Indicator Id is : " +
        // selctedIndicatorId + " ,selected Indicator :" + selectedIndicator );

        // data2 = convertDoubleTodouble( objData2 );

        if ( chartDisplayOption == null || chartDisplayOption.equalsIgnoreCase( "none" ) )
        {
        }
        // else if(chartDisplayOption.equalsIgnoreCase("ascend")) {
        // sortByAscending(); }
        // else if(chartDisplayOption.equalsIgnoreCase("desend")) {
        // sortByDesscending(); }
        // else if(chartDisplayOption.equalsIgnoreCase("alphabet")) {
        // sortByAlphabet(); }

        // File outputReportFile = locationManager.getFileForWriting(
        // UUID.randomUUID().toString() + ".xls", "db", "output" );

        // String outputReportFile = System.getProperty( "user.home" ) +
        // File.separator + "dhis" + File.separator +
        // configurationService.getConfigurationByKey(
        // Configuration_IN.KEY_REPORTFOLDER ).getValue()
        // + File.separator + "output" + File.separator +
        // UUID.randomUUID().toString() + ".xls";
        String outputReportFile = System.getenv( "DHIS2_HOME" ) + File.separator
            + configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue()
            + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        // System.out.println("Env Variable is  :" + System.getenv( "DHIS2_HOME"
        // ) );
        // System.out.println("Complete path is :" + outputReportFile );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportFile ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "ChartOutput", 0 );

        if ( viewSummary.equals( "no" ) )
        {
            WritableImage writableImage = new WritableImage( 0, 1, 10, 23, encoderBytes );
            sheet0.addImage( writableImage );
            tempRow1 = 24;
        }
        else
        {
            // tempRow1 -= objData1.length;
            tempRow1 = 0;
        }
        tempCol1 = 0;
        // tempCol2 = 0;
        tempRow1++;

        WritableCellFormat wCellformat1 = new WritableCellFormat();
        wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat1.setAlignment( Alignment.CENTRE );
        wCellformat1.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat1.setWrap( true );

        WritableCellFormat wCellformat2 = new WritableCellFormat();
        wCellformat2.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat2.setAlignment( Alignment.CENTRE );
        wCellformat2.setVerticalAlignment( VerticalAlignment.TOP );
        wCellformat2.setBackground( Colour.GRAY_25 );
        wCellformat2.setWrap( true );

        /*
         * WritableCellFormat wCellformat3 = new WritableCellFormat();
         * wCellformat3.setBorder( Border.ALL, BorderLineStyle.THIN );
         * wCellformat3.setAlignment( Alignment.CENTRE );
         * wCellformat3.setVerticalAlignment( VerticalAlignment.CENTRE );
         * wCellformat3.setBackground( Colour.BLACK ); wCellformat3.setWrap(
         * true ); // wCellformat3.setFont( WritableFont.BOLD );
         * //wCellformat3.WritableFont();
         */

        WritableFont wfobj2 = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wcf = new WritableCellFormat( wfobj2 );
        wcf.setBorder( Border.ALL, BorderLineStyle.THIN );
        wcf.setAlignment( Alignment.CENTRE );
        // wcf.setShrinkToFit(true);
        wcf.setVerticalAlignment( VerticalAlignment.CENTRE );
        wcf.setWrap( true );

        if ( radioButtonValue.equals( "indicator" ) )
        {
            System.out.println( "in excel indicator" );

            sheet0.addCell( new Label( tempCol1, tempRow1, "Years", wCellformat2 ) );
            sheet0.addCell( new Label( tempCol1 + 1, tempRow1, "", wCellformat2 ) );

            tempCol1++;

            // for time display
            for ( int i = 0; i < categories1.length; i++ )
            {
                sheet0.addCell( new Label( tempCol1 + 1, tempRow1, categories1[i], wCellformat2 ) );
                tempCol1++;
            }

            tempRow1 = tempRow1 + 1;

            int tempRowValue = 0;
            for ( int j = 0; j < series1.length; j++ )
            {
                tempCol1 = 0;
                sheet0.mergeCells( tempCol1, tempRow1, tempCol1, tempRow1 + 2 );
                sheet0.addCell( new Label( tempCol1, tempRow1, series1[j], wCellformat2 ) );

                int tempNumCol = 1;

                sheet0.addCell( new Label( tempNumCol, tempRow1, "Num", wCellformat2 ) );
                tempNumCol = tempNumCol + 1;
                for ( int k = 0; k < categories1.length; k++ )
                {
                    // tempCol1 = 0;
                    sheet0.addCell( new Number( tempNumCol, tempRow1, numDataArray[j][k], wCellformat1 ) );
                    tempNumCol++;
                }
                int tempDenumCol = 1;

                sheet0.addCell( new Label( tempDenumCol, tempRow1 + 1, "Den", wCellformat2 ) );

                tempDenumCol = tempDenumCol + 1;

                for ( int k = 0; k < categories1.length; k++ )
                {
                    // tempRow1 = 0;
                    sheet0.addCell( new Number( tempDenumCol, tempRow1 + 1, denumDataArray[j][k], wCellformat1 ) );
                    tempDenumCol++;
                }
                int tempValueCol = 1;

                sheet0.addCell( new Label( tempValueCol, tempRow1 + 2, "Val", wCellformat2 ) );
                tempValueCol = tempValueCol + 1;

                for ( int k = 0; k < categories1.length; k++ )
                {
                    // tempRow1 = 0;
                    sheet0.addCell( new Number( tempValueCol, tempRow1 + 2, data1[j][k], wcf ) );
                    tempValueCol++;
                }

                tempRow1 = tempRow1 + 3;
                tempRow1++;
                tempRowValue = tempRow1++;
            }

            tempRow1 = tempRowValue;

            tempRow1 = tempRow1 + 2;
            sheet0.addCell( new Label( tempCol1, tempRow1, "Indicators Names", wCellformat2 ) );

            sheet0.mergeCells( tempCol1 + 1, tempRow1, tempCol1 + 2, tempRow1 );
            sheet0.addCell( new Label( tempCol1 + 1, tempRow1, "Formula", wCellformat2 ) );

            sheet0.addCell( new Label( tempCol1 + 3, tempRow1, "Numerator DataElements", wCellformat2 ) );
            sheet0.addCell( new Label( tempCol1 + 4, tempRow1, "Denominator DataElements", wCellformat2 ) );

            tempRow1 = tempRow1 + 1;

            // for(int j=0; j< series1.length; j++)
            // {
            Indicator indicator = indicatorService.getIndicator( selctedIndicatorId );

            // Indicator indicator = indicatorService.getIndicatorByName(
            // IndicatorName );

            sheet0.addCell( new Label( tempCol1, tempRow1, indicator.getName(), wCellformat1 ) );
            String formula = indicator.getNumeratorDescription() + "/" + indicator.getDenominatorDescription();

            sheet0.addCell( new Label( tempCol1 + 1, tempRow1, formula, wCellformat1 ) );
            String factor = "X" + indicator.getIndicatorType().getFactor();

            sheet0.addCell( new Label( tempCol1 + 2, tempRow1, factor, wCellformat1 ) );
            sheet0.addCell( new Label( tempCol1 + 3, tempRow1, expressionService.getExpressionDescription( indicator
                .getNumerator() ), wCellformat1 ) );
            sheet0.addCell( new Label( tempCol1 + 4, tempRow1, expressionService.getExpressionDescription( indicator
                .getDenominator() ), wCellformat1 ) );

            tempRow1++;
            // }
        }

        else
        {
            System.out.println( "in DataElements Excel sheet" );

            sheet0.addCell( new Label( tempCol1, tempRow1, "Years", wCellformat2 ) );
            // tempCol1++;

            // for time display
            for ( int i = 0; i < categories1.length; i++ )
            {
                sheet0.addCell( new Label( tempCol1 + 1, tempRow1, categories1[i], wCellformat2 ) );
                tempCol1++;
            }

            tempRow1 = tempRow1 + 1;

            for ( int j = 0; j < series1.length; j++ )
            {
                tempCol1 = 0;
                // sheet0.mergeCells( tempCol1 , tempRow1, tempCol1, tempRow1+2
                // );
                sheet0.addCell( new Label( tempCol1, tempRow1, series1[j], wCellformat2 ) );

                tempCol1++;
                for ( int k = 0; k < categories1.length; k++ )
                {
                    sheet0.addCell( new Number( tempCol1, tempRow1, data1[j][k], wCellformat1 ) );
                    tempCol1++;
                }

                tempRow1++;
            }

        }

        /*
         * sheet0.addCell( new Label( tempCol1, tempRow1, "Indicators",
         * wCellformat2) ); sheet0.addCell( new Label( tempCol1+1, tempRow1, "",
         * wCellformat2) );
         * 
         * // System.out.println(tempCol1); tempCol1++;
         * 
         * //System.out.println(tempCol1); for(int i=0; i< categories1.length;
         * i++) { sheet0.addCell( new Label( tempCol1+1, tempRow1,
         * categories1[i], wCellformat2) ); tempCol1++; }
         * 
         * tempRow1 = tempRow1+1; for(int j=0; j< series1.length; j++) {
         * tempCol1 = 0; sheet0.mergeCells( tempCol1 , tempRow1, tempCol1,
         * tempRow1+2 ); sheet0.addCell( new Label( tempCol1, tempRow1,
         * series1[j], wCellformat2) );
         * 
         * int tempNumCol = 1;
         * 
         * sheet0.addCell( new Label( tempNumCol, tempRow1, "Num", wCellformat2)
         * ); tempNumCol = tempNumCol+1; for( int k=0;k<categories1.length;k++ )
         * { // tempCol1 = 0; sheet0.addCell( new Number( tempNumCol, tempRow1,
         * numDataArray[j][k], wCellformat1 ) ); tempNumCol++; } int
         * tempDenumCol = 1;
         * 
         * sheet0.addCell( new Label( tempDenumCol, tempRow1+1, "Den",
         * wCellformat2) );
         * 
         * tempDenumCol = tempDenumCol+1; for( int k=0;k<categories1.length;k++
         * ) { // tempRow1 = 0; sheet0.addCell( new Number( tempDenumCol,
         * tempRow1+1, denumDataArray[j][k], wCellformat1 ) ); tempDenumCol++; }
         * int tempValueCol = 1;
         * 
         * 
         * sheet0.addCell( new Label( tempValueCol, tempRow1+2, "Val",
         * wCellformat2) ); tempValueCol = tempValueCol+1;
         * 
         * for( int k=0;k<categories1.length;k++ ) { //tempRow1 = 0;
         * sheet0.addCell( new Number( tempValueCol, tempRow1+2, data1[j][k],
         * wcf ) ); tempValueCol++; }
         * 
         * tempRow1 = tempRow1+3; tempRow1++;
         * 
         * }
         */

        /*
         * 
         * sheet0.mergeCells( tempCol1 , tempRow1, tempCol1, tempRow1+1 );
         * sheet0.addCell( new Label( tempCol1, tempRow1, "Indicators",
         * wCellformat2) ); tempCol1++; tempCol2++; // sheet0.mergeCells(
         * tempCol1 , tempRow1, tempCol1 + 3, tempRow1 ); for(int i=0; i<
         * categories1.length; i++) { //cell1 = sheet0.getWritableCell(tempCol1,
         * tempRow1); // cellFormat1 = cell1.getCellFormat();
         * 
         * sheet0.mergeCells( tempCol1 , tempRow1, tempCol1 + 2, tempRow1 );
         * sheet0.addCell( new Label( tempCol1, tempRow1, categories1[i],
         * wCellformat2) );
         * 
         * // sheet0.mergeCells( tempCol1 , tempRow1, tempCol1 + 3, tempRow1 );
         * sheet0.addCell( new Label( tempCol1, tempRow1+1, "Num", wCellformat2)
         * ); sheet0.addCell( new Label( tempCol1+1, tempRow1+1, "Den",
         * wCellformat2) ); sheet0.addCell( new Label( tempCol1+2, tempRow1+1,
         * "Val", wCellformat2) );
         * 
         * tempCol1 = tempCol1+3; //tempCol2++; } tempRow1 = tempRow1+2; //
         * tempCol1 = 1;
         * 
         * tempCol1 = 0; for(int j=0; j< series1.length; j++) { tempCol1 = 0;
         * sheet0.addCell( new Label( tempCol1, tempRow1, series1[j],
         * wCellformat2) );
         * 
         * 
         * tempCol1++; for( int k=0;k<categories1.length;k++ ) {
         * //sheet0.addCell( new Number( tempColNo, tempRowNo,
         * Double.parseDouble( tempStr ), wCellformat ) ); sheet0.addCell( new
         * Number( tempCol1, tempRow1, numDataArray[j][k], wCellformat1 ) );
         * sheet0.addCell( new Number( tempCol1+1, tempRow1,
         * denumDataArray[j][k], wCellformat1 ) ); sheet0.addCell( new Number(
         * tempCol1+2, tempRow1, data1[j][k], wCellformat1 ) );
         * 
         * tempCol1 = tempCol1+3; }
         * 
         * tempRow1++; }
         */
        // 23/12/2010
        // tempCol1++;
        // cell1 = sheet0.getWritableCell(tempCol1, tempRow1);
        // cellFormat1 = cell1.getCellFormat();
        // sheet0.addCell( new Number( tempCol1, tempRow1, data1[j][k],
        // wCellformat1 ) );
        // sheet0.addCell( new Number( tempCol1, tempRow1, ""+data1[j][k],
        // wCellformat1) );
        // tempCol1++;

        /*
         * int count1 = 0; int count2 = 0; int flag1 = 0; while(count1 <=
         * categories1.length) { for(int j=0;j<data1.length;j++) { tempCol1 = 1;
         * tempRow1++; WritableCellFormat wCellformat1 = new
         * WritableCellFormat(); wCellformat1.setBorder( Border.ALL,
         * BorderLineStyle.THIN ); wCellformat1.setWrap( true );
         * 
         * WritableCellFormat wCellformat2 = new WritableCellFormat();
         * wCellformat2.setBorder( Border.ALL, BorderLineStyle.THIN );
         * wCellformat2.setAlignment( Alignment.CENTRE );
         * wCellformat2.setBackground( Colour.GRAY_25 ); wCellformat2.setWrap(
         * true );
         * 
         * 
         * WritableCell cell1; CellFormat cellFormat1;
         * 
         * for(int k=count2;k<count1;k++) { if(k==count2 && j==0) { tempCol1 =
         * 0; tempRow1++; cell1 = sheet0.getWritableCell(tempCol1, tempRow1);
         * cellFormat1 = cell1.getCellFormat();
         * 
         * 
         * if (cell1.getType() == CellType.LABEL) { Label l = (Label) cell1;
         * l.setString("Service"); l.setCellFormat( cellFormat1 ); } else {
         * sheet0.addCell( new Label( tempCol1, tempRow1, "Indicators",
         * wCellformat2) ); } tempCol1++;
         * 
         * for(int i=count2; i< count1; i++) { cell1 =
         * sheet0.getWritableCell(tempCol1, tempRow1); cellFormat1 =
         * cell1.getCellFormat(); if (cell1.getType() == CellType.LABEL) { Label
         * l = (Label) cell1; l.setString(categories1[i]); l.setCellFormat(
         * cellFormat1 ); } else { sheet0.addCell( new Label( tempCol1,
         * tempRow1, categories1[i], wCellformat2) ); } tempCol1++; }
         * tempRow1++; tempCol1 = 1; }
         * 
         * 
         * if(k==count2) { tempCol1 = 0; cell1 =
         * sheet0.getWritableCell(tempCol1, tempRow1); cellFormat1 =
         * cell1.getCellFormat();
         * 
         * if (cell1.getType() == CellType.LABEL) { Label l = (Label) cell1;
         * l.setString(series1[j]); l.setCellFormat( cellFormat1 ); } else {
         * sheet0.addCell( new Label( tempCol1, tempRow1, series1[j],
         * wCellformat2) ); } tempCol1++; } cell1 =
         * sheet0.getWritableCell(tempCol1, tempRow1); cellFormat1 =
         * cell1.getCellFormat();
         * 
         * if (cell1.getType() == CellType.LABEL) { Label l = (Label) cell1;
         * l.setString(""+data1[j][k]); l.setCellFormat( cellFormat1 ); } else {
         * sheet0.addCell( new Number( tempCol1, tempRow1, data1[j][k],
         * wCellformat1 ) ); //sheet0.addCell( new Number( tempCol1, tempRow1,
         * ""+data1[j][k], wCellformat1) ); } tempCol1++; } } if(flag1 == 1)
         * break; count2 = count1; if( (count1+10 > categories1.length) &&
         * (categories1.length - count1 <= 10)) { count1 += categories1.length -
         * count1; flag1 = 1; } else count1 += 10; }
         */
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "Chart Output.xls";

        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        return SUCCESS;
    }

    // public void initialzeAllLists(String[]series1S, String[] series2S,
    // String[] categories1S, String[] categories2S)
    public void initialzeAllLists( String[] series1S, String[] categories1S )
    {
        int i;
        series1 = new String[series1S.length];
        // series2 = new String[series2S.length];
        categories1 = new String[categories1S.length];
        // categories2 = new String[categories2S.length];

        for ( i = 0; i < series1S.length; i++ )
        {
            series1[i] = series1S[i];
        }

        for ( i = 0; i < categories1S.length; i++ )
        {
            categories1[i] = categories1S[i];
        }

    }

    public double[][] convertDoubleTodouble( Double[][] objData )
    {
        // System.out.println("Before Sorting : ");
        double[][] data = new double[objData.length][objData[0].length];
        for ( int i = 0; i < objData.length; i++ )
        {
            for ( int j = 0; j < objData[0].length; j++ )
            {
                data[i][j] = objData[i][j].doubleValue();
                // System.out.print(categories1[j]+": "+data[i][j]+", ");
            }
            // System.out.println("");
        }

        return data;
    }// convertDoubleTodouble end

}// class end

