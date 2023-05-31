package com.example.domain

import kotlin.jvm.JvmStatic
import com.jcraft.jsch.JSch
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelSftp.LsEntry
import com.jcraft.jsch.Session
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object SftpClient {
    @JvmStatic
    fun main(args: Array<String>) {
        var requestContent = ""
        //파일경로
        var fileLocation = ""
        val jSch = JSch()
        val session: Session
        val sftp: ChannelSftp
        try {
            //계정 정보
            val id = "stoval"
            val ip = "14.63.161.82"
            val port = 22
            val pw = "sejong!23$"

            //전일 날짜 구하기
            val calendar: Calendar = GregorianCalendar()
            val SDF = SimpleDateFormat("yyyyMMdd")
            calendar.add(Calendar.DATE, -1)
            val check = SDF.format(calendar.time)

            // 접속 정보 세팅
            session = jSch.getSession(id, ip, port)
            session.setPassword(pw)
            val properties = Properties()
            properties["StrictHostKeyChecking"] = "no"
            session.setConfig(properties)
            session.connect()

            // sftp 연결
            val channel = session.openChannel("sftp")
            channel.connect()
            sftp = channel as ChannelSftp
            try {
                //확인해야 되는 파일명
                val checkFileName = "003547_SKYFS_001.$check"

                //파일경로 지정
                fileLocation = "/data/"
                sftp.cd(fileLocation)

                //inputstream 파일 정보를 담는다.
                var inputStream: InputStream? = null
                val reader = BufferedReader(InputStreamReader(inputStream))

                //get 했을때 파일이 없으면 error
                inputStream = sftp[checkFileName]
                try {
                    var line: String? = "" //텍스트 파일 한줄씩 담을 객체
                    while (line != null) {
                        line = try {
                            reader.readLine()
                        } catch (e: Exception) {
                            null
                        }
                        if (line == null) {
                            break
                        } //읽을 행이 없으면 종료
                        requestContent = line
                        println("읽은 정보 -> $requestContent")
                    }
                } catch (e: Exception) {
                    println(e.toString())
                    reader.close()
                }
            } catch (e: Exception) {
                println(e.toString())
                sftp.quit() //sftp 종료
                session.disconnect() //세션 종료
            }
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}