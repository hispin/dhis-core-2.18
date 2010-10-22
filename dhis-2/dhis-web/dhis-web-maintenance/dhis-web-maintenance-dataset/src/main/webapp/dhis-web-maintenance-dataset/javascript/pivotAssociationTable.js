
// -----------------------------------------------------------------------------
// Global variables
// -----------------------------------------------------------------------------

var dataSets = []; // Array->associative array (id,name)
var orgunits = []; // Array->associative array (id,name)
var data = []; // Associative array [(dataset-periodType-orgunit), (association)]

var sizes = []; // Associative array (dimension, size)

var pivotDataSet = true; // Should correspond to init value in form
var pivotOrgunit = false;

var currentDataSet = 0;
var currentOrgunit = 0;

// -----------------------------------------------------------------------------
// Public methods
// -----------------------------------------------------------------------------

/**
 * This method is called from the UI and is responsible for retrieving data from 
 * the server and setting the global variables.
 */
function getData()
{
  clearGlobalVariables();
 
  var levelList = byId( "level" );
  var periodTypeList = byId("periodType");
  
  var level = levelList.options[ levelList.selectedIndex ].value;
  var periodTypeName = periodTypeList.options[ periodTypeList.selectedIndex ].value;

  document.getElementById( "dataLabel" ).innerHTML = i18n_organisation_unit_level + ": " + level + ", " + i18n_period_type + ": " + periodTypeName;
  
  hideDivs();
  
  showLoader();
  
  $.getJSON(
    "getPivotAssociationTable.action",
    {
      "level": level,
	  "periodTypeName": periodTypeName
    },
    function( json ) 
    {
      var pivot = json.pivottable;

      dataSets = pivot.dataSets;
      orgunits = pivot.organisationUnits;
      
      sizes["dataSet"] = pivot.sizeDataSets;
      sizes["orgunit"] = pivot.sizeOrganisationUnits;
      
      data = pivot.associations[0];
      
      hideLoader();
      
      generateTable();
    }
  );
}

/**
 * This method is called from the UI and is responsible for pivoting the table.
 */
function pivotData()
{
  pivotDataSet = document.getElementById( "dataSetBox" ).checked;
  pivotOrgunit = document.getElementById( "orgunitBox" ).checked;
  
  hideDivs();
  
  generateTable();
}


/**
 * This method is called from the UI and assigns a dataset for orgunit.
 */
function assignDataSet( dataSetId, organisationId, assigned, imgElement )
{
  $.getJSON(
    "defineAssociation.action",
    {
      "dataSetId": dataSetId,
	  "organisationId": organisationId,
	  "assigned": assigned
    },
    function( json ) 
    {
		if ( json.response == "success" )
    	{
			data[dataSetId + '-' + organisationId] = assigned;
			
			var action = 'assignDataSet( ' + 
					dataSetId + ',' + 
					organisationId + ',' + 
					!assigned + ', this)';
					
			if(assigned == true){
				imgElement.src = "../images/check.png";
				imgElement.title = "Unassign";
				imgElement.setAttribute('onclick', action); 
			}else{
				imgElement.src = "../images/cross.png";
				imgElement.title = "Assign";
				imgElement.setAttribute('onclick', action);
			}
		}
    }
  );
}

// -----------------------------------------------------------------------------
// Supportive methods
// -----------------------------------------------------------------------------

/**
 * This method is responsible for generating the pivot table.
 */
function generateTable()
{   
  var columnDataSets = pivotDataSet ? dataSets : [null];
  var columnOrgunits = pivotOrgunit ? orgunits : [null];
  
  var rowDataSets = pivotDataSet ? [null] : dataSets;
  var rowOrgunits = pivotOrgunit ? [null] : orgunits;

  var table = document.getElementById( "pivotTable" );

  clearTable( table );
  
  var columns = getColumns( columnDataSets, columnOrgunits );
  var rows = getRows( rowDataSets, rowOrgunits );
  
  var columnDimensions = getColumnDimensions();
  var rowDimensions = getRowDimensions();
  
  var colSpans = getSpans( columnDimensions );
  var rowSpans = getSpans( rowDimensions );

  var html = "<tr>";

  // ---------------------------------------------------------------------------
  // Column headers
  // ---------------------------------------------------------------------------

  for ( d in columnDimensions )
  {
    for ( rowDimension in rowDimensions ) // Make space for row header
    {
      html += "<td class='row'></td>"; 
    }
    
    var dimension = columnDimensions[d];
    var colSpan = colSpans[dimension];
    
    for ( c in columns )
    {
      var modulus = c % colSpan;
      
      if ( modulus == 0 )
      {
        html += "<td class='column' colspan='" + colSpan + "'>" + columns[c][dimension]  + "</td>";
      }
    }
    
    html += "</tr>";
  }
  
  // ---------------------------------------------------------------------------
  // Rows
  // ---------------------------------------------------------------------------

  for ( r in rows )
  {
    html += "<tr>";    
    
    for ( d in rowDimensions ) // Row headers
    {
      var dimension = rowDimensions[d];
      var rowSpan = rowSpans[dimension];
      var modulus = r % rowSpan;
      
      if ( modulus == 0 )
      {
        html += "<td class='row' rowspan='" + rowSpan + "'>" + rows[r][dimension] + "</td>";
      }
    }
    
    for ( c in columns ) // Values
    {
      var value = getValue( columns[c], rows[r] );
      
      var ids = mergeArrays( columns[c], rows[r] );
      
      html += "<td class='cell' " + value + "</td>";
    }
    
    html += "</tr>";
  }
  
  table.innerHTML = html;
  
  hidePivot();
}

/**
* @param dimensions array -> dimensions
*
* @return associative array ( dimension, span )
*/
function getSpans( dimensions )
{
  var spans = [];
  
  var lastIndex = ( dimensions.length - 1 );
  
  var span = 1;
  
  for ( var i=lastIndex; i>=0; i-- )
  {
    var dimension = dimensions[i];
    
    spans[dimension] = span;
    
    var dimensionSize = sizes[dimension];
    
    span = ( span * dimensionSize );
  }
  
  return spans;
}

/**
* @param columnDataSets array -> associative array ( DataSetId, DataSetName )
* @param columnOrgunits array -> associative array ( orgunitId, orgunitName )
*
* @return array -> associative array ( dataSetId, dataSet, periodTypeId, periodType, orgunitId, orgunit )
*/
function getColumns( columnDataSets, columnOrgunits )
{
  var columns = [];
  var columnsIndex = 0;

  for ( var i=0; i<columnDataSets.length; i++ )
  {
      for ( var k=0; k<columnOrgunits.length; k++ )
      {
        var column = [];
        
        if ( columnDataSets[i] != null )
        {
          column["dataSetId"] = columnDataSets[i].id;
          column["dataSet"] = columnDataSets[i].name;
        }
        
        if ( columnOrgunits[k] != null )
        {
          column["orgunitId"] = columnOrgunits[k].id;
          column["orgunit"] = columnOrgunits[k].name;
        }
        
        columns[columnsIndex++] = column;     
      }
  }
  
  return columns;
}

/**
* @param rowDataSets array -> associative array ( dataSetId, dataSetName )
* @param rowOrgunits array -> associative array ( orgunitId, orgunitName )
*
* @return array -> associative array ( dataSetId, dataSet, orgunitId, orgunit )
*/
function getRows( rowDataSets, rowOrgunits )
{
  var rows = [];
  var rowsIndex = 0;

  for ( var i=0; i<rowDataSets.length; i++ )
  {
      for ( var k=0; k<rowOrgunits.length; k++ )
      {
        var row = [];
        
        if ( rowDataSets[i] != null )
        {
          row["dataSetId"] = rowDataSets[i].id;
          row["dataSet"] = rowDataSets[i].name;
        }
        
        if ( rowOrgunits[k] != null )
        {
          row["orgunitId"] = rowOrgunits[k].id;
          row["orgunit"] = rowOrgunits[k].name;
        }
        
        rows[rowsIndex++] = row;
      }
  }
  
  return rows;
}

/**
* @return array -> dimension
*/
function getColumnDimensions()
{
  var dimensions = [];
   
  if ( pivotDataSet )
  {
    dimensions[dimensions.length] = "dataSet";
  }
  
  if ( pivotOrgunit )
  {
    dimensions[dimensions.length] = "orgunit";
  }
  
  return dimensions;
}

/**
* @return array -> dimension
*/
function getRowDimensions()
{
  var dimensions = [];
   
  if ( !pivotDataSet )
  {
    dimensions[dimensions.length] = "dataSet";
  }
  
  if ( !pivotOrgunit )
  {
    dimensions[dimensions.length] = "orgunit";
  }
  
  return dimensions;
}

/**
 * @param array1 the first associative array.
 * @param array2 the second associative array.
 * 
 * @return an associative array with the merged contents of the input arrays.
 */
function mergeArrays( array1, array2 )
{
  for ( a2 in array2 )
  {
    array1[a2] = array2[a2];
  }
  
  return array1;
}

/**
 * @param column associative array ( columnId, columnName )
 * @param row associative array ( rowId, rowName )
 * 
 * @return the value for the given combination of dimension identifiers.
 */
function getValue( column, row )
{
	var key = mergeArrays( column, row );
	  
	var keyString = key.dataSetId + "-" + key.orgunitId;

	var value = data[keyString];

	value = ( value!= null) ? eval(value) : false;

	if(value==true){
		value = '<img title="Unassign" src="../images/check.png" style="cursor:pointer; " onclick="assignDataSet( '+key.dataSetId+',' + key.orgunitId + ',' + !value + ', this )" />';
	}else{
		value = '<img title="Assign" src="../images/cross.png" style="cursor:pointer; " onclick="assignDataSet( '+key.dataSetId+',' + key.orgunitId + ',' + !value + ', this )" />';
	}
	  
	return value;
}

/**
 * Clears the table.
 */
function clearTable( table )
{
  while ( table.rows.length >  0 )
  {
    table.deleteRow( 0 );
  }
}

/**
 * Clears the global variables.
 */
function clearGlobalVariables()
{
  dataSets.length = 0;
  orgunits.length = 0;
  data.length = 0;
  sizes.length = 0;
}
