package com.example.domain;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Vector;

/**
 * packageName   : com.example.domain
 * fileName  : SFTPconnect
 * author    : jiseung-gu
 * date  : 2023/02/09
 * description :
 **/
public class SFTPconnect {
    public static void main(String[] args) {
        String requestContent = "";
        //파일경로
        String fileLocation = "";

        JSch jSch = new JSch();
        Session session;
        ChannelSftp sftp;

        try{
            String id = "ID";
            String ip = "IP";
            Integer port = 22;
            String pw = "PASSWORD ";

            //접속 정보 세팅
            session = jSch.getSession(id,ip,port);
            session.setPassword(pw);
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);
            session.connect();

            // sftp 연결
            Channel channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("연결완료");
            sftp = (ChannelSftp) channel;

            LocalDateTime now = LocalDateTime.now();

            File file = new File("/Users/jiseung-gu/Documents/test");
            //파일 전송
//            File file = new File("Z:"+File.separator+"DBBAK");
            File[] files = file.listFiles();
            for(File f : files) {
                if(f.isFile() && f.getName().contains(now.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")))){
                    FileInputStream in = new FileInputStream(f);
                    sftp.cd("/data");
                    sftp.put(in,f.getName());
                }
            }


            //5일 이후 파일 삭제
            LocalDateTime Delfile = now.minusDays(7);
//            System.out.println("7일전 "+Delfile.format(DateTimeFormatter.ISO_LOCAL_DATE));
            Vector<ChannelSftp.LsEntry> fileList = sftp.ls("/data");
            // LsEntry 건수 만큼 처리
            for(ChannelSftp.LsEntry entry : fileList) {
                // 경로에서 받은 파일명 변수 처리
                String name = entry.getFilename();
                if(name.contains(Delfile.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                    sftp.rm(name);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
