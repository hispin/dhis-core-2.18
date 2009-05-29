package org.hisp.dhis.vn.chr.jdbc;

/**
 * @author Chau Thu Tran
 * 
 */

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.jdbc.JDBCConfigurationProvider;
import org.hisp.dhis.jdbc.StatementHolder;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.jdbc.util.AccessMetaDataService;
import org.hisp.dhis.vn.chr.statement.AddDataStatement;
import org.hisp.dhis.vn.chr.statement.AlterColumnStatement;
import org.hisp.dhis.vn.chr.statement.CreateTableStatement;
import org.hisp.dhis.vn.chr.statement.DeleteDataStatement;
import org.hisp.dhis.vn.chr.statement.FormStatement;
import org.hisp.dhis.vn.chr.statement.GetDataStatement;
import org.hisp.dhis.vn.chr.statement.ListDataStatement;
import org.hisp.dhis.vn.chr.statement.ListRelativeDataStatement;
import org.hisp.dhis.vn.chr.statement.SearchDataStatement;
import org.hisp.dhis.vn.chr.statement.UpdateDataStatement;

public class JDBCFormManager implements FormManager{
	
	private static final Log log = LogFactory.getLog( JDBCFormManager.class );
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    private JDBCConfigurationProvider configurationProvider;

    private AccessMetaDataService accessMetaDataService;
    
    private ElementService elementService;
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    public void setConfigurationProvider( JDBCConfigurationProvider configurationProvider )
    {
        this.configurationProvider = configurationProvider;
    }

    public void setAccessMetaDataService(AccessMetaDataService accessMetaDataService) {
		this.accessMetaDataService = accessMetaDataService;
	}

	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

    // -------------------------------------------------------------------------
    // Implements
    // -------------------------------------------------------------------------

	/**
     * Create table
     * 
     * @param form needs to create table
     * 
     */
    public void createTable( Form form )
    {
    	StatementHolder holder = statementManager.getHolder();
    	
    	String allStatement = "";
    	
    	try {
    		
			// Check table is exist
			if (accessMetaDataService.existTable(form.getName().toLowerCase())) { // exist table

				// Initial statement
				FormStatement statement = null;

				// Get columns into the table
				Set<String> columns = accessMetaDataService
						.getAllColumnsOfTable(form.getName());
				
				columns.remove("id");
			
				// Add or Alter columns into table
				for (Element element : form.getElements()) {
					// add column in to alter list
					if (columns.contains(element.getName())) {
									
						statement = new AlterColumnStatement(form,
								configurationProvider.getConfiguration()
										.getDialect(),
								AlterColumnStatement.ALTER_STATUS, element);
						
						log.debug("Alter column with SQL statement: '"
								+ statement.getStatement() + "'");		
					}

					// if column is not exist in table, add column
					// add column in to add list
					else // if(!columns.contains(element.getName()))
					{
						statement = new AlterColumnStatement(form,
								configurationProvider.getConfiguration()
										.getDialect(),
								AlterColumnStatement.ADD_STATUS, element);
						
						log.debug("Add column with SQL statement: '"
								+ statement.getStatement() + "'");
					}
					allStatement += statement.getStatement();

				}// end column

				// if column is exist in table (elements exist in table, not in form),
				// but not exist in form.elements,
				// delete column in table
				// add column delete list				
				for (String column : columns) {
					
						Element element = elementService.getElement(column);

						if (element == null){

							statement = new AlterColumnStatement(form,
									configurationProvider.getConfiguration()
											.getDialect(),
									AlterColumnStatement.DROP_STATUS, column);

						allStatement += statement.getStatement();
						
						log.debug("Drop column with SQL statement: '"
								+ statement.getStatement() + "'");
					}
				}
				

			}// end alter columns
			
			else { // Table is not exist or Table is not data
				
				// create table
				FormStatement statement = new CreateTableStatement(form,
						configurationProvider.getConfiguration().getDialect());

				allStatement = statement.getStatement();

				log.debug("Creating form table with SQL statement: '"
						+ statement.getStatement() + "'");
			}// end create table

			// execute command Statement

			holder.getStatement().executeUpdate(allStatement);
			
		} catch (Exception ex) {
			throw new RuntimeException("Failed to create table: "
					+ form.getName(), ex);
		} finally {
			holder.close();
		}
	}
   
    /**
     * Load list object
     * 
     * @param form needs to create the table
     * @param pageIndex Index of page
     * @return List Objects
     */
   public ArrayList listObject(Form form, int pageIndex){
	   
	   ArrayList data = new ArrayList();
	   
	   StatementHolder holder = statementManager.getHolder();
	   
	   FormStatement statement = new ListDataStatement( form , 
			   configurationProvider.getConfiguration()
				.getDialect(), pageIndex);

	   log.debug( "Selecting data form table with SQL statement: '" + statement.getStatement() + "'" );
	      
	   try
       {
		   // min
		   holder.getStatement().setFetchSize((pageIndex -1 ) * form.getNoRow() + 1);
		   // max
		   holder.getStatement().setMaxRows(pageIndex * form.getNoRow());
		   
		   ResultSet resultSet = holder.getStatement().executeQuery( statement.getStatement() );

//		   resultSet.setFetchSize(form.getNoRow());
		   
		   resultSet.setFetchSize(1);
		  
		   int noColumn = form.getNoColumn();
		   
		   int rowIndex = 0;
		   
		   while (resultSet.next()) {
			   ArrayList<String> rowData = new ArrayList<String>();
			   
			   for(int i=1;i < noColumn + 2;i++){
				   rowData.add(resultSet.getString(i));
			   }
			   data.add(rowData);
			   
	           rowIndex++;
	        }

       }
       catch ( Exception ex )
       {
           throw new RuntimeException( "Failed to query data : " + form.getName(), ex );
       }
       finally
       {
           holder.close();
       }
       
       return data; 
   }

   /**
    * Add Object by ID
    * 
    * @param form needs to create the table
    * @param data Data of Object
    */
	public void addObject(Form form, String[] data) {
		
		StatementHolder holder = statementManager.getHolder();

		try {
			
			ArrayList<String> arrData = new ArrayList<String>();
			
			for(int i=0; i<data.length;i++){
				arrData.add(data[i]);
			}
			
			FormStatement statement = new AddDataStatement(form,
					configurationProvider.getConfiguration().getDialect(), arrData);

			log.debug("Update data form table with SQL statement: '"
					+ statement.getStatement() + "'");

			holder.getStatement().executeUpdate(statement.getStatement());

		} catch (Exception ex) {
			throw new RuntimeException("Failed to query data : "
					+ form.getName(), ex);
		} finally {
			holder.close();
		}
	}
	
   /**
    * Update Object by ID
    * 
    * @param form needs to create the table
    * @param data Data of Object
    */
	public void updateObject(Form form, ArrayList<String> data){
		StatementHolder holder = statementManager.getHolder();

		try {
			FormStatement statement = new UpdateDataStatement(form,
					configurationProvider.getConfiguration().getDialect(), data);

			log.debug("Update data form table with SQL statement: '"
					+ statement.getStatement() + "'");

			holder.getStatement().executeUpdate(statement.getStatement());
			
		} catch (Exception ex) {
			throw new RuntimeException("Failed to query data : "
					+ form.getName(), ex);
		} finally {
			holder.close();
		}
	}
	
   /**
    * Delete Object by ID
    * 
    * @param form needs to create the table
    * @param id Id of object
    */
   public void deleteObject(Form form, int id){
	   
	   StatementHolder holder = statementManager.getHolder();
	   
	   try
       {
		   FormStatement statement = new DeleteDataStatement( form , 
				   configurationProvider.getConfiguration()
					.getDialect(), id);

		   log.debug( "Delete data form table with SQL statement: '" + statement.getStatement() + "'" );
		   
		   holder.getStatement().executeUpdate( statement.getStatement() );
       }
       catch ( Exception ex )
       {
           throw new RuntimeException( "Failed to query data : " + form.getName(), ex );
       }
       finally
       {
           holder.close();
       }
   }
   
   /**
    * Get data in a Object by id of Object
    * 
    * @param form needs to create the table
    * @param id Id of object
    * @return Values of a Object
    */
   public ArrayList getObject(Form form, int id) {

		ArrayList data = new ArrayList();

		StatementHolder holder = statementManager.getHolder();

		try {
			FormStatement statement = new GetDataStatement(form,
					configurationProvider.getConfiguration().getDialect(), id);

			log.debug("Get data form table with SQL statement: '"
					+ statement.getStatement() + "'");

			ResultSet resultSet = holder.getStatement().executeQuery(
					statement.getStatement());

			while (resultSet.next()) {

				ArrayList<String> rowData = new ArrayList<String>();

				for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++) {
					data.add(resultSet.getString(i));
				}

			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to query data : "
					+ form.getName(), ex);
		} finally {
			holder.close();
		}

		return data;
	}

   /**
    * Search Object by keyword
    * 
    * @param form needs to create the table
    * @param keyword Keyword
    */
	public ArrayList searchObject(Form form, String keyword){
		
		
		ArrayList data = new ArrayList();

		StatementHolder holder = statementManager.getHolder();

		try {
			FormStatement statement = new SearchDataStatement(form,
					configurationProvider.getConfiguration().getDialect(), keyword);

			log.debug("Get data form table with SQL statement: '"
					+ statement.getStatement() + "'");

			ResultSet resultSet = holder.getStatement().executeQuery(
					statement.getStatement());
			   while (resultSet.next()) {
				   
				   ArrayList<String> rowData = new ArrayList<String>();
				   
				   for(int i=1;i < form.getNoColumn() + 2;i++){
					   rowData.add(resultSet.getString(i));
				   }
				   data.add(rowData);
				   
		        }
		} catch (Exception ex) {
			throw new RuntimeException("Failed to query data : "
					+ form.getName(), ex);
		} finally {
			holder.close();
		}

		return data;
	}
	
	
	public ArrayList ListRelativeObject(Form form, String column ,String objectId){
			
		   ArrayList data = new ArrayList();
		   
		   StatementHolder holder = statementManager.getHolder();
		   
		   FormStatement statement = new ListRelativeDataStatement( form , 
				   configurationProvider.getConfiguration()
					.getDialect(), objectId, column );

		   log.debug( "Selecting data form relative table with SQL statement: '" + statement.getStatement() + "'" );
		      
		   try
	       {
			   ResultSet resultSet = holder.getStatement().executeQuery( statement.getStatement() );

//			   resultSet.setFetchSize(form.getNoRow());
			   
			   resultSet.setFetchSize(1);
			  
			   int noColumn = form.getNoColumn();
			   
			   int rowIndex = 0;
			   
			   while (resultSet.next()) {
				   ArrayList<String> rowData = new ArrayList<String>();
				   
				   for(int i=1;i < noColumn + 2;i++){
					   rowData.add(resultSet.getString(i));
				   }
				   data.add(rowData);
				   
		           rowIndex++;
		        }

	       }
	       catch ( Exception ex )
	       {
	           throw new RuntimeException( "Failed to query data : " + form.getName(), ex );
	       }
	       finally
	       {
	           holder.close();
	       }
	       
	       return data;
	}

}
