
-- Groups orgunits into groups based on the text match in the where clause for the orgunit group with the given id

insert into orgunitgroupmembers(orgunitgroupid,organisationunitid)
select 22755 as orgunitgroupid,ou.organisationunitid as organisationunitid from organisationunit ou 
where lower(name) like '%dispensary%'
and not exists (
select orgunitgroupid from orgunitgroupmembers om 
where ou.organisationunitid=om.organisationunitid
and om.orgunitgroupid=22755);
