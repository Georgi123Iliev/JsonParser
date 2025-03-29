package src.json.commands;


import src.json.JsonFileHandler;

public interface ICommand {
    String Execute(JsonFileHandler jsonFileHandler, String[] args);


}
