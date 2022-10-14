package com.chaohu.table.table;

import com.chaohu.table.ContainerConstants;
import com.chaohu.table.field.IField;
import com.chaohu.table.field.SimpleField;
import com.chaohu.table.row.IRow;
import com.chaohu.table.row.SimpleRow;
import com.opencsv.CSVReader;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author wangmin
 */
public class CsvTable extends BaseTable {
    private final String charset;
    private final char separator;
    private final boolean hasHeader;

    protected CsvTable(Builder builder) {
        super(builder);
        this.charset = builder.charset;
        this.separator = builder.separator;
        this.hasHeader = builder.hasHeader;
    }

    @Override
    public boolean load() {
        if (!StringUtils.isEmpty(getPath())) {
            try {
                logger.info("文件开始加载...");
                long start = System.currentTimeMillis();
                CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(getPath()), charset), separator);
                String[] headers = hasHeader ? reader.readNext() : null;
                String[] nextLine;
                clear();
                int index = 1;
                while ((nextLine = reader.readNext()) != null) {
                    IRow row = new SimpleRow.Builder().index(index++).build();
                    for (int i = 0; i < nextLine.length; i++) {
                        String header = hasHeader && headers != null && i < headers.length ? headers[i] : String.valueOf(i);
                        IField field = new SimpleField.Builder().name(header).value(nextLine[i]).build();
                        row.addField(field);
                    }
                    addRow(row);
                }
                logger.info("文件加载结束... 耗时：{} ms", System.currentTimeMillis() - start);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, CsvTable> {
        private char separator = ContainerConstants.CSV_DEFAULT_SEPARATOR;
        private String charset = "GBK";
        private boolean hasHeader = true;

        @Override
        public CsvTable buildTable() {
            return new CsvTable(this);
        }
    }
}
