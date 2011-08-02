package require Tk
 package require BWidget

 proc appCreate { } {

    # Menu description
    set descmenu {
        "&File" all file 0 {
            {command "&New"  {} "New Blank Document" {} -command appNew}
            {command "&Open" {} "Open"               {} -command appOpen}
            {command "&Save" {} "Save"               {} -command appSave}
            {command "E&xit" {} "Exit Application"   {} -command appExit}
        }
        "&Edit" all edit 0 {
	    {command "Cu&t"   {} "Cut"   {Ctrl x} -command appCut}
	    {command "&Copy"  {} "Copy"  {Ctrl c} -command appCopy}
	    {command "&Paste" {} "Paste" {Ctrl v} -command appPaste}
        }
 	"&Help" all help 0 {
	    {command "&About" {} "" {} -command appHelpAbout}
	}
    }

    #  Create main frame
    set mainframe [MainFrame .mainframe -menu $descmenu]

    # toolbar 1 creation
    set tb1  [$mainframe addtoolbar]
    set bbox [ButtonBox $tb1.bbox1 -spacing 0 -padx 1 -pady 1]
    $bbox add -image [Bitmap::get new] -command appNew \
        -highlightthickness 0 -takefocus 0 -relief link \
	-borderwidth 1 -padx 1 -pady 1 \
        -helptext "Create Blank Document"
    $bbox add -image [Bitmap::get open] -command appOpen \
        -highlightthickness 0 -takefocus 0 -relief link \
	-borderwidth 1 -padx 1 -pady 1 \
        -helptext "Open an existing file"
    $bbox add -image [Bitmap::get save] -command appSave \
        -highlightthickness 0 -takefocus 0 -relief link \
	-borderwidth 1 -padx 1 -pady 1 \
        -helptext "Save file"
    pack $bbox -side left -anchor w

    set sep [Separator $tb1.sep -orient vertical]
    pack $sep -side left -fill y -padx 4 -anchor w

    set bbox [ButtonBox $tb1.bbox2 -spacing 0 -padx 1 -pady 1]
    $bbox add -image [Bitmap::get cut] -command appCut \
        -highlightthickness 0 -takefocus 0 -relief link \
	-borderwidth 1 -padx 1 -pady 1 \
        -helptext "Cut selection"
    $bbox add -image [Bitmap::get copy] -command appCopy \
        -highlightthickness 0 -takefocus 0 -relief link \
	-borderwidth 1 -padx 1 -pady 1 \
        -helptext "Copy selection"
    $bbox add -image [Bitmap::get paste] -command appPaste \
        -highlightthickness 0 -takefocus 0 -relief link \
	-borderwidth 1 -padx 1 -pady 1 \
        -helptext "Paste selection"
    pack $bbox -side left -anchor w

    wm protocol . WM_DELETE_WINDOW { appExit }

    pack $mainframe -fill both -expand yes
    update idletasks
 }

 proc appNew {} {
 }
 proc appOpen {} {
    tk_getOpenFile
 }
 proc appSave {} {
 }
 proc appExit {} {
    exit
 }
 proc appCut {} {
     event generate [focus] <<Cut>>
 }
 proc appCopy {} {
    event generate [focus] <<Copy>>
 }
 proc appPaste {} {
    event generate [focus] <<Paste>>
 }
 proc appHelpAbout {} {
    tk_messageBox -message "Application Template"
 }

 proc main {} {
    wm withdraw .
    appCreate
    wm deiconify .
 }

 main