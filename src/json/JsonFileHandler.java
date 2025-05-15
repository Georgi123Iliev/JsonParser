package src.json;

import jdk.jshell.spi.ExecutionControl;
import src.exception.InvalidJsonPathException;
import src.json.Parsing.JsonParseResult;
import src.json.Parsing.JsonParser;
import src.json.Parsing.ValueParseResult;
import src.json.Parsing.ValueParser;
import src.validators.JsonPathIntersectionValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;

public class JsonFileHandler {

    private Path filePath;

    private Object jsonData;


    private JsonParseResult jsonParseResult;
    private boolean isSaved;




    public JsonFileHandler(){};

    public String OpenFile(String fileName)
    {



        if(jsonData != null && !isSaved)
        {
            return "A file is currently opened and not been saved.";
        }

        jsonData = null;
        isSaved = false;


        Path path = Path.of(fileName);

        if (!Files.exists(path)) {

            return "File does not exist";
        }

        try {
            var rawText = Files.readString(path);
            filePath = path;
            jsonParseResult = JsonParser.parseJson(rawText);
            if(jsonParseResult.isSuccess())
                jsonData = jsonParseResult.parsedData;


            isSaved = true;

        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();

        }

        return "";
    }


    private class Nothing{};

    public String remove(String JsonPath)
    {
        var pathQueue = JsonPathQueue(JsonPath);

        return assign(pathQueue, jsonData,null,new Nothing());
    }

    private String assign(Queue<String> JsonPath, Object jsonData,String previousObjectName,Object valueToAdd)
    {

        if(JsonPath.isEmpty())
            return "Empty json path";

        String head = JsonPath.poll();

        if(jsonData instanceof List<?>)
        {


            List<Object> list = (List<Object>) jsonData;





            if(head.matches("\\[\\d+\\]"))
            {
                int num = Integer.parseInt(head.substring(1,head.length()-1));

                if(num >= list.size())
                {
                      return head + " is bigger than the number of items in the list " + previousObjectName;

                }

                if(JsonPath.isEmpty())
                {
                    if(valueToAdd instanceof Nothing)
                    {
                        list.remove(num);
                        return "Element removed";
                    }
                    else {
                        list.set(num, valueToAdd);
                        return "Element set";
                    }

                }

                return assign(JsonPath,list.get(num),head,valueToAdd);
            }
            else
            {
                return "In list " + previousObjectName + " "
                        + head + " has unexpected format for an index";
            }
        } else if (jsonData instanceof Map<?,?>)
        {
            {
                Map<String,Object> map = (Map<String,Object>) jsonData;

                if(map.containsKey(head))
                {
                    if(JsonPath.isEmpty())
                    {

                        if(valueToAdd instanceof Nothing)
                        {
                            map.remove(head);
                            return "Element removed";
                        }
                        else {
                            map.put(head, valueToAdd);
                            return "Element set";
                        }


                    }

                    return assign(JsonPath,map.get(head),head,valueToAdd);
                }
            }

        }



        return "Json path doesn't correspond to working json object " + previousObjectName + " has no element that corresponds to " + head;
    }


    private class NotFound
    {
        public String explanation;

        public NotFound(String explanation) {
            this.explanation = explanation;
        }
    }


    public String move(String from,String to)
    {
        var validationResult = JsonPathIntersectionValidator.Validate(from,to);

        if(!validationResult.isSuccess())
        {
            return validationResult.getReasonForFailure();
        }

        var fromQueue = JsonPathQueue(from);

        Object value = getValueAt(fromQueue, jsonData,"");

        if(value instanceof NotFound)
        {
            return ((NotFound) value).explanation;
        }

        remove(from);


       return assign(JsonPathQueue(to), jsonData,"",value);


    }

    private Object getValueAt(Queue<String> JsonPath, Object jsonObj, String previousObjectName)
    {
        if(JsonPath.isEmpty())
            return new NotFound("Empty json path");

        String head = JsonPath.poll();



        if(jsonObj instanceof List<?>)
        {


            List<Object> list = (List<Object>) jsonObj;





            if(head.matches("\\[\\d+\\]"))
            {
                int num = Integer.parseInt(head.substring(1,head.length()-1));

                if(num >= list.size())
                {
                    return new NotFound(head + " is bigger than the number of items in the list "
                        + previousObjectName);

                }

               if(JsonPath.isEmpty())
                   return list.get(num);

                return getValueAt(JsonPath,list.get(num),head);
            }
            else
            {
                return new NotFound("In list " + previousObjectName + " "
                        + head + " has unexpected format for an index");
            }
        } else if (jsonObj instanceof Map<?,?>)
        {


            {
                Map<String,Object> map = (Map<String,Object>) jsonObj;

                if(map.containsKey(head))
                {
                    if(JsonPath.isEmpty())
                    {

                       return map.get(head);


                    }

                    return getValueAt(JsonPath,map.get(head),head);
                }
            }

        }



        return new NotFound("Json path doesn't correspond to working json object " + previousObjectName + " has no element that corresponds to " + head);
    }


    private String QueueToStr(Queue<String> q)
    {
        StringBuilder sb = new StringBuilder();

        for(var i : q)
        {
            sb.append(i);
        }

        return sb.toString();
    }
    private Queue<String> JsonPathQueue(String JsonPath)
    {
        Queue<String> pathQueue = new ArrayDeque<>();

        for (var s : JsonPath.split("\\."))
        {
            int bracketIndex = -1;
            var substr = s;
            boolean wasSplit = false;
            while(true) {

                bracketIndex = substr.indexOf('[');

                if (bracketIndex != -1) {

                    if(bracketIndex != 0) {
                        var beforeBracket = substr.substring(0, bracketIndex);

                        pathQueue.add(beforeBracket);
                    }
                    var closingIndex = substr.indexOf(']');

                    pathQueue.add(substr.substring(bracketIndex,closingIndex+1));

                    substr = substr.substring(closingIndex+1);

                    wasSplit = true;
                }
                else break;
            }
            if(!wasSplit)
                 pathQueue.add(s);


        }

        return pathQueue;
    }



    public String set(String JsonPath,String Json)
    {

        ValueParseResult valueParseResult = ValueParser.parseValue(Json);
        var jsonParseResult = JsonParser.parseJson(Json);
        if(!valueParseResult.isSuccess) {
            if (jsonParseResult.isSuccess()) {
                return jsonParseResult.errorMessage;
            }
            else
                return "Bad json string";
        }




        if(!jsonParseResult.isSuccess())
            return jsonParseResult.errorMessage;


        var pathQueue = JsonPathQueue(JsonPath);

        return assign(pathQueue, jsonData,null, jsonParseResult.parsedData);
    }

    public String Validate()
    {

        if(jsonParseResult == null)
            return "You must first open a file before validating";

        if(!jsonParseResult.isSuccess())
        {
             return jsonParseResult.errorMessage;
        }
        else {
            return "Json is valid";
        }
    }

    public String search(String key)
    {
        var valsFound = search(key, jsonData);

        if(valsFound.isEmpty())
        {
            return "No values with the key \"" + key + "\"";
        }

        return getStructuredJsonUtility(valsFound,1);
    }

    private List<Object> search(String Key, Object JsonObj)
    {

        List<Object> Values = new ArrayList<Object>();

        if(JsonObj instanceof Map<?,?>)
        {
            var JsonMap = (Map<String,Object>) JsonObj;

            for (var memberKey : JsonMap.keySet())
            {
                var Value = JsonMap.get(memberKey);

                if(memberKey.equals(Key))
                {
                    Values.add(Value);
                }

                Values.addAll(search(Key,Value));
            }
        }
        else if(JsonObj instanceof List<?>)
        {
            var JsonList = (List<Object>) JsonObj;

            for (var jsonItem : JsonList) {
               Values.addAll(search(Key,jsonItem));

            }
        }


        return Values;
    }



    public String getStructuredJson()
    {
        return getStructuredJsonUtility(jsonData,1);
    }

    private static String getStructuredJsonUtility(Object jsonToShow,int depth)
    {
    StringBuilder sb = new StringBuilder();

    String padding = "\t".repeat(depth-1);
    String biggerPadding = "\t".repeat(depth);

      if(jsonToShow instanceof Map<?,?>)
      {
          Map<String,Object> JsonObj = (Map<String, Object>) jsonToShow;
          sb.append(padding+"{\n");

          int n = JsonObj.keySet().toArray().length;

          for (int i =0;i < n;i++)
          {
              var key = JsonObj.keySet().toArray()[i];
              var newJsonObj = JsonObj.get(key);
              sb.append(biggerPadding);

              sb.append("\"" +key + "\""+ " : ");
              if(newJsonObj instanceof Map<?,?> || newJsonObj instanceof List<?>)
              {
                  sb.append("\n");
              }
              sb.append(getStructuredJsonUtility(newJsonObj,depth+1) +((i != n-1) ? ",\n" :"\n"));
          }

          sb.append(padding+"}");

          return sb.toString();

      }
      else if(jsonToShow instanceof List<?>)
      {


          List<Object> JsonObj =  (List<Object>) jsonToShow;


          sb.append(padding+"[");

          if(!JsonObj.isEmpty())
              sb.append("\n");


          int n = JsonObj.size();

          for (int i = 0;i<n;i++)
          {

              var item = JsonObj.toArray()[i];

              if(!(item instanceof Map<?,?> || item instanceof List<?>))
              {
                  sb.append(biggerPadding);
              }




              sb.append(getStructuredJsonUtility(item,depth+1) +((i != n-1) ? ",\n" :"\n"));
          }

          sb.append(padding+"]");

          return sb.toString();

      }

          if(jsonToShow instanceof String)
            return "\""+jsonToShow +"\"";

          return jsonToShow.toString();

    }

    public String saveAs(String fileName)
    {
        Path filePath = Path.of(fileName);
        try {
            Files.createFile(filePath);
            Files.writeString(filePath,getStructuredJson());
        } catch (IOException e) {
                return "Error reading file: " + e.getMessage();
        }


        isSaved = true;
        return "";

    }

    private void CompletePath(Queue<String> jsonPath,Object jsonObj) throws InvalidJsonPathException
    {
        var head = jsonPath.poll();

        var next = jsonPath.peek();




        if(head == null)
            return;


        Object nextObjectToAdd;
        if(next == null)
        {
            nextObjectToAdd = null;
        }
        else if(next.matches("\\[\\d+\\]"))
        {
            nextObjectToAdd = new ArrayList<Object>();
        }
        else
        {
            nextObjectToAdd = new HashMap<String,Object>();
        }


        if(jsonObj instanceof List<?>)
        {


            List<Object> list = (List<Object>) jsonObj;


            if(head.matches("\\[\\d+\\]"))
            {
                int num = Integer.parseInt(head.substring(1,head.length()-1));

                if(num == list.size())
                {
                   list.add(nextObjectToAdd);
                }
                else if(num > list.size())
                {
                    throw new InvalidJsonPathException("");
                }

                CompletePath(jsonPath,list.get(num));
            }
            else
            {
                //ГРЕШЕН ПЪТ
                var stringifiedQueue =  QueueToStr(jsonPath);
                throw new InvalidJsonPathException("The json object does not correspond to the given json path" +
                        "list expected before "   + (stringifiedQueue.isEmpty() ? "the end of the path" : stringifiedQueue) + "but not found");
            }
        } else if (jsonObj instanceof Map<?,?>)
        {
            if(head.matches("\\[\\d+\\]"))
            {
                var stringifiedQueue =  QueueToStr(jsonPath);
                throw new InvalidJsonPathException("Unexpected list " + head + " in the json path before "
                        + (stringifiedQueue.isEmpty() ? "the end of the path" : stringifiedQueue));
            }

                Map<String,Object> map = (Map<String,Object>) jsonObj;

                if(map.containsKey(head))
                {
                    if(jsonPath.isEmpty())
                    {

                        return;


                    }

                    CompletePath(jsonPath,map.get(head));
                }
                else
                {
                    map.put(head,nextObjectToAdd);
                    CompletePath(jsonPath,map.get(head));
                }


        }
    }
    public String close()
    {
        if(isSaved)
        {
            jsonData = null;
            isSaved = false;
            filePath = null;
            jsonParseResult = null;
            return "File closed";
        }
        else
            return "File not saved";

    }


    public String create(String where, String what)
    {

        var parseResult = JsonParser.parseJson(what);

        if(!parseResult.isSuccess())
        {
            return "Error in parsing <to> parameter: " + parseResult.errorMessage;
        }

        Object value = parseResult.parsedData;


        var pathQueue = JsonPathQueue(where);

        try {
            CompletePath(pathQueue,jsonData);
        } catch (InvalidJsonPathException e) {
            return e.getMessage();
        }

        assign(JsonPathQueue(where),jsonData, null,value);
        return "";

    }


    public String save()
    {
        if(filePath != null)
             return saveAs(filePath.toString());
        else
            return "Няма отворен файл";
    }
}
