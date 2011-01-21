
// -------------------------------------------------------------------------
// Public methods
// -------------------------------------------------------------------------

function addOption( list, value, text )
{
  var option = document.createElement( "option" );
  option.value = value;
  option.text = text;
  list.add( option, null );
}

function loadIndicatorGroups()
{
  var list = byId( "indicatorGroup" );
    
  $.getJSON(
    "getIndicatorGroups.action",
    function( json )
    {
      for ( var i=0; i<json.indicatorGroups.length; i++ )
      {
         var id = json.indicatorGroups[i].id;
         var name = json.indicatorGroups[i].name;
         
         addOption( list, id, name );
      }
    }
  );
}

function loadPeriodTypes()
{
  var list = byId( "periodType" );
    
  $.getJSON(
    "getPeriodTypes.action",
    function( json )
    {
      for ( var i=0; i<json.periodTypes.length; i++ )
      {
        var name = json.periodTypes[i].name;
        
        addOption( list, name, name );
      }
    }
  );
}

function showCriteria()
{
  $( "div#criteria" ).show( "fast" );
}

function hideCriteria()
{
  $( "div#criteria" ).hide( "fast" );
}

function showPivot()
{
  $( "div#pivot" ).show( "fast" );
}

function hidePivot()
{
  $( "div#pivot" ).hide( "fast" );
}

function hideDivs()
{
  hideCriteria();
  hidePivot();
}

