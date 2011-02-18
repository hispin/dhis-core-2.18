/**
	The global variables
	@param currentUrlLink this url string use for "OrganisationUnit" browser option
	@param currentParentId currentParentId is the indentifider of selected organisationunit
*/

currentUrlLink = "";
currentParentId = "";

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

function changeType( type )
{
	type == "xls" ? disable( "pageLayout" ) : enable( "pageLayout" );
}