
-- Move population data from last year to this year

-- Replace first periodid with current year, replace second periodid with last year, replace dataset.name with population dataset name

delete from datavalue where periodid=43668 and dataelementid in (
select dataelementid from datasetmembers
join dataset using(datasetid)
where dataset.name='Population estimates' );

insert into datavalue(dataelementid,periodid,sourceid,categoryoptioncomboid,value,storedby,lastupdated,comment,followup)
select dataelementid,43668 as periodid,sourceid,categoryoptioncomboid,ceil(cast(value as double precision)*1.029) as value,storedby,lastupdated,comment,followup
from datavalue
where periodid=21011 and dataelementid in (
select dataelementid from datasetmembers
join dataset using(datasetid)
where dataset.name='Population estimates' );
