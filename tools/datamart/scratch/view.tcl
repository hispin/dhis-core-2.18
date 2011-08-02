proc showviews {parent} {
    # remove current contents of parent
    foreach w [winfo children $parent] {
	destroy $w
    }

    set views [ttk::treeview $parent.views -columns viewname -show {}]

    db eval "select id,name from dataview" {
    	$views insert {} end -id $id -values $name
    }

    ttk::labelframe $parent.view -text [mc "Detail"]
    $views column viewname -width 100 
    grid $views -row 0 -column 0 
    grid $parent.view -row 0 -column 1

    # bind to selecting an item in tree
    bind $views <<TreeviewSelect>> {
    	set tree [focus]
    	showview [winfo parent $tree].view [$tree selection]
    }
}

# widget for showing a view
proc showview {parent view} {
    # remove current contents of parent
    foreach w [winfo children $parent] {
	destroy $w
    }

    # read into viewparams array
    db eval "select * from dataview where id='$view'" ::viewparams {}
    set dimensions [db eval "select dimension from dimensions_in_view where dataview='$view'"]

    parray ::viewparams
    puts $dimensions
   
    set paramframe [ttk::labelframe $parent.pf -text [mc "View parameters"]]
    grid [ttk::label $paramframe.lname -text [mc "Name "]] -row 0 -column 0 -sticky e
    grid [ttk::entry $paramframe.ename -textvariable ::viewparams(name) ] -row 0 -column 1 -sticky ew 
    grid [ttk::label $paramframe.ldesc -text [mc "Description "]] -row 0 -column 2 -sticky e
    grid [ttk::entry $paramframe.edesc -textvariable ::viewparams(description) -width 50] -row 0 -column 3 -sticky ew
    grid [ttk::label $paramframe.ltype -text [mc "Type "]] -row 1 -column 0 -sticky e
    grid [ttk::combobox $paramframe.ctype -textvariable ::viewparams(datatype) -values [list [mc "Indicator"] [mc "DataElement"]] ] -row 1 -column 1
    grid [ttk::label $paramframe.lperiod -text [mc "Period type "]] -row 2 -column 0 -sticky e
    grid [ttk::combobox $paramframe.cperiod -textvariable ::viewparams(periodType) -values [list [mc "M"] [mc "Y"]] ] -row 2 -column 1
   
    pack $paramframe -fill x -padx 10 -pady 5
		    
    set row 0
    

    db eval "select id as dimid, name as dimname from dimensiontype" {
	set dimframe [ttk::labelframe $parent.df$dimid -text $dimname]

	set col 0
	db eval "select dimension.*,dimensiontype.name as dimensiontype from dimension join dimensiontype where dimtype=$dimid AND dimension.dimtype=dimensiontype.id" {
	    grid [ttk::label $dimframe.l$name -text $name ] -column $col -row $row -sticky e
	    incr col
	    grid [ttk::checkbutton $dimframe.$id ] -column $col -row $row -padx 5 -sticky e
	    set col [expr [incr col]%8]
	    if {$col==0} {incr row}
	    if {[lsearch $dimensions $id]>-1} { 
		puts "$id selected"
		$dimframe.$id state selected
	    } else {
		puts "$id unselected"
		$dimframe.$id state 
	    }
	}
	
	pack $dimframe -fill x -padx 10 -pady 5  
    }
    update

}