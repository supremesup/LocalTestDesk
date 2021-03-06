package com.zongteng.ztetl.etl.dw.dim

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.DateUtil

object DimTcmsProduct {

  private val task = "Spark_Etl_DW_Dim_Tcms_Product"

  private val tableName = "dim_tcms_product"

  private val nowDate: String = DateUtil.getNowTime()

  private val gc_tcms = "SELECT \n"  +
    "   pd.row_wid AS row_wid,\n" +
    "   date_format(current_date(), 'yyyyMMdd') AS etl_proc_wid,\n" +
    "   current_timestamp ( ) AS w_insert_dt,\n" +
    "   current_timestamp ( ) AS w_update_dt,\n" +
    "   pd.datasource_num_id AS datasource_num_id,\n" +
    "   pd.data_flag AS data_flag,\n" +
    "   pd.integration_id AS integration_id,\n" +
    "   pd.created_on_dt AS created_on_dt,\n" +
    "   pd.changed_on_dt AS changed_on_dt,\n" +
    " product_code AS product_code,\n" +
    " product_cnname AS product_cnname,\n" +
    " product_enname AS product_enname,\n" +
    " pd.productgroup_code AS product_productgroup_code,\n" +
    " product_currency_code AS product_currency_code,\n" +
    " product_status AS product_status,\n" +
    " nvl(v_product_status.vl_name, product_status) AS product_status_val,\n" +
    " product_trackstatus AS product_trackstatus,\n" +
    " nvl(v_product_trackstatus.vl_name, product_status) AS product_trackstatus_val,\n" +
    " product_aging AS product_aging,\n" +
    " product_note AS product_note,\n" +
    " tms_id AS product_tms_id,\n" +
    " product_print_sign AS product_print_sign,\n" +
    " product_test_sort AS product_test_sort,\n" +
    " product_carrier_name AS product_carrier_name,\n" +
    " operational_difficulty AS product_operational_difficulty,\n" +
    " package_weight AS product_package_weight,\n" +
    " audit_sku_sign AS product_audit_sku_sign,\n" +
    " nvl(v_audit_sku_sign.vl_name, audit_sku_sign) AS product_audit_sku_sign_val,\n" +
    " ispanasonicunit AS product_ispanasonicunit,\n" +
    " nvl(v_ispanasonicunit.vl_name, ispanasonicunit) AS product_ispanasonicunit_val,\n" +
    " pd.last_update_time AS product_last_update_time,\n" +
    " isvirtual AS product_isvirtual,\n" +
    " nvl(v_isvirtual.vl_name, isvirtual) AS product_isvirtual_val,\n" +
    " original_server_code AS product_original_server_code,\n" +
    " original_server_channelid AS product_original_server_channelid,\n" +
    " pdg.productgroup_cnname AS product_productgroup_cnname,\n" +
    " pdg.productgroup_enname AS product_productgroup_enname,\n" +
    " pdg.productgroup_note AS product_productgroup_note,\n" +
    " pdg.productgroup_hawbrule AS product_productgroup_hawbrule,\n" +
    " pdg.last_update_time AS product_productgroup_last_update_time\n" +
    s"FROM (SELECT * FROM ods.gc_tcms_csi_productkind WHERE day = '$nowDate') pd\n" +
    s"LEFT JOIN (SELECT * FROM ods.gc_tcms_xtd_productgroup WHERE day = '$nowDate') pdg ON pd.productgroup_code = pdg.productgroup_code\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    " WHERE vl_datasource_table = 'gc_tcms_csi_productkind' AND vl_type = 'product_status') v_product_status\n" +
    "ON pd.product_status = v_product_status.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    " WHERE vl_datasource_table = 'gc_tcms_csi_productkind' AND vl_type = 'product_trackstatus') v_product_trackstatus\n" +
    "ON pd.product_trackstatus = v_product_trackstatus.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    " WHERE vl_datasource_table = 'gc_tcms_csi_productkind' AND vl_type = 'audit_sku_sign') v_audit_sku_sign\n" +
    "ON pd.audit_sku_sign = v_audit_sku_sign.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    " WHERE vl_datasource_table = 'gc_tcms_csi_productkind' AND vl_type = 'ispanasonicunit') v_ispanasonicunit\n" +
    "ON pd.ispanasonicunit = v_ispanasonicunit.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    " WHERE vl_datasource_table = 'gc_tcms_csi_productkind' AND vl_type = 'isvirtual') v_isvirtual\n" +
    "ON pd.isvirtual = v_isvirtual.vl_value"

  def main(args: Array[String]): Unit = {
    val sqlArray: Array[String] = Array(gc_tcms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))
    Dw_dim_common.getRunCode_full(task, tableName, sqlArray, Array(SystemCodeUtil.GC_TCMS))
  }

}
