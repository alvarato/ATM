package persistencias;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import entidades.User;

public final class Logica extends DAO {
	
	Scanner sn = new Scanner(System.in).useDelimiter("\n");

	//Funcion de login controla si el pin y la cuenta coinciden 
	// si retorna true vuelve a pedir los datos false es que la info es correcta
	public Boolean login(String card,int pin)  {
		String sql = "SELECT pin, number FROM mydb.card WHERE number = '"+card+"';";
		String card1 = "";
		int pin1 = 0;
		try {
			buscar(sql);
			while(resultado.next()) {
				card1 = (String) resultado.getString(2);
				pin1 = (int) resultado.getInt(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Tarjeta o pin Incorrectos");
			return true;
		}
		if(pin1 == pin && card1.equals(card) ) {
			System.out.println("Tarjeta y pin correctos");
			return false;
		}
		System.out.println("Tarjeta o pin Incorrectos");
	return true	;
	}
	
	//guarda el usuario/titular con todos los datos necesario para poder realizar las tareas
	public User guardarUsuario(String card)throws Exception {
		String sql = "SELECT c.pin, c.number, c.user_id, c.cuenta_id, ct.iban FROM mydb.card c"
				+ " LEFT JOIN cuenta ct ON (c.cuenta_id = ct.id)"
				+ " WHERE c.number = '"+card+"';";
		User u2 = new User();
		try {
			buscar(sql);
			while( resultado.next()) {
				u2.setPin((int)resultado.getInt(1)); 
				u2.setCard((String)resultado.getString(2));
				u2.setIdCuenta((long)resultado.getLong(4));
				u2.setIdTitular((long)resultado.getLong(3));
				u2.setIban((String)resultado.getString(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		desconectar();
		return u2;
	}
	
	//controla que el nuevo pin cumpla con los requisitos de 4 digitos 
	//y que no sea igual al anterior antes del cambio
	public Boolean controlPin(User user, int pin2)throws Exception{
		String copiaPin = String.valueOf(pin2);
		if(user.getPin() == pin2) {
			System.out.println("El pin anterior y El nuevo no pueden ser iguales");
			return true;
		}else if(copiaPin.length() == 4){
			System.out.println(user.getCard());
			String sql = "UPDATE mydb.card SET pin = '" + pin2 + "' WHERE number = '"+user.getCard()+"';";
			try {			
				insertarModificarELiminar(sql);
				System.out.println("Pin Cambiado");
				desconectar();
			return false;
			} catch (SQLException e) {
			}

		}
		System.out.println("El pin es menor a 4");
		desconectar();
		return true;
	}

	//controla que el monto a extraer exista en la cuenta y realiza la extraccion
	public void extarer(User user, int monto)throws Exception {
		int monto2 = 0;
		String sql = "SELECT sum(amount) FROM mydb.movement WHERE cuenta_id = '" + user.getIdCuenta() + "';";
		try {
			buscar(sql);
			while( resultado.next()) {
				monto2 = (int) resultado.getInt(1);
				monto2 = monto2/100;
				System.out.println("Su Saldo es:  " + monto2);
			}
		} catch (Exception e) {
			System.out.println();
		}
		
		if(monto2 >= monto) {
			monto *= 100;
			LocalDateTime MyDateObj = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String fechaconformato = MyDateObj.format(myFormatObj);
			try {
				sql = "INSERT INTO movement (amount,sumary,cuenta_id,fecha,categoria)"
						+ " VALUES ('" + (monto)*-1 +"' , '"+ "Extraccion por cajero" +"', "
								+ "'"+ user.getIdCuenta() +"', '"+ fechaconformato + "', 4);";
						insertarModificarELiminar(sql);
						System.out.println("Operacion aceptada tome el monto");
			} catch (SQLException e) {
				// TODO: handle exception
			}
			
		}else {
			System.out.println("No tiene dinero suficiente en la cuenta");
		}
		desconectar();
	}

	//ingresa el deposito desde la caja y realiza un movimiento
	public void depositar(User user,int monto)throws Exception{
		LocalDateTime MyDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String fechaconformato = MyDateObj.format(myFormatObj);
		String sql = "INSERT INTO movement (amount,sumary,cuenta_id,fecha,categoria)"
				+ " VALUES ('" + monto +"' , '"+ "Deposito por cajero" +"', "
				+ "'"+ user.getIdCuenta() +"', '"+ fechaconformato + "', 2);";
		try {
			insertarModificarELiminar(sql);
			System.out.println("El monto ha sido ingresado");
		} catch (SQLException e) {
			System.out.println("Error al ingresar el monto intentelo otra vez");
		}
		desconectar();
	}

	//lista todos los movimientos de una cuenta
	public void listarMovimientos(User user) {
		String sql = "SELECT m.amount, m.sumary, cm.nombre, m.fecha "
				+ "FROM movement m "
				+ "LEFT JOIN categoria_movimiento cm on (m.categoria = cm.id) "
				+ "WHERE m.cuenta_id = '" + user.getIdCuenta() + "';";
		int monto;
		String sumary;
		String categoria;
		String fecha;
		try {
			buscar(sql);
			while(resultado.next()) {
				monto =resultado.getInt(1)/100;
				sumary=resultado.getString(2);
				categoria=resultado.getString(3);
				fecha=resultado.getString(4);
				if(sumary == null) {sumary= "";}
				if(categoria == null) {categoria= "";}
				if(fecha == null) {fecha= "";}
				System.out.println("Monto: " + monto +
						" | Sumary: " + sumary + " | Categoria: "+
						categoria + " | Fecha: " + fecha );
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		desconectar();
	}
	
	//controla si el monto esta en la cuenta
	//realiza la transferencia y crea un movimiento
	public void transferencia(User user, int monto,String iban,String mensaje) throws Exception {
		int monto2 = 0;
		String sql = "SELECT sum(amount) FROM mydb.movement WHERE cuenta_id = '" + user.getIdCuenta() + "';";
		try {
			buscar(sql);
			while( resultado.next()) {
				monto2 = (int) resultado.getInt(1);
				monto2 = monto2/100;
			}
		} catch (Exception e) {
			System.out.println("Error al objtener el monto");
		}
		LocalDateTime MyDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String fechaconformato = MyDateObj.format(myFormatObj);
		if(monto2 <= monto) {
			sql = "INSERT INTO mydb.transfers "
					+ "(amount, iban_t, iban_r, sumary, fecha) "
					+ "VALUES ('"+monto+"', '"+user.getIban()+"', '"+iban+"'," 
					+ " '"+mensaje+"' ,'"+fechaconformato+"');";
			try {
				insertarModificarELiminar(sql);
				System.out.println("Transferencia Realizada");
				try {
					sql = "INSERT INTO movement (amount,sumary,cuenta_id,fecha,categoria)"
							+ " VALUES ('" + (monto)*-1 +"' , '"+ mensaje +"', "
									+ "'"+ user.getIdCuenta() +"', '"+ fechaconformato + "', 3);";
					insertarModificarELiminar(sql);
				} catch (SQLException e) {
					// TODO: handle exception
				}
			} catch (SQLException e) {
			}
		}
		desconectar();
	}

	//permite crear una tarjeta con el dni nombre y pin de un nuevo usuario
	// crea un titular, una cuenta, una tarjeta y relaciona los datos en la base de datos
	public Boolean crearCuenta(String nombre,String dni,int pin) throws Exception {
		// creamos el titular
		String sql;
		String tarjeta = "";
		String iban = "Es02 ";
		int idCuenta=0;
		int idTitular=0;
		sql ="INSERT INTO mydb.titular (name, n_identificacion) VALUES ('"+nombre+"', '"+dni+"');";
		try {
			insertarModificarELiminar(sql);
		} catch (SQLException e) {
			System.out.println("El dni ingresado ya existe");
			return false;
		}
		// creamos el titular
		//Creamos la cuenta

		for (int i = 0; i <16; i++) {
			if(i == 4 || i == 8 || i == 12) {
				tarjeta += " ";
			}
			 int a=  (int)(Math.random()*8+1);
			 tarjeta +=a;
			 a=  (int)(Math.random()*8+1);
			 iban +=a;
		}
		LocalDateTime MyDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String fechaconformato = MyDateObj.format(myFormatObj);
		sql="INSERT INTO mydb.cuenta (fecha_c, iban, descripcion)"
				+ " VALUES ('"+fechaconformato+"', '" + iban + "', '"+"Cuenta Nueva"+"');";
		System.out.println("Su tarjeta: " + tarjeta + " VISA");
		System.out.println("Su iban: " + iban);
		try {
			insertarModificarELiminar(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			return false;
		}
		//Creamos la cuenta
		
		//asociamos la cuenta al titular
		sql = "SELECT id FROM mydb.cuenta WHERE iban = '" + iban + "';";
		try {
			buscar(sql);
			resultado.next();
			idCuenta = (int) resultado.getInt(1);
		} catch (SQLException e) {
			// TODO: handle exception
		}
		sql = "SELECT id FROM mydb.titular WHERE n_identificacion = '" + dni + "';";
		try {
			buscar(sql);
			resultado.next();
			idTitular = (int) resultado.getInt(1);
		} catch (SQLException e) {
			// TODO: handle exception
			return false;
		}
		sql="INSERT INTO mydb.cuenta_titular (cuenta_id, user_id) VALUES ('"+idCuenta+"', '"+idTitular+"');";
		try {
			insertarModificarELiminar(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			return false;
		}
		//asociamos la cuenta al titular
		//Creamos la tarjeta
		sql ="INSERT INTO mydb.card (number, pin, fecha_c, user_id, cuenta_id, activo, tasa)"
				+ " VALUES ('"+tarjeta+"', '"+pin+"', '"+fechaconformato+"', '"+idTitular+"', '"+idCuenta+"', '1', '23.75');";
		//creamos la tarjeta
		try {
			insertarModificarELiminar(sql);
		} catch (SQLException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	//controla que los int que se insertan sean numeros y evita el error 
	//pide una y otra vez un numero hasta que se ingrese uno
	public int controlInt() {
		String ns = sn.nextLine();
		 int n;
		try {
			n = Integer.parseInt(ns);
		} catch (Exception e) {
			System.out.println("no ingreso un numero entero, vuelva a intentarlo");
			n = controlInt();
		}
		return n;
	}

}
