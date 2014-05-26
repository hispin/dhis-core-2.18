
dhis2.util.namespace( 'dhis2.appr' );

dhis2.appr.currentPeriodOffset = 0;
dhis2.appr.permissions = null;

$( function() {
	dhis2.appr.displayCategoryOptionGroups();
} );

//------------------------------------------------------------------------------
// Report
//------------------------------------------------------------------------------

dhis2.appr.dataSetSelected = function()
{
	dhis2.appr.displayPeriods();
}

dhis2.appr.orgUnitSelected = function( orgUnits, orgUnitNames, children )
{
	dhis2.appr.displayCategoryOptionGroups();
}

dhis2.appr.displayPeriods = function()
{
	var pt = $( '#dataSetId :selected' ).data( "pt" );
	dhis2.dsr.displayPeriodsInternal( pt, dhis2.appr.currentPeriodOffset );
}

dhis2.appr.displayNextPeriods = function()
{	
    if ( dhis2.appr.currentPeriodOffset < 0 ) // Cannot display future periods
    {
        dhis2.appr.currentPeriodOffset++;
        dhis2.appr.displayPeriods();
    }
}

dhis2.appr.displayPreviousPeriods = function()
{
    dhis2.appr.currentPeriodOffset--;
    dhis2.appr.displayPeriods();
}

dhis2.appr.displayCategoryOptionGroups = function()
{
	var ou = selection.getSelected()[0];
	
	if ( !ou ) {
		return;
	}
	
	var url = "getCategoryOptionGroups.action";
	
	$.getJSON( url, {ou:ou}, function( json ) {
		if ( json.categoryOptionGroups && json.categoryOptionGroups.length ) {
			var html = "";
			$.each( json.categoryOptionGroups, function( index, group ) {
				html += "<option value=\"" + group.uid + "\" data-dimension=\"" + group.groupSet + "\">" + group.name + "</option>";
			} );
	
			$( "#categoryOptionGroupSection" ).show();
			$( "#categoryOptionGroupId" ).html( html );
		}
		else {
			$( "#categoryOptionGroupSection" ).hide();			
		}
	} );
}

dhis2.appr.getDataReport = function()
{	
    var dataReport = {
        ds: $( "#dataSetId" ).val(),
        pe: $( "#periodId" ).val(),
        ou: selection.getSelected()[0]
    };
    
    var cog = $( "#categoryOptionGroupId" ).val();
    var cogs = $( "#categoryOptionGroupId :selected" ).data( "dimension" );
    
    if ( cog && cogs ) {
    	dataReport.dimension = cogs + ":" + cog;
    	dataReport.cog = cog;
    }
    
    return dataReport;
}

dhis2.appr.generateDataReport = function()
{
	var dataReport = dhis2.appr.getDataReport();
	
	if ( !dataReport.ds )
    {
        setHeaderMessage( i18n_select_data_set );
        return false;
    }
    if ( !dataReport.pe )
    {
        setHeaderMessage( i18n_select_period );
        return false;
    }
    if ( !selection.isSelected() )
    {
        setHeaderMessage( i18n_select_organisation_unit );
        return false;
    }

    hideHeaderMessage();
	$( "#criteria" ).hide( "fast" );
	$( "#content" ).hide( "fast" );
    showLoader();
    
    $.get( "generateDataSetReport.action", dataReport, function( data ) {
    	$( "#content" ).html( data );
    	$( "#shareForm" ).hide();
    	hideLoader();
    	$( "#content" ).show( "fast" );
    	setTableStyles();
    	dhis2.appr.setApprovalState();
    } );
}

//------------------------------------------------------------------------------
// Approval
//------------------------------------------------------------------------------

dhis2.appr.setApprovalState = function()
{
	var data = dhis2.appr.getDataReport();
	
    $( "#approvalDiv" ).hide();
		
	$.getJSON( "../api/dataApprovals", data, function( json ) {	
		if ( !json || !json.state ) {
			return;
		}
	
		dhis2.appr.permissions = json;
		
	    $( ".approveButton" ).hide();
	
	    switch ( json.state ) {
	        case "UNAPPROVABLE":
		        $( "#approvalNotification" ).html( i18n_approval_not_relevant );
		        break;
	    	
		    case "UNAPPROVED_WAITING":
		        $( "#approvalNotification" ).html( i18n_waiting_for_lower_level_approval );
		        break;
		
		    case "UNAPPROVED_ELSEWHERE":
		        $( "#approvalNotification" ).html( i18n_waiting_for_approval_elsewhere );
		        break;		    	
		        
		    case "UNAPPROVED_READY":
		        $( "#approvalNotification" ).html( i18n_ready_for_approval );
		        
		        if ( json.mayApprove ) {
		            $( "#approvalDiv" ).show();
		            $( "#approveButton" ).show();
		        }
		        
		        break;
		
		    case "APPROVED_HERE":
		        $( "#approvalNotification" ).html( i18n_approved );
		        
		        if ( json.mayUnapprove )  {
		            $( "#approvalDiv" ).show();
		            $( "#unapproveButton" ).show();
		        }
		        
		        if ( json.mayAccept )  {
		            $( "#approvalDiv" ).show();
		            $( "#acceptButton" ).show();
		        }
		        
		        break;
		
		    case "APPROVED_ELSEWHERE":
		        $( "#approvalNotification" ).html( i18n_approved_elsewhere );
		        break;
		        
		    case "ACCEPTED_HERE":
		        $( "#approvalNotification" ).html( i18n_approved_and_accepted );
		        
		        if ( json.mayUnapprove )  {
		            $( "#approvalDiv" ).show();
		            $( "#unapproveButton" ).show();
		        }
		        
		        if ( json.mayUnccept )  {
		            $( "#approvalDiv" ).show();
		            $( "#unacceptButton" ).show();
		        }
		        
		        break;

	        case "ACCEPTED_ELSEWHERE":
		        $( "#approvalNotification" ).html( i18n_accepted_elsewhere );
		        break;
		    }
	           	
		} );	
}

dhis2.appr.approveData = function()
{
	if ( !confirm( i18n_confirm_approval ) ) {
		return false;
	}
	
	$.ajax( {
		url: dhis2.appr.getApprovalUrl(),
		type: "post",
		success: function() {
            $( "#approvalNotification" ).show().html( i18n_approved );
            $( "#approvalDiv" ).hide();
			$( "#approveButton" ).hide();
            if ( dhis2.appr.permissions.mayUnapprove ) {
                $( "#approvalDiv" ).show();
                $( "#unapproveButton" ).show();
            }
            if ( dhis2.appr.permissions.mayAccept ) {
                $( "#approvalDiv" ).show();
                $( "#acceptButton" ).show();
            }
		},
		error: function( xhr, status, error ) {
			alert( xhr.responseText );
		}
	} );
}

dhis2.appr.unapproveData = function()
{
	if ( !confirm( i18n_confirm_unapproval ) ) {
		return false;
	}

	$.ajax( {
		url: dhis2.appr.getApprovalUrl(),
		type: "delete",
		success: function() {
            $( "#approvalNotification" ).show().html( i18n_ready_for_approval );
            $( "#approvalDiv" ).hide();
            $( "#unapproveButton" ).hide();
            $( "#acceptButton" ).hide();
            $( "#unacceptButton" ).hide();
            
            if ( dhis2.appr.permissions.mayApprove ) {
                $( "#approvalDiv" ).show();
                $( "#approveButton" ).show();
            }
		},
		error: function( xhr, status, error ) {
			alert( xhr.responseText );
		}
	} );
}

dhis2.appr.acceptData = function()
{
    if ( !confirm( i18n_confirm_accept ) ) {
        return false;
    }

    $.ajax( {
		url: dhis2.appr.getAcceptanceUrl(),
        type: "post",
        success: function() {
            $( "#approvalNotification" ).show().html( i18n_approved_and_accepted );
            $( "#approvalDiv" ).hide();
            $( "#acceptButton" ).hide();
          
            if ( dhis2.appr.permissions.mayUnapprove ) {
                $( "#approvalDiv" ).show();
                $( "#unapproveButton" ).show();
            }
          
            if ( dhis2.appr.permissions.mayUnaccept ) {
                $( "#approvalDiv" ).show();
                $( "#unacceptButton" ).show();
            }
        },
        error: function( xhr, status, error ) {
            alert( xhr.responseText );
        }
    } );
}

dhis2.appr.unacceptData = function()
{
    if ( !confirm( i18n_confirm_unaccept ) ) {
        return false;
    }

    $.ajax( {
		url: dhis2.appr.getAcceptanceUrl(),
        type: "delete",
        success: function() {
            $( "#approvalNotification" ).show().html( i18n_approved );
            $( "#approvalDiv" ).hide();
            $( "#unacceptButton" ).hide();
            if ( dhis2.appr.permissions.mayUnapprove ) {
                $( "#approvalDiv" ).show();
                $( "#unapproveButton" ).show();
            }
            if ( dhis2.appr.permissions.mayAccept ) {
                $( "#approvalDiv" ).show();
                $( "#acceptButton" ).show();
            }
        },
        error: function( xhr, status, error ) {
            alert( xhr.responseText );
        }
  } );
}

dhis2.appr.getApprovalUrl = function()
{
	var data = dhis2.appr.getDataReport();
	var url = "../api/dataApprovals?ds=" + data.ds + "&pe=" + data.pe + "&ou=" + data.ou + "&cog=" + data.cog;	
	return url;
}

dhis2.appr.getAcceptanceUrl = function()
{
	var data = dhis2.appr.getDataReport();
	var url = "../api/dataApprovals/acceptances?ds=" + data.ds + "&pe=" + data.pe + "&ou=" + data.ou + "&cog=" + data.cog;	
	return url;
}
