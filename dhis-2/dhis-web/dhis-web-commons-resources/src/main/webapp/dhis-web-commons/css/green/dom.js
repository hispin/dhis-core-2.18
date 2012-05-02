
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#679a6d" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#4a6f4d" );
  });
  
  $( "li.introItem" ).mouseover( function() // Over intro item
  {
    $( this ).css( "background-color", "#9dc69c" );
  });
  
  $( "li.introItem" ).mouseout( function() // Out intro item
  {
    $( this ).css( "background-color", "#d3e5d3" );
  });
});

// Called from main/Leftbar
function setMainPageNormal()
{
	document.getElementById( 'mainPage' ).style.marginLeft = '270px';
}
