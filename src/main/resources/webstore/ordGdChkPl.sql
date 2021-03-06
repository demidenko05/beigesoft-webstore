select :TORLN.ITSID as ITSID, :TORLN.ITSNAME as ITSNAME, GOOD, ITSOWNER, PRICE, TOT, TOTTX, UOM.ITSNAME as UOMITSNAME, UOM.ITSID as UOMITSID,
case when AVQUAN is null or QUANT>AVQUAN then 0 else QUANT end as QUANT
from :TORLN
left join UNITOFMEASURE as UOM on :TORLN.UOM=UOM.ITSID
left join (select ITEM, ITSQUANTITY as AVQUAN from :TITPL where PICKUPPLACE=:PLACE) as ITPL on ITPL.ITEM=GOOD
where ITSOWNER in (:ORIDS);
