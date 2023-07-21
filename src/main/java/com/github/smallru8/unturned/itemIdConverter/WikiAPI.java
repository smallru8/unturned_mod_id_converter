package com.github.smallru8.unturned.itemIdConverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiAPI {
	
	public Document doc;
	public Element defaultTable;
	public Element elverTable;
	public Element table;
	
	public String mapName;
	
	public Map<String,String> item_ids_df_tg;
	
	private String manual_mapping_table_path = "map_pid_nid.txt";
	
	public WikiAPI() {
		item_ids_df_tg = new HashMap<String,String>();
		try {
			doc = Jsoup.connect("https://unturned.fandom.com/wiki/ID_List#Item").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getTable(String mapName) {
		this.mapName = mapName.split(" ")[0];
		
		Elements tables = doc.getElementsByClass("tables-m1");
		tables.forEach(e ->{
			Elements title = e.getElementsByClass("tables-m3");
			if(title.get(0).text().contains(mapName)) {
				table = e;
				System.out.println("Get target table: "+title.get(0).text());
			}else if(title.get(0).text().contains("Official (−5‒1521)")) {
				defaultTable = e;
				System.out.println("Get default table: "+title.get(0).text());
			}else if(title.get(0).text().contains("Elver (57000-57507)")) {
				elverTable = e;
				System.out.println("Get Elver table: "+title.get(0).text());
			}
		});
	}
	
	public void createMappingTable() {
		Map<String,String> df_item_ids = new HashMap<String,String>();
		
		Elements tg_rows = table.getElementsByTag("tbody").get(0).getElementsByTag("tr");
		Elements df_rows = defaultTable.getElementsByTag("tbody").get(0).getElementsByTag("tr");
		Elements elver_rows = elverTable.getElementsByTag("tbody").get(0).getElementsByTag("tr");
		
		System.out.println("Get Elver items");
		for(int i=0;i<elver_rows.size();i++) {//整理 default table
			Elements cols = elver_rows.get(i).getElementsByClass("tables-c1");
			
			if(cols.size()<2)
				continue;
			for(int j=0;j<cols.size();j+=2) {
				try {
					System.out.println("Item: "+cols.get(j).firstElementChild().text()+" | "+cols.get(j+1).text());
					df_item_ids.put(cols.get(j).firstElementChild().text(), cols.get(j+1).text());
				}catch(Exception e) {
					continue;
				}
			}
		}
		
		System.out.println("Get default items");
		for(int i=0;i<df_rows.size();i++) {//整理 default table
			Elements cols = df_rows.get(i).getElementsByClass("tables-c1");
			
			if(cols.size()<2)
				continue;
			for(int j=0;j<cols.size();j+=2) {
				try {
					System.out.println("Item: "+cols.get(j).firstElementChild().text()+" | "+cols.get(j+1).text());
					df_item_ids.put(cols.get(j).firstElementChild().text(), cols.get(j+1).text());
				}catch(Exception e) {
					continue;
				}
			}
		}
		
		FileWriter nf = null;
		try {
			nf =  new FileWriter("NotfoundId.txt");
			nf.write("# 無法找到以下 "+mapName+" item id 的原版映射，如果有會影響模組運行的物品請在 "+manual_mapping_table_path+" 手動添加映射\r\n");
			nf.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Get "+mapName+" items");
		for(int i=0;i<tg_rows.size();i++) {//統計需要 map 的 item
			Elements cols = tg_rows.get(i).getElementsByClass("tables-c1");
			if(cols.size()<2)
				continue;
			for(int j=0;j<cols.size();j+=2) {
				Elements col_small = cols.get(j).getElementsByTag("small");
				for(int k=0;k<col_small.size();k++) {
					if(col_small.get(k).text().contains(mapName)) {//需要 map
						try {
							String p_id = df_item_ids.get(cols.get(j).firstElementChild().text());
							
							if(p_id==null) {
								System.out.println("[MAP] default_id not found! Item: "+cols.get(j).firstElementChild().text()+" | "+cols.get(j+1).text());
								nf.write("[MAP] default_id not found! Item: "+cols.get(j).firstElementChild().text()+" | "+cols.get(j+1).text()+"\r\n");
								nf.flush();
								continue;
							}
							
							item_ids_df_tg.put(p_id, cols.get(j+1).text());//舊id:新id
							System.out.println("[MAP] default_id | "+mapName+"_id : "+p_id+" | "+cols.get(j+1).text());
						}catch(Exception e) {
							System.out.println("[MAP] default_id not found! Item: "+cols.get(j).firstElementChild().text()+" | "+cols.get(j+1).text());
						}
						break;
					}
				}
			}
		}
		
		try {
			nf.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Get manual items mapping table");
		try {
			FileReader fr = new FileReader(manual_mapping_table_path);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while((line = br.readLine())!=null) {
				if(line.startsWith("#"))
					continue;
				String[] fields = line.split(" ");
				if(fields.length!=2)
					continue;
				System.out.println("[MAP] default_id | "+mapName+"_id : "+fields[0]+" | "+fields[1]);
				item_ids_df_tg.put(fields[0],fields[1]);
			}
			br.close();
			fr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
