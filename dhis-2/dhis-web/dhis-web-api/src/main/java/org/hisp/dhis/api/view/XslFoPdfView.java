package org.hisp.dhis.api.view;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class XslFoPdfView extends AbstractUrlBasedView
{
    public static final String PDF_CONTENT_TYPE = "application/pdf";

    public XslFoPdfView()
    {
        setContentType( PDF_CONTENT_TYPE );
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        response.setContentType( getContentType() );
        model = ViewUtils.filterModel( model );

        Object domainModel = model.get( "model" );

        if ( domainModel == null )
        {
            // throw exception
        }

        JAXBContext context = JAXBContext.newInstance( domainModel.getClass() );
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );

        Source xmlSource = new JAXBSource( context, domainModel );

        Transformer transformer = TransformCacheImpl.instance().getFopTransformer();

        FopFactory fopFactory = FopFactory.newInstance();
        Fop fop = fopFactory.newFop( MimeConstants.MIME_PDF, response.getOutputStream() );

        Result result = new SAXResult( fop.getDefaultHandler() );
        transformer.transform( xmlSource, result );
    }
}
