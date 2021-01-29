package com.larryhsiao.nyx.desktop;

import com.larryhsiao.clotho.database.SingleConn;
import com.larryhsiao.nyx.core.LocalNyx;
import com.larryhsiao.nyx.core.NyxDb;
import com.larryhsiao.nyx.core.sync.server.NyxServer;
import com.larryhsiao.nyx.desktop.attachments.DesktopAttachments;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        new Thread(this::launchServer).start();
    }

    private void launchServer() {
        try {
            final File workspace = new File("build/workspace");
            workspace.mkdir();
            new NyxServer(
                new LocalNyx(
                    new SingleConn(
                        new NyxDb(
                            new File(workspace, "nyx")
                        )
                    ),
                    new DesktopAttachments(new File(workspace, "attachments"))
                )
            ).launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}