package require Tk

source [file join $starkit::topdir tooltip.tcl]
source [file join $starkit::topdir images.tcl]
source [file join $starkit::topdir actions.tcl]
source [file join $starkit::topdir settings.tcl]
source [file join $starkit::topdir stats.tcl]
source [file join $starkit::topdir views.tcl]

proc styles {} {
    ttk::style configure Treeview -rowheight 15

    ttk::style configure spreadsheetLabel.TLabel {
	-background white
    }
}

# create the main window
proc createApp {} {
    toplevel .win

    # draw the pieces
    set ::menu [createMenu]
    set ::toolbar [createToolbar]
    # set ::mainNotebook [createMainNotebook]
    set ::statusbar [createStatusbar]
    set ::displayArea [ttk::frame .win.display]
 
    pack $::toolbar -fill x -padx 15 -pady 15
    pack $::displayArea
    # pack $::mainNotebook -fill both -side top  -padx 15 -pady 15 
    pack $::statusbar -fill x -side bottom -padx 15 -pady 15

    update
}

proc createMenu {} {
    option add *tearOff 0

    menu .win.menubar
    .win configure -menu .win.menubar
    set m .win.menubar
    menu $m.file
    menu $m.datamart
    menu $m.reports
    menu $m.help
    $m add cascade -menu $m.file -label [mc "File"]
    $m add cascade -menu $m.datamart -label [mc "DataMart"]  -state disabled
    $m add cascade -menu $m.reports -label [mc "Reports"] -state disabled
    $m add cascade -menu $m.help -label [mc "Help"] 
    
    # the file menu
    $m.file add command -label [mc "New"] -command newDatamart	
    $m.file add command -label [mc "Open..."] -command openDatamart
    $m.file add command -label [mc "Exit"] -command appExit	

    # the datamart menu
    $m.datamart add command -label [mc "Settings"] -command { showSettings $::displayArea }	
    $m.datamart add command -label [mc "Update aggregate data"] -command appAggregatedData	
    $m.datamart add separator
    $m.datamart add command -label [mc "Load Metadata from file"] -command appLoadMetadataFromFile
    $m.datamart add command -label [mc "Load Metadata from dhis"] -command appLoadMetadataFromDHIS	
    $m.datamart add separator
    $m.datamart add command -label [mc "Import sql from file"] -command appLoadSqlFromFile

    # the reports menu
    $m.reports add command -label [mc "Connect to existing excel file"] -command excelConnect	
    $m.reports add command -label [mc "New excel file"] -command { showViews $::displayArea }
#    $m.reports add separator
#    $m.reports add command -label [mc "Excel Data"] -command {}	
#    $m.reports add command -label [mc "CSV"] -command {}
#    $m.reports add command -label [mc "Html"] -command {}
#    $m.reports add command -label [mc "SDMX-HD"] -command {}

    # help menu
    $m.help add command -label [mc "Console (F2)"] -command {console show}	
    $m.help add command -label [mc "About"] -command appHelpAbout	

    return $m
}

# create a toolbar
proc createToolbar {} {
    set tf [ttk::frame .win.tf -relief groove -borderwidth 1]
    ttk::button $tf.new -image img::new \
		-compound top -style Toolbutton -command newDatamart
    setTooltip $tf.new [mc "Create a new local datamart"]

    ttk::button $tf.load -image img::load \
		-compound top -style Toolbutton -command openDatamart
    setTooltip $tf.load [mc "Open an existing local datamart"]

    set ::configButton [ttk::button $tf.config -image img::config \
			      -compound top -style Toolbutton -state disabled -command {showSettings $::displayArea}]
    setTooltip $tf.config [mc "Modify datamart settings\nand login details"]

    set ::metadataButton [ttk::button $tf.metadata -image img::hierarchy \
			      -compound top -style Toolbutton -state disabled -command appLoadMetadataFromDHIS]
    setTooltip $tf.metadata [mc "Update metadata from DHIS"]

    set ::dataUpdateButton [ttk::button $tf.aggdata -image img::datamart \
				-compound top -style Toolbutton -state disabled -command appAggregatedData]
    setTooltip $tf.aggdata [mc "Update aggregate data from DHIS"]
    
    set ::excelConnectButton [ttk::button $tf.excel -image img::excel \
				  -compound top -style Toolbutton -state disabled -command excelConnect]
    setTooltip $tf.excel [mc "Connect to existing excel file"]

    ttk::button $tf.exit -image img::exit \
		-compound top -style Toolbutton -command appExit
    setTooltip $tf.exit [mc "Exit application"]

    pack $tf.new $tf.load $tf.aggdata $tf.metadata $tf.config $tf.excel -padx 2 -pady 2 -side left
    pack $tf.exit -padx 2 -pady 2 -side right
    # remember the list of disabled buttons
    return $tf
}

proc createStatusbar {} {
    return [ttk::label .win.status -textvariable ::dhis(status) -anchor w -relief groove -padding {5 2} ]
}

proc enableDatamartControls {} {
    foreach button $::datamartToolButtons {
	$button configure -state normal
    }
    $::menu entryconfigure [mc DataMart] -state normal
    $::menu entryconfigure [mc Reports] -state normal
}

proc disableDatamartControls {} {
    foreach button $::datamartToolButtons {
	$button configure -state disabled
    }
    $::menu entryconfigure [mc DataMart] -state disabled
    $::menu entryconfigure [mc Reports] -state disabled
}

proc info2window {msg} {
    .win.nb.log.tree insert {} end -text "[mc Info]: $msg"
}

proc debug2window {msg} {
    .win.nb.log.tree insert {} end -text "[mc Debug]: $msg"
}

proc trace2window {dict} {
    .win.nb.log.tree insert {} end -text "$dict"
}

proc cleantmps {} {
    # clean up any debris from this and previous incarnations
    cleanup 
}

proc main {} {
    wm withdraw .

    set ::dhis(status) "No datamart open"
    set ::dhis(conn) 0
    set ::dhis(dbfile) ""
    set ::dhis(metadata) false

    # call statusChange every time dhis(status) is written
    trace add variable ::dhis(status) write statusChange

    # todo:  tone this down later
    set ::dhis(loggingEnabled) 1
    set ::dhis(loglevel) "debug"
    
    # load custom styles
    styles
    # draw the main window
    createApp
    update idletasks

    set width [expr [winfo screenwidth .]/2] 
    set height [winfo height .win]
    set x [expr {[winfo screenwidth .]/4}]
    set y [expr {([winfo screenheight .]-[winfo height .win])/2}]
    #wm geometry  .win [format "%dx%d+%d+%d" $width $height $x $y]
    wm transient .win .
    wm title     .win "MyDatamart"
    wm withdraw  .win
    wm deiconify .win   
    
    # hook user closing the main window ..
    wm protocol .win WM_DELETE_WINDOW {
	appExit
    }

    # ${::log}::trace add -ns ::dhisweb
    # ${::log}::trace on

    ${::log}::debug "tmpdir: $::dhis(tmpdir)" 

    if {$::argc == 1} {
	set dbfile [lindex $::argv 0]
	${::log}::debug "opening $dbfile"
	openDatamartFile $dbfile
    }	

    bind .win <F2> { console show }
}

main