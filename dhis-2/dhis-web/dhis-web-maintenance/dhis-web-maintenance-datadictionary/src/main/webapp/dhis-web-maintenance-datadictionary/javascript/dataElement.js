function changeValueType( value )
{
	if( value == 'int' ){
		enable( 'calculated' );
		enable( 'zeroIsSignificant' );
	}else{
		disable( 'calculated' );		
		disable( 'zeroIsSignificant' );		
	}	
	
	updateAggreationOperation( value );
}

function updateAggreationOperation( value )
{
	if( value== 'string' || value== 'date')
	{		
        hideById("aggregationOperator");
	}else{		
        showById("aggregationOperator");
	}
}

function changeCategory( value )
{	
	if( value == getFieldValue( 'defaultCategoryCombo') ){
		enable( 'calculated' );
	}else{
		disable( 'calculated' );
	}
}
// -----------------------------------------------------------------------------
// Change data element group and data dictionary
// -----------------------------------------------------------------------------

function criteriaChanged()
{
    var dataDictionaryId = getListValue( "dataDictionaryList" );
	
    var url = "dataElement.action?&dataDictionaryId=" + dataDictionaryId;
	
    window.location.href = url;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataElementDetails( dataElementId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElement' );
    request.setCallbackSuccess( dataElementReceived );
    request.send( '../dhis-web-commons-ajax/getDataElement.action?id=' + dataElementId );
}

function dataElementReceived( dataElementElement )
{
    setInnerHTML( 'nameField', getElementValue( dataElementElement, 'name' ) );
    setInnerHTML( 'shortNameField', getElementValue( dataElementElement, 'shortName' ) );

    var alternativeName = getElementValue( dataElementElement, 'alternativeName' );
    setInnerHTML( 'alternativeNameField', alternativeName ? alternativeName : '[' + i18n_none + ']' );
    
    var description = getElementValue( dataElementElement, 'description' );
    setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );

    var active = getElementValue( dataElementElement, 'active' );
    setInnerHTML( 'activeField', active == 'true' ? i18n_yes : i18n_no );
    
    var typeMap = { 
        'int':i18n_number,
        'bool':i18n_yes_no,
        'string':i18n_text
    };
    var type = getElementValue( dataElementElement, 'valueType' );
    setInnerHTML( 'typeField', typeMap[type] );
    
    var domainTypeMap = {
        'aggregate':i18n_aggregate,
        'patient':i18n_patient
    };
    var domainType = getElementValue( dataElementElement, 'domainType' );
    setInnerHTML( 'domainTypeField', domainTypeMap[domainType] );
    
    var aggregationOperator = getElementValue( dataElementElement, 'aggregationOperator' );
    var aggregationOperatorText = i18n_none;
    if ( aggregationOperator == 'sum' )
    {
        aggregationOperatorText = i18n_sum;
    }
    else if ( aggregationOperator == 'average' )
    {
        aggregationOperatorText = i18n_average;
    }
    setInnerHTML( 'aggregationOperatorField', aggregationOperatorText );   
    
    setInnerHTML( 'categoryComboField', getElementValue( dataElementElement, 'categoryCombo' ) );
    
    var url = getElementValue( dataElementElement, 'url' );
    setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );
	
    var lastUpdated = getElementValue( dataElementElement, 'lastUpdated' );
    setInnerHTML( 'lastUpdatedField', lastUpdated ? lastUpdated : '[' + i18n_none + ']' );
	
    showDetails();
}

function getDataElements( dataElementGroupId, type, filterCalculated )
{
    var url = "getDataElementGroupMembers.action?";

    if ( dataElementGroupId == '[select]' )
    {
        return;
    }

    if ( dataElementGroupId != null )
    {
        url += "dataElementGroupId=" + dataElementGroupId;
    }
	
    if ( type != null )
    {
        url += "&type=" + type
    }

    if ( filterCalculated )
    {
        url += "&filterCalculated=on";
    }

    var request = new Request();
    request.setResponseTypeXML( 'operand' );
    request.setCallbackSuccess( getDataElementsReceived );
    request.send( url );
}

function getDataElementsReceived( xmlObject )
{	
    var availableDataElements = document.getElementById( "availableDataElements" );
		
    clearList( availableDataElements );
	
    var operands = xmlObject.getElementsByTagName( "operand" );
	
    for ( var i = 0; i < operands.length; i++ )
    {
        var id = operands[ i ].getElementsByTagName( "operandId" )[0].firstChild.nodeValue;
        var dataElementName = operands[ i ].getElementsByTagName( "operandName" )[0].firstChild.nodeValue;
		
        var option = document.createElement( "option" );
        option.value = id;
        option.text = dataElementName;
        option.title = dataElementName;
        availableDataElements.add( option, null );
    }
}
// -----------------------------------------------------------------------------
// Remove data element
// -----------------------------------------------------------------------------

function removeDataElement( dataElementId, dataElementName )
{
    removeItem( dataElementId, dataElementName, i18n_confirm_delete, 'removeDataElement.action' );
}
// -----------------------------------------------------------------------------
// Calculated Data Elements
// -----------------------------------------------------------------------------

/**
 * Adds a set of data elements to the CDE table.
 * Either add the selected ones, or add all.
 * @param requireSelect Whether to only add the selected data elements
 */
function addCDEDataElements()
{
	var availableList = jQuery("#availableDataElements option");
	
	jQuery("#selectedDataElements tr.placeholder").remove();
	
	var selectedList = jQuery("#selectedDataElements")
	
	jQuery.each( availableList, function(i, item){
		if( item.selected ){
			var id = item.value;
			var name = item.firstChild.nodeValue;
			jQuery( item ).remove();
			addCDEDataElement( id, name, i);			
		}
	});
	
	updateValidatorRulesForFactors();
}

function addCDEDataElement( id, name, index )
{
    var tr = document.createElement('tr');

    var nameTd = tr.appendChild(document.createElement('td'));
    nameTd.appendChild(document.createTextNode(name));
    var idInput = nameTd.appendChild(document.createElement('input'));
    idInput.type = 'hidden';
    idInput.name = 'dataElementIds';
    idInput.value = id;

    var factorTd = tr.appendChild(document.createElement('td'));
    var factorInput = factorTd.appendChild(document.createElement('input'));
    factorInput.type = 'text';
    factorInput.name = 'factors' + index;
    factorInput.value = 1;

    var opTd = tr.appendChild(document.createElement('td'));
    var button = opTd.appendChild(document.createElement('button')); //TODO: i18n
    button.setAttribute('title', 'Remove from list');
    button.setAttribute('type', 'button');
    button.onclick = removeCDEDataElement;
    var delIcon = button.appendChild(document.createElement('img'));
    delIcon.setAttribute( 'src', '../images/delete.png' );
    delIcon.setAttribute( 'alt', 'Delete icon' );
    
    var selectedTable = byId('selectedDataElements');
    selectedTable.appendChild(tr);
}

/**
 * Remove all elements from the CDE table.
 */
function removeCDEDataElements( e )
{	
	var trs = jQuery( "#selectedDataElements tr[class!=placeholder]:gt(0)");
	
	jQuery.each( trs, function(i, item){		
		var deId = jQuery( "input[name=dataElementIds]", item )[0].value;
		var deName = jQuery( "td", item )[0].firstChild.nodeValue
		jQuery(item).remove();
		jQuery("#availableDataElements").append( '<option value="' + deId + '" selected="true">' + deName + '</option>' );
		
	});	   
}

/**
 * Remove one data element row from the CDE form.
 */
function removeCDEDataElement( e )
{    
	var tr = jQuery( this ).parent().parent();	
	var deId = tr.find( "input[name=dataElementIds]").val();
	var deName = tr.find( "td:first" ).text();
	tr.remove();
	jQuery("#availableDataElements").append( '<option value="' + deId + '" selected="true">' + deName + '</option>' );
	
}

function updateValidatorRulesForFactors()
{
	var inputs = jQuery("#selectedDataElements input[name*=factors]");
	
	jQuery.each(inputs, function(i, item ){
		removeValidatorRules( item );
		addValidatorRules( item, {required:true, number:true});			
	});
}

function removeValidatorRulesForFactors()
{
	var inputs = jQuery("#selectedDataElements input[name*=factors]");
	
	jQuery.each(inputs, function(i, item ){			
		removeValidatorRules( item );		
	});
}

/**
 * Display or hide an element
 * @param id Id of the element to toggle
 * @param display Whether or not to display the element
 */
function toggleCDEForm()
{    
	id = 'calculatedContainer';
	display = isChecked( 'calculated' );
	
    if( display )
    {
		disable( 'valueType' );
		disable( 'selectedCategoryComboId' );
		showById( id );	
		addValidatorRulesById("selectedDEValidator", {
			required: true			
		});
		updateValidatorRulesForFactors();
    }
    else
    {
        enable( 'valueType' );
        enable( 'selectedCategoryComboId' );
		hideById( id );
		removeValidatorRulesById("selectedDEValidator");
		removeValidatorRulesForFactors();
    }
}

function getFactors()
{
	var factorsSubmit = jQuery("#factorsSubmit");
	var inputs = jQuery("#selectedDataElements input[name*=factors]");

	jQuery.each(inputs, function(i, item ){		
		factorsSubmit.append('<option value="' + item.value + '" selected="selected">' + item.value + '</option>');	
	});
}

function getDataElementIdsForValidate()
{
	var dataElementValidators = jQuery("#selectedDEValidator");
	var inputs = jQuery( "#selectedDataElements input[type=hidden]" );
	
	dataElementValidators.children().remove();
	
	jQuery.each(inputs, function(i, item ){				
		dataElementValidators.append('<option value="' + item.value + '" selected="selected">' + item.value + '</option>');	
	});
}
