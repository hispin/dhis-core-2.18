
-- Delete all data values for category combo

delete from datavalue where categoryoptioncomboid in (
select cc.categoryoptioncomboid from categoryoptioncombo cc
join categorycombos_optioncombos co
on (cc.categoryoptioncomboid=co.categoryoptioncomboid)
where categorycomboid=12414 );

-- Data elements and frequency with average agg operator (higher than yearly negative for data mart performance)

select d.dataelementid, d.name, pt.name from dataelement d 
join datasetmembers dsm on d.dataelementid=dsm.dataelementid 
join dataset ds on dsm.datasetid=ds.datasetid 
join periodtype pt on ds.periodtypeid = pt.periodtypeid 
where d.aggregationtype = 'average';

-- Data elements with aggregation levels

select d.dataelementid, d.name, dal.aggregationlevel from dataelementaggregationlevels dal 
join dataelement d on dal.dataelementid=d.dataelementid 
order by name, aggregationlevel;

-- Data elements with less than 100 data values

select de.dataelementid, de.name, (select count(*) from datavalue dv where de.dataelementid=dv.dataelementid) as count 
from dataelement de
where (select count(*) from datavalue dv where de.dataelementid=dv.dataelementid) < 100
order by count;

-- Number of data elements with less than 100 data values

select count(*) from dataelement de
where (select count(*) from datavalue dv where de.dataelementid=dv.dataelementid) < 100;

-- Duplicate codes

select code, count(code) as count
from dataelement
group by code
order by count desc;

-- Exploded category option combo view

select cc.categorycomboid, cc.name as categorycomboname, cn.* from _categoryoptioncomboname cn
join categorycombos_optioncombos co using(categoryoptioncomboid)
join categorycombo cc using(categorycomboid)
order by categorycomboname, categoryoptioncomboname;

-- Groups orgunits into groups based on the text match in the where clause for the orgunit group with the given id

insert into orgunitgroupmembers(orgunitgroupid,organisationunitid)
select 22755 as orgunitgroupid,ou.organisationunitid as organisationunitid from organisationunit ou 
where lower(name) like '%dispensary%'
and not exists (
select orgunitgroupid from orgunitgroupmembers om 
where ou.organisationunitid=om.organisationunitid
and om.orgunitgroupid=22755);

-- Facility overview

select distinct ous.idlevel5 as internalid, ou.uid, ou.code, ou.name, ougs.type, ougs.ownership,
ou2.name as province, ou3.name as county, ou4.name as district, ou.coordinates as longitide_latitude
from _orgunitstructure ous
left join organisationunit ou on ous.organisationunitid=ou.organisationunitid
left join organisationunit ou2 on ous.idlevel2=ou2.organisationunitid
left join organisationunit ou3 on ous.idlevel3=ou3.organisationunitid
left join organisationunit ou4 on ous.idlevel4=ou4.organisationunitid
left join _organisationunitgroupsetstructure ougs on ous.organisationunitid=ougs.organisationunitid
where ous.level=5
order by province, county, district, ou.name;

-- Compare user roles

select authority from userroleauthorities where userroleid=33706 and authority not in (select authority from userroleauthorities where userroleid=21504);
