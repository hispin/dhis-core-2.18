
	var COLOR_GREEN = '#b9ffb9';
	var COLOR_YELLOW = '#fffe8c';
	var COLOR_RED = '#ff8a8a';
	var COLOR_ORANGE = '#ff6600';
	var COLOR_WHITE = '#ffffff';
	var COLOR_GREY = '#cccccc';
	var LocaleColor = 'black';
	
function orgUnitHasBeenSelected( orgUnitIds )
{    
	$( '#dataEntryFormDiv' ).html( '' );
	
	if( orgUnitIds != null && orgUnitIds != "" )
	{
		var dataSetId = $( '#dataSetId' ).val();		
		 $.getJSON( 'getOrganisationUnitForQuality.action', {orgUnitId:orgUnitIds[0]}
	        , function( json ) 
	        {
	            var type = json.response;
	            setFieldValue('orgUnitName', json.message );
	            setFieldValue('selectedOrgunitName', json.message );	            
	            if( type == "success" )
	            {
					enable('dataSetId');
					
					var options = '';
		            $.each(json.dataSets, function(i, obj){
		                options += '<option value="' + obj.id + '"'+ '>' + obj.name + '</option>';
		            });
		            $("select#dataSetId").html(options);
		            
		            $("select#dataSetId option[value="+dataSetId+"]").attr('selected', 'selected');
		            	            
					setFieldValue('selectedOrgunitID',orgUnitIds[0])
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );
	                loadDataEntryForm();
	            }
	            else if( type == "input" )
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
	
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var date1 = Date.parse( startDate );
	var date2 = Date.parse( endDate );
	if (date1 > date2) {
	    alert ("Please enter correct date");
	    return false;
	}	
	if(startDate != "" && endDate != "")
	{
		/* var dataValue = {
     			'orgUnitId' : orgUnitId,        		        			
     			'startDate' : startDate,
				'endDate' : endDate,
				'dataSetId': dataSetId
 			};
				jQuery.ajax( {
             url: 'validateMaxData.action', 
			 data: dataValue,
             dataType: 'json',
             success: handleSuccess,
             error: handleError
         } );
         */
		jQuery('#loaderDiv').show();	    
		jQuery('#dataEntryFormDiv').load('loadQualityMaxForm.action',
			{
				orgUnitId:orgUnitId,
				dataSetId:dataSetId,
				startDate:startDate,
				endDate:endDate
			}, function()
			{
				showById('dataEntryFormDiv');
				jQuery('#loaderDiv').hide();				
			});
		hideLoader();
	}
	
/*	function handleSuccess( json )
	{		
		if(json.message == "true")	
		{
			alert("Max quality score exist between this range");						
		}
		else
		{
			jQuery('#loaderDiv').show();	    
			jQuery('#dataEntryFormDiv').load('loadQualityMaxForm.action',
				{
					orgUnitId:orgUnitId,
					dataSetId:dataSetId,
					startDate:startDate,
					endDate:endDate
				}, function()
				{
					showById('dataEntryFormDiv');
					jQuery('#loaderDiv').hide();				
				});
			hideLoader();
		}
	}
	function handleError( json )
	{	
		alert("Error!");
	}
	*/
}


function saveQualityDataValue( dataElementId )
{
	var dataSetId = $( '#dataSetId' ).val();
	var valueId = "value_"+dataElementId;
	
	var fieldId = "#"+valueId;
	
	var defaultValue = document.getElementById(valueId).defaultValue;
	var value = document.getElementById( valueId ).value;
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	if(startDate == "" && endDate == "")
	{
		alert("Please select start date and end Date");
		return false;
	}
	
	if(defaultValue != value)
	{
		var dataValue = {
				'dataElementId' : dataElementId,
				'dataSetId' : dataSetId,
				'organisationUnitId' : $("#selectedOrgunitID").val(),				
				'value' : value,
				'startDate' : startDate,
				'endDate' : endDate
    };
    jQuery.ajax( {
            url: 'saveQualityValue.action',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
	}
	
	function handleSuccess( json )
	{
	    var code = json.c;

	    if ( code == '0' || code == 0) // Value successfully saved on server
	    {
	    	 markValue( fieldId, COLOR_GREEN );
	    }
	    else if ( code == 2 )
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_dataset_is_locked );
	    }
	    else // Server error during save
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
	    }
	}

	function handleError( jqXHR, textStatus, errorThrown )
	{       
	    markValue( fieldId, COLOR_RED );
	}

	function markValue( fieldId, color )
	{
	    document.getElementById(valueId).style.backgroundColor = color;	   
	}
}













