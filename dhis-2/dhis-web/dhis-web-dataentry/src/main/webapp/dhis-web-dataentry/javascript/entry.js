/**
 * This file depends on form.js.
 * 
 * Format for the span/input identifiers for selectors:
 * 
 * {dataelementid}-{optioncomboid}-val // data value {dataelementid}-dataelement //
 * name of data element {optioncomboid}-optioncombo // name of category option
 * combo {dataelementid}-cell // table cell for data element name
 * {dataelementid}-{optioncomboid}-min // min value for data value
 * {dataelementid}-{optioncomboid}-max // max value for data value
 */

// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------
var FORMULA_PATTERN = /\[.+?\]/g;
var SEPARATOR = '.';

/**
 * Updates all indicator input fields with the calculated value based on the
 * values in the input entry fields in the form.
 */
function updateIndicators()
{
    var entryFieldValues = getEntryFieldValues();

    $( 'input[name="indicator"]' ).each( function( index )
    {
        var indicatorId = $( this ).attr( 'indicatorid' );

        var formula = indicatorFormulas[indicatorId];

        var expression = generateExpression( formula );

        var value = eval( expression );

        value = isNaN( value ) ? '-' : Math.round( value );

        $( this ).attr( 'value', value );
    } );
}

/**
 * Returns an associative array with an entry for each entry input field in the
 * form where the key is the input field id and the value is the input field
 * value.
 */
function getEntryFieldValues()
{
    var entryFieldValues = new Array();

    $( 'input[name="entryfield"]' ).each( function( index )
    {
        entryFieldValues[$( this ).attr( 'id' )] = $( this ).attr( 'value' );
    } );

    return entryFieldValues;
}

/**
 * Parses the expression and substitues the operand identifiers with the value
 * of the corresponding input entry field.
 */
function generateExpression( expression )
{
    var matcher = expression.match( FORMULA_PATTERN );

    for ( k in matcher )
    {
        var match = matcher[k];
        var operand = match.replace( /[\[\]]/g, '' ); // Remove brackets from
                                                        // expression to
                                                        // simplify extraction
                                                        // of identifiers

        var dataElementId = operand.substring( 0, operand.indexOf( SEPARATOR ) );
        var categoryOptionComboId = operand.substring( operand.indexOf( SEPARATOR ) + 1, operand.length );

        var fieldId = '#' + dataElementId + '-' + categoryOptionComboId + '-val';

        var value = $( fieldId ) && $( fieldId ).val() ? $( fieldId ).val() : '0';

        expression = expression.replace( match, value ); // TODO signed
                                                            // numbers
    }

    return expression;
}

/**
 * /* Used by default and section forms.
 */
function saveVal( dataElementId, optionComboId )
{
    var dataElementName = dataElements[dataElementId].name;
    var fieldId = '#' + dataElementId + '-' + optionComboId + '-val';
    var value = $( fieldId ).val();
    var type = dataElements[dataElementId].type;

    $( fieldId ).css( 'background-color', COLOR_YELLOW );

    var periodId = $( '#selectedPeriodId' ).val();

    if ( value )
    {
        if ( type == 'int' || type == 'number' || type == 'positiveNumber' || type == 'negativeNumber' )
        {
            if ( value.length > 255 )
            {
                return alertField( fieldId, i18n_value_too_long + '\n\n' + dataElementName );
            }
            if ( type == 'int' && !isInt( value ) )
            {
                return alertField( fieldId, i18n_value_must_integer + '\n\n' + dataElementName );
            }
            if ( type == 'number' && !isRealNumber( value ) )
            {
                return alertField( fieldId, i18n_value_must_number + '\n\n' + dataElementName );
            }
            if ( type == 'positiveNumber' && !isPositiveInt( value ) )
            {
                return alertField( fieldId, i18n_value_must_positive_integer + '\n\n' + dataElementName );
            }
            if ( type == 'negativeNumber' && !isNegativeInt( value ) )
            {
                return alertField( fieldId, i18n_value_must_negative_integer + '\n\n' + dataElementName );
            }
            if ( isValidZeroNumber( value ) )
            {
                // If value is 0 and zero is not significant for data element,
                // skip value
                if ( significantZeros.indexOf( dataElementId ) == -1 )
                {
                    $( fieldId ).css( 'background-color', COLOR_GREEN );
                    return false;
                }
            }

            var minString = currentMinMaxValueMap[dataElementId + '-' + optionComboId + '-min'];
            var maxString = currentMinMaxValueMap[dataElementId + '-' + optionComboId + '-max'];

            if ( minString && maxString ) // TODO if only one exists?
            {
                var valueNo = new Number( value );
                var min = new Number( minString );
                var max = new Number( maxString );

                if ( valueNo < min )
                {
                    var valueSaver = new ValueSaver( dataElementId, optionComboId, currentOrganisationUnitId, periodId,
                            value, COLOR_ORANGE );
                    valueSaver.save();

                    window.alert( i18n_value_of_data_element_less + ': ' + min + '\n\n' + dataElementName );
                    return;
                }

                if ( valueNo > max )
                {
                    var valueSaver = new ValueSaver( dataElementId, optionComboId, currentOrganisationUnitId, periodId,
                            value, COLOR_ORANGE );
                    valueSaver.save();

                    window.alert( i18n_value_of_data_element_greater + ': ' + max + '\n\n' + dataElementName );
                    return;
                }
            }
        }

        var valueSaver = new ValueSaver( dataElementId, optionComboId, currentOrganisationUnitId, periodId, value,
                COLOR_GREEN );
        valueSaver.save();

        updateIndicators(); // Update indicators in case of custom form
    }
}

function saveBoolean( dataElementId, optionComboId )
{
    var fieldId = '#' + dataElementId + '-' + optionComboId + '-val';
    var value = $( fieldId + ' option:selected' ).val();

    $( fieldId ).css( 'background-color', COLOR_YELLOW );

    var periodId = $( '#selectedPeriodId' ).val();

    var valueSaver = new ValueSaver( dataElementId, optionComboId, currentOrganisationUnitId, periodId, value,
            COLOR_GREEN );
    valueSaver.save();
}

/**
 * Supportive method.
 */
function alertField( fieldId, alertMessage )
{
    $( fieldId ).css( fieldId, COLOR_YELLOW );
    $( fieldId ).select();
    $( fieldId ).focus();
    alert( alertMessage );

    return false;
}

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId_, optionComboId_, organisationUnitId_, periodId_, value_, resultColor_ )
{
    var dataElementId = dataElementId_;
    var optionComboId = optionComboId_;
    var value = value_;
    var resultColor = resultColor_;
    var organisationUnitId = organisationUnitId_;
    var periodId = periodId_;

    this.save = function()
    {
        var url = 'saveValue.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' + dataElementId
                + '&optionComboId=' + optionComboId + '&periodId=' + periodId + '&value=' + value;

        $.ajax( {
            url : url,
            dataType : 'json',
            success : handleResponse,
            error : handleError
        } );
    };

    function handleResponse( json )
    {
        var code = json.c;

        if ( code == 0 )
        {
            markValue( resultColor );
        }
        else
        {
            markValue( COLOR_RED );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }

    function handleError( jqXHR, textStatus, errorThrown )
    {
        markValue( COLOR_RED );
        window.alert( i18n_saving_value_failed_status_code + '\n\n' + textStatus );
    }

    function markValue( color )
    {
        $( '#' + dataElementId + '-' + optionComboId + '-val' ).css( 'background-color', color );
    }
}
