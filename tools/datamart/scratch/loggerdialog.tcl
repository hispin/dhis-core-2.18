#
# $Id:$
#
# Requirements:
#    - Tcl/tk 8.5
#    - logger package
#    - inplace.tcl from http://wiki.tcl.tk/23475
#
# SYNOPSIS
# logger::show pathname args
# 
# DESCRIPTION
#    Show a dialog with a list of logger services and allow
#    to change the log level of each service
#
# SPECIFIC OPTIONS
#    -title
#    -parent
##

package require Tk 8.5
package require Ttk

lappend auto_path datamart.vfs/libext

package require logger

set dir [file dirname [info script]]
source [file join $dir inplace.tcl]

#
# 
#
##
proc ::logger::show { w args } {

    array set defaults [list -parent "" -title "Logger Options"]
    array set options  [array get defaults]

    foreach {option value} $args {
        if { $option ni [array names defaults] } {
            error "unknown option \"$option\""
        }
    }
    if { ([llength $args] % 2) != 0 } {
        error "value missing for \"[lindex $args [llength $args]]\""
    }
    
    array set options $args

    toplevel    $w -class LoggerUI
    wm title    $w $options(-title)
    wm iconname $w $options(-title)
    wm withdraw $w

    if { $options(-parent) ne "" } {
        wm transient $w $options(-parent)
        wm group     $w $options(-parent)
    }
    set xf [ttk::frame $w.f]

    set headings [list Service Level]
    set columns  [list text list]
    set f   [ttk::frame     $xf.f]
    set tv  [ttk::treeview  $f.tv -show headings \
                                  -columns $columns \
                                  ]
    set vsb [ttk::scrollbar $f.vsb -orient vertical \
                                   -command [list logger::UpdateTreeview $tv] \
            ]

    FillTreeview $tv
    xtreeview::_treeheaders $tv true $headings
    bind $tv <<TreeviewInplaceEdit>> [list logger::EditTreeviewItem %W %d]
    set col 1
    foreach h $headings {
        set column #$col
        $tv heading $column -text $h
        incr col
    }

    grid $tv  -row 0 -column 0 -sticky news
    grid $vsb -row 0 -column 1 -sticky  ns
    grid rowconfigure    $f 0 -weight 1
    grid columnconfigure $f 0 -weight 1

    set bf        [ttk::frame  $xf.bf]
    set btnOk     [ttk::button $bf.btnOk     -text " Ok " \
                                             -command [list logger::OnButtonClick $w $tv ok] \
                  ]
    set btnCancel [ttk::button $bf.btnCancel -text " Cancel " \
                                             -command [list logger::OnButtonClick $w $tv cancel] \
                  ]
    bind $btnOk     <Key-Return>     [list logger::OnButtonClick $w $tv ok]
    bind $btnCancel <Key-Escape>     [list logger::OnButtonClick $w $tv cancel]

    grid $btnCancel $btnOk -sticky news -padx 10 -pady 5

    grid $f  -row 0 -column 0 -sticky news
    grid $bf -row 1 -column 0 -sticky ew
    grid rowconfigure    $xf 0 -weight 1
    grid columnconfigure $xf 0 -weight 1

    pack $xf -expand 1 -fill both

    wm protocol  $w  WM_DELETE_WINDOW [list logger::OnButtonClick $w $tv cancel]

    Place $w $options(-parent)
}

#
#
#
##
proc logger::Place { w parent } {

    update idletasks
    if { $parent eq "" } {
        set parent "."

        set W [winfo screenwidth $parent]
        set H [winfo screenheight $parent]
        set X 0
        set Y 0
    } else {
        set W [winfo width $parent]
        set H [winfo height $parent]
        set X [winfo rootx $parent]
        set Y [winfo rooty $parent]
    }
    set xpos "+[ expr {$X+($W-[winfo reqwidth $w])/2}]"
    set ypos "+[ expr {$Y+($H-[winfo reqheight $w])/2}]"

    wm geometry $w "$xpos$ypos"
    wm deiconify $w 
}

#
#
#
##
proc logger::FillTreeview { tv } {

    foreach svc [logger::services] {
       set svccmd [logger::servicecmd $svc]

       set lvl [${svccmd}::currentloglevel]

       $tv insert {} end -values [list $svc $lvl]
    }

}

#
#
#
##
proc logger::UpdateTreeview { tv args } {
    ::xtreeview::updateWnds $tv
    $tv yview
}

#
#
#
##
proc logger::EditTreeviewItem { tv data } {

    puts [info level 0]
    if {[$tv children [lindex $data 1]] eq ""} {
        switch [lindex $data 0] {
            {#0} {
                xtreeview::_inplaceEntry $tv {*}$data
            }
            {bool} {
                xtreeview::_inplaceCheckbutton $tv {*}$data true false
            }
            {int} {
                xtreeview::_inplaceSpinbox $tv {*}$data 0 100 1 
            }
            {list} {
                set a [xtreeview::_inplaceList $tv {*}$data [logger::levels]]
            }
        }
    } elseif {[lindex $data 0] eq "list"} {
        puts "list"
        xtreeview::_inplaceEntryButton $tv {*}$data {
            #set %%v "tree: %W, column,item=%d"
            puts "list: tree: $tv, item '$data'"
        }
    }
}

#
#
#
##
proc logger::Close { w } {
    destroy $w
}

#
#
#
##
proc logger::OnButtonClick { w tv action } {

    if { $action eq "cancel" } {
        Close $w
        return
    }

    # update last changed item
    set item [$tv focus]
    xtreeview::_clear $tv $item
    xtreeview::_update_value $tv list $item


    # set new log levels foreach service
    foreach item [$tv children {}] {
        set values [$tv item $item -values]
        lassign $values svc lvl
        
        set svccmd [logger::servicecmd $svc]
        ${svccmd}::setlevel $lvl
    }

    Close $w
}

# Demo code
if { $argv0 eq [info script] } {

    catch {console show}
    for { set i 0 } { $i < 5 } { incr i } {
        set log($i) [logger::init L$i]
    }

    logger::show .logUI
}