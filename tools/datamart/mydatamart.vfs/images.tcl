# set up images for buttons following dhis2 theme 

set imagedir [file join $::dhis(resource) images]

image create photo img::datamart -file $imagedir/data_mart_export.png
image create photo img::hierarchy -file $imagedir/hierarchy.png
image create photo img::config -file $imagedir/configuration.png
image create photo img::dataelement -file $imagedir/dataelement.png
image create photo img::excel -file $imagedir/excel.png
image create photo img::hierarchy -file $imagedir/hierarchy.png
image create photo img::import -file $imagedir/import.png
image create photo img::new -file $imagedir/new.png
image create photo img::load -file $imagedir/load.png
image create photo img::exit -file $imagedir/exit.png
image create photo img::success -file $imagedir/success.png
image create photo img::fail -file $imagedir/fail.png
