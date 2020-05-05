/*==============================================================*/
/* Database name:  PhysicalDataModel_2                          */
/* DBMS name:      ORACLE Version 9i                            */
/* Created on:     2003-05-02 오후 3:26:46                        */
/*==============================================================*/

drop table TST_AGREEMENT cascade constraints;
drop table TST_PROJECT_NO cascade constraints;

/*==============================================================*/
/* Table: TST_AGREEMENT                                         */
/*==============================================================*/

-- 예산 승인 (디테일)
-- 총액은 해당 연도의 다섯 개 양의 총액
create table TST_AGREEMENT  (
   project_year         VARCHAR2(4)                      not null,
   project_no           VARCHAR2(5)                      not null, -- 프로젝트 번호 테이블
   subproject_no        VARCHAR2(3)                      not null, -- 서브 프로젝트 (과제번호) 테이블
   detail_no1           VARCHAR2(2)                      not null,
   detail_no2           VARCHAR2(2)                      not null,
   first_amount         NUMBER,
   second_amount        NUMBER,
   third_amount         NUMBER,
   fourth_amount        NUMBER,
   replace_amount       NUMBER,
   constraint TST_AGREEMENT_PK primary key (project_year, project_no, subproject_no)
);


/*==============================================================*/
/* Table: TST_PROJECT_NO                                        */
/*==============================================================*/

-- 검색 조건 : (사업번호 OR 사업명 OR 사업책임자) AND (년도 - 필수)
/* 사업 번호 (정보) */
create table TST_PROJECT_NO  (
   project_no           VARCHAR2(5)                      not null,
   project_no_name      VARCHAR2(50),
   agree_date           VARCHAR2(10),
   agree_no             VARCHAR2(10),
   project_owner        VARCHAR2(6),
   project_period_from  DATE,
   project_period_to    DATE,
   constraint TST_PROJECT_NO_PK primary key (project_no)
);

