
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#5a845d" );
    $( this ).css( "border", "1px solid #D0D0D0" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#405f43" );
    $( this ).css( "border", "1px solid #405f43" );
  });
});
