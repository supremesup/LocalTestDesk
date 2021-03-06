package com.zongteng.ztetl.etl.dw.fact.full.orders

import com.zongteng.ztetl.common.{Dw_dim_common, Dw_fact_common, Dw_par_val_list_cache, SystemCodeUtil}
import com.zongteng.ztetl.util.DateUtil

object DWFactOrdersFull {

  //任务名称(一般同类名)
  private val task = "SPARK_ETL_DW_Fact_Orders"

  //dw层类名
  private val tableName = "fact_orders"

  // 获取当天的时间
  private val nowDate: String = DateUtil.getNowTime()

  private val gc_wms = "select od.row_wid as row_wid\n" +
    "    , od.etl_proc_wid as etl_proc_wid\n" +
    "    , od.w_insert_dt as w_insert_dt\n" +
    "    , od.w_update_dt as w_update_dt\n" +
    "    , od.datasource_num_id as datasource_num_id\n" +
    "    , od.data_flag as data_flag\n" +
    "    , od.integration_id as integration_id\n" +
    "    , od.created_on_dt as created_on_dt\n" +
    "    , od.changed_on_dt as changed_on_dt\n" +
    "    , null as timezone\n" +
    "    , null as exchange_rate\n" +
    "    , concat(od.datasource_num_id, od.customer_id) as customer_key\n" +
    "    , concat(od.datasource_num_id, od.warehouse_id) as warehouse_key\n" +
    "    , concat(od.datasource_num_id, od.to_warehouse_id) as to_warehouse_key\n" +
    "    , sm.row_wid as sm_key\n" +
    "    , od.order_id as order_id\n" +
    "    , od.order_code as order_code\n" +
    "    , od.reference_no as order_reference_no\n" +
    "    , od.customer_id as order_customer_id\n" +
    "    , od.customer_code as order_customer_code\n" +
    "    , od.platform as order_platform\n" +
    "    , od.order_platform_type as order_platform_type\n" +
    "    , od.create_type as order_create_type\n" +
    "    , od.warehouse_id as order_warehouse_id\n" +
    "    , od.to_warehouse_id as order_to_warehouse_id\n" +
    "    , od.is_oda as order_is_oda\n" +
    "    , concat(pv1.vl_name, od.is_oda) as order_is_oda_val\n" +
    "    , od.oda_type as order_oda_type\n" +
    "    , od.is_signature as order_is_signature\n" +
    "    , concat(pv2.vl_name, od.is_signature) as order_is_signature_val\n" +
    "    , od.is_insurance as order_is_insurance\n" +
    "    , concat(pv3.vl_name, od.is_insurance) as order_is_insurance_val\n" +
    "    , od.insurance_value as order_insurance_value\n" +
    "    , od.order_type as order_type\n" +
    "    , concat(pv4.vl_name, od.order_type) as order_type_val\n" +
    "    , od.country_code as order_country_code\n" +
    "    , od.sm_code as order_sm_code\n" +
    "    , od.order_advance_pickup as order_advance_pickup\n" +
    "    , od.parcel_declared_value as order_parcel_declared_value\n" +
    "    , od.shipping_fee_estimate as order_shipping_fee_estimate\n" +
    "    , od.currency_code as order_currency_code\n" +
    "    , od.parcel_contents as order_parcel_contents\n" +
    "    , od.parcel_quantity as order_parcel_quantity\n" +
    "    , od.order_status as order_status\n" +
    "    , concat(pv5.vl_name, od.order_status) as order_status_val\n" +
    "    , od.problem_status as order_problem_status\n" +
    "    , concat(pv6.vl_name, od.problem_status) as order_problem_status_val\n" +
    "    , od.underreview_status as order_underreview_status\n" +
    "    , concat(pv7.vl_name, od.underreview_status) as order_underreview_status_val\n" +
    "    , od.upload_express_status as order_upload_express_status\n" +
    "    , concat(pv8.vl_name, od.upload_express_status) as order_upload_express_status_val\n" +
    "    , od.anew_express_status as order_anew_express_status\n" +
    "    , concat(pv9.vl_name, od.anew_express_status) as order_anew_express_status_val\n" +
    "    , od.intercept_status as order_intercept_status\n" +
    "    , concat(pv10.vl_name, od.intercept_status) as order_intercept_status_val\n" +
    "    , od.sync_cost_status as order_sync_cost_status\n" +
    "    , concat(pv11.vl_name, od.sync_cost_status) as order_sync_cost_status_val\n" +
    "    , od.sync_status as order_sync_status\n" +
    "    , concat(pv12.vl_name, od.sync_status) as order_sync_status_val\n" +
    "    , od.order_waiting_status as order_waiting_status\n" +
    "    , concat(pv13.vl_name, od.sync_status) as order_waiting_status_val\n" +
    "    , od.order_picking_status as order_picking_status\n" +
    "    , concat(pv14.vl_name, od.order_picking_status) as order_picking_status_val\n" +
    "    , od.order_charge_status as order_charge_status\n" +
    "    , concat(pv15.vl_name, od.order_charge_status) as order_charge_status_val\n" +
    "    , od.print_sort as order_print_sort\n" +
    "    , od.print_quantity as order_print_quantity\n" +
    "    , od.add_time as order_add_time\n" +
    "    , od.update_time as order_update_time\n" +
    "    , od.order_paydate as order_paydate\n" +
    "    , od.order_pick_type as order_pick_type\n" +
    "    , concat(pv16.vl_name, od.order_pick_type) as order_pick_type_val\n" +
    "    , od.picking_basket as order_picking_basket\n" +
    "    , od.picker_id as order_picker_id\n" +
    "    , od.remark as order_remark\n" +
    "    , od.site_id as order_site_id\n" +
    "    , od.seller_id as order_seller_id\n" +
    "    , od.buyer_id as order_buyer_id\n" +
    "    , od.buyer_name as order_buyer_name\n" +
    "    , od.buyer_mail as order_buyer_mail\n" +
    "    , od.sync_service_status as order_sync_service_status\n" +
    "    , od.sync_count as order_sync_count\n" +
    "    , od.sc_id as order_sc_id\n" +
    "    , od.sync_required_sign as order_sync_required_sign\n" +
    "    , od.sync_wms_status as order_sync_wms_status\n" +
    "    , od.sync_wms_sign as order_sync_wms_sign\n" +
    "    , od.sync_wms_time as order_sync_wms_time\n" +
    "    , od.operator_note as order_operator_note\n" +
    "    , od.order_exception_status as order_exception_status\n" +
    "    , concat(pv17.vl_name, od.order_exception_status) as order_exception_status_val\n" +
    "    , od.order_exception_type as order_exception_type\n" +
    "    , concat(pv18.vl_name, od.order_exception_type) as order_exception_type_val\n" +
    "    , od.order_exception_sub_type as order_exception_sub_type\n" +
    "    , concat(pv19.vl_name, od.order_exception_sub_type) as order_exception_sub_type_val\n" +
    "    , od.shared_sign as order_shared_sign\n" +
    "    , od.is_residential as order_is_residential\n" +
    "    , concat(pv20.vl_name, od.is_residential) as order_is_residential_val\n" +
    "    , od.is_fba as order_is_fba\n" +
    "    , concat(pv21.vl_name, od.is_fba) as order_is_fba_val\n" +
    "    , od.outbound_time as order_outbound_time\n" +
    "    , od.is_more_box as order_is_more_box\n" +
    "    , concat(pv22.vl_name, od.is_more_box) as order_is_more_box_val\n" +
    "    , od.is_attachment as order_is_attachment\n" +
    "    , concat(pv23.vl_name, od.is_attachment) as order_is_attachment_val\n" +
    "    , od.address_valid_status as order_address_valid_status\n" +
    "    , od.o_timestamp as order_o_timestamp\n" +
    "    , od.age_detection as order_age_detection\n" +
    "    , od.payment_time as order_payment_time\n" +
    "    , od.is_recommend as order_is_recommend\n" +
    "    , concat(pv24.vl_name, od.is_recommend) as order_is_recommend_val\n" +
    "    , od.oms_date_create as order_oms_date_create\n" +
    "    , od.pre_delivery_time as order_pre_delivery_time\n" +
    "    , od.sc_currency_code as order_sc_currency_code\n" +
    "    , od.is_flow_volume as order_is_flow_volume\n" +
    "    , concat(pv25.vl_name, od.is_flow_volume) as order_is_flow_volume_val\n" +
    "    , od.is_cross_warehouse as order_is_cross_warehouse\n" +
    "    , concat(pv26.vl_name, od.is_cross_warehouse) as order_is_cross_warehouse_val\n" +
    "    , od.is_truck_service as order_is_truck_service\n" +
    "    , concat(pv27.vl_name, od.is_truck_service) as order_is_truck_service_val\n" +
    "    , od.new_order_type as order_new_order_type\n" +
    "    , concat(pv28.vl_name, od.new_order_type) as order_new_order_type_val\n" +
    "    , od.design_batch_status as order_design_batch_status\n" +
    "    , concat(pv29.vl_name, od.design_batch_status) as order_design_batch_status_val\n" +
    "    , date_format(od.add_time, 'yyyyMM') as month\n" +
    "from (select * from ods.gc_wms_orders WHERE data_flag != 'DELETE') as od\n" +
    s"left join ${Dw_dim_common.getDimSql("gc_wms_shipping_method","sm")} on sm.sm_code=od.sm_code\n" +
    "left join dw.par_val_list as pv1 on pv1.row_wid= concat(od.datasource_num_id,'_', 'is_oda','_', od.is_oda,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv2 on pv2.row_wid= concat(od.datasource_num_id,'_', 'is_signature','_', od.is_signature,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv3 on pv3.row_wid= concat(od.datasource_num_id,'_', 'is_insurance','_', od.is_insurance,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv4 on pv4.row_wid= concat(od.datasource_num_id,'_', 'order_type','_', od.order_type,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv5 on pv5.row_wid= concat(od.datasource_num_id,'_', 'order_status','_', od.order_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv6 on pv6.row_wid= concat(od.datasource_num_id,'_', 'problem_status','_', od.problem_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv7 on pv7.row_wid= concat(od.datasource_num_id,'_', 'underreview_status','_', od.underreview_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv8 on pv8.row_wid= concat(od.datasource_num_id,'_', 'upload_express_status','_', od.upload_express_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv9 on pv9.row_wid= concat(od.datasource_num_id,'_', 'anew_express_status','_', od.anew_express_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv10 on pv10.row_wid= concat(od.datasource_num_id,'_', 'intercept_status','_', od.intercept_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv11 on pv11.row_wid= concat(od.datasource_num_id,'_', 'sync_cost_status','_', od.sync_cost_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv12 on pv12.row_wid= concat(od.datasource_num_id,'_', 'sync_status','_', od.sync_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv13 on pv13.row_wid= concat(od.datasource_num_id,'_', 'order_waiting_status','_', od.order_waiting_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv14 on pv14.row_wid= concat(od.datasource_num_id,'_', 'order_picking_status','_', od.order_picking_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv15 on pv15.row_wid= concat(od.datasource_num_id,'_', 'order_charge_status','_', od.order_charge_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv16 on pv16.row_wid= concat(od.datasource_num_id,'_', 'order_pick_type','_', od.order_pick_type,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv17 on pv17.row_wid= concat(od.datasource_num_id,'_', 'order_exception_status','_', od.order_exception_status,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv18 on pv18.row_wid= concat(od.datasource_num_id,'_', 'order_exception_type','_', od.order_exception_type,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv19 on pv19.row_wid= concat(od.datasource_num_id,'_', 'order_exception_sub_type','_', od.order_exception_sub_type,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv20 on pv20.row_wid= concat(od.datasource_num_id,'_', 'is_residential','_', od.is_residential,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv21 on pv21.row_wid= concat(od.datasource_num_id,'_', 'is_fba','_', od.is_fba,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv22 on pv22.row_wid= concat(od.datasource_num_id,'_', 'is_more_box','_', od.is_more_box,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv23 on pv23.row_wid= concat(od.datasource_num_id,'_', 'is_attachment','_', od.is_attachment,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv24 on pv24.row_wid= concat(od.datasource_num_id,'_', 'is_recommend','_', od.is_recommend,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv25 on pv25.row_wid= concat(od.datasource_num_id,'_', 'is_flow_volume','_', od.is_flow_volume,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv26 on pv26.row_wid= concat(od.datasource_num_id,'_', 'is_cross_warehouse','_', od.is_cross_warehouse,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv27 on pv27.row_wid= concat(od.datasource_num_id,'_', 'is_truck_service','_', od.is_truck_service,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv28 on pv28.row_wid= concat(od.datasource_num_id,'_', 'new_order_type','_', od.new_order_type,'_', 'gc_wms_orders')\n" +
    "left join dw.par_val_list as pv29 on pv29.row_wid= concat(od.datasource_num_id,'_', 'design_batch_status','_', od.design_batch_status,'_', 'gc_wms_orders')"

    private val zy_wms = "select od.row_wid as row_wid\n" +
      "    , od.etl_proc_wid as etl_proc_wid\n" +
      "    , od.w_insert_dt as w_insert_dt\n" +
      "    , od.w_update_dt as w_update_dt\n" +
      "    , od.datasource_num_id as datasource_num_id\n" +
      "    , od.data_flag as data_flag\n" +
      "    , od.integration_id as integration_id\n" +
      "    , od.created_on_dt as created_on_dt\n" +
      "    , od.changed_on_dt as changed_on_dt\n" +
      "    , null as timezone\n" +
      "    , null as exchange_rate\n" +
      "    , concat(od.datasource_num_id, od.customer_id) as customer_key\n" +
      "    , concat(od.datasource_num_id, od.warehouse_id) as warehouse_key\n" +
      "    , concat(od.datasource_num_id, od.to_warehouse_id) as to_warehouse_key\n" +
      "    , sm.row_wid as sm_key\n" +
      "    , od.order_id as order_id\n" +
      "    , od.order_code as order_code\n" +
      "    , od.reference_no as order_reference_no\n" +
      "    , od.customer_id as order_customer_id\n" +
      "    , od.customer_code as order_customer_code\n" +
      "    , od.platform as order_platform\n" +
      "    , od.order_platform_type as order_platform_type\n" +
      "    , od.create_type as order_create_type\n" +
      "    , od.warehouse_id as order_warehouse_id\n" +
      "    , od.to_warehouse_id as order_to_warehouse_id\n" +
      "    , od.is_oda as order_is_oda\n" +
      "    , concat(pv1.vl_name, od.is_oda) as order_is_oda_val\n" +
      "    , od.oda_type as order_oda_type\n" +
      "    , od.is_signature as order_is_signature\n" +
      "    , concat(pv2.vl_name, od.is_signature) as order_is_signature_val\n" +
      "    , od.is_insurance as order_is_insurance\n" +
      "    , concat(pv3.vl_name, od.is_insurance) as order_is_insurance_val\n" +
      "    , od.insurance_value as order_insurance_value\n" +
      "    , od.order_type as order_type\n" +
      "    , concat(pv4.vl_name, od.order_type) as order_type_val\n" +
      "    , od.country_code as order_country_code\n" +
      "    , od.sm_code as order_sm_code\n" +
      "    , od.order_advance_pickup as order_advance_pickup\n" +
      "    , od.parcel_declared_value as order_parcel_declared_value\n" +
      "    , od.shipping_fee_estimate as order_shipping_fee_estimate\n" +
      "    , od.currency_code as order_currency_code\n" +
      "    , od.parcel_contents as order_parcel_contents\n" +
      "    , od.parcel_quantity as order_parcel_quantity\n" +
      "    , od.order_status as order_status\n" +
      "    , concat(pv5.vl_name, od.order_status) as order_status_val\n" +
      "    , od.problem_status as order_problem_status\n" +
      "    , concat(pv6.vl_name, od.problem_status) as order_problem_status_val\n" +
      "    , od.underreview_status as order_underreview_status\n" +
      "    , concat(pv7.vl_name, od.underreview_status) as order_underreview_status_val\n" +
      "    , od.upload_express_status as order_upload_express_status\n" +
      "    , concat(pv8.vl_name, od.upload_express_status) as order_upload_express_status_val\n" +
      "    , od.anew_express_status as order_anew_express_status\n" +
      "    , concat(pv9.vl_name, od.anew_express_status) as order_anew_express_status_val\n" +
      "    , od.intercept_status as order_intercept_status\n" +
      "    , concat(pv10.vl_name, od.intercept_status) as order_intercept_status_val\n" +
      "    , od.sync_cost_status as order_sync_cost_status\n" +
      "    , concat(pv11.vl_name, od.sync_cost_status) as order_sync_cost_status_val\n" +
      "    , od.sync_status as order_sync_status\n" +
      "    , concat(pv12.vl_name, od.sync_status) as order_sync_status_val\n" +
      "    , od.order_waiting_status as order_waiting_status\n" +
      "    , concat(pv13.vl_name, od.sync_status) as order_waiting_status_val\n" +
      "    , od.order_picking_status as order_picking_status\n" +
      "    , concat(pv14.vl_name, od.order_picking_status) as order_picking_status_val\n" +
      "    , od.order_charge_status as order_charge_status\n" +
      "    , concat(pv15.vl_name, od.order_charge_status) as order_charge_status_val\n" +
      "    , od.print_sort as order_print_sort\n" +
      "    , od.print_quantity as order_print_quantity\n" +
      "    , od.add_time as order_add_time\n" +
      "    , od.update_time as order_update_time\n" +
      "    , od.order_paydate as order_paydate\n" +
      "    , od.order_pick_type as order_pick_type\n" +
      "    , concat(pv16.vl_name, od.order_pick_type) as order_pick_type_val\n" +
      "    , od.picking_basket as order_picking_basket\n" +
      "    , od.picker_id as order_picker_id\n" +
      "    , od.remark as order_remark\n" +
      "    , od.site_id as order_site_id\n" +
      "    , od.seller_id as order_seller_id\n" +
      "    , od.buyer_id as order_buyer_id\n" +
      "    , od.buyer_name as order_buyer_name\n" +
      "    , od.buyer_mail as order_buyer_mail\n" +
      "    , od.sync_service_status as order_sync_service_status\n" +
      "    , od.sync_count as order_sync_count\n" +
      "    , od.sc_id as order_sc_id\n" +
      "    , od.sync_required_sign as order_sync_required_sign\n" +
      "    , od.sync_wms_status as order_sync_wms_status\n" +
      "    , od.sync_wms_sign as order_sync_wms_sign\n" +
      "    , od.sync_wms_time as order_sync_wms_time\n" +
      "    , od.operator_note as order_operator_note\n" +
      "    , od.order_exception_status as order_exception_status\n" +
      "    , concat(pv17.vl_name, od.order_exception_status) as order_exception_status_val\n" +
      "    , od.order_exception_type as order_exception_type\n" +
      "    , concat(pv18.vl_name, od.order_exception_type) as order_exception_type_val\n" +
      "    , od.order_exception_sub_type as order_exception_sub_type\n" +
      "    , concat(pv19.vl_name, od.order_exception_sub_type) as order_exception_sub_type_val\n" +
      "    , od.shared_sign as order_shared_sign\n" +
      "    , od.is_residential as order_is_residential\n" +
      "    , concat(pv20.vl_name, od.is_residential) as order_is_residential_val\n" +
      "    , od.is_fba as order_is_fba\n" +
      "    , concat(pv21.vl_name, od.is_fba) as order_is_fba_val\n" +
      "    , od.outbound_time as order_outbound_time\n" +
      "    , od.is_more_box as order_is_more_box\n" +
      "    , concat(pv22.vl_name, od.is_more_box) as order_is_more_box_val\n" +
      "    , od.is_attachment as order_is_attachment\n" +
      "    , concat(pv23.vl_name, od.is_attachment) as order_is_attachment_val\n" +
      "    , od.address_valid_status as order_address_valid_status\n" +
      "    , od.o_timestamp as order_o_timestamp\n" +
      "    , od.age_detection as order_age_detection\n" +
      "    , od.payment_time as order_payment_time\n" +
      "    , null as order_is_recommend\n" +
      "    , null as order_is_recommend_val\n" +
      "    , null as order_oms_date_create\n" +
      "    , od.pre_delivery_time as order_pre_delivery_time\n" +
      "    , null as order_sc_currency_code\n" +
      "    , null as order_is_flow_volume\n" +
      "    , null as order_is_flow_volume_val\n" +
      "    , null as order_is_cross_warehouse\n" +
      "    , null as order_is_cross_warehouse_val\n" +
      "    , null as order_is_truck_service\n" +
      "    , null as order_is_truck_service_val\n" +
      "    , null as order_new_order_type\n" +
      "    , null as order_new_order_type_val\n" +
      "    , null as order_design_batch_status\n" +
      "    , null as order_design_batch_status_val\n" +
      "    , date_format(od.add_time, 'yyyyMM') as month\n" +
      "    from (select * from ods.zy_wms_orders WHERE data_flag != 'DELETE') as od\n" +
      s"left join ${Dw_dim_common.getDimSql("zy_wms_shipping_method","sm")} on sm.sm_code=od.sm_code\n" +
      "left join dw.par_val_list as pv1 on pv1.row_wid= concat(od.datasource_num_id,'_', 'is_oda','_', od.is_oda,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv2 on pv2.row_wid= concat(od.datasource_num_id,'_', 'is_signature','_', od.is_signature,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv3 on pv3.row_wid= concat(od.datasource_num_id,'_', 'is_insurance','_', od.is_insurance,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv4 on pv4.row_wid= concat(od.datasource_num_id,'_', 'order_type','_', od.order_type,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv5 on pv5.row_wid= concat(od.datasource_num_id,'_', 'order_status','_', od.order_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv6 on pv6.row_wid= concat(od.datasource_num_id,'_', 'problem_status','_', od.problem_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv7 on pv7.row_wid= concat(od.datasource_num_id,'_', 'underreview_status','_', od.underreview_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv8 on pv8.row_wid= concat(od.datasource_num_id,'_', 'upload_express_status','_', od.upload_express_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv9 on pv9.row_wid= concat(od.datasource_num_id,'_', 'anew_express_status','_', od.anew_express_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv10 on pv10.row_wid= concat(od.datasource_num_id,'_', 'intercept_status','_', od.intercept_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv11 on pv11.row_wid= concat(od.datasource_num_id,'_', 'sync_cost_status','_', od.sync_cost_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv12 on pv12.row_wid= concat(od.datasource_num_id,'_', 'sync_status','_', od.sync_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv13 on pv13.row_wid= concat(od.datasource_num_id,'_', 'order_waiting_status','_', od.order_waiting_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv14 on pv14.row_wid= concat(od.datasource_num_id,'_', 'order_picking_status','_', od.order_picking_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv15 on pv15.row_wid= concat(od.datasource_num_id,'_', 'order_charge_status','_', od.order_charge_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv16 on pv16.row_wid= concat(od.datasource_num_id,'_', 'order_pick_type','_', od.order_pick_type,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv17 on pv17.row_wid= concat(od.datasource_num_id,'_', 'order_exception_status','_', od.order_exception_status,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv18 on pv18.row_wid= concat(od.datasource_num_id,'_', 'order_exception_type','_', od.order_exception_type,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv19 on pv19.row_wid= concat(od.datasource_num_id,'_', 'order_exception_sub_type','_', od.order_exception_sub_type,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv20 on pv20.row_wid= concat(od.datasource_num_id,'_', 'is_residential','_', od.is_residential,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv21 on pv21.row_wid= concat(od.datasource_num_id,'_', 'is_fba','_', od.is_fba,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv22 on pv22.row_wid= concat(od.datasource_num_id,'_', 'is_more_box','_', od.is_more_box,'_', 'zy_wms_orders')\n" +
      "left join dw.par_val_list as pv23 on pv23.row_wid= concat(od.datasource_num_id,'_', 'is_attachment','_', od.is_attachment,'_', 'zy_wms_orders')"

  def main(args: Array[String]): Unit = {
    val sqlArray: Array[String] = Array(gc_wms, zy_wms).map(_.replaceAll("dw.par_val_list", Dw_par_val_list_cache.TEMP_PAR_VAL_LIST_NAME))
    Dw_fact_common.getRunCode_hive_full_Into(task, tableName, sqlArray, Array(SystemCodeUtil.GC_WMS, SystemCodeUtil.ZY_WMS))
  }

}
