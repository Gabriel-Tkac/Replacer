package com.asseco.replacer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileOccurence {
	
	private String fileName;
	
	private File file;
	
	private List<Replace> replaces;

	public FileOccurence(File file) {
		this.file = file;
		this.fileName = file.getName();
		replaces = new ArrayList<Replace>();
	}

	public String getFileName() {
		return fileName;
	}

	public File getFile() {
		return file;
	}
	
	public void addReplace(Replace replace) {
		replaces.add(replace);
	}
	
	public List<Replace> getReplaces() {
		return replaces;
	}

	public String toString() {
		String occurences = "Zhoda na riadkoch: ";
		for (Replace replace: replaces)
			occurences += String.valueOf(replace.getLine()) + ", ";
		return occurences;
	}

}
