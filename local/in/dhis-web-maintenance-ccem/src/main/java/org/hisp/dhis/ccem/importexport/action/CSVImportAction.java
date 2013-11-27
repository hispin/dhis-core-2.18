package org.hisp.dhis.ccem.importexport.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.csvreader.CsvReader;
import com.opensymphony.xwork2.Action;

public class CSVImportAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
     
    private ModelTypeAttributeService modelTypeAttributeService;
    
    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }

    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }
    
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private File upload;

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private String fileFormat;

    public void setFileFormat( String fileFormat )
    {
        this.fileFormat = fileFormat;
    }

    private String message = "";

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        message += "<br><font color=blue>Importing StartTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font><br>";

        try
        {
            ZipInputStream zis = new ZipInputStream( new FileInputStream( upload ) );
            
            String uncompressedFolderPath = unZip( zis  );
            
            Map<String, List<String>> lookupDataMap = getLookupData( uncompressedFolderPath );
            
            /**
             * TODO - Need to use parameter / constant for timebeing directly used name
             */
            ModelType refrigeratorModel = modelTypeService.getModelTypeByName( "Refrigerator Catalog" );
            
            importRefrigeratorCatalogData( uncompressedFolderPath, refrigeratorModel );
            
            /*
            for( String lookupKey : lookupDataMap.keySet() )
            {
                //System.out.println( "******************** "+ lookupKey + " ***************");
                for( String lookupVal : lookupDataMap.get( lookupKey ) )
                {
                    System.out.println( lookupVal );
                }
            }
            */
            
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red><strong>Please check the file format : " + fileName + "<br>Detailed Log Message: " + e.getMessage() + "</font></strong>";
        }

        message += "<br><br><font color=blue>Importing EndTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font>";

        return SUCCESS;
    }

    public void importRefrigeratorCatalogData( String refrigeratorCatalogDataCSVFilePath, ModelType refrigeratorModel )
    {
        refrigeratorCatalogDataCSVFilePath += File.separator + "RefrigeratorCatalog.csv";
        
        try
        {
            CsvReader csvReader = new CsvReader( refrigeratorCatalogDataCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            
            csvReader.readHeaders();
            
            Map<String, ModelTypeAttribute> modelTypeAttributeMap = new HashMap<String, ModelTypeAttribute>();
            String headers[] = csvReader.getHeaders();            
            for( int i = 0; i < headers.length; i++ )
            {
                ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttributeByDescription( headers[i] );
                //Model model = modelTypeAttributeService.getM.getModelByDescription( headers[i] );
                modelTypeAttributeMap.put( headers[i], modelTypeAttribute );
            }
            
            Integer colCount = headers.length;
            
            while( csvReader.readRecord() )
            {
                
                String catalogId = csvReader.get( "CatalogID" );
                String modelName = csvReader.get( "ModelName" );
                String manufacturer = csvReader.get( "Manufacturer" );
                String refPowerSource = csvReader.get( "RefPowerSource" );
                String refType = csvReader.get( "RefType" );
                String climateZone = csvReader.get( "ClimateZone" );
                String dataSource = csvReader.get( "DataSource" );
                String refGrossVolume = csvReader.get( "RefGrossVolume" );
                String refNetVolume = csvReader.get( "RefNetVolume" );
                String freezeGrossVolume = csvReader.get( "FreezeGrossVolume" );
                String freezeNetVolume = csvReader.get( "FreezeNetVolume" );
                
                
                Model model = new Model();
                model.setName( catalogId + " + " + modelName );
                model.setDescription( catalogId + " + " + modelName );
                model.setModelType( refrigeratorModel );
                
                List<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>();
                
                for( int i = 0; i < headers.length; i++ )
                {
                    ModelTypeAttribute modelTypeAttribute = modelTypeAttributeMap.get( headers[i] );
                    
                    if ( modelTypeAttribute != null )
                    {
                        ModelAttributeValue modelAttributeValue = new ModelAttributeValue();
                        modelAttributeValue.setModel( model );
                        modelAttributeValue.setModelTypeAttribute( modelTypeAttribute );
                        modelAttributeValue.setValue( csvReader.get( headers[i] ) );
                        
                        modelAttributeValues.add( modelAttributeValue );
                        
                    }
                }
                
                // -------------------------------------------------------------------------
                // Save model
                // -------------------------------------------------------------------------
                    
                Integer id = modelService.createModel(  model, modelAttributeValues );

            }                                                    

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> getLookupData( String lookupsCSVFilePath )
    {
        Map<String, List<String>> lookupDataMap = new HashMap<String, List<String>>();
        
        lookupsCSVFilePath += File.separator + "Lookups.csv";
        
        try
        {
            //InputStream in = new FileInputStream( lookupsCSVFilePath );
            
            CsvReader csvReader = new CsvReader( lookupsCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            //CsvReader csvReader = new CsvReader( in, Charset.forName( "UTF-8" ) );
            
            csvReader.readHeaders();
            
            String headers[] = csvReader.getHeaders();
            
            Integer colCount = headers.length;
            
            System.out.println( colCount );
            
            while( csvReader.readRecord() )
            {
                System.out.println( csvReader.get(0) );
                for( int i = 0; i < colCount; i++ )
                {
                    List<String> tempList = lookupDataMap.get( headers[i] );
                    if( tempList == null )
                    {
                        tempList = new ArrayList<String>();                        
                    }
                    tempList.add( csvReader.get( headers[i] ) );
                    lookupDataMap.put( headers[i], tempList );
                }
            }
            
            csvReader.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return lookupDataMap;
    }
         
    public String unZip( ZipInputStream zis  )
    {
        byte[] buffer = new byte[1024];
        
        String outputReportPath = "";

        try
        {
            outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  "temp";

            File newdir = new File( outputReportPath );
            if( !newdir.exists() )
            {
                newdir.mkdirs();
            }
            outputReportPath += File.separator + UUID.randomUUID().toString();
           
            //ZipInputStream zis = new ZipInputStream( new FileInputStream( zipFile ) );

            ZipEntry ze = zis.getNextEntry();

            while ( ze != null )
            {
                String individualFileName = ze.getName();
                File newFile = new File( outputReportPath + File.separator + individualFileName );

                System.out.println( "file unzip : " + newFile.getAbsoluteFile() );

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File( newFile.getParent() ).mkdirs();

                FileOutputStream fos = new FileOutputStream( newFile );

                int len;
                while ( (len = zis.read( buffer )) > 0 )
                {
                    fos.write( buffer, 0, len );
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            
            zis.close();
        }
        catch ( IOException ex )
        {
            ex.printStackTrace();
        }
        
        return outputReportPath;
    }
}
