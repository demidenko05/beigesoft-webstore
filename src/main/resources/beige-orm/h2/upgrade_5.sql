insert into DEBTORCREDITORCATEGORY(ITSNAME, ITSID, ITSVERSION) values ('Online buyers', 7771777, 1462867931627);
insert into TRADINGSETTINGS (ITSID, ITSVERSION, DEFAULTCUSTOMERCATEGORY, REMEMBERUNAUTHORIZEDUSERFOR, ITEMSPERPAGE, MAXQUANTITYOFTOPLEVELCATALOGS, MAXQUANTITYOFBULKITEMS, COLUMNSCOUNT) values (1, 1462867931627, 7771777, 0, 50, 5, 50, 2);
insert into SETTINGSADD (ITSID, ITSVERSION, RECORDSPERTRANSACTION) values (1, 1462867931627, 100);
alter table DEBTORCREDITOR add column REGEMAIL varchar(25);
alter table DEBTORCREDITOR add column REGADDRESS1 varchar(45);
alter table DEBTORCREDITOR add column REGADDRESS2 varchar(45);
alter table DEBTORCREDITOR add column REGZIP varchar(10);
alter table DEBTORCREDITOR add column REGCOUNTRY varchar(25);
alter table DEBTORCREDITOR add column REGSTATE varchar(25);
alter table DEBTORCREDITOR add column REGCITY varchar(25);
alter table DEBTORCREDITOR add column REGPHONE varchar(15);
alter table DEBTORCREDITOR add column TAXIDENTIFICATIONNUMBER varchar(15);
alter table ACCSETTINGS add column REGEMAIL varchar(25);
alter table ACCSETTINGS add column REGADDRESS1 varchar(45);
alter table ACCSETTINGS add column REGADDRESS2 varchar(45);
alter table ACCSETTINGS add column REGZIP varchar(10);
alter table ACCSETTINGS add column REGCOUNTRY varchar(25);
alter table ACCSETTINGS add column REGSTATE varchar(25);
alter table ACCSETTINGS add column REGCITY varchar(25);
alter table ACCSETTINGS add column REGPHONE varchar(15);
alter table ACCSETTINGS add column TAXIDENTIFICATIONNUMBER varchar(15);
update DATABASEINFO set DATABASEVERSION=5, DESCRIPTION='Beige Accounting DB version 5';
