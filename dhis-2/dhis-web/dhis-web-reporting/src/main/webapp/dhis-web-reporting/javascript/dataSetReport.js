
dhis2.util.namespace( 'dhis2.dsr' );

dhis2.dsr.currentPeriodOffset = 0;
dhis2.dsr.periodTypeFactory = new PeriodType();
dhis2.dsr.currentDataSetReport = null;

//------------------------------------------------------------------------------
// Get and set methods
//------------------------------------------------------------------------------

function getDataSetReport()
{
    var dataSetReport = {
        ds: $( "#dataSetId" ).val(),
        periodType: $( "#periodType" ).val(),
        pe: $( "#periodId" ).val(),
        ou: selectionTreeSelection.getSelectedUid()[0],
        selectedUnitOnly: $( "#selectedUnitOnly" ).is( ":checked" ),
        offset: dhis2.dsr.currentPeriodOffset
    };
    
    var dims = [];
    
    $( ".dimension" ).each( function( index, value ) {
    	var dim = $( this ).data( "uid" );
    	var item = $( this ).val();
    	
    	if ( dim && item )
    	{
    		var dimQuery = dim + ":" + item;
    		dims.push( dimQuery );
    	}
    } );
    
    dataSetReport.dimension = dims;
    
    return dataSetReport;
}

function setDataSetReport( dataSetReport )
{
	$( "#dataSetId" ).val( dataSetReport.dataSet );
	$( "#periodType" ).val( dataSetReport.periodType );
	
	dhis2.dsr.currentPeriodOffset = dataSetReport.offset;
	
	displayPeriods();
	$( "#periodId" ).val( dataSetReport.period );
	
	selectionTreeSelection.setMultipleSelectionAllowed( false );
	selectionTree.buildSelectionTree();
	
	$( "body" ).on( "oust.selected", function() 
	{
		$( "body" ).off( "oust.selected" );
		generateDataSetReport();
	} );
}

//------------------------------------------------------------------------------
// Data set
//------------------------------------------------------------------------------

/**
 * Callback for changes to data set selection.
 */
dhis2.dsr.dataSetSelected = function()
{
	var ds = $( "#dataSetId" ).val();
	var cc = $( "#dataSetId :selected" ).data( "categorycombo" );
	
	if ( cc && cc != dhis2.dsr.metaData.defaultCategoryCombo ) {
		var categoryCombo = dhis2.dsr.metaData.categoryCombos[cc];
		var categoryIds = categoryCombo.categories;
		
		dhis2.dsr.setAttributesMarkup( categoryIds );		
	}
	else {
		$( "#attributeComboDiv" ).html( "" ).hide();
	}
}

/**
* Sets markup for drop down boxes for the given categories in the selection div.
*/
dhis2.dsr.setAttributesMarkup = function( categoryIds )
{
	if ( !categoryIds || categoryIds.length == 0 ) {
		return;
	}
	
	var categoryRx = [];	
	$.each( categoryIds, function( idx, id ) {
		categoryRx.push( $.get( "../api/dimensions/" + id + ".json" ) );
	} );

	$.when.apply( $, categoryRx ).done( function() {
		var html = '';
		
		$.each( arguments, function( idx, cat ) {
			var category = cat[0];
			
			html += '<div class="inputSection">';
			html += '<label>' + category.name + '</label>';
			html += '<select class="dimension" data-uid="' + category.id + '" style="width:330px">';
			html += '<option value="-1">[ ' + i18n_select_option_view_all + ' ]</option>';
			
			$.each( category.items, function( idx, option ) {
				html += '<option value="' + option.id + '">' + option.name + '</option>';
			} );
			
			html += '</select>';
			html += '</div>';
		} );

		$( "#attributeComboDiv" ).show().html( html );
	} );
}

//------------------------------------------------------------------------------
// Period
//------------------------------------------------------------------------------

function displayPeriods()
{
    var periodType = $( "#periodType" ).val();
    var periods = dhis2.dsr.periodTypeFactory.get( periodType ).generatePeriods( dhis2.dsr.currentPeriodOffset );
    periods = dhis2.dsr.periodTypeFactory.reverse( periods );
    periods = dhis2.dsr.periodTypeFactory.filterFuturePeriodsExceptCurrent( periods );

    $( "#periodId" ).removeAttr( "disabled" );
    clearListById( "periodId" );

    for ( i in periods )
    {
        addOptionById( "periodId", periods[i].iso, periods[i].name );
    }
}

function displayNextPeriods()
{
    if ( dhis2.dsr.currentPeriodOffset < 0 ) // Cannot display future periods
    {
        dhis2.dsr.currentPeriodOffset++;
        displayPeriods();
    }
}

function displayPreviousPeriods()
{
    dhis2.dsr.currentPeriodOffset--;
    displayPeriods();
}

//------------------------------------------------------------------------------
// Run report
//------------------------------------------------------------------------------

function drillDownDataSetReport( orgUnitId, orgUnitUid )
{
	selectionTree.clearSelectedOrganisationUnits();
	selectionTreeSelection.select( orgUnitId );
	
	var dataSetReport = getDataSetReport();
	dataSetReport["ou"] = orgUnitUid;
	displayDataSetReport( dataSetReport );
}

function generateDataSetReport()
{
	var dataSetReport = getDataSetReport();
	displayDataSetReport( dataSetReport );
}

function displayDataSetReport( dataSetReport )
{	
    if ( !dataSetReport.ds )
    {
        setHeaderMessage( i18n_select_data_set );
        return false;
    }
    if ( !dataSetReport.pe )
    {
        setHeaderMessage( i18n_select_period );
        return false;
    }
    if ( !selectionTreeSelection.isSelected() )
    {
        setHeaderMessage( i18n_select_organisation_unit );
        return false;
    }
    
    dhis2.dsr.currentDataSetReport = dataSetReport;
    
    hideHeaderMessage();
    hideCriteria();
    hideContent();
    showLoader();
	
    delete dataSetReport.periodType;
    delete dataSetReport.offset;
    
    var url = "generateDataSetReport.action" + dhis2.dsr.getDatSetReportQueryParams( dataSetReport );
    
    $.get( url, function( data ) {
    	$( '#content' ).html( data );
    	hideLoader();
    	showContent();
    	dhis2.dsr.showApproval();
    	setTableStyles();
    } );
}

/**
 * Generates the query params part of the URL for the given data set report.
 */
dhis2.dsr.getDatSetReportQueryParams = function( dataSetReport )
{
    var url = 
    	"?ds=" + dataSetReport.ds + 
    	"&pe=" + dataSetReport.pe + 
    	"&ou=" + dataSetReport.ou +
    	"&selectedUnitOnly=" + dataSetReport.selectedUnitOnly;
    
    $.each( dataSetReport.dimension, function( inx, val ) {
    	url += "&dimension=" + val;
    } );
    
    return url;
}

function exportDataSetReport( type )
{
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	
	var url = "generateDataSetReport.action" + 
		dhis2.dsr.getDatSetReportQueryParams( dataSetReport ) +
	    "&type=" + type;
	    
	window.location.href = url;
}

function setUserInfo( username )
{
	$( "#userInfo" ).load( "../dhis-web-commons-ajax-html/getUser.action?username=" + username, function() {
		$( "#userInfo" ).dialog( {
	        modal : true,
	        width : 350,
	        height : 350,
	        title : "User"
	    } );
	} );	
}

function showCriteria()
{
	$( "#criteria" ).show( "fast" );
}

function hideCriteria()
{
	$( "#criteria" ).hide( "fast" );
}

function showContent()
{
	$( "#content" ).show( "fast" );
	$( ".downloadButton" ).show();
	$( "#interpretationArea" ).autogrow();
}

function hideContent()
{
	$( "#content" ).hide( "fast" );
	$( ".downloadButton" ).hide();
}

function showAdvancedOptions()
{
	$( "#advancedOptionsLink" ).hide();
	$( "#advancedOptions" ).show();
}

dhis2.dsr.showApproval = function()
{
	var approval = $( "#dataSetId :selected" ).data( "approval" );
	
	if ( !approval ) {
		$( "#approvalDiv" ).hide();
		return false;
	}
	
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	
	var url = "../api/dataApprovals" + dhis2.dsr.getDatSetReportQueryParams( dataSetReport );
	
	$.get( url, function( status ) {
		if ( status && '"READY_FOR_APPROVAL"' == status ) {
			$( "#approvalDiv" ).show();
			$( "#approveButton" ).prop( "disabled", false );
			$( "#unapproveButton" ).prop( "disabled", true );
			$( "#message" ).hide();
		}
		else if ( status && '"APPROVED"' == status ) {
			$( "#approvalDiv" ).show();
			$( "#approveButton" ).prop( "disabled", true );
			$( "#unapproveButton" ).prop( "disabled", false );
			$( "#message" ).hide();		
		}
		else if ( status && '"WAITING_FOR_LOWER_LEVEL_APPROVAL"' == status ) {
			$( "#approvalDiv" ).hide();
			$( "#message" ).show().html( i18n_waiting_for_lower_level_approval );		
		}
	} );
}

//------------------------------------------------------------------------------
// Approval
//------------------------------------------------------------------------------

dhis2.dsr.approveData = function()
{
	var check = confirm( i18n_confirm_approval );
	
	if ( !check ) {
		return false;
	}
	
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	var url = "../api/dataApprovals" + dhis2.dsr.getDatSetReportQueryParams( dataSetReport );
	
	$.ajax( {
		url: url,
		type: "post",
		success: function() {
			$( "#approveButton" ).prop( "disabled", true );
			$( "#unapproveButton" ).prop( "disabled", false );			
		},
		error: function( xhr, status, error ) {
			alert( xhr.responseText );
		}
	} );
}

dhis2.dsr.unapproveData = function()
{
	var check = confirm( i18n_confirm_unapproval );
	
	if ( !check ) {
		return false;
	}
	
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	var url = "../api/dataApprovals" + dhis2.dsr.getDatSetReportQueryParams( dataSetReport );
	
	$.ajax( {
		url: url,
		type: "delete",
		success: function() {
			$( "#approveButton" ).prop( "disabled", false );
			$( "#unapproveButton" ).prop( "disabled", true );			
		},
		error: function( xhr, status, error ) {
			alert( xhr.responseText );
		}
	} );
}

//------------------------------------------------------------------------------
// Share
//------------------------------------------------------------------------------

function shareInterpretation()
{
	var dataSetReport = getDataSetReport();
    var text = $( "#interpretationArea" ).val();
    
    if ( text.length && $.trim( text ).length )
    {
    	text = $.trim( text );
    	
	    var url = "../api/interpretations/dataSetReport/" + $( "#currentDataSetId" ).val() +
	    	"?pe=" + dataSetReport.pe +
	    	"&ou=" + dataSetReport.ou;
	    	    
	    $.ajax( url, {
	    	type: "POST",
	    	contentType: "text/html",
	    	data: text,
	    	success: function() {	    		
	    		$( "#interpretationArea" ).val( "" );
	    		setHeaderDelayMessage( i18n_interpretation_was_shared );
	    	}    	
	    } );
    }
}

//------------------------------------------------------------------------------
// Hooks in custom forms - must be present to avoid errors in forms
//------------------------------------------------------------------------------

function onValueSave( fn )
{
	// Do nothing
}

function onFormLoad( fn )
{
	// Do nothing
}
