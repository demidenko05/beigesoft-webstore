select CLID, TXCAT as TAXCATID, TAX as TAXID, TAX.ITSNAME as TAXNAME, INVITEMTAXCATEGORYLINE.ITSPERCENTAGE as ITSPERCENTAGE, sum(TOTTX) as TOTALTAXES
from
( select ITSID as CLID, TXCAT, TOTTX
  from CARTLN
  where TXCAT is not null and DISAB=0 and SEL:CONDSEL and ITSOWNER=:CARTID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TXCAT
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
join TAX on INVITEMTAXCATEGORYLINE.TAX=TAX.ITSID
group by CLID, TAXCATID, TAXID, TAXNAME, INVITEMTAXCATEGORYLINE.ITSPERCENTAGE;
