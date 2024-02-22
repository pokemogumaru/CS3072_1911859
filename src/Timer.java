public class Timer {
//Class for other classes to use for timing how long things took.
  private long startTime;
  private long endTime;

  public void start() {
    startTime = System.currentTimeMillis();
  }

  public void stop() {
    endTime = System.currentTimeMillis();
  }

  public String getTotal() {
    long totalTime = endTime - startTime;
    long seconds = totalTime / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    minutes %= 60;
    seconds %= 60;
    return String.format("%d hours, %d minutes, %d seconds", 
         hours, minutes, seconds);
  }
  public String getTotalSeconds()
  {
	  long totalTime = endTime - startTime;
	  long seconds = totalTime / 1000;
	  // Convert seconds to string and enclose in double quotes
	  //CSVs should native support ints but not longs, quotes should help longs be recognised
	  return "\"" + seconds + "\""; 
  }
}