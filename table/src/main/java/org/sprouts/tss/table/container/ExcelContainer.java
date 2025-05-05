package org.sprouts.tss.table.container;

import org.sprouts.tss.table.table.SheetTable;
import com.google.common.base.Preconditions;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author wangmin
 */
public class ExcelContainer extends BaseContainer {
    private static final String SUFFIX = "xlsx";
    private final boolean hsaHeader;

    public ExcelContainer(Builder builder) {
        super(builder);
        this.hsaHeader = builder.hasHeader;
    }

    @Override
    public boolean init() {
        logger.info("文件开始加载...");
        long start = System.currentTimeMillis();
        Workbook workbook = read();
        for (int sheetIndex = 0; sheetIndex < Objects.requireNonNull(workbook).getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            SheetTable sheetTable = new SheetTable.Builder().sheet(sheet).hsaHeader(hsaHeader).name(sheet.getSheetName()).build();
            addTable(sheetTable);
        }
        logger.info("文件加载结束... 耗时：{} ms", System.currentTimeMillis() - start);
        return false;
    }

    private Workbook read() {
        Preconditions.checkArgument(!StringUtils.isBlank(getPath()), "文件路径为空，无法初始化");
        try {
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(getPath())));
            return getPath().endsWith(SUFFIX) ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public boolean setTable(ITable table) {
//        if (table != null) {
//            logger.info("开始写入...");
//            Workbook workbook = read();
//            if (workbook != null) {
//                Sheet sheet = workbook.getSheet(table.getKey());
//                for (int i = hsaHeader ? 1 : 0; i <= sheet.getLastRowNum(); i++) {
//                    Row row = sheet.getRow(i);
//                    if (row == null) {
//                        continue;
//                    }
//                    IRow newRow = table.getRows()[i == 0 ? i : i - 1];
//                    for (int j = 0; j < row.getLastCellNum(); j++) {
//                        Cell cell = row.getCell(j);
//                        if (cell == null) {
//                            continue;
//                        }
//                        cell.setCellValue(newRow.getFieldsValue()[j]);
//                    }
//                }
//                try (FileOutputStream out = new FileOutputStream(getPath())) {
//                    workbook.write(out);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                logger.info("写入完成...");
//                return true;
//            }
//            return false;
//        }
//        return false;
//    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, ExcelContainer> {
        private boolean hasHeader = true;

        @Override
        public ExcelContainer buildContainer() {
            return new ExcelContainer(this);
        }
    }
}
