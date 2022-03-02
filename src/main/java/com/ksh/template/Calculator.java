package com.ksh.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filepath) throws IOException {
//        BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer sum = 0;
//                String line = null;
//
//                while((line = br.readLine()) != null){
//                    sum += Integer.valueOf(line);
//                }
//
//                return sum;
//            }
//        };
//
//        return fileReadTemplate(filepath, sumCallback);

        LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.valueOf(line);
            }
        };

        return lineReadTemplete(filepath, sumCallback, 0);
    }

    public Integer calcMultiply(String filepath) throws IOException {
//        BufferedReaderCallback callback = new BufferedReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer multiply = 1;
//                String line = null;
//
//                while((line = br.readLine()) != null){
//                    multiply *= Integer.valueOf(line);
//                }
//
//                return multiply;
//            }
//        };
//
//        return fileReadTemplate(filepath, callback);

        LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.valueOf(line);
            }
        };

        return lineReadTemplete(filepath, sumCallback, 1);
    }

    public String concatenate(String filepath) throws IOException{
        LineCallback<String> concatenateCallback = new LineCallback<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) {
                return value + line;
            }
        };

        return lineReadTemplete(filepath, concatenateCallback, "");
    }

    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException{
        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader(filepath));
            int ret = callback.doSomethingWithReader(br);
            return ret;
        }catch(IOException ex){
            System.out.println(ex.getMessage());
            throw ex;
        }finally{
            if(br != null){
                try {
                    br.close();
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public <T> T lineReadTemplete(String filepath, LineCallback<T> callback, T initVal) throws IOException {
        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader(filepath));
            T res = initVal;
            String line = null;

            // 파일 처리는 공통 부분으로 만들고 실제 계산 로직만 분리해서 콜백으로 처리
            while((line = br.readLine()) != null){
                res = callback.doSomethingWithLine(line, res);
            }

            return res;
        }catch(IOException ex){
            System.out.println(ex.getMessage());
            throw ex;
        }finally{
            if(br != null){
                try {
                    br.close();
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
