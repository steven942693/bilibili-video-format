package com.steven;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.steven.App.*;

public class Main {
    private static int fileCount = 0;
    public static void start(File input,File output) throws Exception {
        if (input == null || output == null){
            JOptionPane.showMessageDialog(progressBar,"未选择输入或者输出目录,操作终止!!!");
            button.setForeground(Color.GREEN);
            button.setText("< 开 始 >");
            return;
        }

        File[] files = input.listFiles();
        fileCount = files.length;
        for (int i = 0; i < files.length; i++) {
            if (!flag){
                return;
            }
            if (files[i].isDirectory()) {
                progressBar.setValue(100*(i+1)/files.length);
                progressBar.setString("总进度 : "+(100*(i+1)/files.length)+"%");
                System.out.print(files[i]+"-->");
                handleFile(files[i],output);
            }
        }
        progressBar.setValue(100);
        progressBar.setString("总进度 : 100%");
        JOptionPane.showMessageDialog(progressBar,"处理完成!!!");
        button.setForeground(Color.GREEN);
        button.setText("< 开 始 >");
    }

    private static void handleFile(File peerDir,File outputDir) throws Exception {
        File[] listFiles = peerDir.listFiles();
        String allNames = Arrays.toString(listFiles);
        if (allNames.contains(".info")){
            ArrayList<File> videoFiles = new ArrayList<>();
            String partName = null;
            for (File file : listFiles) {
                String fileName = file.getName();
                if (fileName.endsWith(".flv") || fileName.endsWith(".mp4")){
                    videoFiles.add(file);
                }
                if (fileName.endsWith(".info")){
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null){
                        sb.append(line);
                    }
                    String fileInfo = sb.toString();
                    partName = getFileName(fileInfo);
                    System.out.print(partName);
                }
            }
            if (videoFiles.size() > 0){
                // 进行视频文件的复制
                copyVideoFiles(videoFiles,outputDir,partName);
            }

        }
    }

    private static void copyVideoFiles(ArrayList<File> videoFiles, File outputDir, String partName) throws IOException {
        for (File videoFile : videoFiles) {
            String videoFileName = videoFile.getName();
            String[] doteSplit = videoFileName.split("\\.");
            String type = doteSplit[doteSplit.length - 1];

            videoFileName = videoFileName.substring(0, videoFileName.lastIndexOf("."));
            String[] splits = videoFileName.split("-");
            String prefix = "";
            for (int i = 1; i < splits.length; i++) {
                prefix += splits[i]+"_";
            }
            String videoNewName = prefix + partName + "." + type;
            // 开始复制文件
            copyFile(videoFile,outputDir,videoNewName);
        }
    }

    private static void copyFile(File videoFile, File outputDir, String videoNewName) throws IOException {
        if (!flag){
            return;
        }
        if (!outputDir.exists()){
            outputDir.mkdirs();
        }
        current_File.setText(videoNewName);
        FileInputStream fis = new FileInputStream(videoFile);
        File outVideoFile = new File(outputDir, videoNewName);
        String replace = outVideoFile.getAbsolutePath().replace(" ", "_");
        File outputFile = new File(replace);

        FileOutputStream fos = new FileOutputStream(outputFile);
        long sum_len = 0;
        long length = videoFile.length();
        int len;
//        byte[] bytes = new byte[1024*1024*50];
        int selfAdaptiveSize = (int) length;
        byte[] bytes = new byte[selfAdaptiveSize<0 ? 1024*1024*50 : selfAdaptiveSize];
        peer_progressBar.setValue(0);
        peer_progressBar.setString("当前视频进度 : "+0+"%");
        while ((len = fis.read(bytes))!= -1){
            fos.write(bytes,0,len);
            sum_len += len;
            int percent = (int) (100*sum_len / length);
            peer_progressBar.setValue(percent);
            peer_progressBar.setString("当前视频进度 : "+percent+"%");
        }
        fos.flush();
        fos.close();
        fis.close();
        System.out.println("-->"+videoNewName);
        System.gc();
    }

    private static String getFileName(String fileInfo) {
        JSONObject object = JSON.parseObject(fileInfo);
        String PartName = object.getString("PartName");
        String PartNo = object.getString("PartNo");
        PartNo = formatPartNO(PartNo);
        if (PartName.startsWith(PartNo)){
            return PartName;
        }
        return PartNo+"-"+PartName;
    }

    private static String formatPartNO(String PartNo){
        String fileCount_str = String.valueOf(fileCount);
        String PartNo_str = String.valueOf(PartNo);
        String prefix = "";
        for (int i = 0; i < fileCount_str.length()-PartNo_str.length(); i++) {
            prefix += "0";
        }
        return prefix + PartNo;
    }

}
