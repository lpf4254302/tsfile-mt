package org.apache.iotdb.tool.ui.scene;

import org.apache.iotdb.tool.core.model.AnalysedResultModel;
import org.apache.iotdb.tool.core.model.EncodeCompressAnalysedModel;
import org.apache.iotdb.tool.ui.config.TableAlign;
import org.apache.iotdb.tool.ui.table.EncodeCompressAnalyseTable;
import org.apache.iotdb.tool.ui.view.BaseTableView;

import com.sun.org.apache.xpath.internal.operations.String;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static org.apache.iotdb.tool.ui.common.constant.StageConstant.*;

/**
 * Encode and Compress Analyse
 *
 * @author shenguanchu
 */
public class EncodeAnalysePage {
  private static final Logger logger = LoggerFactory.getLogger(IoTDBParsePageV3.class);
  private Scene scene;
  private IoTDBParsePageV3 ioTDBParsePage;
  private ObservableList<EncodeCompressAnalyseTable> analyseDataList =
      FXCollections.observableArrayList();

  /** table datas */
  private TableView pageHeaderTableView;

  private TableView pageTVTableView;

  public EncodeAnalysePage() {}

  public EncodeAnalysePage(Stage stage, IoTDBParsePageV3 ioTDBParsePage) {
    this.ioTDBParsePage = ioTDBParsePage;
    init(stage);
  }

  public Scene getScene() {
    return scene;
  }

  private void init(Stage stage) {
    pageHeaderTableView = new TableView();
    pageTVTableView = new TableView();

    AnchorPane anchorPane = new AnchorPane();
    scene = new Scene(anchorPane, ENCODE_ANALYSE_PAGE_WIDTH, ENCODE_ANALYSE_PAGE_HEIGHT);
    stage.setScene(scene);
    stage.setTitle("Encoding and Compressing Analysis");
    stage.show();
    stage.setResizable(false);

    // search filter
    HBox analyseBox = new HBox();
    anchorPane.getChildren().add(analyseBox);
    analyseBox.getStyleClass().add("encode-compress-analyse-box");
    analyseBox.setPrefWidth(ENCODE_ANALYSE_PAGE_WIDTH);
    analyseBox.setPrefHeight(ENCODE_ANALYSE_PAGE_HEIGHT * 0.2);

    ObservableList<Node> searchFilterBoxChildren = analyseBox.getChildren();
    Label deviceIdLabel = new Label("deviceID:");
    TextField deviceIdText = new TextField();
    Label measurementIdLabel = new Label("measurementID:");
    TextField measurementIdText = new TextField();
    Button searchButton = new Button("Analyse");
    searchButton.setGraphic(new ImageView("/icons/find-light.png"));
    searchButton.getStyleClass().add("search-button");

    searchFilterBoxChildren.addAll(
        deviceIdLabel, deviceIdText, measurementIdLabel, measurementIdText, searchButton);

    // button click event
    searchButton.setOnMouseClicked(
        event -> {
          java.lang.String deviceIdTextText = deviceIdText.getText().trim();
          java.lang.String measurementIdTextText = measurementIdText.getText().trim();
          try {
            AnalysedResultModel analysedResultModel =
                ioTDBParsePage
                    .getTsFileAnalyserV13()
                    .fetchAnalysedResultWithDeviceAndMeasurement(
                        deviceIdTextText, measurementIdTextText);
            showQueryDataSet(analysedResultModel);
          } catch (Exception exception) {
            logger.error(
                "Failed to analyse the encode and compression type of the TimeSeries, deviceId:{}, measurementId:{}",
                deviceIdTextText,
                measurementIdTextText);
          }
        });

    //        IPageInfo pageInfo = (PageInfo) pageItem.getValue().getParams();

    // 数据来源
    //        try {
    //            BatchData batchData =
    //                    ioTDBParsePage
    //                            .getTsFileAnalyserV13()
    //                            .fetchBatchDataByPageInfo((PageInfo)
    // pageItem.getValue().getParams());
    //            while (batchData.hasCurrent()) {
    //                Object currValue = batchData.currentValue();
    //                this.tvDatas.add(
    //                        new IoTDBParsePageV3.TimesValues(
    //                                new Date(batchData.currentTime()).toString(),
    //                                currValue == null ? "" : currValue.toString()));
    //                batchData.next();
    //            }
    //        } catch (Exception e) {
    //            logger.error(
    //                    "Failed to get page details, the page statistics:{}",
    //                    pageInfo.getStatistics().toString());
    //        }

    BaseTableView baseTableView = new BaseTableView();

    // table page data
    AnchorPane pageDataPane = new AnchorPane();
    pageDataPane.setLayoutX(0);
    pageDataPane.setLayoutY(ENCODE_ANALYSE_PAGE_HEIGHT * 0.2);
    pageDataPane.setPrefHeight(ENCODE_ANALYSE_PAGE_HEIGHT * 0.2);
    anchorPane.getChildren().add(pageDataPane);
    TableColumn<String, String> typeNameCol =
        baseTableView.genColumn(TableAlign.CENTER, "typeName", "typeName");
    TableColumn<String, String> encodeNameCol =
        baseTableView.genColumn(TableAlign.CENTER_LEFT, "encodeName", "encodeName");
    TableColumn<String, String> compressNameCol =
        baseTableView.genColumn(TableAlign.CENTER_LEFT, "compressName", "compressName");
    TableColumn<String, String> originSizeCol =
        baseTableView.genColumn(TableAlign.CENTER_LEFT, "originSize", "originSize");
    TableColumn<String, String> encodeSizeCol =
        baseTableView.genColumn(TableAlign.CENTER_LEFT, "encodedSize", "encodedSize");
    TableColumn<String, String> uncompressSizeCol =
        baseTableView.genColumn(TableAlign.CENTER_LEFT, "uncompressSize", "uncompressSize");
    TableColumn<String, String> compressedSizeCol =
        baseTableView.genColumn(TableAlign.CENTER_LEFT, "compressedSize", "compressedSize");
    TableColumn<String, String> compressedCostCol =
        baseTableView.genColumn(TableAlign.CENTER_LEFT, "compressedCost", "compressedCost");
    TableColumn<String, String> scoreCol =
            baseTableView.genColumn(TableAlign.CENTER_LEFT, "score", "score");


    baseTableView.tableViewInit(
        pageDataPane,
        pageTVTableView,
        analyseDataList,
        true,
        typeNameCol,
        encodeNameCol,
        compressNameCol,
        originSizeCol,
        encodeSizeCol,
        uncompressSizeCol,
        compressedSizeCol,
        compressedCostCol,
            scoreCol);
    pageTVTableView.setLayoutX(0);
    pageTVTableView.setLayoutY(0);
    pageTVTableView.setPrefWidth(ENCODE_ANALYSE_PAGE_WIDTH);
    pageTVTableView.setPrefHeight(ENCODE_ANALYSE_PAGE_HEIGHT * 0.8);

    URL uiDarkCssResource = getClass().getClassLoader().getResource("css/ui-dark.css");
    if (uiDarkCssResource != null) {
      this.getScene().getStylesheets().add(uiDarkCssResource.toExternalForm());
    }
  }

  private void showQueryDataSet(AnalysedResultModel analysedResultModel) {
    analyseDataList.clear();
    EncodeCompressAnalysedModel currentAnalysed = analysedResultModel.getCurrentAnalysed();
    List<EncodeCompressAnalysedModel> analysedList = analysedResultModel.getAnalysedList();
    // 1. currentAnalysed result
    analyseDataList.add(new EncodeCompressAnalyseTable(
            currentAnalysed.getTypeName(),
            currentAnalysed.getEncodeName(),
            currentAnalysed.getCompressName(),
            currentAnalysed.getOriginSize(),
            currentAnalysed.getEncodedSize(),
            currentAnalysed.getUncompressSize(),
            currentAnalysed.getCompressedSize(),
            currentAnalysed.getCompressedCost(),
            currentAnalysed.getScores()
    ));
    // 2. others analysed results
    for (EncodeCompressAnalysedModel encodeCompressAnalysedModel : analysedList) {
      analyseDataList.add(new EncodeCompressAnalyseTable(
              encodeCompressAnalysedModel.getTypeName(),
              encodeCompressAnalysedModel.getEncodeName(),
              encodeCompressAnalysedModel.getCompressName(),
              encodeCompressAnalysedModel.getOriginSize(),
              encodeCompressAnalysedModel.getEncodedSize(),
              encodeCompressAnalysedModel.getUncompressSize(),
              encodeCompressAnalysedModel.getCompressedSize(),
              encodeCompressAnalysedModel.getCompressedCost(),
              encodeCompressAnalysedModel.getScores()
      ));
    }

//    tvTableView.setVisible(true);
  }
}