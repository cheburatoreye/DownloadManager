package com.company;
import java.io.*;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadManager {

    private static String path(boolean src) {
        String path;
        Console console=System.console();
        Pattern p= Pattern.compile("^(([a-zA-Z]\\:)|(\\\\))(\\\\{1}|((\\\\{1})[^\\\\]([^/:*?<>\"|]*))+)$");
        Matcher m;
        do {
            if (src){
                console.printf("%s","Enter source file path:");
            }
            else {
                console.printf("%s","Enter dest file path:");
            }
            m=p.matcher(path=console.readLine());
        }while (!m.find());
        m.reset();
        return  path;
    }
    private static long copyTime() {
        Console console=System.console();
        Pattern p= Pattern.compile("(^\\d+$)|(^[ \\t\\n]*$)");
        Matcher m;
        long mincopytime=10;
        String copytime;
        do {
            console.printf("%s","Enter max time copying in millis:");
            m=p.matcher(copytime=console.readLine());
        }while (!m.find());
        m.reset();

        if (copytime.equals("")){
            copytime ="1000";
        }

        if (Integer.valueOf(copytime) < mincopytime) {
            console.printf("%s","too low min time,it must be more than: "+mincopytime+" millisec");
        } else {
            if (Integer.valueOf(copytime) > mincopytime) {
                mincopytime = Integer.valueOf(copytime);
            }

        }
        return mincopytime;
    }
    private static int copySpeed(){
        Pattern p= Pattern.compile("(^[1-5]$)|(^[ \\t\\n]*$)");
        Matcher m;
        String speedofcopy;
        Console console=System.console();
        int copyingspeed=512;
        do {
            console.printf("%s","Enter copying speed from 1 to 5:");
            m=p.matcher(speedofcopy=console.readLine());
        }while (!m.find());
        m.reset();

        switch (speedofcopy){
            case "1":{
                copyingspeed=1;
            }
            case "2":{
                copyingspeed=16;
            }
            case "3":{
                copyingspeed=64;
            }
            case "4":{
                copyingspeed=128;
            }
            case "5":{
                copyingspeed=512;
            }
            case "":{
                copyingspeed=512;//скорость по умолчанию
            }

        }
        return  copyingspeed;
    }
    private static boolean fileExist(String pathIn){
        boolean exist=false;
            File file = new File(pathIn);
            if(file.exists() && !file.isDirectory()) {
                exist=true;
            }
            return exist;
    }
    private static void copyFile(String pathin,String pathout,int copyspeed,long copytime,boolean append){
        Console console=System.console();
        try (InputStream inputStream=new FileInputStream(pathin);
             OutputStream outputStream=new FileOutputStream(pathout,append)){
            Integer data = 0;
            Double bytequantity=0.0;
            long copytimeperiod;
            File fileout=new File(pathout);
            long start=System.currentTimeMillis();
            byte[] buffer=new byte[copyspeed*8];

            if (append){
                inputStream.skipNBytes(fileout.length());
            }

            while ((data = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer,0,data);
                bytequantity+=data;
                if (Duration.ofMillis((System.currentTimeMillis()-start)).toMillis()>copytime){
                    console.printf("%s","copy not completed time elapsed");
                    break;
                }
            }

            copytimeperiod=Duration.ofMillis((System.currentTimeMillis()-start)).toMillis();
            console.printf("%s"," copy time : "+copytimeperiod+" millisec");
            console.printf("%s"," /file copying finished / ");
            if (append){
                console.printf("%s"," speed: "+bytequantity/copytimeperiod/1000+"bytes/second "+" / written size: "+bytequantity/(1024*1024)+" MBytes");
                console.printf("%s"," file size: "+fileout.length()/(1024*1024)+" MBytes");
            }
            else {
                console.printf("%s"," speed: "+bytequantity/copytimeperiod/1000+"bytes/second "+" / file size: "+bytequantity/(1024*1024)+" MBytes");
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
    private static void startDownload() {
      String pathin;
      String pathout;
      int copyingspeed;
      long maxcopytime;

             pathin=path(true);

          if (!fileExist(pathin)){
             // console.printf("%s","no such file");
              System.out.println("no such file");
          }
          else {
              pathout=path(false);
              copyingspeed=copySpeed();
              maxcopytime=copyTime();
                 if (fileExist(pathout)){
                      copyFile(pathin,pathout,copyingspeed, maxcopytime,true);
                      }
                      else {
                        copyFile(pathin,pathout,copyingspeed, maxcopytime,false);
                      }
                }
          }

    public static void main(String[] args) {
        startDownload();
    }

}
