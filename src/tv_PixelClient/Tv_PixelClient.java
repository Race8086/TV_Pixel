/*
* Clase cliente, solo para sincronizar push
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
import java.net.InetAddress;
import java.net.Socket;
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

 
class NetworkClient {

    public Socket sClient;
    public DataInputStream din;
    public DataOutputStream dout;
    public BufferedReader br;
    public InetAddress address;
    public int Port;
    /** constructor de la clase
     *  estblece IP del cliente y puerto de conexión
     * @throws IOException  Sin implementar 
     */
    public void NetworkClient() throws IOException{

        address=InetAddress.getLocalHost();
        Port = 8086;
        // ¿Y si falla?
    }
    
    /**
     * Registra pixel en la matriz
     * Intenta una conexión al servidor y si le responde satisfactoriamente
     * termina con true, en otro caso devuelve false
     * @return true = registro correcto, false = sin registro o Error
     */
    public boolean regPixel(){
        String  mensaje;
        try {
            sClient= new Socket("localhost",6666);
            //String strline=null;
        // Canales para enviar y recibir mensajes
            din=new DataInputStream(sClient.getInputStream());  
            //dout=new DataOutputStream(sClient.getOutputStream());  
            //br=new BufferedReader(new InputStreamReader(System.in));      
        
            //dout.writeUTF(address.toString());  // Envía la dirección IP
            //dout.flush();
            mensaje = din.readUTF();
            if ("OK".equals(mensaje)){
                System.out.println("Conexión establecida.Pixel ON!");
                return true;
             }
            else{
                dout.close();
                sClient.close();
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
 * Maneja una etiqueta de texto en modo gráfico,
 * se puede elegir el tipo de fuente, el color y el tamaño
 * @author loned
 */
class MyCanvas extends JComponent {

    String cad="A";
    int x=120;
    int y=80;
  public void paint(Graphics g) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial Black", Font.BOLD, 1100));
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    g.drawString (cad,(int) (screenSize.getWidth()/2) - 480 ,(int) (screenSize.getHeight()/2)+380);  
  }
}
/**
 *
 * @author loned
 */

// Issue  1: No funciona la creación del color a partir de los componentes rgb 
public class Tv_PixelClient {
 final JFrame frame = new JFrame("Tv_Pixel");
 JPanel panel;
 JButton btn1;
 JButton btn2;
 MyCanvas gp;                           // objeto para escribir en modo gráfico
 NetworkClient  Tv_netCli;              // Objeto para conectar el pixel al servidor
 
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
 char texto[] = {'0','9','8','7','6','5','4','3','2','1','0'};
 int r;               // Componente RED de color
 int g;               // Componente GREEN de color
 int b;               // Componente BLUE de color
 Color _defaultColor = Color.BLACK;
 Color _currentColor = Color.BLACK;
 int latencia;          // Latencia en msec del contenido
 long timer_l;          // timer de latencia
 double w_width;        // Ancho del display
 double w_height;       // Alto del display
 boolean bfe_flag;      // Flag full_screen
 int sections;
boolean frezze_flag;    // Flag para desactivar latencia
    /**
     * @param args the command line arguments
     */
static GraphicsDevice device = GraphicsEnvironment
        .getLocalGraphicsEnvironment().getScreenDevices()[0];
public  Tv_PixelClient(){
        this.latencia = 125;            // 125 msec.
        this.timer_l = System.currentTimeMillis();
        this.frezze_flag = false;
        this.sections = 1;
        this.r = 0; this.g = 0; this.b = 0;

}
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
 * Cambia a modo pantalla completa y actualiza atributos de tamaño
 */
private void setFullScr(){

    device.setFullScreenWindow(frame);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.w_height = screenSize.getHeight();
    this.w_width = screenSize.getWidth();
    this.bfe_flag = true;
}
/**
 * Cambia a modo ventana y actualiza atributos de tamaño
 * 
 */
private void setWindScr(){

    device.setFullScreenWindow(null);
    this.w_height = frame.getHeight();
    this.w_width = frame.getWidth();
    this.bfe_flag = false;
}
private void DumpChar(char car){

if (bfe_flag){
        gp.cad = "" + car;
} else {

}


}
/**
 * Setup inicial de la clase, instanciación de objetos y creación de listeners
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
 * EL cliente se intenta registar al servidor de pixels, y si lo consigue
 * se queda en un blucle esperando comandos y ejecutándolos
 */
public void clientTV(){
    String cmd;
    boolean Pixel_On = true;
    Tv_netCli = new NetworkClient();
    if (Tv_netCli.regPixel()){
        setup();                // Setup incial
        //testColors();           // prueba de colores y listeners
        
        while (Pixel_On){
                try {
                    cmd = Tv_netCli.din.readUTF();
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
