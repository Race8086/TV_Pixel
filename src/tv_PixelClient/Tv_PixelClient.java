/*
* TODO:
*      Implementar parámetros de entrada : Dirección / Puerto del servidor.
 */
package tv_PixelClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.spi.DirStateFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import jdk.internal.org.objectweb.asm.util.ASMifier;
import jdk.nashorn.internal.codegen.CompilerConstants;
import jdk.nashorn.internal.objects.NativeString;


/**
 * Clase que implementa un cliente para comunicación bidireccinal por sockets IP
 * @author loned
 */
class NetworkClient {

    /**
     * Socket de conexión.
     */
    public Socket sClient;              // Socket para establecer la conexión
    /**
     * Stream para enviar datos.
     */
    public PrintWriter dout;            // Enviar datos
    /**
     * Buffer de lectura de datos.
     */
    public BufferedReader _in;           // Recibir datos
    /**
     * Objeto dirección IP del Objeto tv_PixelClient.
     */
    public InetAddress address;         // Dirección del cliente
    /**
     * Puerto de conexión al servidor.
     */
    public int Port;                    // Puerto de conexión
    
    /**
     * Envía mesaje
     * @param msg mensaje a enviar.
     */
    public void sendMessage(String msg) {
        dout.println(msg);

    }

    /**
     * Cierra la conexión establecida, liberado todos los objetos de conexión.
     */
    public void stopConnection() {
        try {
            _in.close();
            dout.close();
            sClient.close();
        } catch (IOException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }  
    /**
     * Registra pixel en la matriz
     * Intenta una conexión al servidor y si le responde satisfactoriamente
     * termina con true, en otro caso devuelve false
     * @return true = registro correcto, false = sin registro o Error
     */
    public boolean regPixel(String _ip,int _port){
        String  mensaje;
        try {
            sClient= new Socket(_ip,_port);
            //String strline=null;
        // Canales para enviar y recibir mensajes
            _in = new BufferedReader(new InputStreamReader(sClient.getInputStream()));
            //dout=new DataOutputStream(sClient.getOutputStream());  
            //br=new BufferedReader(new InputStreamReader(System.in));      
        
            //dout.writeUTF(address.toString());  // Envía la dirección IP
            //dout.flush();
            mensaje = _in.readLine();
            if ("OK".equals(mensaje)){
                System.out.println("Conexión establecida.Pixel ON!");
                return true;
             }
            else{
                stopConnection();
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    /**
     * Elimina pixel de la matriz
     * @return 
     */
    public boolean unRegPixel(){
        
        try {
            dout.close();
            sClient.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    return false;
    }
}
/**
 * Clase para manejar una etiqueta de texto en modo gráfico,
 * se puede elegir el tipo de fuente, el color y el tamaño
 * @author loned
 */
class MyCanvas extends JComponent {

    String cad="A";  
    // creo que no sirve para nada esta cad??
    int x=120;
    int y=80;
    
 /**
  * Método que se invoca automáticamente al refrescar el Canvas
  * @param g objeto gráfico
  */   
  public void paint(Graphics g) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial Black", Font.BOLD, 1100));
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    g.drawString (cad,(int) (screenSize.getWidth()/2) - 480 ,(int) (screenSize.getHeight()/2)+380);  
  }
}
/**
 * Clase que implementa un objeto Tv_PixelClient.
 * Este objeto se registra en una determinada posición de una matriz vistual y acepta comandos
 * de un equipo servidor. Los comando tienen una determinada estructura provocando efectos
 * visuales en el monitor donde se ejecuta este objeto.
 * @author loned
 */
public class Tv_PixelClient {
 /**
  * Marco JFrame que servirá de contenedor al objeto gráfico Canvas.
  */   
 final JFrame frame = new JFrame("Tv_Pixel");
 /**
  * Panel de la ventana
  */
 JPanel panel;
 /**
  * Botón 1. NO IMPLEMENTADO.
  */
 JButton btn1;
 /**
  * Botón 2. NO IMPLEMENTADO.
  */
 JButton btn2;
 /**
  * Objeto para manejar la pantalla.
  */
 MyCanvas gp;                           // objeto para escribir en modo gráfico
 /**
  * Objeto NetworkClient para gestionar las comunicaciones entre Tv_PixelClient y su servidor.
  */
 NetworkClient  Tv_netCli;              // Objeto para conectar el pixel al servidor
 /**
  * Matriz de colores para autodiagnóstico.
  */
 Color secuence[] = {   Color.WHITE,
                        Color.BLACK,
                        Color.BLUE,
                        Color.CYAN,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.ORANGE,
                        Color.RED,
                        Color.PINK,
                        Color.GRAY,
                        Color.BLACK};
 /**
  * Texto de autodiagnóstico.
  */
 char texto[] = {'0','9','8','7','6','5','4','3','2','1','0'};
 /**
  * Componente Red
  */
 int r;               // Componente RED de color
 /**
  * Componente Verde
  */
 int g;               // Componente GREEN de color
 /**
  * Componente Azul
  */
 int b;               // Componente BLUE de color
 /**
  *  Color por defecto de fondo.
  */
 Color _defaultColor = Color.BLACK;
  /**
  *  Color actual de fondo.
  */
 Color _currentColor = Color.BLACK;
 /**
  * Persistencia del contenido en msec.
  */
 int latencia;          // Latencia en msec del contenido
 long timer_l;          // timer de latencia
 /**
  * Ancho en pixels del monitor asociado al cliente.
  */
 double w_width;        // Ancho del display
 /**
  * Alto en pixels del monitor asociado al cliente.
  */
 double w_height;       // Alto del display
 /**
  * Indicador de pantalla completa (true), en modo ventana (false)
  */
 boolean bfe_flag;      // Flag full_screen
 
 int sections;
boolean frezze_flag;    // Flag para desactivar latencia
/**
 * Dispositivo gráfico primario asociado al equipo que corre este cliente.
 */
static GraphicsDevice device = GraphicsEnvironment
        .getLocalGraphicsEnvironment().getScreenDevices()[0];
/**
 * Clase principal que implementa el Pixel.
 */

public  Tv_PixelClient(){
        this.latencia = 125;            // 125 msec.
        this.timer_l = System.currentTimeMillis();
        this.frezze_flag = false;
        this.sections = 1;
        this.r = 0; this.g = 0; this.b = 0;

}
/**
 * Método de autodiagnóstico que comprueba los colores disponibles.
 */
private void testColors(){
    for (int i=0;i<secuence.length;i++){
            //cpanel = new java.awt.Color(r, g, b);
            gp.cad = "" + texto[i];
            frame.getContentPane().setBackground(secuence[i]);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tv_PixelClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
/**
 * Cambia a modo pantalla completa y actualiza atributos de tamaño.
 */
private void setFullScr(){

    device.setFullScreenWindow(frame);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.w_height = screenSize.getHeight();
    this.w_width = screenSize.getWidth();
    this.bfe_flag = true;
}
/**
 * Cambia a modo ventana y actualiza atributos de tamaño.
 * 
 */
private void setWindScr(){

    device.setFullScreenWindow(null);
    this.w_height = frame.getHeight();
    this.w_width = frame.getWidth();
    this.bfe_flag = false;
}
/**
 * Establece el caracter que se presentará en el dispositivo gráfico.
 * @param car Caracter Ascci a mostrar.
 */
private void DumpChar(char car){

if (bfe_flag){
        gp.cad = "" + car;
} else {

}


}
/**
 * Setup inicial de la clase, instanciación de objetos y creación de listeners.
 * 
 */
private void setup(){

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setSize(300,200);
    //frame.setUndecorated(true);
    btn1 = new JButton("Full-Screen");
    btn1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {      
            setFullScr();
        }
    });
    btn2 = new JButton("Normal");
    btn2.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setWindScr();
        }
    });
    gp = new MyCanvas();
//    gp.cad = "Hola";
//    gp.x = 30;
//    gp.y = 100;
//    gp.c = Color.WHITE;
    panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//    frame.getContentPane().add(btn1);
//    frame.getContentPane().add(btn2);
    frame.getContentPane().add(gp);
    //frame.add(panel);
    frame.getContentPane().setBackground(new java.awt.Color(0, 0, 0));
    //setFullScr(); Windowed mode
    frame.setVisible(true);
}

/**
 * Crea un objeto color en JavaSwing a partir de una cadena codificada de caracteres
 * @param scmd sección del comando que contiene las 3 componentes de color.
 * @return Objeto color generado
 */
private Color GenRGB(String scmd){

            r =Integer.parseInt(scmd.substring(1, 4));
            g =Integer.parseInt(scmd.substring(4, 7));
            b =Integer.parseInt(scmd.substring(7, 10));
            return new Color(r,g,b);
}
/**
 * Ejectua el comando que recibe del servidor
 * se supone que los mensajes están correctamente construidos.
 * 
 * @param cmd Comando a ejecutar
  */
public boolean doCmd(String cmd){

boolean result = true;
char token;
Color rgb;

token = cmd.charAt(0);
    switch (token) {

        case 'V':           // VRRRGGGBBBC0000
            rgb = GenRGB(cmd);
            gp.cad = "" +cmd.charAt(10); // Obtiene 'C'
            frame.getContentPane().setBackground(rgb);
            _currentColor = rgb;
            if (Integer.parseInt(cmd.substring(11, 15))== 0) frezze_flag = true;
            break;
        case 'C':           // Comando "CRRRGGGBBB"
            rgb = GenRGB(cmd);
            frame.getContentPane().setBackground(rgb);
            timer_l = System.currentTimeMillis();
            _currentColor = rgb;
            break;
        case 'T':           // Comando "TC"
            gp.cad = "" +cmd.charAt(1); // Obtiene 'C'
            frame.getContentPane().setBackground(_currentColor);
            timer_l = System.currentTimeMillis();
            break;
        case 'L':           // Comando "L0000"
            latencia = Integer.parseInt(cmd.substring(1, 5));
            break;
        case 'F':
            frezze_flag = true;
            break;
        case 'R':
            latencia = 125;
            break;        
        case 'Q':           // Terminar cliente
            result = false;
            break;
        default:
            result = true;
    }
return result;
}
/**
 * Instancia del objeto Tv_PixleClient.
 * Genera un objeto NetworkClient y se registra en el servidor.
 * Si tiene éxito, el método se queda ejecutando un bluce infinito
 * donde se queda a la espera de un comando y lo ejecuta.
 * El método termina cuando recibe el comando 'Q' (Terminar)
 */
public void clientTV(){
    String cmd;
    boolean Pixel_On = true;
    Tv_netCli = new NetworkClient();
    if (Tv_netCli.regPixel("192.168.1.210",8086)){
        setup();                // Setup incial
        //testColors();           // prueba de colores y listeners 
        while (Pixel_On){
                try {
                    cmd = Tv_netCli._in.readLine();
                    Pixel_On= doCmd(cmd);   //Empty black screen
                } catch (IOException ex) {
                    Logger.getLogger(Tv_PixelClient.class.getName()).log(Level.SEVERE, null, ex);
                }
        } // while
        Tv_netCli.unRegPixel();     // Cerramos el socket
    } //if
}
/**
 * Constructor de la clase principal
 * 
 * @param args compatibilidad Os calls
 */
public static void main(String[] args) {

    Tv_PixelClient app = new Tv_PixelClient();
    app.clientTV();
}
    
}
