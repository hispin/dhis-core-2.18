package org.hisp.dhis.api.view;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author mortenoh
 */
public class Jaxb2View extends AbstractView
{
    public static final String DEFAULT_CONTENT_TYPE = "application/xml";

    public Jaxb2View()
    {
        setContentType( DEFAULT_CONTENT_TYPE );
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        response.setContentType( getContentType() );
        model = ViewUtils.filterModel( model );

        Object domainModel = model.get( "model" );

        if ( domainModel == null )
        {
            // TODO throw exception
        }

        OutputStream outputStream = response.getOutputStream();
        JAXBContext context = JAXBContext.newInstance( domainModel.getClass() );
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );

        marshaller.marshal( domainModel, outputStream );

/*
        Marshaller.Listener listener = new IdentifiableObjectListener(request);
        marshaller.setListener(listener);

        marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
                "\n<?xml-stylesheet type=\"text/xsl\" href=\"dhis-web-api/xslt/chart.xslt\"?>\n");
*/
    }
}
