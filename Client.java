import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;
import java.util.*;
import java.io.*;



interface ClientIF extends Remote {
   char[][] playerTurn(char[][]board, int playerNum) throws RemoteException,IOException;
   void printstatement(String s) throws RemoteException;
   void printBoard(char [][] board) throws RemoteException;
   void end (int whichCase) throws RemoteException ;
   int playagain() throws RemoteException;
   void endexit() throws RemoteException;
}

public class Client extends UnicastRemoteObject implements ClientIF{

    public static int user=0;
    public boolean exitclient=false;
    public Client() throws RemoteException {
    }

    public void printBoard(char [][] board) throws RemoteException {
        System.out.println(" -------------\n"+" | "+ board[0][0] + " | " + board[0][1] + " | " + board[0][2]+ " | " + "\n -------------");
        System.out.println(" | "+board[1][0] + " | " + board[1][1] + " | " + board[1][2]+ " | " + "\n -------------");
        System.out.println(" | "+board[2][0] + " | " + board[2][1] + " | " + board[2][2] + " | " + "\n -------------");
    }

    public void end (int whichCase){
        if (whichCase == 1){
            System.out.println("**** You win ****");
        }
        else if (whichCase == 2){
            System.out.println("**** You Lose ****");
        }
        else{
            System.out.println("**** Game draw! ****");
        }

    }
    public int playagain() throws RemoteException {
      System.out.println("Play again? Enter 0/1");
      Scanner inp = new Scanner(System.in);
      int num = inp.nextInt();
      return num;
    }
    public void endexit() throws RemoteException{
        exitclient=true;
    }


    public char[][] playerTurn(char[][]board, int activePlayer) throws RemoteException,IOException{
        printBoard(board);
        System.out.println("Enter number(1-9)");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        long startTime = System.currentTimeMillis();
        int num=0;
        while ((System.currentTimeMillis() - startTime) < 10* 1000
        && !in.ready()) {
        }
        if (in.ready()) {
           num = Integer.parseInt(in.readLine());
        } else {
          System.out.println("You did not enter input within timelimit");
          char[][] temp = new char[3][3];
          temp[0][0]='0';
          return temp;
        }
        int row = (num-1)/3;
        int col = (num-1)%3;
        while((num<1 || num>9) || (board[row][col]=='X' || board[row][col]=='O')){
          System.out.println("Invalid input. Please try again");
          num = Integer.parseInt(in.readLine());
          row = (num-1)/3;
          col = (num-1)%3;
        }
        if(activePlayer==1)
        board[row][col]='X';
        else
        board[row][col]='O';
        printBoard(board);
        num=0;
        return board;
    }
    public void printstatement(String s) throws RemoteException {
        System.out.println(s);
    }

  public static void main(String[] args) {
	      Scanner in = new Scanner(System.in);
	      String host = (args.length < 1) ? null : args[0];
	try {
	    Registry registry = LocateRegistry.getRegistry(host);
	    ServerIF stub = (ServerIF) registry.lookup("TicTacToe");
      Client client = new Client();
    //  int num_p = stub.checkPlayers();
      System.out.println("Do you want to play TicTacToe? yes/no");
      String s = in.nextLine();
      if(s.trim().equals("yes")){
        stub.Register(client);
        while(true){
          synchronized(client){
              if(client.exitclient)
              break;
          }
        }
        System.exit(0);
      }
    else{
      System.out.println("Client disconnecting....");
      System.exit(0);
      }

	}

  catch (Exception e) {
	    System.err.println("Client exception: " + e.toString());
	    e.printStackTrace();
	}
    }


}
