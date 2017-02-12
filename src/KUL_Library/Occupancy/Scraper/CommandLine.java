package KUL_Library.Occupancy.Scraper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandLine extends Thread {

    boolean commandLineRunning = true;
    SimpleDateFormat format = new SimpleDateFormat("EEEE dd-MMM-yyyy HH:mm:ss");
    String fileName;
    String defaultLibrary;

    CommandLine() {
        printHelp(); // print instructions by showing what each number does
        loadDefaultSettings();
    }

    public void run() {

        Scanner userInput = new Scanner(System.in);
        //reads user Input (number 0 - 9)

        Survey survey = new Survey(10000, defaultLibrary);

        load(survey); // load all measurements from file in measurement
        survey.start();
        // creates survey that reads the occupancy from the internet

        while (commandLineRunning == true) {
            int input = Integer.valueOf(userInput.nextLine());
            switch (input) {
                case 0:
                    survey.running = false;       
                    commandLineRunning = false;
                    break;
                case 1:
                    System.out.println("the average is " + survey.getAverageOccupancy(survey.getMeasurementList()));
                    break;
                case 2:
                    printHelp();
                    break;
                case 3:
                    System.out.println("Peak time for session: " + survey.getPeakHour());
                    break;
                case 4:
                    System.out.println("Please enter the day for which you want the average.");
                    String str = userInput.nextLine();
                    System.out.println("Average for " + str + " is " + survey.getAverageOccupancy(survey.getListFor(str)));
                    break;
                case 5:
                    System.out.println(survey.overview());
                    break;
                case 6:
                    System.out.println(survey.overviewByDay());
                    break;
                case 7:
                    if (survey.getStreamingStatus() == false) {
                        survey.startStreaming();
                        System.out.println("Streaming started");
                    } else {
                        survey.stopStreaming();
                        System.out.println("Streaming stopped");
                    }
                    break;
                case 8:
                    System.out.println(survey.overviewByHour());
                    break;

                case 9: //9: save measurements into log file and clear measurements in memory
                    save(survey);
                    break;
                case 10: // load measurements from file to memory. The measurements to be read are in format: Occupancy: 51 Sat Jun 25 16:33:58 CEST 2016
                    load(survey);
                    break;
                case 11:
                    Month mon = Month.of(6);
                    System.out.println("Average Occupancy for month " + mon.name() + " is: ");
                    System.out.println(survey.getAverageOccupancy(survey.getListFor(mon)));
                    break;
            }
        }
    }

    private static void printHelp() {
        System.out.println("0: exit");
        System.out.println("1: Average");
        System.out.println("2: print help");
        System.out.println("3: Peak hour for session");
        System.out.println("4: get average Occupancy for A certain day");
        System.out.println("5: Overview");
        System.out.println("6: Overview by day");
        System.out.println("7: toggle streaming");
        System.out.println("8: Overview by hour");
        System.out.println("9: Save measurements into log file and clear measurements in memory");
        System.out.println("10: Load file to memory");
    }

    private void loadDefaultSettings() {
        // Loads default options : the file default.txt which contains configuration for which library will be used and in which file we will load and save to
        try {
            Scanner sc = new Scanner(new File("default.txt"));

            while (sc.hasNext()) {

                sc.skip("library: ");
                this.defaultLibrary = sc.nextLine();
                sc.skip("default file: ");
                this.fileName = sc.next();

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CommandLine.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File default.txt not present");
        }

    }

    private void load(Survey survey) {
        
        try {
            Scanner sc = new Scanner(new File(fileName));
            System.out.println("contents of log file " + fileName + " loaded");
            while (sc.hasNext()) {
                sc.skip("Occupancy: ");
                int occup = sc.nextInt();
                sc.skip(" ");
                String dateText = sc.nextLine();

                Date date = format.parse(dateText);
                Calendar myCal = new GregorianCalendar();
                myCal.setTime(date);

                Measurement measur = new Measurement(occup, myCal);
                survey.addMeasurement(measur);
            }
            sc.close();
        } catch (FileNotFoundException fnfe) {
            System.out.println("File " + fileName + " not found");
        } catch (ParseException ex) {
            Logger.getLogger(CommandLine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//Loads a survey from the file "OccupancyLog.txt" 

    private void save(Survey survey) {
        // Saves a Survey in the file "filename"
        try {
            Files.write(Paths.get(fileName), survey.toString().getBytes(), StandardOpenOption.CREATE);
            int i = survey.getMeasurementList().size();

            //survey.resetMeasurementList();
            // reset the String after dumping
            System.out.println(i + " measurements saved succesfully into log file " + fileName);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            System.out.println("save to file not succesful");
        }

    }
}
