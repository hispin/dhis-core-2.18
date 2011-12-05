package org.hisp.dhis.api.view;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.hisp.dhis.api.utils.WebLinkPopulatorListener;

public class Jaxb2Utils
{

    public static Marshaller createMarshaller( Object domainModel, HttpServletRequest request )
        throws JAXBException, PropertyException
    {
        JAXBContext context = JAXBContext.newInstance( domainModel.getClass() );
        
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
        WebLinkPopulatorListener listener = new WebLinkPopulatorListener( request );
        marshaller.setListener( listener );
        return marshaller;
    }

}
