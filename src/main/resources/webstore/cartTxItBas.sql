select sum(TOT) as TOTALTAX, TAX as TAXID
from CARTITTXLN where DISAB=0 and CARTID=:CARTID
group by TAX;
