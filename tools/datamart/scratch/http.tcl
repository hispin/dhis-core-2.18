# exp oo http

package require TclOO
package require http

oo::class create Httpcon {

    variable name token
    
    constructor {pname} {
	set name $pname
    }

    method data {socket token} {
	puts "in data handler for $name"
	set d [read $socket]
	set nchars [string length $d]
	puts "data: $d nchars"
	return $nchars
    }
	
    method startfetch {url {timeout 10000} } {
	set token [http::geturl $url -command [list my finished] -handler [self method data] -timeout $timeout]
    }
    
    method dump {} {
	puts "name: $name"
	puts "token: $token"
	upvar \#0 $token state
	parray state
    }
} 

Httpcon create datacon1 "demo login" 
Httpcon create datacon2 "kenya"

datacon1 startfetch  http://www.google.com

after 1000 {
    datacon1 dump
}

vwait forever

