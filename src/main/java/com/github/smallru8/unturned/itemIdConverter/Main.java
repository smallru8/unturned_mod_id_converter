package com.github.smallru8.unturned.itemIdConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {
	
	public static String WORK_DIR = "304930";
	public static String CONF = "cfg.properties";
	
	public static void main( String[] args ) throws FileNotFoundException, IOException
    {
		Properties p = new Properties();
		p.load(new FileInputStream(CONF));
		
		WikiAPI wiki = new WikiAPI();
		wiki.getTable(p.getProperty("map_name", "Buak (25298-27530)"));
		wiki.createMappingTable();
		Converter converter = new Converter(wiki);
		converter.find_file_and_edit(new File(WORK_DIR));
		
		System.out.println("Done! Press Enter key to close...");
        try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}  
    }
}
