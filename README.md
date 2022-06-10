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
+ C-RRRGGGBB        Establece color de fondo a RRR-GGG-BBB
+ L-0000            Persistencia del color actual hasta pasar a desactivar el pixel (C-000000000)
+ F                 Freeze : Congela la visualización hasta recibir un nuevo comando de color o R
+ R                 Restablece la latencia por defecto
+ T-C               Imprime el caracter ASCCI especificado por C
+ V-RRRGGGBBBC0000  Comando único que supone la unión de los anteriores
                    si L = 0000 es equivalente al comando Freeze (F)
+ Q                 Termina el programa
