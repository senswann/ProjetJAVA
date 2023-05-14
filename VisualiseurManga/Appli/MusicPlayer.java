import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Cette classe permet de gérer la musique d'ambiance. <br /> <br />
 * Fonctionnalités :
 * <ul>
 * <li>trouver le fichier de la musique</li>
 * <li>stopper et lancer la musique</li>
 * <li>changer le volume de la musique</li>
 * </ul>
 * 
 * @see Thread
 */
public class MusicPlayer extends Thread {

    /**
     * Stock la musique utilisé.
     */
    private Clip clip;  //variable stockant la music actuelle

    /**
     * Boolean pour savoir si le player est en cours.
     */
    private boolean plays=false; // boolean pour savoir si le player est en cours
   
    /**
     * Constructeur par défaut.
     */
    MusicPlayer(){}

    /**
     * Change la musique actuelle.
     * 
     * @param mus chemin vers le fichier de la musique.
     * @see javax.sound.sampled.AudioInputStream
     */
    public void setMusic(String mus){ // fonction changeant la music actuelle
        if(plays){ // si music en cours alors pause
            clip.stop();
            clip.close();
        }
        try{ // try faisant la recherche de la music rehcerché et la stockant dans Clip
            AudioInputStream input=AudioSystem.getAudioInputStream(new File(mus));
            clip=AudioSystem.getClip();
            clip.open(input);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stoppe ou lance la musique selon le boolean en paramètre.
     * 
     * @param play boolean qui interchange de valeur entre true et false pour interchanger entre lancer et stopper la musique.
     */
    public void playMusic(boolean play){ // fonction stopant ou lancant la music selon le boolean en parametre
        if(play){ // si true on lance la music
            if(clip!=null){
                plays=true;
                //System.out.println("start music");
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                //System.out.println(clip);
            }
            clip.loop(99);
        }else{ // si faux on stop la music
            plays=false;
            clip.stop();
            //System.out.println("fin music");
        }
    }

    public void PlayLoad(){
        if(!plays){ // si true on lance la music
            setMusic("Music/load.wav");
            setVolume(getVolume()-0.95f);
            playMusic(true);
        }else{ // si faux on stop la music
            plays=false;
            clip.stop();
            clip.close();
        }
    }
    
    /**
     * Récupère le volume de la musique actuelle.
     * 
     * @return le volume de la musique en float.
     */
    public float getVolume() { // fonction récupérant le volume de la music actuelle
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);  // récupere le volume du clip actuelle
        return (float) Math.pow(10f, volumeControl.getValue() / 20f); // calcul permetant d'avoir le volume de la music en float
    }
    
    /**
     * Moddifie le volume de la musique.
     * Vérifie d'abord que la valeur en paramètre soit bien comprise entre 0 et 1f, puis effectue un calcul pour passer du type FloatControl au type float.
     * 
     * @param volume float de la valeur pour laquelle cela modifie le volume.
     * @see javax.sound.sampled.FloatControl
     */
    public void setVolume(float volume) { // fonction permettant de modifié le volume selon un volume donné en parametre
        if (volume < 0f || volume > 1f) // boucle permettant de controller que la music soit bien compris entre 0 et 1f 
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);  // récupere le volule du clip actuelle
        volumeControl.setValue(20f * (float) Math.log10(volume)); // effectue les changement avec le volume passé en parametre
    }

    /**
     * 
     */
    public void VolumePlus(){ // fonction appelé pour augmenter le volume du clip
        if(getVolume()+0.05f < 1f)
            setVolume(getVolume()+0.05f);
    }

    public void VolumeMoins(){ // fonction appelé pour baisser le volume du clip
        if(getVolume()-0.05f > 0f)
            setVolume(getVolume()-0.05f);
    }

}
