package org.hisp.dhis.api.converter;

import org.hisp.dhis.api.resources.XIdentifiableObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

public class Jaxb2HttpMessageConverter implements HttpMessageConverter<XIdentifiableObject>
{
    @Override
    public boolean canRead( Class<?> clazz, MediaType mediaType )
    {
        System.err.println("canread");
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canWrite( Class<?> clazz, MediaType mediaType )
    {
        System.err.println("canwriter");
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MediaType> getSupportedMediaTypes()
    {
        System.err.println("getsupportedmediatypes");
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public XIdentifiableObject read( Class<? extends XIdentifiableObject> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException
    {
        System.err.println("read");
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void write( XIdentifiableObject identifiableObject, MediaType contentType, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException
    {
        System.err.println("write");
    }
}
