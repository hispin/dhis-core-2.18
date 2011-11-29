#!/usr/bin/tclsh

# extract all translatable strings from tcl files

set files [exec find ./mydatamart.vfs -iname *.tcl]

list strings

foreach f $files {
    # puts stderr "processing $f"
    set fd [open $f] 
    set contents [read $fd] 
    set match ""
    set str ""
    foreach {match msg} [regexp -inline -all {\[mc ("[^"]*")} $contents ] {
      lappend ::strings $msg 
    }  
    close $fd
}

foreach msg $strings {
  puts "$msg $msg"
}
