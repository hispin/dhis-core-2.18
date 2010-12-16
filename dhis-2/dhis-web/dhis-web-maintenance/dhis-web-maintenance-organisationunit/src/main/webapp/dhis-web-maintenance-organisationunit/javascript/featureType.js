
// -----------------------------------------------------------------------------
// Set feature type "Point" or "MultiPolygon" from coordinate string (no GeoJSON validation)
// -----------------------------------------------------------------------------

function setFeatureType( field, c )
{
    field.style.color = "#000000";
    field.style.fontStyle = "normal";
    
    if ( c.length >= 2 )
    {
        if ( c.substring( 0, 1 ) == "[" && c.substring( 1, 2 ) != "[" && c.substring( c.length - 1, c.length ) == "]" && c.substring( c.length - 2, c.length - 1 ) != "]" )
        {
            field.value = "Point";
            return;
        }

        if ( c.length >= 8 )
        {
            if ( c.substring( 0, 4 ) == "[[[[" && c.substring( c.length - 4, c.length ) == "]]]]")
            {
                field.value = "MultiPolygon";
                return;
            }
        }
    }
    
    if ( field.value )
    {
        field.value = "";
    }
}

// -----------------------------------------------------------------------------
// Simple feature type / coordinates field validation
// -----------------------------------------------------------------------------

function validateFeatureType( cField, fField )
{
    if ( cField.value && ( !fField.value || fField.value == validationMessage.unrecognizedcoordinatestring ) )
    {
        fField.style.color = "#ff0000";
        fField.style.fontStyle = "italic";
        fField.value = validationMessage.unrecognizedcoordinatestring;
        return false;
    }
    
    return true;
}