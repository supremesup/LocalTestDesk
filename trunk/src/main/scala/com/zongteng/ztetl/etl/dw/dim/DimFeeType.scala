package com.zongteng.ztetl.etl.dw.dim

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.DateUtil

object DimFeeType {

  private val task = "Spark_Etl_Dim_Fee_Type"

  private val tableName = "dim_fee_type"
  //获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  private val gc_wms = "SELECT \n" +
    " row_wid AS row_wid,\n" +
    " date_format(current_date(), 'yyyyMMdd') AS etl_proc_wid,\n" +
    " current_timestamp ( ) AS w_insert_dt,\n" +
    " current_timestamp ( ) AS w_update_dt,\n" +
    " datasource_num_id AS datasource_num_id,\n" +
    " data_flag AS data_flag,\n" +
    " ft_id AS integration_id,\n" +
    " created_on_dt AS created_on_dt,\n" +
    " changed_on_dt AS changed_on_dt,\n" +
    "\n" +
    " ft_id AS ft_id,\n" +
    " ft_code AS ft_code,\n" +
    " ft_name_en AS ft_name_en,\n" +
    " ft_name_cn AS ft_name_cn,\n" +
    " ft_note AS ft_note,\n" +
    " ft_type AS ft_type,\n" +
    " nvl(v_ft_type.vl_name, ft_type) AS ft_type_val,\n" +
    " ft_add_time AS ft_add_time,\n" +
    " ft_update_time AS ft_update_time\n" +
    s"FROM (SELECT * FROM ods.gc_wms_fee_type WHERE day = '$nowDate') AS ft \n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list WHERE vl_type = 'ft_type' AND vl_datasource_table = 'gc_wms_fee_type') v_ft_type\n" +
    "ON ft.ft_type = v_ft_type.vl_value"

  private val zy_wms = "SELECT \n" +
    " row_wid AS row_wid,\n" +
    " date_format(current_date(), 'yyyyMMdd') AS etl_proc_wid,\n" +
    " current_timestamp ( ) AS w_insert_dt,\n" +
    " current_timestamp ( ) AS w_update_dt,\n" +
    " datasource_num_id AS datasource_num_id,\n" +
    " data_flag AS data_flag,\n" +
    " ft_id AS integration_id,\n" +
    " created_on_dt AS created_on_dt,\n" +
    " changed_on_dt AS changed_on_dt,\n" +
    "\n" +
    " ft_id AS ft_id,\n" +
    " ft_code AS ft_code,\n" +
    " ft_name_en AS ft_name_en,\n" +
    " ft_name_cn AS ft_name_cn,\n" +
    " ft_note AS ft_note,\n" +
    " ft_type AS ft_type,\n" +
    " nvl(v_ft_type.vl_name, ft_type) AS ft_type_val,\n" +
    " ft_add_time AS ft_add_time,\n" +
    " ft_update_time AS ft_update_time\n" +
    s"FROM (SELECT * FROM ods.zy_wms_fee_type WHERE day = '${nowDate}') AS ft \n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list WHERE vl_type = 'ft_type' AND vl_datasource_table = 'gc_wms_fee_type') v_ft_type\n" +
    "ON ft.ft_type = v_ft_type.vl_value"



  def main(args: Array[String]): Unit = {
    val sqlArray: Array[String] = Array(gc_wms, zy_wms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))
    Dw_dim_common.getRunCode_full(
      task,
      tableName,
      sqlArray,
      Array(SystemCodeUtil.GC_WMS, SystemCodeUtil.ZY_WMS)
    )
  }




}
