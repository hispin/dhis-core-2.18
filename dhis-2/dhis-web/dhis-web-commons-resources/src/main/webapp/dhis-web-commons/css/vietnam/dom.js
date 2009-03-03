
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#942d32" );
    $( this ).css( "border", "1px solid #d0d0d0" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#7B0A0F" );
    $( this ).css( "border", "1px solid #7B0A0F" );
  });
});
