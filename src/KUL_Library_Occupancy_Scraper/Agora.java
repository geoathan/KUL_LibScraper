package KUL_Library_Occupancy_Scraper;


public class Agora {
//main program loop. Starts command line thread 
//Agora class starts a CommandLine thread. The commandLine thread starts a survey thread which takes measurements in a specific frequency
    public static void main(String[] args) {
        
       CommandLine command = new CommandLine(); // create command line object, which is a thread
       command.start(); // start command line, run gets executed
        
        
        
    //??? why do i need this?
      //  try {
      //      Thread.sleep(3000);
       // } catch (InterruptedException ex) {
       //     Logger.getLogger(Agora.class.getName()).log(Level.SEVERE, null, ex);
       // }
            
          
    }
}
