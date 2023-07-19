package com.side.imgproject.s3;


import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
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


import java.net.URL;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void uploadImage(MultipartFile multipartFile, String dirName) throws Exception {
        String url = "https://kik-bucket.s3.ap-northeast-2.amazonaws.com/image/";
        String photo = "";

        String originalName = dirName + "/" + UUID.randomUUID() + "--" + multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(size);

        amazonS3Client.putObject(new PutObjectRequest(bucket, originalName, multipartFile.getInputStream(), objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        URL s3Url = amazonS3Client.getUrl(bucket, originalName);
        photo = s3Url.toString();

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                .withRegion("ap-northeast-2")
                .build();

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photo).withBucket(bucket)))
                .withMaxLabels(10)
                .withMinConfidence(75F);

        try {
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            List<Label> labels = result.getLabels();

            System.out.println("Detected labels for " + photo);
            for (Label label : labels) {
                System.out.println(label.getName() + ": " + label.getConfidence().toString());
            }
        } catch (AmazonRekognitionException e) {
            e.printStackTrace();
        }
    }

}




