import java.awt.*;
import javax.swing.*;
import java.io.File;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.*;

//pour la décompresion
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Page extends JLabel {

    private int width; // width de l'image
    private int height; // height de l'image
    private Image UwU; // image actuelle
    private Image UwU_ap; // image apres
    private Image UwU_av; // image avant
    public boolean load =false;
    public boolean image_load =false;
    private int nb_maxpages;
    private int page_actuelle;
    private String Titre;
    private int count_load = 0;

    public boolean test_larg=false;
    public boolean test_full=false;

    private int centrage = 0;

    //private Vector<Integer> coef = new Vector<Integer>();
    private double coef = 1;

    public class Avant extends Thread{
        public Avant(){}
        public void run(){
            try{
                //System.out.println("Debut thread avant ");
                    image_load =true;
                    UwU_av = ImageIO.read(new File("D@ta/"+Titre+"/"+(page_actuelle-1)+".jpg"));
                    image_load =false;
                //System.out.println("Fin thread avant");
            }catch (IOException e) {
                e.printStackTrace();
            } 
        }
     }
    public class Load extends Thread{
        public Load(){}
        public void run(){
            setOpaque(false);
            load = true;
            do{
                try{
                    //System.out.println("load rotate");
                    repaint();
                    Thread.sleep(150);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                } 
                //rotate une image
            }while(load);
        }
    }

    public class Prochain extends Thread{
        public Prochain(){}
        public void run(){
            try{
                //System.out.println("Debut thread prochian ");
                    image_load =true;
                    UwU_ap = ImageIO.read(new File("D@ta/"+Titre+"/"+(page_actuelle+1)+".jpg"));
                    image_load =false;
                //System.out.println("Fin thread prochian");
            }catch (IOException e) {
                e.printStackTrace();
            } 
        }
     }

    Page(){
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setOpaque(true); // activer pour permettre le changement de couleur de fond mais creer des bug d'affichage avec les zoom
    }

    public void Decompression(String chemin, String name){ // fonction permettant la décompression d'un fichier Zip

    String test_autre_livre = "D@ta/"+Titre;
    File test = new File(test_autre_livre);
    if (test.exists()) {
        deleteDirectory(test); // modification a faire sur la fonction
    }
        nb_maxpages=0;
        Titre=name;
        //System.out.println("Commencement de la décompression");
        final String conteneur = "D@ta/"+name;
            // Créez le dossier Output s'il n'existe pas.
            File folder = new File(conteneur);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            // Créez un buffer (Tampon).
		    byte[] buffer = new byte[1024];
            ZipInputStream zipIs = null;
            try {
                // Créez un objet ZipInputStream pour lire les fichiers à partir d'un chemin (path).
                zipIs = new ZipInputStream(new FileInputStream(chemin));

                ZipEntry entry = null;
                // Parcourir chaque Entry (de haut en bas jusqu'à la fin)
                while ((entry = zipIs.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    String outFileName = conteneur + File.separator + entryName;
                    //System.out.println("Unzip: " + outFileName);

                    if (entry.isDirectory()) {
                        // Créer des dossiers.
                        new File(outFileName).mkdirs();
                    } else {
                        nb_maxpages+=1;
                        File f1 = new File(outFileName);
                        File f2 = new File(nb_maxpages+".jpg");
                        f1.renameTo(f2);
                        outFileName = conteneur + File.separator +nb_maxpages+".jpg";
                        // Créez un Stream pour graver des données dans le fichier.
                        FileOutputStream fos = new FileOutputStream(outFileName);

                        int len;
                        // ​​​​​​​
                        // Lisez les données sur Entry présent
                        while ((len = zipIs.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        } 
                        fos.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    zipIs.close();
                } catch (Exception e) {
                }
            }
            if(load){
                load=false;
                setOpaque(true);
            }
        //System.out.println("Fin de la décompression");
    }

    public void paint(Graphics g){ // fonction appeler pour faire apparaitre la zone dessin
        Style(g, width, height);
                
    }

    private void Style(Graphics g, int width, int height){
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        removeAll();
        if(load){
            count_load += 20;
            //System.out.println("SPIN");
            g2.translate(5 , 5);
            g2.rotate(Math.toRadians(20+count_load), width/2, height/2);
            // Drawing the rotated image at the required drawing locations
            g2.drawImage(UwU, 0, 0, null);
        }else{
            g2.drawImage(UwU, centrage, 0, width/* correspondra a la largeur de l'image*/, height/* correspondra a la hauteur de l'image*/, null);
            setPreferredSize(new Dimension(width, height));
        }
        g2.dispose();
    }

    
    public String Lecture_Fichier(String chemin, JScrollPane f){ // foncttion appeler dans le lecteur de fichier qui s'occupe de initié UwU a l'image dans le chemin
        Pattern pattern = Pattern.compile("[\\*'\"]"); //pour enlever les caracter spéciaux des chemin
        String[] result = pattern.split(chemin);
        String name = Arrays.toString(result); // convertir le résultat en String
        String recherche = "\\"; // recherche du charactere a compter
        //System.out.println("L'extension est : "+chemin.charAt(chemin.length() -3)+chemin.charAt(chemin.length() -2)+chemin.charAt(chemin.length() -1));
            Decompression(chemin, name.substring(compterTotal(name, recherche.charAt(0)),name.length()-5));
            page_actuelle=0;
            page_actuelle+=1;
            Prochain prochain = new Prochain();
            prochain.start();
            try {
                UwU = ImageIO.read(new File("D@ta/"+name.substring(compterTotal(name, recherche.charAt(0)),name.length()-5)+"/"+page_actuelle+".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            } 
            width = UwU.getWidth(null); // méthode permettant de récupéré la taille de l'image
            height = UwU.getHeight(null);
            if(width > f.getSize().width && height > f.getSize().height){
                //System.out.println("la dimension dépasse l'écran ");
               Right();
            }else{
                Resize(f);
            }
            setPreferredSize(new Dimension(width, height));
            repaint();
        return name.substring(compterTotal(name, recherche.charAt(0)),name.length()-5); //retourne le nom du fichier sans l'extension
    }
    
    public void ZoomPlus(){  // fonction faisant le zoom
        height = multi(height, 1.2);
        width =  multi(width, 1.2);
        //System.out.println("Taille de l'image ZoomPlus "+width+height);
        setPreferredSize(new Dimension(width, height));
        repaint();
    }
    
    public void ZoomMoins(){ // fonction faisant le dezoom
        height = div(height, 1.2);
        width = div(width, 1.2);
        //System.out.println("Taille de l'image ZoomMoins "+width+height);
        setPreferredSize(new Dimension(width, height));
        repaint();
    }
    
    
    public void ZoomPlus2(){  // fonction faisant le zoom
        height = multi(height, 1.05);
        width =  multi(width, 1.05);
        setPreferredSize(new Dimension(width, height));
        repaint();
    }
    
    public void ZoomMoins2(){ // fonction faisant le dezoom
        height = div(height, 1.05);
        width = div(width, 1.05);
        setPreferredSize(new Dimension(width, height));
        repaint();
    } // Zoom de la mollette désactivé
    
    public void Resize(JScrollPane f){  // fonction permettant de centré l'image par rapport au dimension de la ScrollPane
        centrage = (f.getSize().width/2)-width/2;
    }
    public void StandardVue(){
        width = UwU.getWidth(null); // méthode permettant de récupéré la taille de l'image d'origine
        height = UwU.getHeight(null);
        repaint();
        //System.out.println("Standard Vue");
    }

    public void TouteLargeur(JScrollPane f){ // permet a l'image de prendre toute la largeur de la ScrollPane
        width=f.getSize().width - (f.getVerticalScrollBar().getSize().width+3);
        double redimensionnement = (double)width / (double)UwU.getWidth(null);
        height = multi(UwU.getHeight(null),redimensionnement);;
        setPreferredSize(new Dimension(width, height));
        Right();
        repaint();
        //System.out.println("Toute Largeur");
    }

    public void Full(JScrollPane f){ // permet de mettre la hauteur max de l'image a la hauteur de la ScrollPane;
        height=f.getSize().height - (f.getHorizontalScrollBar().getSize().height+3);
        double redimensionnement = (double)height / (double)UwU.getHeight(null);
        if(height<UwU.getHeight(null)){
             //System.out.println("taille plus grande que l'image");
            width = multi(UwU.getWidth(null),redimensionnement);
        }else{
            //System.out.println("taille plus petite que l'image");
            width =  multi(UwU.getWidth(null),redimensionnement);
        }
        setPreferredSize(new Dimension(width, height));
        Resize(f);
        repaint();
       // System.out.println("Full");
    }

    public void Right(){ // pour centrer l'image a droite
        centrage=0; 
    }

    private int multi(int i, double d){ // fonction faisant la multipication d'un int par un double
        int res= (int)(i*d);
        return res;
    }

    private int div(int i, double d){ // fonction faisant la division d'un int par un double
        int res= (int)(i/d);
        return res;
    }

    public static int compterOccurrences(String maChaine, char recherche) // fonction comptant le nombre d'apparition du char rechercher
    {
    int nb = 0;
        for (int i=0; i < maChaine.length(); i++)
        {
            if (maChaine.charAt(i) == recherche)
            nb++;
        }
        return nb;
    }
    public static int compterTotal(String maChaine, char recherche) // fonction parcourant la String et retournant la derniere parti du chemin
        {
        int max = compterOccurrences(maChaine,recherche);
        int k=0;
        int nb = 0;
             for (int i=0; i < maChaine.length(); i++)
                {
                if (maChaine.charAt(i) == recherche)
                    k++;
                nb++;
                if(k==max)
                    return nb; 
                }
         return nb;
        }

    public void deleteDirectory(File file){
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectory(entry);
                    //System.out.println("delete : "+entry);
                }
            }
        }
        if (!file.delete()) {
            //System.out.println("pas de delete possible");
        }
    }

    public int getNbMaxPage(){
        return nb_maxpages;
    }

    public int Avant(JScrollPane f, Vector<Integer> count){
        Prochain prochain = new Prochain();
        Avant avant = new Avant();
        if(page_actuelle-1>0){
            if(!image_load){
                page_actuelle-=1;
                UwU = UwU_av;
                UwU_ap=null;
                UwU_av=null;
                avant.start();  
                prochain.start();         
                width = UwU.getWidth(null); // méthode permettant de récupéré la taille de l'image
                height = UwU.getHeight(null);
                if(test_full){
                    Full(f);
                 }else if(test_larg){
                     TouteLargeur(f);
                 }else{
                     Resize(f);
                 }
                 f.getVerticalScrollBar().setValue(0);
                 f.getHorizontalScrollBar().setValue(0);
                 if(count.size()>0){
                    Coeficiant(count);
                }
               // System.out.println("xifth ! "+width);
                //System.out.println("height ! "+height);
               // System.out.println("coef ! "+coef);
                width= multi(width, coef);
                height = multi(height, coef);
                //System.out.println("xifth2 ! "+width);
                //System.out.println("height2 ! "+height);
                setPreferredSize(new Dimension(width, height));
                repaint();
            }
        }
        return page_actuelle;
    }
    
    public int Apres( JScrollPane f, Vector<Integer> count){
        
        Avant avant = new Avant();
        Prochain prochain = new Prochain();
        if(page_actuelle+1<=nb_maxpages){ 
            if(!image_load){
                page_actuelle+=1;
                UwU = UwU_ap;
                UwU_ap=null;
                UwU_av=null;
                avant.start();  
                prochain.start();         
                width = UwU.getWidth(null); // méthode permettant de récupéré la taille de l'image
                height = UwU.getHeight(null);
                if(test_full){
                    Full(f);
                 }else if(test_larg){
                     TouteLargeur(f);
                 }else{
                     Resize(f);
                 }
                 f.getVerticalScrollBar().setValue(0);
                 f.getHorizontalScrollBar().setValue(0);
                 if(count.size()>0){
                    Coeficiant(count);
                }
               // System.out.println("xifth ! "+width);
                //System.out.println("height ! "+height);
               // System.out.println("coef ! "+coef);
                width= multi(width, coef);
                height = multi(height, coef);
                //System.out.println("xifth2 ! "+width);
                //System.out.println("height2 ! "+height);
                setPreferredSize(new Dimension(width, height));
                repaint();
            }
        }
        return page_actuelle;
    }

    public void chargement(JScrollPane f){
        try{ 
            UwU = ImageIO.read(new File("Image/Load.png"));

        }catch (IOException e) {
            e.printStackTrace();
        }
        width = UwU.getWidth(null); // méthode permettant de récupéré la taille de l'image
        height = UwU.getHeight(null);
        Resize(f);
        Load load = new Load();
        load.start();
        repaint();
    }

    public int Recherche( JScrollPane f, int page, Vector<Integer> count){
        if(page<0 || page>nb_maxpages){
            //System.out.println("recherche non effectuer");
        }else{
            page_actuelle=page;
            Avant avant = new Avant();
            Prochain prochain = new Prochain();
            if(page_actuelle+1<=nb_maxpages){ 
                prochain.start();   
            }
            if( page_actuelle-1>0){
                avant.start();  
            }
            String chemin = "D@ta/"+Titre+"/"+page+".jpg";
            //System.out.println(chemin);
            try {
                UwU = ImageIO.read(new File(chemin));
            } catch (IOException e) {
                e.printStackTrace();
            } 
            width = UwU.getWidth(null); // méthode permettant de récupéré la taille de l'image
            height = UwU.getHeight(null);
            if(test_full){
                Full(f);
            }else if(test_larg){
                TouteLargeur(f);
            }else{
                Resize(f);
            }
            setPreferredSize(new Dimension(width, height));
            if(count.size()>0){
                Coeficiant(count);
            }
            width= multi(width, coef);
            height = multi(height, coef);
             setPreferredSize(new Dimension(width, height));
             repaint();
        }
        f.getVerticalScrollBar().setValue(0);
        f.getHorizontalScrollBar().setValue(0);
        return page_actuelle;
    }

    public int get_nbmax(){
        return nb_maxpages;
    }

   public void reset_coef(){
        coef=1;
    }

    public void Coeficiant(Vector<Integer> count){
        int height2 = height;
        int width2 = width;
        for(int i=0; i<count.size(); i++){
            int type=count.elementAt(i);
                if(type==0){
                    height2 = multi(height2, 1.2);
                    width2 =  multi(width2, 1.2);
            }
            if(type==1){
                    height2 = multi(height2, 1.05);
                    width2 =  multi(width2, 1.05);
            }
            if(type==2){
                    height2 = div(height2, 1.2);
                    width2 =  div(width2, 1.2);
            }
            if(type==3){
                    height2 = div(height2, 1.05);
                    width2 =  div(width2, 1.05);
            }
        }
        //System.out.println("xifth22 ! "+width2);
        //System.out.println("height22 ! "+height2);
        coef=(double)height2/(double)height;
        //System.out.println("resultar ! "+coef);
    }
    
}
