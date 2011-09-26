function exportPDF( type )
{	
	var params = "type=" + type;
	params += "&dataDictionaryId=" + jQuery( '#dataDictionaryList' ).val();
	
	exportPdfByType( type, params );
}

function changeValueType( value )
{
    if ( value == 'int' )
    {
        enable( 'zeroIsSignificant' );
    } else
    {
        disable( 'zeroIsSignificant' );
    }

    updateAggreationOperation( value );
}

function updateAggreationOperation( value )
{
    if ( value == 'string' || value == 'date' )
    {
        hideById( "aggregationOperator" );
    } else
    {
        showById( "aggregationOperator" );
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
	jQuery.get( '../dhis-web-commons-ajax-json/getDataElement.action', 
		{ "id": dataElementId }, function( json ) {
		setInnerHTML( 'nameField', json.dataElement.name );
		setInnerHTML( 'shortNameField', json.dataElement.shortName );

		var alternativeName = json.dataElement.alternativeName;
		setInnerHTML( 'alternativeNameField', alternativeName ? alternativeName : '[' + i18n_none + ']' );

		var description = json.dataElement.description;
		setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );

		var active = json.dataElement.active;
		setInnerHTML( 'activeField', active == 'true' ? i18n_yes : i18n_no );

		var typeMap = {
			'int' : i18n_number,
			'bool' : i18n_yes_no,
			'string' : i18n_text
		};
		var type = json.dataElement.valueType;
		setInnerHTML( 'typeField', typeMap[type] );

		var domainTypeMap = {
			'aggregate' : i18n_aggregate,
			'patient' : i18n_patient
		};
		var domainType = json.dataElement.domainType;
		setInnerHTML( 'domainTypeField', domainTypeMap[domainType] );

		var aggregationOperator = json.dataElement.aggregationOperator;
		var aggregationOperatorText = i18n_none;
		if ( aggregationOperator == 'sum' )
		{
			aggregationOperatorText = i18n_sum;
		} else if ( aggregationOperator == 'average' )
		{
			aggregationOperatorText = i18n_average;
		}
		setInnerHTML( 'aggregationOperatorField', aggregationOperatorText );

		setInnerHTML( 'categoryComboField', json.dataElement.categoryCombo );

		var url = json.dataElement.url;
		setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );

		var lastUpdated = json.dataElement.lastUpdated;
		setInnerHTML( 'lastUpdatedField', lastUpdated ? lastUpdated : '[' + i18n_none + ']' );
		
		var temp = '';
		var dataSets = json.dataElement.dataSets;
		for ( var i = 0 ; i < dataSets.length ; i ++ )
		{
			temp += dataSets[i].name + '<br/>';
		}
		setInnerHTML( 'dataSetsField', temp ? temp : '[' + i18n_none + ']' );
	
		showDetails();
	});
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
        url += "&type=" + type;
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
        var id = operands[i].getElementsByTagName( "operandId" )[0].firstChild.nodeValue;
        var dataElementName = operands[i].getElementsByTagName( "operandName" )[0].firstChild.nodeValue;

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
