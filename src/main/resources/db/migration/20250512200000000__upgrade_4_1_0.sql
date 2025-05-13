-- Table: HFJ_SPIDX_NUMBER
alter table HFJ_SPIDX_NUMBER alter column RES_ID type int8;
alter table HFJ_SPIDX_NUMBER alter column RES_ID set not null;


-- Table: HFJ_SPIDX_COORDS
alter table HFJ_SPIDX_COORDS alter column RES_ID type int8;
alter table HFJ_SPIDX_COORDS alter column RES_ID set not null;


-- Table: HFJ_SPIDX_TOKEN
alter table HFJ_SPIDX_TOKEN alter column RES_ID type int8;
alter table HFJ_SPIDX_TOKEN alter column RES_ID set not null;


-- Table: HFJ_SPIDX_STRING
alter table HFJ_SPIDX_STRING alter column RES_ID type int8;
alter table HFJ_SPIDX_STRING alter column RES_ID set not null;


-- Table: HFJ_SPIDX_DATE
alter table HFJ_SPIDX_DATE alter column RES_ID type int8;
alter table HFJ_SPIDX_DATE alter column RES_ID set not null;


-- Table: HFJ_SPIDX_QUANTITY
alter table HFJ_SPIDX_QUANTITY alter column RES_ID type int8;
alter table HFJ_SPIDX_QUANTITY alter column RES_ID set not null;


-- Table: HFJ_SPIDX_URI
alter table HFJ_SPIDX_URI alter column RES_ID type int8;
alter table HFJ_SPIDX_URI alter column RES_ID set not null;


-- Table: HFJ_SEARCH
alter table HFJ_SEARCH add column EXPIRY_OR_NULL timestamp ;
alter table HFJ_SEARCH add column NUM_BLOCKED int4 ;


-- Table: SEQ_BLKEXJOB_PID
create sequence SEQ_BLKEXJOB_PID start 1 increment 50;


-- Table: HFJ_BLK_EXPORT_JOB
CREATE TABLE HFJ_BLK_EXPORT_JOB ( PID int8  not null, JOB_ID varchar(36)  not null, JOB_STATUS varchar(10)  not null, CREATED_TIME timestamp  not null, STATUS_TIME timestamp  not null, EXP_TIME timestamp  not null, REQUEST varchar(500)  not null, OPTLOCK int4  not null, EXP_SINCE timestamp , STATUS_MESSAGE varchar(500) ,  PRIMARY KEY (PID) ) ;
create index IDX_BLKEX_EXPTIME on HFJ_BLK_EXPORT_JOB(EXP_TIME);
create unique index IDX_BLKEX_JOB_ID on HFJ_BLK_EXPORT_JOB(JOB_ID);


-- Table: SEQ_BLKEXCOL_PID
create sequence SEQ_BLKEXCOL_PID start 1 increment 50;


-- Table: HFJ_BLK_EXPORT_COLLECTION
CREATE TABLE HFJ_BLK_EXPORT_COLLECTION ( PID int8  not null, JOB_PID int8  not null, RES_TYPE varchar(40)  not null, TYPE_FILTER varchar(1000) , OPTLOCK int4  not null,  PRIMARY KEY (PID) ) ;
alter table HFJ_BLK_EXPORT_COLLECTION add constraint FK_BLKEXCOL_JOB foreign key (JOB_PID) references HFJ_BLK_EXPORT_JOB;


-- Table: SEQ_BLKEXCOLFILE_PID
create sequence SEQ_BLKEXCOLFILE_PID start 1 increment 50;


-- Table: HFJ_BLK_EXPORT_COLFILE
CREATE TABLE HFJ_BLK_EXPORT_COLFILE ( PID int8  not null, COLLECTION_PID int8  not null, RES_ID varchar(100)  not null,  PRIMARY KEY (PID) ) ;
alter table HFJ_BLK_EXPORT_COLFILE add constraint FK_BLKEXCOLFILE_COLLECT foreign key (COLLECTION_PID) references HFJ_BLK_EXPORT_COLLECTION;


-- Table: HFJ_RES_VER_PROV
CREATE TABLE HFJ_RES_VER_PROV ( RES_VER_PID int8  not null, RES_PID int8  not null, SOURCE_URI varchar(100) , REQUEST_ID varchar(16) ,  PRIMARY KEY (RES_VER_PID) ) ;
alter table HFJ_RES_VER_PROV add constraint FK_RESVERPROV_RESVER_PID foreign key (RES_VER_PID) references HFJ_RES_VER;
alter table HFJ_RES_VER_PROV add constraint FK_RESVERPROV_RES_PID foreign key (RES_PID) references HFJ_RESOURCE;
create index IDX_RESVERPROV_SOURCEURI on HFJ_RES_VER_PROV(SOURCE_URI);
create index IDX_RESVERPROV_REQUESTID on HFJ_RES_VER_PROV(REQUEST_ID);


-- Table: TRM_VALUESET_C_DESIGNATION
alter table TRM_VALUESET_C_DESIGNATION add column VALUESET_PID int8  not null;
alter table TRM_VALUESET_C_DESIGNATION add constraint FK_TRM_VSCD_VS_PID foreign key (VALUESET_PID) references TRM_VALUESET;


-- Table: HFJ_SEARCH_RESULT
alter table HFJ_SEARCH_RESULT drop constraint FK_SEARCHRES_RES;
alter table HFJ_SEARCH_RESULT drop constraint FK_SEARCHRES_SEARCH;


-- Table: TRM_VALUESET
alter table TRM_VALUESET add column TOTAL_CONCEPTS int8  not null;
alter table TRM_VALUESET add column TOTAL_CONCEPT_DESIGNATIONS int8  not null;
drop index IDX_VALUESET_EXP_STATUS;


-- Table: SEQ_SEARCHPARM_ID
drop sequence SEQ_SEARCHPARM_ID;


-- Table: TRM_VALUESET_CONCEPT
alter table TRM_VALUESET_CONCEPT add column VALUESET_ORDER int4  not null;
create unique index IDX_VS_CONCEPT_ORDER on TRM_VALUESET_CONCEPT(VALUESET_PID, VALUESET_ORDER);


-- Table: HFJ_RESOURCE
alter table HFJ_RESOURCE alter column RES_TYPE type varchar(40);
alter table HFJ_RESOURCE alter column RES_TYPE set not null;


-- Table: HFJ_RES_VER
alter table HFJ_RES_VER alter column RES_TYPE type varchar(40);


-- Table: HFJ_HISTORY_TAG
alter table HFJ_HISTORY_TAG alter column RES_TYPE type varchar(40);


-- Table: HFJ_RES_LINK
alter table HFJ_RES_LINK alter column SOURCE_RESOURCE_TYPE type varchar(40);
alter table HFJ_RES_LINK alter column TARGET_RESOURCE_TYPE type varchar(40);


-- Table: HFJ_RES_TAG
alter table HFJ_RES_TAG alter column RES_TYPE type varchar(40);


-- Table: TRM_CONCEPT_DESIG
alter table TRM_CONCEPT_DESIG alter column VAL type varchar(2000);


-- Table: TRM_VALUESET_C_DESIGNATION
alter table TRM_VALUESET_C_DESIGNATION alter column VAL type varchar(2000);


-- Table: TRM_CONCEPT_PROPERTY
alter table TRM_CONCEPT_PROPERTY add column PROP_VAL_LOB oid ;
