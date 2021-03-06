package com.zongteng.ztetl.etl.dw.dim

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.DateUtil

object DimWarehousePhysical {
  //任务名称(一般同类名)
  private val task = "Dw_dim_warehouse_physical"

  //dw层类名
  private val tableName = "dim_warehouse_physical"

  //获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()


  //要执行的sql语句
  private val gc_wms = "SELECT py.row_wid as row_wid," +
    " from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid," +
    " from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt," +
    " from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt," +
    " py.datasource_num_id as datasource_num_id," +
    " py.data_flag as data_flag," +
    " py.integration_id as integration_id," +
    " py.created_on_dt  as created_on_dt," +
    " py.changed_on_dt  as changed_on_dt," +
    " cast(concat(py.datasource_num_id,py.warehouse_id) as bigint) as warehouse_key," +
    " py.wp_id AS wp_id," +
    " py.warehouse_id AS wp_warehouse_id," +
    " ws.warehouse_code AS wp_warehouse_code," +
    " py.wp_name AS wp_name," +
    " py.wp_code AS wp_code," +
    " py.wp_status as wp_status," +
    " nvl(pvl1.vl_bi_name,py.wp_status) as wp_status_val," +
    " py.wp_priority AS wp_priority," +
    " py.wp_state AS wp_state," +
    " py.wp_city AS wp_city," +
    " py.wp_postcode AS wp_postcode," +
    " py.wp_company AS wp_company," +
    " py.wp_contacter AS wp_contacter," +
    " py.wp_phone AS wp_phone," +
    " py.wp_street_address1 AS wp_street_address1," +
    " py.wp_street_address2 AS wp_street_address2," +
    " py.wp_street_number AS wp_street_number," +
    " py.wp_add_time  AS wp_add_time," +
    " py.wp_update_time  AS wp_update_time    " +
    " FROM (select * from ods.gc_wms_warehouse_physical where day= "+ nowDate +") AS py" +
    " left join (select row_wid,warehouse_code,warehouse_id from ods.gc_wms_warehouse where day= "+ nowDate +") as ws on ws.warehouse_id = py.warehouse_id" +
    " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9004' and pvl.vl_type='wp_status' and pvl.vl_datasource_table='gc_wms_warehouse_physical') as pvl1 on pvl1.vl_value=py.wp_status"


  def main(args: Array[String]): Unit = {
    val sqlArray: Array[String] = Array(gc_wms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))
    Dw_dim_common.getRunCode_full(task, tableName, sqlArray, Array(SystemCodeUtil.GC_WMS))

  }

}
