lappend auto_path datamart.vfs/libext
lappend auto_path datamart.vfs/libdhis

#package require dhisweb
package require logger
set log [logger::init main]
${log}::setlevel debug
proc logdebug2window {txt} {
    .w.t insert end "debug: $txt\n"
}
proc lognotice2window {txt} {
    .w.t insert end "notice: $txt\n"
}
${::log}::logproc debug logdebug2window
${::log}::logproc notice lognotice2window

# do x-www-urlencoded character mapping
# The spec says: "non-alphanumeric characters are replaced by '%HH'"
# 1 leave alphanumerics characters alone
# 2 Convert every other character to an array lookup
# 3 Escape constructs that are "special" to the tcl parser
# 4 "subst" the result, doing all the array substitutions

 proc httpMapReply {string} {
    global httpFormMap
    set alphanumeric	a-zA-Z0-9
    if {![info exists httpFormMap]} {

	for {set i 1} {$i <= 256} {incr i} {
	    set c [format %c $i]
	    if {![string match \[$alphanumeric\] $c]} {
		set httpFormMap($c) %[format %.2x $i]
	    }
	}
	# These are handled specially
	array set httpFormMap {
	    " " +   \n %0d%0a
	}
    }
    regsub -all \[^$alphanumeric\] $string {$httpFormMap(&)} string
    regsub -all \n $string {\\n} string
    regsub -all \t $string {\\t} string
    regsub -all {[][{})\\]\)} $string {\\&} string
    return [subst $string]
}

# Call http_formatQuery with an even number of arguments, where the first is
# a name, the second is a value, the third is another name, and so on.

proc http_formatQuery {args} {
    set result ""
    set sep ""
    foreach i $args {
	append result  $sep [httpMapReply $i]
	if {$sep != "="} {
	    set sep =
	} else {
	    set sep &
	}
    }
    ${::log}::debug "Query: $result"
    return $result
}


proc makerequest {url params} {
    if {! [regexp -nocase {^(http[s]?://)?([^/:]+)(:([0-9]+))?(/.*)?$} $url \
	       x proto host y port srvurl]} {
	error "Unsupported URL: $url"
    }
    if {[string length $port] == 0} {
	set port 80
    }
    if {[string length $srvurl] == 0} {
	set srvurl /
    }
    if {[string length $proto] == 0} {
	set url http://$url
    }

    set s [socket $host $port]

    ${::log}::debug "url: $url proto: $proto host: $host port: $port srvurl $srvurl"
    
    # Send data in cr-lf format, but accept any line terminators

    fconfigure $s -translation {auto crlf} ;#-buffersize $state(-blocksize)

    # The following is disallowed in safe interpreters, but the socket
    # is already in non-blocking mode in that case.

    catch {fconfigure $s -blocking off}
    set len 0
    set how GET
    set len [string length $params]
    if {$len > 0} {
	set how POST
    }
    puts $s "$how $srvurl HTTP/1.0"
    puts $s "Accept: */*"
    puts $s "Host: $host"
    puts $s "User-Agent: Bob"
    
    set headers [list] 
    
    foreach {key value} $headers {
	regsub -all \[\n\r\]  $value {} value
	set key [string trim $key]
	if {[string length $key]} {
	    puts $s "$key: $value"
	}
    }
    if {$len > 0} {
	puts $s "Content-Length: $len"
	puts $s "Content-Type: application/x-www-form-urlencoded"
	puts $s ""
	fconfigure $s -translation {auto binary}
	puts -nonewline $s $params
    } else {
	puts $s ""
    }
    flush $s
    fileevent $s readable [list httpEvent $s]
} 

proc httpEvent {socket} {
    ${::log}::debug "activity on $socket"
    set data [read $socket]
    ${::log}::debug $data
    close $socket
}
    

ttk::frame .w
ttk::label .w.l1 -text "Username"
ttk::label .w.l2 -text "Password"
ttk::label .w.l3 -text "URL"
ttk::entry .w.e1
ttk::entry .w.e2 -show *
ttk::entry .w.e3 -width 30

ttk::label .w.l4 -textvariable ::loginstatus 

ttk::button .w.b1 -text "Login" -command {
    # dhisweb::login [.w.e3 get] [.w.e1 get] [.w.e2 get] 5000
    try {
	set params [http_formatQuery j_username [.w.e1 get] j_password [.w.e2 get] ]
	set fullURL [.w.e3 get]/dhis-web-commons-security/login.action
	makerequest $fullURL $params
    } on error err {
	${::log}::notice $err
    } finally {}
}

text .w.t -padx 20 -pady 20 -yscrollcommand ".w.s set"
ttk::scrollbar .w.s -orient vertical -command ".w.t yview"


grid .w.l1 .w.e1 -sticky w
grid .w.l2 .w.e2 -sticky w
grid .w.l3 .w.e3 -sticky w
grid .w.b1 .w.l4 -sticky w
grid .w.t -column 3 -row 0 -rowspan 10
grid .w.s -column 4 -row 0 -rowspan 10 -sticky ns

foreach slave [grid slaves .w] {
    grid configure $slave -padx 5 -pady 5
}

.w.e3 insert end "http://hiskenya.org"

pack .w

${log}::debug "Entering event loop"


