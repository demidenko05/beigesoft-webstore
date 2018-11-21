select case when I18NITEM.ITSNAME is null then ITEM.ITSNAME else I18NITEM.ITSNAME end as ITEMITSNAME,
TAXCAT.ITSID as TAXCATEGORYITSID, TAXCAT.ITSNAME as TAXCATEGORYITSNAME, TAXCAT.AGGRONLYPERCENT as TAXCATEGORYAGGRONLYPERCENT,
ITEM.ITSID as ITEMITSID, PRICECATEGORY.ITSID as PRICECATEGORYITSID, ITEMPRICE.ITSPRICE as ITSPRICE, ITEMPRICE.UNSTEP as UNSTEP
from :TITEMPRICE as ITEMPRICE 
join :TITEM as ITEM on ITEMPRICE.ITEM=ITEM.ITSID
join PRICECATEGORY as PRICECATEGORY on ITEMPRICE.PRICECATEGORY=PRICECATEGORY.ITSID
left join INVITEMTAXCATEGORY as TAXCAT on TAXCAT.ITSID=ITEM.TAXCATEGORY
left join (select ITSNAME, HASNAME from :TI18NITEM where HASNAME=:ITEMID and LANG=':LANG') as I18NITEM on I18NITEM.HASNAME=ITEM.ITSID
where PRICECATEGORY.ITSID:PRCATIDCOND and ITEM=:ITEMID;
