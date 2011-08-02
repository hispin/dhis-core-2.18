#
# Tcl package index file
#
# Note sqlite*3* init specifically
#
package ifneeded sqlite3 3.7.4 \
    [list load [file join $dir sqlite374.dll] Sqlite3]
