
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#4f85bb" );
    $( this ).css( "border", "1px solid #e5e5e5" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#265687" );
    $( this ).css( "border", "1px solid #265687" );
  });
});
