package com.zongteng.ztetl.etl.dw.dim.all

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_par_val_list_cache, SystemCodeUtil}

object DimTcmsFeeType {

  private val task = "Spark_Dim_Tcms_Fee_Type"

  private val tableName = "dim_tcms_fee_type"

  private val gc_tcms = "SELECT \n" +
    " ft.row_wid AS row_wid,\n" +
    " date_format(current_date(), 'yyyyMMdd') AS etl_proc_wid,\n" +
    " current_timestamp ( ) AS w_insert_dt,\n" +
    " current_timestamp ( ) AS w_update_dt,\n" +
    " ft.datasource_num_id AS datasource_num_id,\n" +
    " ft.data_flag AS data_flag,\n" +
    " ft.fk_id AS integration_id,\n" +
    " ft.created_on_dt AS created_on_dt,\n" +
    " ft.changed_on_dt AS changed_on_dt,\n" +
    "\n" +
    " ft.fk_id AS ft_id,\n" +
    " ft.tms_id AS ft_tms_id,\n" +
    " ft.fk_code AS ft_code,\n" +
    " ft.fk_name AS fk_name,\n" +
    " ft.fk_ename AS fk_ename,\n" +
    " ft.fk_note AS ft_note,\n" +
    " ft.fk_type AS ft_type,\n" +
    " nvl(v_fk_type.vl_name, ft.fk_type) AS ft_type_val, \n" +
    " ft.group_id AS ft_group_id,\n" +
    " ft.last_update_time AS ft_last_update_time,\n" +
    " ft.serverbill_printed AS ft_serverbill_printed,\n" +
    " ftg.group_code AS ft_group_code,\n" +
    " ftg.group_name AS ft_group_name,\n" +
    " ftg.group_ename AS ft_group_ename,\n" +
    " ftg.group_remark AS ft_group_remark,\n" +
    " ftg.tms_id AS ft_group_tms_id,\n" +
    " ftg.last_update_time AS ft_group_last_update_time\n" +
    s"FROM ${Dw_dim_common.getDimSql("gc_tcms_xtd_customer_feekind", "ft")}\n" +
    s"LEFT JOIN ${Dw_dim_common.getDimSql("gc_tcms_xtd_customer_feekind_group", "ftg")} \n" +
    "ON ft.group_id = ftg.group_id\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list WHERE vl_type = 'fk_type' AND vl_datasource_table = 'gc_tcms_xtd_customer_feekind') v_fk_type\n" +
    " ON ft.fk_type = v_fk_type.vl_value"

  def main(args: Array[String]): Unit = {
    val sqlArray: Array[String] = Array(gc_tcms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))
    Dw_dim_common.getRunCode_full(
      task,
      tableName,
      sqlArray,
      Array(SystemCodeUtil.GC_TCMS)
    )
  }









}
