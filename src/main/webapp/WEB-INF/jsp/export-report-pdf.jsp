<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "com.itextpdf.text .*, com.itextpdf.text.pdf .*, java.io. *"%>
<%
response.setContentType("application/pdf");
Document document = new Document();
try{
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
PdfWriter.getInstance(document, buffer);
document.open();

document.addTitle("Report");

document.add(new Paragraph(new Phrase("Move2Alf report", new Font(BaseFont.createFont(BaseFont.HELVETICA_BOLD, "Cp1252", false)))));

document.add( Chunk.NEWLINE );

%>
<c:set var="name" value="${job.name}" scope="session" />
<%
document.add(new Paragraph("Name: "+(String) session.getAttribute("name")));

%>
<c:set var="description" value="${job.description}" scope="session" />
<%
document.add(new Paragraph("Description: "+(String) session.getAttribute("description")));

%>
<fmt:formatDate var="startTime" value="${cycle.startDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
<c:set var="startDateTime" value="${startTime}" scope="session" />
<%
document.add(new Paragraph("Start Time: "+session.getAttribute("startDateTime").toString()));

%>
<fmt:formatDate var="endTime" value="${cycle.endDateTime}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
<c:set var="endDateTime" value="${endTime}" scope="session" />
<%
if(null == session.getAttribute("endDateTime")){
	document.add(new Paragraph("End Time: "));
}else{
	document.add(new Paragraph("End Time: "+session.getAttribute("endDateTime").toString()));
}

%>
<c:set var="duration" value="${duration}" scope="session" />
<%
document.add(new Paragraph("Duration: "+(String) session.getAttribute("duration")));

%>
<c:set var="status" value="${cycle.schedule.state.displayName}" scope="session" />
<%
document.add(new Paragraph("Status: "+(String) session.getAttribute("status")));

%>
<c:set var="nrDocuments" value="${documentListSize}" scope="session" />
<%
document.add(new Paragraph("Nr. of documents: "+ session.getAttribute("nrDocuments").toString()));

%>
<c:set var="docsPerSecond" value="${docsPerSecond}" scope="session" />
<%
document.add(new Paragraph("Docs / s: "+(String) session.getAttribute("docsPerSecond")));

document.add( Chunk.NEWLINE );

PdfPTable table = new PdfPTable(3);

table.setHorizontalAlignment(Element.ALIGN_LEFT);

//firsth row
PdfPCell cell = new PdfPCell(new Paragraph("List of imported documents"));
cell.setColspan(3);
table.addCell(cell);

//second row
table.addCell("Name");
table.addCell("Processed date and time");
table.addCell("Status");

%>

<c:forEach var="document" items="${processedDocuments}" >
<c:set var="docName" value="${document.name}" scope="session" />
<c:set var="docDateTime" value="${document.processedDateTime}" scope="session" />
<c:set var="docStatus" value="${document.status.displayName}" scope="session" />

<%//twelfth row
table.addCell((String) session.getAttribute("docName"));
table.addCell(session.getAttribute("docDateTime").toString());
table.addCell((String) session.getAttribute("docStatus"));
%>
</c:forEach> 
<%
document.add(table);
document.close();

DataOutput dataOutput = new DataOutputStream(response.getOutputStream());
byte[] bytes = buffer.toByteArray();
response.setContentLength(bytes.length);
for(int i = 0; i < bytes.length; i++)
{
dataOutput.writeByte(bytes[i]);
}

}catch(DocumentException e){
e.printStackTrace();
}

%>
