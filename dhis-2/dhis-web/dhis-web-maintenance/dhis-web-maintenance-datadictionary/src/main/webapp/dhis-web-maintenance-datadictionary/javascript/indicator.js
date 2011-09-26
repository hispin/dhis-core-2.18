function exportPDF( type )
{
	var params = "type=" + type;
	params += "&dataDictionaryId=" + jQuery( '#dataDictionaryList' ).val();
	
	exportPdfByType( type, params );
}

function indicatorTypeChanged()
{
    var type = byId( 'indicatorTypeId' ).options[byId( 'indicatorTypeId' ).selectedIndex].getAttribute( 'number' );
    byId( 'denominatorButton' ).disabled = eval( type );
    if ( eval( type ) )
    {
        setFieldValue( 'denominator', '1' );
    } else
    {
        if ( getFieldValue( 'denominatorFormula' ) == undefined )
        {
            setFieldValue( 'denominator', '' );
        } else
        {
            setFieldValue( 'denominator', getFieldValue( 'denominatorFormula' ) );
        }
    }
}

// -----------------------------------------------------------------------------
// Change indicator group and data dictionary
// -----------------------------------------------------------------------------

function criteriaChanged()
{
    var dataDictionaryId = getListValue( "dataDictionaryList" );

    var url = "indicator.action?&dataDictionaryId=" + dataDictionaryId;

    window.location.href = url;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showIndicatorDetails( indicatorId )
{
    var request = new Request();
    request.setResponseTypeXML( 'indicator' );
    request.setCallbackSuccess( indicatorReceived );
    request.send( '../dhis-web-commons-ajax/getIndicator.action?id=' + indicatorId );
}

function indicatorReceived( indicatorElement )
{
    setInnerHTML( 'nameField', getElementValue( indicatorElement, 'name' ) );

    setInnerHTML( 'shortNameField', getElementValue( indicatorElement, 'shortName' ) );

    var alternativeName = getElementValue( indicatorElement, 'alternativeName' );
    setInnerHTML( 'alternativeNameField', alternativeName ? alternativeName : '[' + i18n_none + ']' );

    var description = getElementValue( indicatorElement, 'description' );
    setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );

    var annualized = getElementValue( indicatorElement, 'annualized' );
    setInnerHTML( 'annualizedField', annualized == "true" ? i18n_yes : i18n_no );

    setInnerHTML( 'indicatorTypeNameField', getElementValue( indicatorElement, 'indicatorTypeName' ) );

    var numeratorDescription = getElementValue( indicatorElement, 'numeratorDescription' );
    setInnerHTML( 'numeratorDescriptionField', numeratorDescription ? numeratorDescription : '[' + i18n_none + ']' );

    var denominatorDescription = getElementValue( indicatorElement, 'denominatorDescription' );
    setInnerHTML( 'denominatorDescriptionField', denominatorDescription ? denominatorDescription : '[' + i18n_none
            + ']' );

    var url = getElementValue( indicatorElement, 'url' );
    setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );

    var lastUpdated = getElementValue( indicatorElement, 'lastUpdated' );
    setInnerHTML( 'lastUpdatedField', lastUpdated ? lastUpdated : '[' + i18n_none + ']' );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove indicator
// -----------------------------------------------------------------------------

function removeIndicator( indicatorId, indicatorName )
{
    removeItem( indicatorId, indicatorName, i18n_confirm_delete, 'removeIndicator.action' );
}
