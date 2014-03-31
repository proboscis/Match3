package com.glyph._scala.lib.util.layout
/**
 * @author glyph
 */
object GridLayout {
  val EXTEND_NONE = 0x00
  val EXTEND_X = 0x01
  val EXTEND_Y = 0x10
  type LayoutFlag = Int
  case class Cell[+A](target:A,x:Int,y:Int,column:Int,row:Int,flag:LayoutFlag)
  case class Layout(x:Float,y:Float,w:Float,h:Float)
  case class LayoutCell[A](target:A,layout:Layout)
  case class Grid(length:Float,numGrid:Int,pad:Float,margin:Float){
    //gridLengthを積み重ねると誤差でずれてしまうので、毎回計算する必要がある
    val gridLength = (length -pad*2 - (numGrid - 1)*margin)/numGrid
    def calcGridLength(span:Int) = span* gridLength + (span-1)*margin
    def calcExtendedGridLength(pos:Int,span:Int) = {
      var p = 0f
      if (pos == 0) p += pad
      if (pos + span == numGrid) p += pad
      calcGridLength(span) + p
    }
    def calcPosition(pos:Int) = pad + (length-pad*2+margin)/numGrid * pos
    def calcExtendedPosition(pos:Int) = if(pos == 0) 0f else calcPosition(pos)
  }

  def calculateLayouts[A](targets:Seq[Cell[A]])(width:Float,height:Float,numColumn:Int,numRow:Int,padX:Float,padY:Float,marginX:Float,marginY:Float)
                         :Seq[LayoutCell[A]] ={
    val gridX = Grid(width,numColumn,padX,marginX)
    val gridY = Grid(height,numRow,padY,marginY)
    targets.map{
      case Cell(tgt,x,y,col,row,flag) =>
        assert(x + col <= numColumn && y + row <= numRow)
        val rx = if((flag & EXTEND_X) != 0) gridX.calcExtendedPosition(x) else gridX.calcPosition(x)
        val ry = if((flag & EXTEND_Y) != 0) gridY.calcExtendedPosition(y) else gridY.calcPosition(y)
        val rWidth = if((flag & EXTEND_X) != 0)gridX.calcExtendedGridLength(x,col) else gridX.calcGridLength(col)
        val rHeight = if((flag & EXTEND_Y) != 0)gridY.calcExtendedGridLength(y,row) else gridY.calcGridLength(row)
        LayoutCell(tgt,Layout(rx,ry,rWidth,rHeight))
    }
  }
}
