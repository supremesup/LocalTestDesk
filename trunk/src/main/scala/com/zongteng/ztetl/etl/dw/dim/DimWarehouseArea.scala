package com.zongteng.ztetl.etl.dw.dim

import com.zongteng.ztetl.api.SparkConfig
import com.zongteng.ztetl.common.{Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.{DateUtil, Log}
import org.apache.spark.sql.{DataFrame, SparkSession}

object DimWarehouseArea {
  def main(args: Array[String]): Unit = {
    //写入日志开始
    val task: String = Log.start("Dw_dim_warehouse_area")

    try {
      val spark: SparkSession = SparkConfig.init("Dw_dim_warehouse_area")
      //获取当天时间
      val nowDate: String = DateUtil.getNowTime()
      val sql1 = "select * from  ods.gc_owms_au_warehouse_area   where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_cz_warehouse_area   where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_de_warehouse_area   where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_es_warehouse_area   where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_frvi_warehouse_area where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_it_warehouse_area   where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_jp_warehouse_area   where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_uk_warehouse_area   where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_ukob_warehouse_area where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_usea_warehouse_area where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_uswe_warehouse_area where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_usnb_warehouse_area where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_usot_warehouse_area where day=" + nowDate + "" +
        " union all" +
        " select * from  ods.gc_owms_ussc_warehouse_area where day=" + nowDate + ""

      val owmsDF: DataFrame = spark.sql(sql1)
      owmsDF.createOrReplaceTempView("gc_owms_warehouse_area")


      val gc_wms = "SELECT wa.row_wid as row_wid" +
        " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
        " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
        " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
        " ,wa.datasource_num_id as datasource_num_id" +
        " ,wa.data_flag as data_flag" +
        " ,wa.integration_id as integration_id" +
        " ,wa.created_on_dt as  created_on_dt" +
        " ,wa.changed_on_dt as changed_on_dt" +
        " ,cast(concat(wa.datasource_num_id,wa.warehouse_id) as bigint) as warehouse_key" +
        " ,wa.wa_id as wa_id" +
        " ,wa.wa_code as wa_code" +
        " ,wa.warehouse_id as wa_warehouse_id" +
        " ,ws.warehouse_code as wa_warehouse_code" +
        " ,owa.wp_code as wa_wp_code" +
        " ,wa.wa_name as wa_name" +
        " ,wa.wa_name_en as wa_name_en" +
        " ,wa.wa_type as wa_type" +
        " ,nvl(pvl2.vl_bi_name,wa.wa_type) as wa_type_val" +
        " ,wa.pc_id as wa_pc_id" +
        " ,wa.wa_status as wa_status" +
        " ,nvl(pvl1.vl_bi_name,wa.wa_status) as wa_status_val" +
        " ,wa.wa_sort as wa_sort" +
        " ,wa.wa_add_time as wa_add_time" +
        " ,wa.wa_update_time as wa_update_time" +
        " FROM (select * from ods.gc_wms_warehouse_area where day= "+ nowDate +") as wa" +
        " LEFT JOIN (select * from ods.gc_wms_warehouse where day= "+ nowDate +") as ws on ws.warehouse_id=wa.warehouse_id" +
        " LEFT JOIN gc_owms_warehouse_area as owa on wa.warehouse_id=owa.warehouse_id and wa.wa_id=owa.wa_id" +
        " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9004' and pvl.vl_type='wa_status' and pvl.vl_datasource_table='gc_wms_warehouse_area') as pvl1 on pvl1.vl_value=wa.wa_status" +
        " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9004' and pvl.vl_type='wa_type'   and pvl.vl_datasource_table='gc_wms_warehouse_area') as pvl2 on pvl2.vl_value=wa.wa_type"

      val zy_wms = "SELECT wa.row_wid as row_wid" +
        " ,from_unixtime(unix_timestamp(),'yyyyMMdd') as etl_proc_wid" +
        " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_insert_dt" +
        " ,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') as w_update_dt" +
        " ,wa.datasource_num_id as datasource_num_id" +
        " ,wa.data_flag as data_flag" +
        " ,wa.integration_id as integration_id" +
        " ,wa.created_on_dt as  created_on_dt" +
        " ,wa.changed_on_dt as changed_on_dt" +
        " ,cast(concat(wa.datasource_num_id,wa.warehouse_id) as bigint) as warehouse_key" +
        " ,wa.wa_id as wa_id" +
        " ,wa.wa_code as wa_code" +
        " ,wa.warehouse_id as wa_warehouse_id" +
        " ,ws.warehouse_code as wa_warehouse_code" +
        " ,null as wa_wp_code" +
        " ,wa.wa_name as wa_name" +
        " ,wa.wa_name_en as wa_name_en" +
        " ,wa.wa_type as wa_type" +
        " ,nvl(pvl2.vl_bi_name,wa.wa_type) as wa_type_val" +
        " ,wa.pc_id as wa_pc_id" +
        " ,wa.wa_status as wa_status" +
        " ,nvl(pvl1.vl_bi_name,wa.wa_status) as wa_status_val" +
        " ,wa.wa_sort as wa_sort" +
        " ,wa.wa_add_time as wa_add_time" +
        " ,wa.wa_update_time as wa_update_time" +
        " FROM (select * from ods.zy_wms_warehouse_area where day= "+ nowDate +") as wa" +
        " LEFT JOIN (select * from ods.zy_wms_warehouse where day= "+ nowDate +") as ws on ws.warehouse_id=wa.warehouse_id" +
        " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9022' and pvl.vl_type='wa_status' and pvl.vl_datasource_table='zy_wms_warehouse_area') as pvl1 on pvl1.vl_value=wa.wa_status" +
        " left join  (select * from dw.par_val_list as pvl where pvl.datasource_num_id='9022' and pvl.vl_type='wa_type' and pvl.vl_datasource_table='zy_wms_warehouse_area') as pvl2 on pvl2.vl_value=wa.wa_type"


      // 值列表缓存
      Dw_par_val_list_cache.cacheParValList(spark, Array(SystemCodeUtil.GC_WMS, SystemCodeUtil.ZY_WMS))

      val sqlArray: Array[String] = Array(gc_wms, zy_wms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))

      val insertStr = "insert overwrite  table dw.dim_warehouse_area  " + sqlArray.mkString(" union all ")
      spark.sql(insertStr)

      spark.stop()
      //写入结束日志
      Log.end(task)
    } catch {
      case ex: Exception => Log.error(task, ex)
    }

  }
}
