import swing._
import event._

class MyTable extends Table {
  def updateSize = {
     for (i <- 0 to (rowCount - 1)) {
        
       val r = rendererComponent(false, false, i, 0)       
       
       r.preferredSize.width = 75
       println(r.preferredSize)
       val c  = peer.getCellRenderer(i,0)       
       println(c.getTableCellRendererComponent(peer, 0, false, false, i,0))
       //peer.setRowHeight(i, r.preferredSize.height)        
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

object sampleComment extends CommentPanel(new Comment(-1, "id", new java.util.Date, "author", "comment L1" +"\n" + "comment line 2"))   

object MainApplication extends SimpleGUIApplication {
  
//  var com : Topic = DelfiTopicProducer.process("http://www.delfi.lt/news/ringas/politics/mapavilioniene-moters-kunas-kaip-musio-laukas.d?id=24027136&com=1")
//  com.comments = com.comments.reverse

  var com : Topic = null
  
  def top = new MainFrame {
    title = "Komentatorius"
    
    var commentsTable : MyTable = null
    
    object topicsTable extends Table() {
        model = TopicModel
        rowHeight = 15
 
        preferredViewportSize = new java.awt.Dimension(300, 0)
        
        
    }

    
    object nameField extends TextField { columns = 20 }
    object fahrenheit extends TextArea { rows = 6; columns = 20; border = Swing.LineBorder(java.awt.Color.BLACK)}

    object topicsScrollPane extends ScrollPane {      
        contents = {
            topicsTable
        }
    }
    
    object commentsScrollPane extends ScrollPane {
      
      contents = { 
          object CommentsTable extends MyTable() {
              model = CommentsModel
              rowHeight = 50
              
              peer.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION)
              
              override def rendererComponent(isSelected : Boolean, hasFocus : Boolean, row : Int, column : Int)  = {
                 val commentTextArea = new TextArea(com.comments{row}.text) { 
                   lineWrap = true
                   preferredViewportSize = new java.awt.Dimension(50, 60);
                   preferredSize = new java.awt.Dimension(100, 200);                   
                 }
                 
                 commentTextArea.revalidate
                 
                 commentTextArea
              }
              
          }
          commentsTable = CommentsTable 
          CommentsTable
      }
    }
    
    
    object buttonLoad extends Button ("Load")
    
    object buttonSubscribe extends Button ("Subscribe")
    
    contents = new GridBagPanel {
      layout(new Label("Vardas: ")) = new Constraints {gridx = 1; gridy = 0; anchor = GridBagPanel.Anchor.NorthWest}
      layout(nameField) = new Constraints {gridx = 2; gridy = 0; ; fill = scala.swing.GridBagPanel.Fill.Both}
      layout(new Label("Komentaras:")) =  new Constraints {gridx = 1; gridy = 1; anchor = GridBagPanel.Anchor.NorthWest}
      layout(fahrenheit) = new Constraints {gridx = 2; gridy = 1; fill = scala.swing.GridBagPanel.Fill.Both}
      layout(buttonLoad)  = new Constraints {gridx = 1; gridy = 2; gridwidth = 2; anchor = GridBagPanel.Anchor.East}                         
      layout(buttonSubscribe)  = new Constraints {gridx = 2; gridy = 2}

      border = Swing.EmptyBorder(15, 10, 10, 10)
      
      layout(commentsScrollPane) = new Constraints {grid = (1, 3); gridwidth = 2; fill = scala.swing.GridBagPanel.Fill.Horizontal; weightx=1}
      
      layout(sampleComment) = new Constraints {grid = (1, 4); gridwidth = 2; fill = GridBagPanel.Fill.Both}

      layout(topicsScrollPane) = new Constraints {grid = (0, 0); gridheight = 5; fill = GridBagPanel.Fill.Both}

    }        
    
    listenTo(nameField, fahrenheit, buttonLoad, buttonSubscribe, topicsTable.selection)
    
    reactions += {
      case ValueChanged(`fahrenheit`) =>
          println(fahrenheit.text)
        
      case EditDone(`nameField`) =>
        val c = nameField.text.toInt
        val f = c * 9 / 5 + 32
        fahrenheit.text = f.toString
        
      case ButtonClicked(`buttonLoad`) => {
        commentsTable.updateSize
      }
      
      case ButtonClicked(`buttonSubscribe`) => {
        SubscribeToTopicWindow.visible = true
      }
      
      case TableRowsSelected(`topicsTable`, range, adjusting) => {
        if (!adjusting) {
          Actions.getTopicSelection(topicsTable.selection.cells)
        }
      }      
    }     
  }
  
  
object Actions {
  
  def getTopicSelection(cells: scala.collection.mutable.Set[(Int, Int)]) = {
        
    if (cells.size == 1) {
      for ((row, col) <- cells) { 
        println("Topic no: " + row)         
        val topic = Data.getSampleSubscriptions(){row}
        
        Data.getCommentsForTopic(topic.url)       
      }
    }
  }
}  
  
object CommentsModel extends javax.swing.table.AbstractTableModel {

    override def getValueAt(row : Int, col : Int) = { com.comments{row}.text }  
      
    override def getColumnCount() = { 1 }
   
    override def getRowCount() = { 
      if (com != null) {  
        com.comments.length
      } else {
        0
      } 
    
    }
   
    override def getColumnName(col : Int) = { "Comment" } }
}

object TopicModel extends javax.swing.table.AbstractTableModel {

    override def getValueAt(row : Int, col : Int) = {
        Data.getSampleSubscriptions(){row}.url
    }  
      
    override def getColumnCount() = { 1 }
   
    override def getRowCount() = { 
        Data.getSampleSubscriptions().size
    }
   
    override def getColumnName(col : Int) = { "Topic" } }

