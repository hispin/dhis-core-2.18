
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#f5790b" );
    $( this ).css( "border", "1px solid #ffffff" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#518a0f" );
    $( this ).css( "border", "1px solid #518a0f" );
  });
});
