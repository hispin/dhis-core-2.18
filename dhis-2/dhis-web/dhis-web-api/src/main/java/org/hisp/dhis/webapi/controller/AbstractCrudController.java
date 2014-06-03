package org.hisp.dhis.webapi.controller;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import com.google.common.collect.Lists;
import org.hisp.dhis.acl.Access;
import org.hisp.dhis.acl.AclService;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.dxf2.filter.FilterService;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.hibernate.exception.CreateAccessDeniedException;
import org.hisp.dhis.hibernate.exception.DeleteAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.node.NodeHint;
import org.hisp.dhis.node.NodeService;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.controller.exception.NotFoundException;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractCrudController<T extends IdentifiableObject>
{
    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    @Autowired
    protected IdentifiableObjectManager manager;

    @Autowired
    protected CurrentUserService currentUserService;

    @Autowired
    protected FilterService filterService;

    @Autowired
    protected AclService aclService;

    @Autowired
    protected SchemaService schemaService;

    @Autowired
    protected LinkService linkService;

    @Autowired
    protected RenderService renderService;

    @Autowired
    protected ImportService importService;

    @Autowired
    protected NodeService nodeService;

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getObjectList(
        @RequestParam Map<String, String> parameters, Model model, HttpServletResponse response, HttpServletRequest request )
    {
        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();
        List<T> entityList = getEntityList( metaData, options );
        String viewClass = options.getViewClass( "basic" );

        postProcessEntities( entityList );
        postProcessEntities( entityList, options, parameters );

        ReflectionUtils.invokeSetterMethod( schemaService.getSchema( getEntityClass() ).getPlural(), metaData, entityList );

        if ( viewClass.equals( "basic" ) )
        {
            handleLinksAndAccess( options, metaData, entityList, false );
        }
        else
        {
            handleLinksAndAccess( options, metaData, entityList, true );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", viewClass );

        return StringUtils.uncapitalize( getEntitySimpleName() ) + "List";
    }

    @RequestMapping( method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE } )
    public void getObjectListJson(
        @RequestParam( required = false ) String include,
        @RequestParam( required = false ) String exclude,
        @RequestParam( value = "filter", required = false ) List<String> filters,
        @RequestParam Map<String, String> parameters, Model model, HttpServletResponse response, HttpServletRequest request ) throws IOException
    {
        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();

        Schema schema = schemaService.getSchema( getEntityClass() );

        boolean hasPaging = options.hasPaging();

        // get full list if we are using filters
        if ( filters != null && !filters.isEmpty() )
        {
            options.getOptions().put( "links", "false" );

            if ( options.hasPaging() )
            {
                hasPaging = true;
                options.getOptions().put( "paging", "false" );
            }
        }

        List<T> entityList = getEntityList( metaData, options );

        // enable object filter
        if ( filters != null && !filters.isEmpty() )
        {
            entityList = filterService.filterObjects( entityList, filters );

            if ( hasPaging )
            {
                Pager pager = new Pager( options.getPage(), entityList.size(), options.getPageSize() );
                metaData.setPager( pager );
                entityList = PagerUtils.pageCollection( entityList, pager );
            }
        }

        postProcessEntities( entityList );
        postProcessEntities( entityList, options, parameters );

        response.setContentType( MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8" );

        ReflectionUtils.invokeSetterMethod( schemaService.getSchema( getEntityClass() ).getPlural(), metaData, entityList );

        if ( include != null && include.contains( "access" ) )
        {
            options.getOptions().put( "viewClass", "sharing" );
        }

        if ( options.getViewClass( "basic" ).equals( "basic" ) )
        {
            handleLinksAndAccess( options, metaData, entityList, false );
        }
        else
        {
            handleLinksAndAccess( options, metaData, entityList, true );
        }

        // enable property filter
        if ( include != null || exclude != null )
        {
            RootNode rootNode = new RootNode( "metadata" );

            if ( hasPaging )
            {
                ComplexNode pagerNode = rootNode.addNode( new ComplexNode( "pager" ) );
                pagerNode.addNode( new SimpleNode( "page", metaData.getPager().getPage() ) );
                pagerNode.addNode( new SimpleNode( "pageCount", metaData.getPager().getPageCount() ) );
                pagerNode.addNode( new SimpleNode( "total", metaData.getPager().getTotal() ) );
                pagerNode.addNode( new SimpleNode( "nextPage", metaData.getPager().getNextPage() ) );
                pagerNode.addNode( new SimpleNode( "prevPage", metaData.getPager().getPrevPage() ) );
            }

            rootNode.addNode( filterService.filterProperties( getEntityClass(), entityList, include, exclude ) );

            // response.setContentType( MediaType.APPLICATION_XML_VALUE );
            // nodeService.serialize( rootNode, MediaType.APPLICATION_XML_VALUE, response.getOutputStream() );
            nodeService.serialize( rootNode, MediaType.APPLICATION_JSON_VALUE, response.getOutputStream() );
        }
        else
        {
            renderService.toJson( response.getOutputStream(), metaData, JacksonUtils.getViewClass( options.getViewClass( "basic" ) ) );
        }
    }


    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getObject( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        T entity = getEntity( uid );

        if ( entity == null )
        {
            throw new NotFoundException( uid );
        }

        if ( options.hasLinks() )
        {
            linkService.generateLinks( entity );
        }

        if ( aclService.isSupported( getEntityClass() ) )
        {
            addAccessProperties( entity );
        }

        postProcessEntity( entity );
        postProcessEntity( entity, options, parameters );

        model.addAttribute( "model", entity );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            throw new CreateAccessDeniedException( "You don't have the proper permissions to create this object." );
        }

        T parsed = renderService.fromXml( request.getInputStream(), getEntityClass() );
        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.CREATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            throw new CreateAccessDeniedException( "You don't have the proper permissions to create this object." );
        }

        T parsed = renderService.fromJson( request.getInputStream(), getEntityClass() );
        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.CREATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE } )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream
        input ) throws Exception
    {
        T object = getEntity( uid );

        if ( object == null )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), object ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        T parsed = renderService.fromXml( request.getInputStream(), getEntityClass() );
        ((BaseIdentifiableObject) parsed).setUid( uid );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream
        input ) throws Exception
    {
        T object = getEntity( uid );

        if ( object == null )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), object ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        T parsed = renderService.fromJson( request.getInputStream(), getEntityClass() );
        ((BaseIdentifiableObject) parsed).setUid( uid );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    //--------------------------------------------------------------------------
    // DELETE
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws
        Exception
    {
        T object = getEntity( uid );

        if ( !aclService.canDelete( currentUserService.getCurrentUser(), object ) )
        {
            throw new DeleteAccessDeniedException( "You don't have the proper permissions to delete this object." );
        }

        manager.delete( object );
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------

    /**
     * Override to process entities after it has been retrieved from
     * storage and before it is returned to the view. Entities is null-safe.
     */

    protected void postProcessEntities( List<T> entityList, WebOptions options, Map<String, String> parameters )
    {

    }

    /**
     * Override to process entities after it has been retrieved from
     * storage and before it is returned to the view. Entities is null-safe.
     */

    protected void postProcessEntities( List<T> entityList )
    {

    }

    /**
     * Override to process a single entity after it has been retrieved from
     * storage and before it is returned to the view. Entity is null-safe.
     */
    protected void postProcessEntity( T entity ) throws Exception
    {
    }

    /**
     * Override to process a single entity after it has been retrieved from
     * storage and before it is returned to the view. Entity is null-safe.
     */
    protected void postProcessEntity( T entity, WebOptions options, Map<String, String> parameters ) throws Exception
    {
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    protected List<T> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<T> entityList;

        if ( options.getOptions().containsKey( "query" ) )
        {
            entityList = Lists.newArrayList( manager.filter( getEntityClass(), options.getOptions().get( "query" ) ) );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = Lists.newArrayList( manager.getBetween( getEntityClass(), pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = Lists.newArrayList( manager.getAllSorted( getEntityClass() ) );
        }

        return entityList;
    }

    protected T getEntity( String uid )
    {
        return manager.getNoAcl( getEntityClass(), uid ); //TODO consider ACL
    }

    protected void addAccessProperties( T object )
    {
        Access access = new Access();
        access.setManage( aclService.canManage( currentUserService.getCurrentUser(), object ) );
        access.setExternalize( aclService.canExternalize( currentUserService.getCurrentUser(), object.getClass() ) );
        access.setWrite( aclService.canWrite( currentUserService.getCurrentUser(), object ) );
        access.setRead( aclService.canRead( currentUserService.getCurrentUser(), object ) );
        access.setUpdate( aclService.canUpdate( currentUserService.getCurrentUser(), object ) );
        access.setDelete( aclService.canDelete( currentUserService.getCurrentUser(), object ) );

        ((BaseIdentifiableObject) object).setAccess( access );
    }

    protected void handleLinksAndAccess( WebOptions options, WebMetaData metaData, List<T> entityList, boolean deep )
    {
        if ( options != null && options.hasLinks() )
        {
            linkService.generateLinks( metaData );
        }

        if ( !JacksonUtils.isSharingView( options.getViewClass( "basic" ) ) )
        {
            return;
        }

        if ( entityList != null && aclService.isSupported( getEntityClass() ) )
        {
            for ( T object : entityList )
            {
                addAccessProperties( object );
            }
        }
    }

    //--------------------------------------------------------------------------
    // Reflection helpers
    //--------------------------------------------------------------------------

    private Class<T> entityClass;

    private String entityName;

    private String entitySimpleName;

    @SuppressWarnings( "unchecked" )
    protected Class<T> getEntityClass()
    {
        if ( entityClass == null )
        {
            Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
            entityClass = (Class<T>) actualTypeArguments[0];
        }

        return entityClass;
    }

    protected String getEntityName()
    {
        if ( entityName == null )
        {
            entityName = getEntityClass().getName();
        }

        return entityName;
    }

    protected String getEntitySimpleName()
    {
        if ( entitySimpleName == null )
        {
            entitySimpleName = getEntityClass().getSimpleName();
        }

        return entitySimpleName;
    }

    @SuppressWarnings( "unchecked" )
    protected T getEntityInstance()
    {
        try
        {
            return (T) Class.forName( getEntityName() ).newInstance();
        }
        catch ( InstantiationException ex )
        {
            throw new RuntimeException( ex );
        }
        catch ( IllegalAccessException ex )
        {
            throw new RuntimeException( ex );
        }
        catch ( ClassNotFoundException ex )
        {
            throw new RuntimeException( ex );
        }
    }
}
