// -------------------------------------------------------------------------
// Public methods
// -------------------------------------------------------------------------

function toggleDataType()
{
    $( "#indicatorGroupDiv" ).toggle();
    $( "#dataElementGroupDiv" ).toggle();
}

function showCriteria()
{
    $( "div#criteria" ).show( "fast" );
}

function hideCriteria()
{
    $( "div#criteria" ).hide( "fast" );
}

function showPivot()
{
    $( "div#pivot" ).show( "fast" );
}

function hidePivot()
{
    $( "div#pivot" ).hide( "fast" );
}

function hideDivs()
{
    hideCriteria();
    hidePivot();
}
