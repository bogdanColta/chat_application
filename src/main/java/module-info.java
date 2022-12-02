module chat.chat_app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens chat.chat_app.server to javafx.fxml;
    exports chat.chat_app.server;
}