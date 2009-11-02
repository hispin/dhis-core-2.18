
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#ffcc34" );
    $( this ).css( "border", "1px solid #ffffff" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#405f43" );
    $( this ).css( "border", "1px solid #405f43" );
  });
  
  $( "li.introItem" ).mouseover( function() // Over intro item
  {
    $( this ).css( "background-color", "#a4d2a3" );
    $( this ).css( "border", "1px solid #ffffff" );
  });
  
  $( "li.introItem" ).mouseout( function() // Out intro item
  {
    $( this ).css( "background-color", "#d5efd5" );
    $( this ).css( "border", "1px solid #d5efd5" );
  });
});
