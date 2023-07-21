package com.github.smallru8.unturned.itemIdConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Converter {

	public String ignore = "3000549606";
	public WikiAPI wiki;
	
	public Converter(WikiAPI w) {
		wiki = w;
	}
	
	public void find_file_and_edit(File f) {
		if(f.isDirectory() && !f.getName().equals(ignore)) {//dir 繼續往下拆
			String[] files = f.list();
			for(String f_name : files) {
				find_file_and_edit(new File(f,f_name));
			}
		}else if(f.isFile() && f.getName().endsWith(".dat")){//file 開始處理
			System.out.println("[File] "+f.getAbsolutePath());
			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String data = "";
				String tmp = null;
				while((tmp = br.readLine())!=null) {
					String[] fields = tmp.split(" ",2);
					if(fields.length == 2 && 
						(
							fields[0].matches("Blueprint_[0-9]+_Supply_[0-9]+_ID") ||
							fields[0].matches("Blueprint_[0-9]+_Product") ||
							fields[0].matches("Blueprint_[0-9]+_Tool") ||
							fields[0].matches("Blueprint_[0-9]+_Output_[0-9]+_ID") ||
							fields[0].matches("Action_[0-9]+_Source")
						)
					){
						fields[1] = fields[1].replace(" ", "");
						if(wiki.item_ids_df_tg.containsKey(fields[1])) { //id 替換
							System.out.println("[Replace] previous id: "+fields[1]+", new id: "+wiki.item_ids_df_tg.get(fields[1]));
							tmp = fields[0] + " " + wiki.item_ids_df_tg.get(fields[1]);
						}
					}
					
					data += tmp+"\n";//等等存檔用
				}
				br.close();
				fr.close();
				
				//替換完 id 存檔
				FileWriter fw = new FileWriter(f);
				fw.write(data);
				fw.flush();
				fw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
}
