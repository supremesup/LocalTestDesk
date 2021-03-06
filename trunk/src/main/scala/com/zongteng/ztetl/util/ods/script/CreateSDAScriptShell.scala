package com.zongteng.ztetl.util.ods.script

import java.io.FileOutputStream
import java.sql.{Connection, ResultSet, Statement}

import com.zongteng.ztetl.entity.common.{AssistParameter, MysqlOdsScript}
import com.zongteng.ztetl.util.Theme
import org.apache.commons.lang3.StringUtils

import scala.collection.mutable.ListBuffer

/**
  * 事实表
  */
object CreateSDAScriptShell {

  val excel_path = "C:\\Users\\sxzhu\\Desktop\\自动化\\ODS自动化脚本1.xlsx"

  // 脚本输出的路径，指定存放到那个目录就好，文件名有对应的命名方法已经设置好（记住填上路径）
  val theme = "5、transfer"
  val base_path = s"E:\\纵腾\\JTYYB\\CDHProject\\Delivery\\E 开发实现\\SourceCode\\etl\\5、transfer\\ods"
  val ddl_path = s"${base_path}\\fact_ddl\\" // 最后要加\\
  val sqoop_path = s"${base_path}\\fact_sqoop\\" // 最后要加\\

  val azkaban_path = "E:\\纵腾\\JTYYB\\CDHProject\\Delivery\\E 开发实现\\SourceCode\\etl\\5、transfer\\ods\\fact_azkaban\\init"

  /**
    * 事实表(fact)才会生成，维度表不会生成
    * 先false执行一遍程序，生成对应的样例类；再true执行一遍程序，生成对应的配置信息。
    * 因为生成对应的配置信息需要样例类的存在
    */
  val isCreateStream = true

  // 主题（确定导的主题）
  var caseClass_theme: String = Theme.PICKING


  def outputSqoopScript() = {
    AzkabanScript.outputAzkabanScript(azkaban_path, ods_table)
  }

  // 将sqoop脚本输出到指定的目录
  def outputSqoopScript(sqoopScript: ListBuffer[String]) = {
    val sqoop_file_path: String = sqoop_path + "sqoop_ods_" + ods_table + "_full.sh"
    println("sqoop路径：" + sqoop_file_path)
    var stream: FileOutputStream = null
    try {
      stream = new FileOutputStream(sqoop_file_path)
      sqoopScript.foreach((x: String) => {
        stream.write(x.getBytes)
        stream.write("\r\n".getBytes()) // 换行
      })
    } catch {
      case e: Exception => println("sqoop_io异常：检查目录是否存在")
    } finally {
      stream.close()
    }
  }

  // 将ddl脚本输出到指定的目录
  def outputDdlScript(ddlScript: List[String]) = {
    val ddl_file_path: String = ddl_path + "ddl_" + ods_table + ".sh"
    println("ddl路径：" + ddl_file_path)

    // 打印建表的脚本的处理
    ddlScript.foreach(println(_))

    var stream: FileOutputStream = null
    try {
      stream = new FileOutputStream(ddl_file_path)
      ddlScript.foreach((x: String) => {
        stream.write(x.getBytes)
        stream.write("\r\n".getBytes()) // 换行
      })
    } catch {
      case e: Exception => println("ddl_io异常：检查目录是否存在")
    } finally {
      stream.close()
    }
  }


  def main(args: Array[String]): Unit = {
    var excel_num = 1

    SqoopScript.getInfoByExel(excel_path).foreach((mysqlOdsScript: MysqlOdsScript) => {

      evalVal(mysqlOdsScript)

      println("=================================Sqoop===" + ods_table + "=START==========================================================")
      connection = SqoopScript.getJDBCConnection(excel_num + 1, mysqlOdsScript.mysqlDataSource, connect, username, password)
      //connection =SqoopScript.getConnection(excel_num+1,mysqlOdsScript.mysqlDataSource)
      println("开启数据库连接 == " + connection)
      val sqoopScript: ListBuffer[String] = generateSqoopScript()
      checkFiledNum()
      outputSqoopScript(sqoopScript)
      println("=================================Sqoop===" + ods_table + "=END==========================================================")

      println("++++++++++++++++++++++++++++++++++Ddl+++" + ods_table + "+START++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      val ddlScript: List[String] = DdlScript.makeCreateHiveTableSql(ods_table, hql_filed_List.toList, tableType, mysqlFiled.toList, hbase_cf)
      outputDdlScript(ddlScript)
      println("++++++++++++++++++++++++++++++++++Ddl+++" + ods_table + "+END++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

      println("++++++++++++++++++++++++++++++++++Azkaban+++" + ods_table + "+START++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      outputSqoopScript()
      println("++++++++++++++++++++++++++++++++++Azkaban+++" + ods_table + "+START++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

      if ("fact".equalsIgnoreCase(tableType)) {
        println("++++++++++++++++++++++++++++++++++CaseClass+++" + ods_table + "+START++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        OdsStreamScript.outputCaseClassScript(mysqlDataSource, mysqlTable, mysqlFiled)
        println("++++++++++++++++++++++++++++++++++CaseClass+++" + ods_table + "+START++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

        println("++++++++++++++++++++++++++++++++++AssistParameterUtilScript+++" + ods_table + "+START++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        outPutAssistParameterUtilScript
        println("++++++++++++++++++++++++++++++++++AssistParameterUtilScript+++" + ods_table + "+START++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      }

      odsTables += ods_table

      excel_num += 1
    })


    AllowMysqlTableScript.outPutAllowMysqlTableScript(odsTables.distinct.toList, caseClass_theme)

    println("一共生成了" + (excel_num - 1) + "个脚本")
    println("已经存在" + OdsStreamScript.existNum + "个样例类")
    println("一共新建" + OdsStreamScript.createNum + "个样例类")
    println("一共生成了" + AssistParameterUtilScript.recordNum + "个的实时配置信息")
    println("AllowMysqlTableScript一共涉及" + odsTables.distinct.size + "张表")
  }

  private def outPutAssistParameterUtilScript = {

    try {
      if (isCreateStream) {
        // 将表名去掉下划线，用驼峰命名法命名
        val caseClassName: String = mysqlTable.split("_").map(str => {
          str.substring(0, 1).toUpperCase + str.substring(1)
        }).mkString("")

        // 包名
        var datasourceNumId = mysqlDataSource
        if (datasourceNumId.startsWith("gc_lms")) {
          datasourceNumId = "gc_lms." + datasourceNumId
        } else if (datasourceNumId.startsWith("gc_owms")) {
          datasourceNumId = "gc_owms." + datasourceNumId
        } else if (datasourceNumId.startsWith("zy_owms")) {
          datasourceNumId = "zy_owms." + datasourceNumId
        }

        val className = Class.forName(s"com.zongteng.ztetl.entity.$datasourceNumId.$caseClassName")

        val assistParameter = AssistParameter(className, pk, hbase_cf, datasource_num_id, created_on_dt, updataTime1, updataTime2, mysqlTable, mysqlDataSource)

        AssistParameterUtilScript.outPutAssistParameterUtilScript(assistParameter, caseClass_theme)
      }
    } catch {
      case e: ClassNotFoundException => {
        "重新跑一遍：由于没有生成对应的样例类，所以出现异常，这段程序跑完之后就会生成对应的样例类，所以重新跑一遍即可"
      }
    }

  }

  // 记录字段个数
  private var source$Num = Map[String, Int]()

  // jdbc连接信息
  var connect: String = _
  var username: String = _
  var password: String = _
  var schema: String = _
  var datasource_num_id: String = _
  var mysql_datasource_name: String = _

  var pk: String = _
  var created_on_dt: String = _
  //var changed_on_dt: String = _
  var updataTime1: String = _ // 时间戳
  var updataTime2: String = _ // 修改时间

  var ods_table: String = _ // 通过datasource(zy_wms) + mysql的表名（shipping_method）拼接而成：zy_wms_shipping_method

  var hbase_cf: String = _ // 列族
  var hbase_table: String = _ // hbase表

  val hbase_rk = "row_wid" // row_key

  val timestamp_select_sql: String = "case when year(timestamp_field) = 0 then\n" +
    "        cast(null as datetime) \n" +
    "    else cast(timestamp_field as datetime) \n" +
    "    end as timestamp_field"

  val sql = "SELECT table_name               as 表," +
    "\n       COLUMN_NAME              列名," +
    "\n       COLUMN_TYPE              数据类型," +
    "\n       DATA_TYPE                字段类型," +
    "\n       CHARACTER_MAXIMUM_LENGTH 长度," +
    "\n       IS_NULLABLE              是否为空," +
    "\n       COLUMN_DEFAULT           默认值," +
    "\n       COLUMN_COMMENT           备注" +
    "\n  FROM INFORMATION_SCHEMA.COLUMNS" +
    "\n where table_schema = 'tableschema'\n AND table_name = 'tablename'"

  var from_mysql_table: String = _
  var connection: Connection = _
  var mysqlTable: String = _
  var tableType: String = _
  var job_name: String = _ // 任务名称

  var hql_filed_List: ListBuffer[String] = _

  var mysqlFiled: ListBuffer[String] = _

  var mysqlDataSource: String = _

  // 存储脚本所有的ods层数据
  val odsTables: ListBuffer[String] = ListBuffer()

  def evalVal(mysqlOdsScript: MysqlOdsScript) = {

    connect = mysqlOdsScript.connect
    username = mysqlOdsScript.username
    password = mysqlOdsScript.password
    schema = mysqlOdsScript.schema
    datasource_num_id = mysqlOdsScript.datasource_num_id
    mysql_datasource_name = mysqlOdsScript.mysql_datasource_name

    pk = mysqlOdsScript.primaryKey
    created_on_dt = mysqlOdsScript.createTime
    // changed_on_dt = mysqlOdsScript.updataTime
    updataTime1 = mysqlOdsScript.updataTime_1
    updataTime2 = mysqlOdsScript.updataTime_2

    ods_table = mysqlOdsScript.mysqlDataSource + "_" + mysqlOdsScript.mysqlTable

    hbase_cf = mysqlOdsScript.columnFamily
    hbase_table = ods_table

    //gc_inventory_batch_log系统统一导入这个表中
    //hbase_table = mysqlOdsScript.mysqlDataSource + "_"+mysqlOdsScript.mysqlTable.substring(0,19)

    from_mysql_table = "FROM " + mysqlOdsScript.mysql_datasource_name + "." + mysqlOdsScript.mysqlTable

    mysqlTable = mysqlOdsScript.mysqlTable

    tableType = mysqlOdsScript.tableType

    job_name = "sqoop_import_ods_" + ods_table

    mysqlDataSource = mysqlOdsScript.mysqlDataSource

    // 打印信息到控制台
    println(connect + " " + username + " " + password + " " + schema + " " + datasource_num_id +
      " " + pk + " " + created_on_dt + " " + updataTime1 + " " + updataTime2 + " " + ods_table +
      " " + hbase_cf + " " + hbase_table + " " + from_mysql_table + " " + mysqlTable + " " + tableType +
      " " + job_name + " " + mysqlDataSource)
  }

  /**
    * 获取到所有字段, 并且对应default null做对应的转换
    *
    * @return
    */
  def getMysqlFile() = {

    var statement: Statement = null
    var resultSet: ResultSet = null
    val exeSql = sql.replace("tableschema", schema).replace("tablename", mysqlTable)
    var mysql_filed_List = ListBuffer[String]()

    hql_filed_List = new ListBuffer[String]()
    mysqlFiled = new ListBuffer[String]()

    try {
      statement = connection.createStatement() // 执行查询语句，返回表的元数据信息
      resultSet = statement.executeQuery(exeSql)

      var mysqlFiledNUm: Int = 0
      println(resultSet)
      while (resultSet.next()) {
        val dataType: String = resultSet.getString("数据类型").toLowerCase //转成小写字母

        val hqlType: String = transformFiledType(dataType) // 将mysql类型转为hql类型
        hql_filed_List += (s"  ${resultSet.getString("列名")} ${hqlType},") // hql的字段声明（字段名字 字段类型）

        mysqlFiled += (s"${resultSet.getString("列名")},")
        val is_default_null: Boolean = "YES".equalsIgnoreCase(resultSet.getString("是否为空")) && resultSet.getString("默认值") == null

        var field: String = transformField(dataType, resultSet.getString("列名"), is_default_null)

        mysql_filed_List += ("    " + field + ",")

        mysqlFiledNUm += 1
      }

      // 去掉最后一行逗号
      val last: String = mysql_filed_List.last
      mysql_filed_List.update(mysql_filed_List.length - 1, last.substring(0, last.lastIndexOf(",")))

      val last2: String = hql_filed_List.last
      hql_filed_List.update(hql_filed_List.length - 1, last2.substring(0, last2.lastIndexOf(",")))

      val last3: String = mysqlFiled.last
      mysqlFiled.update(mysqlFiled.length - 1, last3.substring(0, last3.lastIndexOf(",")))

      source$Num += ("mysql" -> mysqlFiledNUm)
      source$Num += ("hive" -> mysql_filed_List.length)
      source$Num += ("ddl_hive" -> hql_filed_List.length)
      source$Num += ("surrogate" -> (mysqlFiledNUm + 9))

    } catch {
      case e: Exception => throw e
    } finally {
      resultSet.close()
      statement.close()
      println("关闭数据库连接 == " + connection)
      connection.close()
      //用连接池的时候放开
      /*println("关闭数据库连接 == " + connection)
      SqoopScript.close(resultSet,statement,connection)*/
    }
    mysql_filed_List
  }

  /**
    * DDL的CREATEA TABLE的规则
    *
    * @param dataType
    * @return
    */
  def transformFiledType(dataType: String) = {
    // 时间类型
    val hqlType = if (Array("datetime", "timestamp", "date").contains(dataType)) {
      "Timestamp"
    }
    else if (dataType.matches(".*varchar.*|.*char.*|.*text.*|.*enum.*|.*bit.*")) {
      "String"
    }
    else if (dataType.matches(".*int.*")) {
      "Int"
    }
    else if (dataType.matches(".*decimal.*")) {
      dataType
    }
    else if (dataType.matches(".*float.*")) {
      dataType.replace("float", "decimal")
    }
    else if (dataType.matches(".*double.*")) {
      dataType.replace("double", "decimal")
    }
    else {
      dataType
    }

    hqlType.replaceFirst("\\).*", ")")
  }

  //需要判断mysql值是否有换行的表和字段,ods层表名+"."+字段名
  private val tableFiled = Array[String]("gc_wms_gc_receiving.receiving_description",
    "gc_oms_receiving.receiving_description",
    "gc_wms_receiving.receiving_description",
    "zy_wms_receiving.receiving_description")

  /**
    * 判断是否符合mysql值要替换的表和字段
    *
    * @param filed
    * @return
    */
  def filterFiledAndTable(filed: String) = {
    var result = false
    result = tableFiled.contains(ods_table.concat(".").concat(filed))
    result
  }

  /**
    * 规则：如果时间是'0000-00-00 00:00:00'，转成null；其他的不变
    *
    * @param dataType
    * @param field
    * @param is_default_null
    * @return
    */
  def transformField(dataType: String, field: String, is_default_null: Boolean) = {

    //    val char_select_sql = "ifnull(char_filed, '') as char_filed"
    //    val int_select_sql = "ifnull(int_filed, 0) as int_filed"
    //    val decimal_select_sql = "ifnull(decimal_filed, 0) as decimal_filed"w


    // 时间类型 -- 暂时将这个时间类型屏蔽，zeroDateTimeBehavior=convertToNull
    //    if (Array("datetime", "timestamp", "date").contains(dataType)) {
    //      timestamp_select_sql.replaceAll("timestamp_field", field)
    //    }


    //    else if (dataType.matches(".*varchar.*|.*char.*|.*text.*") && is_default_null)  {
    //      char_select_sql.replaceAll("char_filed", field)
    //    }
    //    else if (dataType.matches(".*int.*") && is_default_null) {
    //      int_select_sql.replaceAll("int_filed", field)
    //    }
    //    else if (dataType.matches(".*decimal.*") && is_default_null) {
    //      decimal_select_sql.replaceAll("decimal_filed", field)
    //    }

    if (filterFiledAndTable(field)) {
      s"REPLACE(REPLACE(${field},'\\r',''),'\\n',' ') as ${field}"
    } else if (dataType.matches(".*float.*")) {
      s"cast(${field} as ${dataType.replaceAll("float", "decimal")}) as ${field}"
    } else {
      field
    }

  }

  def generateSqoopScript() = {

    val mysql_filed_sql: ListBuffer[String] = getMysqlFile()
    val common_file_sql: ListBuffer[String] = getCommonQuerySql()

    val sqoop_shell = "#!/bin/bash\n" +
      "export JAVA_HOME=/opt/module/jdk1.8.0_144\n" +
      "\n" +
      "#新增5个字段\n" +
      "#ETL编号，记录ETL跑数批次 YYYYMMDD\n" +
      "etl_proc_wid=`date +%Y%m%d`\n" +
      "#记录数仓的插入时间 YYYY-MM-DD HH:MM:SS \n" +
      "w_insert_dt=`date '+%Y-%m-%d %H:%M:%S'`\n" +
      "#DW最近更新日期  记录数仓的更新时间 YYYY-MM-DD HH:MM:SS \n" +
      "w_update_dt=`date '+%Y-%m-%d %H:%M:%S'`\n" +
      "\n" +
      "#标识批量还是实时，实时区分I/U/D  批量batch，实时插入为I，更新为U,删除为D  batch,I,U,D \n" +
      "data_flag='batch'\n" +
      "\n" +
      "echo \"批次：$etl_proc_wid\"\n" +
      "echo \"数仓的插入时间：$w_insert_dt\"\n" +
      "echo \"数仓的更新时间：$w_update_dt\"\n" +
      "echo \"标识批量还是实时：$data_flag\"\n" +
      "\n" +
      "#任务名称\n" +
      s"job_name='$job_name'\n" +
      "#标识数据来源系统  数据来源进行编号配置\n" +
      s"datasource_num_id='$datasource_num_id'\n" +
      s"pk='$pk'\n" +
      s"database='ods'\n" +
      s"table='$ods_table'\n" +
      "\n" +
      "echo \"任务名称：$job_name\"\n" +
      "echo \"数据库来源系统：$datasource_num_id\"\n" +
      "echo \"主键：$pk\"\n" +
      "echo \"数据库：$database\"\n" +
      "echo \"表：$table\"" +
      "\n"

    val sqoop_shell_2 = s"connect='$connect'\n" +
      s"username='$username'\n" +
      s"password='$password'\n"

    val sqoopCommand: Array[String] = SqoopScript.getSqoopScriptByTableType(tableType, hbase_cf, hbase_rk, hbase_table)

    val sqoopScript: ListBuffer[String] = ListBuffer[String](sqoop_shell, sqoop_shell_2, "querySql=\"") ++=
      common_file_sql ++=
      mysql_filed_sql ++=
      ListBuffer[String](from_mysql_table, "WHERE \"'$CONDITIONS'\n") ++= // 修改限制条件 限制为10万条数据 记住删除 ' limit 100000'
      sqoopCommand

    sqoopScript.foreach(println(_))

    sqoopScript
  }

  /**
    *
    * @return
    */
  def getCommonQuerySql() = {

    var default_created_on_dt = "cast(null as datetime) as created_on_dt,"
    var default_changed_on_dt = "cast(null as datetime) as changed_on_dt,"

    val timestamp_select_sql2: String = "case when year(timestamp_field) = 0 then\n" +
      "         cast(null as datetime) \n" +
      "    else cast(timestamp_field as datetime)\n" +
      "    end as timestamp_field2"

    val timestamp_select_sql3: String =
      "case when year(updateTimeField1) = 0 then\n" +
        "        case when year(updateTimeField2) = 0 then\n" +
        "            cast(null as datetime)\n" +
        "        else \n" +
        "            cast(updateTimeField2 as datetime)\n" +
        "        end \n" +
        "    else \n" +
        "        cast(updateTimeField1 as datetime)\n" +
        "    end as changed_on_dt,"

    val querySql_1 = "select \n" +
      // s"    cast(CONCAT(${datasource_num_id}, ${pk}) AS SIGNED) as row_wid,\n" +
      s"    CONCAT(${datasource_num_id}, ${pk}) as row_wid,\n" +
      "    '${etl_proc_wid}' as etl_proc_wid,\n" +
      "    cast('${w_insert_dt}' as datetime) as w_insert_dt,\n" +
      "    cast('${w_update_dt}' as datetime) as w_update_dt,\n" +
      "    '${datasource_num_id}' as datasource_num_id,\n" +
      "    '${data_flag}' as data_flag,\n" +
      s"    ${pk} as integration_id,"

    //gc_inventory_batch_log系统所有表的row_wid为系统标号拼主键_数据库名称
    /*val querySql_1 = "select \n" +
      s"    CONCAT(${datasource_num_id}, ${pk},'_','${mysqlTable}')  as row_wid,\n" +
      "    '${etl_proc_wid}' as etl_proc_wid,\n" +
      "    cast('${w_insert_dt}' as datetime) as w_insert_dt,\n" +
      "    cast('${w_update_dt}' as datetime) as w_update_dt,\n" +
      "    '${datasource_num_id}' as datasource_num_id,\n" +
      "    '${data_flag}' as data_flag,\n" +
      s"    ${pk} as integration_id,"*/
    var querySql_2 = if (StringUtils.isBlank(created_on_dt)) {
      default_created_on_dt
    } else {
      timestamp_select_sql2.replace("timestamp_field2", "created_on_dt").replaceAll("timestamp_field", created_on_dt).concat(",")
    }

    var querySql_3 =
      if (StringUtils.isBlank(updataTime1) && StringUtils.isBlank(updataTime2)) {
        default_changed_on_dt
      } else if (StringUtils.isNotBlank(updataTime1) && StringUtils.isNotBlank(updataTime2)) {
        timestamp_select_sql3.replace("updateTimeField1", updataTime1).replace("updateTimeField2", updataTime2)
      } else if (StringUtils.isNotBlank(updataTime1)) {
        timestamp_select_sql2.replace("timestamp_field2", "changed_on_dt").replaceAll("timestamp_field", updataTime1).concat(",")
      } else if (StringUtils.isNotBlank(updataTime2)) {
        timestamp_select_sql2.replace("timestamp_field2", "changed_on_dt").replaceAll("timestamp_field", updataTime2).concat(",")
      }

    ListBuffer(querySql_1, "    " + querySql_2, "    " + querySql_3)
  }

  /**
    * 检查mysql转为hql是否正确，如果不正确抛出异常
    */
  def checkFiledNum() = {
    val mysqlNum: Int = source$Num.get("mysql").get
    val hiveNum: Int = source$Num.get("hive").get
    val ddl_hive_num: Int = source$Num.get("ddl_hive").get
    val surrogateNum: Int = source$Num.get("surrogate").get


    println("mysql字段个数 " + mysqlNum)
    println("hive字段个数 " + hiveNum)
    println("ddl的hive字段个数 " + ddl_hive_num)
    println("mysql加上9个代理键个数 " + surrogateNum)

    if (mysqlNum != hiveNum && mysqlNum != ddl_hive_num) {
      throw new Exception(s"$ods_table 表mysql字段和转化成为hql字段不匹配, 请检查")
    }

  }


}
