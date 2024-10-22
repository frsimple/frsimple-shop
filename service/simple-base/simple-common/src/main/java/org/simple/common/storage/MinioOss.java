package org.simple.common.storage;

import cn.hutool.core.bean.BeanUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.apache.commons.lang.StringUtils;
import org.simple.common.constant.RedisConstant;
import org.simple.common.dto.FIleDto;
import org.simple.common.dto.OssDto;
import org.simple.common.utils.ComUtil;
import org.simple.common.utils.CommonResult;
import org.springframework.data.redis.core.RedisTemplate;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MinioOss {
    private static MinioOss minioOss = null;
    private static OssDto ossDto;

    private RedisTemplate redisTemplate;

    private MinioOss() {
    }

    public static MinioOss getInstance(RedisTemplate template) {
        if (null == minioOss) {
            minioOss = new MinioOss();
        }
        //设置配置对象
        OssDto var = BeanUtil.fillBeanWithMap(
                template.opsForHash().entries(RedisConstant.MINIO_PIX), new OssDto(),
                false);
        ossDto = var;
        return minioOss;
    }

    private MinioClient getMinioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(ossDto.getEndpoint())
                .credentials(ossDto.getAccesskeyid(), ossDto.getAccesskeysecret()).build();
        return minioClient;
    }


    public CommonResult fileUpload(File file, boolean isPrivate, String userid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = getMinioClient();
        String fileName = file.getName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String path = "";
        if (!isPrivate) {
            path = "public/";
        } else {
            path = "private/";
        }
        path = path + sdf.format(new Date()) + "/";
        if (StringUtils.isNotEmpty(userid)) {
            path = path + userid + "/" + fileName;
        }
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(ossDto.getWorkspace())
                .object(path)
                .stream(new FileInputStream(file), file.length(), -1)
                .contentType(new MimetypesFileTypeMap().getContentType(file))
                .build();
        minioClient.putObject(args);

        if (isPrivate) {
            return CommonResult.success(path);
        } else {
            return CommonResult.success(ossDto.getEndpoint() + "/" + ossDto.getWorkspace() + "/" + path);
        }
    }


    /**
     * 获取私有文件授权链接
     *
     * @param filepath
     */
    public String downLoadLink(String filepath, Integer expir) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = getMinioClient();
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(ossDto.getWorkspace())
                .object(filepath)
                .expiry(expir, TimeUnit.SECONDS)
                .build());
        return url;
    }


    /**
     * 下载文件，返回输入流
     *
     * @param filepath
     */
    public FIleDto downLoad(String filepath) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = getMinioClient();
        FIleDto fIleDto = new FIleDto();
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(ossDto.getWorkspace())
                        .object(filepath)
                        .build())) {
            fIleDto.setFileName(filepath);
            fIleDto.setFileBytes(ComUtil.toByteArray(stream));
        }
        return fIleDto;
    }


    /**
     * 查询文件列表
     */
    public FIleDto listFiles(Integer size, String marker, String prefix) {
        MinioClient minioClient = getMinioClient();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(ossDto.getWorkspace())
                        .startAfter(marker)
                        .prefix(prefix)
                        .maxKeys(size)
                        .build());
        List<org.simple.common.dto.File> listfile = new ArrayList<>();
        results.forEach(e -> {
            try {
                String childPath = e.get().objectName();
                org.simple.common.dto.File f = new org.simple.common.dto.File();
                f.setKey(childPath);
                f.setSize(e.get().size());
                if (!e.get().isDir()) {
                    f.setUpdateDate(Date.from(e.get().lastModified().toInstant()));
                }
                listfile.add(f);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
//
//        List<org.simple.common.dto.File> listfile = new ArrayList<>();
//        if (CollectionUtil.isNotEmpty(list)) {
//            list.forEach(row -> {
//                org.simple.common.dto.File f = new org.simple.common.dto.File();
//                f.setKey(row.getKey());
//                f.setSize(row.getSize());
//                f.setUpdateDate(row.getLastModified());
//                listfile.add(f);
//            });
//        }
        FIleDto fIleDto = new FIleDto();
        fIleDto.setFileList(listfile);
        //fIleDto.setNextMarker(objectListing.getNextMarker());
        return fIleDto;
    }
}
