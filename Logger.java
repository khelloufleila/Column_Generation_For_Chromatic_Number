package column_generation;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	//private Statement statement;
	private StopWatch st;
	private String session;
	private String pc_name;
	private class StopWatch {
		  private long startTime = 0;
		  private long stopTime = 0;
		  private boolean running = false;
		  public void start() {
		    this.startTime = System.currentTimeMillis();
		    this.running = true;
		  }
		  public void stop() {
		    this.stopTime = System.currentTimeMillis();
		    this.running = false;
		  }
		  //elapsed time in seconds
		  public long elapsedTimeSec() {
		    long elapsed;
		    if (running) {
		      elapsed = ((System.currentTimeMillis() - startTime) / 1000);
		    }
		    else {
		      elapsed = ((stopTime - startTime) / 1000);
		    }
		    return elapsed;
		  }
		} 
	public Logger() {
		try {
			
			st = new StopWatch();
			st.start();
			DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
			Date session_date = new Date();
			session = dateFormat.format(session_date);
			pc_name = InetAddress.getLocalHost().getHostName();
		}
		catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	public double timeStamp() {
		return st.elapsedTimeSec();
	}
	 public void writeLog(String instance, double obj, double bb) {
	    	System.out.println("INSERT INTO Logs VALUES ('" + session + "','" + instance + "'," + timeStamp() + "," + obj + "," + bb + ",'" + pc_name + "');");
	    }
}
