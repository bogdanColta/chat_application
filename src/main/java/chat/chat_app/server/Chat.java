package chat.chat_app.server;

import chat.chat_app.client.ChatClient;
import chat.chat_app.client.ChatListener;
import chat.chat_app.client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.Grid;

import static javafx.application.Platform.runLater;


public class Chat extends Application implements ChatListener{

    static TextArea chatArea;

    static Label invaliMessage;

    static int portInt = -1;

    @Override
    public void start(Stage stage) throws IOException {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label ipLabel = new Label("IP");
        GridPane.setConstraints(ipLabel, 0, 0);

        TextField ip = new TextField();
        GridPane.setConstraints(ip, 5, 0);

        Label portLabel = new Label("Port");
        GridPane.setConstraints(portLabel, 0, 2);

        TextField port = new TextField();
        GridPane.setConstraints(port, 5, 2);

        Label nameLabel = new Label("Username");
        GridPane.setConstraints(nameLabel, 0, 4);

        TextField username = new TextField();
        GridPane.setConstraints(username, 5, 4);

        Button connect = new Button("Connect");
        GridPane.setConstraints(connect, 5, 10);

        grid.getChildren().addAll(ipLabel, ip, portLabel, port, nameLabel, username, connect);

        Scene scene = new Scene(grid, 300, 300);
        stage.setScene(scene);
        stage.show();

        GridPane grid1 = new GridPane();
        grid1.setPadding(new Insets(10, 10, 10, 10));
        grid1.setVgap(10);
        grid1.setHgap(10);

        TextField mess = new TextField();
        GridPane.setConstraints(mess, 0, 10);

        grid1.getChildren().add(mess);

        chatArea = new TextArea();

        chatArea.setPadding(new Insets(10,10,10,10));

        GridPane.setHgrow(chatArea, Priority.ALWAYS);
        GridPane.setVgrow(chatArea, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(chatArea);
        GridPane.setConstraints(scrollPane, 0, 0);

        grid1.getChildren().add(scrollPane);
        chatArea.setEditable(false);

        Scene scene1 = new Scene(grid1, 700, 400);

        connect.setOnAction(e -> {
            grid.getChildren().remove(invaliMessage);
            Chat chat = new Chat();

            try{
                portInt = Integer.parseInt(port.getText());
            }catch (Exception ex){

            }

            if (portInt < 0 || portInt > 65536) {
                invaliMessage = new Label("Incorrect port");
                GridPane.setConstraints(invaliMessage, 0, 15);
                grid.getChildren().add(invaliMessage);
            }else {
                ChatClient client = new Client();

                String address = ip.getText();

                if (!client.connect(address, portInt)){
                    invaliMessage = new Label("Connection failed!");
                    GridPane.setConstraints(invaliMessage, 0, 15);
                    grid.getChildren().add(invaliMessage);
                }else {
                    client.addChatListener(chat);

                    String username1 = username.getText();

                    if (!client.sendUsername(username1)){
                        invaliMessage = new Label("Invalid username!");
                        GridPane.setConstraints(invaliMessage, 0, 15);
                        grid.getChildren().add(invaliMessage);
                    }else {
                        stage.setScene(scene1);
                        Button send = new Button("Send");
                        GridPane.setConstraints(send, 10, 10);
                        grid1.getChildren().add(send);

                        send.setOnAction(c -> {
                            String line = mess.getText();
                            client.sendMessage(line);
                            mess.clear();
                        });
                    }
                }
            }

        });
    }

    public static void main(String[] args) throws IOException {
        launch();
    }

    @Override
    public void messageReceived(String from, String message) {
        System.out.println("FROM "+from+": "+message);

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        String strDate = dateFormat.format(date);
        chatArea.appendText(strDate + " | " + from + ": " + message + "\n");
    }

}


