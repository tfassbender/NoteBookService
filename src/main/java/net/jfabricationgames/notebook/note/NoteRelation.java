package net.jfabricationgames.notebook.note;

public enum NoteRelation {
	
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
	
	private NoteRelation(String sign) {
		this.sign = sign;
	}
	
	public String getSign() {
		return sign;
	}
}