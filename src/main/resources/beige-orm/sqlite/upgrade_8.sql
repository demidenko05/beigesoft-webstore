alter table GOODSINLISTLUV add column SESERVICESPECIFICLUV integer default null;
alter table GOODSINLISTLUV add column SESERVICEPRICELUV integer default null;
alter table GOODSINLISTLUV add column SESERVICEPLACELUV integer default null;
alter table GOODSINLISTLUV add column SESERVICERATINGLUV integer default null;
alter table GOODSINLISTLUV add column SEGOODSPECIFICLUV integer default null;
alter table GOODSINLISTLUV add column SEGOODPRICELUV integer default null;
alter table GOODSINLISTLUV add column SEGOODPLACELUV integer default null;
alter table GOODSINLISTLUV add column SEGOODRATINGLUV integer default null;
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting OIO DB version 8';
