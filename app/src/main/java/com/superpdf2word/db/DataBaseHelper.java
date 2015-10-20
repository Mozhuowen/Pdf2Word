package com.superpdf2word.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.superpdf2word.db.beans.PWFile;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper
{
	private static final String TABLE_NAME = "pwfile.db";  
 
    private Dao<PWFile, Integer> pwfileDao;
	
	private DataBaseHelper(Context context)  
    {  
        super(context, TABLE_NAME, null, 1);  
    }

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		 try  
	        {  
	            TableUtils.createTable(connectionSource, PWFile.class);  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		try  
        {  
            TableUtils.dropTable(connectionSource, PWFile.class, true);  
            onCreate(arg0	, connectionSource);  
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }
	}
	
	private static DataBaseHelper instance;
	
    public static synchronized DataBaseHelper getHelper(Context context)  
    {  
        if (instance == null)  
        {  
            synchronized (DataBaseHelper.class)  
            {  
                if (instance == null)  
                    instance = new DataBaseHelper(context);  
            }  
        }  
  
        return instance;  
    }
    
    /** 
     * ���userDao 
     *  
     * @return 
     * @throws SQLException 
     */  
    public Dao<PWFile, Integer> getPWFileDao() throws SQLException  
    {  
        if (pwfileDao == null)  
        {  
        	pwfileDao = getDao(PWFile.class);  
        }  
        return pwfileDao;  
    }
    
    /** 
     * �ͷ���Դ 
     */  
    @Override  
    public void close()  
    {  
        super.close();  
        pwfileDao = null;  
    } 
}