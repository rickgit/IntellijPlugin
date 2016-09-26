package com.woqutz.plugin;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diff.impl.patch.formove.PathMerger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
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
public class Copy extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = vFile != null ? vFile.getName() : null;
        try {
            File file = new File(vFile.getPath());
            if (!file.isFile())
                return;
            Project project = e.getData(PlatformDataKeys.PROJECT);
//            PackageChooserDialog dialog = new PackageChooserDialog("Select a Java package", project);
//            dialog.selectPackage("com.didi.java");
//            dialog.show();
//            TextFieldWithBrowseButton.
            FileChooserDescriptor fcd = new FileChooserDescriptor(false, true, false, false, false, false);
            fcd.setShowFileSystemRoots(true);
            fcd.setTitle("init.destination.directory.title");
            fcd.setDescription("init.destination.directory.description");
            fcd.setHideIgnored(false);
            final VirtualFile baseDir = project.getBaseDir();
            String hostPath = baseDir.getPath() + "/DidiPluginHost/assets/plugins";
            VirtualFile chooseVf=null;
            if (new File(hostPath).exists() && new File(hostPath).isDirectory())
                chooseVf = LocalFileSystem.getInstance().findFileByPath(hostPath);
            else
                chooseVf=baseDir;
            final VirtualFile[] files = FileChooser.chooseFiles(fcd, project, chooseVf);
            if (files.length == 0) {
                return;
            }
            final VirtualFile root = files[0];
            if (!root.isDirectory())
                return;
            String[] split = fileName.split("[_]");
            fileName=split.length>1?split[0]+".apk":fileName;
            nioTransferCopy(file, new File(root.getPath(), fileName));
            VirtualFile fileByRelativePath = root.findFileByRelativePath(".");
            root.refresh(true,true);
        } catch (Exception e1) {

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
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private static void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inStream);
            close(in);
            close(outStream);
            close(out);
        }
    }

    private static void close(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
