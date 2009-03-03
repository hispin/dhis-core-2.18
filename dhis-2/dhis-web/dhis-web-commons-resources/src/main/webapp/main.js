
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
}
