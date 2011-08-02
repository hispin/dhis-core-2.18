# widget for selecting orgunits
# replaces contents of parent

proc displayorgunit {parent db} {
    # remove current contents of parent
    foreach w [winfo children $parent] {
	destroy $w
    }
    #set up query
    set ou organisationunit
    set oustr _orgunitstructure
    set query "select $oustr.organisationunitid as id, shortname, name,\
$ou.organisationunitid, \
level, idlevel1,idlevel2,idlevel3,idlevel4,idlevel5,idlevel6,idlevel7 \
from $ou, $oustr where $ou.organisationunitid=id and level<5 order by level"
    
    set orgtree [ttk::treeview $parent.t \
		    -columns "Name" \
		    -selectmode browse ]

    set ous [db eval $query {
	global count
	incr count
	puts "$id $level"
	puts "$orgtree insert ou$idlevel1 -id ou$id -text '$shortname' -value '$name'"
	switch -- $level {
	    1 { $orgtree insert {} end -id ou$id -text "$shortname" -value "$name" }
	    2 { $orgtree insert ou$idlevel1 end -id ou$id -text "$shortname" -value "$name" }
	    3 { $orgtree insert ou$idlevel2 end -id  ou$id -text "$shortname" -value "$name"}
	    4 { $orgtree insert ou$idlevel3 end -id  ou$id -text "$shortname" -value "$name"}
	    5 { $orgtree insert ou$idlevel4 end -id  ou$id -text "$shortname" -value "$name"}
	    6 { $orgtree insert ou$idlevel5 end -id  ou$id -text "$shortname" -value "$name"}
	    7 { $orgtree insert ou$idlevel6 end -id  ou$id -text "$shortname" -value "$name"}
	    8 { $orgtree insert ou$idlevel7 end -id  ou$id -text "$shortname" -value "$name"}
	}
    }]
    
    pack $parent.t
}