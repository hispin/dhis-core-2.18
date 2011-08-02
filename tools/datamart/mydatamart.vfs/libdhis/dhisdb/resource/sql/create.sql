BEGIN TRANSACTION;
	-- used for storing local metadata parameters for remote queries 
   	DROP TABLE IF EXISTS metadata;
	CREATE TABLE metadata ( 
        tag character varying(20) PRIMARY KEY, 
        tag_value character varying(100) ); 
        
        -- sample values
        -- INSERT OR REPLACE INTO metadata VALUES ('url','http://dhis.uio.no/demo'); 
        -- INSERT OR REPLACE INTO metadata VALUES ('username','demo'); 
        -- INSERT OR REPLACE INTO metadata VALUES ('myorgunit',''); 
        -- INSERT OR REPLACE INTO metadata VALUES ('myorgunitid',''); 
        -- INSERT OR REPLACE INTO metadata VALUES ('parentorgunit',''); 
        -- INSERT OR REPLACE INTO metadata VALUES ('parentorgunitid',''); 

        -- sqlite doesn't have a monthname function
        -- use this for lookup 
        CREATE TABLE IF NOT EXISTS months ( 
        number character varying(2) PRIMARY KEY, 
        name character varying(3) 
        ); 
        
        INSERT OR REPLACE INTO months VALUES ('01','Jan');
        INSERT OR REPLACE INTO months VALUES ('02','Feb'); 
        INSERT OR REPLACE INTO months VALUES ('03','Mar'); 
        INSERT OR REPLACE INTO months VALUES ('04','Apr'); 
        INSERT OR REPLACE INTO months VALUES ('05','May'); 
        INSERT OR REPLACE INTO months VALUES ('06','Jun'); 
        INSERT OR REPLACE INTO months VALUES ('07','Jul'); 
        INSERT OR REPLACE INTO months VALUES ('08','Aug');
        INSERT OR REPLACE INTO months VALUES ('09','Sep'); 
        INSERT OR REPLACE INTO months VALUES ('10','Oct'); 
        INSERT OR REPLACE INTO months VALUES ('11','Nov'); 
        INSERT OR REPLACE INTO months VALUES ('12','Dec'); 
        
        DROP TABLE IF EXISTS aggregateddatavalue;
	CREATE TABLE aggregateddatavalue ( 
        period character varying(8), 
        organisationunitid integer, 
        dataelementid integer,
        categoryoptioncomboid integer,
        "value" double precision,
        periodtype CHAR( 1 ),        
        PRIMARY KEY ( period, organisationunitid, dataelementid, categoryoptioncomboid ) 
        ); 
                
        DROP TABLE IF EXISTS aggregatedindicatorvalue;
        CREATE TABLE aggregatedindicatorvalue ( 
        period character varying(8), 
        organisationunitid integer,
        indicatorid integer, 
        factor double precision, 
        numeratorvalue double precision,
        denominatorvalue double precision,
        periodtype CHAR( 1 ),        
        PRIMARY KEY ( period, organisationunitid, indicatorid )
        ); 
        
        DROP TABLE IF EXISTS _categoryoptioncomboname;
        CREATE TABLE _categoryoptioncomboname 
        ( 
        categoryoptioncomboid INTEGER PRIMARY KEY, 
        categoryoptioncomboname CHARACTER VARYING( 250 ) 
        ); 
        
        DROP TABLE IF EXISTS _orgunitstructure;
        CREATE TABLE _orgunitstructure ( 
        organisationunitid integer PRIMARY KEY, 
        "level" integer, 
        idlevel1 integer, idlevel2 integer, idlevel3 integer, idlevel4 integer, idlevel5 integer, idlevel6
        integer, idlevel7 integer, idlevel8 integer 
        ); 
        
        DROP TABLE IF EXISTS orgunitlevel;
        CREATE TABLE orgunitlevel (
        orgunitlevelid integer PRIMARY KEY, 
        "level" integer NOT NULL UNIQUE, 
        "name" character varying(255) NOT NULL 
        ); 
        
        -- abreviated orgunit table 
       	DROP TABLE IF EXISTS organisationunit;
	CREATE TABLE organisationunit ( 
        organisationunitid integer PRIMARY KEY, 
        "name" character varying(230) NOT NULL, 
        shortname character varying(50) NOT NULL, 
        code character varying(25), 
        active boolean
        ); 
        
        -- abreviated dataelement table 
        DROP TABLE IF EXISTS dataelement;
        CREATE TABLE dataelement ( 
        dataelementid integer PRIMARY KEY, 
        "name" character varying(230) NOT NULL, 
        shortname character varying(25) NOT NULL, 
        aggregationtype character varying(16) NOT NULL 
        ); 
        
        DROP TABLE IF EXISTS indicator;
        CREATE TABLE indicator ( 
        indicatorid integer PRIMARY KEY, 
        "name" character varying(230) NOT NULL,
        shortname character varying(25) NOT NULL, 
        annualized boolean, 
        indicatortypeid integer,
        numerator text, 
        numeratoraggregationtype character varying(16), 
        denominator text,
        denominatoraggregationtype character varying(16) 
        ); 
        
        DROP TABLE IF EXISTS indicatortype;
        CREATE TABLE indicatortype
        ( 
        indicatortypeid integer PRIMARY KEY, 
        "name" character varying(160) NOT NULL,
        indicatorfactor integer NOT NULL 
        ); 

        DROP TABLE IF EXISTS _organisationunitgroupsetstructure;
	CREATE TABLE _organisationunitgroupsetstructure 
	( 
	organisationunitid INTEGER PRIMARY KEY,
        organisationunitname CHARACTER VARYING( 250 )
	);

        DROP TABLE IF EXISTS _indicatorgroupsetstructure;
	CREATE TABLE _indicatorgroupsetstructure 
	( 
	indicatorid INTEGER PRIMARY KEY, 
	indicatorname CHARACTER VARYING( 250 )	
	);

        DROP TABLE IF EXISTS _dataelementgroupsetstructure;
	CREATE TABLE _dataelementgroupsetstructure 
	(
        dataelementid INTEGER PRIMARY KEY,
        dataelementname CHARACTER VARYING( 250 )
	);

        DROP TABLE IF EXISTS _categorystructure;
	CREATE TABLE _categorystructure 
	( 
        categoryoptioncomboid INTEGER PRIMARY KEY
	);


	----------------------------------------------------------------------------
	-- new stuff for creating views --------------------------------------------
        -- rationalizing mcdonalds and Groupsets
	----------------------------------------------------------------------------

	DROP TABLE IF EXISTS dimension;
	CREATE TABLE dimension
	(
	id integer PRIMARY KEY AUTOINCREMENT,
	name CHARACTER VARYING(250),
	dimtype INTEGER REFERENCES dimensiontype(id),
	dimcolumn CHARACTER VARYING(250),
	display CHARACTER VARYING(250),
	concept CHARACTER VARYING(25)
	);

	DROP TABLE IF EXISTS dimensiontype;
	CREATE TABLE dimensiontype
	(
	id INTEGER PRIMARY KEY,
	name CHARACTER VARYING(20)
	);
	INSERT INTO dimensiontype VALUES (1,'Category');
	INSERT INTO dimensiontype VALUES (2,'DataelementGroupSet');
	INSERT INTO dimensiontype VALUES (3,'IndicatorGroupSet');
	INSERT INTO dimensiontype VALUES (4,'OrganisationGroupSet');

	DROP TABLE IF EXISTS dataview;
	CREATE TABLE dataview
	(	
	id integer PRIMARY KEY AUTOINCREMENT,
	name CHARACTER VARYING(25),
	description CHARACTER VARYING(250),
	datatype CHARACTER VARYING(10), -- dataelement or indicator
	periodType CHARACTER(1), -- M, Y 
	fromPeriod CHARACTER VARYING(8),
	toPeriod CHARACTER VARYING(8)
	);

	DROP TABLE IF EXISTS dimensions_in_view;
	CREATE TABLE dimensions_in_view
	(
	dataview integer REFERENCES dataview(id),
	dimension integer REFERENCES dimension(id)
	);

        -- TODO: need to experiment with these to tune performance
        CREATE INDEX IF NOT EXISTS adv_period ON aggregateddatavalue ( period );
        CREATE INDEX IF NOT EXISTS adv_periodytpe ON aggregateddatavalue ( periodtype );
        CREATE INDEX IF NOT EXISTS ind_periodytpe ON aggregatedindicatorvalue ( periodtype );
        CREATE INDEX IF NOT EXISTS level ON _orgunitstructure ( level);
        CREATE INDEX IF NOT EXISTS level1 ON _orgunitstructure ( idlevel1 );
        CREATE INDEX IF NOT EXISTS level2 ON _orgunitstructure ( idlevel2 );
        CREATE INDEX IF NOT EXISTS level3 ON _orgunitstructure ( idlevel3 );
        CREATE INDEX IF NOT EXISTS level3 ON _orgunitstructure ( idlevel4 );

END TRANSACTION;