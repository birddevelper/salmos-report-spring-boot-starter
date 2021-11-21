package io.github.birddevelper.salmos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class HtmlReportMakerTest {


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
            student.name = "Jessi Je - "+i;
            student.age = 34+i;
            student.skills = Arrays.asList("java", "node");
            studentList.add(student);
        }

        ObjectFactory objectFactory = new ObjectFactory();
        Map<String,String> fieldMap = new HashMap<>();
        fieldMap.put("name", "Full Name");
        fieldMap.put("age", "Age");

        objectFactory.setReportFields(fieldMap);
        objectFactory.loadObjects(studentList);
        HtmlReportMaker htmlReportMaker = new HtmlReportMaker(objectFactory);
        Assertions.assertNotEquals("",htmlReportMaker.generate());

    }
}
