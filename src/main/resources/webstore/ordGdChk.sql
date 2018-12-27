select ITSID, GOOD, case when AVQUAN is null or QUANT>AVQUAN then 0 else QUANT end as QUANT
from CUSTORDERGDLN
left join (select ITEM, ITSQUANTITY as AVQUAN from GOODSPLACE where PICKUPPLACE=:PLACE) as ITPL on ITPL.ITEM=GOOD
where ITSOWNER in (:ORIDS);
