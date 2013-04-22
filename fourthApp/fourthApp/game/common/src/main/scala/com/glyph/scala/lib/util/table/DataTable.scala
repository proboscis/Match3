package com.glyph.scala.lib.util.table

import com.glyph.libgdx.util.{ArrayStack, ArrayBag}
import java.util
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class DataTable {

  val mTable = new ArrayStack[ArrayBag[Any]]
  val mIndexToTypeMap = new util.HashMap[Int, Manifest[_]]
  val mNameToColumnIndexMap = new util.HashMap[String, Integer]
  val mColumnIndexToNameMap = new util.HashMap[Int, String]

  private def log(msg: String) {
    Glyph.log("DataTable", msg)
  }


  /*
    /**
     * this is slow since this requires map reference and casting.
     * and dangerous since you can set anything as an value
     * @param id
     * @param colName
     * @param value
     */
    def set(id:Int,colName:String,value:Any){
      set(id,getColumnIndex(colName),value)
    }
    */
  /*
    /**
     * this is dangerous since this enables you to add anything in the table
     * @param id
     * @param colIndex
     * @param value
     */
    def set(id:Int,colIndex:Int,value:Any){
      var column = mTable.get(colIndex)
      if (column == null){
        val colName = getColumnName(colIndex)
        column = addColumn(colName)
      }
      column.set(id,value)
    }
  */
  def set[T:Manifest](id: Int, colName: String, value: T) {
    log("set:( " + id + " , " + colName + " )")
    val colIndex = getColumnIndex(colName)
    set(id, colIndex, value)
  }

  /**
   * set value with type check
   * @param id
   * @param colIndex
   * @param value
   * @tparam T
   */
  def set[T:Manifest](id: Int, colIndex: Int, value: T) {
    log("set:( " + id + " , " + colIndex + " )")
    typeCheck[T](colIndex)
    var column = mTable.get(colIndex)
    if (column == null) {
      log("column == nulll")
      val colName = getColumnName(colIndex)
      log("colName = " + colName)
      column = addColumn(colName)
    }
    column.set(id, value)
  }


  /**
   * get the index of specified column. <br>
   * creates new index if it is not defined yet
   * @param colName
   * @return
   */
  def getColumnIndex(colName: String): Int = {
    log("get column index:" + colName)
    val result = mNameToColumnIndexMap.get(colName)
    if (result == null) {
      val index = mTable.push(null)
      log("create new column index:" + index)
      mNameToColumnIndexMap.put(colName, (index))
      mColumnIndexToNameMap.put(index, colName)
      index
    } else {
      log("column index = " + result)
      result
    }
  }

  /**
   * beware that this method slows RuntimeException when the column is not defined yet.
   * @param colIndex
   * @return
   */
  def getColumnName(colIndex: Int): String = {
    val result = mColumnIndexToNameMap.get(colIndex)
    if (result == null) {
      throw new RuntimeException("column with specified index is not defined yet")
    } else {
      result
    }
  }

  /**
   * adds a column with specified type and name
   * @param colName
   */
  def addColumn[T](colName: String)(implicit typ: Manifest[T]): ArrayBag[T] = {
    log("addColumn:( " + colName + " , " + typ.runtimeClass.getSimpleName + " )")
    val columnIndex = getColumnIndex(colName)
    log("colIndex = " + columnIndex)
    if (mTable.get(columnIndex) == null) {
      log("create new ArrayBag for column")
      mIndexToTypeMap.put(columnIndex, typ)
      mTable.setWithResult(columnIndex, new ArrayBag[Any]).asInstanceOf[ArrayBag[T]]
    } else {
      log("column with name [" + colName + "] is already defined")
      throw new RuntimeException("column with name " + colName + " is already defined")
    }
  }

  /**
   * get a column as an ArrayBag of specified type
   * @param colName
   * @tparam T
   */
  def getColumn[T:Manifest](colName: String) {
    getColumn[T](getColumnIndex(colName))
  }

  def getColumn[T:Manifest](colIndex: Int): ArrayBag[T] = {
    typeCheck[T](colIndex)
    mTable.get(colIndex).asInstanceOf[ArrayBag[T]]
  }

  def typeCheck[T](colIndex: Int)(implicit typ: Manifest[T]): Boolean = {
    val colType = getColumnType(colIndex)
    if (colType == typ) {
      true
    } else {
      val msg = "type mismatch" + "\n type of value:" + typ.runtimeClass + "\n type of column:" + colType.runtimeClass
      log(msg)
      throw new RuntimeException(msg)
      false
    }
  }

  def getColumnType(colName: String): Manifest[_] = {
    val colIndex = getColumnIndex(colName)
    getColumnType(colIndex)
  }

  def getColumnType(colIndex: Int): Manifest[_] = {
    mIndexToTypeMap.get(colIndex)
  }
}
