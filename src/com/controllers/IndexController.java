package com.controllers;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.tools.Libs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class IndexController extends Window {

    private Logger log = LoggerFactory.getLogger(IndexController.class);
    private Textbox tUsername;
    private Textbox tPassword;

    public void onCreate() {
        initComponents();
       
    }

    private void initComponents() {
        tUsername = (Textbox) getFellow("tUsername");
        tPassword = (Textbox) getFellow("tPassword");
        tUsername.setFocus(true);
    }

    public void login() {
        boolean valid = false;
        Session s = Libs.sfDB.openSession();
        try {
              String qry = "select * "
                    + "from "+Libs.getDbName()+".dbo.ms_user "
                    + "where userid='" + tUsername.getText() + "' and password='" + tPassword.getText() + "';";
        		
            System.out.println(qry);
            List<Object[]> l = s.createSQLQuery(qry).list();
            if (l.size()==1) valid = true;
            
        } catch (Exception ex) {
            log.error("login", ex);
        } finally {
            if (s!=null && s.isOpen()) s.close();
        }

        System.out.println(valid);
        
        if (valid) {
        	Object[] obj = Libs.getSalesCode(tUsername.getText());
      	  	Executions.getCurrent().getSession().setAttribute("sales", obj[0]);
            Executions.getCurrent().getSession().setAttribute("user", tUsername.getText());
            Executions.sendRedirect("main.zul");
        } else {
            Messagebox.show("Invalid username/password combination!", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

  
}
