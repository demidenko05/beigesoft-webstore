select ITSTYPE, ALLSEGOOD.ITEMID, IMAGEURL, ITSPRICE, PREVIOUSPRICE, AVAILABLEQUANTITY, ITSRATING, DETAILSMETHOD,
  case when I18NSPECIFICINLIST.ITSNAME is null then ALLSEGOOD.ITSNAME else I18NSPECIFICINLIST.ITSNAME end as ITSNAME,
  case when I18NSPECIFICINLIST.SPECIFICINLIST is null then ALLSEGOOD.SPECIFICINLIST else I18NSPECIFICINLIST.SPECIFICINLIST end as SPECIFICINLIST
from (
  select ITSTYPE, ITEMID, ITSNAME, IMAGEURL, SPECIFICINLIST, ITSPRICE, PREVIOUSPRICE, AVAILABLEQUANTITY, ITSRATING, DETAILSMETHOD
  from ITEMINLIST
  where AVAILABLEQUANTITY>0 and ITSTYPE=2 :WHEREADD
 ) as ALLSEGOOD
join (
  select distinct ITEM as SEGOODINCATALOG from SEGOODCATALOG where ITSCATALOG:CATALOGFILTER
 ) as SEGOODINCATALOGALL on SEGOODINCATALOGALL.SEGOODINCATALOG=ALLSEGOOD.ITEMID
left join (
  select ITSNAME, SPECIFICINLIST, ITEMID from I18NSPECIFICINLIST  where ITSTYPE=2 and LANG=':LANG'
 ) as I18NSPECIFICINLIST on I18NSPECIFICINLIST.ITEMID=ALLSEGOOD.ITEMID
