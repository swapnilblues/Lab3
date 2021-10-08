package client;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import java.util.concurrent.CountDownLatch;

import java.io.*;

public class HttpClientTutorial {

  final static private int NUMTHREADS = 100;
  private int count = 0;

  private static String url = "http://localhost:8080/Lab2Final_war_exploded/hello_world/";

  public void inc() {
    count++;
  }

  public int getVal() {
    return this.count;
  }


  public static void hitServer() {
    // Create an instance of HttpClient.
    HttpClient client = new HttpClient();

    // Create a method instance.
    GetMethod method = new GetMethod(url);

    // Provide custom retry handler is necessary
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler(3, false));

    try {
      // Execute the method.
      int statusCode = client.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + method.getStatusLine());
      }

      // Read the response body.
      byte[] responseBody = method.getResponseBody();

      // Deal with the response.
      // Use caution: ensure correct character encoding and is not binary data
      System.out.println(new String(responseBody));

    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Release the connection.
      method.releaseConnection();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    final HttpClientTutorial counter = new HttpClientTutorial();
    CountDownLatch  completed = new CountDownLatch(NUMTHREADS);

    long startTime = System.currentTimeMillis();
    System.out.println("Start Time: " + startTime);
    for (int i = 0; i < NUMTHREADS; i++) {
      // lambda runnable creation - interface only has a single method so lambda works fine
      Runnable thread = () -> {
        hitServer();
        counter.inc();
        completed.countDown();
      };
      new Thread(thread).start();
      System.out.println("Curr Time: " + System.currentTimeMillis());
    }

//    Thread.sleep(5000);

    completed.await();
    long endTime = System.currentTimeMillis();
    System.out.println("End Time: " + endTime);
    System.out.println("Total time taken: "+ (endTime-startTime));

    System.out.println("Value should be equal to " + NUMTHREADS + " It is: " + counter.getVal());
  }
}
