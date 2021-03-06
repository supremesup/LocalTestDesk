package com.zongteng.ztetl.etl.dw.dim

import com.zongteng.ztetl.common.Dw_dim_common
import com.zongteng.ztetl.util.DateUtil

object DimTcmsZone {
  //任务名称(一般同类名)
  private val task = "DimTcmsZone"

  //dw层类名
  private val tableName = "dim_tcms_zone"

  //获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  //要执行的sql语句
  private val gc_tcms="SELECT zd.row_wid as row_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,zd.datasource_num_id as datasource_num_id" +
    " ,zd.data_flag as data_flag" +
    " ,zd.integration_id as integration_id" +
    " ,zd.created_on_dt as created_on_dt" +
    " ,zd.changed_on_dt as changed_on_dt" +
    " ,cast(concat(zd.datasource_num_id,zd.zs_id) as bigint) as zs_key" +
    " ,zd.zs_id as zone_zs_id" +
    " ,zd.zd_id as zone_id" +
    " ,zd.zd_sort as zone_sort" +
    " ,zd.zd_code as zone_code" +
    " ,zd.zn_name as zone_zn_name" +
    " ,zd.zn_ename as zone_zn_ename" +
    " ,zd.zn_note as zone_zn_note" +
    " ,zd.last_update_time as zone_last_update_time" +
    " FROM (select * from ods.gc_tcms_xtd_zone_division where day="+ nowDate +" ) as zd"

  def main(args: Array[String]): Unit = {
    Dw_dim_common.getRunCode_full(task, tableName, Array(gc_tcms))
  }
}
