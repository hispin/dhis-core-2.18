namespace eval transform {

    variable result
    
    # Wrapper around exec to xsltproc
    # @proc transform
    # @param stylesheet filename of xslt
    # @param userdata something which will available again in callbacks
    # @param data filename of doc to be transformed
    # @param datahandler callback (optional)
    # @param msghandler callback (optional)
    proc transform {stylesheet data {userdata ""} {datahandler transform::datah} {msghandler transform::msgh} } {
	try {
	    # create two pipes - one for messages and one for data
	    lassign [chan pipe] msgrd msgwr
	    lassign [chan pipe] datard datawr
	    
	    puts "spawned xsltproc in background"
	    exec -ignorestderr xsltproc $stylesheet $data 2>@$msgwr >@$datawr &
	    
	} on ok res {
	    #close write end of pipes in parent process
	    close $msgwr
	    close $datawr
	    
	    # connect handlers to stdin and stderr
	    fconfigure $datard -buffering full -blocking 0
	    fileevent $datard readable [list $datahandler $datard $userdata]
	    
	    fconfigure $msgrd -buffering full -blocking 0
	    fileevent $msgrd readable [list $msghandler $msgrd $userdata] 
	} on error err {
	    close $msgwr
	    close $datawr
	    close $msgrd
	    close $datard
	    error $err
	} finally {}
    } 		    

    # callbacks

    proc datah {datard userdata} {
	if [eof $datard] { 
	    close $datard 
	    finished $datard $userdata
	} else {
	    puts "$userdata: [gets $datard]]"
	}
    }

    proc msgh {msgrd userdata} {
	if [eof $msgrd] { 
	    close $msgrd 
	} else {
	    puts stderr "$userdata msg: [gets $msgrd]"
	}
    }

    proc finished {datard userdata} {
	puts "$userdata transform done"
    }

 proc transform2db {dbfilename metafile } {
	try {
	    # copy files into tmp directory
	    set tmpdir [file normalize [file dirname $dbfilename]/tmp]
	    file delete -force $tmpdir
	    try {
		set mnt_file [vfs::zip::Mount $metafile metafile]
		if {![file exists metafile/Export.xml]} {
		    error "Metadata file is missing Export.xml!"
		}
		file mkdir $tmpdir
		file copy metafile/Export.xml $tmpdir/Export.xml
		file copy $::xsltdir/dxf2sql.xsl $tmpdir/dxf2sql.xsl	
	    } on error err {
		error $err
	    } finally {
		if {[file exists  metafile]} {
		    vfs::zip::Unmount $mnt_file metafile
		}
	    }
	    
	    # run transform
	    set ::dhis(status) "starting transform"
	    transform::transform $tmpdir/Export.xml $tmpdir/dxf2sql.xsl \
		::dhisdb::insertdata $db 
	} on error err {
	    return [list DB_ERR $err]
	} finally { }
	
	return DB_OK
    }
}

