lappend auto_path mydatamart.vfs/libext
lappend auto_path mydatamart.vfs/libdhis

package require sqlite3
package require dhisweb

sqlite3 db ":memory:"
db eval "
        DROP TABLE IF EXISTS aggregateddatavalue;
	CREATE TABLE aggregateddatavalue ( 
        period character varying(8), 
        organisationunitid integer, 
        dataelementid integer,
        categoryoptioncomboid integer,
        \"value\" double precision,
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
        ); "

${::dhisweb::log}::setlevel info
${::dhisweb::log}::debug "Hello from the logger"

set testhost http://192.168.1.11:8082
set username admin
set password district
# set testhost http://hiskenya.org
# set username bobjolliffe
# set password 

proc http::Log {args} {
    puts $args
}

# db eval "BEGIN TRANSACTION"
proc go {} {
    set tok [::dhisweb::fetchValues $::testhost 3 18 DataValues monthly 20080101 20100331 db]
    
    if {$tok ne 0} {
	parray $tok
	puts "state: [array get $tok state]  status: [::http::status $tok] error: [::http::error $tok]"
    } else {
	puts "couldn't connect to $testhost"
    }
    #trace add variable [set tok](valuesRead) write "[list httpreactor $tok]"
}

proc httpreactor {tok state posterror write} {
    upvar \#0 $tok httpstate
    puts "trace : $state $posterror $write"
    puts "$httpstate(valuesRead) of $httpstate(valuesToRead)"
    #parray httpstate
    if {$httpstate(status) ne ""} {
	set ::done 1
    }
    .p configure -maximum $httpstate(valuesToRead) -value $httpstate(valuesRead)    
    update idletasks
}

proc login {} {
    set logintok [::dhisweb::login $::testhost $::username $::password ]
}

console show
button .l -text login -command login
button .g -text go -command go

ttk::progressbar .p
 
pack .l .g .p

# while {[::http::status $tok] eq ""} {
#     puts "waiting"
#     vwait $tok
#     puts "state: [array get $tok state]"
# }

# set done 0
# vwait done

# db eval "COMMIT TRANSACTION"

# puts "status: [::http::status $tok] error: [::http::error $tok]"
# puts "[db eval "select count() from aggregateddatavalue"] data values inserted"
# puts "[db eval "select count() from aggregatedindicatorvalue"] indicator values inserted"