package btree.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import btree.BTree;
import btree.DataSerializer;
import btree.PageSerializer;
import btree.types.StringType;

public class Main {
	public static void main(String[] args) throws Exception {
		
		StringType registersDT = new StringType(6);
		StringType namesDT = new StringType(36);

		File registers = new File("C:/EDA/registers.dat");
		PageSerializer<String> ps = new PageSerializer<>(registers, registersDT, 8);

		File names = new File("C:/EDA/names.dat");
		DataSerializer<String> ds = new DataSerializer<>(names, namesDT);

		BTree<String, String> st = new BTree<String, String>(ps, ds);

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(
					"C:\\EDA\\alunosEDA.csv"));
			while (br.ready()) {
				String line = br.readLine();
				String strings[] = line.split(";");
					st.put(strings[0], strings[1]);
			}

			br.close();
			
		} catch (FileNotFoundException ex) {
			System.out.println(ex);
		} catch (IOException e) {
			System.out.println(e);
		}

		//Buscar todos as matriculas
		for (String key : st.keys())
			System.out.println(key);
		
		//Buscar o aluno com a matricula 383810
		System.out.println(st.get("383810"));
		
		//Verifica se existe aluno com a matricula 999999
		System.out.println(st.contains("999999")?"Sim":"Não");
		
	}
}
