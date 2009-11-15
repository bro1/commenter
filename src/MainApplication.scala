import swing._
import event._


class CommentPanel(com : Comment) extends GridBagPanel {
  
  object commentField extends TextArea(com.text) { 
	  lineWrap = true
          wordWrap = true
          peer.setSelectionStart(120);
  }
  
  add({val l = new Label("a"); val s = l.preferredSize; l.preferredSize = new java.awt.Dimension((s.getWidth * 20).toInt, s.getHeight.toInt); l.text = com.postedBy; l } , new Constraints{gridx = 0; gridy = 0; fill = GridBagPanel.Fill.Both})
  add(new Label(com.postedAt.toString), new Constraints{gridx = 1; gridy = 0; fill = GridBagPanel.Fill.Both})
  add(new Button("+"), new Constraints{gridx = 2; gridy = 0; fill = GridBagPanel.Fill.Both})
  add(new Button("-"), new Constraints{gridx = 3; gridy = 0; fill = GridBagPanel.Fill.Both})
  add(commentField, new Constraints{gridx = 0; gridy = 1; gridwidth = 4; fill = GridBagPanel.Fill.Both})


//  border = Swing.EmptyBorder(15, 10, 10, 10)

border = Swing.LineBorder(java.awt.Color.DARK_GRAY)




}

object MainApplication extends SimpleGUIApplication {
  
  var currentTopic : Topic = null

  object CommentsPanel extends GridBagPanel {
    def a(c : Component) = {

        add(c, new Constraints{fill = GridBagPanel.Fill.Both; gridy = contents.size})
    }

    def clear() = {
        contents.dropWhile((c) => {false})
    }
  }

  object CommentsScroll extends ScrollPane {

    preferredSize = new java.awt.Dimension(500, 600)

    peer.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
    peer.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    peer.getVerticalScrollBar.setUnitIncrement(20)

    contents = {
        CommentsPanel
    }
  
  }


  
  
  def top = new MainFrame {
    title = "Komentatorius"
    
    object topicsTable extends Table() {
        model = TopicModel
        rowHeight = 15         
    }
    
    object nameField extends TextField { columns = 20 }
    object fahrenheit extends TextArea { rows = 6; columns = 20; border = Swing.LineBorder(java.awt.Color.BLACK)}

    object topicsScrollPane extends ScrollPane {      
        contents = {
            topicsTable
        }

        preferredSize = new java.awt.Dimension(250, 400)       
    }

     
    
    object buttonLoad extends Button ("Load")
    
    object buttonSubscribe extends Button ("Subscribe")
    
    contents = new GridBagPanel {
//      layout(new Label("Vardas: ")) = new Constraints {gridx = 1; gridy = 0; anchor = GridBagPanel.Anchor.NorthWest}
//      layout(nameField) = new Constraints {gridx = 2; gridy = 0; ; fill = scala.swing.GridBagPanel.Fill.Both}
//      layout(new Label("Komentaras:")) =  new Constraints {gridx = 1; gridy = 1; anchor = GridBagPanel.Anchor.NorthWest}
//      layout(fahrenheit) = new Constraints {gridx = 2; gridy = 1; fill = scala.swing.GridBagPanel.Fill.Both}
      layout(buttonLoad)  = new Constraints {gridx = 1; gridy = 0; anchor = GridBagPanel.Anchor.East}
      layout(buttonSubscribe)  = new Constraints {gridx = 2; gridy = 0}

      border = Swing.EmptyBorder(5, 5, 5, 5)
      // was commentsScrollPane
      layout(CommentsScroll) = new Constraints {
          grid = (1, 1);
          gridwidth = 2;
          fill = scala.swing.GridBagPanel.Fill.Both;
          weightx = 1;
          weighty = 1
      }
      


      layout(topicsScrollPane) = new Constraints {
          grid = (0, 0);
          gridheight = 2;
          fill = GridBagPanel.Fill.Both;
          weightx=0.5
          weighty = 0.5
      }


    }        
    
    listenTo(nameField, fahrenheit, buttonLoad, buttonSubscribe, topicsTable.selection)
    
    reactions += {
//      case ValueChanged(`fahrenheit`) =>
//          println(fahrenheit.text)
//        
//      case EditDone(`nameField`) =>
//        val c = nameField.text.toInt
//        val f = c * 9 / 5 + 32
//        fahrenheit.text = f.toString
        
      case ButtonClicked(`buttonLoad`) => {
//        commentsTable.updateSize
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
         
        val topic = Data.getSubscribtions(){row}
        
        // TODO:
        //topic.getTimeOfNextUpdate
        
        Data.getCommentsForTopic(topic.id)
        
        topic.update
        currentTopic = topic
        CommentsModel.fireTableDataChanged

        CommentsPanel.clear()

        for (c <- currentTopic.comments) {
            CommentsPanel.a(new CommentPanel(c))            
        }

        CommentsScroll.revalidate        
      }
    }
  }
}  
  
object CommentsModel extends javax.swing.table.DefaultTableModel {

    override def getValueAt(row : Int, col : Int) = { 
      currentTopic.comments{row}.text 
    }
      
    override def getColumnCount() = { 1 }
   
    override def getRowCount() = { 
      if (currentTopic != null) {
        currentTopic.comments.length
      } else {
        0
      } 
    
    }
   
    override def getColumnName(col : Int) = { "Komentaras" } }
}

object TopicModel extends javax.swing.table.AbstractTableModel {

    override def getValueAt(row : Int, col : Int) = {
      Data.getSubscribtions(){row}.title
    }  
      
    override def getColumnCount() = { 1 }
   
    override def getRowCount() = { 
        Data.getSubscribtions().size
    }
   
    override def getColumnName(col : Int) = { "Topic" } }

