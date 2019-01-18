join (
  select ITEM as ITSPEC
  from :TITSPEC
  where :WHESPITFLR
  group by ITEM
  having count(ITEM)=:SPITFLTCO
 ) as ITSPECALL on ITINCATALL.ITINCAT=ITSPECALL.ITSPEC
