package cs421;
/**
 * Super Hello class to demonstrate power of ant and javadoc
 *
 * @author Sidney Chen
 */


public class SuperHello extends SuperGreeting {
  
  
  public static void main (String[] args) {
    
    SuperHello greeting = new SuperHello();
    greeting.printHelloWorld();
    
  }
  
  /**
   * Main method for SuperHello class that
   * prints the hello world string.
   *
   */
  public void printHelloWorld() {
    System.out.println("Hello World\n");
  }  
 

}





