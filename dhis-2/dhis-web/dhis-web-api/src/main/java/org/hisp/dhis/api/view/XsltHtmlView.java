package org.hisp.dhis.api.view;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class XsltHtmlView extends AbstractUrlBasedView
{
    public static final String HTML_CONTENT_TYPE = "text/html";
    
    public XsltHtmlView()
    {
        setContentType( HTML_CONTENT_TYPE );
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        response.setContentType( getContentType() );
        model = ViewUtils.filterModel( model );

        Object domainModel = model.get( "model" );

        if ( domainModel == null )
        {
            // TODO throw exception
        }

        JAXBContext context = JAXBContext.newInstance( domainModel.getClass() );
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );

        Source xmlSource = new JAXBSource( context, domainModel );

        Transformer transformer = TransformCacheImpl.instance().getHtmlTransformer();

        OutputStream output = response.getOutputStream();

        // pass on any parameters set in xslt-params
        Map<String, String> params = (Map<String, String>) model.get( "xslt-params" );
        if ( params != null )
        {
            for ( Map.Entry<String, String> entry : params.entrySet() )
            {
                transformer.setParameter( entry.getKey(), entry.getValue() );
            }
        }

        transformer.transform( xmlSource, new StreamResult( output ) );

    }
}
