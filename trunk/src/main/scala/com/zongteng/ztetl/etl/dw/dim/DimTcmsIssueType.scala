package com.zongteng.ztetl.etl.dw.dim

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.DateUtil

/**
  * 客户问题表
  *
  */
object DimTcmsIssueType {

  private val task = "Spark_Etl_Dw_Dim_TCMS_Issue_Type"

  private val tableName = "dim_tcms_issue_type"

  private val nowDate: String = DateUtil.getNowTime()

  val gc_tcms = "SELECT \n" +
    " row_wid AS row_wid,\n" +
    " date_format(current_date(), 'yyyyMMdd') AS etl_proc_wid,\n" +
    " current_timestamp ( ) AS w_insert_dt,\n" +
    " current_timestamp ( ) AS w_update_dt,\n" +
    " datasource_num_id AS datasource_num_id,\n" +
    " data_flag AS data_flag,\n" +
    " tms_id AS integration_id,\n" +
    " created_on_dt AS created_on_dt,\n" +
    " changed_on_dt AS changed_on_dt,\n" +
    "\n" +
    " tms_id AS issue_type_tms_id,\n" +
    " issuekind_code AS issue_type_issuekind_code,\n" +
    " issuekind_cnname AS issue_type_issuekind_cnname,\n" +
    " issuekind_content AS issue_type_issuekind_content,\n" +
    " isu_interactionsign AS issue_type_isu_interactionsign,\n" +
    " issue_enable AS issue_type_issue_enable,\n" +
    " nvl(v_issue_enable.vl_name, issue_enable) AS issue_type_issue_enable_val,\n" +
    " issue_class_code AS issue_type_issue_class_code,\n" +
    " issuekind_mnemonic AS issue_type_issuekind_mnemonic,\n" +
    " issuekind_link AS issue_type_issuekind_link,\n" +
    " last_update_time AS issue_type_last_update_time\n" +
    s"FROM (SELECT * FROM ods.gc_tcms_cts_customer_issuekind WHERE day = '$nowDate') AS cci\n" +
    "LEFT JOIN \n" +
    "(SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_datasource_table = 'gc_tcms_cts_customer_issuekind' AND vl_type = 'issue_enable') v_issue_enable\n" +
    "ON cci.issue_enable = v_issue_enable.vl_value"

  def main(args: Array[String]): Unit = {
    val sqlArray: Array[String] = Array(gc_tcms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))
    Dw_dim_common.getRunCode_full(task, tableName, sqlArray, Array(SystemCodeUtil.GC_TCMS))
  }

}
