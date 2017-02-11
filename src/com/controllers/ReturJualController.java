package com.controllers;

import com.pojo.ClientPOJO;
import com.pojo.ICDPOJO;
import com.pojo.ProductPOJO;
import com.tools.Libs;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.*;


@SuppressWarnings("serial")
public class ReturJualController extends Window {

    private Logger log = LoggerFactory.getLogger(ReturJualController.class);
    private Bandbox bbClient;
    private Bandbox bbProduct;
    private Listbox lb;
    private Listbox lbClients;
    private Listbox lbProducts;
    private Textbox tPolicyNumber;
    private Textbox tPolicyYear;
    private Paging pg;
    private Paging pgClients;
    private Paging pgProducts;
    private String whereClients;
    private String whereProducts;
    private Combobox kategori;
    private Combobox cbSales;
    private Datebox tglretur;
    private Datebox tempo;
    private String sales;
    
    private List<ICDPOJO> tempProductICDs = new ArrayList<ICDPOJO>();
    private List<ICDPOJO> productICDs = new ArrayList<ICDPOJO>();

    public void onCreate() {
        initComponents();
       
        
        populateClients(0, pgClients.getPageSize());
    }

    private void initComponents() {
    	cbSales = (Combobox) getFellow("cbSales");
        kategori = (Combobox) getFellow("kategori");
    	lb = (Listbox) getFellow("lb");
        lbClients = (Listbox) getFellow("lbClients");
        lbProducts = (Listbox) getFellow("lbProducts");
        pg = (Paging) getFellow("pg");
        pgClients = (Paging) getFellow("pgClients");
        pgProducts = (Paging) getFellow("pgProducts");
        tPolicyNumber = (Textbox) getFellow("tPolicyNumber");
        tPolicyYear = (Textbox) getFellow("tPolicyYear");
        bbClient = (Bandbox) getFellow("bbClient");
        bbProduct = (Bandbox) getFellow("bbProduct");
        
        Libs.getKategori(kategori);
        sales = (String)Executions.getCurrent().getSession().getAttribute("sales");
        System.out.println(sales);
        if (sales.equals("0")) {
        Libs.getSales(cbSales);
        }else{
        	cbSales.appendItem(Libs.findSales(sales));
        	cbSales.setSelectedIndex(0);
        }

        pgClients.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populateClients(evt.getActivePage()*pgClients.getPageSize(), pgClients.getPageSize());
            }
        });
        
        pgProducts.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populateProducts(evt.getActivePage()*pgProducts.getPageSize(), pgProducts.getPageSize());
            }
        });

        pg.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populate(evt.getActivePage() * pg.getPageSize(), pg.getPageSize());
            }
        });
    }

 
    private void KategoriSelected(){
    	
    }
    
    private void SalesSelected(){
    	
    }

    private void populateClients(int offset, int limit) {
        lbClients.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select "
                    + "kode, nama ,perusahaan ";
            String q2 = "from "+Libs.getDbName()+".dbo.customer ";
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

    private void populateProducts(int offset, int limit) {
    	//Messagebox.show("populate produk");
        ClientPOJO clientPOJO = (ClientPOJO) bbClient.getAttribute("e");
        lbProducts.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
        	String q0 = "select count(*) ";
            String q1 = "select kode,nama,merk ";
            String q2 = "from "+Libs.getDbName()+".dbo.barang ";
            String q3 = "";
            String q4 = "order by kode  asc ";

            //if (clientPOJO!=null) {
            //    q3 = "where hhdrinsid='" + clientPOJO.getClientId() + "' ";
            //}

            if (whereProducts!=null) {
                if (!q3.isEmpty()) q3 += "and (" + whereProducts + ") ";
                else q3 += "where (" + whereProducts + ") ";
            }

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pgProducts.setTotalSize(rc);
            
            //Messagebox.show("query = "+q1 + q2 + q3 + q4);
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                ProductPOJO e = new ProductPOJO();
                e.setClientPOJO(clientPOJO);
                e.setProductId(Libs.nn(o[0]));
                e.setProductMerk(Libs.nn(o[1]));
                e.setProductName(Libs.nn(o[2]));
               

                Listitem li = new Listitem();
                li.setValue(e);

                li.appendChild(new Listcell(e.getProductMerk()));
                li.appendChild(new Listcell(e.getProductId()));
                li.appendChild(new Listcell(e.getProductName()));

                lbProducts.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateProducts", ex);
        } finally {
            s.close();
        }
    }

    public void clientSelected() {
        if (lbClients.getSelectedCount()==1) {
            ClientPOJO clientPOJO = lbClients.getSelectedItem().getValue();
            bbClient.setText(clientPOJO.getClientId());
            bbClient.setAttribute("e", clientPOJO);
            populateProducts(0, pgProducts.getPageSize());
            bbClient.close();
        }
    }

    public void productSelected() {
        if (lbProducts.getSelectedCount()>0) {
            ProductPOJO productPOJO = lbProducts.getSelectedItem().getValue();
            bbProduct.setText(productPOJO.getProductId());
            bbProduct.setAttribute("e", productPOJO);

            tPolicyNumber.setText(productPOJO.getProductName());
            tPolicyYear.setText(productPOJO.getProductMerk());
            populateProductICDs();
            populate(0, pg.getPageSize());

            bbProduct.close();
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

    public void quickSearchProducts() {
        Textbox tQuickSearchProducts = (Textbox) getFellow("tQuickSearchProducts");

        if (!tQuickSearchProducts.getText().isEmpty()) {
            whereProducts = "nama like '%" + tQuickSearchProducts.getText() + "%' or "
                    + "kode like '%" + tQuickSearchProducts.getText() + "%' ";
            populateProducts(0, pgProducts.getPageSize());
            //populateProducts();
        } else {
            refreshProducts();
        }
    }

    public void refreshProducts() {
        whereProducts = null;
        populateProducts(0, pgProducts.getPageSize());
        //populateProducts();
    }

    private void populate(int offset, int limit) {
        lb.getItems().clear();
        int max = offset + limit;
        if (max>productICDs.size()) max = productICDs.size();

        for (int i=offset; i<max; i++) {
            ICDPOJO icdPOJO = productICDs.get(i);

            Listitem li = new Listitem();
            li.setValue(icdPOJO);

            li.appendChild(new Listcell(icdPOJO.getIcdCode()));
            li.appendChild(new Listcell(icdPOJO.getDescription()));

            lb.appendChild(li);
        }
    }

    private void populateProductICDs() {
        productICDs.clear();

        Session s = Libs.sfDB.openSession();
        try {
            String qry = "select endorsmentid, endorsmenttype from EDC_PRJ.dbo.edc_endorsmentbypolis "
                    + "where nopolis='" + tPolicyNumber.getText() + "' and thnpolis='" + tPolicyYear.getText() + "';";

            List<Object[]> l = s.createSQLQuery(qry).list();
            for (Object[] o : l) {
                Listitem li = new Listitem();
                ICDPOJO c = Libs.getICDById(Libs.nn(o[0]));
                productICDs.add(c);
            }
            pg.setTotalSize(l.size());
        } catch (Exception ex) {
            log.error("populateProductICDs", ex);
        } finally {
            if (s!=null && s.isOpen()) s.close();
        }
    }

    public void clear() {
        if (Messagebox.show("Do you want to clear the list?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
            lb.getItems().clear();
            productICDs.clear();
            pg.setTotalSize(0);
        }
    }

    private void refineProductICDs() {
        Map<String,ICDPOJO> productICDMap = new HashMap<String,ICDPOJO>();

        for (ICDPOJO icdPOJO : tempProductICDs) {
            if (icdPOJO!=null && productICDMap.get(icdPOJO.getIcdCode())==null) {
                productICDs.add(icdPOJO);
                productICDMap.put(icdPOJO.getIcdCode(), icdPOJO);
            }
        }
    }

    public void importExceptions(UploadEvent evt) {
        if (Messagebox.show("Do you want to upload from " + evt.getMedia().getName() + "?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
            tempProductICDs.clear();

            try {
                POIFSFileSystem fs = new POIFSFileSystem(evt.getMedia().getStreamData());
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sheet = wb.getSheetAt(0);
                for (int i=0; i<sheet.getLastRowNum()+1; i++) {
                    HSSFRow row = sheet.getRow(i);
                    HSSFCell cell1 = row.getCell(0);
                    HSSFCell cell2 = row.getCell(2);
                    HSSFCell cell3 = row.getCell(3);
                    HSSFCell cell4 = row.getCell(4);
                    HSSFCell cell5 = row.getCell(5);
                    HSSFCell cell6 = row.getCell(6);
                    
                    
                    
                    String icd1 = cell1.getStringCellValue().trim();
                    String icd2 = cell2.getStringCellValue().trim();
                    String icd3 = cell3.getStringCellValue().trim();
                    String icd4 = cell4.getStringCellValue().trim();
                    String icd5 = cell5.getStringCellValue().trim();
                    String icd6 = cell6.getStringCellValue().trim();
                    
                    Messagebox.show("data :" + icd1 +icd2 +icd3 +icd4 + icd5 + icd6 );
                    
                    Listitem li = new Listitem();
                   
                    li.appendChild(new Listcell(icd1));
                    li.appendChild(new Listcell(icd2));

                    lb.appendChild(li);
                    
               
                }

                 //pg.setTotalSize(productICDs.size());
                //populate(0, pg.getPageSize());
            } catch (Exception ex) {
                log.error("importExceptions", ex);
            }
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (bbClient.getAttribute("e")==null) {
            valid = Libs.showErrorForm("Please select Client!");
        } else if (bbProduct.getAttribute("e")==null) {
            valid = Libs.showErrorForm("Please select Product!");
        } else if (lb.getItems().size()==0) {
            valid = Libs.showErrorForm("Please add ICD to the list!");
        }

        return valid;
    }

    public void save() {
        if (validate()) {
            if (Messagebox.show("Do you want to save this ICD Exceptions?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
                if (saveProcess()) {
                    Messagebox.show("ICD Exceptions has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
                } else {
                    Messagebox.show("Error occured while uploading. Please see log file", "Error", Messagebox.OK, Messagebox.ERROR);
                }
            }
        }
    }

    private boolean saveProcess() {
        boolean result = false;

        Session s = Libs.sfDB.openSession();
        try {
            s.beginTransaction();

            String qry = "delete from EDC_PRJ.dbo.edc_endorsmentbypolis "
                    + "where thnpolis=" + tPolicyYear.getText() + " "
                    + "and nopolis=" + tPolicyNumber.getText() + ";";

            s.createSQLQuery(qry).executeUpdate();
            s.flush();
            s.clear();

            for (ICDPOJO icdPOJO : productICDs) {
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                qry = "insert into EDC_PRJ.dbo.edc_endorsmentbypolis ("
                        + "thnpolis, brpolis, distpolis, nopolis, endorsmentid, endorsmenttype,"
                        + "createdate, createby, modifydate, modifyby, flg"
                        + ") values ("
                        + tPolicyYear.getText() + ","
                        + 1 + ","
                        + 0 + ","
                        + tPolicyNumber.getText() + ","
                        + "'" + icdPOJO.getIcdCode() + "',"
                        + 2 + ","
                        + "convert(datetime, '" + date + "', 105),"
                        + "'" + Libs.getUsername() + "',"
                        + "convert(datetime, '" + date + "', 105),"
                        + "'" + Libs.getUsername() + "',"
                        + "'1'"
                        + ");";

                s.createSQLQuery(qry).executeUpdate();
                s.flush();
                s.clear();

                result = true;
            }

            s.getTransaction().commit();
        } catch (Exception ex) {
            log.error("saveProcess", ex);
        } finally {
            if (s!=null && s.isOpen()) s.close();
        }

        return result;
    }


    public void icdSelected() {
        if (lb.getSelectedCount()==1) {
            ((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
        }
    }

    public void delete() {
        if (Messagebox.show("Do you want to remove this exception?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
            productICDs.remove(lb.getSelectedItem().getValue());
            lb.removeChild(lb.getSelectedItem());
            Messagebox.show("Exception has been removed", "Information", Messagebox.OK, Messagebox.INFORMATION);
        }
    }

}
