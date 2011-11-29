namespace import -force msgcat::mcset

set lang fr
set stringsFile [open [file join $starkit::topdir msgs/strings.fr]]

set strings [read $stringsFile]

foreach {en fr} $strings {
    mcset $lang $en $fr
}
close $stringsFile
