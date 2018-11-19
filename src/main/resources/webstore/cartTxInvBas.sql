select TAX as TAXID, TAX.ITSNAME as TAXNAME, INVITEMTAXCATEGORYLINE.ITSPERCENTAGE, sum(SUBT) as SUBTOTAL, sum(TOT) as ITSTOTAL
from
( select TXCAT, SUBT, TOT
  from CARTLN
  where TXCAT is not null and DISAB=0 and SELLER:CONDSEL and ITSOWNER=:CARTID
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID=TXCAT
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER=INVITEMTAXCATEGORY.ITSID
join TAX on INVITEMTAXCATEGORYLINE.TAX=TAX.ITSID
group by TAXID, TAXNAME, ITSPERCENTAGE;
