package main;


import java.util.Scanner;

import entidades.User;
import persistencias.Logica;

public class main {
	public static void main(String[] args) throws Exception {
		Scanner sn = new Scanner(System.in).useDelimiter("\n");
		Logica logica= new Logica();
		User user = new User();
		System.out.println("");
		boolean b = false;
        int aux;
        String salida;
        int monto;
        boolean prueba;
        System.out.println("quiere crear un titular con tarjeta? s/n");
        salida = sn.nextLine();
        if(salida.equals("s")) {
        	do {
        		System.out.println("ingrese su dni sin letra");
        		int dnii = logica.controlInt();
        		System.out.println("ingrese la letra de dni");
            	String dni = sn.nextLine();
            	dni = dnii + dni;
            	System.out.println(dni);
            	System.out.println("ingrese su nombre");
                String nombre =  sn.nextLine();
            	System.out.println("ingrese su pin");
            	int pin = logica.controlInt();
            	prueba = logica.crearCuenta(nombre,dni,pin);
        	}while(!prueba);

        }
        do {
        	System.out.println("Bienvenido al Cajero de Splai");
        	do {
                System.out.println("Ingrese Numero de Tarjeta");
                user.setCard(sn.nextLine());
                System.out.println("Ingrese Pin");
                user.setPin(logica.controlInt());
        		b = logica.login(user.getCard(),user.getPin());
        	}while(b);
        	user = logica.guardarUsuario(user.getCard());
            do {
            	
            	System.out.println("1:Realizar Extracíon | 2: Realizar Ingreso");
            	System.out.println("3:Ver Movimientos | 4:Editar Pin");
            	System.out.println("5:Realizar Transferencia");
            	System.out.println("");
            	aux = logica.controlInt();
                switch (aux) 
                {
                    case 1:  System.out.println("Escriba el monto a Extraer");
                    		 monto = logica.controlInt();
                    		 logica.extarer(user, monto);
                    		 break;
                    case 2:  System.out.println("Ingrese el Deposito");
                    		 monto = logica.controlInt();
                    		 monto *= 100;
                    		 logica.depositar(user, monto);
                             break;
                    case 3: System.out.println("Cargando Movimientos");
                    		 logica.listarMovimientos(user);
                             break;
                    case 4:  do{                		 
                    		 System.out.println("Escriba el nuevo pin con 4 digitos");
                    		 monto = logica.controlInt();
                    		 }while(logica.controlPin(user,monto));
                             break;
                    case 5:  System.out.println("Ingrese el monto");
                    	 	 monto = logica.controlInt();
                    	 	 monto *= 100;
                    	 	 System.out.println("Ingrese el iban de la cuenta"
                    	 	 		+ " que va recibir la transferencia"); 
                    	 	 String iban = sn.next();
                    	 	System.out.println("Ingreses un mensaje");
                    	 	String mensaje = sn.next();
                    	 	 logica.transferencia(user, monto, iban, mensaje);
                             break;
                    default: System.out.println("numero erroneo");
                             break;
                }
                
            System.out.println("ingrese s para cerrar la sesion");
            System.out.println("Presione cualquier boton para realizar otra operacion");
            	salida = sn.nextLine();
            	
            }while(salida.equalsIgnoreCase("s"));
            System.out.println("fin de la ejecucion del programa");
        	b = true;
        }while(b);


		

	}
}
	

