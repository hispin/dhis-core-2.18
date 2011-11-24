
-- Delete all data values for category combo

delete from datavalue where categoryoptioncomboid in (
select cc.categoryoptioncomboid from categoryoptioncombo cc
join categorycombos_optioncombos co
on (cc.categoryoptioncomboid=co.categoryoptioncomboid)
where categorycomboid=12414 );

-- Data elements and frequency with average agg operator (lower than yearly negative for data mart performance)

select d.dataelementid, d.name, pt.name from dataelement d 
join datasetmembers dsm on d.dataelementid=dsm.dataelementid 
join dataset ds on dsm.datasetid=ds.datasetid 
join periodtype pt on ds.periodtypeid = pt.periodtypeid 
where d.aggregationtype = 'average';

-- Data elements with aggregation levels

select d.dataelementid, d.name, dal.aggregationlevel from dataelementaggregationlevels dal 
join dataelement d on dal.dataelementid=d.dataelementid 
order by name, aggregationlevel;

