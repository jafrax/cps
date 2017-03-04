package com.controllers;

import bsh.Interpreter;

import java.io.ByteArrayOutputStream; 

//import com.pojo.ClientPOJO;
//import com.pojo.ProductPOJO;
//import com.pojo.ReportPOJO;
//import com.report.TestComposer;
//import com.sun.xml.internal.ws.api.server.Container;
//import com.tools.Libs;




import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import com.pojo.ClientPOJO;
import com.tools.Libs;

import java.awt.BorderLayout;
import java.io.*;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.swing.JFrame;


public class PrintSuplierController extends Window {

    private Logger log = LoggerFactory.getLogger(PrintSuplierController.class);
    private Listbox lbClients;
    private Paging pgClients;
    private Bandbox bbClient;
    private String whereClients;
    private Combobox cbReportType;
//    private Combobox cbSort;
    private Iframe iframe;

    public void onCreate() {
        initComponents();
        populateClients(0, pgClients.getPageSize());
    }

    private void initComponents() {
    	lbClients = (Listbox) getFellow("lbClients");
    	pgClients = (Paging) getFellow("pgClients");
    	bbClient = (Bandbox) getFellow("bbClient");
//    	cbSort = (Combobox) getFellow("cbSort");
        cbReportType = (Combobox) getFellow("cbReportType");
        
        bbClient.setText("");
//        cbSort.appendItem("kode");
//        cbSort.appendItem("perusahaan");
//        cbSort.setSelectedIndex(0);
    }    
    
    public void print() {
        Map params = new HashMap();
        params.put("idsuplier", bbClient.getValue());
        String laporan = "";
        
        if (bbClient.getText() == ""){
        	laporan = "ReportSuplierAll";
		}else {
			laporan = "ReportSuplierById";
		}
        
        printReport(cbReportType.getValue() ,laporan, params);
    }

    
    private void printReport(String tipe, String reportName, Map parameters) {
    	try {
    		System.out.println(reportName);
            InputStream input = new FileInputStream(new File(Executions.getCurrent().getSession().getWebApp().getRealPath("/resources/reports/" + reportName + ".jrxml")));
            JasperDesign jasperDesign = JRXmlLoader.load(input);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource()); //jika tanpa data/ koneksi
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.104", "sa", "NewLife");
//            String url = "jdbc:jtds:sqlserver://192.168.1.101;user=sa;password=NewLife";
            String url = Libs.JasperReportConnection();
            Connection conn = DriverManager.getConnection(url);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
           
            if ( tipe.equals("Print") ){
            	 JasperPrintManager.printReport(jasperPrint,false);
            }else if (tipe.equals("View") ){
            	 JasperViewer.viewReport (jasperPrint,false);
            }else if (tipe.equals("XML")){
            	 OutputStream output = new FileOutputStream(new File(Libs.config.get("temp_dir") + File.separator + reportName + ".xml"));
                 JasperExportManager.exportReportToXmlStream(jasperPrint, output);
                 output.close();

                 File f = new File(Libs.config.get("temp_dir") + File.separator + reportName + ".xml");
                 InputStream is = new FileInputStream(f);
                 Filedownload.save(is, "application/xml", reportName + ".xml");            
                 f.delete();
            }else{
            	
            	OutputStream output = new FileOutputStream(new File(Libs.config.get("temp_dir") + File.separator + reportName + ".pdf"));
                JasperExportManager.exportReportToPdfStream(jasperPrint, output);
                output.close();
                File f = new File(Libs.config.get("temp_dir") + File.separator + reportName + ".pdf");
           		Clients.evalJavaScript("window.open('temp/"+reportName+".pdf','','top=100,left=200,height=600,width=800,scrollbars=1,resizable=1')");

//                InputStream is = new FileInputStream(f);
//                org.zkoss.zhtml.Filedownload.save(is, "application/pdf", reportName + ".pdf");  
//                f.delete();

            }         
           
        } catch(Exception ex) {
        	ex.printStackTrace();
            log.error("printReport", ex);
        }
    }
    
     
    private void printservice(){
    
		 PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		
		 // Get the printer name
		 for(int i=0;i<services.length;i++) 
		 {
		  String name = services[0].getName();
		  System.out.println("printer="+name);
		 }
    }

	private void populateClients(int offset, int limit) {
        lbClients.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
        	String q0 = "select count(*) ";
            String q1 = "select "
                    + "kode, nama ,perusahaan ";
            String q2 = "from "+Libs.getDbName()+".dbo.suplier ";
            String q3 = "";
            String q4 = "order by kode asc ";

            if (whereClients!=null) q3 = "where " + whereClients;

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pgClients.setTotalSize(rc);

            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                ClientPOJO e = new ClientPOJO();
                e.setClientId(Libs.nn(o[0]).trim());
                e.setClientName(Libs.nn(o[1]).trim());

                Listitem li = new Listitem();
                li.setValue(e);
                li.appendChild(new Listcell(e.getClientId()));
                li.appendChild(new Listcell(e.getClientName()));
                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
                lbClients.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateClients", ex);
        } finally {
            s.close();
        }
    }

    public void clientSelected() {
        if (lbClients.getSelectedCount()==1) {
            ClientPOJO clientPOJO = lbClients.getSelectedItem().getValue();
            bbClient.setText(clientPOJO.getClientId());
            bbClient.setAttribute("e", clientPOJO);
            bbClient.close();
        }
    }


    public void quickSearchClients() {
        Textbox tQuickSearchClients = (Textbox) getFellow("tQuickSearchClients");

        if (!tQuickSearchClients.getText().isEmpty()) {
            whereClients = "perusahaan like '%" + tQuickSearchClients.getText() + "%' ";
            populateClients(0, pgClients.getPageSize());
            pgClients.setActivePage(0);
        } else {
            refreshClients();
        }
    }

    public void refreshClients() {
        whereClients = null;
        populateClients(0, pgClients.getPageSize());
    }

    
    public void refresh(){
    	initComponents();
        populateClients(0, pgClients.getPageSize());
    }


}
