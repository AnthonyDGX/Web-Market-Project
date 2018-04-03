/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Customer;
import model.DAO;
import model.DiscountCode;

/**
 *
 * @author DGX
 */
@WebServlet(name = "adminController", urlPatterns = {"/adminController"})
public class AdminController extends HttpServlet{
     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, SQLException, ParseException {
		// Quelle action a appelé cette servlet ?
		String action = request.getParameter("action");
                HttpSession session = request.getSession();
                DAO dao = new DAO();

                
                ArrayList<String> des = dao.allProduct();
                request.setAttribute("listeProduits", des);
                
                // pour le CA par produit
                DateFormat formatter;
                formatter = new SimpleDateFormat("yy-MMM-dd");
                Date  date_debut = formatter.parse(request.getParameter("date_debut"));
                Date  date_fin = formatter.parse(request.getParameter("date_fin"));
		if (null != action) {
			switch (action) {				
				case "logout":
					doLogout(request);
                                        request.getRequestDispatcher("login.jsp").forward(request, response);
					break;
                                case "caByProduct":
                                    session.setAttribute("productCA", dao.chiffreAffaireByProduct(date_debut, date_fin));
                                    request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                                break;

                                                                
                                
			}
		}

		

	}
     
     @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
             Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
         }
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
             Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
         }
}
     
     
     
   

	private void doLogout(HttpServletRequest request) {
		// On termine la session
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	private String findUserInSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return (session == null) ? null : (String) session.getAttribute("userName");
	}
}
