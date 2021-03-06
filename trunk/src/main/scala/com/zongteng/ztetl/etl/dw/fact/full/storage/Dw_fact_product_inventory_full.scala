package com.zongteng.ztetl.etl.dw.fact.full.storage

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_fact_common}
import com.zongteng.ztetl.util.DateUtil

object Dw_fact_product_inventory_full {
  //任务名称(一般同类名)
  private val task = "Dw_fact_product_inventory_full"

  //dw层类名
  private val tableName = "fact_product_inventory"

  // 获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  //要执行的sql语句
  private val gc_wms = "select pi.row_wid " +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,pi.datasource_num_id as datasource_num_id" +
    " ,pi.data_flag as  data_flag" +
    " ,pi.integration_id as  integration_id" +
    " ,pi.created_on_dt as created_on_dt" +
    " ,pi.changed_on_dt as changed_on_dt" +
    " ,case " +
    " when tz.timezone_season_type='winner_time'  then  " +
    "     case when pi.pi_add_time between tz.timezone_season_start and tz.timezone_season_end then tz.timezone_winner_time_dif_val else tz.timezone_summer_time_dif_val end " +
    " when   tz.timezone_season_type='summer_time' then " +
    "     case when pi.pi_add_time between tz.timezone_season_start and tz.timezone_season_end then tz.timezone_summer_time_dif_val else tz.timezone_winner_time_dif_val end " +
    " else null end as timezone" +
    " ,null as exchange_rate" +
    " ,concat(w.datasource_num_id,w.warehouse_id) warehouse_key" +
    " ,concat(pi.datasource_num_id,pi.product_id) product_key" +
    " ,concat(pi.datasource_num_id,pi.customer_id) customer_key" +
    " ,pi.pi_id" +
    " ,pi.product_barcode pi_product_barcode" +
    " ,pi.customer_id pi_customer_id" +
    " ,pi.customer_code pi_customer_code" +
    " ,pi.product_id pi_product_id" +
    " ,pi.warehouse_id pi_warehouse_id" +
    " ,w.warehouse_code pi_warehouse_code" +
    " ,pi.pi_planned" +
    " ,pi.pi_onway" +
    " ,pi.pi_pending" +
    " ,pi.pi_sellable" +
    " ,pi.pi_unsellable" +
    " ,pi.pi_reserved" +
    " ,pi.pi_outbound" +
    " ,pi.pi_shipped" +
    " ,pi.pi_hold" +
    " ,pi.pi_no_stock" +
    " ,pi.pi_warning_qty" +
    " ,pi.pi_shared" +
    " ,pi.pi_sold_shared" +
    " ,pi.buyer_id pi_buyer_id" +
    " ,pi.pi_add_time" +
    " ,pi.pi_update_time" +
    " ,pi.pi_stocking" +
    " ,pi.pi_tune_out" +
    " ,pi.pi_tune_in" +
    " ,pi.change_time pi_change_time" +
    " ,date_format(pi.created_on_dt,'yyyyMM') as month" +
    " from  (select * from ods.gc_wms_product_inventory where data_flag<>'delete') as pi" +
    s" left join  ${Dw_dim_common.getDimSql("gc_wms_warehouse","w")} on w.warehouse_id=pi.warehouse_id" +
    s" left join  ${Dw_dim_common.getDimSql("gc_wms_product","p")} on pi.product_id=p.product_id" +
    " left join dw.par_timezone as tz on tz.warehouse_code=w.warehouse_code and  tz.timezone_year=year(pi.pi_add_time)"
  private val zy_wms = "select pi.row_wid " +
    " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
    " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
    " ,pi.datasource_num_id as datasource_num_id" +
    " ,pi.data_flag as  data_flag" +
    " ,pi.integration_id as  integration_id" +
    " ,pi.created_on_dt as created_on_dt" +
    " ,pi.changed_on_dt as changed_on_dt" +
    " ,case " +
    " when tz.timezone_season_type='winner_time'  then  " +
    "     case when pi.pi_add_time between tz.timezone_season_start and tz.timezone_season_end then tz.timezone_winner_time_dif_val else tz.timezone_summer_time_dif_val end " +
    " when   tz.timezone_season_type='summer_time' then " +
    "     case when pi.pi_add_time between tz.timezone_season_start and tz.timezone_season_end then tz.timezone_summer_time_dif_val else tz.timezone_winner_time_dif_val end " +
    " else null  end as timezone" +
    " ,null as exchange_rate" +
    " ,concat(w.datasource_num_id,w.warehouse_id) warehouse_key" +
    " ,concat(pi.datasource_num_id,pi.product_id) product_key" +
    " ,concat(pi.datasource_num_id,pi.customer_id) customer_key" +
    " ,pi.pi_id" +
    " ,pi.product_barcode pi_product_barcode" +
    " ,pi.customer_id pi_customer_id" +
    " ,pi.customer_code pi_customer_code" +
    " ,pi.product_id pi_product_id" +
    " ,pi.warehouse_id pi_warehouse_id" +
    " ,w.warehouse_code pi_warehouse_code" +
    " ,pi.pi_planned" +
    " ,pi.pi_onway" +
    " ,pi.pi_pending" +
    " ,pi.pi_sellable" +
    " ,pi.pi_unsellable" +
    " ,pi.pi_reserved" +
    " ,pi.pi_outbound" +
    " ,pi.pi_shipped" +
    " ,pi.pi_hold" +
    " ,pi.pi_no_stock" +
    " ,pi.pi_warning_qty" +
    " ,pi.pi_shared" +
    " ,pi.pi_sold_shared" +
    " ,pi.buyer_id pi_buyer_id" +
    " ,pi.pi_add_time" +
    " ,pi.pi_update_time" +
    " ,pi.pi_stocking" +
    " ,pi.pi_tune_out" +
    " ,pi.pi_tune_in" +
    " ,null pi_change_time" +
    " ,date_format(pi.created_on_dt,'yyyyMM') as month" +
    " from  (select * from ods.zy_wms_product_inventory where  data_flag<>'delete') as pi" +
    s" left join  ${Dw_dim_common.getDimSql("zy_wms_warehouse","w")} on w.warehouse_id=pi.warehouse_id" +
    s" left join  ${Dw_dim_common.getDimSql("zy_wms_product","p")} on pi.product_id=p.product_id" +
    " left join dw.par_timezone as tz on tz.warehouse_code=w.warehouse_code and  tz.timezone_year=year(pi.pi_add_time)"

  def main(args: Array[String]): Unit = {
    Dw_fact_common.getRunCode_hive_full_Into(task, tableName, Array(gc_wms,zy_wms))
  }
}
