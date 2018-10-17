alter table GOODSINLISTLUV add column SESERVICESPECIFICLUV bigint default null;
alter table GOODSINLISTLUV add column SESERVICEPRICELUV bigint default null;
alter table GOODSINLISTLUV add column SESERVICEPLACELUV bigint default null;
alter table GOODSINLISTLUV add column SESERVICERATINGLUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODSPECIFICLUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODPRICELUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODPLACELUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODRATINGLUV bigint default null;
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting OIO DB version 8';
