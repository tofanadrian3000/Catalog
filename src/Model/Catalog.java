package Model;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import DTO.GroupTeacherDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.test.annotations.WrapToTest;


@WrapToTest
public class Catalog {

    /**
     *
     * @param
     * @param grade
     * writes to file the given grade for the given homework
     */
    public String writeData(Grade maxGrade, Grade grade, Student student, Integer noExemptions){
        if (noExemptions!=0)
            if (grade.getGrade()-maxGrade.getGrade()==0)
                return ("Tema: " + maxGrade.getID().getIdHomework().toString()+"\n" +
                        "Studentul: " + student.getName()+"\n" +
                        "Nota: " + String.valueOf(maxGrade.getGrade()) +"\n" +
                        "Feedback: " + grade.getFeedback() +"\n" +
                        "Absente motivate: " + String.valueOf(noExemptions) +
                        "\n\n");
            else return ("Tema: " + maxGrade.getID().getIdHomework().toString()+"\n" +
                    "Studentul: " + student.getName()+"\n" +
                    "Nota: " + String.valueOf(maxGrade.getGrade()) +"\n" +
                    "Feedback: " + grade.getFeedback() +"\n" +
                    "Penalizare: " + String.valueOf(grade.getGrade()-maxGrade.getGrade())+ "\n" +
                    "Absente motivate: " + String.valueOf(noExemptions) +
                    "\n\n");
        else if (grade.getGrade()-maxGrade.getGrade()==0)
            return ("Tema: " + maxGrade.getID().getIdHomework().toString()+"\n" +
                    "Studentul: " + student.getName()+"\n" +
                    "Nota: " + String.valueOf(maxGrade.getGrade()) +"\n" +
                    "Feedback: " + grade.getFeedback() +"\n" +
                    "\n\n");
        else return ("Tema: " + maxGrade.getID().getIdHomework().toString()+"\n" +
                    "Studentul: " + student.getName()+"\n" +
                    "Nota: " + String.valueOf(maxGrade.getGrade()) +"\n" +
                    "Feedback: " + grade.getFeedback() +"\n" +
                    "Penalizare: " + String.valueOf(grade.getGrade()-maxGrade.getGrade())+ "\n" +
                    "\n\n");
    }

    /**
     * @param student
     * @param homework
     * @param grade
     * write to file with the given filename the given grade for the given homework
     */
    public static String writeStudentGrade(Student student, Homework homework, Grade grade){
        return "Tema: " + homework.getID().toString()+"\n" +
                "Nota: " + String.valueOf(grade.getGrade()) +"\n" +
                "Predata in: " + grade.getData().toString() + "\n" +
                "Deadline: " + homework.getDeadline() + "\n" +
                "Feedback: " + grade.getFeedback()+"\n\n\n";
    }

    public void writeFinalGradeForAllStudents(List<Map.Entry<Student, Double>> finalGrades){
        File file = new File("PdfDocuments/FinalGrades.pdf");
        file.getParentFile().mkdirs();
        PdfDocument pdf;
        try {
            pdf = new PdfDocument(new PdfWriter("PdfDocuments/FinalGrades.pdf"));
            Document document = new Document(pdf);

            Paragraph headerParagraph = new Paragraph("Final Grades");
            headerParagraph.setTextAlignment(TextAlignment.CENTER);
            headerParagraph.setBold();
            headerParagraph.setFontSize(24);
            document.add(headerParagraph);

            Table table = new Table(4);
            table.setWidth(500).setMarginBottom(10);

            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Nr. Crt").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Name").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Email").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Grade").setFontSize(15)));

            AtomicInteger nrCrt= new AtomicInteger(1);
            finalGrades.forEach(x->{
                table.addCell(new Cell().add(new Paragraph(nrCrt.toString())));
                nrCrt.getAndIncrement();
                table.addCell(new Cell().add(new Paragraph(x.getKey().getName())));
                table.addCell(new Cell().add(new Paragraph(x.getKey().getEmail())));
                table.addCell(new Cell().add(new Paragraph(x.getValue().isNaN() ? "-":x.getValue().toString().substring(0, Math.min(x.getValue().toString().length(), 4)))));
            });
            table.setTextAlignment(TextAlignment.CENTER);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);

            document.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void writePassableStudents(List<Student> students){
        File file = new File("PdfDocuments/PassableStudents.pdf");
        file.getParentFile().mkdirs();
        PdfDocument pdf;
        try {
            pdf = new PdfDocument(new PdfWriter("PdfDocuments/PassableStudents.pdf"));
            Document document = new Document(pdf);

            Paragraph headerParagraph = new Paragraph("Passable Students");
            headerParagraph.setTextAlignment(TextAlignment.CENTER);
            headerParagraph.setBold();
            headerParagraph.setFontSize(24);
            document.add(headerParagraph);

            Table table = new Table(4);
            table.setWidth(500).setMarginBottom(10);

            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Nr. Crt").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Name").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Group").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Email").setFontSize(15)));

            AtomicInteger nrCrt= new AtomicInteger(1);
            students.forEach(x->{
                table.addCell(new Cell().add(new Paragraph(nrCrt.toString())));
                nrCrt.getAndIncrement();
                table.addCell(new Cell().add(new Paragraph(x.getName())));
                table.addCell(new Cell().add(new Paragraph(x.getGroup())));
                table.addCell(new Cell().add(new Paragraph(x.getEmail())));
            });
            table.setTextAlignment(TextAlignment.CENTER);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);

            document.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void writeHardestHomeworks(List<Map.Entry<Homework, Double>> homeworks){
        File file = new File("PdfDocuments/HardestHomework.pdf");
        file.getParentFile().mkdirs();
        PdfDocument pdf;
        try {
            pdf = new PdfDocument(new PdfWriter("PdfDocuments/HardestHomework.pdf"));
            Document document = new Document(pdf);

            Paragraph headerParagraph = new Paragraph("Hardest Homeworks");
            headerParagraph.setTextAlignment(TextAlignment.CENTER);
            headerParagraph.setBold();
            headerParagraph.setFontSize(24);
            document.add(headerParagraph);

            Table table = new Table(4);
            table.setWidth(500).setMarginBottom(10);

            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Nr. Crt").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Received").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Deadline").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Medium").setFontSize(15)));

            AtomicInteger nrCrt= new AtomicInteger(1);
            homeworks.forEach(x->{
                table.addCell(new Cell().add(new Paragraph(nrCrt.toString())));
                nrCrt.getAndIncrement();
                table.addCell(new Cell().add(new Paragraph(x.getKey().getReceived().toString())));
                table.addCell(new Cell().add(new Paragraph(x.getKey().getDeadline().toString())));
                table.addCell(new Cell().add(new Paragraph(x.getValue().toString().substring(0, Math.min(x.getValue().toString().length(), 4)))));
            });
            table.setTextAlignment(TextAlignment.CENTER);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);

            document.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void writeTopGroups(List<Map.Entry<GroupTeacherDTO, Double>> groups){
        File file = new File("PdfDocuments/TopGroups.pdf");
        PdfDocument pdf;
        try {
            pdf = new PdfDocument(new PdfWriter("PdfDocuments/TopGroups.pdf"));
            Document document = new Document(pdf);

            Paragraph headerParagraph = new Paragraph("Top Groups");
            headerParagraph.setTextAlignment(TextAlignment.CENTER);
            headerParagraph.setBold();
            headerParagraph.setFontSize(24);
            document.add(headerParagraph);

            Table table = new Table(4);
            table.setWidth(500).setMarginBottom(10);

            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Nr. Crt").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Group").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Professor").setFontSize(15)));
            table.addHeaderCell(new Cell().setBold().add(new Paragraph("Medium").setFontSize(15)));

            AtomicInteger nrCrt= new AtomicInteger(1);
            groups.forEach(x->{
                table.addCell(new Cell().add(new Paragraph(nrCrt.toString())));
                nrCrt.getAndIncrement();
                table.addCell(new Cell().add(new Paragraph(x.getKey().getGroup())));
                table.addCell(new Cell().add(new Paragraph(x.getKey().getTeacher())));
                table.addCell(new Cell().add(new Paragraph(x.getValue().toString().substring(0, Math.min(x.getValue().toString().length(), 4)))));
            });
            table.setTextAlignment(TextAlignment.CENTER);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);

            document.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }
}
