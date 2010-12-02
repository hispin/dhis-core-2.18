package org.hisp.dhis.web.api.model;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;

public class DataSetValueTest
{

    static JAXBContext JC;
    String s = "<dataSetValue completed=\"true\" periodName=\"10-2010\" id=\"54\" name=\"Handicaped Data Set -mobile\"><dataValue categoryOptComboID=\"1\" id=\"1743\" value=\"12\" /></dataSetValue>";

    @BeforeClass
    public static void init() throws JAXBException {
        JC = JAXBContext.newInstance( DataSetValue.class );
    }
    
    @Test
    public void unmarshall()
        throws JAXBException
    {
        Unmarshaller unmarshaller = JC.createUnmarshaller();
        DataSetValue dataSetValue = (DataSetValue) unmarshaller.unmarshal( new StringReader( s ) );

        assertEquals( 54, dataSetValue.getId() );
        List<DataValue> dataValues = dataSetValue.getDataValues();
        assertNotNull(dataValues);
        assertEquals( 1, dataValues.size() );
        assertEquals( 1743, dataValues.get( 0 ).getId() );
    }

    @Test
    public void marshall()
        throws JAXBException
    {
        Unmarshaller unmarshaller = JC.createUnmarshaller();
        DataSetValue dataSetValue = (DataSetValue) unmarshaller.unmarshal( new StringReader( s ) );

        Marshaller marshaller = JC.createMarshaller();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal( dataSetValue, stringWriter );
        String xml = stringWriter.toString();
        System.out.println(xml);
        assertTrue( xml.contains( "name=\"Handicaped Data Set -mobile\"" ) );
        assertTrue( xml.contains( "id=\"1743\"" ) );
    }

    
}
