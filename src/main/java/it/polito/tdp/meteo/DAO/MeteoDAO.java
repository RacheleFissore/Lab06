package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.*;

import it.polito.tdp.meteo.DAO.ConnectDB;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public List<String> getLocalita() {
		final String sql = "SELECT DISTINCT Localita FROM situazione";
		List<String> localita = new ArrayList<String>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				String l = rs.getString("Localita");
				localita.add(l);
			}

			conn.close();
			return localita;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		final String sql = "SELECT Localita, Data, Umidita "
				+ "FROM situazione s "
				+ "WHERE Localita=? "
				+ "AND MONTH(DATA)=? "
				+ "ORDER BY data ASC";
		
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setInt(2, mese);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Rilevamento getRilevamentoLocalitaGiorno(int giorno, int mese, String localita) {
		final String sql = "SELECT Localita, Data, Umidita "
				+ "FROM situazione s "
				+ "WHERE Localita=? "
				+ "AND MONTH(Data)=? "
				+ "AND DAY(Data)=? "
				+ "ORDER BY data ASC";

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setInt(2, mese);
			st.setInt(3, giorno);
			ResultSet rs = st.executeQuery();

			Rilevamento r = null;
			while (rs.next()) {
				r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
			}

			conn.close();
			return r;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getUmiditaMedia(int mese) {
		final String sql = "SELECT localita, AVG(umidita) AS media "
				+ "FROM situazione "
				+ "WHERE MONTH(DATA) = ? "
				+ "GROUP BY(localita)";

		String ris="";
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				ris += rs.getString("localita") + " - " + rs.getDouble("media") + "\n";
			}

			conn.close();
			return ris;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
} 
