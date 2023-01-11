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

  @Override
  public void handle(HttpExchange he) throws IOException {
    int leak_amount_mb = Integer.parseInt(he.getRequestHeaders().getFirst("memory_mb"));
    int leak_amount=leak_amount_mb * mb;
    try{
      leaks.add(new byte[leak_amount]);
      sendString(he, String.format("Leaked %d mb\n", leak_amount),200);
      } catch (java.lang.OutOfMemoryError e) {
        System.out.println(e);
        System.exit(1);
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
