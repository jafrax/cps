package com.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;
import org.zkoss.zul.Messagebox;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;



// lib cron java
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class WebAppStart implements WebAppInit {

    private Logger log = LoggerFactory.getLogger(WebAppStart.class);
	
    @Override
    public void init(WebApp wa) throws Exception {
        loadConfig(wa);
        connectDatabaseDB();
//        connectMysqlDB();
//        connectPostresqlDB();
     }


	private void loadConfig(WebApp wa) {
        Libs.config = new Properties();
        try {
            File f = new File(wa.getRealPath("/conf/config.properties"));
            Libs.config.load(new FileInputStream(f));
        } catch (Exception ex) {
            log.error("loadConfig", ex);
        }
    }

    private void connectDatabaseDB() {
        Configuration configuration = new Configuration();
        System.out.println("SQL Server");
        
        Properties prop = new Properties();
        prop.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        prop.put("hibernate.connection.driver_class", "net.sourceforge.jtds.jdbc.Driver");
        prop.put("hibernate.connection.url", String.valueOf(Libs.config.get("db_url")));
        prop.put("hibernate.connection.username", String.valueOf(Libs.config.get("db_username")));
        prop.put("hibernate.connection.password", String.valueOf(Libs.config.get("db_password")));
        prop.put("transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        prop.put("current_session_context_class", "thread");
        prop.put("hibernate.show_sql", "false");

        if (Libs.config.get("c3p0").equals("true")) {
            prop.put("hibernate.c3p0.acquire_increment", "2");
            prop.put("hibernate.c3p0.idle_test_period", "3000");
            prop.put("hibernate.c3p0.timeout", "1800");
            prop.put("hibernate.c3p0.max_size", "25");
            prop.put("hibernate.c3p0.min_size", "3");
            prop.put("hibernate.c3p0.max_statements", "0");
            prop.put("hibernate.c3p0.preferredTestQuery", "select 1;");
            prop.put("hibernate.c3p0.validate", "true");
        }
        configuration.setProperties(prop);

        
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        Libs.sfDB = configuration.buildSessionFactory(serviceRegistry);
    }

    

    
    private void connectMysqlDB() {
    	 Configuration configuration = new Configuration();
    	  System.out.println("JDBC MySQL");
    	  
    	  Properties prop = new Properties();
          prop.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
          prop.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
          prop.put("hibernate.connection.url", String.valueOf(Libs.config.get("MysqlDb_url")));
          prop.put("hibernate.connection.username", String.valueOf(Libs.config.get("MysqlDb_username")));
          prop.put("hibernate.connection.password", String.valueOf(Libs.config.get("MysqlDb_password")));
          prop.put("transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
          prop.put("hibernate.current_session_context_class", "thread");
          prop.put("hibernate.cache.provider_class","org.hibernate.cache.NoCacheProvider");
          prop.put("hibernate.show_sql", "false");
          prop.put("hibernate.hbm2ddl.auto", "validate");
          prop.put("hibernate.format_sql","true");
          prop.put("hibernate.use_sql_comments","true"); 
          prop.put("hibernate.connection.release_mode","auto");
          prop.put("hibernate.current_session_context_class","jta");
          prop.put("hibernate.transaction.manager_lookup_class","org.hibernate.transaction.WeblogicTransactionManagerLookup");
          
          

          if (Libs.config.get("c3p0").equals("true")) {
              prop.put("hibernate.c3p0.acquire_increment", "2");
              prop.put("hibernate.c3p0.idle_test_period", "3000");
              prop.put("hibernate.c3p0.timeout", "18000");
              prop.put("hibernate.c3p0.max_size", "25");
              prop.put("hibernate.c3p0.min_size", "3");
              prop.put("hibernate.c3p0.max_statements", "0");
              prop.put("hibernate.c3p0.preferredTestQuery", "select 1;");
              prop.put("hibernate.c3p0.validate", "true");
          }
          configuration.setProperties(prop);
          
    	  ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
          Libs.sfMysqlDB = configuration.buildSessionFactory(serviceRegistry); 
    }

    
    
    private void connectPostresqlDB() {
   	 Configuration configuration = new Configuration();
   	  System.out.println("JDBC Postgresql");
   	  
   	  Properties prop = new Properties();
         prop.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
         prop.put("hibernate.connection.driver_class", "org.postgresql.Driver");
         prop.put("hibernate.connection.url", String.valueOf(Libs.config.get("postgredb_url")));
         prop.put("hibernate.connection.username", String.valueOf(Libs.config.get("postgredb_username")));
         prop.put("hibernate.connection.password", String.valueOf(Libs.config.get("postgredb_password")));
         prop.put("transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
         prop.put("hibernate.current_session_context_class", "thread");
         prop.put("hibernate.cache.provider_class","org.hibernate.cache.NoCacheProvider");
         prop.put("hibernate.show_sql", "false");
         prop.put("hibernate.hbm2ddl.auto", "validate");
         prop.put("hibernate.format_sql","true");
         prop.put("hibernate.use_sql_comments","true"); 
         prop.put("hibernate.connection.release_mode","auto");
         prop.put("hibernate.current_session_context_class","jta");
         prop.put("hibernate.transaction.manager_lookup_class","org.hibernate.transaction.WeblogicTransactionManagerLookup");
         
         

         if (Libs.config.get("c3p0").equals("true")) {
             prop.put("hibernate.c3p0.acquire_increment", "2");
             prop.put("hibernate.c3p0.idle_test_period", "3000");
             prop.put("hibernate.c3p0.timeout", "18000");
             prop.put("hibernate.c3p0.max_size", "25");
             prop.put("hibernate.c3p0.min_size", "3");
             prop.put("hibernate.c3p0.max_statements", "0");
             prop.put("hibernate.c3p0.preferredTestQuery", "select 1;");
             prop.put("hibernate.c3p0.validate", "true");
         }
         configuration.setProperties(prop);
         
   	  ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
         Libs.sfpostgresqllDB = configuration.buildSessionFactory(serviceRegistry); 
   }
 
         
    
    
}
