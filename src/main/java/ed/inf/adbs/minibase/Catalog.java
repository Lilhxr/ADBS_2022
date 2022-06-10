package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Catalog {
	private static Catalog instance = null;
	public static Catalog getInstance(String dbDir){
		if (instance == null) {
			instance = new Catalog(dbDir);
		}
		return instance;
	}

	private static HashMap<String, String> nameMap = new HashMap<>();
	public HashMap<String, String> getNameMap(){
		return nameMap;
	}
	private static HashMap<String, ArrayList<String>> schemaMap = new HashMap<>();
	public HashMap<String, ArrayList<String>> getSchemaMap(){
		return schemaMap;
	}

	private Catalog(String path) {
		schemaMap = this.constructSchemas(path + File.separator + "schema.txt");
		nameMap = this.constructTables(path + File.separator + "files");
	}

	/**
	 * Construct Schema Map according to the specified file.
	 * @param filePath the file which store the schemas.
	 * @return Schema Map
	 */
	private HashMap<String, ArrayList<String>> constructSchemas(String filePath)
	{
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try {
			Scanner scanner = new Scanner(new File(filePath));
			while(scanner.hasNextLine()) {
				String schema = scanner.nextLine();
				String[] split = schema.split(" ");
				ArrayList<String> types = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
				map.put(split[0], types);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Construct Table Map according to the specified files.
	 * @param fileDirectory the directory of files which store the tables.
	 * @return Table Map
	 */
	private HashMap<String, String> constructTables(String fileDirectory)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		File d= new File(fileDirectory);
		String[] list = d.list();
		assert list != null;
		for (String s : list) {
			int f = s.indexOf(".csv");
			if (f >= 0) {
				map.put(s.substring(0, f), fileDirectory + File.separator + s);
			}
		}
		return map;
	}
}
