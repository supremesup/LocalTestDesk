package com.zongteng.ztetl.etl.dw.fact.full.storage

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_fact_common, Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.DateUtil

object Dw_fact_take_stock_item_full {
  //任务名称(一般同类名)
  private val task = "Dw_fact_take_stock_item_full"

  //dw层类名
  private val tableName = "fact_take_stock_item"

  // 获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  //要执行的sql语句
  private val gc_wms = "select tsi.row_wid " +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,tsi.datasource_num_id as datasource_num_id" +
    " ,tsi.data_flag as  data_flag" +
    " ,tsi.integration_id as  integration_id" +
    " ,tsi.created_on_dt as created_on_dt" +
    " ,tsi.changed_on_dt as changed_on_dt" +
    " ,null as timezone" +
    " ,null as exchange_rate" +
    " ,concat(w.datasource_num_id,w.warehouse_id) warehouse_key" +
    " ,c.row_wid customer_key" +
    " ,concat(tsi.datasource_num_id,tsi.product_id) product_key" +
    " ,lc.row_wid lc_key" +
    " ,wp.row_wid wp_key" +
    " ,tsi.tsi_id" +
    " ,tsi.ts_code tsi_ts_code" +
    " ,c.customer_id tsi_customer_id" +
    " ,tsi.customer_code tsi_customer_code" +
    " ,lc.lc_id tsi_lc_id" +
    " ,tsi.lc_code tsi_lc_code" +
    " ,tsi.product_id tsi_product_id" +
    " ,tsi.product_barcode tsi_product_barcode" +
    " ,tsi.tsi_current_quantity" +
    " ,tsi.tsi_quantity" +
    " ,tsi.tsi_status" +
    " ,concat(pvl1.vl_bi_name,tsi.tsi_status) tsi_status_type_val" +
    " ,tsi.tsi_add" +
    " ,tsi.tsi_abnormal_type" +
    " ,concat(pvl2.vl_bi_name,tsi.tsi_abnormal_type) tsi_abnormal_type_val" +
    " ,tsi.tsi_note" +
    " ,tsi.tsi_update_time" +
    " ,tsi.tsi_order_occupied_quantity" +
    " ,ts.warehouse_id tsi_warehouse_id" +
    " ,w.warehouse_code tsi_warehouse_code" +
    " ,wp.wp_id tsi_wp_id" +
    " ,wp.wp_code tsi_wp_code" +
    " from  (select * from ods.gc_wms_take_stock_item where data_flag<>'delete') as tsi " +
    " left join (select * from ods.gc_wms_take_stock where data_flag<>'delete') as ts on tsi.ts_code=ts.ts_code" +
    s" left join  ${Dw_dim_common.getDimSql("gc_wms_warehouse","w")} on w.warehouse_id=ts.warehouse_id" +
    s" left join  ${Dw_dim_common.getDimSql("gc_wms_location","lc")} on lc.warehouse_id=ts.warehouse_id and  lc.lc_code=tsi.lc_code" +
    s" left join ${Dw_dim_common.getDimSql("gc_wms_warehouse_physical","wp")} on wp.wp_code=ts.wp_code" +
    s" left join ${Dw_dim_common.getDimSql("gc_wms_customer","c")} on c.customer_code=tsi.customer_code" +
    " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9004' and pvl.vl_type='tsi_status' and pvl.vl_datasource_table='gc_wms_take_stock_item') " +
    "            as pvl1 on pvl1.vl_value=tsi.tsi_status" +
    " left join  (select * from dw.par_val_list as pv1 where pv1.datasource_num_id='9004' and pv1.vl_type='tsi_abnormal_type' and pv1.vl_datasource_table='gc_wms_take_stock_item') " +
    "            as pvl2 on pvl2.vl_value=tsi.tsi_abnormal_type"
  private val zy_wms = "select tsi.row_wid " +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,tsi.datasource_num_id as datasource_num_id" +
    " ,tsi.data_flag as  data_flag" +
    " ,tsi.tsi_id as  integration_id" +
    " ,tsi.created_on_dt as created_on_dt" +
    " ,tsi.changed_on_dt as changed_on_dt" +
    " ,null as timezone" +
    " ,null as exchange_rate" +
    " ,concat(w.datasource_num_id,w.warehouse_id) warehouse_key" +
    " ,c.row_wid customer_key" +
    " ,concat(tsi.datasource_num_id,tsi.product_id) product_key" +
    " ,lc.row_wid lc_key" +
    " ,null wp_key" +
    " ,tsi.tsi_id" +
    " ,tsi.ts_code tsi_ts_code" +
    " ,c.customer_id tsi_customer_id" +
    " ,tsi.customer_code tsi_customer_code" +
    " ,lc.lc_id tsi_lc_id" +
    " ,tsi.lc_code tsi_lc_code" +
    " ,tsi.product_id tsi_product_id" +
    " ,tsi.product_barcode tsi_product_barcode" +
    " ,tsi.tsi_current_quantity" +
    " ,tsi.tsi_quantity" +
    " ,tsi.tsi_status" +
    " ,concat(pvl1.vl_bi_name,tsi.tsi_status) tsi_status_type_val" +
    " ,tsi.tsi_add" +
    " ,tsi.tsi_abnormal_type" +
    " ,concat(pvl2.vl_bi_name,tsi.tsi_abnormal_type) tsi_abnormal_type_val" +
    " ,tsi.tsi_note" +
    " ,tsi.tsi_update_time" +
    " ,tsi.tsi_order_occupied_quantity" +
    " ,ts.warehouse_id tsi_warehouse_id" +
    " ,w.warehouse_code tsi_warehouse_code" +
    " ,null tsi_wp_id" +
    " ,null tsi_wp_code" +
    " from  (select * from ods.zy_wms_take_stock_item where data_flag<>'delete') as tsi " +
    " left join (select * from ods.zy_wms_take_stock where data_flag<>'delete') as ts on tsi.ts_code=ts.ts_code" +
    s" left join  ${Dw_dim_common.getDimSql("zy_wms_warehouse","w")} on w.warehouse_id=ts.warehouse_id" +
    s" left join  ${Dw_dim_common.getDimSql("zy_wms_location","lc")} on lc.warehouse_id=ts.warehouse_id and  lc.lc_code=tsi.lc_code" +
    s" left join ${Dw_dim_common.getDimSql("zy_wms_customer","c")} on c.customer_code=tsi.customer_code" +
    " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9022' and pvl.vl_type='tsi_status' and pvl.vl_datasource_table='zy_wms_take_stock_item') " +
    "            as pvl1 on pvl1.vl_value=tsi.tsi_status" +
    " left join  (select * from dw.par_val_list as pv1 where pv1.datasource_num_id='9022' and pv1.vl_type='tsi_abnormal_type' and pv1.vl_datasource_table='zy_wms_take_stock_item') " +
    "            as pvl2 on pvl2.vl_value=tsi.tsi_abnormal_type"

  def main(args: Array[String]): Unit = {
    val sqlArray: Array[String] = Array(gc_wms,zy_wms).map(_.replaceAll("dw.par_val_list",Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))
    Dw_fact_common.getRunCode_hive_nopartition_full_Into(task,tableName,sqlArray,Array(SystemCodeUtil.GC_WMS,SystemCodeUtil.ZY_WMS))
  }
}
