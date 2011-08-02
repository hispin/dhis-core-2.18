# the settings dialog

package require msgcat
namespace import msgcat::*

trace add variable ::dhisweb::loginstatus write loginreactor 

trace add variable ::dhis(level) write checkMetadata
trace add variable ::dhis(myorgunitid) write checkMetadata
trace add variable ::dhis(password) write checkMetadata

proc showSettings { parent } {
    # remove current contents of window
    foreach w [winfo children $parent] {
	destroy $w
    }
    
    set settingsDialog $parent
    
    # retrieve any saved settings (username, url etc)
    dhisdb::getLocalMetadata db

    if {[llength [array names ::dhis url]]==0} {
	set ::dhis(url) http://
    }

    # two frames - for a new database (with no metadata) we will only
    # display the first one (settingsFrame)
    set settingsFrame [ttk::labelframe $settingsDialog.dhis2 -text [mc "DHIS2 connection"] ]
    set ouselection [ttk::labelframe $settingsDialog.ou -text [mc "Orgunit settings"] ]
    
    set settings [list \
		      [mc "URL:"] ::dhis(url) \
		      [mc "Username:"] ::dhis(username) \
		     ]
    set row 0
    foreach {label textvariable} $settings {
	grid [ttk::label $settingsFrame.label$row -text $label ] \
	    -row $row -column 0 -padx 2 -pady 2 -sticky w
	grid [ttk::entry $settingsFrame.entry$row -textvariable $textvariable -width 40] \
	    -row $row -column 1 -padx 20 -pady 2 -sticky w
	incr row
    }
    grid [ttk::label $settingsFrame.label$row -text [mc "Password:" ] ]\
	-row $row -column 0 -padx 2 -pady 2 -sticky w
    grid [ttk::entry $settingsFrame.entry$row -show "*" -textvariable ::dhis(password) -width 40] \
	-row $row -column 1 -padx 20 -pady 2 -sticky w

    grid [ttk::button $settingsFrame.login -text [mc "Login"] -command {
	dhisweb::login $::dhis(url) $::dhis(username) $::dhis(password)
	persistLocal url $::dhis(url)
	persistLocal username $::dhis(username)
    } ] -row $row -column 2 

    set ::loginImage [ttk::label $settingsFrame.loginimage ] 
    grid $::loginImage -row $row -column 3 
    incr row
    
    grid [ttk::label $settingsFrame.label$row -text [mc  "My OrgUnit:" ] ] \
	-row $row -column 0 -padx 2 -ipady 2 -sticky w
    grid [ttk::label $settingsFrame.orgunit -textvariable ::dhis(myorgunit)] \
	-row $row -column 1 -padx 20 -pady 2 -sticky w
    incr row
    grid [ttk::label $settingsFrame.label$row -text [mc  "My analysis level:" ] ] \
	-row $row -column 0 -padx 2 -ipady 2 -sticky w
    grid [ttk::label $settingsFrame.level -textvariable ::dhis(level)] \
	-row $row -column 1 -padx 20 -pady 2 -sticky w

    pack $settingsFrame -ipadx 2 -ipady 2 -padx 5 -pady 5 -fill both
    
    # orgunit settings - need to select orgunit plus the aggregation level of interest
    # set up orgunit tree selection
    # set up the query
    set ou organisationunit
    set oustr _orgunitstructure
    set query "select $oustr.organisationunitid as id, shortname, name,\
$ou.organisationunitid, \
level, idlevel1,idlevel2,idlevel3,idlevel4,idlevel5,idlevel6,idlevel7 \
from $ou, $oustr where $ou.organisationunitid=id order by level"
    
    # create the widget
    set orgtree [ttk::treeview $ouselection.orgtree \
		     -show {tree headings} \
		     -selectmode browse ]    
    $orgtree heading #0 -text "Orgunit"

    # populate it
    set ous [db eval $query {
	switch -- $level {
	    1 { $orgtree insert {} end -id $id -text "$name" }
	    2 { $orgtree insert $idlevel1 end -id $id -text "$name" }
	    3 { $orgtree insert $idlevel2 end -id $id -text "$name"}
	    4 { $orgtree insert $idlevel3 end -id $id -text "$name"}
	    5 { $orgtree insert $idlevel4 end -id $id -text "$name"}
	    6 { $orgtree insert $idlevel5 end -id $id -text "$name"}
	    7 { $orgtree insert $idlevel6 end -id $id -text "$name"}
	    8 { $orgtree insert $idlevel7 end -id $id -text "$name"}
	}
    }]
    
    # make sure current selection is visible and highlighted
    if { [llength [array names ::dhis myorgunitid]]} {
	$orgtree see $::dhis(myorgunitid)
	$orgtree selection set $::dhis(myorgunitid)
    }
    update


    # bind to selecting an item in tree
    bind $ouselection.orgtree <<TreeviewSelect>> {
	set tree [focus]

	set orgunit [$tree selection]
	set parent [$tree parent $orgunit]
	set ::dhis(myorgunit) [ $tree item $orgunit -text]
	set ::dhis(myorgunitid) $orgunit
	set ::dhis(parentorgunit) [$tree item $parent -text]
	set ::dhis(parentorgunitid) $parent
	
	persistLocal myorgunit $::dhis(myorgunit)
	persistLocal myorgunitid $::dhis(myorgunitid)
	persistLocal parentorgunit $::dhis(parentorgunit)
	persistLocal parentorgunitid $::dhis(parentorgunitid)
    } 

    set levelPicker [ttk::treeview $ouselection.level -columns {name} \
			 -displaycolumns {name} -show headings -selectmode browse -height 5 ]
    
    $levelPicker column 0 -width 100
    $levelPicker heading 0 -text "Analysis level"

    foreach {level name} [::dhisdb::getLevels db] {
	$levelPicker insert {} end  -id $level -values $name
    }

    # make sure current selection is visible and highlighted
    if { [llength [array names ::dhis level]]} {
	$levelPicker selection set $::dhis(level)
    }
    update

    bind $levelPicker <<TreeviewSelect>> {
	set ::dhis(level) [[focus] selection]
	${log}::debug "level select from [focus] is $::dhis(level)" 
	persistLocal level $::dhis(level)
    }
    
    pack $orgtree -side left 
    pack $levelPicker -side top 

    if {[::dhisdb::haveOrgunits db]>0} {
	showHaveMetadataControls
	pack $ouselection -ipadx 2 -ipady 2 -padx 5 -pady 5 -fill both -expand 1
    } else {
	set ::dhis(status) [mc "No orgunit data - you need to download metadata"]
    }
}

proc persistLocal {tag value} {
    ${::log}::debug "Persisting $tag"
    dhisdb::saveLocalMetadata db $tag $value
}

proc loginreactor {args} {
    ${::log}::debug "login status: $::dhisweb::loginstatus"
    switch $::dhisweb::loginstatus {
	LSUCCESS {
	    $::loginImage configure -image img::success
	    set ::dhis(status) [mc "Logged in"]
	}
	LFAILED {
	    $::loginImage configure -image img::fail
	    set ::dhis(status) [mc "Log in failed - check username/password"]
	}
	BADHOST {
	    $::loginImage configure -image img::fail
	    set ::dhis(status) [mc "Couldn't connect to host - check URL"]
	}
    }
}

proc checkMetadata {args} {
    if {[info exists ::dhis(level)] && [info exists ::dhis(myorgunit)] && [info exists ::dhis(password)]} {
	${::log}::debug "level and orgunit are set.  ok to download"
	$::dataUpdateButton configure -state normal
    }
}

