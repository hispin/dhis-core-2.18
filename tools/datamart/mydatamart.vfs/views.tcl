proc showViews {parent} {

    # remove current contents of window
    foreach w [winfo children $parent] {
	destroy $w
    }

    set ::availableViews [lsort [::dhisdb::getViews db]]
    set ::includeViews [list {}]

    set viewsframe [ttk::labelframe $parent.viewsframe -text [mc "Select views for workbook"]]

    set frame1 [ttk::frame $viewsframe.frame1] 
    set ::availableViewsBox \
	[listbox $viewsframe.frame1.availableViewsBox -selectmode single -listvariable ::availableViews -width 30 -yscrollcommand "$frame1.yscrollAvailable set" ]
    scrollbar $viewsframe.frame1.yscrollAvailable -command "$::availableViewsBox yview"
    pack $::availableViewsBox -side left
    pack $frame1.yscrollAvailable -expand 1 -fill y

    set frame2 [ttk::frame $viewsframe.frame2] 
    set ::includedViewsBox \
	[listbox $viewsframe.frame2.includedViewsBox -selectmode single -listvariable ::includedViews -width 30 -yscrollcommand "$frame2.yscrollIncluded set"]
    scrollbar $viewsframe.frame2.yscrollIncluded -command "$::includedViewsBox yview"
    pack $::includedViewsBox -side left
    pack $frame2.yscrollIncluded -expand 1 -fill y
    

    grid [ttk::label $viewsframe.availableText -text [mc "Available views"]] -row 0 -column 0 
    grid [ttk::label $viewsframe.includedText -text [mc "Included views"]] -row 0 -column 1
    grid $frame1 -row 1 -column 0 -padx 5 -pady 5
    grid $frame2 -row 1 -column 1 -padx 5 -pady 5
    
    bind $::availableViewsBox <Double-ButtonPress-1> {
	set index [$::availableViewsBox curselection]
	if {$index != ""} {
	    set ::includedViews [lsort [linsert $::includedViews 0 [lindex $::availableViews $index]]]
	    set ::availableViews [lreplace $::availableViews $index $index]
	}
    }
    bind $::includedViewsBox <Double-ButtonPress-1> {
	set index [$::includedViewsBox curselection]
	if {$index != ""} {
	    set ::availableViews [lsort [linsert $::availableViews 0 [lindex $::includedViews $index]]]
	    set ::includedViews [lreplace $::includedViews $index $index]
	}
    }

    pack $viewsframe -padx 10 -pady 10
    pack [ttk::button $parent.go -text [mc "Create Excel workbook"] -command createExcelWithViews]
}

proc createExcelWithViews {} {
    set types { {{Excel files} {.xlsx}} }
    set excelFile [tk_getSaveFile \
		    -filetypes $types \
		    -defaultextension ".dmart" \
		    -title [mc "Create new excel file"]]
    
    ${::log}::debug "creating $excelFile with $::includedViews"
    excel::createBlankWithViews $excelFile $::dhis(dbfile) $::includedViews
    ${::log}::debug "opening $excelFile"
    eval exec [auto_execok start] \"\" [list $excelFile] &
} 

