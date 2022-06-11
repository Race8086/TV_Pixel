/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*
*   TODO  Cambiar los objetos para leer y escribir por los de la última página de interner
*
*
*/
package tv_PixelServer;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Tv_PixelServer {
    
    public final boolean DEBUG = true;
    int ncon=0;       // Número de conexiones tramitadas
    Socket[][] Tv_matrix;
    PrintWriter [][] out;
    BufferedReader[][] in;  
    String[][] Tv_PixelValue;    
// Atributos para gestión de las comunicaciones con sockets
    Socket sNetCli=null;
    ServerSocket sNetServer=null;
 
   
    
    
    
public boolean Crea_NetServer(){

    try {
        sNetServer = new ServerSocket(6666);
        
    } catch (IOException ex) {
        Logger.getLogger(Tv_PixelServer.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    
return true;
}    
    
/**
 * Genera una matriz de pixel a base de objetos tv_PixelClient, para ello
 * generar un array de conexiones tcp (sockets) con cada uno de los tv_PixelClient disponibles
 * El orden de ubicación dentro del array de pixels viene dado por el momento 
 * en que se establece la conexión con el objeto.Se organizan de izquierda a derecha y de arriba hacia abajo
 * @param filas     Numero de filas
 * @param columnas  Numero de columnas
 * 
 */    
    public void crea_matrix(int filas, int columnas){
        
        Tv_matrix = new Socket[filas][columnas];
        Tv_PixelValue = new String [filas][columnas];
        // bucle para crear las conexiones
        for (int f=0;f<filas;f++){
            System.out.print("\nLinea " + f+ ": ");
            for (int c=0;c<columnas;c++){
                try {
                    Tv_matrix[f][c]=sNetServer.accept();
                    out[f][c] = new PrintWriter(Tv_matrix[f][c].getOutputStream(), true);
                    in[f][c] = new BufferedReader(new InputStreamReader(Tv_matrix[f][c].getInputStream()));
                    out[f][c].println("OK");
                    Tv_PixelValue[f][c]="V000000001 0000"; // Pixel en negro y sin caracter
                    System.out.print("[*]  ");
                } catch (IOException ex) {
                    Logger.getLogger(Tv_PixelServer.class.getName()).log(Level.SEVERE, null, ex);
                } // try-catch
            } // for columnas    
        } // for filas
        System.out.println("\nMatriz generada!");
}    
/**
 * Envía el valor al objeto TV_Pixel, el valor con el que se quiere actualizar
 * @param PixSocket     Objeto Tv_Pixel a actualizar 
 * @param cmd           Comando ( valor ) para el objeto
 */
    public void update_pixel(int fila, int columna, String cmd){
         Tv_PixelValue[fila][columna] = cmd;
    }
    
  /**
   * Establece valor lógico del pixel virtual
   * @param fila        fila de la matriz
   * @param columna     columna de la matriz
   * @param cmd         valor
   */  
    public void set_pixel(int fila, int columna, String cmd){
       
        out[fila][columna].println(cmd);  // Envía valor al cliente
    }
    
    // update matrix necesita que antes se haya generado el 
    // bitmap (actualización de los pixels)
    public void update_matrix(){

        int f;
        int c;
        
        f = Tv_matrix.length;
        c = Tv_matrix[0].length;
        for (int fil=0;fil<f;fil++){
            for (int col=0;col<c;col++){
                set_pixel(fil,col,Tv_PixelValue[fil][col]);
            } // col
        } // fil
    
    
    }
    
    public void close_matrix(){
    // Cierra todas las conexines abiertas
    }

public boolean genera_bitmap(int filas, int columnas){
    
    String cmd;
    int fila;
    int columna;
        cmd = "V000000001 0000";
        fila = 0;
        columna = 0;
        update_pixel(fila, columna, cmd);
        return true;
}    
 public void run(int filas, int columnas){
     System.out.println("Iniciando Server ... ");
   if (Crea_NetServer()){
        crea_matrix(filas,columnas);
        System.out.print("");
        while (genera_bitmap(filas,columnas))
        {
             update_matrix();
        }
        close_matrix();
   }
 }   
/**
 * Inicia un servidor tv_Pixel
 * @param args 
 */ 
    
    public static void main(String args[]){

    Tv_PixelServer app = new Tv_PixelServer();
    app.run(1,1); // Matriz de 2 x 2
    
}

}