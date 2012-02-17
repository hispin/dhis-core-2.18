
// -----------------------------------------------------------------------------
// Page init
// -----------------------------------------------------------------------------

$( document ).ready( function() { pageInit(); } );

function pageInit()
{
	// jQuery ajax setup
	
	$.ajaxSetup(
	{
  		cache: false
	} );
	
	// Zebra stripes in lists
	
	$( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
    $( "table.listTable tbody tr:even" ).addClass( "listRow" );

    // Hover rows in lists
    
    $( "table.listTable tbody tr" ).mouseover( function()
    {
    	$( this ).addClass( "listHoverRow" );
    } );
    $( "table.listTable tbody tr" ).mouseout( function()
    {
        $( this ).removeClass( "listHoverRow" );
    } );
    
    // Hover on rightbar close image
    
    $( "#hideRightBarImg" ).mouseover( function()
    {
    	$( this ).attr( "src", "../images/hide_active.png" );
    } );
    $( "#hideRightBarImg" ).mouseout( function()
    {
        $( this ).attr( "src", "../images/hide.png" );
    } );
        
    // Set dynamic back URLs for about page links
        
	var currentPath = '../dhis-web-commons-about/';
	var backURL = '?backUrl=' + window.location;

	$( "#menuDropDownHelpCenter" ).click( function()
		{
			window.location.href = currentPath + 'help.action' + backURL;
		});
		
	$( "#menuDropDownFeedBack" ).click(	function()
		{
			window.location.href = currentPath + 'displayFeedbackForm.action' + backURL;
		});
		
	$( "#menuDropDownChangeLog" ).click( function()
		{
			window.location.href = currentPath + 'displayChangeLog.action' + backURL;
		});
		
	$( "#menuDropDownSupportiveSoftware" ).click( function()
		{
			window.location.href= currentPath + 'displaySupportiveSoftware.action' + backURL;
		});
	
	$( "#menuDropDownUserAccount" ).click( function()
		{
			window.location.href = currentPath + 'showUpdateUserAccountForm.action' + backURL;
		});
		
	$( "#menuDropDownWebApi" ).click( function()
		{
			window.location.href = '../api';
		});
		
	$( "#menuDropDownAboutDHIS2" ).click( function()
		{
			window.location.href = currentPath + 'about.action' + backURL;
		});
	
	// Intro fade in
	
	$( "#introList" ).fadeIn();
}

// -----------------------------------------------------------------------------
// Menu functions
// -----------------------------------------------------------------------------

var menuTimeout = 500;
var closeTimer = null;
var dropDownId = null;

function showDropDown( id )
{
    cancelHideDropDownTimeout();
    
    var newDropDownId = "#" + id;
  
    if ( dropDownId != newDropDownId )
    {   
        hideDropDown();

        dropDownId = newDropDownId;
        
        $( dropDownId ).show();
    }
}

function hideDropDown()
{
	if ( dropDownId )
	{
	    $( dropDownId ).hide();
	    
	    dropDownId = null;
	}
}

function hideDropDownTimeout()
{
    closeTimer = window.setTimeout( hideDropDown, menuTimeout );
}

function cancelHideDropDownTimeout()
{
    if ( closeTimer )
    {
        window.clearTimeout( closeTimer );
        
        closeTimer = null;
    }
}

// -----------------------------------------------------------------------------
// Leftbar
// -----------------------------------------------------------------------------

var leftBar = new LeftBar();

function LeftBar()
{    
    this.showAnimated = function()
    {
        setMenuVisible();        
        setMainPageNormal( '270px' ); // Delegated to dom.js for each style
        $( 'div#leftBar' ).show( 'fast' );
        $( 'span#showLeftBar' ).hide( 'fast' );
    };
    
    this.hideAnimated = function()
    {
        setMenuHidden();
        setMainPageFullscreen( '20px' );
        $( 'div#leftBar' ).hide( 'fast' );
        $( 'span#showLeftBar' ).show( 'fast' );
    };
    
    this.hide = function()
    {
        setMenuHidden();
        setMainPageFullscreen( '20px' );
        document.getElementById( 'leftBar' ).style.display = 'none';
        document.getElementById( 'showLeftBar' ).style.display = 'block';
    };

	function setMainPageFullscreen()
	{
		document.getElementById( 'mainPage' ).style.marginLeft = '20px';
	}

    function setMenuVisible()
    {
        $.get( '../dhis-web-commons/menu/setMenuVisible.action' );        
    }
    
    function setMenuHidden()
    {        
        $.get( '../dhis-web-commons/menu/setMenuHidden.action' );        
    }    
    
    this.openHelpForm = function( id )
    {
		window.open ("../dhis-web-commons/help/viewDynamicHelp.action?id=" + id,"Help", 'width=800,height=600,scrollbars=yes');
    };
}
