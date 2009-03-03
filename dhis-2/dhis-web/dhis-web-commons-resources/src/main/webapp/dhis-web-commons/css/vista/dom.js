
$( document ).ready( function()
{        
  $( "li.menuDropDownItem" ).mouseover( function() // Over dropdown item
  {
    $( this ).css( "background-color", "#5B5B5B" );
    $( this ).css( "border", "1px solid #EAEAEA" );
  });
  
  $( "li.menuDropDownItem" ).mouseout( function() // Out dropdown item
  {
    $( this ).css( "background-color", "#2f2f2f" );
    $( this ).css( "border", "1px solid #2f2f2f" );
  });
});
