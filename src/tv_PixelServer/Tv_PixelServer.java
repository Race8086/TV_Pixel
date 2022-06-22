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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Clase que implementa el servidor de Pixels, porciona atributos y métodos
 * para establecer una red de monitores y poder enviarles (mediante sockets)
 * los comandos que se desea ejecuten.
 * @author loned
 */
public class Tv_PixelServer {
    
    public final boolean DEBUG = true;
    /**
     * Número de conexiones realizadas. NO IMPLEMENTADO
     */
    int ncon=0;       // Número de conexiones tramitadas
    // meto un comentario
    /**
     * Matriz de sockets para gestionar las conexiones.
     */
    Socket[][] Tv_matrix;
    /**
     * Matriz de streams para envío de mensajes a los Clientes registrados.
     */
    PrintWriter [][] out;
    /**
     * Matriz de streams de lectura de resultados de los clientes.
     */
    BufferedReader[][] in;  
    /**
     * Matriz de valores lógicos de los pixels (Monitores) registrados.
     * 
     * Contiene el comando que se enviará / ejecutará el monitor.
     * 
     */
    String[][] Tv_PixelValue;    
// Atributos para gestión de las comunicaciones con sockets
    /**
     * Socket de cliente.
     */
    Socket sNetCli=null;
    /**
     * Socket de Servidor
     */
    ServerSocket sNetServer=null;  
/**
 * Array de comando de auto-diagnóstico que enviará el servidor una vez
 * generada la matriz de monitores.
 */ 
    String[] texto = {
    
        "V000000001>0000",
        "V111000001R0000",
        "V000111000M0000",
        "V000000111S0000",
        "V000000001*0000",
        "V00000011180000",
        "V00011100160000",
        "V111000001*0000",
        "V111111000B0000",
        "V000111111Y0000",
        "V000000001E0000",
        "V000000001 0000",
        "Q"  
    };
    
/**
 * Genera un socket IP sobre el puerto 8086
 * @return True: Socket creado correctamente, False: error al crear el socket.
 */    
    
public boolean Crea_NetServer(){

    try {
        sNetServer = new ServerSocket(8086);
        
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
        
        // Inicializa estructuras
        Tv_matrix = new Socket[filas][columnas];
        Tv_PixelValue = new String [filas][columnas];
        out = new PrintWriter [filas][columnas];
        in = new BufferedReader[filas][columnas];
        
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
   * Establece valor lógico del pixel virtual. El comnado se envía a través de la conexión socket de salida 
   * establecida entre el servidor y el monitor registrado.
   * @param fila        fila de la matriz (monitor registrado)
   * @param columna     columna de la matriz (monitor registrado)
   * @param cmd         valor en el formato de definido
   */  
    public void set_pixel(int fila, int columna, String cmd){
       
        out[fila][columna].println(cmd);  // Envía valor al cliente
    }
    
       
/**
 * Actualiza todos los pixels de la matriz , enviando el comando que ejecutará
 * el monitor registrado.
 * Llama a set_pixel para cada uno de los monitores registrados.
 * La estructura Tv_PixelValue contiene la matriz de comandos para enviar
 */    
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
/**
 * Destruye la matriz de monitores generada,eliminando todas las conexiones entre
 * servidor y monitores registrados.
 */    
    public void close_matrix(){
    // Cierra todas las conexines abiertas
    int f;
        int c;
        System.out.println("Cerrando matriz...");
        f = out.length;
        c = out[0].length;
        for (int fil=0;fil<f;fil++){
            for (int col=0;col<c;col++){
                out[fil][col].close();
                //in [fil][col].close();               
            } // col
        } // fil
    }

/**
 * Se encarga de generar el bitmap que se mostrará, esencialmente la generación
 * 
 * del bitmap consiste en actualizar cada uno de los pixels (monitores registrado)
 * @param filas resolución en horizontal del bitmap
 * @param columnas resolución en vertical del bitmap
 * @param _frame frame que se va a mostrar
 * @return  true : operación realizada con éxito , false: detener renderización.
 */    
public boolean genera_bitmap(int filas, int columnas,int _frame){
    
    String cmd;
    int fila;
    int columna;
    boolean result;
    
        result = true;
        cmd = texto[_frame];
        fila = 0;
        columna = 0;
        update_pixel(fila, columna, cmd);
        update_pixel(fila, columna+1, cmd);
        update_pixel(fila, columna+2, cmd);
        update_pixel(fila+1, columna, cmd);
        update_pixel(fila+1, columna+1, cmd);
        update_pixel(fila+1, columna+2, cmd);
        if ("Q".equals(cmd)) result = false;
        return result;
}    
/**
 * Instancia del servidor, que ejecuta un bucle de servicio
 * 
 * El servidor permite crear una matriz de filasxcolumnas de monitores que están corriendo una instancia de
 * la clase cliente.El proceso es como sigue:
 * - Se crear un serividor de conexión (Crea_NetServer)
 * - Se registran filas x columnas servidores (crea_matrix)
 * - Se entra en un bucle donde se construye el bitmap lógico (genera_bitmap) y se envía a todos los 
 *   monitores registrados.
 * - Es misión por tanto de genera_bitmap el construir las imágenes con el contenido y temporización que se desee.
 * @param filas número de monitores en horizontal
 * @param columnas número de monitores en vertical
 */ 
public void run(int filas, int columnas){
     
     int _frames=0;
     
     System.out.println("Iniciando Server ... ");
   if (Crea_NetServer()){
        crea_matrix(filas,columnas);
         try {
             TimeUnit.MILLISECONDS.sleep(10000);
         } catch (InterruptedException ex) {
             Logger.getLogger(Tv_PixelServer.class.getName()).log(Level.SEVERE, null, ex);
         }
        System.out.print("");
        while (genera_bitmap(filas,columnas,_frames))
        {
             update_matrix();
             _frames ++;
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tv_PixelServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        close_matrix();
   }
     System.out.println("Terminando el servidor");
     
 }   
/**
 * Inicia un servidor tv_Pixel
 * @param args 
 */ 
    
    public static void main(String args[]){

    Tv_PixelServer app = new Tv_PixelServer();
    app.run(1,3); // Matriz de 2 x 2
        try {
            app.sNetServer.close();
            System.out.println("Cerrando sockets ...");
        } catch (IOException ex) {
            Logger.getLogger(Tv_PixelServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Terminando apicación");
    }

}