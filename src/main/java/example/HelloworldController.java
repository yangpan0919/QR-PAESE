package example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.Event;
import javafx.fxml.FXML;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@FXMLController
public class HelloworldController implements Initializable {

    @FXML
    private Label helloLabel;

    @FXML
    private TextField nameField;


    @FXML
    public void exportclick(Event event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择文件");
//        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();
        final File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            nameField.setText(selectedDirectory.getAbsolutePath());
        }

    }

    @FXML
    private void setHelloText(final Event event) {


        String text = nameField.getText();
        text = text.trim();
        if (StringUtils.isEmpty(text)) {
            helloLabel.setText("请填写文件路径！！！");
            return;
        }
        System.out.println("文件路径为：" + text);
        File file = new File(text);

        try {
            if (!file.exists()) {
                CommonUiUtil.alert(Alert.AlertType.INFORMATION, "请填写正确文件路径！！！");
                return;
            }
            parseFile(file, text);

            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "文件解析成功");
        } catch (Exception e) {
            e.printStackTrace();
            CommonUiUtil.alert(Alert.AlertType.ERROR, "文件解析失败!!!");
        }


    }

    private void parseFile(File file, String text) throws Exception {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                outOneFile(file1, text);
            }
        }

    }

    public static String logKey = "wojiushizhemezhuai";

    private void outOneFile(File file, String text) throws Exception {
        if (file.isDirectory()) {
            return;
        }
        String path = file.getPath();
        InputStream inTest = new FileInputStream(file);
        BufferedReader testbr = new BufferedReader(new InputStreamReader(inTest, "UTF-8"));
        List<String> list = new ArrayList<>();
        String strTemp;
        while ((strTemp = testbr.readLine()) != null) {
            String trim = strTemp.trim();
            if (StringUtils.isEmpty(trim)) {
                continue;
            }
            if (trim.contains("PPLog [INFO]")) {
                list.add(trim);
            } else {
                int index = list.size() - 1;
                list.set(index, list.get(index) + "\n" + trim);
            }

        }
        String replace = path.replace(text, text + "_RESULT");
        System.out.println(file.getName());
        System.out.println(replace);
        List<String> resultList = new ArrayList<>();

        for (String s : list) {
            String pre = s.substring(0, 33);
            String changeStr = s.substring(33);

            String str = pre + AESUtil.AESDncode(logKey, changeStr);
            resultList.add(str);
        }
        File out = new File(replace);
        FileOutputStream fos = FileUtils.openOutputStream(out);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (int i = 0; i < resultList.size(); i++) {
            bw.write(resultList.get(i) + "\n");
        }
        bw.flush();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameField.setEditable(false);
    }
}
