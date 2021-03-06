package com.zongteng.ztetl.etl.dw.fact.full.picking

import com.zongteng.ztetl.common.{Dw_fact_common, Dw_par_val_list_cache, SystemCodeUtil}


object Dw_fact_wellen_rule_full {

  val sql = "SELECT\n" +
    "wr.row_wid AS row_wid,\n" +
    "date_format(current_date(), 'yyyyMMdd') AS etl_proc_wid,\n" +
    "current_timestamp ( ) AS w_insert_dt,\n" +
    "current_timestamp ( ) AS w_update_dt,\n" +
    "wr.datasource_num_id AS datasource_num_id,\n" +
    "wr.data_flag AS data_flag,\n" +
    "wr.wellen_id AS integration_id,\n" +
    "wr.created_on_dt AS created_on_dt,\n" +
    "wr.changed_on_dt AS changed_on_dt,\n" +
    "NULL AS timezone,\n" +
    "NULL AS exchange_rate,\n" +
    "\n" +
    "concat(wr.datasource_num_id, warehouse_id) AS warehouse_key,\n" +
    "concat(wr.datasource_num_id, picker_id) AS picker_key,\n" +
    "\n" +
    "wellen_id,\n" +
    "wms_wellen_id AS wellen_wms_wellen_id,\n" +
    "wellen_name,\n" +
    "warehouse_id AS wellen_warehouse_id,\n" +
    "wellen_order_max,\n" +
    "wellen_order_min,\n" +
    "product_volume_max AS wellen_product_volume_max,\n" +
    "product_weight_max AS wellen_product_weight_max,\n" +
    "wellen_begin_time,\n" +
    "wellen_end_time,\n" +
    "customer_code AS wellen_customer_code,\n" +
    "picker_id AS wellen_picker_id,\n" +
    "wellen_time,\n" +
    "wellen_space,\n" +
    "wellen_sort,\n" +
    "order_type AS wellen_order_type,\n" +
    "nvl(v_order_type.vl_name, order_type) AS wellen_order_type_val,\n" +
    "picking_mode AS wellen_picking_mode,\n" +
    "nvl(v_picking_mode.vl_name, picking_mode) AS wellen_picking_mode_val,\n" +
    "is_more_box AS wellen_is_more_box,\n" +
    "nvl(v_is_more_box.vl_name, is_more_box) AS wellen_is_more_box_val,\n" +
    "order_advance_pickup AS wellen_order_advance_pickup,\n" +
    "wellen_area,\n" +
    "nvl(v_wellen_area.vl_name, wellen_area) AS wellen_area_val,\n" +
    "wellen_status,\n" +
    "nvl(v_wellen_status.vl_name, wellen_status) AS wellen_status_val,\n" +
    "is_sync_owms AS wellen_is_sync_owms,\n" +
    "nvl(v_is_sync_owms.vl_name, is_sync_owms) AS wellen_is_sync_owms_val,\n" +
    "wellen_code,\n" +
    "picking_sort AS wellen_picking_sort,\n" +
    "nvl(v_picking_sort.vl_name, picking_sort) AS wellen_picking_sort_val,\n" +
    "advance_pickup_time AS wellen_advance_pickup_time,\n" +
    "creater_id AS wellen_creater_id,\n" +
    "create_time AS wellen_create_time,\n" +
    "creator_id AS wellen_creator_id,\n" +
    "isCrossWarehouseA AS wellen_is_cross_warehouse,\n" +
    "isCrossWarehouseValA AS wellen_is_cross_warehouse_val,\n" +
    "date_format(create_time, 'yyyyMMdd') month\n" +
    "FROM (SELECT * FROM ods.WellenRuleTable WHERE data_flag != 'DELETE') wr \n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'order_type' AND vl_datasource_table = 'WellenRuleTable') v_order_type \n" +
    "ON wr.order_type = v_order_type.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'picking_mode' AND vl_datasource_table = 'WellenRuleTable') v_picking_mode \n" +
    "ON wr.picking_mode = v_picking_mode.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'is_more_box' AND vl_datasource_table = 'WellenRuleTable') v_is_more_box \n" +
    "ON wr.is_more_box = v_is_more_box.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'wellen_area' AND vl_datasource_table = 'WellenRuleTable') v_wellen_area \n" +
    "ON wr.wellen_area = v_wellen_area.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'wellen_status' AND vl_datasource_table = 'WellenRuleTable') v_wellen_status\n" +
    "ON wr.wellen_status = v_wellen_status.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'is_sync_owms' AND vl_datasource_table = 'WellenRuleTable') v_is_sync_owms\n" +
    "ON wr.is_sync_owms = v_is_sync_owms.vl_value\n" +
    "\n" +
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'picking_sort' AND vl_datasource_table = 'WellenRuleTable') v_picking_sort\n" +
    "ON wr.picking_sort = v_picking_sort.vl_value\n" +
    "\n"

  val sql2 =
    "LEFT JOIN (SELECT vl_value, vl_name FROM dw.par_val_list \n" +
    "WHERE vl_type = 'is_cross_warehouse' AND vl_datasource_table = 'WellenRuleTable') v_is_cross_warehouse\n" +
    "ON wr.is_cross_warehouse = v_is_cross_warehouse.vl_value"

  def makeSql() = {

    odsTables.map(x => {
      val isCrossWarehouse = if (x.contains("zy_owms")) "NULL" else "is_cross_warehouse"
      val isCrossWarehouseVal = if (x.contains("zy_owms")) "NULL" else "nvl(v_is_cross_warehouse.vl_name, is_cross_warehouse)"

      val sql3 = if (x.contains("zy_owms")) sql else sql + sql2

      sql3.replace("isCrossWarehouseA", isCrossWarehouse).
        replace("isCrossWarehouseValA", isCrossWarehouseVal).
        replace("WellenRuleTable", x)
    })

  }

  val odsTables = Array(
    "gc_owms_au_wellen_rule",
    "gc_owms_cz_wellen_rule",
    "gc_owms_de_wellen_rule",
    "gc_owms_es_wellen_rule",
    "gc_owms_frvi_wellen_rule",
    "gc_owms_it_wellen_rule",
    "gc_owms_jp_wellen_rule",
    "gc_owms_uk_wellen_rule",
    "gc_owms_ukob_wellen_rule",
    "gc_owms_usea_wellen_rule",
    "gc_owms_uswe_wellen_rule",
    "gc_owms_usnb_wellen_rule",
    "gc_owms_usot_wellen_rule",
    "gc_owms_ussc_wellen_rule",
    "zy_owms_au_wellen_rule",
    "zy_owms_cz_wellen_rule",
    "zy_owms_de_wellen_rule",
    "zy_owms_ru_wellen_rule",
    "zy_owms_uk_wellen_rule",
    "zy_owms_usea_wellen_rule",
    "zy_owms_uswe_wellen_rule",
    "zy_owms_ussc_wellen_rule"
  )

  private val systemCodes: Array[String] = Array(
    SystemCodeUtil.GC_OWMS_AU,
    SystemCodeUtil.GC_OWMS_CZ,
    SystemCodeUtil.GC_OWMS_DE,
    SystemCodeUtil.GC_OWMS_ES,
    SystemCodeUtil.GC_OWMS_FRVI,
    SystemCodeUtil.GC_OWMS_IT,
    SystemCodeUtil.GC_OWMS_JP,
    SystemCodeUtil.GC_OWMS_UK,
    SystemCodeUtil.GC_OWMS_UKOB,
    SystemCodeUtil.GC_OWMS_USEA,
    SystemCodeUtil.GC_OWMS_USWE,
    SystemCodeUtil.GC_OWMS_USNB,
    SystemCodeUtil.GC_OWMS_USOT,
    SystemCodeUtil.GC_OWMS_USSC,
    SystemCodeUtil.ZY_OWMS_AU,
    SystemCodeUtil.ZY_OWMS_CZ,
    SystemCodeUtil.ZY_OWMS_DE,
    SystemCodeUtil.ZY_OWMS_RU,
    SystemCodeUtil.ZY_OWMS_UK,
    SystemCodeUtil.ZY_OWMS_USEA,
    SystemCodeUtil.ZY_OWMS_USWE,
    SystemCodeUtil.ZY_OWMS_USSC
  )

  val task = "Dw_fact_wellen_rule"

  val tableName = "fact_wellen_rule"

  def main(args: Array[String]): Unit = {

    val sqlArray: Array[String] = makeSql.map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))

    Dw_fact_common.getRunCode_hive_full_Into(task, tableName, sqlArray, systemCodes)
  }

}
