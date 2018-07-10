select ITSTYPE, ALLSERVICE.ITEMID, IMAGEURL, ITSPRICE, PREVIOUSPRICE, AVAILABLEQUANTITY, ITSRATING, DETAILSMETHOD,
  case when I18NSPECIFICINLIST.ITSNAME is null then ALLSERVICE.ITSNAME else I18NSPECIFICINLIST.ITSNAME end as ITSNAME,
  case when I18NSPECIFICINLIST.SPECIFICINLIST is null then ALLSERVICE.SPECIFICINLIST else I18NSPECIFICINLIST.SPECIFICINLIST end as SPECIFICINLIST
from (
  select ITSTYPE, ITEMID, ITSNAME, IMAGEURL, SPECIFICINLIST, ITSPRICE, PREVIOUSPRICE, AVAILABLEQUANTITY, ITSRATING, DETAILSMETHOD
  from ITEMINLIST
  where AVAILABLEQUANTITY>0 and ITSTYPE=1 :WHEREADD
 ) as ALLSERVICE
join (
  select distinct ITEM as SERVICEINCATALOG from SERVICECATALOG where ITSCATALOG:CATALOGFILTER
 ) as SERVICEINCATALOGALL on SERVICEINCATALOGALL.SERVICEINCATALOG=ALLSERVICE.ITEMID
left join (
  select ITSNAME, SPECIFICINLIST, ITEMID from I18NSPECIFICINLIST  where ITSTYPE=1 and LANG=':LANG'
 ) as I18NSPECIFICINLIST on I18NSPECIFICINLIST.ITEMID=ALLSERVICE.ITEMID
