
//--------------------------------------------------------------------------------------------
// Load program-stages by the selected program
//--------------------------------------------------------------------------------------------

function loadProgramStages()
{
	hideById('dataEntryFormDiv');
	clearListById('programStageId');
	setFieldValue('executionDate','');
	setFieldValue('dueDate','');
	disable('completeBtn');
	disable('validationBtn');
	disable('newEncounterBtn');
	disable('executionDate');
		
	var programId = jQuery('#dataRecordingSelectDiv [name=programId]').val();
	if ( programId == 0 )
	{
		hideById('historyPlanLink');
		return;
	}
	jQuery.postJSON( "loadProgramStages.action",
		{
			programId: programId
		}, 
		function( json ) 
		{    
			addOptionById( 'programStageId', "0", i18n_select );
			for ( i in json.programStages ) 
			{
				addOptionById( 'programStageId', json.programStages[i].id, json.programStages[i].name );
			} 
			
			var singleEvent = jQuery('#dataRecordingSelectDiv [name=programId] option:selected').attr('singleevent');
				
			if(singleEvent=='true')
			{
				byId('programStageId').selectedIndex = 1;
				jQuery('#programStageIdTR').attr('class','hidden');
				jQuery('#dueDateTR').attr('class','hidden');
				enable('completeBtn');
				enable('validationBtn');
				hideById('historyPlanLink');
				loadDataEntry();
			}
			else
			{
				
				// show history / plan
				setInnerHTML( 'currentSelection', '' ); 
				var history = '<table class="history">';
				history += '<tr>';
				history += '<td class="bold row">' + i18n_program_stage + '</td>';
				history += '<td class="bold row">' + i18n_scheduled_for + '</td>';
				history += '</tr>';
				for ( i in json.programStageInstances ) 
				{
					history += '<tr bgcolor=' + json.programStageInstances[i].colorMap + '>';
					history += '<td>';
					history += '<span>' + json.programStageInstances[i].name + '</span>';
					history += '</td>';
					history += '<td class="cent">';
					history += json.programStageInstances[i].infor;
					history += '</td>';
					history += '</tr>';
				}
				history += '</table>';
				setInnerHTML( 'currentSelection', history );
				showById('historyPlanLink');
				
				disable('completeBtn');
				disable('validationBtn');
				
				jQuery('#programStageIdTR').removeAttr('class');
				jQuery('#dueDateTR').removeAttr('class');
				
			}
			
	});
}

function showHistoryPlan()
{
	$('#currentSelection' ).dialog({
        title: i18n_program_stages_history_plan,
		maximize: true, 
		closable: true,
		modal:false,
		overlay:{background:'#000000', opacity:0.1},
		width: 400,
        height: 400
    });
}

//--------------------------------------------------------------------------------------------
// Load data-entry-form
//--------------------------------------------------------------------------------------------

function loadDataEntry()
{
	setInnerHTML('dataEntryFormDiv', '');
	showById('dataEntryFormDiv');
	setFieldValue( 'dueDate', '' );
	setFieldValue( 'executionDate', '' );
	disable('validationBtn');
	disable('completeBtn');
	disable('newEncounterBtn');
		
	if( getFieldValue('programStageId') == null
		|| getFieldValue('programStageId') == 0 )
	{
		enable('newEncounterBtn');
		return;
	}
	
	showLoader();
	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageId:getFieldValue('programStageId')
		},function( )
		{
			var executionDate = jQuery('#dataRecordingSelectDiv input[id=executionDate]').val();
			var completed = jQuery('#entryFormContainer input[id=completed]').val();
			var irregular = jQuery('#entryFormContainer input[id=irregular]').val();
			
			enable('executionDate');
			if( executionDate != '' && completed == 'false' )
			{
				enable('validationBtn');
				enable('completeBtn');
			}
			else if( completed == 'true' )
			{
				disable('validationBtn');
				disable('completeBtn');
				disable('executionDate');
			}
			
			if( completed == 'true' && irregular == 'true' )
			{
				enable( 'newEncounterBtn' );
			}
			
			hideLoader();
			hideById('contentDiv'); 
		} );
}

//------------------------------------------------------------------------------
//Save value
//------------------------------------------------------------------------------

function saveVal( dataElementId )
{
	var programStageId = byId('programStageId').value;
	var fieldId = programStageId + '-' + dataElementId + '-val';
	
	var field = byId( fieldId ); 
	var fieldValue = jQuery.trim( field.value );

	var data = jQuery( "#" + fieldId ).metadata({
        type:'attr',
        name:'data'
    });
	
	var dataElementName = data.deName; 
    var type = data.deType;
    var providedByAnotherFacility = document.getElementById( programStageId + '_' + dataElementId + '_facility' ).checked;
 
	field.style.backgroundColor = '#ffffcc';
    
    if( fieldValue != '' )
    {
        if ( type == 'int' || type == 'number' || type == 'positiveNumber' || type == 'negativeNumber' )
        {
            if (  type == 'int' && !isInt( fieldValue ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + dataElementName );

                field.focus();

                return;
            }
			else if ( type == 'number' && !isRealNumber( fieldValue ) )
            {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_number + '\n\n' + dataElementName );
                field.focus();

                return;
            } 
			else if ( type == 'positiveNumber' && !isPositiveInt( fieldValue ) )
            {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_positive_integer + '\n\n' + dataElementName );
                field.focus();

                return;
            } 
			else if ( type == 'negativeNumber' && !isNegativeInt( fieldValue ) )
            {
                field.style.backgroundColor = '#ffcc00';
                window.alert( i18n_value_must_negative_integer + '\n\n' + dataElementName );
                field.focus();

                return;
            }
        }
    	
    }
    
	var valueSaver = new ValueSaver( dataElementId, fieldValue, providedByAnotherFacility, type, '#ccffcc'  );
    valueSaver.save();
}

function saveDate( dataElementId )
{	
	var programStageId = byId('programStageId').value;
	var fieldId = programStageId + '-' + dataElementId + '-val';
	var field = jQuery( "#" + fieldId ); 
	var data = field.metadata({
        type:'attr',
        name:'data'
    });
	
    var providedByAnotherFacility = document.getElementById( programStageId + '_' + dataElementId + '_facility' ).checked;
	
    var dateSaver = new DateSaver( dataElementId, field.val(), providedByAnotherFacility, '#ccffcc' );
    dateSaver.save();
}

function saveOpt( dataElementId )
{
	var programStageId = byId('programStageId').value;
	var field = byId( programStageId + '-' + dataElementId + '-val' );
	
	field.style.backgroundColor = '#ffffcc';
	var providedByAnotherFacility = document.getElementById( programStageId + '_' + dataElementId + '_facility' ).checked;
 
	var valueSaver = new ValueSaver( dataElementId, field.options[field.selectedIndex].value, providedByAnotherFacility, 'bool', '#ccffcc' );
    valueSaver.save();
}

function updateProvidingFacility( dataElementId, checkedBox )
{
	var programStageId = byId( 'programStageId' ).value;
    checkedBox.style.backgroundColor = '#ffffcc';
    var providedByAnotherFacility = document.getElementById( programStageId + '_' + dataElementId + '_facility' ).checked;
 
    var facilitySaver = new FacilitySaver( dataElementId, providedByAnotherFacility, '#ccffcc' );
    facilitySaver.save();
    
}

function saveExecutionDate( programStageId, executionDateValue )
{
    var field = document.getElementById( 'executionDate' );
	
    field.style.backgroundColor = '#ffffcc';
	
    var executionDateSaver = new ExecutionDateSaver( programStageId, executionDateValue, '#ccffcc' );
    executionDateSaver.save();
	
    if( !jQuery("#entryForm").is(":visible") )
    {
        toggleContentForReportDate(true);
    }
}

/**
* Display data element name in selection display when a value field recieves
* focus.
* XXX May want to move this to a separate function, called by valueFocus.
* @param e focus event
* @author Hans S. Tommerholt
*/
function valueFocus(e) 
{
    //Retrieve the data element id from the id of the field
    var str = e.target.id;
	
    var match = /.*\[(.*)\]/.exec( str ); //value[-dataElementId-]
	
    if ( ! match )
    {
        return;
    }

    var deId = match[1];
	
    //Get the data element name
    var nameContainer = document.getElementById('value[' + deId + '].name');
	
    if ( ! nameContainer )
    {
        return;
    }

    var name = '';
	
    var as = nameContainer.getElementsByTagName('a');

    if ( as.length > 0 )	//Admin rights: Name is in a link
    {
        name = as[0].firstChild.nodeValue;
    }
    else
    {
        name = nameContainer.firstChild.nodeValue;
    }
	
}

function keyPress( event, field )
{
    var key = 0;
    if ( event.charCode )
    {
        key = event.charCode; /* Safari2 (Mac) (and probably Konqueror on Linux, untested) */
    }
    else
    {
        if ( event.keyCode )
        {
            key = event.keyCode; /* Firefox1.5 (Mac/Win), Opera9 (Mac/Win), IE6, IE7Beta2, Netscape7.2 (Mac) */
        }
        else
        {
            if ( event.which )
            {
                key = event.which; /* Older Netscape? (No browsers triggered yet) */
            }
        }
    }
   
    if ( key == 13 ) /* CR */
    { 
        nextField = getNextEntryField( field );
        if ( nextField )
        {
            nextField.focus(); /* Does not seem to actually work in Safari, unless you also have an Alert in between */
        }
        return true;
    }
    
    /* Illegal characters can be removed with a new if-block and return false */
    return true;
}

function getNextEntryField( field )
{
    var inputs = document.getElementsByName( "entryfield" );
    
    // Simple bubble sort
    for ( var i = 0; i < inputs.length - 1; ++i )
    {
        for ( var j = i + 1; j < inputs.length; ++j )
        {
            if ( inputs[i].tabIndex > inputs[j].tabIndex )
            {
                tmp = inputs[i];
                inputs[i] = inputs[j];
                inputs[j] = tmp;
            }
        }
    }
    
    i = 0;
    for ( ; i < inputs.length; ++i )
    {
        if ( inputs[i] == field )
        {
            break;
        }
    }
    
    if ( i == inputs.length - 1 )
    {
        // No more fields after this:
        return false;
    }
    else
    {
        return inputs[i + 1];
    }
}

//-----------------------------------------------------------------
// Save value for dataElement of type text, number, boolean, combo
//-----------------------------------------------------------------

function ValueSaver( dataElementId_, value_, providedByAnotherFacility_, dataElementType_, resultColor_  )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';
	
    var dataElementId = dataElementId_;
	var value = value_;
    var providedByAnotherFacility = providedByAnotherFacility_;
	var type = dataElementType_;
    var resultColor = resultColor_;
	
    this.save = function()
    {
		var params = 'dataElementId=' + dataElementId;
			params += '&value=' + value;
			params += '&providedByAnotherFacility=' + providedByAnotherFacility;
			
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
		request.sendAsPost( params );
        request.send( 'saveValue.action');
    };
 
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else
        {
            if(value!="")
            {
                markValue( ERROR );
                window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
            }
            else
            {
                markValue( resultColor );
            }
        }
    }
 
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }
 
    function markValue( color )
    {
		var programStageId = getFieldValue('programStageId');
        var element = byId( programStageId + "-" + dataElementId + '-val' );
        element.style.backgroundColor = color;
    }
}

function DateSaver( dataElementId_, value_, providedByAnotherFacility_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ffcc00';
	
    var dataElementId = dataElementId_;
    var value = value_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;

    this.save = function()
    {
		var params = 'dataElementId=' + dataElementId;
		params +=  '&value=' + value;
		params +=  '&providedByAnotherFacility=' + providedByAnotherFacility;

        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
		request.sendAsPost( params );
        request.send( 'saveDateValue.action' );
    };

    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else if(code == 1)
        {
            if(value != "")
            {
                var dataelementList = rootElement.getElementsByTagName( 'validation' );
                var message = '';

                for ( var i = 0; i < dataelementList.length; i++ )
                {
                    message += "\n - " + dataelementList[i].firstChild.nodeValue;
                }

                markValue( ERROR );
                window.alert( i18n_violate_validation + message);
            }
            else
            {
                markValue( resultColor );
            }
        }
		else if(code == 2)
        {
			markValue( ERROR );
            window.alert( i18n_invalid_date + ":\n" + rootElement.getElementsByTagName( 'message' )[0].firstChild.nodeValue );
		}
        else
        {
            if(value != "")
            {
                markValue( ERROR );
                window.alert( i18n_invalid_date );
            }
            else
            {
                markValue( resultColor );
            }
        }
    }

    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( color )
    {
		var programStageId = byId('programStageId').value;
        var element = byId(  programStageId + "-" + dataElementId + '-val' );
        
        element.style.backgroundColor = color;
    }
}

function FacilitySaver( dataElementId_, providedByAnotherFacility_, resultColor_ )
{
    var SUCCESS = 'success';
    var ERROR = '#error';
	
    var dataElementId = dataElementId_;
    var providedByAnotherFacility = providedByAnotherFacility_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponseCheckBox );
        request.setCallbackError( handleHttpErrorCheckBox );
        request.setResponseTypeXML( 'status' ); 
        request.send( 'saveProvidingFacility.action?dataElementId=' + dataElementId 
					+'&providedByAnotherFacility=' + providedByAnotherFacility );
    };

    function handleResponseCheckBox( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        if ( code == 0 )
        {
            markValue( SUCCESS );
        }
        else
        {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }

    function handleHttpErrorCheckBox( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( result )
    {
		var programStageId = byId( 'programStageId' ).value;
        if( result == SUCCESS )
        {
            jQuery('label[for="'+programStageId+'_'+dataElementId+'_facility"]').toggleClass('checked');
        }
        else if( result == ERROR )
        {
            jQuery('label[for="'+programStageId+'_'+dataElementId+'_facility"]').removeClass('checked');
            jQuery('label[for="'+programStageId+'_'+dataElementId+'_facility"]').addClass('error');
        }
    }
}

function ExecutionDateSaver( programStageId_, executionDate_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ffcc00';
	
    var programStageId = programStageId_;
    var executionDate = executionDate_;
    var resultColor = resultColor_;

    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
		
		var params  = "executionDate=" + executionDate;
			params += "&programStageId=" + programStageId;
		request.sendAsPost(params);
		
        request.send( "saveExecutionDate.action");
    };

    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getAttribute( 'type' );
        if ( codeElement == 'success' )
        {
            markValue( resultColor );
			if( getFieldValue('programStageInstanceId' )=='' )
			{
				setFieldValue('programStageInstanceId',rootElement.firstChild.nodeValue);
				loadDataEntry();
			}
			else
			{
				showById('entryFormContainer');
				showById('dataEntryFormDiv');
				showById('entryForm');
			}
        }
        else
        {
            if( executionDate != "")
            {
                markValue( ERROR );
                showWarningMessage( i18n_invalid_date );
            }
            else
            {
                markValue( ERROR );
				showWarningMessage( i18n_please_enter_report_date );
            }
			hideById('dataEntryFormDiv');
        }
    }

    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }

    function markValue( color )
    {
        var element = document.getElementById( 'executionDate' );
           
        element.style.backgroundColor = color;
    }
}

//-----------------------------------------------------------------
//
//-----------------------------------------------------------------

function initCustomCheckboxes()
{
    jQuery('input[type=checkbox][name="providedByAnotherFacility"]').prettyCheckboxes();
}

function toggleContentForReportDate(show)
{
    if( show ){
        jQuery("#entryForm").show();
        jQuery("#completeBtn").removeAttr('disabled');
		jQuery("#validationBtn").removeAttr('disabled');
    }else {
        jQuery("#entryForm").hide();
        jQuery("#completeBtn").attr('disabled', 'disabled');
		jQuery("#validationBtn").attr('disabled', 'disabled');
    }
}

function doComplete()
{
    var flag = false;
    jQuery("#dataEntryFormDiv input[name='entryfield'],select[name='entryselect']").each(function(){
        jQuery(this).parent().removeClass("errorCell");
        if( jQuery(this).metadata({
            "type":"attr",
            "name":"data"
        }).compulsory ){
            if( !jQuery(this).val() || jQuery(this).val() == "undifined" ){
                flag = true;
                jQuery(this).parent().addClass("errorCell");
            }
        }
    });
    if( flag ){
        alert(i18n_error_required_field);
        return;
    }else {
        if( confirm(i18n_complete_confirm_message) )
		{
			$.postJSON( "completeDataEntry.action",
				{
					programStageInstanceId: getFieldValue('programStageInstanceId')
				},
				function (data)
				{
					jQuery("#dataEntryFormDiv :input").each(function()
					{
						jQuery(this).attr('disabled', 'disabled');
					});
					jQuery("#dataEntryFormDiv").find(".ui-datepicker-trigger").each(function()
					{
						jQuery(this).attr('class', 'hidden');
					});
					jQuery("#dataEntryFormDiv").find(".holder").each(function()
					{
						jQuery(this).attr('class', 'visible');
					});
					
					disable('validationBtn');
					disable('completeBtn');
					disable('executionDate');
					var irregular = jQuery('#entryFormContainer [name=irregular]').val();
					if( irregular == 'true')
					{
						enable('newEncounterBtn');
					}
					
					enable('createEventBtn');
					selection.enable();
		
					hideLoader();
					hideById('contentDiv');
				},'xml');
		}
    }
}

TOGGLE = {
    init : function() {
        jQuery(".togglePanel").each(function(){
            jQuery(this).next("table:first").addClass("sectionClose");
            jQuery(this).addClass("close");
            jQuery(this).click(function(){
                var table = jQuery(this).next("table:first");
                if( table.hasClass("sectionClose")){
                    table.removeClass("sectionClose").addClass("sectionOpen");
                    jQuery(this).removeClass("close").addClass("open");
                    window.scroll(0,jQuery(this).position().top);
                }else if( table.hasClass("sectionOpen")){
                    table.removeClass("sectionOpen").addClass("sectionClose");
                    jQuery(this).removeClass("open").addClass("close");
                }
            });
        });
    }
};

function entryFormContainerOnReady()
{
	var currentFocus = undefined;

    if( jQuery("#entryFormContainer") ) {
		
        if( jQuery("#executionDate").val() != '' )
        {
            toggleContentForReportDate(true);
        }
		
        jQuery("input[name='entryfield'],select[name='entryselect']").each(function(){
            jQuery(this).focus(function(){
                currentFocus = this;
            });
            
            jQuery(this).addClass("inputText");
        });
		
        TOGGLE.init();
		
		
		jQuery("#entryForm :input").each(function()
		{ 
			if( jQuery(this).attr( 'options' )!= null )
			{
				autocompletedField(jQuery(this).attr('id'));
			}
		});
    }
}

//------------------------------------------------------
// Run validation
//------------------------------------------------------

function runValidation()
{
	$('#validateProgramDiv' ).load( 'validateProgram.action' ).dialog({
			title: i18n_violate_validation,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 450
		});
}

//------------------------------------------------------
// Register Irregular-encounter
//------------------------------------------------------

function registerIrregularEncounter()
{
	jQuery.postJSON( "registerIrregularEncounter.action",{}, 
		function( json ) 
		{   
			loadDataEntry();
		});
}

function autocompletedField( idField )
{
	var input = jQuery( "#" +  idField )
	var dataElementId = input.attr( 'dataElementId' );
	var options = new Array();
	options = input.attr('options').replace('[', '').replace(']', '').split(', ');
	options.push(" ");

	input.autocomplete({
			delay: 0,
			minLength: 0,
			source: options,
			select: function( event, ui ) {
				input.val(ui.item.value);
				saveVal( dataElementId );
				input.autocomplete( "close" );
			},
			change: function( event, ui ) {
				if ( !ui.item ) {
					var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
						valid = false;
					for (var i = 0; i < options.length; i++)
					{
						if (options[i].match( matcher ) ) {
							this.selected = valid = true;
							break;
						}
					}
					if ( !valid ) {
						// remove invalid value, as it didn't match anything
						$( this ).val( "" );
						input.data( "autocomplete" ).term = "";
						return false;
					}
				}
				saveVal( dataElementId );
			}
		})
		.addClass( "ui-widget" );

	this.button = $( "<button type='button'>&nbsp;</button>" )
		.attr( "tabIndex", -1 )
		.attr( "title", i18n_show_all_items )
		.insertAfter( input )
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.addClass( "small-button" )
		.click(function() {
			// close if already visible
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				return;
			}

			// work around a bug (likely same cause as #5265)
			$( this ).blur();

			// pass empty string as value to search for, displaying all results
			input.autocomplete( "search", "" );
			input.focus();
		});
}