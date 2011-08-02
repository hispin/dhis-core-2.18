
lappend auto_path datamart.vfs/libext
lappend auto_path datamart.vfs/libdhis

package require dhisweb
package require dhisdb
package require vfs::zip

source datamart.vfs/transform.tcl

set mntfile [vfs::zip::Mount Export_meta.zip Export_meta.zip]
file copy -force Export_meta.zip/Export.xml ./Export.xml
vfs::zip::Unmount $mntfile Export_meta.zip
set xsltdir datamart.vfs/xslt
file copy -force $xsltdir/dxf2sql.xsl dxf2sql.xsl  

proc readHandler {datard userdata} {
    fconfigure $datard -blocking 1
    set ::data [read $datard]
    close $datard
}

set data ""

puts "starting"
transform::transform dxf2sql.xsl Export.xml "test" ::readHandler
puts "running"
vwait ::data
    
puts  [expr [string length $::data]/1000]
