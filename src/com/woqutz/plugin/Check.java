package com.woqutz.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anshu.wang on 2016/9/21.
 */
public class Check extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = vFile != null ? vFile.getName() : null;
        try {
            File file = new File(vFile.getPath());
            if (!file.isFile())
                return;
            String md5ByFile = getMd5ByFile(file);
//            FileOutputStream fileOutputStream = new FileOutputStream(new File(vFile.getParent().getPath(),"a.txt"));
//            PrintWriter printWriter = new PrintWriter(fileOutputStream);
//            printWriter.write(" md5 "+vFile.getPath()+" "+md5ByFile);
//            System.out.println("nihao");
//            printWriter.close();
            Project project = e.getData(PlatformDataKeys.PROJECT);
            Messages.showMessageDialog("文件修改时间："+new SimpleDateFormat("yyyy年MM月dd HH:mm:ss").format(file.lastModified())+"\n 文件MD5码: "+md5ByFile,"文件的Md5",null);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
