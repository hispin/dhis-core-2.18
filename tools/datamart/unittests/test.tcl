lappend auto_path datamart.vfs/libext
lappend auto_path datamart.vfs/libdhis

package require dhisweb

${::dhisweb::log}::setlevel debug

${::dhisweb::log}::debug "Hello from the logger"

after {1000} {
    try {
	set tok [dhisweb::fetchDataValues http://localhost:8082 2 18 monthly 20100101 20100131 10000]
    } on error err {
	puts "ouch $err"
    }
}
puts $::dhisweb::datavaluestatus
vwait ::dhisweb::datavaluestatus
#puts [getCookies $tok]
#::http::cleanup tok