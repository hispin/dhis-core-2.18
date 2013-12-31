
function orgUnitHasBeenSelected( orgUnitIds )
{    
	$( '#dataEntryFormDiv' ).html( '' );
	
	document.getElementById('selectedOrgunitID').value = orgUnitIds;
	
	//alert( orgUnitIds );
	
	if( orgUnitIds != null && orgUnitIds != "" )
	{
		 $.getJSON( 'getOrganisationUnit.action', {orgUnitId:orgUnitIds[0]}
	        , function( json ) 
	        {
	            var type = json.response;
	            setFieldValue('orgUnitName', json.message );
	            setFieldValue('selectedOrgunitName', json.message );
	                
	            if( type == 'success' )
	            {
					enable('dataSetId');
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );
	            }
	            else if( type == 'input' )
	            {
	                disable('dataSetId');
	                disable('selectedPeriodId');
	                disable('prevButton');
	                disable('nextButton');
	                
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );
	            }
	        } );		
	}
}

selection.setListenerFunction( orgUnitHasBeenSelected );


function loadDataEntryForm()
{
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	
	
	
	
	var dataSetId = $( '#dataSetId' ).val();
	
	
	
	$( '#dataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );
	

	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" && dataSetId == "-1" )
	{
		$( '#dataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    jQuery('#loaderDiv').show();
	    
		jQuery('#dataEntryFormDiv').load('loadDataEntryForm.action',
			{
				orgUnitId:orgUnitId,
				dataSetId:dataSetId,
				selectedPeriodId:selectedPeriodId
			}, function()
			{
				showById('dataEntryFormDiv');
				jQuery('#loaderDiv').hide();
			});
		hideLoader();
	}

}

























// load periods
function loadPeriods()
{
	$( '#dataEntryFormDiv' ).html( '' );
	
    var orgUnitId = $( '#selectedOrgunitID' ).val();

    var dataSetId = $( '#dataSetId' ).val();
	
	
	if ( dataSetId == "-1" )
	{
		showWarningMessage( i18n_select_dataset );
		
		document.getElementById( "selectedPeriodId" ).disabled = true;
		document.getElementById( "prevButton" ).disabled = true;
		document.getElementById( "nextButton" ).disabled = true;
		return false;
	}
	
	else
	{
		
		enable('selectedPeriodId');
		
		enable('prevButton');
		enable('nextButton');
				
		var url = 'loadPeriods.action?dataSetId=' + dataSetId;
		
		var list = document.getElementById( 'selectedPeriodId' );
			
		clearList( list );
		
		addOptionToList( list, '-1', '[ Select ]' );
		
	    $.getJSON( url, function( json ) {
	    	for ( i in json.periods ) {
	    		addOptionToList( list, json.periods[i].isoDate, json.periods[i].name );
	    	}
	    } );
		
	}
}


//next and pre periods
function getAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	$( '#dataEntryFormDiv' ).html( '' );
	
	var dataSetId = $( '#dataSetId' ).val();
	
	var availableList = document.getElementById( availablePeriodsId );
	var selectedList = document.getElementById( selectedPeriodsId );
	
	clearList( selectedList );
	
	addOptionToList( selectedList, '-1', '[ Select ]' );
	
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"dataSetId": dataSetId ,
		"year": year },
		function( json ) {
			
			for ( i in json.periods ) {
	    		addOptionToList( selectedList, json.periods[i].isoDate, json.periods[i].name );
	    	}
			
		} );
}













