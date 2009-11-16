--Author: Jason P. --This code is a series of functions used to create a report to characterize --data submission rates for a particular time period using the DHIS 2 database. 

--create a type to hold the desired 
CREATE TYPE datasubmissions_type AS(
sourceid integer,
periodid integer, 
datasetid integer,
submittedcount bigint,
expectedcount bigint);


-- Function: monthly_facility_data_submissions_onemonth(integer)

-- DROP FUNCTION monthly_facility_data_submissions_onemonth(integer);

CREATE OR REPLACE FUNCTION monthly_facility_data_submissions_onemonth(myperiod integer)
  RETURNS SETOF datasubmissions_type AS
$BODY$

DECLARE 
rec record;
BEGIN
FOR rec in (
SELECT orgunitstructure.idlevel4 as sourceid, submitted.periodid,
 submitted.datasetid,  submitted.submitteddatacount , 
 expected.expected as expectedcount from orgunitstructure orgunitstructure
JOIN 
--begin the join of the actual submissions
(SELECT count(datavalue.value) as submitteddatacount, 
datavalue.sourceid, datavalue.periodid, datasetmembers.datasetid from datavalue datavalue 
JOIN datasetmembers datasetmembers on 
datasetmembers.dataelementid = datavalue.dataelementid
--accept a single paramater, and use this to select out all records for the desired time period
where datavalue.periodid = $1
GROUP  BY datavalue.sourceid, datavalue.periodid, datasetmembers.datasetid) as submitted on
--only worry about orgunits with a level4 id
--TO DO: THe function should probably extended to accept different hierarchy levels
submitted.sourceid = orgunitstructure.idlevel4
--Start the join of expected dataelements for the ones that were actually submitted
JOIN
(SELECT count(datasetmembers.dataelementid) as expected, datasetmembers.datasetid 
FROM datasetmembers datasetmembers
GROUP BY datasetmembers.datasetid )
expected on 
expected.datasetid = submitted.datasetid  
where orgunitstructure.idlevel4 IS NOT NULL 
--Start the union of facilities that did not submitted anything, returning a set of blank
--records with expected counts
UNION

SELECT nonsubmitters.sourceid, $1 as periodid, nonsubmitters.datasetid, NULL::bigint as submittedcount , expected.expected as expectedcount  FROM (
SELECT DISTINCT datasetsource.sourceid,  datasetsource.datasetid from datasetsource datasetsource
EXCEPT
SELECT DISTINCT datavalue.sourceid, datasetmembers.datasetid from datavalue datavalue
JOIN datasetmembers datasetmembers ON datasetmembers.dataelementid = datavalue.dataelementid
where datavalue.periodid = $1
AND datavalue.sourceid IN (SELECT DISTINCT idlevel4 from orgunitstructure where idlevel4 is not null))  as nonsubmitters
JOIN
(SELECT count(datasetmembers.dataelementid) as expected, datasetmembers.datasetid 
FROM datasetmembers datasetmembers
GROUP BY datasetmembers.datasetid)
expected on 
expected.datasetid = nonsubmitters.datasetid 
ORDER BY sourceid, periodid, datasetid )
LOOP
     RETURN NEXT rec;
  END LOOP;
  END;
  $BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION monthly_facility_data_submissions_onemonth(integer) OWNER TO dhis;


-- Function: monthly_facility_data_submissions_timeperiods(integer, integer)
--THis is a helper function to determine the actual periods between the 
--periods provided as report paramaters. 
--TO DO: Error checking to ensure that startperiod <= --TO DO: periodtype ID is hardcoded and refers to an integer. Should replace with some other mechanism to --TO DO: It is not safe to assume that periods are necessarily sequential. This function should accept date types, and return a set of period ids. 
--either the desired type of periodicity, or use a *~ expression to select out the monthly time period id 
-- DROP FUNCTION monthly_facility_data_submissions_timeperiods(integer, integer);

CREATE OR REPLACE FUNCTION monthly_facility_data_submissions_timeperiods(startperiod integer, endperiod integer)
  RETURNS SETOF integer AS
$BODY$


SELECT periodid from period where
  startdate::timestamp >= (SELECT startdate from period where periodid = $1)::timestamp
  AND
  enddate::timestamp <= (SELECT enddate from period where periodid = $2)::timestamp
    AND 
    --note that this period type may/should be changed depending on the system. 
  periodtypeid = '6'
  ORDER BY startdate;

  $BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION monthly_facility_data_submissions_timeperiods(integer, integer) OWNER TO dhis;

--This table will be used to house the materialized view 
--which will contain results from the monthly_facility_data_submissions_timeperiods() --procedure. 

-- Table: 
-- DROP TABLE mv_monthly_facility_data_submissions;

-- Table: mv_monthly_facility_data_submissions

-- DROP TABLE mv_monthly_facility_data_submissions;

CREATE TABLE mv_monthly_facility_data_submissions
(
  sourceid integer,
  periodid integer,
  datasetid integer,
  submittedcount bigint,
  expectedcount bigint,
  id serial NOT NULL,
  CONSTRAINT pk_submissions_summary PRIMARY KEY (id)
)
WITH (OIDS=FALSE);
ALTER TABLE mv_monthly_facility_data_submissions OWNER TO dhis;

-- Create a function to materialize a range of time periods. 
-- Function: monthly_facility_data_submissions_by_month(integer, integer)

-- DROP FUNCTION monthly_facility_data_submissions_by_month(integer, integer);

CREATE OR REPLACE FUNCTION monthly_facility_data_submissions_by_month(startperiod integer, endperiod integer)
  RETURNS integer AS
$BODY$
DECLARE

timeperiods record;

BEGIN 
--get rid of old records. 
--TO DO. Make this optional so that the table only updates new records, ignoring insert errors and updating old records.  
TRUNCATE TABLE mv_monthly_facility_data_submissions;

FOR timeperiods in SELECT monthly_facility_data_submissions_timeperiods($1, $2) LOOP

EXECUTE 'INSERT INTO mv_monthly_facility_data_submissions SELECT * FROM monthly_facility_data_submissions_onemonth(' || timeperiods.monthly_facility_data_submissions_timeperiods || ')';

END LOOP;

RETURN 1; 

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION monthly_facility_data_submissions_by_month(integer, integer) OWNER TO dhis;

--The materialized view is refreshed by a select query. This could --be included in the report as an option, through the DHIS 2 interface with 
--some modification, executed manually through the PgAdmin interface,
--executed after bulk updates automatically, or scheduled to --regularly with the PgAgent. 

----SELECT * FROM monthly_facility_data_submissions_by_month(5425, 5436);