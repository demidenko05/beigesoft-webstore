select sum(TOT) as ITSTOTAL from CARTLN where DISAB=0 and ITSOWNER=:CARTID group by ITSOWNER;
