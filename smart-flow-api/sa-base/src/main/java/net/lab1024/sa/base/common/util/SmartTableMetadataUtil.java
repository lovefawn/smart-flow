package net.lab1024.sa.base.common.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Component
public class SmartTableMetadataUtil {

 @Resource
 private DataSource dataSource;  // 注入的数据源

 private static DataSource staticDataSource;  // 静态变量保存数据源

 @PostConstruct
 public void init() {
  staticDataSource = this.dataSource;
 }


 /**
  * 根据实体类获取表元数据
  * @param entityClass 实体类Class对象
  * @return JSON格式的表元数据
  */
 public static JSONObject getTableMetadata(Class<?> entityClass) throws Exception {
  TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
  if (tableInfo == null) {
   throw new RuntimeException("实体类未配置MyBatis-Plus注解");
  }
  return getTableMetadataByTableInfo(tableInfo);
 }

 /**
  * 根据表名获取表元数据
  * @param tableName 数据库表名
  * @return JSON格式的表元数据
  */
 public static JSONObject getTableMetadata(String tableName) throws Exception {
  if (staticDataSource == null) {
   throw new RuntimeException("数据源未初始化");
  }
  return getTableMetadataByTableName(tableName);
 }

 /**
  * 根据TableInfo获取表元数据（内部方法）
  * @param tableInfo MyBatis-Plus表信息
  * @return JSON格式的表元数据
  */
 private static JSONObject getTableMetadataByTableInfo(TableInfo tableInfo) throws Exception {
  String tableName = tableInfo.getTableName();

  // 1. 获取表注释
  String tableComment = getTableCommentFromDB(tableName);
  JSONObject tableJson = new JSONObject();
  tableJson.set("englishName", tableName);
  tableJson.set("chineseName", tableComment);

  // 2. 获取字段注释
  Map<String, String> fieldComments = getFieldCommentsFromDB(tableName);
  JSONArray columns = new JSONArray();
  for (TableFieldInfo field : tableInfo.getFieldList()) {
   JSONObject column = new JSONObject();
   String columnName = field.getColumn();
   column.set("englishName", columnName);
   column.set("chineseName", fieldComments.getOrDefault(columnName, "无注释"));
   column.set("javaType", field.getPropertyType().getSimpleName());
   columns.add(column);
  }

  // 3. 构建结果
  JSONObject result = new JSONObject();
  result.set("table", tableJson);
  result.set("columns", columns);
  return result;
 }

 /**
  * 根据表名获取表元数据（内部方法）
  * @param tableName 数据库表名
  * @return JSON格式的表元数据
  */
 private static JSONObject getTableMetadataByTableName(String tableName) throws Exception {
  // 1. 获取表注释
  String tableComment = getTableCommentFromDB(tableName);
  JSONObject tableJson = new JSONObject();
  tableJson.set("englishName", tableName);
  tableJson.set("chineseName", tableComment);

  // 2. 获取字段信息和注释
  Map<String, String> fieldComments = getFieldCommentsFromDB(tableName);
  Map<String, String> fieldTypes = getFieldTypesFromDB(tableName);

  JSONArray columns = new JSONArray();
  for (Map.Entry<String, String> entry : fieldComments.entrySet()) {
   String columnName = entry.getKey();
   String comment = entry.getValue();
   String sqlType = fieldTypes.getOrDefault(columnName, "VARCHAR");

   JSONObject column = new JSONObject();
   column.set("englishName", columnName);
   column.set("chineseName", comment);
   column.set("sqlType", sqlType);
   column.set("javaType", sqlTypeToJavaType(sqlType));
   columns.add(column);
  }

  // 3. 构建结果
  JSONObject result = new JSONObject();
  result.set("table", tableJson);
  result.set("columns", columns);
  return result;
 }

 // 从数据库获取表注释（基于JDBC元数据）
 private static String getTableCommentFromDB(String tableName) throws Exception {
  try (Connection conn = staticDataSource.getConnection()) {
   DatabaseMetaData metaData = conn.getMetaData();
   try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
    if (rs.next()) return rs.getString("REMARKS");
   }
  }
  return tableName;
 }

 // 批量获取字段注释
 private static Map<String, String> getFieldCommentsFromDB(String tableName) throws Exception {
  Map<String, String> comments = new HashMap<>();
  try (Connection conn = staticDataSource.getConnection()) {
   DatabaseMetaData metaData = conn.getMetaData();
   try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
    while (rs.next()) {
     String columnName = rs.getString("COLUMN_NAME");
     String remark = rs.getString("REMARKS");
     comments.put(columnName, remark != null ? remark : "无注释");
    }
   }
  }
  return comments;
 }

 // 批量获取字段类型
 private static Map<String, String> getFieldTypesFromDB(String tableName) throws Exception {
  Map<String, String> types = new HashMap<>();
  try (Connection conn = staticDataSource.getConnection()) {
   DatabaseMetaData metaData = conn.getMetaData();
   try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
    while (rs.next()) {
     String columnName = rs.getString("COLUMN_NAME");
     String typeName = rs.getString("TYPE_NAME");
     int columnSize = rs.getInt("COLUMN_SIZE");
     int decimalDigits = rs.getInt("DECIMAL_DIGITS");

     // 构建完整的类型信息
     String fullType = typeName;
     if (columnSize > 0) {
      if (decimalDigits > 0) {
       fullType = typeName + "(" + columnSize + "," + decimalDigits + ")";
      } else {
       fullType = typeName + "(" + columnSize + ")";
      }
     }
     types.put(columnName, fullType);
    }
   }
  }
  return types;
 }

 // SQL类型转Java类型
 private static String sqlTypeToJavaType(String sqlType) {
  if (sqlType == null) return "String";

  String upperType = sqlType.toUpperCase();
  if (upperType.startsWith("VARCHAR") || upperType.startsWith("CHAR") ||
      upperType.startsWith("TEXT") || upperType.startsWith("LONGTEXT")) {
   return "String";
  } else if (upperType.startsWith("INT") || upperType.startsWith("TINYINT") ||
             upperType.startsWith("SMALLINT") || upperType.startsWith("MEDIUMINT")) {
   return "Integer";
  } else if (upperType.startsWith("BIGINT")) {
   return "Long";
  } else if (upperType.startsWith("DECIMAL") || upperType.startsWith("NUMERIC")) {
   return "BigDecimal";
  } else if (upperType.startsWith("FLOAT")) {
   return "Float";
  } else if (upperType.startsWith("DOUBLE")) {
   return "Double";
  } else if (upperType.startsWith("DATE")) {
   return "Date";
  } else if (upperType.startsWith("DATETIME") || upperType.startsWith("TIMESTAMP")) {
   return "Date";
  } else if (upperType.startsWith("TIME")) {
   return "Time";
  } else if (upperType.startsWith("BOOLEAN") || upperType.startsWith("BIT")) {
   return "Boolean";
  } else {
   return "String";
  }
 }

}
