# BSD license
package require Tk 8.5

package provide xtreeview 1.2

namespace eval xtreeview {
  # intercept all the events changing focus
  bind XTreeview <<TreeviewSelect>> "+::xtreeview::checkFocus %W"
  bind XTreeview <ButtonRelease-1> "+::xtreeview::checkFocus %W %x %y"
  bind XTreeview <KeyRelease> "+::xtreeview::checkFocus %W"
  bind XTreeview <ButtonPress-4> "+after idle ::xtreeview::updateWnds %W"
  bind XTreeview <ButtonPress-5> "+after idle ::xtreeview::updateWnds %W"
  bind XTreeview <MouseWheel> "+after idle ::xtreeview::updateWnds %W"
  bind XTreeview <B1-Motion> {+if {$ttk::treeview::State(pressMode)=="resize"} { ::xtreeview::updateWnds %W }}
  bind XTreeview <Configure> "+after idle ::xtreeview::updateWnds %W"
  bind XTreeview <Home> {%W focus [lindex [%W children {}] 0]}
  bind XTreeview <End> {%W focus [lindex [%W children {}] end]}
  
  # images indicating sort order 
  image create bitmap ::xtreeview::arrow(0) -data {
     #define arrowUp_width 7
     #define arrowUp_height 4
     static char arrowUp_bits[] = {
        0x08, 0x1c, 0x3e, 0x7f
     };
   }
   image create bitmap ::xtreeview::arrow(1) -data {
     #define arrowDown_width 7
     #define arrowDown_height 4
     static char arrowDown_bits[] = {
        0x7f, 0x3e, 0x1c, 0x08
     };
   }
   image create bitmap ::xtreeview::arrowBlank -data {
     #define arrowBlank_width 7
     #define arrowBlank_height 4
     static char arrowBlank_bits[] = {
        0x00, 0x00, 0x00, 0x00
     };
   }

  
  variable curfocus
  
  # check, if focus has changed
  proc checkFocus {w {X {}} {Y {}} } {
    variable curfocus
    if {![info exists curfocus($w)]} {
      set changed 1
    } elseif {$curfocus($w)!=[$w focus]} {
      _clear $w $curfocus($w)
      set changed 1
    } else {
      set changed 0
    }
    set newfocus [$w focus]
    if {$changed} {
      if {$newfocus!=""} {
        _focus $w $newfocus
        if {$X!=""} {
          set col [$w identify column $X $Y]
          if {$col!=""} {
            if {$col!="#0"} {
              set col [$w column $col -id]
            }  
          }  
          catch {focus $w.$col}
        }  
      }        
      set curfocus($w) $newfocus
      updateWnds $w 
    }
  }
  # update inplace edit widgets positions
  proc updateWnds {w} {
    variable curfocus
    if {![info exists curfocus($w)]} { return }
    set item $curfocus($w)
    if {$item==""} { return }
    foreach col [concat [$w cget -columns] #0] {
      set wnd $w.$col
      if {[winfo exists $wnd]} {
        set bbox [$w bbox $item $col]
        if {$bbox==""} { 
          place forget $wnd
        } else {
          place $wnd -x [lindex $bbox 0] -y [lindex $bbox 1] -width [lindex $bbox 2] -height [lindex $bbox 3]
        }
      }
    }
  }
  # remove all inplace edit widgets
  proc _clear {w item} {
    foreach col [concat [$w cget -columns] #0] {
      set wnd $w.$col
      if {[winfo exists $wnd]} { 
        destroy $wnd
      }
    }
  }
  # called when focus item has changed
  proc _focus {w item} {
    set cols [$w cget -displaycolumns]
    if {$cols=="#all"} { 
      set cols [concat #0 [$w cget -columns]]
    }
    foreach col $cols {
      event generate $w <<TreeviewInplaceEdit>> -data [list $col $item]
      if {[winfo exists $w.$col]} {
        bind $w.$col <Key-Tab> {focus [tk_focusNext %W]}
        bind $w.$col <Shift-Key-Tab> {focus [tk_focusPrev %W]}
      }
    }
  }
  # hierarchical sorting procedure
  proc _sorttree {tree col direction {isroot 1} {root {}} } {
    if {$isroot} {
      if {$col!="#0"} {
        set col [$tree column $col -id]
      }
      set selection [$tree selection]
      $tree selection remove $selection
      set focus [$tree focus]
      $tree focus {}
      checkFocus $tree
    }
    # Build something we can sort
    set data {}
    if {$col=="#0"} {
      foreach row [$tree children $root] {
          lappend data [list [$tree item $row -text] $row]
      }
    } else {
      foreach row [$tree children $root] {
          lappend data [list [$tree set $row $col] $row]
      }
    }  
    if {$data!=""} {
      set dir [expr {$direction ? "-decreasing" : "-increasing"}]
      set r -1
      # Now reshuffle the rows into the sorted order
      foreach info [lsort -dictionary -index 0 $dir $data] {
          $tree move [lindex $info 1] $root [incr r]
          if {[$tree item [lindex $info 1] -open]} {
            _sorttree $tree $col $direction 0 [lindex $info 1]
          }  
      }
    }  
    if {$isroot} {
       # Switch the heading so that it will sort in the opposite direction
      variable curfocus
      catch {
        eval [lindex [after info $curfocus($tree,sorticon)] 0]
        after cancel $curfocus($tree,sorticon)
      }
      set curfocus($tree,sorticon) [after 3000 [list catch [list $tree heading $col -image ::xtreeview::arrowEmpty]]]
      $tree heading $col -command [namespace code [list _sorttree $tree $col [expr {1-$direction}]]] -image ::xtreeview::arrow($direction)
      $tree selection set $selection
      $tree focus $focus
      checkFocus $tree
    }  
  }
  # installs in-place edit bindings, adjusts tree header columns width, assigns column names, installs sorting handlers  
  proc _treeheaders {path {sort true} {treecolumnname {}} } {
    set tags [bindtags $path]
    if {[lsearch -exact $tags XTreeview]<0} {
      bindtags $path [linsert $tags [lsearch -exact $tags Treeview]+1 XTreeview]
    }
    set font [::ttk::style lookup [$path cget -style] -font]
    if {$font==""} {
      set font TkTextFont
    }
    foreach col [$path cget -columns] {
      if {$col!=""} {
        if {$sort} {
          $path heading $col -text $col -command [namespace code [list _sorttree $path $col 0]] 
        } else {
          $path heading $col -text $col 
        }
        $path column $col -width [font measure $font @@@@$col]
      }
    }
    if {$treecolumnname!=""} {
      $path heading #0 -text $treecolumnname
      $path column  #0 -width [font measure $font @@@@$treecolumnname]
      if {$sort} {
        $path heading #0 -command [namespace code [list _sorttree $path #0 0]] 
      }
    }
  }
  # helper functions for inplace edit
  proc _get_value {w column item} {
    if {$column=="#0"} {
      return [$w item $item -text]
    } else {
      return [$w set $item $column]
    }
  }
  proc _set_value {w column item value} {
    if {$column=="#0"} {
      $w item $item -text $value
    } else {
      $w set $item $column $value
    }
  }
  proc _update_value {w column item} {
    variable curfocus
    set value [_get_value $w $column $item]
    set newvalue $curfocus($w,$column)
    if {$value!=$newvalue} {
      _set_value $w $column $item $newvalue
    }
  }
  # these functions create widgets for in-place edit, use them in your in-place edit handler
  proc _inplaceEntry {w column item} {
    variable curfocus
    set wnd $w.$column 
    ttk::entry $wnd -textvariable [namespace current]::curfocus($w,$column) -width 3
    set curfocus($w,$column) [_get_value $w $column $item]
    bind $wnd <Destroy> [namespace code [list _update_value $w $column $item]]
  }
  proc _inplaceEntryButton {w column item script} {
    variable curfocus
    set wnd $w.$column
    ttk::frame $wnd
    pack [ttk::entry $wnd.e -width 3 -textvariable [namespace current]::curfocus($w,$column)] -side left -fill x -expand true
    pack [ttk::button $wnd.b -style Toolbutton -text "..." -command [string map [list %v [namespace current]::curfocus($w,$column)] $script]] -side left -fill x 
    set curfocus($w,$column) [_get_value $w $column $item]
    bind $wnd <Destroy> [namespace code [list _update_value $w $column $item]]
  }
  proc _inplaceCheckbutton {w column item {onvalue 1} {offvalue 0} } {
    variable curfocus
    set wnd $w.$column 
    ttk::checkbutton $wnd -variable [namespace current]::curfocus($w,$column) -onvalue $onvalue -offvalue $offvalue
    set curfocus($w,$column) [_get_value $w $column $item]
    bind $wnd <Destroy> [namespace code [list _update_value $w $column $item]]
  }
  proc _inplaceList {w column item values} {
    variable curfocus
    set wnd $w.$column 
    ttk::combobox $wnd -textvariable [namespace current]::curfocus($w,$column) -values $values -state readonly 
    set curfocus($w,$column) [_get_value $w $column $item]
    bind $wnd <Destroy> [namespace code [list _update_value $w $column $item]]
  }
  proc _inplaceSpinbox {w column item min max step} {
    variable curfocus
    set wnd $w.$column 
    spinbox $wnd -textvariable [namespace current]::curfocus($w,$column) -from $min -to $max -increment $step
    set curfocus($w,$column) [_get_value $w $column $item]
    bind $wnd <Destroy> [namespace code [list _update_value $w $column $item]]
  }
}

if { $argv0 eq [info script] } {
  catch {console show}
  pack [ttk::treeview .tv -columns {bool int list} -show {tree headings} -selectmode extended -yscrollcommand {.sb set}] -fill both -expand true -side left
  pack [ttk::scrollbar .sb -orient v -command {after idle ::xtreeview::updateWnds .tv;.tv yview}] -fill y -side left
  xtreeview::_treeheaders .tv true text

  .tv insert {} end -text {Sample text} -values {true 15 {Letter B}}
  set i0 [.tv insert {} end -text {Sample text} -values {false 25 {Letter C}}]
  .tv insert {} end -text {Sample text} -values {true 35 {Letter D}}
  .tv insert $i0 end -text {Sample subitem} -values {true 45 {Letter A}}
  for {set i 0} {$i<50} {incr i} {
    .tv insert $i0 end -text "Subitem $i" -values [list true $i {Letter B}]
  }
 
  bind .tv <<TreeviewInplaceEdit>> {
    if {[%W children [lindex %d 1]]==""} {
      switch [lindex %d 0] {
        {#0} { xtreeview::_inplaceEntry %W {*}%d }
        {bool} { xtreeview::_inplaceCheckbutton %W {*}%d true false}
        {int} { xtreeview::_inplaceSpinbox %W {*}%d 0 100 1 }
        {list} { xtreeview::_inplaceList %W {*}%d {"Letter A" "Letter B" "Letter C" "Letter D"} }
      }
    } elseif {[lindex %d 0]=="list"} {
      xtreeview::_inplaceEntryButton %W {*}%d {
        set %%v "tree: %W, column,item=%d"
      }
    }
  }
}