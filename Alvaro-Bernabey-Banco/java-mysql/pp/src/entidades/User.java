package entidades;

public class User {

	private int pin;
	private Long idTitular;
	private Long idCuenta;
	private String card;
	private String iban;
	public User() {}
	
	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public int getPin() {
		return pin;
	}
	public void setPin(int pin) {
		this.pin = pin;
	}
	
	public Long getIdTitular() {
		return idTitular;
	}
	public void setIdTitular(Long idTitular) {
		this.idTitular = idTitular;
	}
	public Long getIdCuenta() {
		return idCuenta;
	}
	public void setIdCuenta(Long idCuenta) {
		this.idCuenta = idCuenta;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	
	
}
