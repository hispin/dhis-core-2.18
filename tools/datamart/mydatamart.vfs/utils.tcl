# clean up any debris from this and previous incarnations
proc cleanup {} {
    set tmpdir [join [list $::dhis(tmpdir) ".."] "/"]
    set oldtmps [glob -nocomplain -type d -directory $tmpdir mydatamart_* ]
    foreach oldtmp $oldtmps {
	${::log}::debug "removing $oldtmp" 
	file delete -force $oldtmp
    }   
}

# extract Export.xml from a dxf zip file to tmpdir
proc dxf2tmp {dxfFile} {
    set tmpdir $::dhis(tmpdir)
    set mntfile [vfs::zip::Mount $dxfFile $dxfFile]
    if {![file exists $dxfFile/Export.xml]} {
	error [mc "Zip file is not a dxf export file"]
    }
    file copy -force $dxfFile/Export.xml $tmpdir/Export.xml
    vfs::zip::Unmount $mntfile $dxfFile
}

proc metadataXsltCallback {fd outfile} {
    set res [gets $fd]
    if {[eof $fd]} {
	${::log}::debug [mc "Transform metadata done"]
	close $fd
	set ::dhis(status) [mc "Importing metadata"]
	update 
	try {
	    dhisdb::evalFile db $outfile
	    set ::dhis(status) [mc "Generating views"]
	    dhisdb::generateViews db
	    set ::dhis(status) [mc "Metadata import done"]
	    # redraw settings to show hierarchy
	    showSettings $::displayArea
      	} on error err {
	    tk_messageBox -icon error -message \
		[mc "Problem importing metadata\n$err"]
	} finally {
	    destroy .metadataLoading
	}

    } else {
	${::log}::debug "transform: $res"
    }
}

proc showMetadataProgress {} {
    toplevel .metadataLoading
    set mainframe [ttk::frame .metadataLoading.f -borderwidth 5 -padding 10 -relief solid] 
    ttk::label $mainframe.text -textvariable ::dhis(status)
    ttk::progressbar $mainframe.p -mode indeterminate
    pack $mainframe.text $mainframe.p
    pack $mainframe
    $mainframe.p start
    wm overrideredirect .metadataLoading 1
    wm geometry .metadataLoading +[expr [winfo screenwidth .]/2]+[expr [winfo screenheight .]/2]
    wm withdraw .metadataLoading
    wm deiconify .metadataLoading
    grab .metadataLoading
    raise .metadataLoading
    update idletask
    #tkwait .metadataLoading
    #wm title $win [mc "Metadata import"]
}

# enable buttons and menu items when db file is opened
proc showFileOpenControls {} {
    $::configButton configure -state normal
    $::metadataButton configure -state normal
    .win.menubar entryconfigure 1 -state normal
}

# enable buttons and menu items when there is metadata
proc showHaveMetadataControls {} {
    $::excelConnectButton configure -state normal
    .win.menubar entryconfigure 2 -state normal
}

