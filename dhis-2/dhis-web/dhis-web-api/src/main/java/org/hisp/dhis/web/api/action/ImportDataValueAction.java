package org.hisp.dhis.web.api.action;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import com.opensymphony.xwork2.ActionSupport;

public class ImportDataValueAction
    extends ActionSupport
    implements ServletRequestAware, ServletResponseAware
{
    // Dependencies
    private ProgramStageInstanceService programStageInstanceService;

    private PatientDataValueService patientDataValueService;

    private DataElementService dataElementService;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private OrganisationUnitService orgUnitService;

    private DataElementCategoryService dataElementCategoryService;

    // Setter and Getter
    @Override
    public void setServletResponse( HttpServletResponse response )
    {
        this.response = response;

    }

    @Override
    public void setServletRequest( HttpServletRequest request )
    {
        this.request = request;

    }

    public HttpServletRequest getServletRequest()
    {
        return request;
    }

    public HttpServletResponse getServletResponse()
    {
        return response;
    }

    public ProgramStageInstanceService getProgramStageInstanceService()
    {
        return programStageInstanceService;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    public PatientDataValueService getPatientDataValueService()
    {
        return patientDataValueService;
    }

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    public DataElementService getDataElementService()
    {
        return dataElementService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public OrganisationUnitService getOrgUnitService()
    {
        return orgUnitService;
    }

    public void setOrgUnitService( OrganisationUnitService orgUnitService )
    {
        this.orgUnitService = orgUnitService;
    }

    public DataElementCategoryService getDataElementCategoryService()
    {
        return dataElementCategoryService;
    }

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // Output
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
    }

    @Override
    public String execute()
        throws Exception
    {
        String message = "Upload Successfully!";
        request = ServletActionContext.getRequest();
        response = ServletActionContext.getResponse();
        this.setInputStream( new ByteArrayInputStream( message.getBytes() ) );
        
        InputStream clientInput = request.getInputStream();
        DataInputStream dis = new DataInputStream( clientInput );
        
        if ( clientInput.available() > -1 )
        {
            int numOfDataValue = dis.readInt();
            OrganisationUnit orgUnit = orgUnitService.getOrganisationUnit( dis.readInt() );
            this.setInputStream( new ByteArrayInputStream( message.getBytes() ) );
            try
            {
                for ( int i = 0; i < numOfDataValue; i++ )
                {
                    this.saveDataValue( dis, orgUnit );
                }
            }
            catch ( Exception ex )
            {
                message = "Upload fail!";
                this.setInputStream( new ByteArrayInputStream( message.getBytes() ) );
            }
        }
        return SUCCESS;
    }

    private void saveDataValue( DataInputStream dis, OrganisationUnit orgUnit ) throws IOException

    {
            DataElement dataElement = dataElementService.getDataElement( dis.readInt() );
            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( dis
                .readInt() );
            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                .getDataElementCategoryOptionCombo( 1 );

            PatientDataValue patientDataValue = new PatientDataValue();
            patientDataValue.setDataElement( dataElement );
            patientDataValue.setOptionCombo( optionCombo );
            patientDataValue.setOrganisationUnit( orgUnit );
            patientDataValue.setProgramStageInstance( programStageInstance );
            patientDataValue.setTimestamp( new Date() );
            patientDataValue.setProvidedByAnotherFacility( false );
            patientDataValue.setValue( dis.readUTF() );

            patientDataValueService.savePatientDataValue( patientDataValue );
        }


    }


