select sum(ITSTOTAL) as ITSTOTAL, sum(ITSQUANTITY) as TOTALITEMS from CARTITEM where ISDISABLED=0 and ITSOWNER=:CARTID group by ITSOWNER;
