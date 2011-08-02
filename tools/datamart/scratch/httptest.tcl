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

${::dhisweb::log}::setlevel debug
${::dhisweb::log}::debug "Hello from the logger"

#set testhost http://192.168.1.11:8082
#set username admin
#set password district
 set testhost http://hiskenya.org
 set username bobjolliffe
 set password Easter1916 

set logintok [::dhisweb::login $testhost $username $password ]

puts "logging in"
vwait [set logintok](status)

# db eval "BEGIN TRANSACTION"
set tok [::dhisweb::fetchValues $testhost 3 18 DataValues monthly 20050101 20110331 db]

if {$tok ne 0} {
    parray $tok
    puts "state: [array get $tok state]  status: [::http::status $tok] error: [::http::error $tok]"
} else {
    puts "couldn't connect to $testhost"
}

proc httpreactor {tok state posterror write} {
    upvar \#0 $tok httpstate
    puts "trace : $state $posterror $write"
    puts "$httpstate(valuesRead) of $httpstate(valuesToRead)"
    #parray httpstate
    if {$httpstate(status) ne ""} {
	set ::done 1
    }
}
 
trace add variable [set tok](valuesRead) write "[list httpreactor $tok]"

while {[::http::status $tok] eq ""} {
     puts "waiting"
     vwait $tok
#     puts "state: [array get $tok state]"
 }

# set done 0
# vwait done

# db eval "COMMIT TRANSACTION"

# puts "status: [::http::status $tok] error: [::http::error $tok]"
# puts "[db eval "select count() from aggregateddatavalue"] data values inserted"
# puts "[db eval "select count() from aggregatedindicatorvalue"] indicator values inserted"