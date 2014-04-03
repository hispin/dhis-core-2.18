
dhis2.util.namespace( 'dhis2.appr' );

dhis2.appr.currentPeriodOffset = 0;
dhis2.appr.permissions = null;

//------------------------------------------------------------------------------
// Report
//------------------------------------------------------------------------------

dhis2.appr.dataSetSelected = function()
{
	dhis2.appr.displayPeriods();
}

dhis2.appr.orgUnitSelected = function()
{
	
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

//------------------------------------------------------------------------------
// Approval
//------------------------------------------------------------------------------

/**
 * Generates the URL for the approval of the given data set report.
 */
dhis2.appr.getDataApprovalUrl = function( dataSetReport )
{
    var url = "../api/dataApprovals" +
        "?ds=" + dataSetReport.ds +
        "&pe=" + dataSetReport.pe +
        "&ou=" + dataSetReport.ou;

    return url;
}

/**
 * Generates the URL for the acceptance of the given data set report approval.
 */
dhis2.appr.getDataApprovalAcceptanceUrl = function( dataSetReport )
{
    var url = "../api/dataApprovals/acceptances" +
        "?ds=" + dataSetReport.ds +
        "&pe=" + dataSetReport.pe +
        "&ou=" + dataSetReport.ou;

    return url;
}

dhis2.appr.showApproval = function()
{
	var dataSetReport = dhis2.dsr.getDataSetReport();
	
	var approval = $( "#dataSetId :selected" ).data( "approval" );

	$( "#approvalNotification" ).hide();
    $( "#approvalDiv" ).hide();

	if ( !approval ) {
		return;
	}
	
	var url = dhis2.appr.getDataApprovalUrl( dataSetReport );
	
	$.getJSON( url, function( json ) {
	
	if ( !json || !json.state ) {
		return;
	}

	dhis2.appr.permissions = json;
		
	var state = json.state;

    $( "#approveButton" ).hide();
    $( "#unapproveButton" ).hide();
    $( "#acceptButton" ).hide();
    $( "#unacceptButton" ).hide();

    switch (state) {
    case "UNAPPROVED_WAITING":
        $("#approvalNotification").show().html(i18n_waiting_for_lower_level_approval);
        break;

    case "UNAPPROVED_READY":
        $("#approvalNotification").show().html(i18n_ready_for_approval);
        
        if (json.mayApprove) {
            $("#approvalDiv").show();
            $("#approveButton").show();
        }
        
        break;

    case "APPROVED_HERE":
        $("#approvalNotification").show().html(i18n_approved);
        
        if (json.mayUnapprove)  {
            $("#approvalDiv").show();
            $("#unapproveButton").show();
        }
        
        if (json.mayAccept)  {
            $("#approvalDiv").show();
            $("#acceptButton").show();
        }
        
        break;

    case "ACCEPTED_HERE":
        $("#approvalNotification").show().html(i18n_approved);
        
        if (json.mayUnapprove)  {
            $("#approvalDiv").show();
            $("#unapproveButton").show();
        }
        
        if (json.mayUnccept)  {
            $("#approvalDiv").show();
            $("#unacceptButton").show();
        }
        
        break;
    }
    });	
}

dhis2.appr.approveData = function()
{
	if ( !confirm( i18n_confirm_approval ) ) {
		return false;
	}
	
	var dataSetReport = dhis2.dsr.getDataSetReport();
	var url = dhis2.appr.getDataApprovalUrl( dataSetReport );
	
	$.ajax( {
		url: url,
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
	
	var dataSetReport = dhis2.dsr.getDataSetReport();
	var url = dhis2.appr.getDataApprovalUrl( dataSetReport );
	
	$.ajax( {
		url: url,
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

  var dataSetReport = dhis2.dsr.getDataSetReport();
  var url = dhis2.appr.getDataApprovalAcceptanceUrl( dataSetReport );

  $.ajax( {
      url: url,
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

    var dataSetReport = dhis2.dsr.getDataSetReport();
    var url = dhis2.appr.getDataApprovalAcceptanceUrl( dataSetReport );

    $.ajax( {
        url: url,
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
