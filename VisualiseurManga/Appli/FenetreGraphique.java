import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*; 
import java.awt.event.*;
import java.io.File;
import java.util.Vector;

import java.awt.Desktop;
import java.net.URI;



public class FenetreGraphique extends JFrame implements	WindowListener{

    private final FenetreGraphique window = this;

    class Decompression extends Thread{ // thread qui permettra défectuer la décompression du livre
        private JFileChooser selecteur;
        public Decompression(JFileChooser select){
            selecteur = select;
        }
        public void run(){
            musplay.PlayLoad();
            //System.out.println("Fichier choisi: " + selecteur.getSelectedFile().getAbsolutePath());
            // setEnabled(false) permet de désactiver les bouton
            Larg.setEnabled(false);
            Mode.setEnabled(false);
            Music.setEnabled(false);
            Son.setEnabled(false);
            SonPlus.setEnabled(false);
            SonMoins.setEnabled(false);
            Fichier.setEnabled(false);
            Full.setEnabled(false);
            ZoomMoins.setEnabled(false);
            ZoomPlus.setEnabled(false);
            Avant.setEnabled(false);
            Apres.setEnabled(false);
            Page.setEnabled(false);
            Download.setEnabled(false);
            Titre.setText(pages.Lecture_Fichier(selecteur.getSelectedFile().getAbsolutePath(), panel)); // lance la décompression du cbz voulu + retourne le titre du livre
            Page.setText("1"); // page actuelle
            Max.setText(" / "+pages.getNbMaxPage()); // nombbre max de page
            // setEnabled(true) permet de réactiver les bouton
            Larg.setEnabled(true);
            Mode.setEnabled(true);
            Music.setEnabled(true);
            Son.setEnabled(true);
            SonPlus.setEnabled(true);
            SonMoins.setEnabled(true);
            Fichier.setEnabled(true);
            Full.setEnabled(true);
            ZoomMoins.setEnabled(true);
            ZoomPlus.setEnabled(true);
            Avant.setEnabled(true);
            Apres.setEnabled(true);
            Page.setEnabled(true);
            Download.setEnabled(true);
            repaint(); // fonction permettant de rapeler la fonction paint qui s'occupe de redessiner l'image
            Reload();// reload pour update la panel
            musplay.PlayLoad();
        } 
    }
    class Chargement extends Thread{ // thread  s'occupant d'effectuer le visuel du temps de chargement
        public Chargement(){}
        public void run(){
            panel.setSize(window.getSize()); // faire que la taille du panel soit égal a celui de la fenetre
            Titre.setText("En cours de décompression..."); // changer le titre temporairement
            pages.chargement(panel); // lancer la fonction de pages affichant l'image du chargement
            Reload();// reload pour update la panel
        }
     }

    class AjustementListener implements AdjustmentListener { // class permettant d'éffectuer un ajustement de la scrollbar selon les changement de dimension
        public void adjustmentValueChanged(AdjustmentEvent evt) { // si l'ajustement change on effectue un changement sur les scrollbars
          //Adjustable source = evt.getAdjustable();
          if (evt.getValueIsAdjusting()) { // si la valeur est ajusté
              Reload();
            return;
          }
          Reload(); // reload pour update la panel
          //System.out.println("Event de la Horizontal Scrollbar"); 
          //System.out.println("Event de la Vertical Scrollbar");
        }
    }

        /* Différent bouton de la fenetre */
    private JToolBar toolbar = new JToolBar(); // création de la toolbar
    private JButton Fichier = new JButton( new ImageIcon( "Image/Fichier.png" ) ); // création du bouton Fichier
    private JButton Mode = new JButton( new ImageIcon( "Image/Mode.png" ) );
    private JButton Music = new JButton( new ImageIcon( "Image/Music.png" ) );
    private JButton Son = new JButton( new ImageIcon( "Image/Son.png" ) );
    private JButton SonMoins = new JButton( new ImageIcon( "Image/VolumeMoins.png" ) );
    private JButton SonPlus = new JButton( new ImageIcon( "Image/VolumePlus.png" ) );
    private JLabel Titre = new JLabel(); // il faut en faire une variable de cette classe pour pouvoir la modifier apres
    private JButton Download = new JButton(new ImageIcon( "Image/Download.png" )) ;
    private JButton Larg = new JButton( new ImageIcon( "Image/Larg.png" ) );
    private JButton Full = new JButton( new ImageIcon( "Image/Full.png" ) );

    private JToolBar toolbar2 = new JToolBar(); // création de la toolbar 2
    private JButton Avant = new JButton( new ImageIcon( "Image/Avant.png" ) );
    private JButton ZoomMoins = new JButton( new ImageIcon( "Image/ZoomMoins.png" ) );
    private JTextField Page = new JTextField(3);
    private JLabel Max = new JLabel();
    private JButton ZoomPlus = new JButton( new ImageIcon( "Image/ZoomPlus.png" ) );
    private JButton Apres = new JButton( new ImageIcon( "Image/Apres.png" ) );

        /* Couleur utilisé pour le mode sombre */
    private Color Lumineux = new Color(255, 255, 255);
    private Color Lumineux_Container = new Color(245, 237, 237);
    private Color Sombre = new Color(64, 60, 67);
    private Color Sombre_Container = new Color(100, 95, 111);
    private Color Hover = new Color(225, 109, 109);
    private boolean mode = true;

        /* pages qui contiendra l'image */

    Page pages = new Page();
    Scroller panel = new Scroller(pages, Lumineux_Container);

        /* test effectuer dans la fenetre */

    private boolean test_larg = false;
    private boolean test_full = false;
    private boolean test_zoom = false;
    private boolean test_zoom_adapt = false;
    private boolean test_son = false;

    private Vector<Integer> count_zoom = new Vector<Integer>(); // tableau stockant les différent zoom effectuer sur la pages
    private Vector<Integer> test = new Vector<Integer>(); // tableau stockant les différent zoom effectuer sur la pages
    private MusicPlayer musplay = new MusicPlayer(); // player de music
    private int size_old = 0;

    public FenetreGraphique(String s){
        super(s); // pour creer une fenetre
        Image icon = Toolkit.getDefaultToolkit().getImage("Image/logo.png"); // pour récupérer l'icon
        setIconImage(icon);  // pour mettre l'icon
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // pour fermer lors du click sur la croix
        setSize(1280,720); //pour la dimension de la fenetre
        setLocationRelativeTo(null); // pour centrer la fenetre
        setResizable(true); // pour quelle soit redimensionnable    

        pages.addComponentListener(new ComponentAdapter(){ // ajout d'un listener écoutant les différents changement de pages
            public void componentResized(ComponentEvent e){ // fonction appeler lors d'un changement de taille des component appelant le resize de l'image
                //System.out.println("Appele du changemnt de taille de l'image");
                ResizeImage(); // fonction effectuant le resize de l'image
                Reload();
            }
        });

        addComponentListener(new ComponentAdapter() { // Ajout d'un ComponentListener a la JFrame
            public void componentResized(ComponentEvent e) { //Listener du redimensionnement du Component
                if (!test_larg && !test_full)
                    ResizeImage(); // fonction appelant le Resize
                else{ 
                    if(test_full){ // si le mode full est activé
                        ChangeFull();
                    }else if(test_larg){ // si le mode larg est activé
                        ChangeLargeur();
                    }
                }
                Reload();
                //panel.SetSize(pages);
            }
        });

        addWindowListener(this); // pour ajouter un windowListener a cette fenetre

                /*  Construction de la ToolBar   */

        toolbar.setBackground(Lumineux); // permet de mettre une couleur a la toolbar

        Fichier.setBackground(toolbar.getBackground());
        Fichier.setBorderPainted(false); // pour enlever les bordure d'un bouton
        Fichier.setToolTipText( "Permet l'ouverture du livre" ); // ajoute un commentaire quant on survole le bouton
        Fichier.addActionListener( this::FichierListener ); // Ajoute un écouteur appeler lors d'un clic

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Fichier);
        toolbar.add( Fichier ); // ajoute le bouton a la toolbar
        toolbar.addSeparator(); // pour mettre un espace

        Music.setBackground(toolbar.getBackground());
        Music.setBorderPainted(false);
        Music.setToolTipText( "Permet de Choisir une music de type .wav a jouer en fond" );
        Music.addActionListener( this::LoadMusicListener ); 

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Music);
        toolbar.add(Music);
        toolbar.addSeparator();

        Son.setBackground(toolbar.getBackground());
        Son.setBorderPainted(false);
        Son.setToolTipText( "Permet de lancer la music en fond" );
        Son.addActionListener( this::SonListener ); 

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Son);
        toolbar.add(Son);
        toolbar.addSeparator();
        
        SonMoins.setBackground(toolbar.getBackground());
        SonMoins.setBorderPainted(false);
        SonMoins.setToolTipText( "Permet de baisser la music en fond" );
        SonMoins.addActionListener( this::SonMoinsListener ); 

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(SonMoins);
        toolbar.add(SonMoins);
        toolbar.addSeparator();
        
        SonPlus.setBackground(toolbar.getBackground());
        SonPlus.setBorderPainted(false);
        SonPlus.setToolTipText( "Permet d'augmenter la music en fond" );
        SonPlus.addActionListener( this::SonPlusListener ); 

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(SonPlus);
        toolbar.add(SonPlus);
        toolbar.addSeparator();

        
        toolbar.add(Box.createHorizontalGlue()); // permet de mettre les prochain bouton a milieu   

        // label qui contiendra le titre
        Titre.setFont(new Font("Arial", Font.BOLD, 20)); // pour faire un sorte que tout le titre utilise cette police
        Titre.setText("En attente de Livre...");
        toolbar.add( Titre);
        toolbar.addSeparator();

        toolbar.add(Box.createHorizontalGlue()); // permet de mettre les prochain bouton a droite

        
        // Bouton Mode Sombre
        Mode.setBackground(toolbar.getBackground());
        Mode.setBorderPainted(false);
        Mode.setToolTipText( "Permet de changer le Mode de Couleur Lumineux/Sombre" );
        Mode.addActionListener( this::ModeListener ); 
        
        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Mode);
        toolbar.add( Mode );
        toolbar.addSeparator();
        
        // Bouton Download
        Download.setBackground(toolbar.getBackground());
        Download.setBorderPainted(false);
        Download.setToolTipText( "Permet d'aller sur un site de téléchargement" );
        Download.addActionListener( this::DownloadListener ); 

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Download);
        toolbar.add( Download );
        toolbar.addSeparator();

        // Bouton pour que l'image prene toute la largeur
        Larg.setBackground(toolbar.getBackground());
        Larg.setBorderPainted(false);
        Larg.setToolTipText( "permet de mettre la page prennant toute la largeur" );
        Larg.addActionListener( this::LargListener );
        Larg.setBorderPainted(false);
        
        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Larg);
        toolbar.add( Larg );
        toolbar.addSeparator();
        
        // Bouton pour que l'image passe en pleine Ecran
        Full.setBackground(toolbar.getBackground());
        Full.setBorderPainted(false);
        Full.setToolTipText( "Permet de mettre la page en pleine écran" );
        Full.addActionListener( this::FullListener );
        Full.setBorderPainted(false);

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Full);
        toolbar.add( Full );
        toolbar.addSeparator(); 

        toolbar.setFloatable (false); // pour rendre la ToolBar non Dockable(Floatable)
        getContentPane().add(toolbar, BorderLayout.NORTH); // ajout de la toolbar au container avec la contrainte d'etre en en-tete (Nord)

                /****  Fin Construction de la ToolBar   ****/


                /****  Construction toolbar2 ****/

        toolbar2.setBackground(Lumineux); // permet de mettre une couleur a la toolbar
        //toolbar2.setLayout(new FlowLayout());

        Avant.setBackground(toolbar2.getBackground());
        Avant.setBorderPainted(false); // pour enlever les bordure d'un bouton
        Avant.setToolTipText( "Permet de revenir à la page précédente" ); // ajoute un commentaire quant on survole le bouton
        Avant.addActionListener( this::AvantListener ); // Ajoute un écouteur appeler lors d'un clic

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Avant);
        toolbar2.add( Avant ); // ajoute le bouton a la toolbar
        for(int k=0; k<15; k++){
            toolbar2.addSeparator();
        } 

        ZoomMoins.setBackground(toolbar.getBackground());
        ZoomMoins.setBorderPainted(false);
        ZoomMoins.setToolTipText( "Permet de dézoomer" );
        ZoomMoins.addActionListener( this::ZoomMoinsListener ); 

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(ZoomMoins);
        toolbar2.add( ZoomMoins );
        toolbar2.addSeparator();
        
        toolbar2.add(Box.createHorizontalGlue()); // permet de mettre les prochain bouton a milieu   

        // label qui contiendra la page actuelle
        for(int k=0; k<15; k++){
            toolbar2.addSeparator();
        }
        Page.setFont(new Font("Arial", Font.BOLD, 20)); // pour faire un sorte que tout le titre utilise cette police
        Page.setEditable(false); // pour le rendre non éditable tant que pas de lecture de fichier
        toolbar2.add(Page, BorderLayout.CENTER);
        toolbar2.addSeparator();
        Max.setFont(new Font("Arial", Font.BOLD, 20)); // pour faire un sorte que tout le titre utilise cette police
        toolbar2.add(Max, BorderLayout.CENTER);
        for(int k=0; k<15; k++){
            toolbar2.addSeparator();
        }

        toolbar2.add(Box.createHorizontalGlue()); // permet de mettre les prochain bouton a droite

        // Bouton pour que l'image prene toute la largeur
        ZoomPlus.setBackground(toolbar.getBackground());
        ZoomPlus.setBorderPainted(false);
        ZoomPlus.setToolTipText( "permet de zoomer" );
        ZoomPlus.addActionListener( this::ZoomPlusListener );
        ZoomPlus.setBorderPainted(false);

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(ZoomPlus);
        toolbar2.add( ZoomPlus );
        for(int k=0; k<15; k++){
            toolbar2.addSeparator();
        }
        
        // Bouton pour que l'image passe en pleine Ecran
        Apres.setBackground(toolbar.getBackground());
        Apres.setBorderPainted(false);
        Apres.setToolTipText( "Permet d'aller à la page suivante" );
        Apres.addActionListener( this::ApresListener );
        Apres.setBorderPainted(false);

        // appelle de la fonction changeant le fond de bouton
        HoverBouton(Apres);
        toolbar2.add( Apres );
        toolbar2.addSeparator(); 

        toolbar2.setFloatable (false); // pour rendre la ToolBar non Dockable(Floatable)

        getContentPane().add(toolbar2, BorderLayout.SOUTH);

                /**** Fin de Construction toolbar2 ****/



                /**** Ajout de la zone d'affichage des pages */


        KeyListener keyListener = new KeyListener() { // keyListener permettant d'ajouter la fonction de reccherche de pages
            public void keyPressed(KeyEvent event) {
                //System.out.println("Pressed "+event);
              if(event.getKeyCode() == KeyEvent.VK_ENTER){ // pour lancer la recherche apres avoir rentré les valeur dans la zone de recherche
                Page.setText(String.valueOf(pages.Recherche(panel, Integer.parseInt(Page.getText()),(count_zoom.size()>size_old?count_zoom:test)))); // recherche de la pages renvoyant le numéro de la nouvelle pages
                Reload();
                Update(); // permet d'adapter a la nouvelle taille
                //count_zoom.clear();//pour vidé le stockage des différent zoom
                size_old = count_zoom.size();
            }
            }
            public void keyReleased(KeyEvent event) {}
            public void keyTyped(KeyEvent event) {}
        };      
        Page.addKeyListener(keyListener); // ajout du keyListener a Page
        
        pages.setHorizontalAlignment(JLabel.CENTER); // marche pour un text

       
        MouseListener mouseLtnr = new MouseListener() // création d'un mouse listener permettant d'effectuer le changement de page en cliquant sur un coté de l'écran
        {
            public void mouseClicked(MouseEvent e) { // quand un clic est détecter
                if (e.getButton() == MouseEvent.BUTTON1){ // quand clic gauche
                    int x = e.getX(); // contient la position de la souris
                    if(x>panel.getSize().width/2){ // si elle est superieur au centre en X de la fenetre
                        //System.out.println("droite");
                        Page.setText(String.valueOf(pages.Apres(panel,(count_zoom.size()>size_old?count_zoom:test)))); // on fait appele a la fonction changemnt a la page n+1
                        ResizeImage();
                        Reload();
                        Update();
                        size_old = count_zoom.size();   
                        //count_zoom.clear(); // on reset le conteur de zoom
                    }
                    if(x<panel.getSize().width/2){// si elle est inférieur au centre en X de la fenetre
                        //System.out.println("gauche");
                        Page.setText(String.valueOf(pages.Avant(panel,(count_zoom.size()>size_old?count_zoom:test)))); // on fait appele a la fonction changemnt a la page n-1
                        ResizeImage();
                        Reload();
                        Update();
                        size_old = count_zoom.size();
                        //count_zoom.clear();
                        //System.out.println("Clic sur le Avant");
                    }
                }
            }
            public void mouseEntered(MouseEvent e){}
            public void mouseExited(MouseEvent e){}
            public void mousePressed(MouseEvent e){ // quand clic maintenue
                if (e.getButton() == MouseEvent.BUTTON3){ // si clic droit
                    test_zoom_adapt = true; // on passe le test_zoom mollette a true
                    //System.out.println("bouton droit");
                    //System.out.println("touche maintenue");
                }
            }  
            public void mouseReleased(MouseEvent e){ //quand clic laché
                test_zoom_adapt = false; // on passe le test_zoom mollette a false
                //System.out.println("touche laché");
            }   
        };
        pages.addMouseListener(mouseLtnr); // ajout du mouse listener ci-dessus a pages

        panel.addMouseWheelListener(this::scrollmouse ); // ajout du mouse listener de la molette

        

        AdjustmentListener listener = new AjustementListener();
        panel.getHorizontalScrollBar().addAdjustmentListener(listener); // ajout de l'adjustement listener a la scrollbar horizontal
        panel.getVerticalScrollBar().addAdjustmentListener(listener); // ajout de l'adjustement listener a la scrollbar vertical 

        getContentPane().add(panel); 
    }


    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    private void scrollmouse(MouseWheelEvent e){ // fonction permettant de zoomer/dezoomer a la molette
        int point = e.getWheelRotation(); // variable stockant la rotattion actuelle
        if(test_zoom_adapt){ // si le test du maintient du clic droit est vraie
            panel.setWheelScrollingEnabled(false); // on desactive temporairement le scrolling dans la page
            if(point>0){ // si mollette recule
                if(test_larg || test_full){ // on desactive les test de largeur et de full si il sont actif
                    test_full =false;
                    test_larg =false;
                }
                pages.ZoomMoins2(); // on utlise la fonction de zoom de pages
                //System.out.println("Clic sur le ZoomMoins"); 
                if(pages.getSize().width > panel.getSize().width && pages.getSize().height > panel.getSize().height){
                    test_zoom = true;
                }else{
                    test_zoom = false;
                }
                ResizeImage();
                Reload();
                Update();
                //count_zoommoins2+=1;
                count_zoom.add(3);
            }else{
                if(test_larg || test_full){
                    test_full =false;
                    test_larg =false;
                }
                pages.ZoomPlus2();
                //System.out.println("Clic sur le ZoomPlus");
                if(pages.getSize().width > panel.getSize().width && pages.getSize().height > panel.getSize().height){
                    test_zoom = true;
                }else{
                    test_zoom = false;
                }
                ResizeImage();
                Reload();
                Update();
                //count_zoomplus2+=1;
                count_zoom.add(1);
            }
        }else{
            panel.setWheelScrollingEnabled(true); // on reactive le scrolling dans la page
        }
    } 

    private void FichierListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Fichier
    {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){

        }
        test_zoom = false;
        Page.setEditable(true); // pour rendre le nombre de page editable

        //System.out.println("Clic sur le Fichier");
        
        /**** Selecteur de fichier   ****/
        
        JFileChooser selecteur = new JFileChooser();
        selecteur.addChoosableFileFilter(new FileNameExtensionFilter("Comic Book archive Zipped", "cbz")); // extension possible a selectionner 
        selecteur.setAcceptAllFileFilterUsed(false); // pour faire un sorte que all file ne soit pas selectable
        Chargement load = new Chargement();
        Decompression decomp = new Decompression(selecteur);
        int resultat = selecteur.showOpenDialog(null);
        if(resultat == JFileChooser.APPROVE_OPTION) {
            load.start();
            decomp.start(); // Creer un Thread décompression
            //System.out.println("Fichier choisi: " + selecteur.getSelectedFile().getAbsolutePath());
            //Titre.setText(pages.Lecture_Fichier(selecteur.getSelectedFile().getAbsolutePath(), panel));
            repaint(); // fonction permettant de rapeler la fonction paint qui s'occupe de redessiner l'image
            //test_zoom = pages.test;
            Reload();
            //Update();
        }

                /**** Fin Selecteur de fichier   ****/
    } 

    private void ModeListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Mode
    {
        test_zoom = false;
        //System.out.println("Clic sur le Mode");

            /**** Changement de couleur */

            if(mode){
                Titre.setForeground(Lumineux);
                Page.setForeground(Lumineux);
                Max.setForeground(Lumineux);
                toolbar.setBackground(Sombre);

                toolbar2.setBackground(Sombre);
                panel.Change(Sombre_Container);
            
                //change l'icon des boutons
                Fichier.setIcon( new ImageIcon( "Image/Fichier_dark.png" ));
                Music.setIcon( new ImageIcon( "Image/Music_dark.png" ));
                Son.setIcon( new ImageIcon( "Image/Son_dark.png" ));
                SonMoins.setIcon( new ImageIcon( "Image/VolumeMoins_dark.png" ));
                SonPlus.setIcon( new ImageIcon( "Image/VolumePlus_dark.png" ));
                Mode.setIcon( new ImageIcon( "Image/Mode_dark.png" ));
                Download.setIcon( new ImageIcon( "Image/Download_dark.png" ));
                Larg.setIcon( new ImageIcon( "Image/Larg_dark.png" ));
                Full.setIcon( new ImageIcon( "Image/Full_dark.png" ));

                Avant.setIcon( new ImageIcon( "Image/Avant_dark.png" ));
                ZoomMoins.setIcon( new ImageIcon( "Image/ZoomMoins_dark.png" ));
                ZoomPlus.setIcon( new ImageIcon( "Image/ZoomPlus_dark.png" ));
                Apres.setIcon( new ImageIcon( "Image/Apres_dark.png" ));

                //changer fond des boutons
                Fichier.setBackground(toolbar.getBackground());
                Mode.setBackground(toolbar.getBackground());
                Music.setBackground(toolbar.getBackground());
                Son.setBackground(toolbar.getBackground());
                SonMoins.setBackground(toolbar.getBackground());
                SonPlus.setBackground(toolbar.getBackground());
                Download.setBackground(toolbar.getBackground());
                Larg.setBackground(toolbar.getBackground());
                Full.setBackground(toolbar.getBackground());

                Avant.setBackground(toolbar.getBackground());
                ZoomMoins.setBackground(toolbar.getBackground());
                Page.setBackground(Sombre_Container);
                ZoomPlus.setBackground(toolbar.getBackground());
                Apres.setBackground(toolbar.getBackground());

                mode = false;
            }else{
                Titre.setForeground(Sombre);
                Page.setForeground(Sombre);
                Max.setForeground(Sombre);
                
                panel.Change(Lumineux_Container);;
                toolbar.setBackground(Lumineux);
                toolbar2.setBackground(Lumineux);

                //change l'icon des boutons
                Fichier.setIcon( new ImageIcon( "Image/Fichier.png" ));
                Music.setIcon( new ImageIcon( "Image/Music.png" ));
                Son.setIcon( new ImageIcon( "Image/Son.png" ));
                SonMoins.setIcon( new ImageIcon( "Image/VolumeMoins.png"));
                SonPlus.setIcon( new ImageIcon( "Image/VolumePlus.png" ));
                Mode.setIcon( new ImageIcon( "Image/Mode.png" ));
                Download.setIcon( new ImageIcon( "Image/Download.png" ));
                Larg.setIcon( new ImageIcon( "Image/Larg.png" ));
                Full.setIcon( new ImageIcon( "Image/Full.png" ));

                Avant.setIcon( new ImageIcon( "Image/Avant.png" ));
                ZoomMoins.setIcon( new ImageIcon( "Image/ZoomMoins.png" ));
                ZoomPlus.setIcon( new ImageIcon( "Image/ZoomPlus.png" ));
                Apres.setIcon( new ImageIcon( "Image/Apres.png" ));

                //changer fond des boutons
                Fichier.setBackground(toolbar.getBackground());
                Mode.setBackground(toolbar.getBackground());
                Music.setBackground(toolbar.getBackground());
                Son.setBackground(toolbar.getBackground());
                SonMoins.setBackground(toolbar.getBackground());
                SonPlus.setBackground(toolbar.getBackground());
                Download.setBackground(toolbar.getBackground());
                Larg.setBackground(toolbar.getBackground());
                Full.setBackground(toolbar.getBackground());  
                
                Avant.setBackground(toolbar.getBackground());
                ZoomMoins.setBackground(toolbar.getBackground());
                Page.setBackground(getContentPane().getBackground());
                ZoomPlus.setBackground(toolbar.getBackground());
                Apres.setBackground(toolbar.getBackground());

                mode = true;
            }
        Reload();
        Update();
            /**** Fin changement de couleur */

    }

    private void LoadMusicListener(ActionEvent event){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}
        JFileChooser selecteur = new JFileChooser();
        selecteur.setCurrentDirectory(new File("Music"));
        selecteur.addChoosableFileFilter(new FileNameExtensionFilter("Music wav", "wav")); // extension possible a selectionner pour le test
        selecteur.setAcceptAllFileFilterUsed(false); // pour faire un sorte que all file ne soit pas selectable
        int resultat = selecteur.showOpenDialog(null);
        if(resultat == JFileChooser.APPROVE_OPTION) {
            musplay.setMusic(selecteur.getSelectedFile().getAbsolutePath());
            test_son=false;
        }
    }

    private void SonListener(ActionEvent event){
        try{
            if(test_son){
                musplay.playMusic(false);
                test_son=false;
            }else{
                musplay.playMusic(true);
                test_son=true;
            }
        }catch(Exception e){
            test_son=false;
        }
    }

    private void SonPlusListener(ActionEvent event){
        try{
            if(test_son){
                musplay.VolumePlus();
            }
        }catch(Exception e){
            test_son=false;
        }
    }

    private void SonMoinsListener(ActionEvent event){
        try{
            if(test_son){
                musplay.VolumeMoins();
            }
        }catch(Exception e){
            test_son=false;
        }
    }

    private void DownloadListener(ActionEvent event){
        try{
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("https://jnovels.com/manga-cbz-cbr-pdf-download/"));
            }
        }catch(Exception e){
        }
        
    }

    private void LargListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Larg
    {
        /*count_zoommoins = 0;
        count_zoomplus = 0;
        count_zoommoins2 = 0;
        count_zoomplus2 = 0;*/
        size_old=0;
        count_zoom.clear();
        pages.reset_coef();
        test_zoom = false;
        Reload();
        if(!test_larg){
            ChangeLargeur();
            test_larg = true;
            test_full = false;
            pages.test_larg = true;
            pages.test_full = false;
        }
        else{
            test_larg=false;
            pages.test_larg=false;
            pages.StandardVue();
            ResizeImage();
        }
        //panel.TestSize(pages);
        //System.out.println("Clic sur le Larg");
        Update();
    }

    private void FullListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Full
    {
        /*count_zoommoins = 0;
        count_zoomplus = 0;
        count_zoommoins2 = 0;
        count_zoomplus2 = 0;*/
        size_old=0;
        count_zoom.clear();
        pages.reset_coef();
        test_zoom = false;
        Reload();
        if(!test_full){
            ChangeFull();
            test_full = true;
            test_larg = false;
            pages.test_full = true;
            pages.test_larg = false;
        }
        else{
            test_full=false;
            pages.test_full=false;
            pages.StandardVue();
            ResizeImage();
        }
        //panel.TestSize(pages);
        //System.out.println("Clic sur le Full");
        Update();
        
    }

    private void AvantListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Larg
    {
        Page.setText(String.valueOf(pages.Avant(panel,(count_zoom.size()>size_old?count_zoom:test))));
        ResizeImage(); 
        Reload();
        Update();
        //count_zoom.clear();
        size_old = count_zoom.size();
        //System.out.println("Clic sur le Avant");
    }

    private void ZoomMoinsListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Larg
    {
        if(test_larg || test_full){
            test_full =false;
            test_larg =false;
        }
        pages.ZoomMoins();
        //System.out.println("Clic sur le ZoomMoins");
        if(pages.getSize().width > panel.getSize().width && pages.getSize().height > panel.getSize().height){
            test_zoom = true;
        }else{
            test_zoom = false;
        }
        ResizeImage();
        Reload();
        Update();
        //count_zoommoins +=1;
        count_zoom.add(2);
    }

    private void ZoomPlusListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Larg
    {
        if(test_larg || test_full){
            test_full =false;
            test_larg =false;
        }
        pages.ZoomPlus();
        //System.out.println("Clic sur le ZoomPlus");
        if(pages.getSize().width > panel.getSize().width && pages.getSize().height > panel.getSize().height){
            test_zoom = true;
        }else{
            test_zoom = false;
        }
        ResizeImage();
        Reload();
        Update();
        count_zoom.add(0);
    }

    private void ApresListener( ActionEvent event ) // event appelé lors de l'appui sur le bouton Larg
    {
        Page.setText(String.valueOf(pages.Apres(panel,(count_zoom.size()>size_old?count_zoom:test))));
        ResizeImage();
        Reload();
        Update();
        //count_zoom.clear();
        size_old = count_zoom.size();
        //System.out.println("Clic sur le Apres");
    }

    private void ResizeImage(){ // fonction appelant le Resize de l'image
    if(test_larg || test_zoom)
        pages.Right();
    else
        pages.Resize(panel);
    }

    private void Update(){
        //System.out.println("l'ancienne taille : "+pages.getSize());
       pages.setSize(pages.getSize().width+1, pages.getSize().height+1);
       pages.setSize(pages.getSize().width-1, pages.getSize().height-1);
       //System.out.println("l'nouvelle taille : "+pages.getSize());
    }

    private void ChangeLargeur(){
        pages.TouteLargeur(panel);
    }

    private void ChangeFull(){
        pages.Full(panel);
    }

    // fonction qui prend en paramemtre un bouton qui fait un sorte qu'il chngede couleur quand la souris le survole
    private void HoverBouton(JButton button){
        button.addMouseListener(new java.awt.event.MouseAdapter() { // ajout d'un écouteur qui se déclanche quand la souris passe au dessus du bouton en question
            public void mouseEntered(java.awt.event.MouseEvent evt) { // quand le bouton est survolé
                button.setBackground(Hover); // chnagement de couleur
                Reload();
            }
        
            public void mouseExited(java.awt.event.MouseEvent evt) { // quand la souris sort de la range
                button.setBackground(toolbar.getBackground()); // prend la couleur actuelle de la toolbar
                Reload();
            }
        });
    }

    /**
     * Recharge le fond derrière l'image. Permet d'éviter un bug graphique du fond lorsque l'on déplace l'image.
     */
    private void Reload(){
        Color act = panel.getBackground(); // récupération de la couleur
        panel.setBackground(Lumineux);
        panel.setBackground(act);
    }

    public void windowClosed(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e)
    {
        String conteneur = "D@ta";
            // Créez le dossier Output s'il n'existe pas.
        File folder = new File(conteneur);
        pages.deleteDirectory(folder); // modification a faire sur la fonction
        System.exit(0);
    }

}