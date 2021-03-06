package com.zongteng.ztetl.etl.dw.fact.full.orders

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_fact_common, Dw_par_val_list_cache}
import com.zongteng.ztetl.util.DateUtil

object Dw_fact_ship_order_full {
  //任务名称(一般同类名)
  private val task = "Dw_fact_ship_order_full"

  //dw层类名
  private val tableName = "fact_ship_order"

  // 获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  //要执行的sql语句
  private val gc_wms ="SELECT so.row_wid as row_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,so.datasource_num_id as datasource_num_id" +
    " ,so.data_flag as data_flag" +
    " ,so.integration_id as integration_id" +
    " ,so.created_on_dt as created_on_dt" +
    " ,so.changed_on_dt as changed_on_dt" +
    " ,null as timezone" +
    " ,null as exchange_rate" +
    " ,cast(concat(so.datasource_num_id,so.warehouse_id) as bigint) as warehouse_key" +
    " ,cast(concat(so.datasource_num_id,od.to_warehouse_id) as bigint) as to_warehouse_key" +
    " ,cast(concat(so.datasource_num_id,od.customer_id) as bigint) as customer_key" +
    " ,cast(concat(so.datasource_num_id,so.sc_id) as bigint) as sc_key" +
    " ,cast(concat(so.datasource_num_id,sm.sm_id) as bigint) as sm_key" +
    " ,so.so_id as so_id" +
    " ,so.so_code as so_code" +
    " ,so.sc_id as so_sc_id" +
    " ,so.order_id as so_order_id" +
    " ,so.order_code as so_order_code" +
    " ,so.sp_code as so_sp_code" +
    " ,so.service_number as so_service_number" +
    " ,so.tracking_number as so_tracking_number" +
    " ,so.warehouse_id as so_warehouse_id" +
    " ,so.pp_barcode as so_pp_barcode" +
    " ,sm.sm_id as so_sm_id" +
    " ,so.sm_code as so_sm_code" +
    " ,so.so_status as so_status" +
    " ,so.service_provider_weight as so_service_provider_weight" +
    " ,so.so_weight as so_weight" +
    " ,so.so_vol_weight as so_vol_weight" +
    " ,so.so_length as so_length" +
    " ,so.so_width as so_width" +
    " ,so.so_height as so_height" +
    " ,so.so_declared_value as so_declared_value" +
    " ,so.so_insurance_value as so_insurance_value" +
    " ,so.so_tariff_fee as so_tariff_fee" +
    " ,so.so_shipping_fee as so_shipping_fee" +
    " ,so.currency_code as so_currency_code" +
    " ,so.currency_rate as so_currency_rate" +
    " ,so.so_add_time as so_add_time" +
    " ,so.so_ship_time as so_ship_time" +
    " ,so.so_delivered_time as so_delivered_time" +
    " ,so.so_update_time as so_update_time" +
    " ,so.so_timestamp as so_timestamp" +
    " ,from_unixtime(unix_timestamp(so.created_on_dt),'yyyyMM') as month" +
    " FROM (select * from ods.gc_wms_ship_order where data_flag<>'delete' ) as so" +
    s" left join ${Dw_dim_common.getDimSql("gc_wms_shipping_method","sm")} on sm.sm_code=so.sm_code and sm.sm_status=1" +
    " left join (select * from ods.gc_wms_orders where data_flag<>'delete' ) as od on od.order_id=so.order_id"
  private val zy_wms="SELECT so.row_wid as row_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,so.datasource_num_id as datasource_num_id" +
    " ,so.data_flag as data_flag" +
    " ,so.integration_id as integration_id" +
    " ,so.created_on_dt as created_on_dt" +
    " ,so.changed_on_dt as changed_on_dt" +
    " ,null as timezone" +
    " ,null as exchange_rate" +
    " ,cast(concat(so.datasource_num_id,so.warehouse_id) as bigint) as warehouse_key" +
    " ,cast(concat(so.datasource_num_id,od.to_warehouse_id) as bigint) as to_warehouse_key" +
    " ,cast(concat(so.datasource_num_id,od.customer_id) as bigint) as customer_key" +
    " ,cast(concat(so.datasource_num_id,so.sc_id) as bigint) as sc_key" +
    " ,cast(concat(so.datasource_num_id,sm.sm_id) as bigint) as sm_key" +
    " ,so.so_id as so_id" +
    " ,so.so_code as so_code" +
    " ,so.sc_id as so_sc_id" +
    " ,so.order_id as so_order_id" +
    " ,so.order_code as so_order_code" +
    " ,so.sp_code as so_sp_code" +
    " ,so.service_number as so_service_number" +
    " ,so.tracking_number as so_tracking_number" +
    " ,so.warehouse_id as so_warehouse_id" +
    " ,so.pp_barcode as so_pp_barcode" +
    " ,sm.sm_id as so_sm_id" +
    " ,so.sm_code as so_sm_code" +
    " ,so.so_status as so_status" +
    " ,so.service_provider_weight as so_service_provider_weight" +
    " ,so.so_weight as so_weight" +
    " ,so.so_vol_weight as so_vol_weight" +
    " ,so.so_length as so_length" +
    " ,so.so_width as so_width" +
    " ,so.so_height as so_height" +
    " ,so.so_declared_value as so_declared_value" +
    " ,so.so_insurance_value as so_insurance_value" +
    " ,so.so_tariff_fee as so_tariff_fee" +
    " ,so.so_shipping_fee as so_shipping_fee" +
    " ,so.currency_code as so_currency_code" +
    " ,so.currency_rate as so_currency_rate" +
    " ,so.so_add_time as so_add_time" +
    " ,so.so_ship_time as so_ship_time" +
    " ,so.so_delivered_time as so_delivered_time" +
    " ,so.so_update_time as so_update_time" +
    " ,so.so_timestamp as so_timestamp" +
    " ,from_unixtime(unix_timestamp(so.created_on_dt),'yyyyMM') as month" +
    " FROM (select * from ods.zy_wms_ship_order where data_flag<>'delete' ) as so" +
    s" left join ${Dw_dim_common.getDimSql("zy_wms_shipping_method","sm")} on sm.sm_code=so.sm_code and sm.sm_status=1" +
    " left join (select * from ods.zy_wms_orders where data_flag<>'delete' ) as od on od.order_id=so.order_id"


  def main(args: Array[String]): Unit = {
    Dw_fact_common.getRunCode_hive_full_Into(task, tableName, Array(gc_wms,zy_wms),Dw_par_val_list_cache.EMPTY_PAR_VAL_LIST)
  }
}
