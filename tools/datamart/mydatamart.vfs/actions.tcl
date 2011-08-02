# This file contains action procs called from mainwindow UI
proc newDatamart {} {
    set types { {{Datamart files} {.dmart}} }
    set dbfile [tk_getSaveFile \
		    -filetypes $types \
		    -defaultextension ".dmart" \
		    -title [mc "Create new datamart file"]]
    
    if { [string length $dbfile]>0} {
	# if a database is already open, close it
	if {[info command db] eq "db" } {
	    ${::log}::debug "closing current connection"
	    db close
	}
	# now we have a new one
	set result [sqlite3 db $dbfile ]
	set ::dhis(dbfile) $dbfile
	set ::dhis(status) "$dbfile open"

	foreach var {username password myorgunit myorgunitid level} {
	    if [info exists ::dhis($var)] {
		unset ::dhis($var)
	    }
	}
	    
	sqlite3 db $dbfile 
	dhisdb::loadTables db
	dhisdb::updateStatus db
#	showviews .win.nb.views
	${::log}::debug [mc "New datamart created at $dbfile"]
	wm title .win "DHIS Datamart - [file tail $::dhis(dbfile)]" 

	# disable datamart and reports menus
	.win.menubar entryconfigure 1 -state disabled
	.win.menubar entryconfigure 2 -state disabled
	$::excelConnectButton configure -state disabled

	showFileOpenControls
	showSettings $::displayArea
    }    
}

# opens an existing dmart file
proc openDatamart {} {
    set types { {{Datamart files} {.dmart}} }
    set dbfile [tk_getOpenFile -filetypes $types]
    if { [string length $dbfile]>0} {
	# if a database is already open, close it
	if {[info command db] eq "db"} {
	    ${::log}::debug "closing current connection"
	    db close
	}

	foreach var {username password myorgunit myorgunitid level} {
	    if [info exists ::dhis($var)] {
		unset ::dhis($var)
	    }
	}

	openDatamartFile $dbfile
    }
}

proc openDatamartFile {dbfile} {
    sqlite3 db $dbfile 
    if {![dhisdb::hasTables db]} {
	tk_messageBox -icon warning -message "Not a valid datamart file" -type ok
	return
    }
    set ::dhis(dbfile) $dbfile
    set ::dhis(status) "$dbfile open"
    
    dhisdb::upgrade db
    dhisdb::updateStatus db
    if {[dhisdb::haveOrgunits db]} {
	showHaveMetadataControls
    }
    wm title .win "DHIS Datamart - [file tail $::dhis(dbfile)]" 
    
    .win.menubar entryconfigure 1 -state disabled
    .win.menubar entryconfigure 2 -state disabled
    $::excelConnectButton configure -state disabled

    showFileOpenControls
    showSettings $::displayArea
}

proc appExit {} {
    cleantmps
    exit
}

proc confirmLoadMetadata {} {
    tk_messageBox \
	-message [mc "Are you sure?"] \
	-detail [mc "Warning: This operation can take some minutes"] \
	-type yesno 
}

proc appLoadMetadataFromFile {} {
    if {[confirmLoadMetadata] eq no} { return }
    set types { {{DXF metadata zip files} {.zip}} }
    set dxffile [tk_getOpenFile -filetypes $types]
    if { [string length $dxffile]>0} {
	showMetadataProgress
	importDXF $dxffile
    }
}


proc appLoadMetadataFromDHIS {} {    
    if {[confirmLoadMetadata] eq no} { return }

    # give it 5 minutes (TODO: configure this)
    set timeout 300000
    set tok [dhisweb::fetchMetadata $::dhis(url) $timeout ]
    if {$tok == 0} {
	tk_messageBox -message [mc "Failed to connect to host"] \
		-icon error \
		-type ok
    } else {
	showMetadataProgress
	trace add variable [set tok](status) write [list metadataDownloadComplete $tok]
	set ::dhis(status) "Downloading metadata"
    }
}

proc metadataDownloadComplete {tok args} {
    upvar \#0 $tok state
    #parray state
    if {[::http::ncode $tok] != 200 } {
	set error [::http::error $tok]
	${::log}::debug "error $error"
	destroy .metadataLoading
	tk_messageBox -icon error -message [mc "Problem downloading metadata\n$error"]
	showSettings $::displayArea
    } else {
	${::log}::debug "Metadata download success"
	set dxfFile [file join $::dhis(tmpdir) Export_meta.zip]
	set f [open $dxfFile {WRONLY CREAT TRUNC BINARY}]
	puts -nonewline $f $state(body)
	close $f
	importDXF $dxfFile
    }
}

proc importDXF {dxfFile} {
    set tmpdir $::dhis(tmpdir)
    ${::log}::debug "extracting zip"
    dxf2tmp $dxfFile
    set xslt [file join $tmpdir xslt/dxf2sql.xsl]
    if {![file exists $xslt]} {
	file copy [file join $::dhis(resource) xslt/dxf2sql.xsl] $xslt
    } 
    
    ${::log}::debug "transforming metadata"
    set ::dhis(status) [mc "Transforming metadata"]
    xslt::transform_asynch $xslt [file join $tmpdir Export.xml] [file join $tmpdir import.sql] metadataXsltCallback 
}

proc appLoadSqlFromFile {} {
    set sqlFile [tk_getOpenFile -filetypes {{{SQL files} .sql}} ]
    if {[file exists $sqlFile] && ![file isdirectory $sqlFile] } {
	try {
	    dhisdb::evalFile db $sqlFile
	} on error err {
	    tk_messageBox -icon error -message [mc "Error importing file"] -detail $err
	}
    } 
}

proc appAggregatedData {} {
    showstats $::displayArea
}

# set connections in an excel file
proc excelConnect {} {
    set excelFile [ tk_getOpenFile -filetypes {{{Excel files} .xlsx}} ]
    if { [string length $excelFile]>0} {
	if {![excel::hasConnections $excelFile]} {
	    tk_messageBox -icon warning -type ok -message [mc "$excelFile has no connections defined"]
	    return
	}
	${::log}::debug "altering connections in $excelFile to $::dhis(dbfile)"
	excel::alterConnections $excelFile $::dhis(dbfile)

	if {$::tcl_platform(platform) eq "windows" } {
	    ${::log}::debug "Opening $excelFile"
	    # the weird \" and the need for 'list' is to cater for names with spaces :-(
	    # see: http://wiki.tcl.tk/765
	    eval exec [auto_execok start] \"\" [list $excelFile] &
	}
    }
}

# quick hack to export Ola's pivot table (TODO Delete this)
proc excelPivot {} {
    ${::log}::debug "exporting pivot table"
    set connectionstr "DRIVER=SQLite3 ODBC Driver;Database=$::dhis(dbfile);StepAPI=0;SyncPragma=;NoTXN=0;Timeout=;ShortNames=0;LongNames=0;NoCreat=1;NoWCHAR=1;FKSupport=0;JournalMode=;LoadExt=;"
    set tmpexcel [file join $::dhis(tmpdir) excel]
    set srcexcel [file join $::dhis(resource) excel/dhis2_ke.xlsx]
    file mkdir $tmpexcel
    file copy -force $srcexcel $::dhis(tmpdir)
    exec $::dhis(unzip) -u -d $tmpexcel [file join $::dhis(tmpdir) dhis2_ke.xlsx]  xl/connections.xml  

    exec $::dhis(xsltproc) --stringparam connstring $connectionstr  [file join $::dhis(tmpdir) xslt/connection.xsl] [file join $tmpexcel xl connections.xml] > [file join $tmpexcel xl new_connections.xml]

    file rename -force [file join $tmpexcel xl new_connections.xml] [file join $tmpexcel xl connections.xml]
    
    set types { {{Excel files} {.xlsx}} }
    set xlfile [tk_getSaveFile \
		    -filetypes $types \
		    -defaultextension ".xlsx" \
		    -title [mc "New excel file"]]

    if { [string length $xlfile]>0} {
	set pwd [pwd]
	cd $tmpexcel
	file copy -force $srcexcel $xlfile
	exec $::dhis(zip) $xlfile xl/connections.xml
	cd $pwd
    }
    file delete -force $tmpexcel 
}

proc appHelpAbout {} {
    tk_messageBox -message [mc "Remote DHIS datamart synch program\nversion $::dhis(version)"]
}

proc statusChange {args} {
    ${::log}::debug "dhis(status) = $::dhis(status)"
}
