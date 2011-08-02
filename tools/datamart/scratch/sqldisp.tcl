lappend auto_path datamart.vfs/libext .

package require treectrl
package require sqlite3

set offset 0

set container [ttk::frame .c]
set z1 [ttk::treeview .c.t1 -columns {0 1 2 3} -show headings -yscrollcommand {.c.y1 set}
]
$z1 tag configure 0 -background lightblue
scrollbar .c.y1 -ori vert -command ".c.t1 yview"
foreach col {0 1 2 3} name {OrgUnit Gender Period Value} {
        $z1 heading $col -text $name
    }


set z2 [treectrl .c.t2 -yscrollcommand {.c.y2 set}]
scrollbar .c.y2 -ori vert -command ".c.t2 yview"
foreach name {OrgUnit Gender Period Value} {
        $z2 column create -text $name
    }


set go [ttk::button .b -text "Next" -command {
    dbupdate $::offset
    incr ::offset 500
}]


sqlite3 db ../../dhislib/db/datamart.sdb

pack $z1 -side left
pack .c.y1 -side left -fill y
pack $z2 -side left
pack .c.y2 -side left -fill y

pack .c
pack $go


set colour 0

proc dbupdate {offset} {
    $::z1 delete [$::z1 children {}]
    db eval "select * from pivotsource_routinedata_ou3_all limit $offset,100" {
	set id [$::z1 insert {} 0 -values [list $orgunit3 $gender $period $value] -tag $::colour]
	set ::colour [expr ($::colour+1) % 2 ]
    }
}

proc dbupdate2 {offset} {
    #$::z2 delete [$::z2 children {}]
    db eval "select * from pivotsource_routinedata_ou3_all limit $offset,1000" {
	set item [$::z2 item create]
    }
}