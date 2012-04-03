
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

-- Facility overview --

select ou.name, ou.uid, ou.code, ou.coordinates, oustr.level, gsstr.type, gsstr.ownership, 
  (select name from organisationunit where organisationunitid=oustr.idlevel2) as province,
  (select name from organisationunit where organisationunitid=oustr.idlevel3) as county,
  (select name from organisationunit where organisationunitid=oustr.idlevel4) as district
from _orgunitstructure as oustr
join organisationunit as ou on oustr.organisationunitid=ou.organisationunitid
join _organisationunitgroupsetstructure as gsstr on ou.organisationunitid=gsstr.organisationunitid
where oustr.level >= 5;

