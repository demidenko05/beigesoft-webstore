select ITSID, SERVICE, case when AVQUAN is null or QUANT>AVQUAN then 0 else QUANT end as QUANT
from CUSTORDERSRVLN
left join (select ITEM, ITSQUANTITY as AVQUAN from SERVICEPLACE where PICKUPPLACE=:PLACE) as ITPL on ITPL.ITEM=SERVICE
where DT1 is not null and ITSOWNER in (:ORIDS);
