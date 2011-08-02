# initialize starkit (virtual file system)
package require starkit 
starkit::startup 

starkit::autoextend [file join $starkit::topdir lib tcllib] 
starkit::autoextend [file join $starkit::topdir lib tklib] 

lappend auto_path [file join $starkit::topdir libext]
lappend auto_path [file join $starkit::topdir libdhis]

# do some initialization
source [file join $starkit::topdir appinit.tcl]
source [file join $starkit::topdir utils.tcl]

# load up the app
source [file join $starkit::topdir mainwindow.tcl]

