package net.jfabricationgames.notebook.note;

public enum Relation {
	
	NONE(""),//
	BEFORE("<"),//
	AFTER(">"),//
	GREATER(">"),//
	LESS("<"),//
	EQUALS("="),//
	GREATER_EQUALS(">="),//
	LESS_EQUALS("<="),//
	IN("IN");
	
	private final String sign;
	
	private Relation(String sign) {
		this.sign = sign;
	}
	
	public String getSign() {
		return sign;
	}
}