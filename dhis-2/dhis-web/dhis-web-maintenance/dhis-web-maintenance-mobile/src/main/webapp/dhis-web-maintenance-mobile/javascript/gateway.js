function changeValueType( value )
{
	hideAll();
    if ( value == 'modem')
    {
        showById( "modemFields" );
    } else if (value == 'bulksms') 
    {
    	showById( "bulksmsFields" );
    } else 
    {
    	showById( "clickatellFields" );
    }
}

function hideAll() 
{
	 hideById( "modemFields" );
	 hideById( "bulksmsFields" );
	 hideById( "clickatellFields" );
}