$(document).ready(function(){
    if($(window).width() <= 800){
        $('img#menuSeparator1').css('left','400px');
        $('img#menuSeparator2').css('left','490px');
        $('img#menuSeparator3').css('left','580px');
        $('img#menuSeparator4').css('left','670px');
        $('img#menuSeparator5').css('left','750px');
        $('div#menuLink1').css('left','398px');
        $('div#menuLink2').css('left','485px');
        $('div#menuLink3').css('left','580px');
        $('div#menuLink4').css('left','665px');
        $('div#menuDropDown1').css('left','400px');
        $('div#menuDropDown2').css('left','490px');
        $('div#menuDropDown3').css('left','580px');

        $('#leftBar').css('width','175px');
        $('#leftBarContents h2').css('margin-right','10px');
        $('#leftBarContents h2').css('margin-left','10px');
        $('#leftBarContents ul').css('margin-left','30px');
        $('#leftBarContents img').css('margin-left','10px');

        $('#orgUnitTree').css('width','172px');

        $('#mainPage').css('margin-left','200px');
        $('div#orgUnitTree ul').css('margin-left','10px');
    }
});

var leftBar = new LeftBar();

function LeftBar()
{    
    this.showAnimated = function()
    {
        setMenuVisible();        
        setMainPageLeftMargin( '300px' );
        $( 'div#leftBar' ).show( 'fast' );
        $( 'span#showLeftBar' ).hide( 'fast' );
    };
    
    this.hideAnimated = function()
    {
        setMenuHidden();
        setMainPageLeftMargin( '20px' );
        $( 'div#leftBar' ).hide( 'fast' );
        $( 'span#showLeftBar' ).show( 'fast' );
    };
    
    this.hide = function()
    {
        setMenuHidden();
        setMainPageLeftMargin( '20px' );
        document.getElementById( 'leftBar' ).style.display = 'none';
        document.getElementById( 'showLeftBar' ).style.display = 'block';
    } 

    function setMainPageLeftMargin( width )
    {
        document.getElementById( 'mainPage' ).style.marginLeft = width;
    }
        
    function setMenuVisible()
    {
        var request = new Request();
        request.send( '../dhis-web-commons/menu/setMenuVisible.action' );        
    }
    
    function setMenuHidden()
    {        
        var request = new Request();
        request.send( '../dhis-web-commons/menu/setMenuHidden.action' );        
    }

    this.openHelpForm = function( id )
    {
		window.open ("../dhis-web-commons/help/viewDynamicHelp.action?id=" + id,"Help", 'width=800,height=600,scrollbars=yes');
    }    
}
