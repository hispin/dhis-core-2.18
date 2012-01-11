# widget for showing database stats

set months [list \
		[mc Jan] [mc Feb] [mc Mar] [mc Apr] [mc May] [mc Jun] \
		[mc Jul] [mc Aug] [mc Sep] [mc Oct] [mc Nov] [mc Dec] ]

for {set x 1980} {$x<2050} {incr x} {lappend years $x}

proc showstats {parent} {

    # remove current contents of window
    foreach w [winfo children $parent] {
	destroy $w
    }
    
    # these are progress indicators
    set ::dhis(weekly,datavalue,progress) 0
    set ::dhis(monthly,datavalue,progress) 0
    set ::dhis(quarterly,datavalue,progress) 0
    set ::dhis(yearly,datavalue,progress) 0
    set ::dhis(weekly,indicator,progress) 0
    set ::dhis(monthly,indicator,progress) 0
    set ::dhis(quarterly,indicator,progress) 0
    set ::dhis(yearly,indicator,progress) 0

    # default datamart synch updates monthly values
    set ::dhis(weekly,datavalue,update) 0
    set ::dhis(monthly,datavalue,update) 1
    set ::dhis(quarterly,datavalue,update) 0
    set ::dhis(yearly,datavalue,update) 0
    set ::dhis(weekly,indicator,update) 0
    set ::dhis(monthly,indicator,update) 1
    set ::dhis(quarterly,indicator,update) 0
    set ::dhis(yearly,indicator,update) 0

    # try and make some sensible defaults for from and to dates ...
    # set "from" to the month after the most recent month in the datamart
    if { [llength $::dhis(lastdatamonth) ] && [llength $::dhis(lastindicatormonth)] } {
	set lastMonthlyPeriod [expr {$::dhis(lastindicatormonth) > $::dhis(lastdatamonth)} ? $::dhis(lastdatamonth) : $::dhis(lastindicatormonth)] 
    } else {
	set lastMonthlyPeriod "197912"
    }
    set monthno [scan [string range $lastMonthlyPeriod end-1 end] %d ]  
    set ::dhis(fromYear) [string range $lastMonthlyPeriod 0 3 ]
    incr monthno
    if {$monthno == 13} { set monthno 1; incr ::dhis(fromYear) }
    set ::dhis(fromMonth) [lindex $::months [expr $monthno-1]]
    # set "to" to current month 
    set currentMonthlyPeriod [clock format [clock seconds] -format "%Y%m" ]
    set ::dhis(toMonth) [lindex $::months [expr [scan [string range $currentMonthlyPeriod end-1 end] %d]-1 ] ] 
    set ::dhis(toYear) [string range $currentMonthlyPeriod 0 3 ]
    ${::log}::debug "last: $lastMonthlyPeriod ; current: $currentMonthlyPeriod"


    # finally - layout the widgets
    set dvframe [ttk::labelframe $parent.dv -text [mc "Aggregated datavalues"] ]
    set indframe [ttk::labelframe $parent.ind -text [mc "Aggregated indicator values"] ]
    set synchframe [ttk::labelframe $parent.synch -text [mc "Update from remote dhis"] ]

    set headers [list \
		     [mc "Period type"] [mc "Count"] [mc "Earliest"] [mc "Latest"] [mc "Update"]  "Progress" ""]
    
    set column 0
    foreach header $headers {
	grid [ttk::label $dvframe.header$column -text $header] -sticky ew -column $column -row 0 
	grid [ttk::label $indframe.header$column -text $header] -sticky ew -column $column -row 0 
	incr column
    }
    grid columnconfigure $dvframe 6 -minsize 40
    grid columnconfigure $indframe 6 -minsize 40

    #########################################################################################################
    # datavalues
    #########################################################################################################
    grid [ttk::label $dvframe.weekly -text [mc "Weekly"] -relief flat ] -column 0 -row 1 -sticky ew
    grid [ttk::label $dvframe.numwv -textvariable ::dhis(weeklydatavalues) ] -column 1 -row 1 -sticky ew
    grid [ttk::label $dvframe.ewv -textvariable ::dhis(firstdataweek) ] -column 2 -row 1 -sticky ew
    grid [ttk::label $dvframe.lwv -textvariable ::dhis(lastdataweek) ] -column 3 -row 1 -sticky ew
    grid [ttk::checkbutton $dvframe.cw -variable ::dhis(weekly,datavalue,update) ] -column 4 -row 1 -sticky ew
    grid [ttk::progressbar $dvframe.pw -variable ::dhis(weekly,datavalue,progress) ] -column 5 -row 1 -sticky ew -pady 3
    set ::dhis(weekly,datavalue,image) [ttk::label $dvframe.iw ] 
    grid $::dhis(weekly,datavalue,image) -row 1 -column 6 
    grid columnconfigure $dvframe 6 -minsize 40

    grid [ttk::label $dvframe.monthly -text [mc "Monthly"] -relief flat ] -column 0 -row 2 -sticky ew
    grid [ttk::label $dvframe.nummv -textvariable ::dhis(monthlydatavalues) ] -column 1 -row 2 -sticky ew
    grid [ttk::label $dvframe.emv -textvariable ::dhis(firstdatamonth) ] -column 2 -row 2 -sticky ew
    grid [ttk::label $dvframe.lmv -textvariable ::dhis(lastdatamonth) ] -column 3 -row 2 -sticky ew
    grid [ttk::checkbutton $dvframe.cm -variable ::dhis(monthly,datavalue,update) ] -column 4 -row 2 -sticky ew
    grid [ttk::progressbar $dvframe.pm -variable ::dhis(monthly,datavalue,progress) ] -column 5 -row 2 -sticky ew -pady 3
    set ::dhis(monthly,datavalue,image) [ttk::label $dvframe.im ] 
    grid $::dhis(monthly,datavalue,image) -row 2 -column 6 

    grid [ttk::label $dvframe.quarterly -text [mc "Quarterly"] -relief flat ] -column 0 -row 3 -sticky ew
    grid [ttk::label $dvframe.numqv -textvariable ::dhis(quarterlydatavalues) ] -column 1 -row 3 -sticky ew
    grid [ttk::label $dvframe.eqv -textvariable ::dhis(firstdataquarter) ] -column 2 -row 3 -sticky ew
    grid [ttk::label $dvframe.lqv -textvariable ::dhis(lastdataquarter) ] -column 3 -row 3 -sticky ew
    grid [ttk::checkbutton $dvframe.cq -variable ::dhis(quarterly,datavalue,update) ] -column 4 -row 3 -sticky ew
    grid [ttk::progressbar $dvframe.pq -variable ::dhis(quarterly,datavalue,progress) ] -column 5 -row 3 -sticky ew -pady 3
    set ::dhis(quarterly,datavalue,image) [ttk::label $dvframe.iq ] 
    grid $::dhis(quarterly,datavalue,image) -row 3 -column 6 

    grid [ttk::label $dvframe.yearly -text [mc "Yearly"] ] -column 0 -row 4 -sticky ew
    grid [ttk::label $dvframe.numyv -textvariable ::dhis(yearlydatavalues)] -column 1 -row 4 -sticky ew
    grid [ttk::label $dvframe.eyv -textvariable ::dhis(firstdatayear)] -column 2 -row 4 -sticky ew
    grid [ttk::label $dvframe.lyv -textvariable ::dhis(lastdatayear)] -column 3 -row 4 -sticky ew
    grid [ttk::checkbutton $dvframe.cy -variable ::dhis(yearly,datavalue,update)] -column 4 -row 4 -sticky ew
    grid [ttk::progressbar $dvframe.py -variable ::dhis(yearly,datavalue,progress)] -column 5 -row 4 -sticky ew -pady 3
    set ::dhis(yearly,datavalue,image) [ttk::label $dvframe.iy ] 
    grid $::dhis(yearly,datavalue,image) -row 4 -column 6 
    
    #########################################################################################################
    # indicators
    #########################################################################################################
    grid [ttk::label $indframe.weekly -text [mc "Weekly"] ] -column 0 -row 1 -sticky ew
    grid [ttk::label $indframe.numwv -textvariable ::dhis(weeklyindicatorvalues)] -column 1 -row 1 -sticky ew
    grid [ttk::label $indframe.ewv -textvariable ::dhis(firstindicatorweek)] -column 2 -row 1 -sticky ew
    grid [ttk::label $indframe.lwv -textvariable ::dhis(lastindicatorweek)] -column 3 -row 1 -sticky ew
    grid [ttk::checkbutton $indframe.cw -variable ::dhis(weekly,indicator,update)] -column 4 -row 1 -sticky ew 
    grid [ttk::progressbar $indframe.pw -variable ::dhis(weekly,indicator,progress)] -column 5 -row 1 -sticky ew -pady 3
    set ::dhis(weekly,indicator,image) [ttk::label $indframe.iw ] 
    grid $::dhis(weekly,indicator,image) -row 1 -column 6 

    grid [ttk::label $indframe.monthly -text [mc "Monthly"] ] -column 0 -row 2 -sticky ew
    grid [ttk::label $indframe.nummv -textvariable ::dhis(monthlyindicatorvalues)] -column 1 -row 2 -sticky ew
    grid [ttk::label $indframe.emv -textvariable ::dhis(firstindicatormonth)] -column 2 -row 2 -sticky ew
    grid [ttk::label $indframe.lmv -textvariable ::dhis(lastindicatormonth)] -column 3 -row 2 -sticky ew
    grid [ttk::checkbutton $indframe.cm -variable ::dhis(monthly,indicator,update)] -column 4 -row 2 -sticky ew 
    grid [ttk::progressbar $indframe.pm -variable ::dhis(monthly,indicator,progress)] -column 5 -row 2 -sticky ew -pady 3
    set ::dhis(monthly,indicator,image) [ttk::label $indframe.im ] 
    grid $::dhis(monthly,indicator,image) -row 2 -column 6 

    grid [ttk::label $indframe.quarterly -text [mc "Quarterly"] ] -column 0 -row 3 -sticky ew
    grid [ttk::label $indframe.numqv -textvariable ::dhis(quarterlyindicatorvalues)] -column 1 -row 3 -sticky ew
    grid [ttk::label $indframe.eqv -textvariable ::dhis(firstindicatorquarter)] -column 2 -row 3 -sticky ew
    grid [ttk::label $indframe.lqv -textvariable ::dhis(lastindicatorquarter)] -column 3 -row 3 -sticky ew
    grid [ttk::checkbutton $indframe.cq -variable ::dhis(quarterly,indicator,update)] -column 4 -row 3 -sticky ew
    grid [ttk::progressbar $indframe.pq -variable ::dhis(quarterly,indicator,progress)] -column 5 -row 3 -sticky ew -pady 3
    set ::dhis(quarterly,indicator,image) [ttk::label $indframe.iq ] 
    grid $::dhis(quarterly,indicator,image) -row 3 -column 6 

    grid [ttk::label $indframe.yearly -text [mc "Yearly"] ] -column 0 -row 4 -sticky ew
    grid [ttk::label $indframe.numyv -textvariable ::dhis(yearlyindicatorvalues)] -column 1 -row 4 -sticky ew
    grid [ttk::label $indframe.eyv -textvariable ::dhis(firstindicatoryear)] -column 2 -row 4 -sticky ew
    grid [ttk::label $indframe.lyv -textvariable ::dhis(lastindicatoryear)] -column 3 -row 4 -sticky ew
    grid [ttk::checkbutton $indframe.by -variable ::dhis(yearly,indicator,update)] -column 4 -row 4 -sticky ew
    grid [ttk::progressbar $indframe.py -variable ::dhis(yearly,indicator,progress)] -column 5 -row 4 -sticky ew -pady 3
    set ::dhis(yearly,indicator,image) [ttk::label $indframe.iy ] 
    grid $::dhis(yearly,indicator,image) -row 4 -column 6 
		    
    grid [ttk::label $synchframe.lfrom -text [mc "From"] ] -column 0 -row 0 -sticky ew
    grid [ttk::combobox $synchframe.cfromMonth -width 4 -values $::months -textvariable ::dhis(fromMonth)] -column 1 -row 0 -sticky e
    grid [ttk::combobox $synchframe.cfromYear -width 5 -values $::years -textvariable ::dhis(fromYear)] -column 2 -row 0 -sticky e
    grid [ttk::label $synchframe.lto -text [mc "To"] ] -column 0 -row 1 -sticky ew
    grid [ttk::combobox $synchframe.ctoMonth -width 4 -values $::months -textvariable ::dhis(toMonth)] -column 1 -row 1 -sticky e
    grid [ttk::combobox $synchframe.ctoYear -width 5 -values $::years -textvariable ::dhis(toYear)] -column 2 -row 1 -sticky e

    grid [ttk::button $synchframe.getdata -text [mc "Download %s data for %s" [dhisdb::getLevelName db $::dhis(level)] $::dhis(myorgunit)] \
	      -command [list synchData $::dhis(myorgunitid) $::dhis(level)]] \
	-column 0 -row 3 -columnspan 3 -sticky new
    
    grid [ttk::button $synchframe.getpeerdata -text [mc "Download %s data from peers" [dhisdb::getLevelName db [::dhisdb::getLevelByOrgunit db $::dhis(myorgunitid)]]] \
	      -command [list synchData [dhisdb::getParentOrgUnit db $::dhis(myorgunitid)] [::dhisdb::getLevelByOrgunit db $::dhis(myorgunitid)] ] ] \
	-column 0 -row 4 -columnspan 3 -sticky new    
    
    grid $synchframe -row 0 -column 1 -rowspan 2 -padx 20 -pady 20 -sticky nsew
    grid $dvframe -row 0 -column 0 -padx 20 -pady 10
    grid $indframe -row 1 -column 0 -padx 20 -pady 10

    update idletasks


}

proc synchData {orgunit level} {
    ${::dhisweb::log}::setlevel debug
    set to "[format %04d $::dhis(toYear)][format %02d [ expr [lsearch $::months $::dhis(toMonth)]+1]]28" ; #TODO: fix last day of month
    set from "[format %04d $::dhis(fromYear)][format %02d [expr [lsearch $::months $::dhis(fromMonth)]+1]]01"

    ${::log}::debug  "Download periods $from $to"
    set ::dhis(status) [mc "Downloading aggregate data"]
    try {
	foreach downloadtype {weekly,datavalue monthly,datavalue quarterly,datavalue yearly,datavalue weekly,indicator monthly,indicator quarterly,indicator yearly,indicator } {
	    $::dhis($downloadtype,image) configure -image {}
	    if {$::dhis($downloadtype,update) == 1} {
		
		lassign [split $downloadtype ,] periodType valueType
		switch $valueType {
		    datavalue {set valueTypeParam DataValues}
		    indicator {set valueTypeParam IndicatorValues}
		}
		
		# delete old values
		dhisdb::deleteValues db $periodType $level $orgunit $valueType $from $to
		
		${::log}::debug "Downloading for $periodType $valueType"
		set tok [::dhisweb::fetchValues $::dhis(url) $level $orgunit $valueTypeParam $periodType $from $to db]
		
		if {$tok ne 0} {
		    # set up some bindings
		    trace add variable [set tok](valuesRead) write "[list dataReactor $tok $downloadtype]"
		    trace add variable [set tok](status) write "[list finishReactor $tok $downloadtype]"
		} else {
		    error [mc "Failed to connect to server"]
		} 
	    }
	} 
    } on error msg {
	${::log}::debug $msg
	set ::dhis(status) $msg
	showSettings $::displayArea
    }
}

# this gets called when data is being read - used to update progress
proc dataReactor {tok downloadtype args} {
    upvar \#0 $tok httpstate
    ${::log}::debug "$downloadtype data trace : $tok $args"
    ${::log}::debug "$httpstate(valuesRead) of $httpstate(valuesToRead)"
    #parray httpstate
    if {$httpstate(status) ne ""} {
	set ::done 1
    }
    if {$httpstate(valuesToRead) != 0} {
	set ::dhis($downloadtype,progress) \
	    [expr ($httpstate(valuesRead)*100)/$httpstate(valuesToRead)]  
    }
    dhisdb::updateStatus db
    update idletasks
}

# this gets called when http state is set .. ie download is complete
proc finishReactor {tok downloadtype args} {
    upvar \#0 $tok httpstate
    set downloadResult $httpstate(status)
    ${::log}::debug "$downloadtype finished: $downloadResult"    
    set ::dhis($downloadtype,progress) 0
    switch $downloadResult {
	login { 
	    set ::dhis(status) [mc "Login failure"]
	    showSettings $::displayArea
	}
	ok {
	    set ::dhis(status) [mc "Download finished"]
	    $::dhis($downloadtype,image) configure -image img::success
	}
	default {
	    parray $tok
	    set ::dhis(status) [mc "Download failed - see console for detailed error"]
	    $::dhis($downloadtype,image) configure -image img::fail
	}
    }
}
