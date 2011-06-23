var selectedOrganisationUnit = null;

var distributionDivs = [ "chartDiv", "tableDiv", "loaderDiv" ];

function organisationUnitSelected( units )
{
    if ( units && units[0] )
    {
        selectedOrganisationUnit = units[0];
    } else
    {
        selectedOrganisationUnit = null;
    }
}

function displayOrgUnitDistribution()
{
    if ( inputInvalid() )
    {
        return false;
    }

    displayDiv( "loaderDiv", distributionDivs );

    var groupSetId = $( "#groupSetId" ).val();
    var url = "getOrgUnitDistribution.action?groupSetId=" + groupSetId + "&type=html&r=" + getRandomNumber();
    $( "#tableDiv" ).load( url, function()
    {
        displayDiv( "tableDiv", distributionDivs );
        pageInit();
    } );
}

function getOrgUnitDistribution( type )
{
    if ( inputInvalid() )
    {
        return false;
    }

    var groupSetId = $( "#groupSetId" ).val();
    var url = "getOrgUnitDistribution.action?groupSetId=" + groupSetId + "&type=" + type + "&r=" + getRandomNumber();
    window.location.href = url;
}

function displayOrgUnitDistributionChart()
{
    if ( inputInvalid() )
    {
        return false;
    }

    displayDiv( "chartDiv", distributionDivs );
    $( "#chartImg" ).attr( "src", "../images/ajax-loader-circle.gif" );
    var groupSetId = $( "#groupSetId" ).val();
    var source = "getOrgUnitDistributionChart.action?groupSetId=" + groupSetId + "&r=" + getRandomNumber();
    $( "#chartImg" ).attr( "src", source );
}

function inputInvalid()
{
    var groupSetId = $( "#groupSetId" ).val();

    if ( groupSetId == null || groupSetId == 0 )
    {
        setHeaderDelayMessage( i18n_select_group_set );
        return true;
    }

    if ( selectedOrganisationUnit == null || selectedOrganisationUnit == "" )
    {
        setHeaderDelayMessage( i18n_select_org_unit );
        return true;
    }

    return false;
}
