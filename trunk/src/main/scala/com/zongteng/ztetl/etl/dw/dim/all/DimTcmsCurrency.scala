package com.zongteng.ztetl.etl.dw.dim.all

import com.zongteng.ztetl.common.Dw_dim_common
import com.zongteng.ztetl.util.DateUtil

object DimTcmsCurrency {
  //任务名称(一般同类名)
  private val task = "DimTcmsCurrency"

  //dw层类名
  private val tableName = "dim_tcms_currency"

  //获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  //要执行的sql语句
  private val gc_tcms="SELECT cy.row_wid as row_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,cy.datasource_num_id as datasource_num_id" +
    " ,cy.data_flag as data_flag" +
    " ,cy.integration_id as integration_id" +
    " ,cy.created_on_dt as  created_on_dt" +
    " ,cy.changed_on_dt as changed_on_dt" +
    " ,cy.currency_id as currency_id" +
    " ,cy.currency_name_en as currency_name_en" +
    " ,cy.currency_name as currency_name" +
    " ,cy.currency_code as currency_code" +
    " ,cy.currency_local as currency_is_local" +
    " ,nvl(pvl1.vl_bi_name,cy.currency_local) as currency_is_local_val" +
    " ,cy.currency_symbol_left as currency_symbol_left" +
    " ,cy.currency_symbol_right as currency_symbol_right" +
    " ,cy.currency_decimal_point as currency_decimal_point" +
    " ,cy.currency_thousands_point as currency_thousands_point" +
    " ,cy.currency_decimal_places as currency_decimal_places" +
    " ,cy.currency_hs_code as currency_hs_code" +
    " ,cy.currency_official_rate as currency_official_rate" +
    " ,cy.currency_rate as currency_rate" +
    " ,cy.currency_update_time as currency_update_time" +
    " FROM (select * from ods.gc_tcms_currency where day= "+ nowDate +" ) as cy" +
    " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9020' and pvl.vl_type='currency_local' and pvl.vl_datasource_table='gc_tcms_currency') as pvl1 on pvl1.vl_value=cy.currency_local"

  def main(args: Array[String]): Unit = {
    Dw_dim_common.getRunCode_full(task, tableName, Array(gc_tcms))
  }
}
