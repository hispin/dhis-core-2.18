INSERT INTO optionset ( optionsetid, name, lastupdated, created, version )
select nextval('hibernate_sequence'), name || '_' || uid, now(), now(), 1 from trackedentityattribute where valuetype='combo';
 

INSERT INTO optionsetmembers ( optionsetid, optionvalue, sort_order )
select opt.optionsetid, teo.name,teo.trackedentityattributeoptionid from trackedentityattribute tea inner join trackedentityattributeoption teo 
on tea.trackedentityattributeid=teo.trackedentityattributeid
inner join optionset opt on opt.name=tea.name || '_' || tea.uid;