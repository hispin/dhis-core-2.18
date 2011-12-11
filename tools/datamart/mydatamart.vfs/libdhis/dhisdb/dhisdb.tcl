package provide dhisdb 1.0

package require sqlite3
package require vfs::zip

source [file join [file dirname [info script]] generateviews.tcl ]

namespace eval dhisdb {
    
    variable tables [list metadata \
			 months \
			 aggregateddatavalue \
			 aggregatedindicatorvalue \
			 _categoryoptioncomboname \
			 _orgunitstructure \
			 orgunitlevel \
			 organisationunit \
			 dataelement \
			 indicator \
			 indicatortype \
			 _dataelementgroupsetstructure \
			 _indicatorgroupsetstructure \
			 _organisationunitgroupsetstructure \
			 _categorystructure ]

    # point to the resource directory in the package
    variable resource [file join [file dirname [info script]] resource] 

    proc getTables {handle} {
	set query "select name from sqlite_master where type=\"table\" order by name"
	set tables [$handle eval $query ]
	return $tables
    }

    proc getViews {handle} {
	set query "SELECT name FROM sqlite_master WHERE type='view' ORDER BY name;"
	$handle eval $query
    }

    # check if all required tables exist in db
    proc hasTables {handle} {
	set currenttables [getTables $handle]
	foreach table $::dhisdb::tables {
	    if {[lsearch -exact $currenttables $table] == -1} {
		puts "$table not found"
		return false
	    }
	}
	return true
    }

    proc getDatabaseVersion {handle} {
	set version [$handle eval "SELECT tag_value FROM metadata WHERE tag LIKE 'version'"]
    }

    proc loadMetadata {handle dxffile tmpdir} {
	set mntfile [vfs::zip::Mount $dxfile $dxffile]
	file copy -force zipmnt/Export.xml $tmpdir/Export.xml
	vfs::zip::Unmount $mntfile $dxfile

	loadDxf 
    }

    proc evalChannel {handle channel} {
	fconfigure $channel -encoding utf-8
        $handle eval [read $channel]
    }

    proc evalFile {handle filename} {
	set f [open $filename]
	try {
	    evalChannel $handle $f
	} on error err {
	    error $err
	} finally {
	    close $f
	}
    }

    proc loadTables {handle} {
	variable resource
	set tables [file join $resource sql create.sql]
	evalFile db $tables
    }

    proc updateStatus {handle} { 
	lassign [$handle eval "select count(), max(period), min(period) from aggregateddatavalue where periodtype='W'"] \
	    ::dhis(weeklydatavalues) ::dhis(lastdataweek) ::dhis(firstdataweek)
	lassign [$handle eval "select count(), max(period), min(period) from aggregatedindicatorvalue where periodtype='W'"] \
	    ::dhis(weeklyindicatorvalues) ::dhis(lastindicatorweek) ::dhis(firstindicatorweek)
	lassign [$handle eval "select count(), max(period), min(period) from aggregateddatavalue where periodtype='M'"] \
	    ::dhis(monthlydatavalues) ::dhis(lastdatamonth) ::dhis(firstdatamonth)
	lassign [$handle eval "select count(), max(period), min(period) from aggregatedindicatorvalue where periodtype='M'"] \
	    ::dhis(monthlyindicatorvalues) ::dhis(lastindicatormonth) ::dhis(firstindicatormonth)
	lassign [$handle eval "select count(), max(period), min(period) from aggregateddatavalue where periodtype='Q'"] \
	    ::dhis(quarterlydatavalues) ::dhis(lastdataquarter) ::dhis(firstdataquarter)
	lassign [$handle eval "select count(), max(period), min(period) from aggregatedindicatorvalue where periodtype='Q'"] \
	    ::dhis(quarterlyindicatorvalues) ::dhis(lastindicatorquarter) ::dhis(firstindicatorquarter)
	lassign [$handle eval "select count(), max(period), min(period) from aggregateddatavalue where periodtype='Y'"] \
	    ::dhis(yearlydatavalues) ::dhis(lastdatayear) ::dhis(firstdatayear)
	lassign [$handle eval "select count(), max(period), min(period) from aggregatedindicatorvalue where periodtype='Y'"] \
	    ::dhis(yearlyindicatorvalues) ::dhis(lastindicatoryear) ::dhis(firstindicatoryear)
    }

    proc getLocalMetadata {handle} {
	set ::dhis(levels) [ db eval "select level, name from orgunitlevel" ]
	# set ::dhis(views) [ db eval "select name from dataview" ]
	$handle eval "select * from metadata" {
	    set ::dhis($tag) $tag_value
	}
    }

    proc saveLocalMetadata {handle tag value} {
	$handle eval "INSERT OR REPLACE INTO metadata VALUES('$tag','$value')"
    }
    

    proc haveOrgunits {handle} {
	$handle eval "select count(*) from organisationunit"
    }

    proc getLevels {handle} {
	$handle eval "select level, name from orgunitlevel order by level"
    } 

    proc getLevelName {handle level} {
	$handle eval "select name from orgunitlevel where level like $level"
    } 

    proc getLevelByOrgunit {handle orgunitid} {
	$handle eval "select level from _orgunitstructure where organisationunitid like $orgunitid"
    } 

    proc getParentOrgUnit {handle orgunitid} {
	set oustruct [$handle eval "select * from _orgunitstructure where organisationunitid like $orgunitid"]
	puts $oustruct
	set level [lindex $oustruct 1]
	set parent [lindex $oustruct $level]
	return $parent
    }

    proc getOrgUnitDimensions {handle} {
	$handle eval "select dimension.dimcolumn from dimension 
                      INNER JOIN dimensiontype 
                      ON dimension.dimtype = dimensiontype.id
                      WHERE dimensiontype.name='OrganisationGroupSet'"
    } 

    proc getCategoryDimensions {handle} {
	$handle eval "select dimension.dimcolumn from dimension 
                      INNER JOIN dimensiontype 
                      ON dimension.dimtype = dimensiontype.id
                      WHERE dimensiontype.name='Category'"
    } 

    proc getDataelementDimensions {handle} {
	$handle eval "select dimension.dimcolumn from dimension 
                      INNER JOIN dimensiontype 
                      ON dimension.dimtype = dimensiontype.id
                      WHERE dimensiontype.name='DataelementGroupSet'"
    } 

    proc getIndicatorDimensions {handle} {
	$handle eval "select dimension.dimcolumn from dimension 
                      INNER JOIN dimensiontype 
                      ON dimension.dimtype = dimensiontype.id
                      WHERE dimensiontype.name='IndicatorGroupSet'"
    } 

    proc readDimensions {handle} {
	set ::dhis(categoryDimensions) [dhisdb::getCategoryDimensions $handle]
	set ::dhis(orgunitDimensions) [dhisdb::getOrgUnitDimensions $handle]
	set ::dhis(dataelementDimensions) [dhisdb::getDataelementDimensions $handle]
	set ::dhis(indicatorDimensions) [dhisdb::getIndicatorDimensions $handle]
    }


    # manage changes between database versions
    proc upgrade {handle} {
	switch [dhisdb::getDatabaseVersion $handle] {
	    "" {
		puts "Upgrading database from 1.0-beta to 1.0"
		db eval "INSERT OR REPLACE INTO metadata VALUES ('level','')" 
		db eval "INSERT OR REPLACE INTO metadata VALUES ('version','$::dhis(version)')" 
	    }
	    1.0 {
		puts "Database is current version 1.0"
	    }
	}
    }

    proc deleteValues {handle periodType level orgunit valueType from to} {
	puts "delete old values"
    }

}
