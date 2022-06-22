# Tv_Pixel
Crea una clase que maneja un terminal en modo FullScreen , trantando al equipo como un pixel direccionable por su IP
## Objetivo
La clase acepta una serie de comando que le permite dibujar un fondo RGB uniforme en toda la superfice de la pantalla.
Adicionalmente se puede imprmir un caracter de gran tamaño.
### Especificaciones iniciales
La clase cuando se instancia, genera una ventana en modo pantalla completa (FullScreen) y se queda esperando a recibir un mensaje remoto o a que de modo local , reciba la pulsación de la tecla F10.
El color por defecto es 0 (BLACK)

COMANDOS DISPONIBLES
- - - - - - - - - - - - - -
+ CRRRGGGBB        Establece color de fondo a RRR-GGG-BBB
+ L0000            Persistencia del color actual hasta pasar a desactivar el pixel (C-000000000)
+ F                Freeze : Congela la visualización hasta recibir un nuevo comando de color o R
+ R                Restablece la latencia por defecto
+ TC               Imprime el caracter ASCCI especificado por C
+ V-RRRGGGBBBC0000  Comando único que supone la unión de los anteriores
                    si L = 0000 es equivalente al comando Freeze (F)
+ Q                 Termina el programa

Solo están probados los comandos V y Q

### TODO
EL proyecto se ha depurado y probado con una configuración fija, los dispositivos cliente, ejecutan una rutina de autodiagnóstico y el servidor también ejecuta un código predefinido para comprobar el funcionamiento una vez creada la matriz. 
Las funcionales pendientes de implementar son:
+ Argumentos para el cliente : Dir IP de conexión y puerto
+ Argumentos de servidor : Puerto de conexión, tamaño de la matriz ( filas x columnas)
+ Modificar el método genera_bitmap para que se conecte a una fuente externa ( fichero de escena o proceso remoto que envía el flujo de frames a visualizar)

Madrid a 22 de junio de 2022
