package eu.xenit.move2alf.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.alfresco.repo.search.impl.parsers.CMISParser.booleanLiteral_return;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

public class ConditionalDataSourceInitializer extends DataSourceInitializer {
	
	private boolean enabled = false;
	
	public boolean isEnabled(){
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
		super.setEnabled(enabled);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(this.isEnabled() && this.databaseEmpty()){
			super.afterPropertiesSet();
		}
	}
	
	private DataSource dataSource;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		super.setDataSource(dataSource);
	}

	private boolean databaseEmpty() {
		DataSource dataSource = this.dataSource;
		try {
			Connection connection = dataSource.getConnection();
			try{
				String sql = "SHOW tables";
				PreparedStatement stm = connection.prepareStatement(sql);
				ResultSet resultSet = stm.executeQuery();
				if(resultSet.next()){
					return false;
				}
				else{
					return true;
				}
				
			}
			finally{
				try {
					connection.close();
				}
				catch (SQLException e) {
					// TODO: handle exception
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}
