package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class PackageManager {

	private static PackageManager instance = null;
	private ArrayList<String> pkgNames = null;

	protected PackageManager() {
		pkgNames = new ArrayList<String>();
		loadPackageNames();
	}

	public static PackageManager getInstance() {
		if (instance == null) {
			instance = new PackageManager();
		}
		return instance;
	}

	protected void loadPackageNames() {
		String fichero = "resources/pkgNames.txt";
		try {
			FileReader fr = new FileReader(fichero);
			BufferedReader br = new BufferedReader(fr);
			String linea;
			while ((linea = br.readLine()) != null) {
				pkgNames.add(linea);

			}
			fr.close();
		} catch (Exception e) {
			System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
		}
	}

	public boolean isPkgName(String name) {
		return pkgNames.contains(name);
	}

	public ArrayList<String> getPkgNames() {
		return pkgNames;
	}

}