package org.hisp.dhis.webapi.controller;

import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.translation.TranslationService;
import org.hisp.dhis.translation.Translations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hisp.dhis.webapi.utils.ContextUtils.CONTENT_TYPE_JSON;

/**
 * Created by kprakash on 05/05/14.
 */

@Controller
@RequestMapping(value = TranslationController.RESOURCE_PATH)
public class TranslationController
{
    public static final String RESOURCE_PATH = "/translations";

    @Autowired
    private ContextUtils contextUtils;


    @Autowired
    private TranslationService translationService;


    @RequestMapping( produces = CONTENT_TYPE_JSON, method = RequestMethod.GET )
    public void exportJson( HttpServletResponse response ) throws IOException
    {
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CacheStrategy.NO_CACHE, "translations.json", false );
        JacksonUtils.toJson( response.getOutputStream(), new Translations( translationService.getAllTranslations() ) );
    }

    @RequestMapping(method = RequestMethod.POST, consumes = CONTENT_TYPE_JSON)
    public void importJson( HttpServletRequest request ) throws IOException
    {
        Translations translations = JacksonUtils.fromJson( request.getInputStream(), Translations.class );
        translationService.createOrUpdate( translations.getTranslations() );
    }

}