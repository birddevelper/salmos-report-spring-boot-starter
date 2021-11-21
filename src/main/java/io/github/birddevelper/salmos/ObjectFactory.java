package io.github.birddevelper.salmos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
@Setter
public class ObjectFactory<T> {

    protected Map<String,String> reportFields;

    @Setter(value= AccessLevel.NONE)
    protected List<Map<String,Object>> listOfObjects;

    public void loadObjects(List<T> objects){
        if(reportFields==null)
            throw new IllegalArgumentException("No field specified to be shown in report, first set report fields in reportFields ");
        listOfObjects = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(T obj: objects){
            Map<String, Object> objectMapping = objectMapper.convertValue(obj, Map.class);
            Map<String,Object> reportRow =  new HashMap<>();
            for (Map.Entry<String, String> field : reportFields.entrySet())
               reportRow.put(field.getValue(), objectMapping.get(field.getKey()));
            listOfObjects.add(reportRow);

        }
    }

}
