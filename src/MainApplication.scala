import swing._
import event._

class MyTable extends Table {
  def updateSize = {
     for (i <- 0 to (rowCount - 1)) {
        
       val r = rendererComponent(false, false, i, 0)
       //r.size.width = 75
       
       r.preferredSize.width = 75
       println(r.preferredSize)
       
       peer.setRowHeight(i, r.preferredSize.height)
        
        
     }     
  }
}


object MainApplication extends SimpleGUIApplication {
  
  var com : Topic = FetchTopic.processTopic("http://www.delfi.lt/news/ringas/politics/mapavilioniene-moters-kunas-kaip-musio-laukas.d?id=24027136&com=1")
  com.comments = com.comments.reverse
  
  
  def top = new MainFrame {
    title = "Komentatorius"
    
    var t1 : MyTable = null
    
    object nameField extends TextField { columns = 20 }
    object fahrenheit extends TextArea { rows = 6; columns = 20; border = Swing.EmptyBorder(15, 10, 10, 10)}
    
    object sp extends ScrollPane {
      
      
      contents = { 
          object t extends MyTable() {
              model = MyTableModel
              rowHeight = 50
              override def rendererComponent(isSelected : Boolean, hasFocus : Boolean, row : Int, column : Int)  = {
                 new TextArea(com.comments{row}.text) { 
                   lineWrap = true
                   pack
                 }
              }
              
          }
          t1 = t 
          t
      }
    }
    
    
    object load1 extends Button ("Load")
    
    object buttonSubscribe extends Button ("Subscribe")
    
    contents = new GridBagPanel {
      layout(new Label("Vardas: ")) = new Constraints {gridx = 0; gridy = 0}
      layout(nameField) = new Constraints {gridx = 1; gridy = 0}
      layout(new Label("Komentaras:")) =  new Constraints {gridx = 0; gridy = 1}
      layout(fahrenheit) = new Constraints {gridx = 1; gridy = 1}
      layout(load1)  = new Constraints {gridx = 0; gridy = 2}                         
      layout(buttonSubscribe)  = new Constraints {gridx = 1; gridy = 2}
                         
      
//      contents += 
//      contents += nameField
//      contents += 
//      contents += fahrenheit
      border = Swing.EmptyBorder(15, 10, 10, 10)
      
      layout(sp) = new Constraints {grid = (0, 3); gridwidth = 2}      
      
    }
        
    
    listenTo(nameField, fahrenheit, load1, buttonSubscribe)
    
    reactions += {
      case ValueChanged(`fahrenheit`) =>
//        val f = fahrenheit.text.toInt
//        val c = (f - 32) * 5 / 9
//        nameField.text = c.toString
          println(fahrenheit.text)
        
      case EditDone(`nameField`) =>
        val c = nameField.text.toInt
        val f = c * 9 / 5 + 32
        fahrenheit.text = f.toString
        
      case ButtonClicked(`load1`) => {
        t1.updateSize
      }
      
      case ButtonClicked(`buttonSubscribe`) => {
        
      }

                
        
    }     
  }
  
  object MyTableModel extends javax.swing.table.AbstractTableModel {
   override def getValueAt(a : Int, b  : Int) = {
     com.comments{a}.text
   }  
      
   override def getColumnCount() = {
     1
   }
   
   override def getRowCount() = {
     com.comments.length
   }
   
   override def getColumnName(col : Int) = {
     "Comment"
   } 
   
   
}
  
}


