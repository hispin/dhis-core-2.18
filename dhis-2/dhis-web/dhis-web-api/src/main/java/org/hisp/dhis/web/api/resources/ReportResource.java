package org.hisp.dhis.web.api.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.web.api.ResponseUtils;

@Path( "/report/{report}/{orgUnit}" )
public class ReportResource
{
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nManager i18nManager;

    public void setI18nManager( I18nManager manager )
    {
        i18nManager = manager;
    }
    
    @GET
    @Produces(ContextUtils.CONTENT_TYPE_PDF)
    public Response renderReport( @PathParam("report") final Integer reportId,
        @PathParam("orgUnit") final String orgUnitUuid )
            throws Exception
    {
        final Report report = reportService.getReport( reportId );
        
        final OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUuid );
        
        if ( report == null || organisationUnit == null )
        {
            return null;
        }
        
        final int organisationUnitId = organisationUnit.getId();
        
        final I18nFormat format = i18nManager.getI18nFormat();
        
        final String filename = CodecUtils.filenameEncode( report.getName() ) + "." + ReportService.REPORTTYPE_PDF;
        
        return ResponseUtils.response( true, filename, false ).entity( new StreamingOutput()
        {
            public void write( OutputStream out )
                throws IOException, WebApplicationException
            {
                reportService.renderReport( out, report, new Date(), organisationUnitId, ReportService.REPORTTYPE_PDF, format );
            }
        } ).build();
    }
}
