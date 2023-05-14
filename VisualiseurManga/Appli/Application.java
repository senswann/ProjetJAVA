/**
 * Cette classe permet de lancer l'application.
 */
public class Application {
    /**
     * Main permettant de lancer l'application.
     * 
     * @param args paramètre par défaut d'un main.
     * @see FenetreGraphique
     */
    public static void main(String[] args){
        FenetreGraphique fenetre; // fentre de l'application
        fenetre = new FenetreGraphique("Liseuse de BD/Manga"); // Nom de l'application
        fenetre.setVisible(true); //pour la rendre visible
    }
}
