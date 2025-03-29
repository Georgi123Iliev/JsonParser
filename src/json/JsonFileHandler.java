package src.json;

import src.json.Parsing.JsonParseResult;
import src.json.Parsing.JsonParser;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonFileHandler {

    private Path filePath;
    private String rawText;
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



        Path path = Path.of(fileName);

        if (!Files.exists(path)) {

            return "File does not exist";
        }

        try {
            rawText = Files.readString(path);
            filePath = path;
            jsonParseResult = JsonParser.parseJson(rawText);
            if(jsonParseResult.isSuccess())
                jsonData = jsonParseResult.parsedData;


            isSaved = true;

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());

        }

        return "";
    }


    public String Set(String path,String json)
    {
        var ParseResult = JsonParser.parseJson(json);

        if(!ParseResult.isSuccess())
            return ParseResult.errorMessage;



        return "";
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
        var valsFound = search(key,jsonData);

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


          for (var key : JsonObj.keySet())
          {
              var newJsonObj = JsonObj.get(key);
              sb.append(biggerPadding);

              sb.append("\"" +key + "\""+ " -> ");
              if(newJsonObj instanceof Map<?,?> || newJsonObj instanceof List<?>)
              {
                  sb.append("\n");
              }
              sb.append(getStructuredJsonUtility(newJsonObj,depth+1) +"\n");
          }

          sb.append(padding+"}");

          return sb.toString();

      }
      else if(jsonToShow instanceof List<?>)
      {


          List<Object> JsonObj =  (List<Object>) jsonToShow;
          sb.append(padding+"[\n");


          for (var item : JsonObj)
          {

              sb.append(biggerPadding);


              if(item instanceof Map<?,?> || item instanceof List<?>)
              {
                  sb.append("\n");
              }
              sb.append(getStructuredJsonUtility(item,depth+1) +"\n");
          }

          sb.append(padding+"]");

          return sb.toString();

      }

          if(jsonToShow instanceof String)
            return "\""+jsonToShow.toString() +"\"";

          return jsonToShow.toString();

    }
}
