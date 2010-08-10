
var basic_operators = 
[
	'*', 'COUNT(*)', 'COUNT(a)', 'DISTINCT()',
	'SUM(a)', 'MIN(a)', 'MAX(a)', 'AVG(a)',
	'AND ()', 'OR ()', 'BETWEEN a AND b'
];
	
var criteria_operators = 
[
	'<', '< \'\'', '< (SELECT  FROM  WHERE)',
	'<=', '<= \'\'', '<= (SELECT  FROM  WHERE)',
	'>', '> \'\'', '> (SELECT  FROM  WHERE)',
	'>=', '>= \'\'', '>= (SELECT  FROM  WHERE)',
	'=', '= \'\'', '= (SELECT  FROM  WHERE)',
	'!=', '!= \'\'', '!= (SELECT  FROM  WHERE)',
	'<>', '<> \'\'', '<> (SELECT  FROM  WHERE)',
	'LIKE', 'LIKE \'\'', 'LIKE \'%\'', 'LIKE \'%%\'',
	'LIKE (SELECT  FROM  WHERE)', 'IN (SELECT  FROM  WHERE)',
	'BETWEEN a AND b', 'IN (a, b)'
];

var criteria_clone = 
[
	'<', '< \'\'', '< (SELECT  FROM  WHERE)',
	'<=', '<= \'\'', '<= (SELECT  FROM  WHERE)',
	'>', '> \'\'', '> (SELECT  FROM  WHERE)',
	'>=', '>= \'\'', '>= (SELECT  FROM  WHERE)',
	'=', '= \'\'', '= (SELECT  FROM  WHERE)',
	'!=', '!= \'\'', '!= (SELECT  FROM  WHERE)',
	'<>', '<> \'\'', '<> (SELECT  FROM  WHERE)',
	'LIKE', 'LIKE \'\'', 'LIKE \'%\'', 'LIKE \'%%\'',
	'LIKE (SELECT  FROM  WHERE)', 'IN (a, b)', 'IN (SELECT  FROM  WHERE)'
];
	
var keywords = 
[
	'SELECT', 'SELECT *', 'SELECT COUNT(*)', 'SELECT DISTINCT()',
	'COUNT(a)', 'SUM(a)', 'MIN(a)', 'MAX(a)', 'AVG(a)', 'AS',
	'FROM', 'WHERE', 'ORDER BY', 'GROUP BY', 'ASC', 'DESC',
	'HAVING', 'HAVING COUNT(*)', 'HAVING SUM(a)', 'HAVING MIN(a)',
	'HAVING MAX(a)', 'HAVING AVG(a)', 'AND ()', 'OR ()', 'BETWEEN a AND b',
	'JOIN <table_name> ON', 'RIGHT JOIN <table_name> ON', 'LEFT JOIN <table_name> ON'
];

		
/**
	The Regex global variables
	Carefully, using the Regex with test() method if pattern has //g
	pattern.test(field_1) --> true
	pattern.test(field_2) --> false
	
	In which, field_1 IS field_2
*/

var resourceComboId = "resource-table-combo";
var propertyComboId = "resource-property-combo";
var aliasFieldId 	= "alias-property";
var showCheckboxId  = "show-checkbox";
var sortComboId		= "sort-property-combo";
var criteriaANDFieldId = "criteria_and_property";
var criteriaORFieldId = "criteria_or_property";
var groupbyCheckboxId = "groupby-property-checkbox";

var regexStar = /^\s*\*\s*$/;

var regexCountStar = /\s*count\s*\(\s*\*\s*\)\s*/i;
var regexSumStar = /\s*sum\s*\(.*\*.*/i;
var regexMinStar = /\s*min\s*\(.*\*.*/i;
var regexMaxStar = /\s*max\s*\(.*\*.*/i;
var regexAvgStar = /\s*avg\s*\(.*\*.*/i;
var regexAverageStar = /\s*average\s*\(.*\*.*/i;

var regexCountOther = /\s*count\s*\(\s*/i;
var regexSumOther = /\s*sum\s*\(\s*/i;
var regexMinOther = /\s*min\s*\(\s*/i;
var regexMaxOther = /\s*max\s*\(\s*/i;
var regexAvgOther = /\s*avg\s*\(\s*/i;
var regexAverageOther = /\s*average\s*\(\s*/i;

var regexCount = /count/i;
var regexSum = /sum/i;
var regexMin = /min/i;
var regexMax = /max/i;
var regexAvg = /avg/i;
var regexAverage = /average/i;

var selectQuery = "SELECT ";
var fromQuery = "FROM ";
var whereQuery = "WHERE ";
var sortQuery = "ORDER BY ";
var havingbyQuery = "HAVING ";
var groupbyQuery = "GROUP BY ";

var table = "";
var field = "";
var alias = "";
var sorttype = "";

var fields = [1];
var insertType = "after";