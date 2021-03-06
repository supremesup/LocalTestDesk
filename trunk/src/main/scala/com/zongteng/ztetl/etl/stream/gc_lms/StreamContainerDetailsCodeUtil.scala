package com.zongteng.ztetl.etl.stream.gc_lms

import java.io
import java.sql.{Connection, PreparedStatement}
import java.text.SimpleDateFormat
import java.util.Date
import com.zongteng.ztetl.api.SparkKafkaCon
import com.zongteng.ztetl.common.jdbc.JDBCUtil
import com.zongteng.ztetl.common.{MysqlDatabase, SystemCodeUtil}
import com.zongteng.ztetl.entity.common.InsertModel
import com.zongteng.ztetl.etl.stream.Stream_json_util
import com.zongteng.ztetl.etl.stream.gc_wms_orders.fact.{Orders_stream_allow_mysql_table, StreamTypeParaUtil}
import com.zongteng.ztetl.util.DateUtil
import org.apache.commons.lang3.StringUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, OffsetRange}
import scala.collection.mutable.ListBuffer

object StreamContainerDetailsCodeUtil {

  /**
    * 生成实时导入ods层代码的工具类
    *
    * @param appName        作业名称
    * @param interval       消费时间间隔
    * @param topicNames     订阅消费的kafka中的主题
    * @param group_Id       消费组
    * @param mysqlDatabases mysql数据库名称
    */
  def getRunCode(appName: String, interval: Int, topicNames: Array[String], group_Id: String, mysqlDatabases: Array[String]) = {

    // 1、获取streaming对象 kafakStream
    val sparkStreamAndKafakStream: (StreamingContext, InputDStream[ConsumerRecord[String, String]]) = SparkKafkaCon.getConnectKafka(appName, interval, topicNames, group_Id)

    val sparkStream: StreamingContext = sparkStreamAndKafakStream._1
    val kafkakStream: InputDStream[ConsumerRecord[String, String]] = sparkStreamAndKafakStream._2

    // 2、记录日志开始
    //val task: String = Log.start(appName)
    try {

      kafkakStream.foreachRDD((rdd: RDD[ConsumerRecord[String, String]]) => {

        println("isEmpty == "  + rdd.isEmpty())

        if (!rdd.isEmpty()) {

          // 获取对应kafka消费的偏移量
          val offset: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

          // filter作用：只消费指定数据库指定表的数据
          rdd.filter((e: ConsumerRecord[String, String]) => checkJsonStr(mysqlDatabases, e.value())).foreachPartition((cols: Iterator[ConsumerRecord[String, String]]) => {

            val connection: Connection = JDBCUtil.getConnection(JDBCUtil.jdbc_zt75_streaming)

            connection.setAutoCommit(false)

            val delSql = "DELETE FROM stream_gc_lms_container_details WHERE row_wid = ?"
            val insertSql = "INSERT INTO stream_gc_lms_container_details VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

            val delstatement: PreparedStatement = connection.prepareStatement(delSql)
            val insstatement: PreparedStatement = connection.prepareStatement(insertSql)
            try {
              cols.foreach((e: ConsumerRecord[String, String]) => {

                // 获取对应的row数据
                val model: InsertModel = Stream_json_util.getInsertObject(e.value())

                // 获取数据来源mysql那个库
                val mysqlDatabase: String = model.database
                val table: String = model.table

                // 数据来源mysql那个库的简称
                val dsShortName: String = MysqlDatabase.shortName(mysqlDatabase)
                val odsTable: String = "stream_" + dsShortName + "_" + table

                val opr_type: String = model.`type`

                println("odsTable == " + odsTable)

                odsTable match {
                  case "stream_gc_lms_au_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_au.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_AU, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_cz_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_cz.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_CZ, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_es_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_es.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_ES, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_frvi_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_frvi.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_FRVI, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_it_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_it.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_IT, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_uk_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_uk.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_UK, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_uswe_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_uswe.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_USWE, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_usea_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_usea.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_USEA, delstatement, insstatement, connection)
                  }
                  case "stream_gc_lms_ussc_container_details" => {
                    import com.zongteng.ztetl.entity.gc_lms.gc_lms_ussc.ContainerDetails
                    fillPlaceholder(model, classOf[ContainerDetails], SystemCodeUtil.GC_LMS_USSC, delstatement, insstatement, connection)
                  }
                }
              })

            } catch {
              case e: Exception => {
                if (connection != null) connection.rollback() // 回滚
                throw e
              }
            } finally {
              JDBCUtil.close(null, delstatement, connection)
              JDBCUtil.close(null, insstatement, connection)
            }

          })

          // 提交对应的kafka消费的偏移量
          kafkakStream.asInstanceOf[CanCommitOffsets].commitAsync(offset)
        }
      })

      //Log.end(task)
    } catch {
      case e: Exception => e.getMessage
        kafkakStream.stop()
      //Log.end(task)
    }

    sparkStream.start()
    sparkStream.awaitTermination()
  }

  /**
    * 填充占位符
    *
    * clazz没有用到，直接用classOf[GcLmsContainerDetails]
    */
  def fillPlaceholder(model: InsertModel, clazz: Class[_], systemCode: String, delstatement: PreparedStatement, insstatement: PreparedStatement, connection: Connection) = {

    import com.zongteng.ztetl.entity.common.GcLmsContainerDetails

    val dsShortName: String = MysqlDatabase.shortName(model.database)
    val odsTable: String = "stream_" + dsShortName + "_" + model.table

    println(s"==============${odsTable}=====进入=================")

    val serializables: ListBuffer[io.Serializable] = Stream_json_util.getCaseClass(model, classOf[GcLmsContainerDetails])

    for (i <- 0 until serializables.length) {
      val caseClass: GcLmsContainerDetails = serializables(i).asInstanceOf[GcLmsContainerDetails]

      delstatement.setString(1, systemCode + caseClass.container_details_id)

      insstatement.setString(1, systemCode + caseClass.container_details_id)
      insstatement.setString(2, systemCode)
      insstatement.setString(3, caseClass.container_details_id)
      insstatement.setString(4, caseClass.container_id)
      insstatement.setString(5, caseClass.container_code)
      insstatement.setString(6, caseClass.order_number)
      insstatement.setString(7, caseClass.tracking_number)
      insstatement.setString(8, caseClass.channel_code)
      insstatement.setString(9, StreamTypeParaUtil.nvlDateTime(caseClass.loader_time))
      insstatement.setString(10, StreamTypeParaUtil.nvlDateTime(caseClass.shipper_time))
      insstatement.setString(11, DateUtil.getNow())
      insstatement.setString(12, model.`type`)

      if (dayNowSub(caseClass.shipper_time, 10) && executeSql(delstatement, insstatement, model.`type`)) {
        connection.commit()
        println(s"${odsTable} == " + caseClass)
        println(s"${odsTable}=====提交成功=================")
      }
    }

  }

  /**
    * add_time和现在相差几天，如果在dayNum之内，返回true，否则返回false
    * @param shipper_time
    * @return
    */
  def dayNowSub(shipper_time: String, dayNum: Int) = {

    var day: Int = 1000

    if (StreamTypeParaUtil.nvlDateTime(shipper_time) != null && shipper_time.length >= 10 && shipper_time.split("-").length == 3) {
      val format = new SimpleDateFormat("yyyy-MM-dd")

      val now: String = DateUtil.format(new Date(), "yyyy-MM-dd")
      val now_time = format.parse(now).getTime
      val add_time2 = format.parse(shipper_time.substring(0, 10)).getTime

      day = ((now_time - add_time2) / 1000 / 60 / 60 / 24).toInt
    }

    day < dayNum
  }

  /**
    * 执行sql，如果执行成功返回true，执行失败返回false
    * @param delstatement
    * @param insstatement
    * @param opr_type
    * @return
    */
  def executeSql(delstatement: PreparedStatement, insstatement: PreparedStatement, opr_type: String) = {

    var result: Boolean = false

    if ("UPDATE".equalsIgnoreCase(opr_type)) {
      delstatement.executeUpdate()
      insstatement.executeUpdate()
      result = true
    } else if ("DELETE".equalsIgnoreCase(opr_type)) {
      delstatement.executeUpdate()
      result = true
    } else if ("INSERT".equalsIgnoreCase(opr_type)) {
      delstatement.executeUpdate()
      insstatement.executeUpdate()
      result = true
    }

    result
  }

  def checkJsonStr(mysqlDatabases: Array[String], jsonStr: String) = {

    var result = false

    if (StringUtils.isNotBlank(jsonStr)) {

      // json字符串转为样例类
      val model: InsertModel = Stream_json_util.getInsertObject(jsonStr)

      //println(model.database)

      // 获取数据来源mysql那个库
      val mysqlDatabase: String = model.database
      val table: String = model.table

      // 数据来源mysql那个库的简称
      val dsShortName: String = MysqlDatabase.shortName(mysqlDatabase)
      val odsTable: String = "stream_" + dsShortName + "_" + table

      // 允许通过的表
      val allow_mysql_table: Array[String] = Orders_stream_allow_mysql_table.table

      // 判断数据来源的库
      if (mysqlDatabases.contains(model.database) && allow_mysql_table.contains(odsTable)) {
        result = true
      }

      println("=====odsTable=======  " + odsTable)
      if (odsTable.equals("stream_gc_lms_au_container_details")) {
        println("=====odsTable=======  " + odsTable)
      }

    }

    result
  }

}
