-- Function: create_eav_datavalue(integer, integer, integer, integer)
--Returns an (objectid, attribute, value)  triplet for  a given 
-- dataelement and sourceid
-- DROP FUNCTION create_eav_datavalueset(integer, integer, integer, integer);
-- CREATE TYPE eav_text as (objectid integer, attribute text, "value" text);

CREATE OR REPLACE FUNCTION create_eav_datavalueset(mydataelementid integer, mysourceid integer)
  RETURNS SETOF eav_text AS
$BODY$

DECLARE
this_objectid integer DEFAULT 0;
periods record;
categoryoptioncomboids record;


BEGIN
 EXECUTE 'DROP TABLE IF EXISTS _eav_dataset';
 EXECUTE 'CREATE TABLE _eav_dataset ( objectid integer, attribute text, "value" text)';

FOR periods IN 

SELECT DISTINCT periodid FROM datavalue where dataelementid = mydataelementid AND
	sourceid = mysourceid LOOP
	FOR categoryoptioncomboids IN 
	SELECT DISTINCT categoryoptioncomboid FROM datavalue 
	where dataelementid = mydataelementid
	 LOOP

EXECUTE 'INSERT INTO _eav_dataset (objectid, attribute, "value")'
          || 'SELECT ' 
          || this_objectid 
          || ', ''dataelementname''::text, "name"::text'
          || ' FROM dataelement where dataelementid = '
          || mydataelementid
          || ' UNION' 
          || ' SELECT ' 
          || this_objectid
          || ', ''startdate''::text, startdate::text FROM period where periodid = ' 
          || periods.periodid
          || ' UNION SELECT '
          || this_objectid 
          ||', ''enddatte''::text, enddate::text '
	  || ' FROM period where periodid = ' 
	  || periods.periodid
	  ||' UNION'
          ||' SELECT ' 
          || this_objectid 
          || ', ''orgunitname''::text, "name"::text' 
          || ' FROM organisationunit where organisationunitid = ' 
          || mysourceid
          || ' UNION '
	  || ' SELECT ' 
	  || this_objectid 
	  || ', ''value''::text, "value"::text'
          || ' FROM datavalue where sourceid = ' 
          || mysourceid
          || ' AND periodid = ' 
          || periods.periodid 
          ||  'AND dataelementid = ' 
          || mydataelementid
          || ' AND categoryoptioncomboid = ' 
          || categoryoptioncomboids.categoryoptioncomboid
          || 'UNION '
          || ' SELECT ' 
          || this_objectid 
          || ' , dataelementcategory.name, dataelementcategoryoption.name 
		FROM categories_categoryoptions
		INNER JOIN dataelementcategory ON 
		dataelementcategory.categoryid = categories_categoryoptions.categoryid
		INNER JOIN dataelementcategoryoption ON  
		dataelementcategoryoption.categoryoptionid = categories_categoryoptions.categoryoptionid
		WHERE categories_categoryoptions.categoryoptionid IN
		(SELECT categoryoptionid FROM categoryoptioncombos_categoryoptions
		 where categoryoptioncomboid = '
          || categoryoptioncomboids.categoryoptioncomboid  ||')'

           ;
         this_objectid := this_objectid + 1;
   END LOOP;

END LOOP;

END;


  $BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION create_eav_datavalue(integer, integer, integer, integer) OWNER TO postgres;

