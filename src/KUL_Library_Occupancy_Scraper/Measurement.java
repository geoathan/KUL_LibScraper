package KUL_Library_Occupancy_Scraper;

import org.jsoup.Jsoup;
//import org.jsoup.Jsoup.*;
import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.*;
//import java.io.*;
//import java.text.*;
import java.text.SimpleDateFormat;

public class Measurement {

    
    int occupancy;
    Calendar date;
    SimpleDateFormat format = new SimpleDateFormat("EEEE dd-MMM-yyyy HH:mm:ss");
    private String library;
   
    Measurement(String library) {
        // -1 occupancy means the measurement did not take place.
        occupancy = -1;
        try {
            this.library=library;
            Document doc = Jsoup.connect("http://bib.kuleuven.be/apps/ub/blokkeninleuven/php/teller_plone.php?bib=" + library + "&toonaantal=1").get();// connects to Agora website ,replace agora with cba to get occupancy of cba
            org.jsoup.select.Elements links = doc.getAllElements();
            String str = "" + links.get(14);
            str = str.substring(120, 123);
            //System.out.println(str); // for debugging

            // specific for the agora website, sometimes the number has 1 digit, sometimes 2, sometimes 3.
            //The program needs a method of reading the correct string everytime
            // In the begining we get all 3 digits, if there is a " /" in the 
            //digits it means we only have 1 digit which is a number so we get that one
            //if the digits include a space " " then it means we have one extra and we only
            //have to take 2 of them.
            //finally if " " and " /" are not present it means that we have 3 digits so we keep
            // the original combination
            // perhaps there is a more elegant and generalized way to do this?
            if (str.contains(" /")) {
                occupancy = Integer.valueOf(str.substring(0, 1));
            } else if (str.contains(" ")) {
                occupancy = Integer.valueOf(str.substring(0, 2));
            } else {
                //System.out.println("yo");
                occupancy = Integer.valueOf(str);
            }
        } catch (IOException ex) {
            Logger.getLogger(Agora.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not access website, check internet conection");
        }

        this.occupancy = occupancy; // sets occupancy for this measurement
        this.date = Calendar.getInstance(Locale.UK); //sets date for this measurement
    }

    Measurement(int occupancy, Calendar date) {
        this.occupancy = occupancy;
        this.date = date;
    }

    public int getOccupancy() {//getter of number of people in Agora
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public Calendar getDate() {//getter of time and date
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getMonth() {
        return this.getDate().get(Calendar.MONTH) + 1;
    }

    @Override
    public String toString() {// returns string with occupancy and date in raw date format
        return "Occupancy: " + this.getOccupancy() + " " + this.getDate().getTime().toString();
    }

    public String toStringFormatted() {// returns string with occupancy and date in formated date format
        return "Occupancy: " + this.getOccupancy() + " " + format.format(this.getDate().getTime());
    }
}
