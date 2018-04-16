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
public class AdminController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ParseException {
        // Quelle action a appelé cette servlet ?
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        DAO dao = new DAO();

        ArrayList<String> des = dao.allProduct();
        request.setAttribute("listeProduits", des);

        // pour le CA par produit
        String date_debut = request.getParameter("date_debut");
        String date_fin = request.getParameter("date_fin");

        // pour le CA par categorie d'article
        String date_debut_cat = request.getParameter("date_debut_cat");
        String date_fin_cat = request.getParameter("date_fin_cat");

        // Ca par Geo
        String date_debut_geo = request.getParameter("date_debut_geo");
        String date_fin_geo = request.getParameter("date_fin_geo");

        // Ca par Client
        String date_debut_cli = request.getParameter("date_debut_cli");
        String date_fin_cli = request.getParameter("date_fin_cli");
        
        
        // Ca par ZIP (zone géo)
        String date_debut_zip = request.getParameter("date_debut_zip");
        String date_fin_zip = request.getParameter("date_fin_zip");

        if (null != action) {
            switch (action) {
                case "logout":
                    doLogout(request);
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    break;
                case "caByProduct":
                    session.setAttribute("productCA", dao.chiffreAffaireByProduct(date_debut, date_fin));
                    session.setAttribute("dateProduct", "du " + date_debut + " au " + date_fin);

                    request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                    break;

                case "caByProductCode":
                    session.setAttribute("productCodeCA", dao.chiffreAffaireByProductCode(date_debut_cat, date_fin_cat));
                    session.setAttribute("dateProductCode", "du " + date_debut_cat + " au " + date_fin_cat);

                    request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                    break;

                case "caByGeo":
                    session.setAttribute("geoCA", dao.chiffreAffaireByState(date_debut_geo, date_fin_geo));
                    session.setAttribute("dateGeo", "du " + date_debut_geo + " au " + date_fin_geo);

                    request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                    break;

                case "caByCli":
                    session.setAttribute("cliCA", dao.chiffreAffaireByCustomer(date_debut_cli, date_fin_cli));
                    session.setAttribute("dateCli", "du " + date_debut_cli + " au " + date_fin_cli);
                    request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                    break;
                    
                    
                case "caByZip":
                    session.setAttribute("zipCA", dao.chiffreAffaireByZip(date_debut_zip, date_fin_zip));
                    session.setAttribute("dateZip", "du " + date_debut_zip + " au " + date_fin_zip);
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
        return;
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
        return;
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
