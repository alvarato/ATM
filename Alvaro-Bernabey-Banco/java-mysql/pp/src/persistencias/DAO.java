package persistencias;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
	

public class DAO {
	private final String DATABASE = "3306";
	private final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String USER = "root";
	private final String PASSWORD = "0218";
	
	protected Connection conexion =  null;
	protected Statement statement = null;
	protected ResultSet resultado = null;
	
	public void conectar() {
		try {
			Class.forName(DRIVER);
			 String URL = "jdbc:mysql://localhost:"+ DATABASE + "/mydb?serverTimezone=UTC";
			 conexion = DriverManager.getConnection(URL,USER,PASSWORD);
			
			//System.out.println("conexion ok");
		} catch (ClassNotFoundException e) {
			System.out.println("Error al cargar el controlador");
		} catch (SQLException e) {
			System.out.println("error a la conexion");
			e.printStackTrace();
		}
		
		
		}
	
	public void desconectar() {
		if(resultado != null) {
			resultado = null;
		}
		
		if(statement !=null) {
			statement = null;
		}
		
//		if(conexion !=null) {
//			conexion = null;
//		}
		
		try {
			conexion.close();
		} catch (Exception e) {
			System.out.println(" error al desconectar");
			e.printStackTrace();
		}
		//System.out.println("desconexion exitosa");
	}
	
	public void insertarModificarELiminar(String sql) throws Exception{
		try {
			conectar();
			statement = conexion.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("error a la conexion");
			e.printStackTrace();
		}
		desconectar();
		
	}
	
	public void buscar(String sql) {
		
		try {
			conectar();
			statement = conexion.createStatement();
			resultado = statement.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
		

}
