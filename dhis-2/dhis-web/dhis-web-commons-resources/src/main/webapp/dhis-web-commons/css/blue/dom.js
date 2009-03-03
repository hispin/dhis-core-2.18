
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#5F6F98" );
    $( this ).css( "border", "1px solid #D0D0D0" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#2a3857" );
    $( this ).css( "border", "1px solid #2a3857" );
  });
});
