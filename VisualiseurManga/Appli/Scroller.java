
import javax.swing.JScrollPane;
import java.awt.*; 

public class Scroller extends JScrollPane{

    Scroller(Component view, Color C){
        super(view, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS); // pour qu'il y est toujours les scrollbar
        //setBackground pour changer la couleur des différent objet de cette classe lors de ca création
        setBackground(C); 
        getVerticalScrollBar().setBackground(C);
        getHorizontalScrollBar().setBackground(C);
        getVerticalScrollBar().setUnitIncrement(40); // permet de gerer la vitesse du scroll
        
    }

    public void Change(Color C){ // fonction permettant le changement de couleur de la ScrollBar
        getVerticalScrollBar().setBackground(C);
        getHorizontalScrollBar().setBackground(C);
        setBackground(C);
    }

}
