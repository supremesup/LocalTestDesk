package com.zongteng.ztetl.etl.dw.fact.full.picking

import com.zongteng.ztetl.common.Dw_fact_common

/**
  * 波次规则日志表
  */
object Dw_fact_wellen_log_full {

  // 任务名称(一般同类名)
  private val task = "Dw_fact_wellen_log_full"

  //dw层类名
  private val tableName = "fact_wellen_log"

  private val odsTables = Array(
    "gc_owms_au_wellen_log",
    "gc_owms_cz_wellen_log",
    "gc_owms_de_wellen_log",
    "gc_owms_es_wellen_log",
    "gc_owms_it_wellen_log",
    "gc_owms_jp_wellen_log",
    "gc_owms_uk_wellen_log",
    "gc_owms_frvi_wellen_log",
    "gc_owms_ukob_wellen_log",
    "gc_owms_usnb_wellen_log",
    "gc_owms_usot_wellen_log",
    "gc_owms_ussc_wellen_log",
    "gc_owms_usea_wellen_log",
    "gc_owms_uswe_wellen_log"
  )

  private val sql="SELECT wl.row_wid as row_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,wl.datasource_num_id as datasource_num_id" +
    " ,wl.data_flag as data_flag" +
    " ,wl.integration_id as integration_id" +
    " ,wl.created_on_dt as created_on_dt" +
    " ,wl.changed_on_dt as changed_on_dt" +
    " ,null as timezone" +
    " ,null as exchange_rate" +
    " ,wl.wellen_log_id as wellen_log_id" +
    " ,wl.wellen_id as wellen_id" +
    " ,wl.wellen_note as wellen_note" +
    " ,wl.user_id as wellen_user_id" +
    " ,wl.wellen_old as wellen_old" +
    " ,wl.wellen_new as wellen_new" +
    " ,wl.create_time as wellen_create_time" +
    " FROM (select * from ods.wellenLog where data_flag<>'delete') as wl"

  def makeSelectSql() = {
    odsTables.map(sql.replace("wellenLog", _))
  }

  def main(args: Array[String]): Unit = {
    val selectSql: Array[String] = makeSelectSql()
    Dw_fact_common.getRunCode_hive_nopartition_full_Into(task, tableName, selectSql)
  }

}
