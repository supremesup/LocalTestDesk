package com.zongteng.ztetl.etl.dw.fact.full.receiving

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_fact_common, Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.DateUtil

object Dw_fact_receiving_box_full {

  // 任务名称(一般同类名)
  private val task = "Dw_fact_receiving_box_full"

  // dw层类名
  private val tableName = "fact_receiving_box"

  // 获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  private val gc_wms = "SELECT \n" +
    "   rrb.row_wid AS row_wid,\n" +
    "   cast(from_unixtime( unix_timestamp( current_date ( ) ), 'yyyyMMdd' ) AS string ) AS etl_proc_wid,\n" +
    "   current_timestamp ( ) AS w_insert_dt,\n" +
    "   current_timestamp ( ) AS w_update_dt,\n" +
    "   rrb.datasource_num_id AS datasource_num_id,\n" +
    "   rrb.data_flag AS data_flag,\n" +
    "   rrb.rb_id AS integration_id,\n" +
    "   rrb.created_on_dt AS created_on_dt,\n" +
    "   rrb.changed_on_dt AS changed_on_dt,\n" +
    "   0 AS  timezone,\n" +
    "   0.000 AS exchange_rate,\n" +
    " \n" +
    "   concat(rrb.datasource_num_id, rrb.receiving_id) AS receiving_key,\n" +
    "   null AS sm_key,\n" +
    "   concat(rrb.datasource_num_id, wa.warehouse_id) AS warehouse_key,\n" +
    "   null AS transit_warehouse_key,\n" +
    "   concat(rrb.datasource_num_id, cu.customer_id) AS customer_key,\n" +
    "   null AS to_warehouse_key,\n" +
    " \n" +
    "   rrb.rb_id AS rb_id,\n" +
    "   wa.warehouse_id AS rb_warehouse_id,\n" +
    "   wa.warehouse_code AS rb_warehouse_code,\n" +
    "   rrb.receiving_id  AS rb_receiving_id,\n" +
    "   rrb.receiving_code AS rb_receiving_code,\n" +
    "   rrb.box_no AS rb_box_no,\n" +
    "   rrb.box_number AS rb_box_number,\n" +
    "   null AS rb_transfer_status,\n" +
    "   null AS rb_transfer_status_val,\n" +
    "   null AS rb_receiving_user_id,\n" +
    "   null AS rb_check_in_user_id,\n" +
    "   null AS rb_check_out_user_id,\n" +
    "   rc.receiving_add_time AS rb_received_time,\n" +
    "   null AS rb_storage_time, \n" +
    "   null AS rb_delivery_time,\n" +
    "   rrb.rb_update_time AS rb_update_time,\n" +
    "   null AS rb_box_length,\n" +
    "   null AS rb_box_width,\n" +
    "   null AS rb_box_height,\n" +
    "   null AS rb_box_weight,\n" +
    "   null AS rb_box_status,\n" +
    "   rrb.reference_box_no AS rb_reference_box_no,\n" +
    "   rrb.sign_status AS rb_sign_status,\n" +
    "   nvl(v_sign_status.vl_bi_name, rrb.sign_status) AS rb_sign_status_val,\n" +
    "   rrb.rv_sign_batch AS rb_sign_batch,\n" +
    "   rrb.sign_time AS rb_sign_time,\n" +
    "   null AS rb_sign_user_id,\n" +
    "   rrb.is_new_add AS rb_is_new_add,\n" +
    "   nvl(v_is_new_add.vl_bi_name, rrb.is_new_add) AS rb_is_new_add_val,\n" +
    "   rrb.r_timestamp AS rb_timestamp,\n" +
    "   rrb.stock_code AS rb_stock_code,\n" +
    "   rrb.sign_user AS rb_sign_user,\n" +
    "   date_format(rc.receiving_add_time, 'yyyyMM') AS month\n" +
    " FROM (SELECT * FROM ods.gc_wms_gc_receiving_box WHERE data_flag != 'DELETE') AS rrb \n" +
    " LEFT JOIN (SELECT * FROM ods.gc_wms_gc_receiving WHERE data_flag != 'DELETE') AS rc ON rc.receiving_id = rrb.receiving_id\n" +
    s" LEFT JOIN ${Dw_dim_common.getDimSql("gc_wms_warehouse","wa")} on wa.warehouse_code = rc.warehouse_code\n" +
    s" LEFT JOIN ${Dw_dim_common.getDimSql("gc_wms_customer","cu")} on cu.customer_code = rc.customer_code\n" +
    "  \n" +
    " LEFT JOIN (\n" +
    "   SELECT * FROM dw.par_val_list \n" +
    "   WHERE datasource_num_id = '9004'\n" +
    "   AND vl_type = 'is_new_add'\n" +
    "   AND vl_datasource_table = 'gc_wms_gc_receiving_box'\n" +
    " ) AS v_is_new_add ON rrb.is_new_add = v_is_new_add.vl_value\n" +
    " \n" +
    " LEFT JOIN (\n" +
    "   SELECT * FROM dw.par_val_list\n" +
    "   WHERE datasource_num_id = '9004'\n" +
    "   AND vl_type = 'sign_status'\n" +
    "   AND vl_datasource_table = 'gc_wms_gc_receiving_box'\n" +
    " ) AS v_sign_status ON rrb.sign_status = v_sign_status.vl_value "

  private val zy_wms = "SELECT \n" +
    "  rrb.row_wid AS row_wid,\n" +
    "  cast(from_unixtime( unix_timestamp( current_date ( ) ), 'yyyyMMdd' ) AS string ) AS etl_proc_wid,\n" +
    "  current_timestamp ( ) AS w_insert_dt,\n" +
    "  current_timestamp ( ) AS w_update_dt,\n" +
    "  rrb.datasource_num_id AS datasource_num_id,\n" +
    "  rrb.data_flag AS data_flag,\n" +
    "  rrb.rb_id AS integration_id,\n" +
    "  rrb.created_on_dt AS created_on_dt,\n" +
    "  rrb.changed_on_dt AS changed_on_dt,\n" +
    "  0 AS  timezone,\n" +
    "  0.000 AS exchange_rate,\n" +
    " \n" +
    "  concat(rrb.datasource_num_id, rrb.receiving_id) AS receiving_key,\n" +
    "  concat(rc.datasource_num_id, sm.sm_id) AS sm_key,\n" +
    "  concat(rrb.datasource_num_id, rrb.warehouse_id) AS warehouse_key,\n" +
    "  concat(rrb.datasource_num_id, rc.transit_warehouse_id) AS transit_warehouse_key,\n" +
    "  concat(rrb.datasource_num_id, rc.customer_id) AS customer_key,\n" +
    "  concat(rrb.datasource_num_id, rc.to_warehouse_id) AS to_warehouse_key,\n" +
    " \n" +
    "  rrb.rb_id AS rb_id,\n" +
    "  rrb.warehouse_id AS rb_warehouse_id,\n" +
    "  null AS rb_warehouse_code,\n" +
    "  rrb.receiving_id AS rb_receiving_id,\n" +
    "  rrb.receiving_code AS rb_receiving_code,\n" +
    "  rrb.box_no AS rb_box_no,\n" +
    "  rrb.box_number AS rb_box_number,\n" +
    "  rrb.rb_transfer_status AS rb_transfer_status,\n" +
    "  nvl(v_rb_transfer_status.vl_bi_name, rrb.rb_transfer_status) AS rb_transfer_status_val,\n" +
    "  rrb.receiving_user_id AS rb_receiving_user_id,\n" +
    "  rrb.check_in_user_id AS rb_check_in_user_id,\n" +
    "  rrb.check_out_user_id AS rb_check_out_user_id,\n" +
    "  rc.receiving_add_time AS rb_received_time,\n" +
    "  rrb.rb_storage_time AS rb_storage_time,\n" +
    "  rrb.rb_delivery_time AS rb_delivery_time,\n" +
    "  rrb.rb_update_time AS rb_update_time,\n" +
    "  rrb.box_length AS rb_box_length,\n" +
    "  rrb.box_width AS rb_box_width,\n" +
    "  rrb.box_height AS rb_box_height,\n" +
    "  rrb.box_weight AS rb_box_weight,\n" +
    "  rrb.box_status AS rb_box_status,\n" +
    "  rrb.reference_box_no AS rb_reference_box_no,\n" +
    "  rrb.sign_status AS rb_sign_status,\n" +
    "  nvl(v_sign_status.vl_bi_name, rrb.sign_status) AS rb_sign_status_val,\n" +
    "  rrb.sign_batch AS rb_sign_batch,\n" +
    "  rrb.sign_time AS rb_sign_time,\n" +
    "  rrb.sign_user_id AS rb_sign_user_id,\n" +
    "  rrb.is_new_add AS rb_is_new_add,\n" +
    "  nvl(v_is_new_add.vl_bi_name, rrb.is_new_add) AS rb_is_new_add_val,\n" +
    "  rrb.rb_timestamp AS rb_timestamp,\n" +
    "  rrb.stock_code AS rb_stock_code,\n" +
    "  null AS rb_sign_user,\n" +
    "  date_format(rc.receiving_add_time, 'yyyyMM') AS month\n" +
    " FROM (SELECT * FROM ods.zy_wms_receiving_box WHERE data_flag != 'DELETE') AS rrb \n" +
    " LEFT JOIN (SELECT * FROM ods.zy_wms_receiving WHERE data_flag != 'DELETE') AS rc ON rc.receiving_id = rrb.receiving_id\n" +
    s" LEFT JOIN ${Dw_dim_common.getDimSql("zy_wms_shipping_method","sm")} ON sm.sm_code = rc.sm_code \n" +
    " \n" +
    " LEFT JOIN (\n" +
    "   SELECT * FROM dw.par_val_list\n" +
    "   WHERE datasource_num_id = '9022'\n" +
    "   AND vl_type = 'is_new_add'\n" +
    "   AND vl_datasource_table = 'zy_wms_receiving_box'\n" +
    "   ) AS v_is_new_add ON rrb.is_new_add=v_is_new_add.vl_value   \n" +
    " LEFT JOIN (\n" +
    "   SELECT * FROM dw.par_val_list\n" +
    "   WHERE datasource_num_id = '9022'\n" +
    "   AND vl_type = 'sign_status'\n" +
    "   AND vl_datasource_table = 'zy_wms_receiving_box'\n" +
    "   ) AS v_sign_status ON rrb.sign_status =v_sign_status.vl_value   \n" +
    " LEFT JOIN (\n" +
    "   SELECT * FROM dw.par_val_list\n" +
    "   WHERE datasource_num_id = '9022'\n" +
    "   AND vl_type = 'rb_transfer_status'\n" +
    "   AND vl_datasource_table = 'zy_wms_receiving_box'\n" +
    "   ) AS v_rb_transfer_status ON rrb.rb_transfer_status=v_rb_transfer_status.vl_value"

  def main(args: Array[String]): Unit = {

    val sqlArray: Array[String] = Array(gc_wms, zy_wms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))

    Dw_fact_common.getRunCode_hive_full_Into(task, tableName, sqlArray, Array(SystemCodeUtil.GC_WMS, SystemCodeUtil.ZY_WMS))
  }
}
