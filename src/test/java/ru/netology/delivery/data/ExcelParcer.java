package ru.netology.delivery.data;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ExcelParcer {

    public static ArrayList parse(String fileName) {
        //инициализируем потоки
        ArrayList result = new ArrayList();
        InputStream inputStream;
        HSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(fileName);
            workBook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //разбираем первый лист входного файла на объектную модель
        assert workBook != null;
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();
        //проходим по всему листу
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            while (cells.hasNext()) {
                Cell cell = cells.next();
                result.add(cell.getStringCellValue());
            }
        }

        return result;
    }
    public static void main (String[] args){
        PrintStream out = new PrintStream(System.out, true, UTF_8); // true = autoflush
        for (int i = 0; i < parse("./artifacts/city.xls").size(); i++) {
            out.println(parse("./artifacts/city.xls").get(i));
        }
    }

}
