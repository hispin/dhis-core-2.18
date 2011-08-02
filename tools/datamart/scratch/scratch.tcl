lappend auto_path datamart.vfs/libext
lappend auto_path datamart.vfs/libdhis

package require sqlite3

try { 
    sqlite3 db "db.sdb" 
} on error err {
    puts "error: $err"
} finally {}

try {
    db eval "
create table names (name varchar(10),age int);
insert into names values('Bob',48);
insert into names values('Ken',50);
" res {parray res}
    #puts "result: $res"
} on error err {
    puts "error: $err"
} finally {
    puts "changes: [db changes]"
}

proc callback {name age} {
    puts "name=$name; age=$age"
}
	       
try {
    set result [db eval "select * from names"]
    puts "result: $result"
} on error err {
    puts "error: $err"
} finally {
    puts "changes: [db changes]"
}

