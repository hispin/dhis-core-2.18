namespace eval dhisdb {

    # generateView
    # generate a view 
    # param datatype : indicator or routinedata
    # param level :  1 - 8
    # param periodtype : m,y,q,s
    # param duplicateDataelementGroupss : optional parameter. Used to filter dataelements which are yearly but 
    #   have also been interpolated to monthly values
    #
    # Example1: generateView indicator 4 y 
    # Example2: generateView routinedata 3 m {main_de_groups Population}
    # Example3: generateView routinedata 5 m {main_de_groups Population main_de_groups Rainfall} 

    proc generateView {handle datatype level periodtype {duplicateDataelementGroups {}} } {
 
	set viewSql ""
	append viewSql "\n-------------------------------------------------------------------------\n"
	set viewName [format "pivotsource_%s_ou%s_%s" $datatype $level $periodtype]
	append viewSql "\nDROP VIEW IF EXISTS $viewName;\n"
	append viewSql "\nCREATE VIEW $viewName AS SELECT\n"

	append viewSql "\n--  Orgunit levels                                                -------\n"
	for {set oulevel 1} {$oulevel <= $level} {incr oulevel} {
	    append viewSql "organisationunit_$oulevel.name AS orgunit$oulevel,\n"
	    append viewSql "organisationunit_$oulevel.shortname AS ou$oulevel,\n"
	}
	append viewSql "_orgunitstructure.level,\n"

	append viewSql "\n--  Orgunit dimensions                                            -------\n"
	foreach dimension [getOrgUnitDimensions $handle] {
	    append viewSql "_organisationunitgroupsetstructure.$dimension AS ou_$dimension,"
	}
	
	switch $datatype {
	    indicator {
		append viewSql "\nindicator.indicatorid,"
		append viewSql "\nindicator.name AS indicator,"
		append viewSql "indicator.shortname AS indshort,"
		append viewSql "indicator.annualized,"
		
		append viewSql "\n--  Indicator dimensions                                            -------\n"
		foreach dimension [getIndicatorDimensions $handle] {
		    append viewSql "_indicatorgroupsetstructure.$dimension,"
		}
		append viewSql "\n"
		
		append viewSql "\n-- get factor, numerator, and denominator values so that the value itself can be calculated in excel and properly aggregated upwards\n"
		append viewSql "(aggregatedindicatorvalue.factor * aggregatedindicatorvalue.numeratorvalue) AS numxfactor,\n" 
		append viewSql "aggregatedindicatorvalue.factor," 
		append viewSql "aggregatedindicatorvalue.denominatorvalue," 
		append viewSql "aggregatedindicatorvalue.numeratorvalue,"
		append viewSql "aggregatedindicatorvalue.periodtype,\n"		
	    }
	    routinedata {
		append viewSql "\ndataelement.dataelementid,"
		append viewSql "\ndataelement.name AS dataelement,"
		append viewSql "\ndataelement.name AS dataelementname,"
		append viewSql "dataelement.shortname AS deshort,"
		append viewSql "_categoryoptioncomboname.categoryoptioncomboname AS catoptioncombo,"	   
		append viewSql "_categoryoptioncomboname.categoryoptioncomboid,\n"
		append viewSql "\n--  category dimensions                                            -------\n"
		foreach dimension [getCategoryDimensions $handle] {
		    append viewSql "_categorystructure.$dimension,"
		}
		append viewSql "\n--  Dataelement dimensions                                            -------\n"
		foreach dimension [getDataelementDimensions $handle] {
		    append viewSql "_dataelementgroupsetstructure.$dimension,"
		}
		append viewSql " \naggregateddatavalue.categoryoptioncomboid,"
		append viewSql "aggregateddatavalue.value,"
		append viewSql "aggregateddatavalue.periodtype,\n"
	    }
	}

	switch $periodtype {
	    w {
		append viewSql "substr( period, 1, 4 ) AS year,"
		append viewSql "substr( period, 5, 3 ) AS week\n"
	    }
	    m {
		append viewSql "--- get month names from the months table\n"
		append viewSql "substr( period, 1, 4 ) AS year,"
		append viewSql "months.name AS month,"
		append viewSql "months.name || '-' || substr( period, 3, 2 ) AS period\n"
	    }
	    q {
		append viewSql "substr( period, 1, 4 ) AS year,"
		append viewSql "substr( period, 5, 2 ) AS quarter\n"
	    }
	    s {
		append viewSql "substr( period, 1, 4 ) AS year,"
		append viewSql "substr( period, 5, 2 ) AS semester\n"
	    }
	    y {
		append viewSql "period AS year\n"
	    }
	}
	append viewSql "--- FROM -------------------------------------------------------------------------\n"
	append viewSql "FROM\n"
	append viewSql "_orgunitstructure _orgunitstructure," 
	for {set oulevel 1} {$oulevel <= $level} {incr oulevel} {
	    append viewSql "organisationunit organisationunit_$oulevel,\n" 
	}
	append viewSql "_organisationunitgroupsetstructure,"


	if {$periodtype eq "m"} {
	    append viewSql "months, "
	}

	switch $datatype {
	    indicator {
		append viewSql "indicator, _indicatorgroupsetstructure, aggregatedindicatorvalue\n"
	    }
	    routinedata {
		append viewSql "dataelement, _dataelementgroupsetstructure, aggregateddatavalue, _categorystructure, _categoryoptioncomboname\n"
	    }
	}
	append viewSql "--- WHERE -------------------------------------------------------------------------\n"
	append viewSql "WHERE\n"
	
	for {set oulevel 1} {$oulevel <= $level} {incr oulevel} {
	    append viewSql "_orgunitstructure.idlevel$oulevel = organisationunit_$oulevel.organisationunitid AND\n"
	}
	append viewSql "_orgunitstructure.level = $level AND\n"

	switch $datatype {
	    indicator {
		append viewSql "indicator.indicatorid = aggregatedindicatorvalue.indicatorid AND
			_indicatorgroupsetstructure.indicatorid = aggregatedindicatorvalue.indicatorid AND
			_orgunitstructure.organisationunitid = aggregatedindicatorvalue.organisationunitid AND
			_organisationunitgroupsetstructure.organisationunitid = aggregatedindicatorvalue.organisationunitid AND\n"
	    }
	    routinedata {
		foreach {groupset group} [split $duplicateDataelementGroups] {
		    switch $periodtype {
			q -
			s -
			m {
			    append viewSql "_dataelementgroupsetstructure.$groupset NOT LIKE '$group' AND\n" 
			}
			y {
			    append viewSql "_dataelementgroupsetstructure.$groupset LIKE '$group' AND\n" 
			}
		    }
		}
		append viewSql "dataelement.dataelementid = aggregateddatavalue.dataelementid AND
			_categoryoptioncomboname.categoryoptioncomboid = aggregateddatavalue.categoryoptioncomboid AND
			_categorystructure.categoryoptioncomboid = aggregateddatavalue.categoryoptioncomboid AND
			_dataelementgroupsetstructure.dataelementid = aggregateddatavalue.dataelementid AND	
			_orgunitstructure.organisationunitid = aggregateddatavalue.organisationunitid AND
			_organisationunitgroupsetstructure.organisationunitid = aggregateddatavalue.organisationunitid AND\n"
	    }
	}

	switch $periodtype {
	    w {
		append viewSql "periodtype = 'W'"
	    }
	    m {
		append viewSql "substr( period, 5 ) = months.number AND "
		append viewSql "periodtype = 'M'"
	    }
	    q {
		append viewSql "periodtype = 'Q'"
	    }
	    y {
		append viewSql "periodtype='Y'"
	    }
	    s {
		append viewSql "periodtype = 'S'"
	    }
	}

	append viewSql ";\n"
	return $viewSql

    }
    
    # generate views 
    proc generateViews {handle} {
	# update dimensions from db
	::dhisdb::readDimensions $handle
	set levels [::dhisdb::getLevels $handle]
	set nlevels [expr [llength $levels]/2 ]

	# set f [open c:/users/bobj/views.sql {RDWR CREAT}]
	# produce views ... 
	foreach periodtype {y m q s w} {
	    for {set level 2}  {$level <= $nlevels} {incr level} {
		foreach datatype {indicator routinedata} {
		    set view [generateView $handle $datatype $level $periodtype ]
		    $handle eval $view
		    # puts $f $view
		}
	    }
	}
	# close $f
    }

    # generate views - Kenya hack
    proc generateViewsKenya {handle} {
	# update dimensions from db
	::dhisdb::readDimensions $handle
	# produce views ... 
	foreach periodtype {y m q s} {
	    foreach level {3 4 5 6 7 8} {
		foreach datatype {indicator routinedata} {
		    set view [generateView $handle $datatype $level $periodtype [list main_de_groups Population] ]
		    $handle eval $view
		}
	    }
	}
    }
}
