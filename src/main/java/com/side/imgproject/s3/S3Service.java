package com.side.imgproject.s3;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;


import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {



    private final AmazonS3Client amazonS3Client;

    @Autowired
    private RekognitionClient rekognitionClient;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<Label> uploadImage(MultipartFile multipartFile, String dirName) throws Exception {
        String imageUrl = "";

        String originalName = dirName + "/" + UUID.randomUUID() + "--" + multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(size);

        amazonS3Client.putObject(new PutObjectRequest(bucket, originalName, multipartFile.getInputStream(), objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));


        imageUrl = amazonS3Client.getUrl(bucket, originalName).toString();

        Image rekognitionImage = Image.builder()
                .s3Object(S3Object.builder()
                        .bucket(bucket)
                        .name(getImageKeyFromUrl(imageUrl))
                        .build())
                .build();

        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(rekognitionImage)
                .maxLabels(10)
                .build();

        DetectLabelsResponse response = rekognitionClient.detectLabels(detectLabelsRequest);

        return response.labels();

    }

    private String getImageKeyFromUrl(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        if (lastSlashIndex != -1 && lastSlashIndex < imageUrl.length() - 1) {
            return imageUrl.substring(lastSlashIndex + 1);
        }
        return "";
    }


}




