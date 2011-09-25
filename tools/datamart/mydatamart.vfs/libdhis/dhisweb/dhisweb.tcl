package provide dhisweb 1.0

package require http
package require vfs::zip
package require logger
package require tls

http::register https 443 ::tls::socket

package require dhisdb

# http client

namespace eval dhisweb {
    
    set log [logger::init dhisweb]
    
    variable loginCookie ""
    
    # common status codes used in status variables below
    array set statuscode {
	IDLE "Idle"
	PROGRESS "Connecting" 
	LSUCCESS "Logged in" 
	LFAILED "Login failed" 
	BADHOST "Failed to connect to host"
	TIMEOUT "Connection timeod out"
	UNKNOWN "Unknown server response"
	4XX "Server 4XX response"
	DATARCVD "Uncompressed data is available"
	SUCCESS "All Done"
    }

    # TODO - put these in http status array :-)    
    variable loginstatus IDLE
    variable datavaluestatus IDLE
    variable indicatorstatus IDLE
    variable metadatastatus IDLE

    # parsed from X-Number-Of-Rows header - as above 
    variable datavaluesToRead
    variable indicatorsToRead
    
    variable connections

    ################################################################################################################
    # API call to initiate a login request 
    # param url : dhis host
    # param : username
    # param : password
    # param : timeout default value 5000 milliseconds
    # return : http token for the connection or 0
    # side effect : variable ::dhisweb::loginstatus wil be set (sometime in the future) to 
    #   one of LSUCCESS, LFAILED, BADHOST or UNKNOWN
    ################################################################################################################
    proc login {url username password {timeout 5000}} {
	set fullUrl "$url/dhis-web-commons-security/login.action"
	set loginParams [::http::formatQuery j_username $username j_password $password]

	try { set tok [::http::geturl $fullUrl \
			   -query $loginParams \
			   -timeout $timeout \
			   -command ::dhisweb::loginCallback ]
	} on error var {
	    ${::dhisweb::log}::debug "error: $var" 
	    set ::dhisweb::loginstatus BADHOST 
	    return 0
	} finally {}
	return $tok
    }

    ################################################################################################################
    # API call to fetch aggregated data or indicator values
    # param url : dhis host
    # param level : the orgunit level to collect for
    # param rootorg : the root orgunit for the collection
    # param valuetype : should be set to either DataValues or Indicators
    # param periodtype : should be set to either monthly, quarterly or yearly
    # param from : should be set to monthly or yearly period string (eg 201001 or 2010)
    # param to : should be set to monthly, quarterly or yearly period string (eg 201001, 2010Q1 or 2010)
    # param db : the database handle to use to insert data
    # return http token for the connection or 0
    # side effects: data is written to the database
    ################################################################################################################
 
    proc fetchValues {url level rootorg valuetype periodtype from to db} {
	variable loginCookie
	variable log
	variable connections
	
	${log}::debug "fetching $periodtype $valuetype with : $loginCookie"
		
	set fullUrl [format "%s/dhis-web-reporting/exp/%s%s.action" $url $periodtype $valuetype]

	set params [::http::formatQuery \
			dataSourceLevel $level \
			dataSourceRoot $rootorg \
			startDate $from \
			endDate $to ]
	
	${log}::debug "http params: $params"

	try {
	    set tok [::http::geturl $fullUrl \
				  -query $params \
				  -headers [list "Cookie" $loginCookie] \
				  -handler ::dhisweb::datavalueCallback \
				  -command ::dhisweb::datavalueFinishHandler ]
	} on error err {
	    ${log}::debug "Error: $err"
	    set ret 0
	} on ok var {
	    # provide extra info in the tok array to be used by callback
	    array set $tok [list valuetype $valuetype periodtype $periodtype db $db]
	    array set $tok [list valuesToRead 0 valuesRead 0]
	    set ret $tok
	} finally {}
	return $ret
    }

    ################################################################################################################
    # API call to fetch aggregated data or indicator values
    # param url : dhis host
    # param outfile : place to write the fetched file to
    # side effect : variable ::dhisweb::metadatastatus will be set (sometime in the future) to 
    #   one of FAILED or the http status (ok, reset, timeout or error)
    ################################################################################################################
    proc fetchMetadata {url timeout} {
	variable loginCookie
	variable log
	variable metadatastatus
	
	${log}::debug "fetching metadata"

	set fullUrl [join [list $url "dhis-web-reporting/exp/exportMetaData.action"] "/" ]
	
	set params [ ::http::formatQuery \
			 exportFormat DXF \
			 dataElements true \
			 dataElementGroups true \
			 dataElementGroupSets true \
			 indicators true \
			 indicatorGroups true \
			 indicatorGroupSets true \
			 organisationUnits true \
			 organisationUnitGroups true \
			 organisationUnitGroupSets true \
			 organisationUnitLevels true \
			 dataSets true ]

	try {
	    set metatok [::http::geturl $fullUrl -command ::dhisweb::metadataDone -progress ::dhisweb::metadataProgress \
			     -query $params \
			     -timeout $timeout \
			     -headers [list "Cookie" $loginCookie] ] 
	    return $metatok

	} on error var {
	    ${log}::debug "Metadata download error: $var"
	    ::http::cleanup metatok
	    return 0
	} finally {puts finally}
    }

    ################################################################################################################
    ############### everything below is internal callbacks which shouldn't really be called directly ###############
    ############### TODO: don't export them from the namespace :-)                                   ###############
    ################################################################################################################

    proc metadataProgress {token total current} {
	update idletasks
    }

    proc metadataDone {token} {
	upvar \#0 $token state
	variable metadatastatus
	variable log
	${log}::debug "Metadata download done: $state(http)"
	set metadatastatus $state(http)
	#parray state
    }
    
    # This callback is invoked whenever http socket has readable data
    proc datavalueCallback {socket token} {
    	upvar \#0 $token state
	variable log
    	
	# if we are currently receiving data then jump straight to task ...
	if {[array names state gunzipper] ne ""} {
	    return [::dhisweb::processData $socket $token]
	}
	
	# otherwise we are processing the header ...
	set httpcode [http::ncode $token]
    	${log}::debug "datavalue callback http: $httpcode"
	
	switch -glob -- $httpcode {
	    3* { 
		${log}::debug "redirect - password required"
		http::reset $token "login"
		return 0
	    }
	    2* {
		${log}::debug "http ok : there should be data available"
		array set metadata $state(meta)
		parray metadata
		puts [http::data $token]
		set state(valuesToRead) $metadata(X-Number-Of-Rows)
		${log}::debug "$state(valuesToRead) datavalues to read"
		
		# we are expecting gzipped data from here
		# make sure interpreter doesn't try to be clever with character encoding
		fconfigure $socket -encoding binary -translation binary
		# create a new zlib stream command for uncompressing
		set state(gunzipper) [zlib stream gunzip]
		set state(prefix) ""
		return [::dhisweb::processData $socket $token]
	    }
	    4* {
		${log}::debug "server error $state(meta)"
		# TODO: extract 4XX message
		http::reset $token "4XX"
		return 0
	    }	    
	}
    }

    # this proc handles the decompression of data and hands off
    # the decompressed data to the db insert handler
    proc processData {socket token} {
	upvar \#0 $token state
	variable log
	${log}::debug "process data"
	
	# read all the available compressed input data from socket
	try {
	    set rawdata [read $socket]
	} on error var {
	    ${log}::debug "Socket read error: $var"
	    error $var
	}
	set nchars [string length $rawdata]
	${log}::debug "$nchars bytes read" 
	
	if { $nchars>0 } {
	    # push the compressed data onto the gunzipper stream
	    $state(gunzipper) put $rawdata

	    # read as much uncompressed data from the stream as we can
	    set uncompresseddata [$state(gunzipper) get]     
	    set reslist [dbinsert $state(db) uncompresseddata $token]
	    incr state(valuesRead) [lindex $reslist 0]
	    set state(prefix) [lindex $reslist 1]
	    ${log}::debug "lines read : $state(valuesRead)"
	} else {
	    ${log}::debug "test for eof socket"
	    if {[eof $socket]} {
		${log}::debug "detected eof on socket"
		# socket is closed - clean out the tail of zlib stream
		while {![$state(gunzipper) eof]} {
		    set uncompressed [$state(gunzipper) get]     
		    set numUncompBytes [string length $uncompressed]
		    ${log}::debug "$numUncompBytes bytes from gzipper tail"
		    if {$numUncompBytes == 0} {
			# this is bad - no eof on gunzipper, but no bytes to read
			${log}::debug "gunzipper error: the connection is broken"
			::http::reset $token "broken"
			break
		    }
		    set reslist [dbinsert $state(db) uncompressed $token]
		    incr state(valuesRead) [lindex $reslist 0]
		    set state(prefix) [lindex $reslist 1]
		    ${log}::debug "tail lines read : $state(valuesRead)"
		}
	    } else {
		    ${log}::debug "Socket error: the connection is broken"
		    ::http::reset $token "broken"
	    }
	}
	
	return $nchars
    }

    # This will always be called when http operation is finished
    proc datavalueFinishHandler {tok} {
	upvar \#0 $tok state
	variable log
	variable connections
	${log}::debug "finished: $state(status)"
	switch -- $state(status) {
	    timeout { 
		dict set connections $tok status TIMEOUT }
	    login {dict set connections $tok status LOGINREQ }	
	    4XX {dict set connections $tok  status 4XX }
	    ok {dict set connections $tok  status DONE }
	    error {
		dict set connections $tok  status BROKEN
		parray $tok
	    }
	    default {dict set connections $tok status UNKNOWN }
	}

    }
    
    #############################################################################################
    #
    # this is called when login request completes
    
    proc loginCallback {token} {
	upvar \#0 $token state
	${::dhisweb::log}::debug "in loginCallback"
	variable loginCookie
	switch -- $state(status) {
	    timeout { 
		set ::dhisweb::loginstatus TIMEOUT
		${::dhisweb::log}::debug "login timedout" 
	    }
	    ok {	
		# read metadata into array
		array set metadata $state(meta)
		# look for a "Location" header
		if {[info exists metadata(Location)]} {
		    # ok .. where are we being redirected to
		    switch -glob $metadata(Location) {
			*login* { 
			    set ::dhisweb::loginstatus LFAILED 
			    ${::dhisweb::log}::debug "login failed"
			}
			default { 
			    # assume success ...
			    # grab the cookie
			    set loginCookie [lindex [split $metadata(Set-Cookie) ";"] 0]
			    ${::dhisweb::log}::debug "login success"
			    set ::dhisweb::loginstatus LSUCCESS
			}
		    } 
		} else {
		    ${::dhisweb::log}::debug "strange: $state(meta)" 
		    set ::dhisweb::loginstatus BADHOST 
		}
	    }
	    default {
		${::dhisweb::log}::debug "error ::http::error $token"
		set ::dhisweb::loginstatus BADHOST 
	    }
	}
	::http::cleanup ::dhisweb::logintok
    }

    #####################################################################################################
    # dbinsert - note dataName is passing data by reference
    #####################################################################################################
    proc dbinsert {db dataName token} {
	upvar 1 $dataName data
	upvar \#0 $token state
	variable log

	db transaction {
	    # set ptype 'm' or 'y' or 'q'
	    set ptype [string index $state(periodtype) 0]   
	    
	    set prefix $state(prefix)
	    # puts $prefix$data
	    # first line is special - we need to add prefix if any
	    set count 0
	    set nl [string first "\n" $data]
	    if {$nl == -1} { return [list $count $prefix$data] }
	    set line "$prefix[string range $data 0 [expr $nl-1] ]" 
	    switch $state(valuetype) {
		DataValues {
		    ${log}::debug "inserting first data"
		    insertData $state(db) $line $ptype
		}
		IndicatorValues {
		    ${log}::debug "inserting first indicator"
		    insertIndicator $state(db) $line $ptype
		}
	    }
	    
	    while {1} {
		incr count
		set current [expr $nl + 1]
		set nl [string first "\n" $data $current]
		if {$nl == -1} {
		    return [list $count [string range $data $current end]]
		}
		set line [string range $data $current [expr $nl-1] ] 
		switch $state(valuetype) {
		    DataValues {insertData $state(db) $line $ptype}
		    IndicatorValues {insertIndicator $state(db) $line $ptype}
		}
	    }
	}
	# keep ui ticking ..
	update idletasks
    }
    
    proc insertData {db linedata ptype} {
	variable log 
	if {[string index $linedata 0]!="#"} {    
	    set query \
		"INSERT OR REPLACE INTO aggregateddatavalue VALUES([string trim $linedata],'[string toupper $ptype]')"
	    try { $db eval $query } on error err {
		${log}::debug $err
		error $err
	    } finally {}
	}
    }

    proc insertIndicator {db linedata ptype} {
	variable log
	if {[string index $linedata 0]!="#"} {
	    set query \
		"INSERT OR REPLACE INTO aggregatedindicatorvalue VALUES([string trim $linedata],'[string toupper $ptype]')"
	    try { $db eval $query } on error err {
		${log}::debug $err
		error $err
	    } finally {}
	}
    }

}