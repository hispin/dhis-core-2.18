/**
	The global variables
	@param currentUrlLink this url string use for "OrganisationUnit" browser option
	@param currentParentId currentParentId is the indentifider of selected organisationunit
*/

currentUrlLink = "";
currentParentId = "";

// ---------------------------------------------------------------

function validateForDrillDown()
{
    var periodType = getListValue( "periodTypeId" );
    var fromDate = byId( "fromDate" ).value.split('-');
    var toDate = byId( "toDate" ).value.split('-');
    var mode = byId( "searchOption" ).value;
    
    if ( periodType == "null" )
    {
        setMessage( i18n_drilldown_choose_period_type );
        return false;
    }
    
    if ( fromDate[0] != "" && fromDate.length != 3 )
    {
        setMessage( i18n_drilldown_fromdate_invalid );
        return false;
    }
    
    if ( toDate[0] != "" && toDate.length != 3 )
    {
        setMessage( i18n_drilldown_enddate_invalid );
        return false;
    }
    
    if ( fromDate[0] > toDate[0] )
    {
        setMessage( i18n_drilldown_fromdate_is_later_than_todate );
        return false;
    }
    
    if ( fromDate[0] == toDate[0] && fromDate[1] > toDate[1] )
    {
        setMessage( i18n_drilldown_fromdate_is_later_than_todate );
        return false;
    }
    
    if ( fromDate[0] == toDate[0] && fromDate[1] == toDate[1] && fromDate[2] > toDate[2] )
    {
        setMessage( i18n_drilldown_fromdate_is_later_than_todate );
        return false;
    }
    
    if ( mode == "null" )
    {
        setMessage( i18n_drilldown_select_browse_mode );
        return false;
    }
    
    return true;
}

function modeHandler()
{
    var modeList = byId( "searchOption" );
    var modeSelection = modeList.value;
    
    var treeSection = byId( "organisationUnitSection" );
    var drillDownCheckBoxDiv = byId( "drillDownCheckBoxDiv" );
    
    if ( modeSelection == "OrganisationUnit" )
    {   
        treeSection.style.display = "block";
        drillDownCheckBoxDiv.style.display = "block";
    }
    else
    {
        treeSection.style.display = "none";
		drillDownCheckBoxDiv.style.display = "none";
		byId( "drillDownCheckBox" ).checked = false;
    }
}

// -----------------------------------------------------------------------------
// Supportive methods
// -----------------------------------------------------------------------------

/**
 * Loads the event listeners for the drill-down table. Called after page is loaded.
 */
function loadListeners()
{
	var table = byId( "drillDownTable" );

	if ( table != null )
	{
		table.addEventListener( "click", setPosition, false );
	}
}

/**
* This method sets the position of the drill-down menu, and is registered as a 
* callback function for mouse click events.
*/
function setPosition( e )
{
  var left = e.pageX + "px";
  var top = e.pageY + "px";
  
  var drillDownMenu = byId( "drillDownMenu" );
  
  drillDownMenu.style.left = left;
  drillDownMenu.style.top = top;
}

/**
 * This method is called from the UI and will display the drildown menu.
 * 
 * @param urlLink this url string use for "OrganisationUnit" browser option
 * @param parentId parentId is the identifier of selected organisation unit
 */
function viewDrillDownMenu( urlLink, parentId )
{
	currentUrlLink = urlLink;
	currentParentId = parentId;

	showDropDown( "drillDownMenu" );
}

/**
 * This method is called from the UI and will display the drildown data.
 * 
 * @param levelStyle levelStyle is the view style of data
 */
function viewDrillDownData( levelStyle )
{
	if ( levelStyle == "current_level" )
	{
		currentUrlLink = currentUrlLink + "&parent=" + currentParentId;
	}
	
	hideDropDown();  
	window.location.href = currentUrlLink;
}


