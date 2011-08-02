package provide xslt 1.0

package require vfs::zip
package require logger

namespace eval xslt {
    set log [logger::init xslt]

    # initialize with name of xsltproc executable 
    proc init {xsltproc } {
	set ::xslt::xsltproc $xsltproc
    }

    # transform_asynch
    # Performs an xslt transform on a file.  xsltproc runs as background process.
    # User should register a callback proc using callback parameter.
    # param xsltfile - the file containing the xslt
    # param infile - the file to be transormed
    # param outfile - the result of the transform
    # param callback - a proc to be called when transform is done 
    #                - should accept 2 parameters: fd (the pipe fd) and outfile (the result) 
    # param params - a list of name/value pairs passed as stringparams to xsltproc
    proc transform_asynch {xsltfile infile outfile callback {params {}}} {
	set xparams ""
	foreach {name value} $params {
	    set xparams "$xparams --stringparam $name $value"
	}   
	
	set xsltexec "\"$xslt::xsltproc\" $xparams -o $outfile $xsltfile $infile 2>@1"
	# perform transfrom in background
	set transformProcess [open |$xsltexec]
	fconfigure $transformProcess -blocking 0 -buffering line
	fileevent $transformProcess readable [list $callback $transformProcess $outfile]
    }

    # transform
    # Performs an xslt transform on a file.
    # param xsltfile - the file containing the xslt
    # param infile - the file to be transormed
    # param outfile - the result of the transform
    # param params - a list of name/value pairs passed as stringparams to xsltproc
    proc transform {xsltfile infile outfile {params {}}} {
	if {[llength $params]>0} {
	    set xparams {}
	    foreach {name value} $params {
		set xparams "$xparams --stringparam $name \"$value\""
	    }   
	    set command "\"$xslt::xsltproc\" $xparams -o \"$outfile\" \"$xsltfile\" \"$infile\"\n"
	    ${xslt::log}::debug "Executing: $command"
	    set res [exec cmd << $command 2>@1]
	    # ${xslt::log}::debug $res
	} else {
	    exec "$xslt::xsltproc" -o $outfile $xsltfile $infile 2>@1
	}  
    }
}