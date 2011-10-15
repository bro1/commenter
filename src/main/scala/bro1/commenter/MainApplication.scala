package bro1.commenter

import swing._
import event._
import lj.scala.utils.TimeActor
import lj.scala.utils.ArticleCheckActor
import java.awt.Font
import java.awt.Dimension
//import javax.swing.

object SizeConstants {

  val labelDimension20 = {
    val label = new Label("a"); 
    val singleSize = label.preferredSize; 
    new java.awt.Dimension((singleSize.getWidth * 34).toInt, singleSize.getHeight.toInt);     
  }
    
  val buttonInsets = new java.awt.Insets(2, 2, 2, 2)
  
}

class CommentPanel(com : Comment) extends GridBagPanel {
  
  object commentField extends TextArea(com.text) { 
	  lineWrap = true
          wordWrap = true
          peer.setSelectionStart(120);
  }
  
  add({val l = new Label(com.postedBy); l.preferredSize = SizeConstants.labelDimension20; l.horizontalAlignment = Alignment.Left; l } , new Constraints{gridx = 0; gridy = 0; fill = GridBagPanel.Fill.Both})
  
  val f = new java.text.SimpleDateFormat("yyyy MM dd HH:mm")  
  
  add(new Label(f.format(com.timeStamp)), new Constraints{gridx = 1; gridy = 0; fill = GridBagPanel.Fill.Both})
  
  val buttonReply = new Button("R")
  buttonReply.margin = SizeConstants.buttonInsets
  
  add(buttonReply, new Constraints{gridx = 2; gridy = 0; fill = GridBagPanel.Fill.Both})
  
  val buttonRateGood = new Button("+")
  buttonRateGood.margin = SizeConstants.buttonInsets
  add(buttonRateGood, new Constraints{gridx = 3; gridy = 0; fill = GridBagPanel.Fill.Both})
  
  val buttonRateBad = new Button("-")
  buttonRateBad.margin = SizeConstants.buttonInsets

  
  add(buttonRateBad, new Constraints{gridx = 4; gridy = 0; fill = GridBagPanel.Fill.Both})
  add(commentField, new Constraints{gridx = 0; gridy = 1; gridwidth = 5; fill = GridBagPanel.Fill.Both})

  border = Swing.LineBorder(java.awt.Color.DARK_GRAY)
  
  listenTo(buttonReply, buttonRateGood, buttonRateBad)
  
  reactions += {
    case ButtonClicked(`buttonReply`) => {
      println("Reply")      
    }
    
    case ButtonClicked(`buttonRateGood`) => {
      println("Rate Good")      
    }
        
    case ButtonClicked(`buttonRateBad`) => {
      println("Rate Bad")      
    }
  }

}

object MainApplication extends SimpleGUIApplication {

  // start the time
  TimeActor.start
  ArticleCheckActor.start
  TopicCacheUpdateActor.start
  
  var currentTopic : Topic = null

  object CommentsPanel extends GridBagPanel {
    
    def a(c : Component) = {
        add(c, new Constraints{fill = GridBagPanel.Fill.Both; gridy = contents.size})
    }

    def clear() = {
      peer.removeAll
    }
  }
  
  object NewCommentPanel extends GridBagPanel {

    object NameLabel extends Label {
      text = "Vardas:"
    }
    
    object NameField extends TextField {
      columns = 20
    }
    
    object Blah extends ScrollPane {
      contents = CommentField
      minimumSize = new Dimension(100,100)
    }
    
    object CommentField extends TextArea { 
	  lineWrap = true
      wordWrap = true
      rows = 5
       
    }

    add(NameLabel, new Constraints{fill = GridBagPanel.Fill.Both; gridx = 0; gridy = 0; weightx = 0.5; weighty=1})
    add(NameField, new Constraints{fill = GridBagPanel.Fill.Both; gridx = 1; gridy = 0; weightx = 1})
    add(Blah, new Constraints{fill = GridBagPanel.Fill.Both; gridy = 2; gridwidth = 2; weighty = 5})

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


    object topicsTable extends Table() {
        model = TopicModel
        rowHeight = 15

        
        override def rendererComponent (isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = {
          
        		val component = super.rendererComponent(isSelected, focused, row, column)
        		        		
        		if (Data.getSubscribtions(){row}.newComments) {        		  
        		  component.font = new Font(component.font.getName(), component.font.getStyle() + Font.BOLD, component.font.getSize())        		  
        		}
        		
        		component
        }
    }
  
  
  def top = new MainFrame {
    title = "Komentatorius"
    
    
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
    object buttonUnsubscribe extends Button ("Unsubscribe")
    
    contents = new GridBagPanel {
      layout(buttonLoad)  = new Constraints {gridx = 1; gridy = 0; anchor = GridBagPanel.Anchor.West}
      layout(buttonSubscribe)  = new Constraints {gridx = 2; gridy = 0; anchor = GridBagPanel.Anchor.Center}
      layout(buttonUnsubscribe)  = new Constraints {gridx = 3; gridy = 0; anchor = GridBagPanel.Anchor.East}

      border = Swing.EmptyBorder(5, 5, 5, 5)
      layout(CommentsScroll) = new Constraints {
          grid = (1, 1);
          gridwidth = 3;
          fill = scala.swing.GridBagPanel.Fill.Both;
          weightx = 1;
          weighty = 1
      }
      


      layout(topicsScrollPane) = new Constraints {
          grid = (0, 0);
          gridheight = 3;
          fill = GridBagPanel.Fill.Both;
          weightx=0.5
          weighty = 0.5
      }
      
      layout(NewCommentPanel) = new Constraints {
        grid = (1,2)
        gridwidth = 3
        fill = GridBagPanel.Fill.Horizontal        
      }


    }        
    
    listenTo(nameField, fahrenheit, buttonLoad, buttonSubscribe, buttonUnsubscribe, topicsTable.selection)
    
    reactions += {   
        
      case ButtonClicked(`buttonLoad`) => {   
        val t = Data.getSubscribtions(){topicsTable.selection.rows.anchorIndex}
        ArticleCheckActor ! t
      }
      
      case ButtonClicked(`buttonSubscribe`) => {
        SubscribeToTopicWindow.visible = true
      }
      
      case ButtonClicked(`buttonUnsubscribe`) => {
        Actions.unsubscribe()
      }      
      
      case TableRowsSelected(`topicsTable`, range, adjusting) => {
        if (!adjusting) {
          Actions.getTopicSelection(topicsTable.selection.cells)
        }
      }
    }     
  }
  
  
object Actions {
  
  def unsubscribe()  {
	  if (MainApplication.currentTopic != null) {
	    currentTopic.unsubscribe()	    
	    currentTopic = null
	  }
  }
  
  def getTopicSelection(cells: scala.collection.mutable.Set[(Int, Int)]) = {
        
    if (cells.size == 1) {
      for ((row, col) <- cells) { 
         
        val topic = Data.getSubscribtions(){row}
                       
        if (topic.comments.isEmpty) topic.update
        
        currentTopic = topic
        topic.markAllCommentsRead()
        CommentsModel.fireTableCellUpdated(row, col)

        CommentsPanel.clear()

        for (c <- currentTopic.commentsSorted) {
            CommentsPanel.a(new CommentPanel(c))            
        }

        CommentsScroll.verticalScrollBar.value = 0
        CommentsScroll.revalidate        
      }
    }
  }
}  
  
object CommentsModel extends javax.swing.table.DefaultTableModel {

    override def getValueAt(row : Int, col : Int) = { 
      currentTopic.comments.sortBy(_.timeStamp).reverse{row}.text 
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


