set ::dhis(version) "1.0 2011-Nov-29"

#load tcl core extensions
package require msgcat

# bring msgcat commands into scope for localizing strings
namespace import msgcat::*
# load LOCALE specific message file
mcload [file join $starkit::topdir msgs]

package require logger 0.3
package require dhisweb
package require dhisdb
package require xslt
package require excel

# create root logger
set ::log [logger::init main]

set ::dhis(resource) [file join $starkit::topdir resource]
set ::dhis(createQuery) [file join $::dhis(resource) sql/create.sql]

# set 20s default login timeout
set ::dhis(login_timeout) 20000

# organize a place for temp stuff ...
set tmpdir "/tmp"
if { [info exists ::env(TMP)] } {
    set tmpdir $::env(TMP)
}

set ::dhis(tmpdir) [file join $tmpdir mydatamart_[pid]]
# make new temp directory for this session
if { ![file exists $::dhis(tmpdir)] } {
    puts "making tmpdir"
    file mkdir $::dhis(tmpdir)
}

# platform specific initialization
if {$tcl_platform(platform) eq "windows" } {
    # make some binaries  available
    foreach binfile {xsltproc.exe zip.exe unzip.exe} {
	if {![file exists [file join $::dhis(tmpdir) $binfile] ] } {
	    file copy -force [file join $::dhis(resource) extbin $binfile] $::dhis(tmpdir)
	}
    }
    xslt::init [file normalize [file join $::dhis(tmpdir) xsltproc.exe]]
    set ::dhis(zip) [file normalize [file join $::dhis(tmpdir) zip.exe]]
    set ::dhis(unzip) [file normalize [file join $::dhis(tmpdir) unzip.exe]] 
} else {
    xslt::init "xsltproc"
    set ::dhis(zip) "zip"
    set ::dhis(unzip) "unzip"
    # define console command to no-op
    proc console {args} {}
}

# while we are at it .. clean up any leftovers from unclean shutdowns
# if its more than 1 hour old delete it
set temps [glob -nocomplain [file join $tmpdir TCL]* ]
set now [clock seconds]
foreach temp $temps { 
    set mod [file mtime $temp]; 
    if {[expr ($now-$mod)/1800]>0} {
	file delete -force $temp 
    }
}

# unload xslt resources into filesystem
file copy -force [file join $::dhis(resource) xslt] $::dhis(tmpdir) 

# initialize excel package
excel::init $::dhis(tmpdir) $::dhis(zip) $::dhis(unzip)
 
# redefine http log proc to use logger
proc http::Log {args} {
    ${::dhisweb::log}::debug $args
}

# if any of these variables change then persist
#trace add variable ::dhis(myorgunit) write persistLocals
#trace add variable ::dhis(level) write persistLocals 
