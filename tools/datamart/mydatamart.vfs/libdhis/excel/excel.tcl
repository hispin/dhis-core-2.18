# A package of utilities for working with xslsx files
# Init should be called before any of the other procs.
# Init is used to set temporrary directory for zipping/unzipping
# as well as executables to be used for zip and unzip

package provide excel 1.0

package require vfs::zip
package require logger
package require xslt

namespace eval excel {
    set log [logger::init excel]
    
    # point to the resource directory in the package
    variable resource [file join [file dirname [info script]] resource] 
    # these should be set by init
    variable exceldir
    variable zip
    variable unzip

    # set up temporary work directory for xlsx packages and 
    # executables for zip/unzip
    proc init {tmpdir {zipper zip} {unzipper unzip} } {
	variable resource
	variable exceldir
	variable zip
	variable unzip
	
	set exceldir [file normalize [file join $tmpdir excel]]
	file mkdir $exceldir
	set zip [file normalize $zipper]
	set unzip [file normalize $unzipper]
	# unpack resources
	file copy $resource $exceldir 
    }

    # remove directory created by init
    proc cleanup {} {
	variable exceldir
	file delete -force $exceldir
    }
    
    # unzip an xlsx file
    # returns the unzipped directory
    proc unpack {excelFile} {
	variable unzip
	variable exceldir
	# unzip xlsx file
	set dirname [file tail $excelFile]_[clock clicks]
	set unzipped [file join $exceldir $dirname]
	file mkdir $unzipped
	exec "$unzip" -d $unzipped $excelFile
	return $unzipped
    }

    # zip up an xlsx file from a directory
    proc pack {unzipped excelFile} {
	variable zip
	set unzipped [file normalize $unzipped]
	set excelFile [file normalize $excelFile]
	# zip excelFile
	set pwd [pwd]
	try {
	    cd $unzipped
	    exec $zip -r $excelFile *
	} finally {
	    cd $pwd
	}
    } 

    # returns true if excelfile has connections.xml
    proc hasConnections {excelFile} {
	variable unzip
	try {
	    set res [exec $unzip -l $excelFile xl/connections.xml]
	} on error err {return false} on ok err {return true}
    }

    # alter connections in excelfile to use local sqlite db
    proc alterConnections {excelFile sqliteFile} {
	variable exceldir
	
	set connectionstr "DRIVER=SQLite3 ODBC Driver;Database=$sqliteFile;StepAPI=0;SyncPragma=;NoTXN=0;Timeout=;ShortNames=0;LongNames=0;NoCreat=1;NoWCHAR=1;FKSupport=0;JournalMode=;LoadExt=;"
	set unzipped [excel::unpack $excelFile]
	set xsl [file join $exceldir resource xslt connection.xsl]
	set pwd [pwd]
	
	${::excel::log}::debug "Unpacked $excelFile to $unzipped"

	try {
	    cd $unzipped
	    set wd [pwd]
	    set old [file join $wd xl connections.xml]
	    set new [file join $wd xl new_connections.xml]

	    xslt::transform $xsl $old $new \
		[list connstring $connectionstr]

	    file rename -force $new $old

	    # put it all back together 
	    excel::pack $unzipped $excelFile 
	    
	} on error err {
	    error $err
	} finally {
	    cd $pwd
	    file delete -force $unzipped
	}
    }

    # create new connection element as string
    proc newConnection {connectionId sqliteFile viewName {description {}} } {
	set connectionString "<connection id='$connectionId' name='$viewName' description='$description' type='1' refreshedVersion='1' saveData='1'><dbPr connection='DRIVER=SQLite3 ODBC Driver;Database=$sqliteFile;StepAPI=0;SyncPragma=;NoTXN=0;Timeout=;ShortNames=0;LongNames=0;NoCreat=1;NoWCHAR=1;FKSupport=0;JournalMode=;LoadExt=;' command='SELECT * FROM $viewName' /></connection>"
	return $connectionString
    }

    proc createConnections {sqliteFile views} {
	set id 1
	set xml "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>
<connections xmlns='http://schemas.openxmlformats.org/spreadsheetml/2006/main'>\n"

	foreach view $views {
	    set xml $xml[newConnection $id $sqliteFile $view]\n
	    incr id
	}

	set xml $xml</connections>
	return $xml
    }

    proc insertConnectionsFile {excelFile connectionsFile} {
	set unpacked [excel::unpack $excelFile]
	file copy -force $connectionsFile [file join $unpacked xl connections.xml]
	excel::pack $unpacked $excelFile
    }

    proc createBlankWithViews {excelFile sqliteFile views} {
	variable exceldir
	variable resource

	set connections [excel::createConnections [file normalize $sqliteFile] $views]
	set filename [file join $exceldir connections[clock clicks] ]
	set f [open $filename {CREAT RDWR}]
	puts $f $connections
	close $f
	file copy -force [file join $resource excel Blank.xlsx] $excelFile
	excel::insertConnectionsFile $excelFile $filename	
    }
}