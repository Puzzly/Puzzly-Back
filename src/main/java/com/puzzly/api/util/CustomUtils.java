package com.puzzly.api.util;

import com.puzzly.api.exception.FailException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class CustomUtils {

    // Key String : _PUZZLY_PRV_KEY_$0501
    private static String PRIVATE_KEY = "50lsSVpazGW8a2zEWXo8KlmUX4OWLtLvQ6uM0GFwn6Q=";

    private static DateTimeFormatter formatDateTime  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Value("${puzzly.filePath}")
    private String filePath;


    public static String aesCBCEncode(String plainText) throws FailException {
        SecretKeySpec secretKey = null;
        IvParameterSpec iv = null;
        String encoded = "";
        try {
            secretKey = new SecretKeySpec(PRIVATE_KEY.substring(0, 32).getBytes("UTF-8"), "AES");
            iv = new IvParameterSpec(PRIVATE_KEY.substring(0, 16).getBytes("UTF-8"));
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            byte[] encryptionByte = c.doFinal(plainText.getBytes("UTF-8"));
            encoded = new String(Base64.getEncoder().encode(encryptionByte));
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException nee) {
            nee.printStackTrace();
            throw new FailException("SERVER_MESSAGE_ERROR_WHILE_INITIALIZING_CUSTOMUTILS_DECODER", 500);
        } catch(InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException bpe){
            bpe.printStackTrace();
            throw new FailException("SERVER_MESSAGE_ERROR_WHILE_DECODING_CUSTOMUTILS_DECODER", 500);
        }
        return encoded;
    }

    public static String aesCBCDecode(String encodedText) throws FailException {
        SecretKeySpec secretKey = null;
        IvParameterSpec iv = null;
        String decoded = "";
        try {
            secretKey = new SecretKeySpec(PRIVATE_KEY.substring(0, 32).getBytes("UTF-8"), "AES");
            iv = new IvParameterSpec(PRIVATE_KEY.substring(0, 16).getBytes("UTF-8"));

            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decodeByte = c.doFinal(Base64.getDecoder().decode(encodedText));
            decoded = new String(decodeByte, "UTF-8");
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException nee) {
            nee.printStackTrace();
            throw new FailException("SERVER_MESSAGE_ERROR_WHILE_INITIALIZING_CUSTOMUTILS_DECODER", 500);
        } catch(InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException bpe){
            bpe.printStackTrace();
            throw new FailException("SERVER_MESSAGE_ERROR_WHILE_DECODING_CUSTOMUTILS_DECODER", 500);
        }

        return decoded;
    }

    public HashMap<String, Object> uploadFile(String context, MultipartFile multipartFile) throws FailException {
        //String lowTimeMonth = String.valueOf(LocalDateTime.now().getMonth().getValue());
        //String lowTimeDay = String.valueOf(LocalDateTime.now().getDayOfMonth());
        //String[] uuid = StringUtils.split(UUID.randomUUID().toString(), "-");
        String uuid = StringUtils.replace(UUID.randomUUID().toString(), "-", "/");
        //ArrayList<String> uuid = (ArrayList<String>) Arrays.asList(StringUtils.split(UUID.randomUUID().toString(), "-"));

        String dirPath = filePath + "/" + context + "/" + StringUtils.substring(uuid, 0, StringUtils.lastIndexOf(uuid, "/"));
        String fileName = StringUtils.substring(uuid, StringUtils.lastIndexOf(uuid, "/"));

        File dirPathFile = new File(dirPath);
        if (!dirPathFile.exists()) {
            if (!dirPathFile.mkdirs()) {
                throw new FailException("SERVER_MESSAGE_CREATE_FILE_DIRECTORY_FAILED.", 500);
            }
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            long fileSize = multipartFile.getSize();
            String originName = multipartFile.getOriginalFilename();
            String[] fileNameArr = StringUtils.split(originName, ".");
            String extension = fileNameArr[fileNameArr.length - 1];

            try (InputStream inputStream = multipartFile.getInputStream();
                 OutputStream outputStream = new FileOutputStream(dirPath + fileName)) {
                // 1Mb씩
                byte[] buffer = new byte[1024 * 1024 * 1];
                int readBytes;
                while ((readBytes = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    outputStream.write(buffer, 0, readBytes);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw new FailException("SERVER_MESSAGE_IOSTREAM_CREATE_FAILED", 500);
            }

            HashMap<String, Object> fileResult = new HashMap<>();
            fileResult.put("dirPath", dirPath);
            fileResult.put("fileName", fileName);
            fileResult.put("originName", originName);
            fileResult.put("fileSize", fileSize);
            fileResult.put("extension", extension);

            return fileResult;
        } else {
            throw new FailException("SERVER_MESSAGE_MULTIPART_FILE_NULL", 400);
        }
    }

    public void downloadFile(String fileFullPath, String originName, String extension, HttpServletRequest request, HttpServletResponse response)
            throws IOException, FailException {

        try (
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(fileFullPath);
        ) {
            String header = request.getHeader("User-Agent");
            // FileName URLEncode 필요할수도.
            if (header.contains("MSIE") || header.contains("Trident")) {
                originName = URLEncoder.encode(originName, "UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + originName + ";");
            } else {
                //originName = new String(originName.getBytes("UTF-8"), "ISO-8859-1");
                originName = URLEncoder.encode(originName, "UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + originName + "\"");
            }

            response.setContentType("application/download");
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");

            byte[] buffer = new byte[8192];
            int readBytes = 0;
            while ((readBytes = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }
        } catch (IOException sae) {
            log.error("downloadFile IOExceptionerror");
            sae.printStackTrace();
            throw new FailException("IOException Error", 400);
        } catch (Exception e) {
            e.printStackTrace();
            String error_msg = "'" + "다운로드 실패" + e.getMessage() + "'";
            log.error("downloadFile error : " + error_msg);
            throw new FailException(error_msg, 400);
        }
    }

    public LocalDate localDateFromDateString(String dateString){
        return LocalDate.parse(dateString, formatDate);
    }

    public LocalDateTime localDateTimeFromDateTimeString(String dateTimeString){
        return LocalDateTime.parse(dateTimeString, formatDateTime);
    }

    public String localDateStringNow(){
        return LocalDate.now().format(formatDate);
    }

    public String localDateTimeStringNow(){
        return LocalDate.now().format(formatDateTime);
    }

}
