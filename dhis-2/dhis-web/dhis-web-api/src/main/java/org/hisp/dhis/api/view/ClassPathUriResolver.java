package org.hisp.dhis.api.view;

import org.springframework.core.io.ClassPathResource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ClassPathUriResolver implements URIResolver
{
    private String templatePath = "/templates/";

    public ClassPathUriResolver()
    {

    }

    public ClassPathUriResolver( String templatePath )
    {
        this.templatePath = templatePath;
    }

    public String getTemplatePath()
    {
        return templatePath;
    }

    public void setTemplatePath( String templatePath )
    {
        this.templatePath = templatePath;
    }

    @Override
    public Source resolve( String href, String base ) throws TransformerException
    {
        String url = getTemplatePath() + href;
        ClassPathResource classPathResource = new ClassPathResource( url );

        if ( !classPathResource.exists() )
        {
            throw (new TransformerException( "Resource " + url + " does not exist in classpath." ));
        }

        Source source = null;

        try
        {
            source = new StreamSource( classPathResource.getInputStream() );
        }
        catch ( IOException e )
        {
            throw (new TransformerException( "IOException while reading " + url + "." ));
        }

        return source;
    }
}
