package com.asseco.replacer.model;

import java.util.Objects;

public class Replace {
	
	private String replaceWhat;
	
	private String replaceWith;
	
	private int line;

	public Replace(String replaceWhat, String replaceWith, int line) {
		this.replaceWhat = replaceWhat;
		this.replaceWith = replaceWith;
		this.line = line;
	}

	public Replace(String replaceWhat, String replaceWith) {
		this.replaceWhat = replaceWhat;
		this.replaceWith = replaceWith;
	}

	public String getReplaceWhat() {
		return replaceWhat;
	}

	public void setReplaceWhat(String replaceWhat) {
		this.replaceWhat = replaceWhat;
	}

	public String getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public int hashCode() {
		return Objects.hash(line, replaceWhat, replaceWith);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Replace other = (Replace) obj;
		return line == other.line && Objects.equals(replaceWhat, other.replaceWhat)
				&& Objects.equals(replaceWith, other.replaceWith);
	}
	
	
}
