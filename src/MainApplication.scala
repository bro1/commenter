import swing._
import event._

class MyTable extends Table {
  def updateSize = {
     for (i <- 0 to (rowCount - 1)) {
        
       val r = rendererComponent(false, false, i, 0)
       
       r.preferredSize.width = 75
       println(r.preferredSize)
       
       peer.setRowHeight(i, r.preferredSize.height)
        
     }     
  }
}



class CommentPanel(com : Comment) extends GridBagPanel {
  
  object commentField extends TextArea(com.text) { 
	lineWrap = true                   
  }
  
  layout(new Label(com.postedBy)) = new Constraints{gridx = 0; gridy = 0; fill = GridBagPanel.Fill.Both}
  layout(new Label(com.postedAt.toString)) = new Constraints{gridx = 1; gridy = 0; fill = GridBagPanel.Fill.Both}
  layout(new Button("+")) = new Constraints{gridx = 2; gridy = 0; fill = GridBagPanel.Fill.Both}
  layout(new Button("-")) = new Constraints{gridx = 3; gridy = 0; fill = GridBagPanel.Fill.Both}
  layout(commentField) = new Constraints{gridx = 0; gridy = 1; gridwidth = 4; fill = GridBagPanel.Fill.Both}  
  border = Swing.EmptyBorder(15, 10, 10, 10)  

}

object sampleComment extends CommentPanel(new Comment("id", new java.util.Date, "author", "comment L1" +"\n" + "comment line 2"))   

object MainApplication extends SimpleGUIApplication {
  
  var com : Topic = FetchTopic.processTopic("http://www.delfi.lt/news/ringas/politics/mapavilioniene-moters-kunas-kaip-musio-laukas.d?id=24027136&com=1")
  com.comments = com.comments.reverse
  
  
  def top = new MainFrame {
    title = "Komentatorius"
    
    var t1 : MyTable = null
    
    object nameField extends TextField { columns = 20 }
    object fahrenheit extends TextArea { rows = 6; columns = 20; border = Swing.LineBorder(java.awt.Color.BLACK)}

    object topicsScrollPane extends ScrollPane {
        contents = {
            object topicsTable extends Table() {
                model = TopicModel
                rowHeight = 15
            }
            topicsTable
        }
    }
    
    object sp extends ScrollPane {
      
      contents = { 
          object t extends MyTable() {
              model = MyTableModel
              rowHeight = 50
              
              override def rendererComponent(isSelected : Boolean, hasFocus : Boolean, row : Int, column : Int)  = {
                 val v = new TextArea(com.comments{row}.text) { 
                   lineWrap = true                    
                 }
                 
                 v.revalidate
                 
                 v
              }
              
          }
          t1 = t 
          t
      }
    }
    
    
    object buttonLoad extends Button ("Load")
    
    object buttonSubscribe extends Button ("Subscribe")
    
    contents = new GridBagPanel {
      layout(new Label("Vardas: ")) = new Constraints {gridx = 0; gridy = 0; anchor = GridBagPanel.Anchor.NorthWest}
      layout(nameField) = new Constraints {gridx = 1; gridy = 0; ; fill = scala.swing.GridBagPanel.Fill.Both}
      layout(new Label("Komentaras:")) =  new Constraints {gridx = 0; gridy = 1; anchor = GridBagPanel.Anchor.NorthWest}
      layout(fahrenheit) = new Constraints {gridx = 1; gridy = 1; fill = scala.swing.GridBagPanel.Fill.Both}
      layout(buttonLoad)  = new Constraints {gridx = 0; gridy = 2; gridwidth = 2; anchor = GridBagPanel.Anchor.East}                         
      layout(buttonSubscribe)  = new Constraints {gridx = 1; gridy = 2}

      border = Swing.EmptyBorder(15, 10, 10, 10)
      
      layout(sp) = new Constraints {grid = (0, 3); gridwidth = 2; fill = scala.swing.GridBagPanel.Fill.Horizontal}
      
      layout(sampleComment) = new Constraints {grid = (0, 4); gridwidth = 2; fill = GridBagPanel.Fill.Both}

      layout(topicsScrollPane) = new Constraints {grid = (2, 0);
gridheight = 4 }

    }
        
    
    listenTo(nameField, fahrenheit, buttonLoad, buttonSubscribe)
    
    reactions += {
      case ValueChanged(`fahrenheit`) =>
          println(fahrenheit.text)
        
      case EditDone(`nameField`) =>
        val c = nameField.text.toInt
        val f = c * 9 / 5 + 32
        fahrenheit.text = f.toString
        
      case ButtonClicked(`buttonLoad`) => {
        t1.updateSize
      }
      
      case ButtonClicked(`buttonSubscribe`) => {
        SubscribeToTopicWindow.visible = true
      }
        
    }     
  }
  
object MyTableModel extends javax.swing.table.AbstractTableModel {

    override def getValueAt(a : Int, b  : Int) = { com.comments{a}.text }  
      
    override def getColumnCount() = { 1 }
   
    override def getRowCount() = { com.comments.length }
   
    override def getColumnName(col : Int) = { "Comment" } }
}

object TopicModel extends javax.swing.table.AbstractTableModel {

    override def getValueAt(a : Int, b  : Int) = {
        Data.getSampleSubscriptions(){a}.url
    }  
      
    override def getColumnCount() = { 1 }
   
    override def getRowCount() = { 
        Data.getSampleSubscriptions().length
    }
   
    override def getColumnName(col : Int) = { "Topic" } }

