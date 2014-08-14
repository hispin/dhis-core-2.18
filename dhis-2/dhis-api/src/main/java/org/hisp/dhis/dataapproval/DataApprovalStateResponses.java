package org.hisp.dhis.dataapproval;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;

import java.util.ArrayList;
import java.util.List;

public class DataApprovalStateResponses
{
    List<DataApprovalStateResponse> dataApprovalStateResponses = new ArrayList<>();

    public DataApprovalStateResponses()
    {
    }

    @JsonProperty
    public List<DataApprovalStateResponse> getDataApprovalStateResponses()
    {
        return dataApprovalStateResponses;
    }

    public void setDataApprovalStateResponses( List<DataApprovalStateResponse> dataApprovalStateResponses )
    {
        this.dataApprovalStateResponses = dataApprovalStateResponses;
    }

    public void add( DataApprovalStateResponse dataApprovalStateResponse )
    {
        dataApprovalStateResponses.add( dataApprovalStateResponse );
    }
}
