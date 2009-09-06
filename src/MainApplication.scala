import swing._
import event._

object MainApplication extends SimpleGUIApplication {
  def top = new MainFrame {
    title = "Komentatorius"
    object nameField extends TextField { columns = 20 }
    object fahrenheit extends TextArea { rows = 6; columns = 20; border = Swing.EmptyBorder(15, 10, 10, 10)}
    object t extends Table(5,1) {
      
//      def rendererComponent(isSelected : Boolean, hasFocus : Boolean, row : Int, column : Int) : Component {
//        return new TextField(1);
//      }
    }
    
    contents = new GridBagPanel {
      layout(new Label("Vardas: ")) = new Constraints {gridx = 0; gridy = 0}
      layout(nameField) = new Constraints {gridx = 1; gridy = 0}
      layout(new Label("Komentaras:")) =  new Constraints {gridx = 0; gridy = 1}
      layout(fahrenheit) = new Constraints {gridx = 1; gridy = 1}
      
//      contents += 
//      contents += nameField
//      contents += 
//      contents += fahrenheit
      border = Swing.EmptyBorder(15, 10, 10, 10)
      
      layout(t) = new Constraints {grid = (0, 2); gridwidth = 2}      
      
    }
        
    
    listenTo(nameField, fahrenheit)
    
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
        
//      case()
    }     
  }
  
}
