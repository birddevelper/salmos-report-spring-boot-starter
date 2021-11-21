package io.github.birddevelper.salmos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class GeneralReportMakerTest {

    public class Student {

        public String name;
        public int age;
        public List<String> skills;


    }

    @Test
    public void generateHtmlFromListOfObjectTest(){
        List<Student> studentList = new ArrayList<>();
        for(int i=1; i<3;i++) {
            Student student = new Student();
            student.name = "John Be - "+i;
            student.age = 34+i;
            student.skills = Arrays.asList("java", "node");
            studentList.add(student);
        }

        ObjectFactory objectFactory = new ObjectFactory();
        Map<String,String> fieldMap = new HashMap<>();
        fieldMap.put("name", "FullName");
        fieldMap.put("age", "Age");

        objectFactory.setReportFields(fieldMap);
        objectFactory.loadObjects(studentList);
        GeneralReportMaker htmlReportMaker = new GeneralReportMaker(objectFactory,"::FullName --- ::Age <br>");
        System.out.println(htmlReportMaker.generate());
        Assertions.assertNotEquals("",htmlReportMaker.generate());

    }
}
