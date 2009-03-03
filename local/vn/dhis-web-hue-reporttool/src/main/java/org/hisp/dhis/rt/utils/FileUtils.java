package org.hisp.dhis.rt.utils;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.opensymphony.webwork.ServletActionContext;

/**
 * @author Lars Helge Overland
 * @author Nguyen Dang Quang
 * @version $Id: FileUtils.java 2871 2007-02-20 16:04:11Z andegje $
 */
public class FileUtils
{
    /**
     * Creates the dhis directory tree with the user.home directory as root. Valid 
     * directories are "dhis", "rt", "design", "xml", "jrxml", "reports", "pdf" and "html".
     * @return Map with the paths to all directories
     */
    public static Map getOutputPath()
    {
        Map<String, String> paths = new HashMap<String, String>();
        
        String dhis_dir = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator;
        String rt_dir = dhis_dir + "rt" + File.separator;
        String design_dir = rt_dir + "design" + File.separator;
        String xml_dir = design_dir + "xml" + File.separator;
        String jrxml_dir = design_dir + "jrxml" + File.separator;
        String reports_dir = rt_dir + "reports" + File.separator;
        String pdf_dir = reports_dir + "PDF" + File.separator;
        String html_dir = reports_dir + "HTML" + File.separator;
        
        paths.put( "dhis", dhis_dir );
        paths.put( "rt", rt_dir );
        paths.put( "design", design_dir );
        paths.put( "xml", xml_dir );
        paths.put( "jrxml", jrxml_dir );
        paths.put( "reports", reports_dir );
        paths.put( "pdf", pdf_dir );
        paths.put( "html", html_dir );
        
        File dhisDir = new File( dhis_dir );
        if ( !dhisDir.isDirectory() )
        {
            dhisDir.mkdir();
        }
        File rtDir = new File( rt_dir );
        if ( !rtDir.isDirectory() )
        {
            rtDir.mkdir();
        }
        File designDir = new File( design_dir );
        if ( !designDir.isDirectory() )
        {
            designDir.mkdir();
        }
        File xmlDir = new File( xml_dir );
        if ( !xmlDir.isDirectory() )
        {
            xmlDir.mkdir();
        }
        File jrxmlDir = new File( jrxml_dir );
        if ( !jrxmlDir.isDirectory() )
        {
            jrxmlDir.mkdir();
        }
        File reportsDir = new File( reports_dir );
        if ( !reportsDir.isDirectory() )
        {
            reportsDir.mkdir();
        }
        File pdfDir = new File( pdf_dir );
        if ( !pdfDir.isDirectory() )
        {
            pdfDir.mkdir();
        }
        File htmlDir = new File( html_dir );
        if ( !htmlDir.isDirectory() )
        {
            htmlDir.mkdir();
        }
        
	checkXmlFile( xml_dir + "Hue_bieu_CoverSignature.xml" );
		
        checkXmlFile( xml_dir + "Hue_bieu_1.xml" );
        
        checkXmlFile( xml_dir + "Hue_bieu_2.xml" );
		
        checkXmlFile( xml_dir + "Hue_bieu_3.xml" );
		
        checkXmlFile( xml_dir + "Hue_bieu_4.xml" );
		
        checkXmlFile( xml_dir + "Hue_bieu_5.xml" );
		
	checkXmlFile( xml_dir + "Hue_bieu_7.xml" );
		
        checkXmlFile( xml_dir + "Hue_bieu_9.xml" );
        
        checkXmlFile( xml_dir + "Hue_bieu_10.xml" );

        checkXmlFile( xml_dir + "Hue_bieu_11.xml" );
 
        checkXmlFile( xml_dir + "Hue_bieu_12.xml" );
		
	checkXmlFile( xml_dir + "Hue_bieu_13.xml" );
 
        checkXmlFile( xml_dir + "Hue_bieu_14.xml" );
		
        return paths;
    }
    
    /**
     * Creates a valid Java class name by removing illegal characters.
     * @param input Input string
     * @return Valid Java class name string
     */
    public static String getQualifiedFileName( String name )
    {    
        String pattern = "[ (){}\\[\\]\\'\\+\\*\\/\\-\\\\]";
        String replacement = "";        
        name = name.replaceAll( pattern, replacement );        
        
        return name;
    }
    
    /**
     * Gets the extension of a file.
     * @param file The file
     * @return The extension of the file
     */
    public static String getExtension( File file )
    {
        if ( file.exists() )
        {
            String path = file.getName();
            return path.substring( path.lastIndexOf( "." ) + 1, path.length() );
        }
        
        return null;
    }
    
    /**
     * Gets the extension of a file.
     * @param fileName The file name
     * @return The extension of the file
     */
    public static String getExtension( String fileName )
    {
        return fileName.substring( fileName.lastIndexOf( "." ) + 1, fileName.length() );
    }
    
    /**
     * Gets the basename of a file.
     * @param file The file
     * @return The basename of the file
     */
    public static String getBaseName( File file )
    {
        if ( file.exists() )
        {
            String path = file.getName();
            return path.substring( 0 , path.lastIndexOf( "." ) );
        }
        
        return null;
    }
    
    /**
     * Gets the basename of a file.
     * @param file The file name
     * @return The basename of the file
     */    
    public static String getBaseName( String fileName )
    {
        return fileName.substring( 0 , fileName.lastIndexOf( "." ) );
    }
    
    /**
     * Gets the path to the report xml file
     * @param name The file name
     * @return Path to the report xml file
     */
    public static String getXmlFileName( String name )
    {
        return getOutputPath().get( "xml" ).toString() + getQualifiedFileName( name ) + ".xml";
    }
    
    /**
     * Gets the path to the report jrxml file
     * @param name The file name
     * @return Path to the report jrxml file
     */
    public static String getJRXmlFileName( String name )
    {
        return getOutputPath().get( "jrxml" ).toString() + getQualifiedFileName( name ) + ".jrxml";
    }
    
    /*
     * Viet Nam only
     */
    public static InputStream getHcmcJRXmlFileAsStream( String kind, int page )
    {
        String path = "/dhis-web-reporttool/template/vietnam/" + kind + "/" + "Trang" + "_" + page + ".jrxml";
        ServletContext context = ServletActionContext.getServletContext();
        return context.getResourceAsStream( path );
    }
  
    public static InputStream getHcmcJasperFileAsStream( String kind, int page )
    {
        String path = "/dhis-web-reporttool/template/hcmc/" + kind + "/" + "Trang" + "_" + page + ".jasper";

        ServletContext context = ServletActionContext.getServletContext();
        System.out.println("chay toi day ko?");
        InputStream abc = context.getResourceAsStream( path );
        System.out.println("Sau khi doc jasper file");
        if (abc != null)System.out.println("stream is not null");
        return abc;
    }

    private static void makeXmlFile( String outputXmlFilePath )
    {
        String xmlFileName = outputXmlFilePath.substring(outputXmlFilePath.lastIndexOf(File.separator) + 1, outputXmlFilePath.length());
        
        try
        {
            Writer out = new OutputStreamWriter( new FileOutputStream( outputXmlFilePath, true ), "UTF8" );
            InputStreamReader streamReader = new InputStreamReader( getXmlFile( xmlFileName ), "UTF8" );
            BufferedReader bufferedReader = new BufferedReader( streamReader );

            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ( (line = bufferedReader.readLine()) != null )
            {
                buffer.append( line + "\n" );
            }

            out.write( buffer.toString() );
            out.flush();
            out.close();
        }
        catch ( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private static InputStream getXmlFile( String fileName )
    {
        String path = "/dhis-web-reporttool/template/vietnam/xml/" + fileName;
        ServletContext context = ServletActionContext.getServletContext();
        return context.getResourceAsStream( path );
    }

    public static InputStream getSignatureJrxmlAsStream()
    {
        String path = "/dhis-web-reporttool/template/vietnam/TrangChuKy.jrxml";
        ServletContext context = ServletActionContext.getServletContext();
        return context.getResourceAsStream( path );
    }

    private static void checkXmlFile( String xmlPath )
    {
        File xml_file = new File( xmlPath );
        if ( !xml_file.exists() )
        {
            makeXmlFile( xmlPath );
        }
    }

}



