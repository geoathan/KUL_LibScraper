package KUL_Library_Occupancy_Scraper;

//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
import static java.lang.Math.sqrt;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

public class Survey extends Thread {

    ArrayList<Measurement> measurementList = new ArrayList<>();
    boolean running = true;
    boolean streaming = true; //wether or not the measurements are streamed in the terminal
    int delay;
    private final String library;

    Survey(int delay, String lib) {
// constructor for Survey. delay is the delay between two consecutive readings
        this.library=lib;
        this.delay = delay;
    }
    
    String getLibrary() {return library;}

    @Override
    public void run() {

        /* PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Agora.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Agora.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        Measurement measurement;

        while (running == true) {

            measurement = new Measurement(library);
            measurementList.add(measurement);

            if (streaming == true) {
                System.out.println(measurement.toStringFormatted());
            }
            //writer.println("Occupancy : " + measurement.getOccupancy() + ", " + formatedTimeOfMeasurement);

            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(Agora.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        // writer.close();
    }

    void addMeasurement(Measurement input) {
        measurementList.add(input);
    }
    
    ArrayList<Measurement> getMeasurementList() {return measurementList;}
  //////////////////////////////////////////////////////////////////////////////
    String getPeakHour() {
        int max = 0;
        Measurement maxi = new Measurement(library);
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd-MMM-yyyy HH:mm:ss");

        for (Measurement current : measurementList) {
            if (current.getOccupancy() > max) {
                max = current.getOccupancy();
                maxi = current;
            }
        }
        return format.format(maxi.getDate().getTime());

    }

    public String overviewByDay() {
        String output = "\nOverview of average occupancy by day:\n"
                + "***************************************************\n"
                + "Monday average occupancy: " + Survey.this.getAverageOccupancy(getListFor("Monday")) + "\n"
                + "Tuesday average occupancy: " + Survey.this.getAverageOccupancy(getListFor("Tuesday")) + "\n"
                + "Wednesday average occupancy: " + Survey.this.getAverageOccupancy(getListFor("Wednesday")) + "\n"
                + "Thursday average occupancy: " + Survey.this.getAverageOccupancy(getListFor("Thursday")) + "\n"
                + "Friday average occupancy: " + Survey.this.getAverageOccupancy(getListFor("Friday")) + "\n"
                + "Saturday average occupancy: " + Survey.this.getAverageOccupancy(getListFor("Saturday")) + "\n"
                + "Sunday average occupancy: " + Survey.this.getAverageOccupancy(getListFor("Sunday")) + "\n"
                + "***************************************************";
        return output;
    }

    public String overviewByHour() {
        String output = null;
        output = "\n\n Averages by hour \n**************************************\n";
        for (int hour = 0; hour < 24; hour++) {
            if (this.getAverageOccupancy(this.getListFor(hour)) == 0) {
                output += "for " + hour + " hours the average is non-defined\n";
            } else {
                output += "for " + hour + " hours, the average occupancy is " + this.getAverageOccupancy(this.getListFor(hour)) + "\n";
            }
        }
        output += "*****************************************";
        return output;
    }

    public String overview() {
        String output = "\n\nOverview of all measurements in Survey \n********************************************************** \n";
        output += this.toString();
        output += "**********************************************************";
        return output;
    }

    @Override
    public String toString() { //dumps measurement data
        String output = "";
        for (Measurement current : measurementList) {

            output += current.toStringFormatted(); // formated
            //output += current.toString();// raw not formatted date
            output += "\n";

        }
        return output;
    }

    /**
     * Deletes all measurements in the survey.
     */
    public void resetMeasurementList() {//resets arraylist of measurement DANGER
        measurementList = new ArrayList<>();
    }

////////////////////////////////////////////////////////////////////////////////
// Control measurement Streaming (showing the measurements live in the terminal)
    public boolean getStreamingStatus() {
        return streaming;
    }

    public void stopStreaming() {
        streaming = false;
    }

    public void startStreaming() {
        streaming = true;
    }
////////////////////////////////////////////////////////////////////////////////
    // GetListFor()' methods. Filtering measurements
     
    public ArrayList<Measurement> getListFor(Calendar start,Calendar finish){
         
        ArrayList<Measurement> temp = new ArrayList<Measurement>();
        for (Measurement current : measurementList) {
            if (current.getDate().before(start) && current.getDate().after(finish)) {
                temp.add(current);
            }
        }
        return temp;
    
    }
    
    public ArrayList<Measurement> getListFor(int hour)  {
     ArrayList<Measurement> hourList = new ArrayList<Measurement>();
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");

        for (Measurement current : measurementList) {
            int test = Integer.valueOf(hourFormat.format(current.getDate().getTime()));
            if (test == hour) {
                hourList.add(current);
            }
        }
        return hourList;
    } // returns a list that has measurements for specific hour

    public ArrayList<Measurement> getListFor(String day) {
    ArrayList<Measurement> dayList = new ArrayList<Measurement>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

        for (Measurement current : measurementList) {
            if (dayFormat.format(current.getDate().getTime()).equals(day)) {
                dayList.add(current);
            }
        }
        return dayList;
    } // returns a list that has measurements for specific day
    
    public ArrayList<Measurement> getListFor(Month input) {
     ArrayList<Measurement> temp = new ArrayList<Measurement>();
        for (Measurement current : measurementList) {
            int test1 = current.getMonth();
            int test2 = input.getValue();
            if (test1 == test2) {
                temp.add(current);
            }
        }
        return temp;
    } // returns a list that has measurements for specific Month
    
    ////////////////////////////////////////////////////////////////////////////
    
    public float getAverageOccupancy(ArrayList<Measurement> input) {
        int sum = 0;
        for (Measurement current : input) {
            sum += current.getOccupancy();
        }
        if (sum == 0) {
            return 0;
        } else {
            return ((float) sum) / input.size();
        }
    } // used in getAverage functions to find average occupancy of a measurement List
    
    public int getPeakOccupancy(ArrayList<Measurement> input){
     int max=0;
        for (Measurement current : input) {
            if (current.getOccupancy() > max) {
                max = current.getOccupancy();
            }
        }
        return max;
    } // returns the value of the maximum Occupancy in a measurement List
    
    public Calendar getPeakTime(ArrayList<Measurement> input) {
     int max = 0;
        Measurement maxi = new Measurement(library);
        for (Measurement current : measurementList) {
            if (current.getOccupancy() > max) {
                max = current.getOccupancy();
                maxi = current;
            }
        }
        return maxi.getDate();
    }
    
    public float getVariance(ArrayList<Measurement> input){
    
        float avg = getAverageOccupancy(input);
        float sum = 0 ;
        for (Measurement current : input )
        {
           sum += (avg - current.getOccupancy())*(avg - current.getOccupancy()) ;
        }
        return sum/input.size();
    }

    public float getStandardDeviation(ArrayList<Measurement> input){
    return (float) sqrt(getVariance(input));
    }
    
    public String toString(ArrayList<Measurement> input) { //dumps measurement data
        String output = "";
        for (Measurement current : input) {

            output += current.toStringFormatted(); // formated
            //output += current.toString();// raw not formatted date
            output += "\n";

        }
        return output;
    }//converts one measurement list to String 
    
    public String overview(ArrayList<Measurement> input) {
        String output = "\n\nOverview of all measurements in Survey \n********************************************************** \n";
        output += toString(input);
        output += "**********************************************************";
        return output;
    }// Same as toString(ArrayList<Measurement> input) but also adds label and asterisks '*'
    

}
