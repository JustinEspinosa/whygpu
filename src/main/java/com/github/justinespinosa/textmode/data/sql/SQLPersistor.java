package com.github.justinespinosa.textmode.data.sql;

import com.github.justinespinosa.textmode.data.PListPersistenceException;
import com.github.justinespinosa.textmode.data.PropertyList;
import com.github.justinespinosa.textmode.data.PropertyListPersistor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * If a property list contains a property that is also a property list, it must be saved with its own persistor 
 * @author justin
 *
 * @param <T>
 */
public class SQLPersistor<T extends PropertyList> extends PropertyListPersistor<T> {

	private Connection connection;
	
	public SQLPersistor(PropertyList params) throws SQLException, ClassNotFoundException{
		super(params);
		
		Class.forName(params().get("driver",String.class));
		
		String url = params().get("connectionString",String.class);
		String usr = params().get("user",String.class);
		String pwd = params().get("password",String.class);
		
		connection = DriverManager.getConnection(url,usr,pwd);

	}
	
	private ResultSet select(T list) throws SQLException{
		Statement query = connection.createStatement();
		query.execute(PListSQLAdapter.getSelect(list));
		return query.getResultSet();
	}
	
	private boolean insert(T list) throws SQLException{
		Statement query = connection.createStatement();
		return (query.executeUpdate(PListSQLAdapter.getInsert(list))==1);
	}

	private boolean update(T list) throws SQLException{
		Statement query = connection.createStatement();
		return (query.executeUpdate(PListSQLAdapter.getUpdate(list))==1);
	}


	@Override
	public void write(T list) throws PListPersistenceException {
		try {
			if(select(list).first())
				update(list);
			else
				insert(list);
			
		} catch (SQLException e) {
			throw new PListPersistenceException(e);
		}
		
	}

	@Override
	public T read(T list) throws PListPersistenceException {
		try {
			ResultSet results = select(list);
			
			if(results.first()){
				String[] values = list.persistantValues();
				
				for(String name : values){
					Object o = results.getObject(name);
					try {
						
						Object convertedObject = PListSQLAdapter.convertItem(list, name, o);
						if(convertedObject instanceof PropertyList){
							SQLPersistor<PropertyList> subPersistor = new SQLPersistor<PropertyList>(params());
							convertedObject = subPersistor.read((PropertyList)convertedObject);
						}
							
						list.set(name, convertedObject);
						
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				}
			}
			
			return list;
			
		} catch (SQLException e) {
			throw new PListPersistenceException(e);
		}
	}

}
