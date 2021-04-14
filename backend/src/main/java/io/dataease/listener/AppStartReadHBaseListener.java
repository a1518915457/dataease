package io.dataease.listener;

import io.dataease.base.domain.DatasetTable;
import io.dataease.base.domain.DatasetTableExample;
import io.dataease.base.domain.DatasetTableField;
import io.dataease.base.domain.DatasetTableFieldExample;
import io.dataease.base.mapper.DatasetTableFieldMapper;
import io.dataease.base.mapper.DatasetTableMapper;
import io.dataease.commons.utils.CommonBeanFactory;
import io.dataease.commons.utils.CommonThreadPool;
import io.dataease.service.dataset.DataSetTableFieldsService;
import io.dataease.service.spark.SparkCalc;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Order(value = 2)
public class AppStartReadHBaseListener implements ApplicationListener<ApplicationReadyEvent> {
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private SparkCalc sparkCalc;
    @Resource
    private Environment env; // 保存了配置文件的信息

    @Resource
    private DatasetTableMapper datasetTableMapper;
    @Resource
    private DataSetTableFieldsService dataSetTableFieldsService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        System.out.println("================= Read HBase start =================");
        // 项目启动，从数据集中找到定时抽取的表，从HBase中读取放入缓存
        DatasetTableExample datasetTableExample = new DatasetTableExample();
        datasetTableExample.createCriteria().andModeEqualTo(1);
        List<DatasetTable> datasetTables = datasetTableMapper.selectByExampleWithBLOBs(datasetTableExample);
        for (DatasetTable table : datasetTables) {
//            commonThreadPool.addTask(() -> {
                try {
                    List<DatasetTableField> fields = dataSetTableFieldsService.getFieldsByTableId(table.getId());
                    sparkCalc.getHBaseDataAndCache(table.getId(), fields);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            });
        }
    }
}
