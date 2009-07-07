
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#588db5" );
    $( this ).css( "color", "#ffffff" );
    $( this ).css( "border", "1px solid #f0f0f0" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#d6e6f2" );
    $( this ).css( "color", "#407298" );
    $( this ).css( "border", "1px solid #d6e6f2" );
  });
});
