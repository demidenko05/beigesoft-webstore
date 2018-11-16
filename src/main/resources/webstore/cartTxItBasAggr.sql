select CLID, TXCAT as TAXCATID, TAX as TAXID, ITSPERCENTAGE, sum(TOTTX) as TOTALTAXES
from
( select ITSID as CLID, SELLER, TXCAT, TOTTX
  from CARTLN
  where TXCAT is not null and DISAB=0 and SELLER:CONDSEL and ITSOWNER=:CARTID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TXCAT
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
group by CLID, TAXCATID, TAXID, ITSPERCENTAGE;
