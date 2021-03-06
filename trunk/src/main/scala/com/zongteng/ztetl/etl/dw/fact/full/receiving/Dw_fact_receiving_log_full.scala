package com.zongteng.ztetl.etl.dw.fact.full.receiving

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_fact_common, Dw_par_val_list_cache}
import com.zongteng.ztetl.util.DateUtil

object Dw_fact_receiving_log_full {
  //任务名称(一般同类名)
  private val task = "Dw_fact_receiving_log_full"

  //dw层类名
  private val tableName = "fact_receiving_log"
  //获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  val gc_wms = "SELECT \n" +
    "    rcl.row_wid AS row_wid,\n" +
    "    cast(from_unixtime( unix_timestamp( current_date ( ) ), 'yyyyMMdd' ) AS string ) AS etl_proc_wid,\n" +
    "    current_timestamp ( ) AS w_insert_dt,\n" +
    "    current_timestamp ( ) AS w_update_dt,\n" +
    "    rcl.datasource_num_id AS datasource_num_id,\n" +
    "    rcl.data_flag AS data_flag,\n" +
    "    rcl.rl_id AS integration_id,\n" +
    "    rcl.created_on_dt AS created_on_dt,\n" +
    "    rcl.changed_on_dt AS changed_on_dt,\n" +
    "    case when pt.timezone_season_type == 'summer_time' then\n" +
    "        pt.timezone_summer_time_dif_val\n" +
    "    else \n" +
    "        pt.timezone_winner_time_dif_val\n" +
    "    end AS timezone,\n" +
    "    null AS exchange_rate,\n" +
    "    null AS sm_key,\n" +
    "    concat(rcl.datasource_num_id, wa.warehouse_id) as warehouse_key,\n" +
    "    null as transit_warehouse_key,\n" +
    "    concat(rcl.datasource_num_id, cu.customer_id) as customer_key,\n" +
    "    null as to_warehouse_key,\n" +
    "    concat(rcl.datasource_num_id, rcl.receiving_id) AS receiving_key,\n" +
    "    concat(rcl.datasource_num_id, us.user_id) AS user_key,\n" +
    "\n" +
    "    rcl.rl_id AS rl_id,\n" +
    "    rcl.receiving_id AS rl_receiving_id,\n" +
    "    rcl.receiving_code AS rl_receiving_code,\n" +
    "    rcl.rl_type AS rl_type,\n" +
    "    rcl.user_name AS rl_user_name,\n" +
    "    rcl.user_code AS rl_user_code,\n" +
    "    rcl.rl_status_from AS rl_status_from,\n" +
    "    rcl.rl_status_to AS rl_status_to,\n" +
    "    rcl.rl_note AS rl_note,\n" +
    "    rcl.rl_add_time AS rl_add_time,\n" +
    "    rcl.rl_ip AS rl_ip,\n" +
    "    rcl.rl_content AS rl_content,\n" +
    "    null AS rl_user_id,\n" +
    "    null AS rl_customer_code,\n" +
    "    date_format(rcl.rl_add_time, 'yyyyMM') AS month\n" +
    "FROM (SELECT * FROM ods.gc_wms_gc_receiving_log  WHERE data_flag != 'DELETE') AS rcl\n" +
    "LEFT JOIN (SELECT * FROM ods.gc_wms_gc_receiving WHERE data_flag != 'DELETE') AS rc ON rcl.receiving_id = rc.receiving_id\n" +
    s"LEFT JOIN ${Dw_dim_common.getDimSql("gc_wms_warehouse","wa")} on wa.warehouse_code = rc.warehouse_code\n" +
    s"LEFT JOIN ${Dw_dim_common.getDimSql("gc_wms_customer","cu")} on cu.customer_code = rc.customer_code\n" +
    s"LEFT JOIN ${Dw_dim_common.getDimSql("gc_wms_user","us")} ON rcl.user_code = us.user_code\n" +
    "LEFT JOIN dw.par_timezone AS pt ON rc.warehouse_code = pt.warehouse_code AND pt.timezone_year = date_format(rcl.rl_add_time, 'yyyy')"

  val zy_wms = "SELECT \n" +
    " rcl.row_wid AS row_wid,\n" +
    " cast(from_unixtime( unix_timestamp( current_date ( ) ), 'yyyyMMdd' ) AS string ) AS etl_proc_wid,\n" +
    " current_timestamp ( ) AS w_insert_dt,\n" +
    " current_timestamp ( ) AS w_update_dt,\n" +
    " rcl.datasource_num_id AS datasource_num_id,\n" +
    " rcl.data_flag AS data_flag,\n" +
    " rcl.rl_id AS integration_id,\n" +
    " rcl.created_on_dt AS created_on_dt,\n" +
    " rcl.changed_on_dt AS changed_on_dt,\n" +
    " case when pt.timezone_season_type == 'summer_time' then\n" +
    "        pt.timezone_summer_time_dif_val\n" +
    "    else \n" +
    "        pt.timezone_winner_time_dif_val\n" +
    "    end AS timezone,\n" +
    " null AS exchange_rate,\n" +
    "\n" +
    " concat(rcl.datasource_num_id, sm.sm_id) AS sm_key,\n" +
    " concat(rcl.datasource_num_id, rc.warehouse_id) AS warehouse_key,\n" +
    "    concat(rcl.datasource_num_id, rc.transit_warehouse_id) AS transit_warehouse_key,\n" +
    "    concat(rcl.datasource_num_id, cu.customer_id) AS customer_key,\n" +
    "    concat(rcl.datasource_num_id, rc.to_warehouse_id) AS to_warehouse_key,\n" +
    " concat(rcl.datasource_num_id, rcl.receiving_id) AS receiving_key,\n" +
    " concat(rcl.datasource_num_id, rcl.user_id) AS user_key,\n" +
    "   \n" +
    " rcl.rl_id AS rl_id,\n" +
    " rcl.receiving_id AS rl_receiving_id,\n" +
    " rcl.receiving_code AS rl_receiving_code,\n" +
    " rcl.rl_type AS rl_type,\n" +
    " null AS rl_user_name,\n" +
    " null AS rl_user_code,\n" +
    " rcl.rl_status_from AS rl_status_from,\n" +
    " rcl.rl_status_to AS rl_status_to,\n" +
    " rcl.rl_note AS rl_note,\n" +
    " rcl.rl_add_time AS rl_add_time,\n" +
    " rcl.rl_ip AS rl_ip,\n" +
    " rcl.rl_content AS rl_content, \n" +
    " rcl.user_id AS rl_user_id,\n" +
    " rcl.customer_code AS rl_customer_code,\n" +
    " date_format(rcl.rl_add_time, 'yyyyMM') AS month\n" +
    "FROM (SELECT * FROM ods.zy_wms_receiving_log  WHERE data_flag != 'DELETE') AS rcl\n" +
    "LEFT JOIN (SELECT * FROM ods.zy_wms_receiving WHERE data_flag != 'DELETE') AS rc ON rcl.receiving_id = rc.receiving_id\n" +
    s"LEFT JOIN ${Dw_dim_common.getDimSql("zy_wms_shipping_method","sm")} ON sm.sm_code = rc.sm_code\n" +
    s"LEFT JOIN ${Dw_dim_common.getDimSql("zy_wms_customer","cu")} ON rcl.customer_code = cu.customer_code\n" +
    s"LEFT JOIN ${Dw_dim_common.getDimSql("zy_wms_warehouse","ws")} ON ws.warehouse_id = rc.warehouse_id \n" +
    "LEFT JOIN dw.par_timezone AS pt ON ws.warehouse_code = pt.warehouse_code AND pt.timezone_year = date_format(rcl.rl_add_time, 'yyyy')"

  def main(args: Array[String]): Unit = {
    Dw_fact_common.getRunCode_hive_full_Into(task, tableName, Array(gc_wms, zy_wms), Dw_par_val_list_cache.EMPTY_PAR_VAL_LIST)

  }
}
