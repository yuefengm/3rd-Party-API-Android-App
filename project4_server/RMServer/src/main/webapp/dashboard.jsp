<%@ page import="java.util.List" %>
<%@ page import="org.bson.Document" %>
<%@ page import="java.util.Date" %>
<%--<%@ page import="java.lang.long" %>--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<-! This is the dashboard page that displays the analytics data and logs. !->
<-! The data is passed to the page as request attributes. !->
<-! The data is displayed using JSP scriptlets. !->
<-! The data is displayed in a table. !->
<-! @Author: Yuefeng Ma; Andrew ID: yuefengm !->
<-! @Date: 2023-11-19 !->
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1>Operations Dashboard</h1>

<div>
    <h2>Analytics Data</h2>
    <p><strong>Top Search Terms:</strong></p>
    <ul>
        <% for(Document term : (List<Document>) request.getAttribute("topSearchTerms")) { %>
        <li><%= term.getString("_id") %>: <%= term.getInteger("count") %></li>
        <% } %>
    </ul>

    <p><strong>Total Requests:</strong> <%= request.getAttribute("requestCount") %></p>

    <p><strong>Top Device Models:</strong></p>
    <ul>
        <% for(Document model : (List<Document>) request.getAttribute("topDeviceModels")) { %>
        <li><%= model.getString("_id") %>: <%= model.getInteger("count") %></li>
        <% } %>
    </ul>
</div>

<div>
    <h2>Logs</h2>
    <table>
        <tr>
            <th>Timestamp</th>
            <th>Type</th>
            <th>Keyword</th>
            <th>User Agent</th>
            <th>Request Duration (ms)</th>
            <th>Response Status</th>
            <th>Total Response Time (ms)</th>
        </tr>
        <% for(Document log : (List<Document>) request.getAttribute("logs")) { %>
        <tr>
            <td><%= new Date((Long)log.get("timestamp"))  %></td>
            <td><%= log.getString("type") %></td>
            <td><%= log.getString("keyword") %></td>
            <td><%= log.getString("userAgent") %></td>
            <td><%= ((Number)log.get("apiRequestDuration")).intValue() %></td>
            <td><%= ((Number)log.get("responseStatus")).intValue() %></td>
            <td><%= ((Number)log.get("totalResponseTime")).intValue() %></td>
        </tr>
        <% } %>
    </table>
</div>
</body>
</html>