package hgw.servant;

import hgw.entity.Entity;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

abstract public class Servant extends Entity {
    protected Double ATK = 0.0; //servant attack stat
    protected Double DEF = 0.0; //servant defense stat
    protected Double STR = 0.0; //servant strength stat

    protected String speech_summon = "I am a generic servant and I have been of summoned.\nAlso hi";

    /* Stats modifiers */
    public Double getATK() {return ATK;} //gets the attack stat
    public Double getDEF() {return DEF;} //gets the defense stat
    public Double getSTR() {return STR;} //gets the strength stat

    public void setATK(Double atk) {ATK = atk;} //sets the attack stat
    public void setDEF(Double def) {DEF = def;} //sets the defense stat
    public void setSTR(Double str) {STR = str;} //sets the strength stat

    /* Speech modifiers */
    public String sp_getSummon() {return speech_summon;}
    public void sp_setSummon(String _speech_summon) {speech_summon = _speech_summon;}

    /* Functions */
    public String summon() {return speech_summon;}

    /* XML Loading */
    public void saveXML(String fileLocation) throws FileNotFoundException {
        XMLEncoder e = new XMLEncoder(new FileOutputStream(fileLocation)); //creates a new XML encoder
        e.writeObject(this); //writes the current object to the XML file
        e.close(); //closes the encoder (also flushes)
    }

    // public void loadXML(String fileLocation) {
    //     XMLDecoder d = new XMLDecoder(new FileInputStream(fileLocation)); //creates a new XML decoder
    //     this = d.readObject();
    //     d.close();
    // }
}