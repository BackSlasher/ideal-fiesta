import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Vector;

import java.util.Timer;
import java.util.TimerTask;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

abstract class MyHandler implements HttpHandler {
  protected void sendString(HttpExchange he, String response, int code) throws IOException {
    he.sendResponseHeaders(code, response.length());
     OutputStream os = he.getResponseBody();
     os.write(response.toString().getBytes());
     os.close();
  }
}

class RootHandler extends MyHandler{
  @Override
  public void handle(HttpExchange he) throws IOException {
    sendString(he, "Go away\n",400);
  }
}


class LeakHandler extends MyHandler{
  static final int mb = 1024 * 1024;
  ArrayList<byte[]> leaks = new ArrayList<>();
  Pattern leakAmount = Pattern.compile("/(\\d+)$");

  @Override
  public void handle(HttpExchange he) throws IOException {
    //String uri = he.getRequestURI().toString();
    //Matcher m = leakAmount.matcher(uri);
    //m.find();
    //int leak_amount = Integer.parseInt(m.group(1));
    int leak_amount=10;
    try{
      leaks.add(new byte[leak_amount * mb]);
      sendString(he, String.format("Leaked %d mb\n", leak_amount),200);
    } catch (Exception e) {
      sendString(he, e.toString(),400);
    }
  }

}

class MemInfoHandler extends MyHandler{
  static final int mb = 1024 * 1024;
  @Override
  public void handle(HttpExchange he) throws IOException {
    Runtime instance = Runtime.getRuntime();
    long usedMb = instance.totalMemory()/mb;
    sendString(he, String.format("%d\n",usedMb), 200);
  }

}



class Main {
  static final int mb = 1024 * 1024;
  static ArrayList<byte[]> leaks = new ArrayList<>();

  public static void main(String args[]) throws Exception{

    Timer timer = new Timer();
    TimerTask myTask = new TimerTask() {
        @Override
        public void run() {
            try{
            int leak_amount=5 * 1024 * 1024; // 5 MB
            leaks.add(new byte[leak_amount]);
            System.out.println("stressed some more");
            } catch (java.lang.OutOfMemoryError e) {
              System.out.println(e);
              System.exit(1);
            }
        }
    };
    timer.schedule(myTask, 2000, 2000);

    int port = 9000;
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    System.out.println("server started at " + port);
    server.createContext("/", new RootHandler());
    server.createContext("/leak", new LeakHandler());
    server.createContext("/mem_info", new MemInfoHandler());
    server.setExecutor(null);
    server.start();
  }
}
