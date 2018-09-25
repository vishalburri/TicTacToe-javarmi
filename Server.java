import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.rmi.Remote;
import java.util.*;
import java.io.*;


interface ServerIF extends Remote {
   void Register (ClientIF Client) throws RemoteException;
}

public class Server implements ServerIF {

    public static ArrayList<ClientIF> Clients=new ArrayList<ClientIF>();
    public static int clientNum=0;

    public Server() throws RemoteException{
     }

    public void Register(ClientIF Client) throws RemoteException {
        this.Clients.add(Client);
        clientNum++;
        if(Clients.size()%2==0){
           new clientThread(Clients.get(Clients.size()-2),Clients.get(Clients.size()-1)).start();
        }
        else
        Clients.get(Clients.size()-1).printstatement("Waiting for another player...");
    }
    public static void main(String args[]) {

	     try {
	    Server obj = new Server();
	    ServerIF stub = (ServerIF) UnicastRemoteObject.exportObject(obj, 0);
	    Registry registry = LocateRegistry.getRegistry();
	    registry.bind("TicTacToe", stub);
	    System.err.println("Server ready");

	}  catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	   }
    }


  }
  class clientThread extends Thread implements Runnable{
    private  char board[][] = new char[3][3];
    private  int playerNum=1;
    private  ClientIF p1;
    private  ClientIF p2;
    private static int accept2=-1;
    public clientThread(ClientIF p1,ClientIF p2){
        this.p1=p1;
        this.p2=p2;
    }
    public void initBoard() {
        board[0][0]='1';
        board[0][1]='2';
        board[0][2]='3';
        board[1][0]='4';
        board[1][1]='5';
        board[1][2]='6';
        board[2][0]='7';
        board[2][1]='8';
        board[2][2]='9';
    }
    public void playGame() throws RemoteException,IOException{
      p1.printstatement("players connected.All the Best");
      p2.printstatement("players connected.All the Best");
       int isOver = 0;
       while(true){
         if(Math.random()>0.5){
           ClientIF p3;
           p3=p1;
           p1=p2;
           p2=p3;
           }
           p1.printstatement("You are player1 (X)");
           p2.printstatement("You are player2 (O)");
       while(isOver==0){

           if (playerNum == 1){
               p2.printstatement("Wait for your turn");
               board = p1.playerTurn(board, playerNum);

               if(board[0][0]=='0'){
                 p2.end(1);
                 p1.end(2);
                 break;
              }
           }
           else{
               p1.printstatement("Wait for your turn");
                board = p2.playerTurn(board, playerNum);
                p1.end(1);
                p2.end(2);
                if(board[0][0]=='0'){
                  p1.end(1);
                  p2.end(2);
                  break;
                }
           }
           isOver = checkWin();
           if (isOver == 0){
               isOver = checkFull();
           }

           if (playerNum == 1){
               playerNum = 2;
           }
           else{
               playerNum = 1;
           }

       }
       Thread t1 = new Thread(new clientThread(p1,p2));
       t1.setName("threadp2");
       t1.start();
       int accept1 = p1.playagain();
        try{
        t1.join();
        }
        catch(Exception e){
          System.out.println(e);
        }
        if(accept1==1 && accept2==1){
        isOver=0;
        initBoard();
        playerNum=1;
        accept2 = -1;
        continue;
        }
        else{
          if(accept1==1)
          p1.printstatement("sorry other player is busy");
          if(accept2==1)
          p2.printstatement("sorry other player is busy");
          accept2=-1;
          playerNum=1;
          break;
        }

     }
      p1.printstatement("Client disconnecting..");
      p2.printstatement("Client disconnecting..");
      p1.endexit();
      p2.endexit();

     }
    public int checkWin() throws RemoteException{
        int i;
        int x;

        for (i=0;i<3;i++){ //checking rows
            if ((board[i][0] == 'X' && board[i][1] == 'X' && board[i][2] == 'X') || (board[i][0] == 'O' && board[i][1] == 'O' && board[i][2] == 'O')){
                if (playerNum == 1){
                    p1.end(1);
                    p2.end(2);
                }
                else {
                    p1.end(2);
                    p2.end(1);
                }
                return 1;
            }
        }
        for (i=0;i<3;i++){ //checking columns
            if ((board[0][i] == 'X' && board[1][i] == 'X' && board[2][i] == 'X') || (board[0][i] == 'O' && board[1][i] == 'O' && board[2][i] == 'O')){
                if (playerNum == 1){
                    p1.end(1);
                    p2.end(2);
                }
                else {
                    p1.end(2);
                    p2.end(1);
                }
                return 1;
            }
        }

        if ((board[0][0] == 'X' && board[1][1] == 'X' && board[2][2] == 'X') || (board[0][2] == 'X' && board[1][1] == 'X' && board[2][0] == 'X') || (board[0][0] == 'O' && board[1][1] == 'O' && board[2][2] == 'O') || (board[0][2] == 'O' && board[1][1] == 'O' && board[2][0] == 'O')){
                if (playerNum == 1){
                    p1.end(1);
                    p2.end(2);
                }
                else {
                    p1.end(2);
                    p2.end(1);
                }
                return 1;
            }
        return 0;
    }
    public int checkFull() throws RemoteException{
        int i;
        int x;
        int full=0;
        for (i=0;i<3;i++){
            for (x=0;x<3;x++){
                if (board[i][x] == 'X' || board[i][x]=='O'){
                    full++;
                }
            }
        }
        if (full == 9){
            p1.end(3);
            p2.end(3);
            return 1;
        }
        else{
            return 0;
        }
    }
    public void run(){
      if(Thread.currentThread().getName().equals("threadp2")){
        try{
         accept2 = p2.playagain();
        }
        catch(Exception e){

        }
      }
      else{
        initBoard();
        try{
        playGame();
        }
        catch(Exception e){

        }
    }
    }

  }
