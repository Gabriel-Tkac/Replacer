package com.asseco.replacer.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
	
	private static final String REPLACER_HOME = "C://AppTestReplacer";
	private static final String LOGS = REPLACER_HOME + "//logs";
	private static final int DAYS_TO_KEEP_LOGS = 30;
	
	private File log;
	private PrintWriter pw;
	private String time;
	SimpleDateFormat dateTime = new SimpleDateFormat("dd. MM. yyyy HH-mm-ss");
	SimpleDateFormat Time = new SimpleDateFormat("HH-mm-ss");

	public Logger() {
		try {
			File root = new File(REPLACER_HOME);
			if (!root.exists())
				root.mkdir();
			File baseDir = new File(LOGS);
			if (!baseDir.exists())
				baseDir.mkdir();
			 
			time = dateTime.format(new Date());
			this.log = new File(LOGS + "/" + time + ".txt"); 
			FileWriter fw = new FileWriter(log);
			this.pw = new PrintWriter(fw);
			
			File[] logs = baseDir.listFiles();
			for (File logFile: logs) {
				String logFilename = logFile.getName();
				try {
					Date date = dateTime.parse(logFilename.substring(0, logFilename.lastIndexOf(".")));
					LocalDate currentDate = LocalDate.now();
				    LocalDate monthBefore = currentDate.minusDays(DAYS_TO_KEEP_LOGS);
				    LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				    
				    if (localDate.isBefore(monthBefore)) {
				    	logFile.delete();
				    	write("Mazeme log " + logFile.getName());
				    }
				} catch (ParseException e) {
					logException(e, false);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getLog() {
		return log;
	}

	public void setLog(File log) {
		this.log = log;
	}
	
	public void write(String log) {
		pw.println(Time.format(new Date()) + " " + log);
		System.out.println(log);
	}
	
	public void writeException(String message, Exception ex, boolean fullTrace) {
		String trace = message + "\n" + logException(ex, fullTrace);
		pw.println(trace);
		System.out.println(trace);
	}
	
	public String time() {
		return dateTime.format(new Date());
	}
	
	public void close() {
		if (pw != null)
			pw.close();
	}
	
	/**
	 * Prevedie stack trace danej vynimky na retazec
	 * @param e - Exception
	 * @return stack trace
	 */
	public static String exceptionStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString();
		return sStackTrace;
	}
   
	/**
	 * Spracuje stack trace vynimky a zapise udaje do logu
	 * @param e - vynimka
	 * @param printFullTrace - ci sa do vypisu vklada plny stack trace
	 */
   public String logException(Exception e, boolean printFullTrace) {
	   String resultTrace = "";
	   String strExc = exceptionStackTrace(e);
	   
	   if (printFullTrace)
		   resultTrace += strExc + "\n";
	   
	   // First line, containing basic exception description
	   String start = strExc.substring(0, strExc.indexOf("at "));
	   resultTrace += e.getClass().getName() + ": ";
	   
	   // Lines taken by splitting the initial trace
	   List<String> firstLineSplitted = parseFirstLineRecursively(start, new ArrayList<String>());
	   for (String trace: firstLineSplitted)
		   resultTrace += trace;
	   
	   // Lines containing causes of exception
	   List<String> recursiveCauses = parseCauseRecursively(strExc, new ArrayList<String>());
	   for (String trace: recursiveCauses)
		   resultTrace += trace;
		   
	   // Lines including references to our package
	   List<String> recursiveTraces = parseTraceRecursively(strExc, new ArrayList<String>());
	   for (String trace: recursiveTraces)
		   resultTrace += trace;
	   
	   return resultTrace;
   }
   
   static List<String> parseTraceRecursively(String in, List<String> out) {
	   if (in.indexOf("com.asseco") == -1)
		   return out;
	   in = in.substring(in.indexOf("com.asseco."));
	   if (in.indexOf("at ") == -1)
		   return out;
	   
	   String strOfInterest = in.substring(0, in.indexOf("at "));
	   out.add(strOfInterest);
	   in = in.substring(in.indexOf("at "));
   
	   out = parseTraceRecursively(in, out);
	   
	   return out;
   }
   
   static List<String> parseCauseRecursively(String in, List<String> out) {
	   if (in.indexOf("Caused by:") == -1)
		   return out;
	   in = in.substring(in.indexOf("Caused by:"));
	   if (in.indexOf("at ") == -1)
		   return out;
	   
	   String strOfInterest = in.substring(0, in.indexOf("at "));
	   out.add(strOfInterest);
	   in = in.substring(in.indexOf("at "));
   
	   out = parseTraceRecursively(in, out);
	   
	   return out;
   }
   
   static List<String> parseFirstLineRecursively(String in, List<String> out) {
	   
	   if (in.indexOf(": ") == -1)
		   return out;
	   out.add(in.substring(0, in.indexOf(": ")));
	   in = in.substring(in.indexOf(": ") + 2);
	   
	   out = parseFirstLineRecursively(in, out);
	   
	   return out;
   }

}
